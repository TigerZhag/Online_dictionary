package com.readboy.mobile.dictionary.view;

import android.content.Context;
import android.graphics.PointF;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewConfigurationCompat;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import com.sen.lib.support.CustomViewPager;

/**
 * Created by Senny on 2015/10/29.
 */
public class NestedParentViewPager extends CustomViewPager {

    private static final String TAG = "NestedParentViewPager";

    private static final int INVALID_POINTER = -1;
    private int mActivePointerId = INVALID_POINTER;

    private float mLastMotionX;
    private float mInitialMotionY;
    private int mTouchSlop;

    private float currentScrollState;
    private NestedOnPagerListener nestedOnPagerListener;

    public NestedParentViewPager(Context context) {
        super(context);
    }

    public NestedParentViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        final ViewConfiguration configuration = ViewConfiguration.get(getContext());
        mTouchSlop = ViewConfigurationCompat.getScaledPagingTouchSlop(configuration);
        removeOnPageChangeListener(nestedOnPagerListener);
        nestedOnPagerListener = new NestedOnPagerListener();
        addOnPageChangeListener(nestedOnPagerListener);
    }

    @Override
    public void removeAllViews() {
        super.removeAllViews();
        removeOnPageChangeListener(nestedOnPagerListener);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        if (ev != null) {
            if (ev.getPointerCount() > 1) {
                ev.setAction(MotionEvent.ACTION_UP);
            }
        }

        boolean result = false;
        try {
            result = super.dispatchTouchEvent(ev);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        final int actionIndex = ev.getActionIndex();
        float x = ev.getX(actionIndex);
        float y = ev.getY(actionIndex);
        final boolean customOrder = isChildrenDrawingOrderEnabled();
        for (int i = getChildCount() - 1; i >= 0; i--) {
            final int childIndex = customOrder ?
                    getChildDrawingOrder(getChildCount(), i) : i;
            final View child = getChildAt(childIndex);
            if (isPointInView(x, y, child, null)) {
                MotionEvent event = MotionEvent.obtain(ev);
                child.dispatchTouchEvent(event);
            }
        }
        return result;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        super.onInterceptTouchEvent(ev);
        /*if (isScrollAble()) {
            return true;
        } else {
            return false;
        }*/
        boolean result = false;
        switch (ev.getAction()) {

            case MotionEvent.ACTION_DOWN:
                mActivePointerId = MotionEventCompat.getPointerId(ev, 0);
                mLastMotionX = ev.getX();
                mInitialMotionY = ev.getY();
                if (currentScrollState == ViewPager.SCROLL_STATE_DRAGGING) {
                    result = true;
                }
                break;

            case MotionEvent.ACTION_MOVE:
                if (mActivePointerId != INVALID_POINTER) {
                    if (ev.findPointerIndex(mActivePointerId) != -1) {
                        final int activePointerId = mActivePointerId;
                        final int pointerIndex = MotionEventCompat.findPointerIndex(ev, activePointerId);
                        final float x = MotionEventCompat.getX(ev, pointerIndex);
                        final float dx = x - mLastMotionX;
                        final float xDiff = Math.abs(dx);
                        final float y = MotionEventCompat.getY(ev, pointerIndex);
                        final float yDiff = Math.abs(y - mInitialMotionY);

                        if (xDiff > mTouchSlop && xDiff * 0.5f > yDiff) {
                            result = true;
                        }
                    }
                }
                break;

            case MotionEvent.ACTION_UP:

                mActivePointerId = INVALID_POINTER;
                mLastMotionX = 0.0f;
                mInitialMotionY = 0.0f;
                break;

            case MotionEvent.ACTION_CANCEL:

                mActivePointerId = INVALID_POINTER;
                mLastMotionX = 0.0f;
                mInitialMotionY = 0.0f;
                break;
        }

        if (!isScrollAble()) {
            return false;
        }
        return result;
    }

    protected boolean isPointInView(float x, float y, View child,
                                                    PointF outLocalPoint) {
        float localX = x + getScrollX() - child.getLeft();
        float localY = y + getScrollY() - child.getTop();
        return localX >= 0 && localX < (child.getRight() - child.getLeft())
                && localY >= 0 && localY < (child.getBottom() - child.getTop());
    }

    private class NestedOnPagerListener implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {

        }

        @Override
        public void onPageScrollStateChanged(int state) {
            currentScrollState = state;
        }
    }

}
