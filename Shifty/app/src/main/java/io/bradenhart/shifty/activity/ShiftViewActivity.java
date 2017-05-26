package io.bradenhart.shifty.activity;

import android.app.SearchManager;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import butterknife.BindDimen;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.bradenhart.shifty.R;
import io.bradenhart.shifty.adapter.WorkWeekRecyclerViewAdapter;
import io.bradenhart.shifty.data.ShiftyContract;
import io.bradenhart.shifty.data.TestData;

import static io.bradenhart.shifty.util.Utils.*;

public class ShiftViewActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    final String TAG = "ShiftViewActivity.class";
    private final String title = "Shifty";
    private final String KEY_DISPLAY_STATE = "KEY_DISPLAY_STATE";
    private static final int ID_CURRENT_WORKWEEK_LOADER = 88;
    private static final int ID_RECENT_WORKWEEK_LOADER = 44;

    @BindView(R.id.appbar_shiftview)
    AppBarLayout appBar;
    Toolbar toolbar;
    TextView titleView;
    @BindView(R.id.rv_shift_list)
    RecyclerView recyclerView;
    @BindView(R.id.button_new_shift)
    FloatingActionButton newShiftButton;
    @BindView(R.id.bottom_navigation)
    BottomNavigationView navView;
    @BindView(R.id.progressbar_shiftview)
    ProgressBar progressBar;
    @BindDimen(R.dimen.workweek_item_height)
    int itemHeight;
    @BindDimen(R.dimen.workweek_shift_progress_width)
    int progressWidth;
    @BindDimen(R.dimen.margin_5dp)
    int margin5dp;

    public static final String[] MAIN_WORKWEEK_PROJECTION = {
            ShiftyContract.Workweek._ID,
            ShiftyContract.Workweek.COLUMN_WEEK_START_DATETIME,
            ShiftyContract.Workweek.COLUMN_TOTAL_PAID_HOURS,
    };

    private int position = RecyclerView.NO_POSITION;
    private WorkWeekRecyclerViewAdapter adapter;

    private boolean showCurrent = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shiftview);

        ButterKnife.bind(ShiftViewActivity.this);

        if (savedInstanceState != null) {
            showCurrent = savedInstanceState.getBoolean(KEY_DISPLAY_STATE, true);
        }

        // set up actionbar
        setUpActionBar();

//        TestData.addDataToDB(getContentResolver());

        adapter = new WorkWeekRecyclerViewAdapter(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (navView.getSelectedItemId() == id) return false;

                switch (id) {
                    case R.id.menu_button_shifts:
                        // show current shifts
                        showCurrent = true;
                        newShiftButton.setVisibility(View.VISIBLE);
                        restartLoader(ID_CURRENT_WORKWEEK_LOADER);
                        makeToast(getApplicationContext(), "showing current shifts");
                        break;
                    case R.id.menu_button_recent:
                        // show recent shifts
                        showCurrent = false;
                        newShiftButton.setVisibility(View.GONE);
                        startLoader(ID_RECENT_WORKWEEK_LOADER);
                        makeToast(getApplicationContext(), "showing recent shifts");
                        break;
                    case R.id.menu_button_calculator:
                        CalculatorActivity.start(ShiftViewActivity.this);
                        break;
                }

                return true;
            }
        });

        showLoading();

        /*
         * Ensures a loader is initialized and active. If the loader doesn't already exist, one is
         * created and (if the activity is currently started) starts the loader. Otherwise
         * the last created loader is re-used.
         */
        startLoader(showCurrent ? ID_CURRENT_WORKWEEK_LOADER : ID_RECENT_WORKWEEK_LOADER);

        if (showCurrent) navView.setSelectedItemId(R.id.menu_button_shifts);
        else navView.setSelectedItemId(R.id.menu_button_recent);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_search:
                SearchActivity.start(ShiftViewActivity.this);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }

    /* initialisation/setup methods */
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
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // shows logo/icon with caret/arrow if passed true. will not show logo/icon if passed false
//        getSupportActionBar().setDisplayShowHomeEnabled(true);
        // set the navigation drawer icon to the hamburger icon
//        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
    }

    private boolean canLoadMoreState() {
        // TODO check if there are any more shifts to load from db
        return false;
    }

    @OnClick(R.id.button_new_shift)
    public void onClickNewShiftButton() {
        ShiftActivity.start(ShiftViewActivity.this, ShiftActivity.Mode.CREATE);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle args) {
        String sortOrder;
        String selection;

        switch (loaderId) {
            case ID_CURRENT_WORKWEEK_LOADER:
                /* Sort order: ascending by start date */
                sortOrder = ShiftyContract.Workweek.COLUMN_WEEK_START_DATETIME + " ASC";
                /* Select all workweeks from the this week onwards */
                selection = ShiftyContract.Workweek.getSQLSelectForThisWeekOnwards();

                return new CursorLoader(this,
                        ShiftyContract.Workweek.CONTENT_URI,
                        MAIN_WORKWEEK_PROJECTION,
                        selection,
                        null,
                        sortOrder);

            case ID_RECENT_WORKWEEK_LOADER:
                /* Sort order: ascending by start date */
                sortOrder = ShiftyContract.Workweek.COLUMN_WEEK_START_DATETIME + " DESC";
                /* Select all workweeks from the this week onwards */
                selection = ShiftyContract.Workweek.getSQLSelectForBeforeThisWeek();

                return new CursorLoader(this,
                        ShiftyContract.Workweek.CONTENT_URI,
                        MAIN_WORKWEEK_PROJECTION,
                        selection,
                        null,
                        sortOrder);

            default:
                throw new RuntimeException("Loader Not Implemented: " + loaderId);
        }

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
        if (position == RecyclerView.NO_POSITION) position = 0;

        if (data.getCount() != 0) {
            showRecyclerView();
        } else {
            hideLoading();
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
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

    private void startLoader(int loaderID) {
        LoaderManager lm = getSupportLoaderManager();
        if (lm.getLoader(loaderID) == null || !lm.getLoader(loaderID).isStarted()) {
            lm.initLoader(loaderID, null, this);
        } else {
            lm.restartLoader(loaderID, null, this);
        }
    }

    private void restartLoader(int loaderID) {
        LoaderManager lm = getSupportLoaderManager();
        if (lm.getLoader(loaderID) == null || !lm.getLoader(loaderID).isStarted()) {
            lm.initLoader(loaderID, null, this);
        } else {
            getSupportLoaderManager().restartLoader(loaderID, null, this);
        }

    }

    @Override
    protected void onRestart() {
        super.onRestart();

        Log.d(TAG, "onRestart()");
//        restartLoader(ID_CURRENT_WORKWEEK_LOADER);

        if (showCurrent) navView.setSelectedItemId(R.id.menu_button_shifts);
        else navView.setSelectedItemId(R.id.menu_button_recent);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        Log.d(TAG, "onRestoreInstanceState()");

        if (savedInstanceState != null) {
            showCurrent = savedInstanceState.getBoolean(KEY_DISPLAY_STATE, true);
            restartLoader(showCurrent ? ID_CURRENT_WORKWEEK_LOADER : ID_RECENT_WORKWEEK_LOADER);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_DISPLAY_STATE, showCurrent);
    }
}
