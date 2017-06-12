package io.bradenhart.shifty.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.ScrollView;

/**
 * A custom scrollview that listens to scrolling changes to know when
 * scrolling stops.
 *
 * @author bradenhart
 */
public class MyScrollView extends ScrollView {

    private int positionY = 0;
    private Runnable scrollRunnable;
    private int newCheck = 50;
    private int height;
    private int childCount;

    /**
     * Interface to listen for the scrollview's scrolling event ending.
     */
    interface OnScrollStoppedListener {
        void onScrollStopped();
    }

    private OnScrollStoppedListener onScrollStoppedListener;

    public MyScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);

        scrollRunnable = new Runnable() {

            public void run() {
                int newPosition = getScrollY();
                if (positionY - newPosition == 0) { //has stopped
                    if (onScrollStoppedListener != null) {

                        onScrollStoppedListener.onScrollStopped();
                    }
                } else {
                    positionY = getScrollY();
                    MyScrollView.this.postDelayed(scrollRunnable, newCheck);
                }
            }
        };
    }

    /**
     * Sets the scroll stopped listener for the scrollview.
     *
     * @param listener the listener
     */
    public void setOnScrollStoppedListener(MyScrollView.OnScrollStoppedListener listener) {
        onScrollStoppedListener = listener;
    }

    /**
     * Starts a task to check the scrollviews state.
     */
    public void startScrollerTask() {
        positionY = getScrollY();
        MyScrollView.this.postDelayed(scrollRunnable, newCheck);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        height = getChildAt(0).getMeasuredHeight();
        childCount = ((LinearLayout) getChildAt(0)).getChildCount();

    }

    /**
     * Gets the height of an item in the scrollview.
     *
     * @return the height of the scrollview item
     */
    public int getUnitHeight() {
        return height / childCount;
    }
}
