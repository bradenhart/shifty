package io.bradenhart.shifty.activity;

import android.content.Intent;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import butterknife.BindDimen;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.bradenhart.shifty.R;
import io.bradenhart.shifty.adapter.MySectionAdapter;
import io.bradenhart.shifty.database.DatabaseManager;
import io.bradenhart.shifty.database.TestData;
import io.bradenhart.shifty.domain.Shift;
import io.bradenhart.shifty.domain.WorkWeek;
import io.bradenhart.shifty.util.DateUtil;
import io.bradenhart.shifty.view.WorkWeekSection;

public class ShiftViewActivity extends AppCompatActivity {

    final String TAG = "MainActivity.class";
    private final String appName = "Shifty";

    @BindView(R.id.appbar_main)
    AppBarLayout appBar;
    Toolbar toolbar;
    TextView titleView;
    @BindView(R.id.rv_shift_list)
    RecyclerView recyclerView;
    @BindView(R.id.button_load_more)
    FloatingActionButton loadMoreButton;
    @BindView(R.id.cardview_bottom_action_bar)
    CardView bottomActionBar;
    @BindView(R.id.button_select_weeks)
    ImageButton selectWeeksButton;
    @BindView(R.id.button_new_shift)
    ImageButton newShiftButton;
    @BindDimen(R.dimen.workweek_item_height)
    int itemHeight;
    @BindDimen(R.dimen.workweek_shift_progress_width)
    int progressWidth;

    private int weeks = 3;
    private int offset = 0;

    private MySectionAdapter sectionedAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shiftview);

        ButterKnife.bind(ShiftViewActivity.this);

        // set up actionbar
        setUpActionBar();

        String[] datetimes = DateUtil.getDateTimesForRange(weeks, offset);

        TestData.deleteAllTestData(getApplicationContext());
        TestData.addDataToDB(getApplicationContext());

        sectionedAdapter = new MySectionAdapter();

        Map<String, List<Shift>> map = new DatabaseManager(getApplicationContext()).getShiftsInDateRange(datetimes);
        SimpleDateFormat oldFmt = new SimpleDateFormat(DateUtil.FMT_DATETIME, Locale.ENGLISH);
        SimpleDateFormat newFmt = new SimpleDateFormat("MMMM dd", Locale.ENGLISH);

        Log.e("DB", map.keySet().size() + "");
        for (String week : map.keySet()) {
            System.out.println("week: " + week);
            try {
                Date date = oldFmt.parse(week);
                String tag = UUID.randomUUID().toString();
                System.out.println(map.get(week));
                WorkWeekSection workWeekSection = new WorkWeekSection.Builder()
                                .setContext(this)
                                .setTag(tag)
                                .setWorkWeek(new WorkWeek("Week of " + newFmt.format(date), map.get(week)))
                                .setAdapter(sectionedAdapter)
                                .build();
                sectionedAdapter.addSection(tag, workWeekSection);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
//        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(sectionedAdapter);
//        recyclerView.scrollToPosition(6);

        recyclerView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View view, int i, int i1, int i2, int i3) {
                if (!recyclerView.canScrollVertically(1)) {
//                    Log.e("SCROLL", "Can't scroll up");
                    loadMoreButton.setVisibility(View.VISIBLE);
                } else {
                    if (loadMoreButton.getVisibility() == View.VISIBLE) {
                        loadMoreButton.setVisibility(View.GONE);
                    }
                }

//                if (!recyclerView.canScrollVertically(0)) {
//                    Log.e("SCROLL", "Can't scroll down");
//                }
            }
        });

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
        titleView.setText(appName);
        // will show the back arrow/caret and make it clickable. will not return home unless parent activity is specified
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // shows logo/icon with caret/arrow if passed true. will not show logo/icon if passed false
//        getSupportActionBar().setDisplayShowHomeEnabled(true);
        // set the navigation drawer icon to the hamburger icon
//        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
    }

    @OnClick(R.id.button_load_more)
    public void clickLoadMoreButton() {
        offset = weeks;
        weeks += 1;
        String[] datetimes = DateUtil.getDateTimesForRange(1, offset);

//        sectionedAdapter = new SectionedRecyclerViewAdapter();

        Map<String, List<Shift>> map = new DatabaseManager(getApplicationContext()).getShiftsInDateRange(datetimes);
        SimpleDateFormat oldFmt = new SimpleDateFormat(DateUtil.FMT_DATETIME, Locale.ENGLISH);
        SimpleDateFormat newFmt = new SimpleDateFormat("MMMM dd", Locale.ENGLISH);

        if (map.keySet().size() == 0) {
            loadMoreButton.setVisibility(View.GONE);
        }

        Toast.makeText(getApplicationContext(), map.keySet().size() + "", Toast.LENGTH_SHORT).show();
        for (String week : map.keySet()) {
            System.out.println("week: " + week);
            try {
                Date date = oldFmt.parse(week);
                String tag = UUID.randomUUID().toString();
                Log.e("TAG", "1... " + tag);
                System.out.println(map.get(week));
                WorkWeekSection workWeekSection = new WorkWeekSection.Builder()
                        .setContext(this)
                        .setTag(tag)
                        .setWorkWeek(new WorkWeek("Week of " + newFmt.format(date), map.get(week)))
                        .setAdapter(sectionedAdapter)
                        .build();

                sectionedAdapter.addSection(tag, workWeekSection);
//                recyclerView.invalidate();
                sectionedAdapter.notifyDataSetChanged();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    @OnClick(R.id.button_select_weeks)
    public void onClickSelectWeeks() {
        String[] datetimes = DateUtil.getDateTimesForRange(1, -1);

//        sectionedAdapter = new SectionedRecyclerViewAdapter();

        Map<String, List<Shift>> map = new DatabaseManager(getApplicationContext()).getShiftsInDateRange(datetimes);
        SimpleDateFormat oldFmt = new SimpleDateFormat(DateUtil.FMT_DATETIME, Locale.ENGLISH);
        SimpleDateFormat newFmt = new SimpleDateFormat("MMMM dd", Locale.ENGLISH);

        Toast.makeText(getApplicationContext(), map.keySet().size() + "", Toast.LENGTH_SHORT).show();
        for (String week : map.keySet()) {
            System.out.println("week: " + week);
            try {
                Date date = oldFmt.parse(week);
                String tag = UUID.randomUUID().toString();
                Log.e("TAG", "1... " + tag);
                System.out.println(map.get(week));
                WorkWeekSection workWeekSection = new WorkWeekSection.Builder()
                        .setContext(this)
                        .setTag(tag)
                        .setWorkWeek(new WorkWeek("Week of " + newFmt.format(date), map.get(week)))
                        .setAdapter(sectionedAdapter)
                        .build();

                sectionedAdapter.addSectionAtStart(tag, workWeekSection);
//                recyclerView.invalidate();
                sectionedAdapter.notifyDataSetChanged();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

    }

    @OnClick(R.id.button_new_shift)
    public void onClickNewShiftButton() {
        Intent intent = new Intent(ShiftViewActivity.this, ShiftActivity.class);

        startActivity(intent);
    }
}
