package io.bradenhart.shifty.activity;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Handler;
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
import android.view.animation.LinearInterpolator;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
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
import io.bradenhart.shifty.ui.ViewWeightAnimationWrapper;
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
    @BindView(R.id.layout_fabs_container)
    LinearLayout fabsContainer;
    @BindView(R.id.button_load_more)
    FloatingActionButton loadMoreButton;
    @BindView(R.id.button_reset)
    FloatingActionButton resetButton;
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
    @BindDimen(R.dimen.margin_5dp)
    int margin5dp;

    private final int DEFAULT_DISPLAY_COUNT = 3;
    private int weeks = DEFAULT_DISPLAY_COUNT;
    private int offset = 0;

    private MySectionAdapter sectionedAdapter;

    private SimpleDateFormat oldFmt;
    private SimpleDateFormat newFmt;

    private Animation fadeInResetAnim, fadeOutLoadAnim, slideUpLoadAnim, slideInLoadAnim,
            slideInResetAnim, slideOutLoadAnim, slideOutResetAnim, scootLoadAnim, spinLoadAnim,
            slideInOutResetAnim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shiftview);

//        Shift[] gen = TestData.generateShifts(1);
//        Log.e("GEN", Arrays.toString(gen));

        oldFmt = new SimpleDateFormat(DateUtil.FMT_DATETIME, Locale.ENGLISH);
        newFmt = new SimpleDateFormat("MMMM dd", Locale.ENGLISH);

//        fadeInResetAnim = getAnim(this, R.anim.fade_in);
//        fadeOutResetAnim = getAnim(this, R.anim.fade_out);
        fadeOutLoadAnim = getAnim(this, R.anim.fade_out);
//        slideUpLoadAnim = getAnim(this, R.anim.slide_up);
        slideInLoadAnim = getAnim(this, R.anim.slide_in_right);
        slideInResetAnim = getAnim(this, R.anim.slide_in_left);
        slideOutLoadAnim = getAnim(this, R.anim.slide_out_left);
        slideOutResetAnim = getAnim(this, R.anim.slide_out_right);
        scootLoadAnim = getAnim(this, R.anim.scoot_to_center);
        spinLoadAnim = getAnim(this, R.anim.spin);
//        slideInOutResetAnim = getAnim(this, R.anim.slide_in_out_start_left);
        setAnimationListener(slideUpLoadAnim, slideInLoadAnim,
                slideInResetAnim, slideOutLoadAnim, slideOutResetAnim, scootLoadAnim, spinLoadAnim,
                slideInOutResetAnim);

        ButterKnife.bind(ShiftViewActivity.this);

        // set up actionbar
        setUpActionBar();

        TestData.deleteAllTestData(getApplicationContext());
        TestData.addDataToDB(getApplicationContext());

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
            public void onScrollChange(View view, int i, int i1, int i2, int i3) {
                if (sectionedAdapter.getItemCount() >= 1) {
                    String nextWeekDate = ((WorkWeekSection) sectionedAdapter.getSectionForPosition(sectionedAdapter.getItemCount() - 1)).getEndDate();
                    Log.e("NEXT", nextWeekDate);
                    int count = new DatabaseManager(getApplicationContext()).countShiftsAfterDate(nextWeekDate);

                    if (!recyclerView.canScrollVertically(1)) {
                        if (count > 0) {
                            Log.e("SCROLL", "show fab + " + count);
//                        loadMoreButton.startAnimation(slideUpLoadAnim);
                            loadMoreButton.startAnimation(slideInLoadAnim);
                        }

                        if (weeks > DEFAULT_DISPLAY_COUNT && resetButton.getVisibility() == View.GONE) {
                            Log.e("SHOW", "show reset fab, weeks: " + weeks);
                            resetButton.startAnimation(slideInResetAnim);
//                            resetButton.startAnimation(slideInOutResetAnim);
                        }
                    } else {
                        Log.e("SCROLL", "hide fab + " + count);
                        loadMoreButton.startAnimation(slideOutLoadAnim);
                    }
                }

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
//        String[] datetimes = DateUtil.getDateTimesForRange(1, offset);

//        sectionedAdapter = new SectionedRecyclerViewAdapter();

        Map<String, List<Shift>> map = fetchWorkWeeks(offset);

        checkLoadMoreState(map.size());

        displayWorkWeeks(map);

        sectionedAdapter.notifyDataSetChanged();

        recyclerView.smoothScrollToPosition(sectionedAdapter.getItemCount() - 1);
    }

    @OnClick(R.id.button_reset)
    public void clickResetButton() {
        makeToast(this, "Reset");
        sectionedAdapter.removeAllSections();
//        sectionedAdapter.notifyDataSetChanged();

        weeks = DEFAULT_DISPLAY_COUNT;
        offset = 0;

        Map<String, List<Shift>> map = fetchWorkWeeks(weeks, offset);

        checkLoadMoreState(map.size());

        displayWorkWeeks(map);

        sectionedAdapter.notifyDataSetChanged();
    }

    private void checkLoadMoreState(int size) {
        if (size == 0) {
            loadMoreButton.startAnimation(slideOutLoadAnim);
//            loadMoreButton.setVisibility(View.GONE);
            makeToast(this, "No more weeks to load.");
        }
    }


//    @OnClick(R.id.button_select_weeks)
//    public void onClickSelectWeeks() {
//        Map<String, List<Shift>> map = fetchWorkWeeks(-1);
//
//        makeToast(getApplicationContext(), map.keySet().size() + "");
//
//        sectionedAdapter.notifyDataSetChanged();
//    }

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

        /** RESET button animations */
        /* reset button is appearing on screen */
        if (animation == slideInResetAnim) {
            // make it visible
            resetButton.setVisibility(View.VISIBLE);
        }

        /* reset button is disappearing */
        if (animation == slideOutResetAnim) {
//            makeToast(getApplicationContext(), "reset slide out started");
            // stop user from clicking it as it moves off screen
            resetButton.setClickable(false);
        }

        //        if (animation == fadeInResetAnim) {
//            resetButton.setVisibility(View.VISIBLE);
//        }

//        if (animation == slideInOutResetAnim) {
//            resetButton.setVisibility(View.VISIBLE);
//        }
        /** */

        /** LOAD MORE button animations */
        /* load more button is appearing */
        if (animation == slideInLoadAnim) {
            loadMoreButton.setVisibility(View.VISIBLE);
        }

        /* load more button disappearing */
        if (animation == slideOutLoadAnim) {
            // stop user from clicking it
            loadMoreButton.setClickable(false);
        }

        //        if (animation == scootLoadAnim) {
//            loadMoreButton.setVisibility(View.VISIBLE);
//        }

        /*  */
//        if (animation == slideUpLoadAnim) {
//            loadMoreButton.setVisibility(View.VISIBLE);
//        }
        /** */


    }

    @Override
    public void onAnimationEnd(Animation animation) {

        /** RESET button animations */
        /* reset button has faded in */
//        if (animation == fadeInResetAnim) {
//            fadeOutResetAnim.setStartOffset(3000);
//            resetButton.startAnimation(fadeOutResetAnim);
//        }

        /* reset button has slid on screen */
        if (animation == slideInResetAnim) {
            ViewWeightAnimationWrapper animationWrapper = new ViewWeightAnimationWrapper(loadMoreButton);
            ObjectAnimator anim = ObjectAnimator.ofFloat(animationWrapper,
                    "weight",
                    animationWrapper.getWeight(),
                    1);
            anim.setDuration(2500);
            anim.start();


//            slideOutResetAnim.setStartOffset(3000);
            resetButton.postDelayed(new Runnable() {
                @Override
                public void run() {
//                    makeToast(getApplicationContext(), "run slide out animation");
                    resetButton.startAnimation(slideOutResetAnim);
                }
            }, 3000);
//            slideOutResetAnim.setDuration(1000);
//            resetButton.startAnimation(slideOutResetAnim);
            resetButton.setClickable(true);
        }

        /* reset button has faded out */
//        if (animation == fadeOutResetAnim) {
//            resetButton.setVisibility(View.GONE);
//        }

        /* reset button has slid off screen */
        if (animation == slideOutResetAnim) {
//            loadMoreButton.startAnimation(spinLoadAnim);
            float parentWidth = fabsContainer.getMeasuredWidth();
            float fabWidth = loadMoreButton.getMeasuredWidth();

            loadMoreButton.animate()
                    .setDuration(750)
                    .x(parentWidth/2 - fabWidth/2)
                    .setInterpolator(new LinearInterpolator())
//                    .setStartDelay(1500)
                    .start();
            resetButton.setVisibility(View.GONE);
        }
        /** */

        /** LOAD MORE button animations */
        /* load more button has slid off screen */
        if (animation == slideOutLoadAnim) {
            // hide the button
            loadMoreButton.setVisibility(View.GONE);
        }

        /* load more button has slid on screen */
        if (animation == slideInLoadAnim) {
            // allow user to click it
            loadMoreButton.setClickable(true);
        }

    }

    @Override
    public void onAnimationRepeat(Animation animation) {

//        if (animation == slideInOutResetAnim) {
//            slideInOutResetAnim.setStartOffset(5000);
//        }

    }

    private void setAnimationListener(Animation... animations) {
        for (Animation a : animations) {
            if (a != null) a.setAnimationListener(this);
        }
    }
}
