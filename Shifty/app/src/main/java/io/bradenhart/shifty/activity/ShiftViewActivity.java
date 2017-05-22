package io.bradenhart.shifty.activity;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import butterknife.BindDimen;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.bradenhart.shifty.R;
import io.bradenhart.shifty.adapter.WorkWeekRecyclerViewAdapter;
import io.bradenhart.shifty.data.DatabaseManager;
import io.bradenhart.shifty.data.ShiftyContract;
import io.bradenhart.shifty.data.TestData;
import io.bradenhart.shifty.domain.Shift;
import io.bradenhart.shifty.domain.WorkWeek;
import io.bradenhart.shifty.util.DateUtil;
import io.bradenhart.shifty.util.Utils;

import static io.bradenhart.shifty.util.Utils.*;

public class ShiftViewActivity extends AppCompatActivity implements Animation.AnimationListener {

    final String TAG = "ShiftViewActivity.class";
    private final String title = "Shifty";

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
    @BindDimen(R.dimen.workweek_item_height)
    int itemHeight;
    @BindDimen(R.dimen.workweek_shift_progress_width)
    int progressWidth;
    @BindDimen(R.dimen.margin_5dp)
    int margin5dp;

    private WorkWeekRecyclerViewAdapter adapter;

    private boolean showCurrent = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shiftview);

        ButterKnife.bind(ShiftViewActivity.this);

        // set up actionbar
        setUpActionBar();

//        TestData.deleteAllTestData(getApplicationContext());
//        TestData.addDataToDB(getApplicationContext());

        /* TEST */
//        int deleted = getContentResolver().delete(ShiftyContract.Shift.CONTENT_URI, null, null);
//        Log.d("TESTDATA", "deleted " + deleted + " shifts before inserting test data");
//        TestData.addDataToDB(getContentResolver());
        ContentValues values = new ContentValues();
        values.put(ShiftyContract.Workweek._ID, "2017-05-22 00:00:00.000");
        values.put(ShiftyContract.Workweek.COLUMN_WEEK_START_DATETIME, "2017-05-22 00:00:00.000");
        values.put(ShiftyContract.Workweek.COLUMN_WEEK_END_DATETIME, "2017-05-28 23:59:59.999");

//        getContentResolver().insert(ShiftyContract.Workweek.CONTENT_URI, values);

        ContentValues values2 = new ContentValues();
//        values2.put(ShiftyContract.Shift.COLUMN_WORKWEEK_ID, "2017-05-22 00:00:00.000");
//        values2.put(ShiftyContract.Shift.COLUMN_SHIFT_START_DATETIME, "2017-05-23 08:00:00.000");
//        values2.put(ShiftyContract.Shift.COLUMN_SHIFT_END_DATETIME, "2017-05-23 16:30:00.000");
        values2.put(ShiftyContract.Shift.COLUMN_WORKWEEK_ID, "2017-05-22 00:00:00.000");
        values2.put(ShiftyContract.Shift.COLUMN_SHIFT_START_DATETIME, "2017-05-25 08:00:00.000");
        values2.put(ShiftyContract.Shift.COLUMN_SHIFT_END_DATETIME, "2017-05-25 16:30:00.000");


//        getContentResolver().delete(ShiftyContract.Shift.CONTENT_URI, null, null);
//        getContentResolver().insert(ShiftyContract.Shift.CONTENT_URI, values2);

        ContentValues values3 = new ContentValues();
        values3.put(ShiftyContract.Shift.COLUMN_SHIFT_START_DATETIME, "2017-05-25 10:00:00.000");
        values3.put(ShiftyContract.Shift.COLUMN_SHIFT_END_DATETIME, "2017-05-25 16:30:00.000");

//        getContentResolver().update(Uri.withAppendedPath(ShiftyContract.Shift.CONTENT_URI, "1"), values3, null, null);

        ContentValues values4 = new ContentValues();
        values4.put(ShiftyContract.Shift.COLUMN_SHIFT_START_DATETIME, "2017-05-16 8:00:00.000");
        values4.put(ShiftyContract.Shift.COLUMN_SHIFT_END_DATETIME, "2017-05-16 16:30:00.000");
        values4.put(ShiftyContract.Shift.COLUMN_WORKWEEK_ID, "2017-05-15 00:00:00.000");

        getContentResolver().update(Uri.withAppendedPath(ShiftyContract.Shift.CONTENT_URI, "1"), values4, null, null);

        /**/

//        adapter = new WorkWeekRecyclerViewAdapter(this, getCurrentWorkWeeks());

        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
//        recyclerView.setHasFixedSize(true);
//        recyclerView.setAdapter(adapter);
        recyclerView.setAdapter(new RecyclerView.Adapter() {
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return null;
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

            }

            @Override
            public int getItemCount() {
                return 0;
            }
        });
//        recyclerView.scrollToPosition(6);

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

    private Map<String, List<Shift>> fetchWorkWeeks(int weeks, int offset) {
        String[] datetimes = DateUtil.getDateTimesForRange(weeks, offset);

        return new DatabaseManager(getApplicationContext()).getShiftsInDateRange(datetimes);
    }

    private Map<String, List<Shift>> fetchWorkWeeks(int offset) {
        return fetchWorkWeeks(1, offset);
    }

    private void displayWorkWeeks(Map<String, List<Shift>> map) {
        for (String week : map.keySet()) {
            System.out.println("week: " + week);
            String tag = UUID.randomUUID().toString();
            Log.e("TAG", "1... " + tag);
            System.out.println(map.get(week));
            WorkWeek workWeek = new WorkWeek(week, map.get(week));
            adapter.addWorkWeek(workWeek);
        }
    }

    @Override
    public void onAnimationStart(Animation animation) {

        /** RESET button animations START */
        /** */

        /** LOAD MORE button animations START */
        /** */

    }

    @Override
    public void onAnimationEnd(Animation animation) {

        /** RESET button animations END */
        /** */

        /** LOAD MORE button animations END */
        /** */

    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }

    private void setAnimationListener(Animation... animations) {
        for (Animation a : animations) {
            if (a != null) a.setAnimationListener(this);
        }
    }

    private Cursor getCurrentWorkWeeks() {
        // get the ISO8601 formatted string for Monday 00:00 of the current week
        String datetimeString = DateUtil.getStartDateForCurrentWeek();
//        String[] projection = new String[] { ShiftyContract.Workweek.COLUMN_WEEK_START_DATETIME };
//        String selection = ShiftyContract.Shift.COLUMN_WEEK_START_DATETIME + " >= ?";
        String[] selectionArgs = new String[] { datetimeString };

        return getContentResolver().query(
                ShiftyContract.Shift.CONTENT_URI, // query Shift table (/shift)
                null,
                null, // get all columns
                selectionArgs,
                ShiftyContract.Workweek._ID + " asc" // order by week start date, earliest to latest
        );
    }

//    private Cursor getCurrentShifts() {
//        // get the ISO8601 formatted string for Monday 00:00 of the current week
//        String datetimeString = DateUtil.getStartDateForCurrentWeek();
//        String selection = ShiftyContract.Shift.COLUMN_WEEK_START_DATETIME + " >= ?";
//        String[] selectionArgs = new String[] { datetimeString };
//
//        return getContentResolver().query(
//                ShiftyContract.Shift.CONTENT_URI, // query Shift table (/shift)
//                null, // get all columns
//                selection, // get all shifts from this week onwards
//                selectionArgs,
//                ShiftyContract.Shift.COLUMN_SHIFT_START_DATETIME + " asc" // order by shift start time, earliest to latest
//        );
//    }

//    private void testGetCurrentShifts() {
//        Log.d(TAG, "start testGetCurrentShifts");
//        Cursor cursor = getCurrentShifts();
//        if (cursor.moveToFirst()) {
//            do {
//                int dateCol = cursor.getColumnIndex(ShiftyContract.Shift.COLUMN_SHIFT_START_DATETIME);
//                String date = cursor.getString(dateCol);
//                Log.d(TAG, date);
//            } while (cursor.moveToNext());
//        } else {
//            Log.d(TAG, "cursor was empty");
//        }
//    }

}
