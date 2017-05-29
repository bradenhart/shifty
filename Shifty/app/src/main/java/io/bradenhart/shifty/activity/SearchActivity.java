package io.bradenhart.shifty.activity;

import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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
 * Created by bradenhart on 27/05/17.
 */

public class SearchActivity extends AppCompatActivity implements Spinner.OnItemSelectedListener {

    private final String TAG = "SearchActivity.class";
    private final String title = "Search";

    @BindView(R.id.appbar_search)
    AppBarLayout appBar;
    Toolbar toolbar;
    TextView titleView;

    @BindView(R.id.button_search_mode)
    Spinner searchModeSpinner;
    @BindView(R.id.textview_search_selected_date)
    TextView selectedDateTV;
    @BindView(R.id.button_search_select_date)
    ImageButton selectDateButton;
    @BindView(R.id.recyclerview_search_results)
    RecyclerView recyclerView;
    @BindView(R.id.progressbar_search)
    ProgressBar progressBar;

    private WorkWeekRecyclerViewAdapter adapter;
    private SearchMode searchMode = SearchMode.MONTH;

    public enum SearchMode {
        WEEK, MONTH
    }


    public static void start(Context context) {
        context.startActivity(new Intent(context, SearchActivity.class));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        ButterKnife.bind(this);

        setUpActionBar();

        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.search_modes, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        searchModeSpinner.setAdapter(spinnerAdapter);
        searchModeSpinner.setOnItemSelectedListener(this);

        adapter = new WorkWeekRecyclerViewAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                finish();
                break;
            default:
                return false;
        }
        return true;
    }

    @OnClick(R.id.button_search_select_date)
    public void onClickSelectDateButton() {
        final Calendar c = Calendar.getInstance();

        DatePickerDialog datePickerFragment = new DatePickerDialog(this,
                android.R.style.Theme_DeviceDefault_Light_Dialog,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        // display the selected date
                        selectedDateTV.setText(DateUtils.getPrettyDateString(year, month, day));

                        Calendar chosen = (Calendar) c.clone();
                        chosen.set(year, month, day);

                        new SearchAsyncTask(SearchActivity.this, chosen.getTime(), searchMode).execute();
                    }
                },
                c.get(YEAR),
                c.get(MONTH),
                c.get(DAY_OF_MONTH));
        datePickerFragment.show();
    }


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
        // shows logo/icon with caret/arrow if passed true. will not show logo/icon if passed false
//        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    private void showRecyclerView() {
        progressBar.setVisibility(View.INVISIBLE);
        recyclerView.setVisibility(View.VISIBLE);
    }

    private void showLoading() {
        recyclerView.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideLoading() {
        progressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
        if (pos == 0) {
            searchMode = SearchMode.WEEK;
        } else if (pos == 1) {
            searchMode = SearchMode.MONTH;
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }


    public class SearchAsyncTask extends AsyncTask<Void, Void, Cursor> {

        private Context context;
        private Date date;
        private SearchMode searchMode;

        public SearchAsyncTask(Context context, Date date, SearchMode searchMode) {
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

        private Cursor performSearch(Date date, SearchMode searchMode) {
            ContentResolver contentResolver = getContentResolver();
            Uri uri = ShiftyContract.Workweek.CONTENT_URI;

            String selection;
            String[] selectionArgs;
            String sortOrder;

            Cursor searchResults = null;

            if (searchMode == SearchMode.WEEK) {
                String searchDatetime = DateUtils.getWeekStart(date, DateUtils.FMT_ISO_8601_DATETIME);
                selection = ShiftyContract.Workweek.COLUMN_WEEK_START_DATETIME + " = ?";
                selectionArgs = new String[] {searchDatetime};
                sortOrder = ShiftyContract.Workweek.COLUMN_WEEK_START_DATETIME + " ASC";

                searchResults = contentResolver.query(uri,
                        null,
                        selection,
                        selectionArgs,
                        sortOrder
                );

            } else if (searchMode == SearchMode.MONTH) {
                String format = DateUtils.FMT_ISO_8601_DATETIME;
                String monthStart = DateUtils.getMonthStart(date, format);
                String monthEnd = DateUtils.getMonthEnd(date, format);
                String weekStart = DateUtils.getWeekStart(monthStart, format);
                String weekEnd = DateUtils.getWeekEnd(monthEnd, format);

                selection = ShiftyContract.Workweek.COLUMN_WEEK_START_DATETIME + " >= ? and "
                        + ShiftyContract.Workweek.COLUMN_WEEK_END_DATETIME + " <= ?";
                selectionArgs = new String[] {weekStart, weekEnd};
                sortOrder = ShiftyContract.Workweek.COLUMN_WEEK_START_DATETIME + " ASC";

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
