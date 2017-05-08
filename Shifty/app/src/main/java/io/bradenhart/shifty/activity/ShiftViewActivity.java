package io.bradenhart.shifty.activity;

import android.content.Intent;
import android.support.annotation.Nullable;
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
import android.view.animation.Animation;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
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
import io.bradenhart.shifty.database.DatabaseManager;
import io.bradenhart.shifty.database.TestData;
import io.bradenhart.shifty.domain.Shift;
import io.bradenhart.shifty.domain.WorkWeek;
import io.bradenhart.shifty.util.DateUtil;
import io.bradenhart.shifty.view.WorkWeekSection;

import static io.bradenhart.shifty.util.Utils.*;

public class ShiftViewActivity extends AppCompatActivity implements Animation.AnimationListener {

    final String TAG = "MainActivity.class";
    private final String appName = "Shifty";

    @BindView(R.id.appbar_main)
    AppBarLayout appBar;
    Toolbar toolbar;
    TextView titleView;
    @BindView(R.id.rv_shift_list)
    RecyclerView recyclerView;
    @BindView(R.id.button_new_shift)
    FloatingActionButton newShiftButton;
    @BindDimen(R.dimen.workweek_item_height)
    int itemHeight;
    @BindDimen(R.dimen.workweek_shift_progress_width)
    int progressWidth;
    @BindDimen(R.dimen.margin_5dp)
    int margin5dp;

    private final int DEFAULT_DISPLAY_COUNT = 20;
    private int weeks = DEFAULT_DISPLAY_COUNT;
    private int offset = 0;
    private int count = 0;

    private MySectionAdapter sectionedAdapter;

    private SimpleDateFormat oldFmt;
    private SimpleDateFormat newFmt;

    private Animation fadeInResetAnim, fadeOutResetAnim, spinLoadAnim, spinResetAnim, fadeInLoadAnim, fadeOutLoadAnim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shiftview);

//        Shift[] gen = TestData.generateShifts(1);
//        Log.e("GEN", Arrays.toString(gen));

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

//        TestData.deleteAllTestData(getApplicationContext());
//        TestData.addDataToDB(getApplicationContext());

        sectionedAdapter = new MySectionAdapter();

        Map<String, List<Shift>> map = fetchWorkWeeks(weeks, offset);

        Log.e("DB", map.keySet().size() + "");
        displayWorkWeeks(map);

        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
//        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(sectionedAdapter);
//        recyclerView.scrollToPosition(6);

        recyclerView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View view, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                String nextWeekDate = ((WorkWeekSection) sectionedAdapter.getSectionForPosition(sectionedAdapter.getItemCount() - 1)).getEndDate();
                Log.e("NEXT", nextWeekDate);
                count = new DatabaseManager(getApplicationContext()).countShiftsAfterDate(nextWeekDate);

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
