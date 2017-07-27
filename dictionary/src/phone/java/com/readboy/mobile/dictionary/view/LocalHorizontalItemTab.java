package com.readboy.mobile.dictionary.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.readboy.Dictionary.R;
import com.readboy.mobile.dictionary.AppConfigInfo;
import com.sen.lib.view.HorizontalItemTab;
import com.sen.lib.view.LinearGroup;

/**
 * Created by 123 on 2016/8/5.
 */

public class LocalHorizontalItemTab extends HorizontalItemTab {

    private Paint mPaint;
    private float strokeWidth = 2.0f * AppConfigInfo.RATIO_DENSITY;;
    private float radius = 14.0f * AppConfigInfo.RATIO_DENSITY;
    private float width;
    private float height;
    private RectF rectF;
    private int color;
    private int currentItemIndex;  //default 0
    private int tabIndex;
    private float positionPercent;

    private void setColor(int color) {
        this.color = color;
    }

    public LocalHorizontalItemTab(Context context) {
        super(context);
    }

    public LocalHorizontalItemTab(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LocalHorizontalItemTab(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.localTab);
        color = ta.getColor(R.styleable.localTab_tabColor, Color.WHITE);
        ta.recycle();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (mPaint == null) {
            mPaint = new Paint();
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setStrokeWidth(strokeWidth);
            mPaint.setAntiAlias(true);
            mPaint.setDither(true);
        }
        if (rectF == null) {
            rectF = new RectF();
        }
        width = r - l + 0.0f;
        height = b - t + 0.0f;

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        super.onPageScrolled(position, positionOffset, positionOffsetPixels);
        if (currentItemIndex == -1) {
            View view = getItemView(0);
            if (view != null) {
                currentItemIndex = 0;
                view.setSelected(true);
            }
        }
    }

    @Override
    public void onPageSelected(int position) {
        View lastView = getItemView(currentItemIndex);
        if (lastView != null) {
            lastView.setSelected(false);
        }
        View currentView = getItemView(position);
        if (currentView != null) {
            currentView.setSelected(true);
        }
        super.onPageSelected(position);
        currentItemIndex = position;
    }

    @Override
    public void scrollTab(float positionPercent, int tabIndex) {
        super.scrollTab(positionPercent, tabIndex);
        this.tabIndex = tabIndex--;
        this.positionPercent = positionPercent;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        View view = getItemView(currentItemIndex);
        if (view != null && !view.isSelected()) {
            view.setSelected(true);
        }
        if (mPaint != null && rectF != null) {
            mPaint.setColor(color);

            float translateX = getScrollX() + 0.0f;
            /*float dimeter = radius * 2;

            //left top radius
            rectF.set(0.0f, 0.0f, dimeter, dimeter);
            canvas.drawArc(rectF, 180, 90, false, mPaint);

            //left line
            canvas.drawLine(0.0f, radius, 0.0f, height - radius, mPaint);

            //left bottom radius
            rectF.set(0.0f, height - dimeter, dimeter, height);
            canvas.drawArc(rectF, 90, 90, false, mPaint);

            //top line
            canvas.drawLine(radius, 0.0f, width - radius, 0.0f, mPaint);

            //right top radius
            rectF.set(width - dimeter, 0.0f, width,dimeter);
            canvas.drawArc(rectF, 270, 90, false, mPaint);

            //right line
            canvas.drawLine(width, radius, width, height - radius, mPaint);

            //right bottom radius
            rectF.set(width - dimeter, height - dimeter, width, height);
            canvas.drawArc(rectF, 0, 90, false, mPaint);

            //bottom line
            canvas.drawLine(radius, height, width - radius, height, mPaint);*/


            float offset = strokeWidth / 2;
            rectF.set(translateX + offset, 0.0f + offset, translateX + width - offset, height - offset);
            mPaint.setStyle(Paint.Style.STROKE);
            canvas.drawRoundRect(rectF, radius, radius, mPaint);

            int childCount = getAdpater().getCount();
            for (int i = 0 ; childCount > i ; i++) {
                View child = getItemView(i);
                if (child != null) {
                    if (child.getRight() > getRight()) {
                        childCount = i;
                        break;
                    }
                }
            }
            float childWidth = width / childCount;
            //separate line
            for (int i = 1 ; childCount > i ; i++) {
                canvas.drawLine(translateX + childWidth * i, 0.0f + offset, translateX + childWidth * i, height - offset, mPaint);
            }

            LinearGroup mLinearGroup = (LinearGroup) this.getItemGroup();
            if (mLinearGroup != null && mLinearGroup.getCacheLocalArray().size() > this.currentItemIndex && this.currentItemIndex >= 0) {
                View currentItem = this.getItemView(this.currentItemIndex);
                if (currentItem != null) {
                    float currentLeft = currentItem.getLeft() + 0.0f;
                    float currentRight = currentItem.getRight() + 0.0f;
                    float currentItemWidth = currentRight - currentLeft;
                    if (currentItemWidth > childWidth) {
                        float nextItem = (currentItemWidth - childWidth) / 2;
                        currentLeft += nextItem;
                        currentRight -= nextItem;
                    }

                    /*if (mLinearGroup.getCacheLocalArray().size() > this.currentItem + 1) {
                        View nextItem1 = this.getItemView(this.currentItem + 1);
                        if (nextItem1 != null) {
                            float nextLeft = nextItem1.getLeft() + 0.0f;
                            float nextRight = nextItem1.getRight() + 0.0f;
                            int nextItemWidth = (int) (nextRight - nextLeft);
                            if (nextItemWidth > childWidth) {
                                float nextMargin = (nextItemWidth - childWidth) / 2;
                                nextLeft += nextMargin;
                                nextRight -= nextMargin;
                            }

                            currentLeft += (nextLeft - currentLeft) * this.positionPercent;
                            currentRight += (nextRight - currentRight) * this.positionPercent;
                        }
                    }*/
                    mPaint.setStyle(Paint.Style.FILL);
                    rectF.set(currentLeft, 0.0f + offset, currentRight, height - offset);
                    canvas.drawRoundRect(rectF, radius, radius, mPaint);
                    if (getAdpater().getCount() == 1) {
                        return;
                    }
                    if (currentLeft == translateX) {
                        rectF.set(currentRight - radius , 0.0f + offset, currentRight, height - offset);
                        canvas.drawRect(rectF, mPaint);
                    } else if (currentItemWidth > Math.abs((translateX + width) - currentRight)) {
                        rectF.set(currentLeft, 0.0f + offset, currentLeft + radius, height - offset);
                        canvas.drawRect(rectF, mPaint);
                    } else {
                        rectF.set(currentRight - radius , 0.0f + offset, currentRight, height - offset);
                        canvas.drawRect(rectF, mPaint);
                        rectF.set(currentLeft, 0.0f + offset, currentLeft + radius, height - offset);
                        canvas.drawRect(rectF, mPaint);
                    }
                }
            }

//            int index = currentItem % (childCount - 1);
//            //fill item background
//            mPaint.setStyle(Paint.Style.FILL);
//            rectF.set(translateX + childWidth * index, 0.0f, translateX + childWidth * (index + 1), height);
//            canvas.drawRoundRect(rectF, radius, radius, mPaint);
//
//
//            if (index == 0) {
//                rectF.set(translateX + childWidth - radius , 0.0f, translateX + childWidth, height);
//                canvas.drawRect(rectF, mPaint);
//            } else if (index == (childCount - 1)) {
//                rectF.set(translateX + childWidth * index, 0.0f, translateX + childWidth * index + radius, height);
//                canvas.drawRect(rectF, mPaint);
//            } else {
//                rectF.set(translateX + childWidth * index, 0.0f,translateX +  childWidth * index + radius, height);
//                canvas.drawRect(rectF, mPaint);
//                rectF.set(translateX + childWidth * (index + 1) - radius, 0.0f, translateX + childWidth * (index + 1), height);
//                canvas.drawRect(rectF, mPaint);
//            }
        }
    }
}
