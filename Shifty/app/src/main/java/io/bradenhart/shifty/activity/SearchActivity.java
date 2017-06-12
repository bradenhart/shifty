package io.bradenhart.shifty.activity;

import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.bradenhart.shifty.R;
import io.bradenhart.shifty.adapter.WorkWeekRecyclerViewAdapter;
import io.bradenhart.shifty.data.ShiftyContract;
import io.bradenhart.shifty.util.DateUtils;
import io.bradenhart.shifty.util.Utils;

import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;

/**
 * Allows the user to search for one or multiple workweeks using
 * a selected date and a search mode (Week or Month) as query parameters.
 *
 * @author bradenhart
 */
public class SearchActivity extends AppCompatActivity implements Spinner.OnItemSelectedListener {

    // logtag
    private final String TAG = SearchActivity.class.getSimpleName();
    // title to be displayed in the toolbar
    private final String title = "Search";

    private Context context;

    /* components for the Activity's actionbar */
    @BindView(R.id.appbar_search)
    AppBarLayout appBar;
    Toolbar toolbar;
    TextView titleView;

    // provides Search mode options for the user to choose from
    @BindView(R.id.button_search_mode)
    Spinner searchModeSpinner;
    // displays the date the user selected
    @BindView(R.id.textview_search_selected_date)
    TextView selectedDateTV;
    // opens calendar for user to select a date
    @BindView(R.id.button_search_select_date)
    ImageButton selectDateButton;
    // displays the search results
    @BindView(R.id.recyclerview_search_results)
    RecyclerView recyclerView;
    // indicates when the results are loading
    @BindView(R.id.progressbar_search)
    ProgressBar progressBar;

    // the adapter for displaying the search results with
    private WorkWeekRecyclerViewAdapter adapter;
    // the chosen search mode, defaults to
    private
    @SearchMode
    int searchMode = SEARCH_MODE_WEEK;

    // set of valid modes for the user to select
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({SEARCH_MODE_WEEK, SEARCH_MODE_MONTH})
    public @interface SearchMode {
    }

    /* SearchMode constants */
    public static final int SEARCH_MODE_WEEK = 0;
    public static final int SEARCH_MODE_MONTH = 1;

    /**
     * Used for starting this Activity. Ensures that the Activity is started with the required
     * extras.
     *
     * @param context The context of the Activity that calls this method
     */
    public static void start(Context context) {
        context.startActivity(new Intent(context, SearchActivity.class));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        context = SearchActivity.this;

        ButterKnife.bind(this);

        setUpActionBar();

        setUpSearchModeSpinner();

        setUpSearchResultsRecyclerView();
    }

    @OnClick(R.id.button_search_select_date)
    public void onClickSelectDateButton() {
        final Calendar c = Calendar.getInstance();

        // show a date picker for the user to select a date for their search
        DatePickerDialog datePickerFragment = new DatePickerDialog(context,
                android.R.style.Theme_DeviceDefault_Light_Dialog,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        // display the selected date
                        selectedDateTV.setText(DateUtils.getPrettyDateString(year, month, day));

                        Calendar chosen = (Calendar) c.clone();
                        chosen.set(year, month, day);

                        // execute the search using the chosen date and search mode
                        new SearchAsyncTask(context, chosen.getTime(), searchMode).execute();
                    }
                },
                c.get(YEAR),
                c.get(MONTH),
                c.get(DAY_OF_MONTH));
        datePickerFragment.show();
    }

    /**
     * Sets up the action bar for this Activity.
     */
    private void setUpActionBar() {
        toolbar = ButterKnife.findById(appBar, R.id.toolbar);
        titleView = ButterKnife.findById(toolbar, R.id.textview_toolbar_title);
        // replace the default actionbar with our toolbar
        setSupportActionBar(toolbar);
        // disable the title that would appear in the actionbar
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        // show the desired title in the toolbar instead of the actionbar
        titleView.setText(title);
        // will show the back arrow/caret and make it clickable. will not return home unless parent activity is specified
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    /**
     * Populate the search mode spinner with search mode options and provide
     * an item selection listener.
     */
    private void setUpSearchModeSpinner() {
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(context,
                R.array.search_modes, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        searchModeSpinner.setAdapter(spinnerAdapter);
        searchModeSpinner.setOnItemSelectedListener(this);
    }

    /**
     * Provide the adapter and layout manager for the recyclerview used for
     * displaying search results.
     */
    private void setUpSearchResultsRecyclerView() {
        adapter = new WorkWeekRecyclerViewAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
    }

    /**
     * Make the recyclerview visible and hide the loading icon.
     */
    private void showRecyclerView() {
        progressBar.setVisibility(View.INVISIBLE);
        recyclerView.setVisibility(View.VISIBLE);
    }

    /**
     * Make the loading icon visible and hide the recyclerview.
     */
    private void showLoading() {
        recyclerView.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
    }

    /**
     * Hide the loading icon (in the case of a failed search).
     */
    private void hideLoading() {
        progressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
        if (pos == 0) {
            searchMode = SEARCH_MODE_WEEK;
        } else if (pos == 1) {
            searchMode = SEARCH_MODE_MONTH;
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }


    /**
     * An async task for performing a search for shifts. Takes a date and search mode
     * and queries the database. Populates the search results with the cursor returned by
     * the query.
     */
    public class SearchAsyncTask extends AsyncTask<Void, Void, Cursor> {

        private Context context;
        private Date date;
        private
        @SearchMode
        int searchMode;

        public SearchAsyncTask(Context context, Date date, @SearchMode int searchMode) {
            this.context = context;
            this.date = date;
            this.searchMode = searchMode;
            System.out.println(date.toString());
        }

        @Override
        protected Cursor doInBackground(Void... voids) {
            return performSearch(date, searchMode);
        }

        @Override
        protected void onPreExecute() {
            showLoading();
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            adapter.swapCursor(cursor);

            if (cursor.getCount() == 0) {
                Utils.makeToast(context, "No results", Toast.LENGTH_LONG);
                hideLoading();
            } else {
                showRecyclerView();
            }
        }

        @Override
        protected void onCancelled() {
            adapter.swapCursor(null);
        }

        /**
         * Performs a search on the database using the query built from the provided
         * parameters.
         *
         * @param date       The date the users has selected to search for
         * @param searchMode The search mode the user has selected
         * @return The cursor resulting from the database query
         */
        private Cursor performSearch(Date date, @SearchMode int searchMode) {
            ContentResolver contentResolver = getContentResolver();
            Uri uri = ShiftyContract.Workweek.CONTENT_URI;

            String selection;
            String[] selectionArgs;
            String sortOrder;

            Cursor searchResults = null;

            if (searchMode == SEARCH_MODE_WEEK) {
                // get the date for the start of the week that the user has chosen
                String searchDatetime = DateUtils.getWeekStart(date, DateUtils.FMT_ISO_8601_DATETIME);
                // create the query parameters
                selection = ShiftyContract.Workweek.COLUMN_WEEK_START_DATETIME + " = ?";
                selectionArgs = new String[]{searchDatetime};
                sortOrder = ShiftyContract.Workweek.COLUMN_WEEK_START_DATETIME + " ASC";

                // query the database
                searchResults = contentResolver.query(uri,
                        null,
                        selection,
                        selectionArgs,
                        sortOrder
                );

            } else if (searchMode == SEARCH_MODE_MONTH) {
                String format = DateUtils.FMT_ISO_8601_DATETIME;
                // get the start date of the month the user has chosen
                String monthStart = DateUtils.getMonthStart(date, format);
                // get the end date of the month the user has chosen
                String monthEnd = DateUtils.getMonthEnd(date, format);
                // search results displays full work weeks so get the start of the first week
                // of the month and the end date of the last week of the month
                String weekStart = DateUtils.getWeekStart(monthStart, format);
                String weekEnd = DateUtils.getWeekEnd(monthEnd, format);

                // create the query parameters
                selection = ShiftyContract.Workweek.COLUMN_WEEK_START_DATETIME + " >= ? and "
                        + ShiftyContract.Workweek.COLUMN_WEEK_END_DATETIME + " <= ?";
                selectionArgs = new String[]{weekStart, weekEnd};
                sortOrder = ShiftyContract.Workweek.COLUMN_WEEK_START_DATETIME + " ASC";

                // query the database
                searchResults = contentResolver.query(uri,
                        null,
                        selection,
                        selectionArgs,
                        sortOrder
                );
            }

            return searchResults;
        }

    }

}
