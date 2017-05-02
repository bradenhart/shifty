package io.bradenhart.shifty.ui;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import io.bradenhart.shifty.R;
import io.bradenhart.shifty.domain.ShiftTime;

import java.util.Locale;

import butterknife.ButterKnife;

/**
 * Created by bradenhart on 25/03/17.
 */

public class TimeScroller extends FrameLayout {

    final String TAG = "TimeScroller";

    View rootView;
    LinearLayout hourLayout, minLayout, periodLayout;
    MyScrollView hourScrollView, minScrollView, periodScrollView;

    String hour = "1";
    String min = "00";
    String period = "AM";

//    @BindDimen(R.dimen.timeScrollerTextViewHeight) int unitHeight;

//    private enum Scroller {
//        HOUR, MIN, PERIOD;
//    }

    public TimeScroller(@NonNull Context context) {
        super(context);
        init(context);
//        ButterKnife.bind(this);
    }

    public TimeScroller(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
        ButterKnife.bind(this);
    }

    /* initialise the components of the TimeScroller here */
    private void init(Context context) {
        rootView = inflate(context, R.layout.view_time_scroller, this);

        hourLayout = (LinearLayout) rootView.findViewById(R.id.llayout_hours);
        minLayout = (LinearLayout) rootView.findViewById(R.id.llayout_mins);
        periodLayout = (LinearLayout) rootView.findViewById(R.id.llayout_period);

        hourScrollView = (MyScrollView) rootView.findViewById(R.id.scrollview_hours);
        minScrollView = (MyScrollView) rootView.findViewById(R.id.scrollview_mins);
        periodScrollView = (MyScrollView) rootView.findViewById(R.id.scrollview_period);

        initScrollViewActions(hourScrollView, hourLayout);
        initScrollViewActions(minScrollView, minLayout);
        initScrollViewActions(periodScrollView, periodLayout);

    }

    private void initScrollViewActions(final MyScrollView scrollView, final LinearLayout layout) {
        scrollView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (MotionEventCompat.getActionMasked(motionEvent) == MotionEvent.ACTION_UP) {
                    scrollView.startScrollerTask();
                    updateTimeInfo();
                }

                return false;
            }
        });


        scrollView.setOnScrollStoppedListener(new MyScrollView.OnScrollStoppedListener() {
            @Override
            public void onScrollStopped() {
                int childHeight = layout.getHeight() / layout.getChildCount();
                int scrollY = scrollView.getScrollY();
                int offset = scrollY % childHeight;
//                Log.e(TAG, "layout height: " + layout.getHeight());
//                Log.e(TAG, "child count: " + layout.getChildCount());
//                Log.e(TAG, "child height: " + childHeight);
//                Log.e(TAG, "offset: " + offset);
//                Log.e(TAG, "scrollY: " + scrollY);
//                Log.e(TAG, "index: " );

                int newY;
                int index = 0;
                if (offset >= 0 && offset < (0.6 * childHeight)) {
                    newY = scrollY - offset;
                    scrollView.scrollTo(0, newY);
                    //index = newY / childHeight + 1;
                    //System.out.println(((TextView) layout.getChildAt(newY/childHeight)).getText().toString());
                } else if (offset >= (0.6 * childHeight) && offset < childHeight) {
                    newY = scrollY + (childHeight - offset);
                    scrollView.scrollTo(0, newY);
                    //index = newY / childHeight;
                    //System.out.println(((TextView) layout.getChildAt(newY/childHeight)).getText().toString());
                }

//                if (layout.getChildAt(index) instanceof TextView) {
//                    String textVal = ((TextView) layout.getChildAt(index)).getText().toString();
//                    switch (scroller) {
//                        case HOUR: hour = textVal; break;
//                        case MIN: min = textVal; break;
//                        case PERIOD: period = textVal; break;
//                    }
//                }

                //Log.e(TAG, "time: " + getTime());
            }

        });
    }

    private void updateTimeInfo() {
        int childHeight;
        int scrollY;
        int index;

        // update hour
        childHeight = hourLayout.getHeight() / hourLayout.getChildCount();
        scrollY = hourScrollView.getScrollY();
        index = scrollY / childHeight + 1;

        if (hourLayout.getChildAt(index) instanceof TextView) {
            hour = ((TextView) hourLayout.getChildAt(index)).getText().toString();
        }

        // update min
        childHeight = minLayout.getHeight() / minLayout.getChildCount();
        scrollY = minScrollView.getScrollY();
        index = scrollY / childHeight + 1;

        if (minLayout.getChildAt(index) instanceof TextView) {
            min = ((TextView) minLayout.getChildAt(index)).getText().toString();
        }

        // update period
        childHeight = periodLayout.getHeight() / periodLayout.getChildCount();
        scrollY = periodScrollView.getScrollY();
        index = scrollY / childHeight + 1;

        if (periodLayout.getChildAt(index) instanceof TextView) {
            period = ((TextView) periodLayout.getChildAt(index)).getText().toString();
        }

    }

    public String getHour() {
        return hour;
    }

    public String getMin() {
        return min;
    }

    public String getPeriod() {
        return period;
    }

    public String getTimeString() {
        updateTimeInfo();
        return String.format(Locale.ENGLISH, "%s:%s %s", getHour(), getMin(), getPeriod());
    }

    public ShiftTime getTime() {
        updateTimeInfo();
        return new ShiftTime(ShiftTime.Hour.get(getHour()), ShiftTime.Minute.get(getMin()), ShiftTime.Period.get(getPeriod()));
    }

    public void resetScroller() {
        ObjectAnimator hourAnimator = ObjectAnimator.ofInt(hourScrollView, "scrollY", hourScrollView.getScrollY(), 0).setDuration(1500);
        ObjectAnimator minuteAnimator = ObjectAnimator.ofInt(minScrollView, "scrollY", minScrollView.getScrollY(), 0).setDuration(1500);
        ObjectAnimator periodAnimator = ObjectAnimator.ofInt(periodScrollView, "scrollY", periodScrollView.getScrollY(), 0).setDuration(1000);
        hourAnimator.start();
        minuteAnimator.start();
        periodAnimator.start();
        updateTimeInfo();
    }

    public long scrollHourTo(ShiftTime.Hour hour, long delay) {
        Log.e("HOUR", delay + "");
        if (hour == ShiftTime.Hour.ONE) return 0;
        int unitHeight = hourScrollView.getUnitHeight();
        ObjectAnimator objectAnimator = ObjectAnimator.ofInt(hourScrollView, "scrollY", 0, hour.ordinal() * unitHeight).setDuration(1500);
        objectAnimator.setStartDelay(delay);
        objectAnimator.start();

        return objectAnimator.getDuration();
    }

    public long scrollMinTo(ShiftTime.Minute minute, long delay) {
//        Log.e("MINUTE", delay + "");
        if (minute == ShiftTime.Minute.ZERO) return 0;
        int unitHeight = minScrollView.getUnitHeight();
        ObjectAnimator objectAnimator = ObjectAnimator.ofInt(minScrollView, "scrollY", 0, minute.ordinal() * unitHeight).setDuration(1500);
        objectAnimator.setStartDelay(delay);
        objectAnimator.start();
        return objectAnimator.getDuration();
    }

    public long scrollPeriodTo(ShiftTime.Period period, long delay) {
//        Log.e("PERIOD", delay + "");
        if (period == ShiftTime.Period.AM) return 0;
        int unitHeight = periodScrollView.getUnitHeight();
        ObjectAnimator objectAnimator = ObjectAnimator.ofInt(periodScrollView, "scrollY", 0, period.ordinal() * unitHeight).setDuration(1000);
        objectAnimator.setStartDelay(delay);
        objectAnimator.start();
        return objectAnimator.getDuration();
    }

    public long scrollAllTo(long startDelay, ShiftTime.Hour hour, ShiftTime.Minute minute, ShiftTime.Period period) {
        long delay = startDelay;
        delay += scrollHourTo(hour, startDelay);
        delay += scrollMinTo(minute, delay);
        delay += scrollPeriodTo(period, delay);
        Log.e("DELAY", delay + "");
        return delay;
    }

}
