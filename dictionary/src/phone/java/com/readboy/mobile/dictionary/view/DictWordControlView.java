package com.readboy.mobile.dictionary.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Senny on 2015/10/30.
 */
public class DictWordControlView extends View {

    private boolean isBlnMoving;
    private float currentX;
    private float currentY;
    private int width;
    private int height;
    private View controlView;

    public void setControlView(View controlView) {
        this.controlView = controlView;
    }

    public DictWordControlView(Context context) {
        super(context);
    }

    public DictWordControlView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DictWordControlView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        width = right - left;
        height = bottom - top;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:
                currentX = event.getX();
                currentY = event.getY();
                break;

            case MotionEvent.ACTION_MOVE:
                if (currentX != event.getX() || currentY != currentY) {
                    isBlnMoving = true;
                }
                if (isBlnMoving) {
                    if (controlView != null) {
                        controlView.setVisibility(View.INVISIBLE);
                    }
                    int offsetX = (int) (event.getX() - currentX);
                    int offsetY = (int) (event.getY() - currentY);
                    setLeft(getLeft() + offsetX);
                    setTop(getTop() + offsetY);
                    setRight(getLeft() + offsetX + width);
                    setBottom(getTop() + offsetY + height);
                }
                break;

            case MotionEvent.ACTION_UP:
                if (!isBlnMoving) {
                    onClick();
                } else {
                    resetLocal();
                }
                currentX = 0.0f;
                currentY = 0.0f;
                isBlnMoving = false;
                break;

        }

        return super.onTouchEvent(event);
    }

    private void onClick() {

    }

    private void resetLocal() {
        if (((ViewGroup) getParent()).getWidth() / 2 > getLeft()) {
            setLeft(0);
            setTop(((ViewGroup) getParent()).getHeight() - height);
            setRight(width);
            setBottom(((ViewGroup) getParent()).getHeight());
            if (controlView != null) {
                int controlViewWidth = controlView.getWidth();
                int controlViewheight = controlView.getHeight();
                controlView.setLeft(width);
                controlView.setRight(controlView.getLeft() + controlViewWidth);
                controlView.setTop(getTop());
                controlView.setBottom(getTop() + controlViewheight);
                controlView.setVisibility(View.VISIBLE);
            }
        } else if (getLeft() > ((ViewGroup) getParent()).getWidth() / 2) {
            setLeft(((ViewGroup) getParent()).getWidth() - width);
            setTop(((ViewGroup) getParent()).getHeight() - height);
            setRight(((ViewGroup) getParent()).getWidth());
            setBottom(((ViewGroup) getParent()).getHeight());
            if (controlView != null) {
                int controlViewWidth = controlView.getWidth();
                int controlViewheight = controlView.getHeight();
                controlView.setLeft(0);
                controlView.setRight(controlView.getLeft() + controlViewWidth);
                controlView.setTop(getTop());
                controlView.setBottom(getTop() + controlViewheight);
                controlView.setVisibility(View.VISIBLE);
            }
        }
    }
}
