package com.readboy.mobile.dictionary.utils;

import android.view.View;

/**
 * Created by Senny on 2015/11/13.
 */
public class FrameTranslationObject {

    private View target;
    private int translationX;
    private int fromX;
    private int toX;
    private OnFrameObjectListener listener;

    public int getTranslationX() {
        return translationX;
    }

    public void setTranslationX(int translationX) {
        this.translationX = translationX;
        if (target != null) {
            target.setTranslationX(translationX);
        }
        if (translationX == toX) {
            if (listener != null) {
                listener.onEnd();
            }
        }
    }

    public void setValue(int fromX, int toX) {
        this.fromX = fromX;
        this.toX = toX;
    }

    public View getTarget() {
        return target;
    }

    public int getFromX() {
        return fromX;
    }

    public int getToX() {
        return toX;
    }

    public void setOnFrameObjectListener(OnFrameObjectListener listener) {
        this.listener = listener;
    }

    public FrameTranslationObject(View target) {
        this.target = target;
    }

}
