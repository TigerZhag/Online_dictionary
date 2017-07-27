package com.readboy.mobile.dictionary.view;

import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/**
 * Created by 123 on 2016/8/9.
 */

public class LocalPanLinearLayout extends LinearLayout {

    private int originLeft;
    private int originTop;
    private int originRight;
    private int originBottom;

    public LocalPanLinearLayout(Context context) {
        super(context);
    }

    public LocalPanLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LocalPanLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (Math.abs(r - l) > Math.abs(originRight - originLeft) ||
                Math.abs(b - t) > Math.abs(originBottom - originTop)) {
            super.onLayout(changed, l, t, r, b);
            originLeft = l;
            originRight = r;
            originTop = t;
            originBottom = b;
        } else {
            super.onLayout(false, originLeft, originTop, originRight, originBottom);
        }
    }
}
