package io.bradenhart.shifty.activity;

import android.content.Intent;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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
import butterknife.Optional;
import io.bradenhart.shifty.R;
import io.bradenhart.shifty.adapter.MySectionAdapter;
import io.bradenhart.shifty.database.DatabaseManager;
import io.bradenhart.shifty.database.ShiftrContract;
import io.bradenhart.shifty.database.TestData;
import io.bradenhart.shifty.domain.Shift;
import io.bradenhart.shifty.domain.WorkWeek;
import io.bradenhart.shifty.util.DateUtil;
import io.bradenhart.shifty.view.WorkWeekSection;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;

public class MainActivity extends AppCompatActivity {

    final String TAG = "MainActivity.class";
    private final String appName = "Shifty";

    @BindView(R.id.appbar_main)
    AppBarLayout appBar;
    Toolbar toolbar;
    TextView titleView;
    @BindView(R.id.rv_shift_list)
    RecyclerView recyclerView;
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

    private MySectionAdapter sectionedAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(MainActivity.this);

        // set up actionbar
        toolbar = ButterKnife.findById(appBar, R.id.toolbar);

        setUpActionBar(toolbar);

        String[] datetimes = DateUtil.getDateTimesForRange(3, 0);

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

    }


    /* initialisation/setup methods */
    private void setUpActionBar(Toolbar toolbar) {
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
        Intent intent = new Intent(MainActivity.this, ShiftActivity.class);

        startActivity(intent);
    }
}
