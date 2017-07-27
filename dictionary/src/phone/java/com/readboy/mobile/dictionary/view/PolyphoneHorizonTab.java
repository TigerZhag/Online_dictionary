package com.readboy.mobile.dictionary.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.util.AttributeSet;
import android.view.View;

import com.sen.lib.view.HorizontalItemTab;
import com.sen.lib.view.LinearGroup;

/**
 * Created by Senny on 2015/11/9.
 */
public class PolyphoneHorizonTab extends HorizontalItemTab {

    private int itemBackgroundID;
//    private Bitmap itemBackgroundBitmap;
    private Drawable itemBackgroundDrawable;
    private int tabIndex;
    private float positionPercent;
    private int backgroundWidth;
    private int itemTabHeight;
    private Rect targetRect;
    private Paint mTabPaint;

    public void setItemBackground(int id) {
        itemBackgroundID = id;
    }

    public void setItemBackgroundWidth(int width) {
        backgroundWidth = width;
    }

    public void setItemTabColor(int color) {
        if (mTabPaint != null) {
            mTabPaint.setColor(color);
        }
    }

    public void setItemTabHeight(int height) {
        itemTabHeight = height;
    }

    public PolyphoneHorizonTab(Context context) {
        super(context);
    }

    public PolyphoneHorizonTab(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PolyphoneHorizonTab(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mTabPaint = new Paint();
    }

    @Override
    public void scrollTab(float positionPercent, int tabIndex) {
        super.scrollTab(positionPercent, tabIndex);
        this.tabIndex = tabIndex--;
        this.positionPercent = positionPercent;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (itemBackgroundDrawable == null && itemBackgroundID != 0) {
            /*BitmapDrawable bitmapDrawable = (BitmapDrawable) getResources().getDrawable(itemBackgroundID);
            if (bitmapDrawable != null) {
                itemBackgroundBitmap = bitmapDrawable.getBitmap();
            }*/
            itemBackgroundDrawable = getResources().getDrawable(itemBackgroundID);
        }
        if (itemBackgroundDrawable != null) {
            LinearGroup mLinearGroup = (LinearGroup) this.getItemGroup();
            if (mLinearGroup != null && mLinearGroup.getCacheLocalArray().size() > this.tabIndex && this.tabIndex >= 0) {
                View currentItem = this.getItemView(this.tabIndex);
                if (currentItem != null) {
                    float currentLeft = (float) currentItem.getLeft();
                    float currentRight = (float) currentItem.getRight();
                    int currentItemWidth = (int) (currentRight - currentLeft);
                    if (currentItemWidth > backgroundWidth) {
                        int nextItem = (currentItemWidth - backgroundWidth) / 2;
                        currentLeft += (float) nextItem;
                        currentRight -= (float) nextItem;
                    }

                    if (mLinearGroup.getCacheLocalArray().size() > this.tabIndex + 1) {
                        View nextItem1 = this.getItemView(this.tabIndex + 1);
                        if (nextItem1 != null) {
                            float nextLeft = (float) nextItem1.getLeft();
                            float nextRight = (float) nextItem1.getRight();
                            int nextItemWidth = (int) (nextRight - nextLeft);
                            if (nextItemWidth > backgroundWidth) {
                                int nextMargin = (nextItemWidth - backgroundWidth) / 2;
                                nextLeft += (float) nextMargin;
                                nextRight -= (float) nextMargin;
                            }

                            currentLeft += (nextLeft - currentLeft) * this.positionPercent;
                            currentRight += (nextRight - currentRight) * this.positionPercent;
                        }
                    }

                    if (targetRect == null) {
                        targetRect = new Rect((int) currentLeft, 2, (int) currentRight, getHeight());
                    } else {
                        targetRect.set((int) currentLeft, 2, (int) currentRight, getHeight());
                    }
//                    canvas.drawBitmap(itemBackgroundBitmap, null, targetRect, null);
                    if (itemBackgroundDrawable instanceof NinePatchDrawable) {
                        Drawable drawable = ((NinePatchDrawable) itemBackgroundDrawable).mutate();
                        drawable.setBounds(targetRect);
                        drawable.draw(canvas);
                    } else {
                        itemBackgroundDrawable.draw(canvas);
                    }

                    canvas.drawRect(currentLeft + 10, (float) (this.getHeight() - itemTabHeight), currentRight - 10, (float) this.getHeight(), mTabPaint);
                }
            }
        }
        super.onDraw(canvas);
    }
}
