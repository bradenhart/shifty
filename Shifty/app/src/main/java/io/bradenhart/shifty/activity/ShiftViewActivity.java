package io.bradenhart.shifty.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.bradenhart.shifty.R;
import io.bradenhart.shifty.adapter.WorkWeekRecyclerViewAdapter;
import io.bradenhart.shifty.data.ShiftyContract;

/**
 * Allows the user to view their shifts. Displays the current week's shifts
 * and all future shifts, by default. Displays recent shifts when the user
 * navigates to the Recent view (in the bottom navigation bar).
 *
 * @author bradenhart
 */
public class ShiftViewActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, SwipeRefreshLayout.OnRefreshListener {

    // logtag
    final String TAG = ShiftViewActivity.class.getSimpleName();
    // title to be displayed in the toolbar
    private final String title = "Shifty";
    /* key constants */
    // key for storing the display mode in Shared Preferences
    public static final String KEY_DISPLAY_MODE = "KEY_DISPLAY_MODE";
    /* other constants */
    // values for the display mode
    private static final String MODE_RECENT = "MODE_RECENT";
    private static final String MODE_CURRENT = "MODE_CURRENT";
    // id values for the cursor loaders
    private static final int ID_CURRENT_WORKWEEK_LOADER = 88;
    private static final int ID_RECENT_WORKWEEK_LOADER = 44;

    private Context context;

    /* components for the Activity's actionbar */
    @BindView(R.id.appbar_shiftview)
    AppBarLayout appBar;
    Toolbar toolbar;
    TextView titleView;

    // allows for the recyclerview to be refreshed with a swipe gesture
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;
    // displays the shifts
    @BindView(R.id.rv_shift_list)
    RecyclerView recyclerView;
    // takes the user to the new shift screen
    @BindView(R.id.button_new_shift)
    FloatingActionButton newShiftButton;
    // displays buttons to different screens/functions of the app
    @BindView(R.id.bottomnavigation_shiftview)
    BottomNavigationView navView;

    // set of valid display modes for the recyclerview
    public enum DisplayMode {
        CURRENT(MODE_CURRENT), RECENT(MODE_RECENT);

        private String value;

        DisplayMode(String value) {
            this.value = value;
        }

        // get the string value for the DisplayMode
        public String getValue() {
            return value;
        }

        // get a DisplayMode from a given value
        public static DisplayMode get(String value) {
            if (value.equals(MODE_RECENT)) return RECENT;
            else return CURRENT; // CURRENT is the default
        }

    }

    // contains the main projection values used when querying the Workweek table in the db
    public static final String[] MAIN_WORKWEEK_PROJECTION = {
            ShiftyContract.Workweek._ID,
            ShiftyContract.Workweek.COLUMN_WEEK_START_DATETIME,
            ShiftyContract.Workweek.COLUMN_TOTAL_PAID_HOURS,
    };

    // the adapter for the recyclerview, displays workweeks
    private WorkWeekRecyclerViewAdapter adapter;

    /**
     * Used for starting this Activity. Ensures that the Activity is started with the required
     * extras.
     * @param context The context of the Activity that calls this method
     */
    public static void start(Context context) {
        Intent intent = new Intent(context, ShiftViewActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shiftview);
        context = ShiftViewActivity.this;

        ButterKnife.bind(this);

        setUpActionBar();

//        TestData.addDataToDB(getContentResolver());

        swipeRefreshLayout.setOnRefreshListener(this);

        adapter = new WorkWeekRecyclerViewAdapter(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
//        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (navView.getSelectedItemId() == id) return false;

                switch (id) {
                    case R.id.menu_button_shifts:
                        // show current shifts
                        saveDisplayMode(DisplayMode.CURRENT);
                        newShiftButton.setVisibility(View.VISIBLE);
                        restartLoader(getLoaderIDForDisplayMode());
                        break;
                    case R.id.menu_button_recent:
                        // show recent shifts
                        saveDisplayMode(DisplayMode.RECENT);
                        newShiftButton.setVisibility(View.GONE);
                        startLoader(getLoaderIDForDisplayMode());
                        break;
                    case R.id.menu_button_calculator:
                        CalculatorActivity.start(context);
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
        startLoader(getLoaderIDForDisplayMode());

        updateNavViewSelectedItem();
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
                SearchActivity.start(context);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
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
    }

    @OnClick(R.id.button_new_shift)
    public void onClickNewShiftButton() {
        ShiftActivity.start(context, ShiftActivity.Mode.CREATE, null);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // restart the Loader for the current display mode
        restartLoader(getLoaderIDForDisplayMode());
        // ensure the navigation view has the correct item selected
        updateNavViewSelectedItem();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    /**
     * Save the provided DisplayMode value in SharedPreferences.
     * @param mode the display mode to save
     */
    private void saveDisplayMode(DisplayMode mode) {
        SharedPreferences sp = getSharedPreferences(getString(R.string.shared_preferences_name), MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        editor.putString(KEY_DISPLAY_MODE, mode.getValue()).apply();
    }

    /**
     * Retrieve the DisplayMode value stored in SharedPreferences.
     * @return the display mode retrieved
     */
    private DisplayMode getDisplayMode() {
        SharedPreferences sp = getSharedPreferences(getString(R.string.shared_preferences_name), MODE_PRIVATE);

        String value = sp.getString(KEY_DISPLAY_MODE, MODE_CURRENT);

        return DisplayMode.get(value);
    }

    /**
     * Get the id for the Loader that matches the current display mode.
     * @return the id for the Loader
     */
    private int getLoaderIDForDisplayMode() {
//        if (getDisplayMode() == DisplayMode.CURRENT) return ID_CURRENT_WORKWEEK_LOADER;
        if (getDisplayMode() == DisplayMode.RECENT) return ID_RECENT_WORKWEEK_LOADER;
        return ID_CURRENT_WORKWEEK_LOADER; // default is the current workweek loader id
    }

    /**
     * Updates the selected item in the BottomNavigationView.
     */
    private void updateNavViewSelectedItem() {
        if (navView == null) return;
        int itemID = getDisplayMode() == DisplayMode.CURRENT ? R.id.menu_button_shifts : R.id.menu_button_recent;
        navView.setSelectedItemId(itemID);
    }

    @Override
    public void onBackPressed() {
        // if the user is in the Recent display mode, go back to Current display mode
        if (getDisplayMode() == DisplayMode.RECENT) {
            saveDisplayMode(DisplayMode.CURRENT);
            restartLoader(ID_CURRENT_WORKWEEK_LOADER);
            updateNavViewSelectedItem();
        } else {
            // go to home screen
            super.onBackPressed();
        }
    }

    @Override
    public void onRefresh() {
        // only show refreshing animation when in Current display mode
        if (getDisplayMode() == DisplayMode.CURRENT) {
            restartLoader(ID_CURRENT_WORKWEEK_LOADER);
        } else if (getDisplayMode() == DisplayMode.RECENT) {
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    /* Loader logic */
    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle args) {
        // sort order for database query
        String sortOrder;
        // column selection for database query
        String selection;

        switch (loaderId) {
            // loads workweeks from current week onwards
            case ID_CURRENT_WORKWEEK_LOADER:
                swipeRefreshLayout.setEnabled(true);
                /* Sort order: ascending by start date */
                sortOrder = ShiftyContract.Workweek.COLUMN_WEEK_START_DATETIME + " ASC";
                /* Select all workweeks from the this week onwards */
                selection = ShiftyContract.Workweek.getSQLSelectForThisWeekOnwards();

                return new CursorLoader(context,
                        ShiftyContract.Workweek.CONTENT_URI,
                        MAIN_WORKWEEK_PROJECTION,
                        selection,
                        null,
                        sortOrder);

            // loads workweeks from before current week
            case ID_RECENT_WORKWEEK_LOADER:
                swipeRefreshLayout.setEnabled(false);
                /* Sort order: ascending by start date */
                sortOrder = ShiftyContract.Workweek.COLUMN_WEEK_START_DATETIME + " DESC";
                /* Select all workweeks from the this week onwards */
                selection = ShiftyContract.Workweek.getSQLSelectForBeforeThisWeek();

                return new CursorLoader(context,
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
        // swap the adapter's old cursor with the new cursor
        adapter.swapCursor(data);
        // hide the loading animation
        hideLoading();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }

    /**
     * Shows the loading animation.
     */
    private void showLoading() {
        swipeRefreshLayout.setRefreshing(true);
    }

    /**
     * Hides the loading animation.
     */
    private void hideLoading() {
        swipeRefreshLayout.setRefreshing(false);
    }

    /**
     * Starts the Loader that has the provided id.
     * @param loaderID the id of the Loader being started
     */
    private void startLoader(int loaderID) {
        LoaderManager lm = getSupportLoaderManager();
        if (lm.getLoader(loaderID) == null || !lm.getLoader(loaderID).isStarted()) {
            lm.initLoader(loaderID, null, this);
        } else {
            lm.restartLoader(loaderID, null, this);
        }
    }

    /**
     * Restarts the Loader that has the provided id.
     * @param loaderID the id of the Loader being restarted
     */
    private void restartLoader(int loaderID) {
        LoaderManager lm = getSupportLoaderManager();
        if (lm.getLoader(loaderID) == null || !lm.getLoader(loaderID).isStarted()) {
            lm.initLoader(loaderID, null, this);
        } else {
            getSupportLoaderManager().restartLoader(loaderID, null, this);
        }

    }
}
