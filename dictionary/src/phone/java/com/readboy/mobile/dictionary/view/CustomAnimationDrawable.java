package com.readboy.mobile.dictionary.view;

import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;

/**
 * Created by Senny on 2015/11/9.
 */
public class CustomAnimationDrawable extends AnimationDrawable {

    private int drawableCounts = 0;
    private int currentFrame = -1;
    private OnEndCustomAnimatoinListener listener;

    public void setOnEndCustomAnimatoinListener(OnEndCustomAnimatoinListener listener) {
        this.listener = listener;
    }

    public CustomAnimationDrawable(AnimationDrawable drawable) {
        /* Add each frame to our animation drawable */
        drawableCounts = drawable.getNumberOfFrames();
        for (int i = 0; i < drawableCounts; i++) {
            this.addFrame(drawable.getFrame(i), drawable.getDuration(i));
        }
    }

    private void nextFrame() {
        int next = currentFrame+1;
        final int N = drawableCounts;
        if (next >= N) {
            next = 0;
        }
        currentFrame = next;
    }

    @Override
    public void unscheduleSelf(Runnable what) {
        super.unscheduleSelf(what);
    }


    @Override
    public void run() {
        nextFrame();
        super.run();
        if (currentFrame == drawableCounts - 1) {
            if (listener != null) {
                listener.endFrame();
            }
            currentFrame = -1;
        }
    }

    public interface OnEndCustomAnimatoinListener {
        void endFrame();
    }
}
