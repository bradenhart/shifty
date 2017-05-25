package io.bradenhart.shifty.activity;

import android.database.Cursor;
import android.net.Uri;
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
import android.support.v7.widget.Toolbar;
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

public class ShiftViewActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    final String TAG = "ShiftViewActivity.class";
    private final String title = "Shifty";
    private static final int ID_WORKWEEK_LOADER = 44;

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

        // set up actionbar
        setUpActionBar();

//        TestData.addDataToDB(getContentResolver());

        /* TEST */

        /**/

        adapter = new WorkWeekRecyclerViewAdapter(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
//        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
//        recyclerView.setAdapter(new RecyclerView.Adapter() {
//            @Override
//            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//                return null;
//            }
//
//            @Override
//            public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
//
//            }
//
//            @Override
//            public int getItemCount() {
//                return 0;
//            }
//        });
////        recyclerView.scrollToPosition(6);

//        navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
//            @Override
//            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//                int id = item.getItemId();
//
//                Map<String, List<Shift>> map;

//                switch (id) {
//                    case R.id.menu_button_shifts:
//                        // show current shifts
//                        newShiftButton.setVisibility(View.VISIBLE);
//                        makeToast(getApplicationContext(), "showing current shifts");
//                        map = new DatabaseManager(getApplicationContext()).getShiftsFromCurrentWeek();
//                        adapter.clear();
//                        displayWorkWeeks(map);
//                        adapter.notifyDataSetChanged();
//                        break;
//                    case R.id.menu_button_recent:
//                        // show recent shifts
//                        newShiftButton.setVisibility(View.GONE);
//                        makeToast(getApplicationContext(), "showing recent shifts");
//                        map = new DatabaseManager(getApplicationContext()).getShiftsBeforeCurrentWeek();
//                        adapter.clear();
//                        displayWorkWeeks(map);
//                        adapter.notifyDataSetChanged();
//                        break;
//                    case R.id.menu_button_search:
//
//                        break;
//                    case R.id.menu_button_calculator:
//                        CalculatorActivity.start(ShiftViewActivity.this);
//                        break;
//                }
//
//
//                return true;
//            }
//        });

//        testGetCurrentShifts();

        showLoading();

        /*
         * Ensures a loader is initialized and active. If the loader doesn't already exist, one is
         * created and (if the activity is currently started) starts the loader. Otherwise
         * the last created loader is re-used.
         */
        getSupportLoaderManager().initLoader(ID_WORKWEEK_LOADER, null, this);

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
        ShiftActivity.start(getApplicationContext(), ShiftActivity.Mode.CREATE);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle args) {

        switch (loaderId) {
            case ID_WORKWEEK_LOADER:
                /* URI for all rows of workweek data in our workweek table */
                Uri workweekQueryUri = ShiftyContract.Workweek.CONTENT_URI;
                /* Sort order: ascending by start date */
                String sortOrder = ShiftyContract.Workweek.COLUMN_WEEK_START_DATETIME + " ASC";
                /* Select all workweeks from the this week onwards */
                String selection = ShiftyContract.Workweek.getSQLSelectForThisWeekOnwards();

                return new CursorLoader(this,
                        workweekQueryUri,
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
//        recyclerView.smoothScrollToPosition(position);

        if (data.getCount() != 0) {
            showRecyclerView();
        } else {
            hideLoading();
        }

//        makeToast(ShiftViewActivity.this, "loaded " + data.getCount() + " workweeks");
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

}
