package io.bradenhart.shifty.activity;

import android.content.Intent;
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
import android.view.animation.Animation;
import android.widget.TextView;

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
import io.bradenhart.shifty.adapter.WorkWeekRecyclerViewAdapter;
import io.bradenhart.shifty.database.DatabaseManager;
import io.bradenhart.shifty.domain.Shift;
import io.bradenhart.shifty.domain.WorkWeek;
import io.bradenhart.shifty.util.DateUtil;

import static io.bradenhart.shifty.util.Utils.*;

public class ShiftViewActivity extends AppCompatActivity implements Animation.AnimationListener {

    final String TAG = "MainActivity.class";
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
    private MySectionAdapter sectionedAdapter;

    private SimpleDateFormat oldFmt;
    private SimpleDateFormat newFmt;

    private Animation fadeInResetAnim, fadeOutResetAnim, spinLoadAnim, spinResetAnim, fadeInLoadAnim, fadeOutLoadAnim;

    private boolean showCurrent = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shiftview);

        oldFmt = new SimpleDateFormat(DateUtil.FMT_DATETIME, Locale.ENGLISH);
        newFmt = new SimpleDateFormat("MMMM dd", Locale.ENGLISH);

        fadeInResetAnim = getAnim(this, R.anim.fade_in);
        fadeOutResetAnim = getAnim(this, R.anim.fade_out);
        fadeInLoadAnim = getAnim(this, R.anim.fade_in);
        fadeOutLoadAnim = getAnim(this, R.anim.fade_out);
        spinLoadAnim = getAnim(this, R.anim.spin);
        spinResetAnim = getAnim(this, R.anim.spin);
        setAnimationListener(spinResetAnim,
                fadeInResetAnim, fadeOutResetAnim,
                fadeInLoadAnim, fadeOutLoadAnim,
                spinLoadAnim);

        ButterKnife.bind(ShiftViewActivity.this);

        // set up actionbar
        setUpActionBar();

//        navView.getMenu().getItem(0).setTitle("Shifts").setIcon(R.drawable.ic_view_list_white_24dp);
//        navView.inflateMenu(R.menu.menu_nav_bar_2);

//        TestData.deleteAllTestData(getApplicationContext());
//        TestData.addDataToDB(getApplicationContext());
        adapter = new WorkWeekRecyclerViewAdapter(this);

//        Map<String, List<Shift>> map = fetchWorkWeeks(weeks, offset);
        Map<String, List<Shift>> map;
        if (showCurrent)
            map = new DatabaseManager(getApplicationContext()).getShiftsFromCurrentWeek();
        else map = new DatabaseManager(getApplicationContext()).getShiftsBeforeCurrentWeek();

        Log.e("DB", map.keySet().size() + "");
        displayWorkWeeks(map);

        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
//        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
//        recyclerView.scrollToPosition(6);

        navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                Map<String, List<Shift>> map;

                switch (id) {
                    case R.id.menu_button_shifts:
                        // show current shifts
                        newShiftButton.setVisibility(View.VISIBLE);
                        makeToast(getApplicationContext(), "showing current shifts");
                        map = new DatabaseManager(getApplicationContext()).getShiftsFromCurrentWeek();
                        adapter.clear();
                        displayWorkWeeks(map);
                        adapter.notifyDataSetChanged();
                        break;
                    case R.id.menu_button_recent:
                        // show recent shifts
                        newShiftButton.setVisibility(View.GONE);
                        makeToast(getApplicationContext(), "showing recent shifts");
                        map = new DatabaseManager(getApplicationContext()).getShiftsBeforeCurrentWeek();
                        adapter.clear();
                        displayWorkWeeks(map);
                        adapter.notifyDataSetChanged();
                        break;
                    case R.id.menu_button_search:

                        break;
                    case R.id.menu_button_calculator:

                        break;
                }


                return true;
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
        Intent intent = new Intent(ShiftViewActivity.this, ShiftActivity.class);

        startActivity(intent);
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
            try {
                Date date = oldFmt.parse(week);
                String tag = UUID.randomUUID().toString();
                Log.e("TAG", "1... " + tag);
                System.out.println(map.get(week));
                WorkWeek workWeek = new WorkWeek("Week of " + newFmt.format(date), map.get(week));
//                WorkWeekSection workWeekSection = new WorkWeekSection.Builder()
//                        .setContext(this)
//                        .setTag(tag)
//                        .setWorkWeek(new WorkWeek("Week of " + newFmt.format(date), map.get(week)))
//                        .setAdapter(sectionedAdapter)
//                        .build();
//                sectionedAdapter.addSection(tag, workWeekSection);
                adapter.addWorkWeek(workWeek);
            } catch (ParseException e) {
                e.printStackTrace();
            }
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
}
