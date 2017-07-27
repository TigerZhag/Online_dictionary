package com.readboy.library.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Sgc on 2015/6/18.
 */
class PageText extends View{

    private int index;
    private ImlDrawText imlDrawText;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public ImlDrawText getImlDrawText() {
        return imlDrawText;
    }

    public void setImlDrawText(ImlDrawText imlDrawText) {
        this.imlDrawText = imlDrawText;
    }

    public PageText(Context context) {
        super(context);
    }

    public PageText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PageText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        ViewGroup.LayoutParams lp = getLayoutParams();
        if (lp != null) {
            super.onMeasure(MeasureSpec.makeMeasureSpec(lp.width, MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(lp.height, MeasureSpec.EXACTLY));
        } else {

            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (imlDrawText != null) {
            imlDrawText.draw(canvas, index);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        imlDrawText = null;
        super.onDetachedFromWindow();
    }
}
