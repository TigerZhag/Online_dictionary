package com.readboy.mobile.dictionary.view;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.readboy.Dictionary.R;
import com.readboy.mobile.dictionary.utils.FrameTranslationObject;

/**
 * Created by Senny on 2015/11/9.
 */
public class HorizontalButtonGroup extends ViewGroup {

    private int totalChildWidth;
    private int spaceWidth;
    private boolean isShowed;

    private int childWidth;
    private int childHeight;

    public int getSpaceWidth() {
        return spaceWidth;
    }

    public HorizontalButtonGroup(Context context) {
        super(context);
    }

    public HorizontalButtonGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public HorizontalButtonGroup(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    private void init(Context context) {
        childWidth = context.getResources().getDimensionPixelSize(R.dimen.control_button_width);
        childHeight = context.getResources().getDimensionPixelSize(R.dimen.control_button_height);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        totalChildWidth = 0;
        int height = 0;
        for (int i = 0;i < getChildCount();i++) {
            View child = getChildAt(i);
            child.measure(MeasureSpec.makeMeasureSpec(childWidth, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(childHeight, MeasureSpec.EXACTLY));
            if (child.getMeasuredHeight() > height) {
                height = child.getMeasuredHeight();
            }
            totalChildWidth += child.getMeasuredWidth();
        }
        if (height != 0) {
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.AT_MOST);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        int width = r - l;
        if (getChildCount() > 1) {
            spaceWidth = (width - totalChildWidth) / (getChildCount() - 1);
            int x = 0;
            for (int i = 0;i < getChildCount();i++) {
                View child = getChildAt(i);
                child.measure(MeasureSpec.makeMeasureSpec(childWidth, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(childHeight, MeasureSpec.EXACTLY));
                child.layout(x, 0, x + child.getMeasuredWidth(), child.getMeasuredHeight());
                x += child.getMeasuredWidth() + spaceWidth;
            }
        }

    }

    public boolean isShowing () {
        return isShowed;
    }

    public void showAnimation() {
        isShowed = true;
        showTransilate();
    }

    public void hideAnimation() {
        isShowed = false;
        hideTransilate();
    }

    private void showTransilate() {
        for (int i = 0;i < getChildCount() - 1;i++) {
            View childView = getChildAt(i);
            FrameTranslationObject translationObject = new FrameTranslationObject(childView);
            translationObject.setValue(getChildAt(getChildCount() - 1).getLeft() - childView.getLeft(), 0);
            ObjectAnimator objectAnimator = ObjectAnimator.ofInt(translationObject, "translationX", translationObject.getFromX(), translationObject.getToX());
            objectAnimator.setDuration((3 - i) * 100);
            if (childView.getTag() instanceof ObjectAnimator) {
                ((ObjectAnimator) childView.getTag()).cancel();
            }
            childView.setVisibility(View.VISIBLE);
            objectAnimator.start();
        }
    }

    private void hideTransilate() {
        for (int i = 0;i < getChildCount() - 1;i++) {
            final View childView = getChildAt(i);
            FrameTranslationObject translationObject = new FrameTranslationObject(childView);
            translationObject.setValue(0, getChildAt(getChildCount() - 1).getLeft() - childView.getLeft());
            ObjectAnimator objectAnimator = ObjectAnimator.ofInt(translationObject, "translationX", translationObject.getFromX(), translationObject.getToX());
            objectAnimator.setDuration((3 - i) * 100);
            objectAnimator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    childView.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            childView.setTag(objectAnimator);
            objectAnimator.start();
        }
    }
}
