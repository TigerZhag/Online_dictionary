package com.readboy.mobile.dictionary.utils;

import android.view.View;

/**
 * Created by Senny on 2015/11/13.
 */
public class FrameRoateObject {

    private View target;
    private int roate;

    public int getRoate() {
        return roate;
    }

    public void setRoate(int roate) {
        this.roate = roate;
        if (target != null) {
            target.setRotation(roate);
        }
    }

    public FrameRoateObject(View target) {
        this.target = target;
    }

}
