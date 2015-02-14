package com.danielgek.tabsscroll;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

/**
 * Created by danielgek on 13/02/15.
 */
public class DraggerViewHolder extends FrameLayout {

    private final double AUTO_OPEN_SPEED_LIMIT = 800.0;
    private int mDraggingState = 0;
    private ViewPager viewPager;
    private ViewDragHelper mDragHelper;
    private int mDraggingBorder;
    private int mVerticalRange;
    private boolean mIsOpen;
    private String LOG_TAG = "TABSLIDING";

    public DraggerViewHolder(Context context, AttributeSet attrs) {
        super(context, attrs);
        mIsOpen = false;
        Log.d(LOG_TAG,"DraggerViewHolder contructor");
    }

    @Override
    protected void onFinishInflate() {
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        mDragHelper = ViewDragHelper.create(this, 1.0f, new DragHelperCallback());
        mIsOpen = false;
        super.onFinishInflate();
        Log.d(LOG_TAG,"DraggerViewHolder onFinishInflate");
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mVerticalRange = (int) (h * 0.70);
        super.onSizeChanged(w, h, oldw, oldh);
        Log.d(LOG_TAG,"DraggerViewHolder onSizeChanged");
    }

    private void onStopDraggingToClosed() {
        // To be implemented
    }

    private void onStartDragging() {

    }

    private boolean isViewPagerTarget(MotionEvent event) {
        int[] queenLocation = new int[2];
        viewPager.getLocationOnScreen(queenLocation);
        int upperLimit = queenLocation[1] + viewPager.getMeasuredHeight();
        int lowerLimit = queenLocation[1];
        int y = (int) event.getRawY();
        Log.d(LOG_TAG,"DraggerViewHolder isViewPagerTarget");
        return (y > lowerLimit && y < upperLimit);

    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (isViewPagerTarget(event) && mDragHelper.shouldInterceptTouchEvent(event)) {
            Log.d(LOG_TAG,"DraggerViewHolder onInterceptTouchEvent true");
            return true;
        } else {
            Log.d(LOG_TAG,"DraggerViewHolder onInterceptTouchEvent false");
            return false;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isViewPagerTarget(event) || isMoving()) {
            mDragHelper.processTouchEvent(event);
            Log.d(LOG_TAG,"DraggerViewHolder onTouchEvent true");
            return true;
        } else {
            Log.d(LOG_TAG,"DraggerViewHolder onTouchEvent super.onTouchEvent(event)");

            return super.onTouchEvent(event);
        }
    }

    @Override
    public void computeScroll() { // needed for automatic settling.
        if (mDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this);
            Log.d(LOG_TAG,"DraggerViewHolder computeScroll inside if");
        }
        Log.d(LOG_TAG,"DraggerViewHolder computeScroll outside if");
    }

    public boolean isMoving() {
        return (mDraggingState == ViewDragHelper.STATE_DRAGGING ||
                mDraggingState == ViewDragHelper.STATE_SETTLING);
    }

    public boolean isOpen() {
        return mIsOpen;
    }







    public class DragHelperCallback extends ViewDragHelper.Callback {
        @Override
        public void onViewDragStateChanged(int state) {
            Log.d(LOG_TAG,"DragHelperCallback onViewDragStateChanged");
            if (state == mDraggingState) { // no change
                return;
            }
            if ((mDraggingState == ViewDragHelper.STATE_DRAGGING || mDraggingState == ViewDragHelper.STATE_SETTLING) &&
                    state == ViewDragHelper.STATE_IDLE) {
                // the view stopped from moving.

                if (mDraggingBorder == 0) {
                    onStopDraggingToClosed();
                } else if (mDraggingBorder == mVerticalRange) {
                    mIsOpen = true;
                }
            }
            if (state == ViewDragHelper.STATE_DRAGGING) {
                onStartDragging();
            }
            mDraggingState = state;
        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            Log.d(LOG_TAG,"DragHelperCallback onViewDragStateChanged");

            mDraggingBorder = top;
        }

        public int getViewVerticalDragRange(View child) {
            Log.d(LOG_TAG,"DragHelperCallback getViewVerticalDragRange");

            return mVerticalRange;
        }

        @Override
        public boolean tryCaptureView(View view, int i) {
            Log.d(LOG_TAG,"DragHelperCallback tryCaptureView");

            return (view.getId() == R.id.viewpager);
        }

        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {
            Log.d(LOG_TAG,"DragHelperCallback clampViewPositionVertical");

            final int topBound = getPaddingTop();
            final int bottomBound = mVerticalRange;
            return Math.min(Math.max(top, topBound), bottomBound);
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            Log.d(LOG_TAG,"DragHelperCallback onViewReleased");

            final float rangeToCheck = mVerticalRange;
            if (mDraggingBorder == 0) {
                mIsOpen = false;
                return;
            }
            if (mDraggingBorder == rangeToCheck) {
                mIsOpen = true;
                return;
            }
            boolean settleToOpen = false;
            if (yvel > AUTO_OPEN_SPEED_LIMIT) { // speed has priority over position
                settleToOpen = true;
            } else if (yvel < -AUTO_OPEN_SPEED_LIMIT) {
                settleToOpen = false;
            } else if (mDraggingBorder > rangeToCheck / 2) {
                settleToOpen = true;
            } else if (mDraggingBorder < rangeToCheck / 2) {
                settleToOpen = false;
            }

            final int settleDestY = settleToOpen ? mVerticalRange : 0;

            if(mDragHelper.settleCapturedViewAt(0, settleDestY)) {
                ViewCompat.postInvalidateOnAnimation(DraggerViewHolder.this);
            }
        }
    }
}
