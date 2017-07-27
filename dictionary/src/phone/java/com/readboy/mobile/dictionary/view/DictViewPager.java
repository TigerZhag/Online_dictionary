package com.readboy.mobile.dictionary.view;

import android.content.Context;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.readboy.Dictionary.R;
import com.readboy.mobile.dictionary.fragment.DictAnimationWordContentFragment;
import com.readboy.mobile.dictionary.fragment.DictStrokeWordFragment;
import com.readboy.mobile.dictionary.fragment.DictWordContentFragment;
import com.readboy.mobile.dictionary.utils.IWordContentGetter;
import com.sen.lib.support.CustomViewPager;

/**
 * Created by 123 on 2016/9/6.
 */

public class DictViewPager extends NestedParentViewPager {

    public DictViewPager(Context context) {
        super(context);
    }

    public DictViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            if (isSelectedState(ev.getX(), ev.getY())) {
                return false;
            }
        } else if (ev.getAction() == MotionEvent.ACTION_UP ||
                ev.getAction() == MotionEvent.ACTION_CANCEL) {
            if (isSelectedState(ev.getX(), ev.getY())) {
                return false;
            }
        }
        return super.onTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (isSelectedState(ev.getX(), ev.getY())) {
            return false;
        }
        return super.onInterceptTouchEvent(ev);
    }

    private boolean isSelectedState(float x, float y) {
        for (int i = 0 ; getChildCount() > i ; i ++) {
            View child = getChildAt(i);
            if (isPointInView(x, y, child)) {
                if (child.getTag() instanceof IWordContentGetter) {
                    if (((IWordContentGetter) child.getTag()).isSelectedText()) {
                        return true;
                    }
                }
                break;
            }
        }
        return false;
    }

    private boolean isPointInView(float x, float y, View child) {
        float localX = x + getScrollX() - child.getLeft();
        float localY = y + getScrollY() - child.getTop();
        return localX >= 0 && (child.getRight() - child.getLeft() >= localX)
                && localY >= 0 && (child.getBottom() - child.getTop() >= localY);
    }
//    1116.4663 1001.32544
}
