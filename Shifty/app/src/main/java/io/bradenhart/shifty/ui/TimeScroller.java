package io.bradenhart.shifty.ui;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import io.bradenhart.shifty.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * A custom view for choosing a time by scrolling through the hours,
 * minutes, and time period.
 *
 * @author bradenhart
 */
public class TimeScroller extends FrameLayout {

    // logtag
    final String TAG = "TimeScroller";

    View rootView;
    LinearLayout hourLayout, minuteLayout, periodLayout;
    MyScrollView hourScrollView, minuteScrollView, periodScrollView;

    String hour = "1";
    String minute = "00";
    String period = "AM";

    public TimeScroller(@NonNull Context context) {
        super(context);
        init(context);
    }

    public TimeScroller(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    /* initialise the components of the TimeScroller here */
    private void init(Context context) {
        rootView = inflate(context, R.layout.view_time_scroller, this);

        hourLayout = (LinearLayout) rootView.findViewById(R.id.llayout_hours);
        minuteLayout = (LinearLayout) rootView.findViewById(R.id.llayout_mins);
        periodLayout = (LinearLayout) rootView.findViewById(R.id.llayout_period);

        hourScrollView = (MyScrollView) rootView.findViewById(R.id.scrollview_hours);
        minuteScrollView = (MyScrollView) rootView.findViewById(R.id.scrollview_mins);
        periodScrollView = (MyScrollView) rootView.findViewById(R.id.scrollview_period);

        initScrollViewActions(hourScrollView, hourLayout);
        initScrollViewActions(minuteScrollView, minuteLayout);
        initScrollViewActions(periodScrollView, periodLayout);

    }

    private void initScrollViewActions(final MyScrollView scrollView, final LinearLayout layout) {
        scrollView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
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

                int newY;
                if (offset >= 0 && offset < (0.6 * childHeight)) {
                    newY = scrollY - offset;
                    scrollView.scrollTo(0, newY);
                } else if (offset >= (0.6 * childHeight) && offset < childHeight) {
                    newY = scrollY + (childHeight - offset);
                    scrollView.scrollTo(0, newY);
                }
            }

        });
    }

    /**
     * Updates the hour, minute, and period values for the currently
     * selected values in the timescroller.
     */
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

        // update minute
        childHeight = minuteLayout.getHeight() / minuteLayout.getChildCount();
        scrollY = minuteScrollView.getScrollY();
        index = scrollY / childHeight + 1;

        if (minuteLayout.getChildAt(index) instanceof TextView) {
            minute = ((TextView) minuteLayout.getChildAt(index)).getText().toString();
        }

        // update period
        childHeight = periodLayout.getHeight() / periodLayout.getChildCount();
        scrollY = periodScrollView.getScrollY();
        index = scrollY / childHeight + 1;

        if (periodLayout.getChildAt(index) instanceof TextView) {
            period = ((TextView) periodLayout.getChildAt(index)).getText().toString();
        }

    }

    /**
     * Gets the selected hour.
     *
     * @return the hour
     */
    public String getHour() {
        updateTimeInfo();
        if (getPeriod().equalsIgnoreCase("PM")) {
            try {
                Integer hourVal = Integer.parseInt(hour);
                hourVal += 12;
                return hourVal.toString();
            } catch (NumberFormatException e) {
                Log.e(TAG, "NumberFormatException converting " + hour + " to Integer");
            }
        }
        return hour;
    }

    /**
     * Gets the selected minute.
     *
     * @return the minute.
     */
    public String getMinute() {
        updateTimeInfo();
        return minute;
    }

    /**
     * Gets the selected period.
     *
     * @return the period.
     */
    public String getPeriod() {
        updateTimeInfo();
        return period;
    }

    /**
     * Gets the selected time as a formatted string.
     *
     * @return the time string
     */
    public String getTimeString() {
        updateTimeInfo();
        String hour = getHour();
        String minute = getMinute();

        return String.format(Locale.ENGLISH,
                "%s:%s:00.000",
                hour.length() == 1 ? "0" + hour : hour,
                minute.length() == 1 ? "0" + minute : minute);
    }

    /**
     * Scrolls the timescroller back to default values.
     */
    public void resetScroller() {
        ObjectAnimator hourAnimator = ObjectAnimator.ofInt(hourScrollView, "scrollY", hourScrollView.getScrollY(), 0).setDuration(1500);
        ObjectAnimator minuteAnimator = ObjectAnimator.ofInt(minuteScrollView, "scrollY", minuteScrollView.getScrollY(), 0).setDuration(1500);
        ObjectAnimator periodAnimator = ObjectAnimator.ofInt(periodScrollView, "scrollY", periodScrollView.getScrollY(), 0).setDuration(1000);
        hourAnimator.start();
        minuteAnimator.start();
        periodAnimator.start();
        updateTimeInfo();
    }

    /**
     * Scrolls the hour scrollview to the given value, with the given delay.
     *
     * @param hour  the hour to scroll to
     * @param delay the animation's start delay
     * @return the duration of the animation.
     */
    private long scrollHourTo(Integer hour, long delay) {
        // if the hour is 1, no scrolling needs to be done
        if (hour == 1) return 0;
        if (hour > 12) hour -= 13;
        else hour -= 1;
        int unitHeight = hourScrollView.getUnitHeight();
        ObjectAnimator objectAnimator = ObjectAnimator.ofInt(hourScrollView, "scrollY", 0, hour * unitHeight).setDuration(1500);
        objectAnimator.setStartDelay(delay);
        objectAnimator.start();

        return objectAnimator.getDuration();
    }

    /**
     * Scrolls the minute scrollview to the given value, with the given delay.
     *
     * @param minute the minute to scroll to
     * @param delay  the animation's start delay
     * @return the duration of the animation
     */
    private long scrollMinTo(Integer minute, long delay) {
        // if the minute is 0, no scrolling needs to be done
        if (minute == 0) return 0;
        int unitHeight = minuteScrollView.getUnitHeight();
        ObjectAnimator objectAnimator = ObjectAnimator.ofInt(minuteScrollView, "scrollY", 0, (minute / 5) * unitHeight).setDuration(1500);
        objectAnimator.setStartDelay(delay);
        objectAnimator.start();
        return objectAnimator.getDuration();
    }

    /**
     * Scrolls the period scrollview to the given value, with the given delay.
     *
     * @param period the period to scroll to
     * @param delay  the animation's start delay
     * @return the duration of the animation
     */
    private long scrollPeriodTo(String period, long delay) {
        // if the period is AM, no scrolling needs to be done
        if (period.equals("AM")) return 0;
        int unitHeight = periodScrollView.getUnitHeight();
        ObjectAnimator objectAnimator = ObjectAnimator.ofInt(periodScrollView, "scrollY", 0, unitHeight).setDuration(1000);
        objectAnimator.setStartDelay(delay);
        objectAnimator.start();
        return objectAnimator.getDuration();
    }

    /**
     * Scrolls all scrollviews to the given values, with the given start delay.
     * The scrollviews begin scrolling one after another, with the next one scrolling
     * once the previous one has finished.
     *
     * @param startDelay the delay before the scrolling begins
     * @param hour       the hours to scroll to
     * @param minute     the minute to scroll to
     * @param period     the period to scroll to
     * @return the total duration of the scrolling.
     */
    public long scrollAllTo(long startDelay, Integer hour, Integer minute, String period) {
        long delay = startDelay;
        delay += scrollHourTo(hour, startDelay);
        delay += scrollMinTo(minute, delay);
        delay += scrollPeriodTo(period, delay);
        return delay;
    }

    /**
     * Scrolls all scrollviews to the given values with the same start delay.
     *
     * @param startDelay the delay before all scrollviews begin scrolling
     * @param hour       the hour to scroll to
     * @param minute     the minute to scroll to
     * @param period     the period to scroll to
     */
    public void scrollAllAtOnce(long startDelay, Integer hour, Integer minute, String period) {
        scrollHourTo(hour, startDelay);
        scrollMinTo(minute, startDelay);
        scrollPeriodTo(period, startDelay);
    }

    /**
     * Compares the selected times between this timescroller and another timescroller.
     *
     * @param otherScroller the timescroller to compare to
     * @return the time comparison (-1 = before, 0 = the same, 1 = after)
     * @throws ParseException exception thrown if there is a problem parsing the time
     *                        in one of the timescrollers
     */
    public int compareTo(TimeScroller otherScroller) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.ENGLISH);
        String thisTime = getHour() + ":" + getMinute();
        String otherTime = otherScroller.getHour() + ":" + otherScroller.getMinute();

        Log.d("TimeScroller", "comparing " + thisTime + " and " + otherTime);

        int result = sdf.parse(thisTime).compareTo(sdf.parse(otherTime));

        Log.d("TimeScoller", "result: " + result);
        return result;
    }

}
