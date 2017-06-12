package io.bradenhart.shifty.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.ScrollView;

/**
 * @author bradenhart
 */

public class MyScrollView extends ScrollView {

    private int positionY = 0;
    private Runnable scrollRunnable;
    private int newCheck = 50;
    private int height;
    private int childCount;

    interface OnScrollStoppedListener{
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

    public void setOnScrollStoppedListener(MyScrollView.OnScrollStoppedListener listener){
        onScrollStoppedListener = listener;
    }

    public void startScrollerTask(){
        positionY = getScrollY();
        MyScrollView.this.postDelayed(scrollRunnable, newCheck);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        height = getChildAt(0).getMeasuredHeight();
        childCount = ((LinearLayout) getChildAt(0)).getChildCount();

    }

    public int getUnitHeight() {
        return height / childCount;
    }
}
