package com.readboy.mobile.dictionary.utils;

import android.view.View;

/**
 * Created by Senny on 2015/11/13.
 */
public class FrameAlphaObject {

    private View target;
    private float alpha;

    public float getAlpha() {
        return alpha;
    }

    public void setAlpha(float alpha) {
        this.alpha = alpha;
        if (target != null) {
            target.setAlpha(alpha);
        }
    }

    public FrameAlphaObject(View target) {
        this.target = target;
    }


}
