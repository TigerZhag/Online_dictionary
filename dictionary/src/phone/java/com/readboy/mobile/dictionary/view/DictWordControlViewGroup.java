package com.readboy.mobile.dictionary.view;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.PointF;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.readboy.Dictionary.R;
import com.readboy.mobile.dictionary.utils.BaseHandler;
import com.readboy.mobile.dictionary.utils.FrameAlphaObject;
import com.readboy.mobile.dictionary.utils.FrameRoateObject;
import com.readboy.mobile.dictionary.utils.FrameTranslationObject;

/**
 * Created by Senny on 2015/11/3.
 */
public class DictWordControlViewGroup extends ViewGroup implements View.OnClickListener {

    private static final int DEFAULT_DALAY_MILLIES = 200;

    private View openButton;
    private HorizontalButtonGroup buttonGroup;
    private boolean isInOpenButtonPointer;
    private boolean isBlnMoved;

    private float currentX;
    private float currentY;
    private OnWordControlButtonListener listener;

    private int spaceLeft = 38;
    private int spaceTop;
    private int spaceRight = 38;
    private int spaceBottom = 32;
    private FrameRoateObject frameRoateObject;
    private FrameAlphaObject frameAlphaObject;
    private FrameTranslationObject translationObject;
    private ObjectAnimator openButtonAnimator;
    private ObjectAnimator viewGroupAlphaAnimator;
    private ObjectAnimator buttonGroupAnimator;
    private int translationXFrom;
    private int translationXTo;

    private ControlHandler controlHandler = new ControlHandler(this);

    public void setOnWordControlButtonListener(OnWordControlButtonListener listener) {
        this.listener = listener;
    }

    public DictWordControlViewGroup(Context context) {
        super(context);
    }

    public DictWordControlViewGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DictWordControlViewGroup(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int width = r - l;
        int height = b - t;

        if (buttonGroup == null) {
            buttonGroup = (HorizontalButtonGroup) inflate(getContext(), R.layout.layout_contronler_buttons, null);
            translationObject = new FrameTranslationObject(buttonGroup);
            if (indexOfChild(buttonGroup) == -1) {
                addView(buttonGroup);
            }
        }
        if (openButton == null) {
            openButton = inflate(getContext(), R.layout.layout_button_open, null);
            frameRoateObject = new FrameRoateObject(openButton.findViewById(R.id.button_open_float_cross));
            frameAlphaObject = new FrameAlphaObject(this);
            viewGroupAlphaAnimator = ObjectAnimator.ofFloat(frameAlphaObject, "alpha", 1.0f, 0.6f);
            viewGroupAlphaAnimator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {

                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    setAlpha(1.0f);
                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            post(alphaRunnable);

            if (indexOfChild(openButton) == -1) {
                addView(openButton);
            }
        }
        openButton.measure(width, height);
        buttonGroup.measure(MeasureSpec.makeMeasureSpec(width - spaceLeft - spaceRight, MeasureSpec.EXACTLY), height);
        openButton.layout(width - openButton.getMeasuredWidth() - spaceRight,
                height - openButton.getMeasuredHeight() - spaceBottom, width - spaceRight, height - spaceBottom);
        buttonGroup.layout(spaceLeft, height - buttonGroup.getMeasuredHeight() - spaceBottom, spaceLeft + buttonGroup.getMeasuredWidth(), height - spaceBottom);
//        buttonGroup.setVisibility(View.INVISIBLE);
        buttonGroup.findViewById(R.id.fragment_search_button_enlarge_size).setOnClickListener(this);
        buttonGroup.findViewById(R.id.fragment_search_button_reduce_size).setOnClickListener(this);
        buttonGroup.findViewById(R.id.fragment_search_button_select_word).setOnClickListener(this);
    }

    private Runnable alphaRunnable = new Runnable() {
        @Override
        public void run() {
            if (!buttonGroup.isShowing() && getAlpha() == 1.0f) {
                viewGroupAlphaAnimator.start();
            }
            postDelayed(alphaRunnable, 1 * 1000);
        }
    };

    @Override
    protected void onDetachedFromWindow() {
        if (openButton != null && alphaRunnable != null) {
            openButton.removeCallbacks(alphaRunnable);
        }
        if (controlHandler != null) {
            controlHandler.removeCallbacksAndMessages(null);
        }
        controlHandler = null;
        super.onDetachedFromWindow();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        boolean result = false;
        boolean moveFlag = true;
        switch (ev.getAction()) {

            case MotionEvent.ACTION_DOWN:

                if (openButton != null) {
                    if (isPointInView(ev.getX(), ev.getY(), openButton, null)) {
                        MotionEvent event = MotionEvent.obtain(ev);
                        event.setLocation(event.getX() - openButton.getLeft(), event.getY() - openButton.getTop());
                        openButton.dispatchTouchEvent(event);
                        isInOpenButtonPointer = true;
                        currentX = ev.getX();
                        currentY = ev.getY();
                        result = true;
                    }
                }
                if (!isInOpenButtonPointer && (buttonGroup.isShowing()
                        && !isPointInView(ev.getX(), ev.getY(), buttonGroup, null))) {
                    hideOpenButtonClick();
                    return true;
                }
                break;

            case MotionEvent.ACTION_MOVE:
                if (openButton != null) {
                    if (isInOpenButtonPointer) {
                        if (currentX != ev.getX() || currentY != ev.getY()) {
//                            isBlnMoved = true;
                        }
                        if (isBlnMoved) {
                            if (buttonGroup != null && buttonGroup.isShowing()) {
                                moveFlag = false;
                            }
                            if (moveFlag) {
                                int offsetX = (int) (ev.getX() - currentX);
                                int offsetY = (int) (ev.getY() - currentY);
                                openButton.setLeft(openButton.getLeft() + offsetX);
                                openButton.setTop(openButton.getTop() + offsetY);
                                openButton.setRight(openButton.getRight() + offsetX);
                                openButton.setBottom(openButton.getBottom() + offsetY);
                            }
                        }
                        currentX = ev.getX();
                        currentY = ev.getY();
                        result = true;
                    }
                }

                break;

            case MotionEvent.ACTION_UP:

                if (isInOpenButtonPointer) {
                    MotionEvent event = MotionEvent.obtain(ev);
                    event.setLocation(event.getX() - openButton.getLeft(), event.getY() - openButton.getTop());
                    openButton.dispatchTouchEvent(event);
                    setAlpha(1.0f);
                    viewGroupAlphaAnimator.cancel();
                    if (isBlnMoved) {
                        if (moveFlag) {
                            resizeOpenButtonLocal();
                        }
                    } else {
                        showOpenButtonClick();
                    }
                    result = true;
                }

                currentX = 0.0f;
                currentY = 0.0f;
                isInOpenButtonPointer = false;
                isBlnMoved = false;
                break;

            case MotionEvent.ACTION_CANCEL:

                if (isInOpenButtonPointer) {
                    MotionEvent event = MotionEvent.obtain(ev);
                    event.setLocation(event.getX() - openButton.getLeft(), event.getY() - openButton.getTop());
                    openButton.dispatchTouchEvent(event);
                    if (isBlnMoved) {
                        if (moveFlag) {
                            resizeOpenButtonLocal();
                        }
                    } else {
                        showOpenButtonClick();
                    }
                    result = true;
                }

                currentX = 0.0f;
                currentY = 0.0f;
                isInOpenButtonPointer = false;
                isBlnMoved = false;
                break;

        }

        if (buttonGroup.isShowing()) {
            MotionEvent event = MotionEvent.obtain(ev);
            event.setLocation(event.getX() - buttonGroup.getLeft(), event.getY() - buttonGroup.getTop());
            buttonGroup.dispatchTouchEvent(event);
        }

        if (result || buttonGroup.isShowing()) {
            return true;
        }

        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fragment_search_button_enlarge_size:
                /*if (controlHandler != null) {
                    controlHandler.sendMessages(R.id.fragment_search_button_enlarge_size, null, 0, 0, DEFAULT_DALAY_MILLIES);
                }*/
                sendMessages(R.id.fragment_search_button_enlarge_size);
                break;

            case R.id.fragment_search_button_reduce_size:
                /*if (controlHandler != null) {
                    controlHandler.sendMessages(R.id.fragment_search_button_reduce_size, null, 0, 0, DEFAULT_DALAY_MILLIES);
                }*/
                sendMessages(R.id.fragment_search_button_reduce_size);
                break;

            case R.id.fragment_search_button_select_word:
                /*if (controlHandler != null) {
                    controlHandler.sendMessages(R.id.fragment_search_button_select_word, null, 0, 0, DEFAULT_DALAY_MILLIES);
                }*/
                sendMessages(R.id.fragment_search_button_select_word);
                break;
        }
    }

    private void sendMessages(int what) {
        if (controlHandler != null) {
            controlHandler.removeCallbacksAndMessages(null);
            controlHandler.sendMessages(what, null, 0, 0, DEFAULT_DALAY_MILLIES);
        }
    }

    private void showOpenButtonClick() {
        if (buttonGroup != null && openButton != null) {
            if (!buttonGroup.isShowing()) {
                if (listener != null) {
                    listener.onShow(buttonGroup.findViewById(R.id.fragment_search_button_enlarge_size),
                            buttonGroup.findViewById(R.id.fragment_search_button_reduce_size),
                            buttonGroup.findViewById(R.id.fragment_search_button_select_word));
                }
                if (openButton.getLeft() == spaceLeft) {
                    int buttonGroupWidth = buttonGroup.getWidth();
                    buttonGroup.setLeft(openButton.getWidth() + buttonGroup.getSpaceWidth() + spaceLeft);
                    buttonGroup.setRight(openButton.getWidth() + buttonGroup.getSpaceWidth() + buttonGroupWidth + spaceLeft);
                    translationXFrom = buttonGroupWidth;
                    translationXTo = 0;
                } else {
                    int buttonGroupWidth = buttonGroup.getWidth();
                    buttonGroup.setLeft(getWidth() - buttonGroupWidth - spaceRight);
                    buttonGroup.setRight(getWidth() - spaceRight);
                    translationXFrom = -buttonGroupWidth;
                    translationXTo = 0;
                }
                buttonGroup.setVisibility(View.VISIBLE);
                if (openButtonAnimator != null) {
                    openButtonAnimator.cancel();
                }
                openButtonAnimator = ObjectAnimator.ofInt(frameRoateObject, "roate", 0, -225);
                openButtonAnimator.start();

                /*if (buttonGroupAnimator != null) {
                    buttonGroupAnimator.cancel();
                }
                buttonGroupAnimator = ObjectAnimator.ofInt(translationObject, "translationX", translationXFrom, translationXTo);
                translationObject.setOnFrameObjectListener(null);
                buttonGroupAnimator.start();*/
                buttonGroup.showAnimation();
            } else {
                hideOpenButtonClick();
            }

        }
    }

    private void hideOpenButtonClick() {
        if (buttonGroup != null) {

            if (openButtonAnimator != null) {
                openButtonAnimator.cancel();
            }
            openButtonAnimator = ObjectAnimator.ofInt(frameRoateObject, "roate", -225, 0);
            openButtonAnimator.start();

            if (buttonGroupAnimator != null) {
                buttonGroupAnimator.cancel();
            }

            /*buttonGroupAnimator = ObjectAnimator.ofInt(translationObject, "translationX", translationXTo, translationXFrom);
            translationObject.setValue(translationXTo, translationXFrom);
            translationObject.setOnFrameObjectListener(new OnFrameObjectListener() {
                @Override
                public void onEnd() {
                    buttonGroup.setVisibility(View.INVISIBLE);
                }
            });
            buttonGroupAnimator.start();*/
            buttonGroup.hideAnimation();
        }
    }

    private void resizeOpenButtonLocal() {
        if (openButton != null) {
            int openButtonWidth = openButton.getWidth();
            int openButtonHeight = openButton.getHeight();
            if (getWidth() / 2 >= openButton.getLeft()) {
                openButton.setLeft(spaceLeft);
                openButton.setRight(spaceLeft + openButtonWidth);
                openButton.setTop(getHeight() - openButtonHeight - spaceBottom);
                openButton.setBottom(getHeight() - spaceBottom);
            } else {
                openButton.setLeft(getWidth() - openButtonWidth - spaceRight);
                openButton.setRight(getWidth() - spaceRight);
                openButton.setTop(getHeight() - openButtonHeight - spaceBottom);
                openButton.setBottom(getHeight() - spaceBottom);
            }
        }
    }

    protected boolean isPointInView(float x, float y, View child,
                                    PointF outLocalPoint) {
        float localX = x + getScrollX() - child.getLeft();
        float localY = y + getScrollY() - child.getTop();
        return localX >= 0 && localX < (child.getRight() - child.getLeft())
                && localY >= 0 && localY < (child.getBottom() - child.getTop());
    }

    private class ControlHandler extends BaseHandler<DictWordControlViewGroup> {


        public ControlHandler(DictWordControlViewGroup dictWordControlViewGroup) {
            super(dictWordControlViewGroup);
        }

        @Override
        public void handleMessage(Message msg) {

            DictWordControlViewGroup instance = getReference().get();
            if (instance == null) {
                return;
            }

            switch (msg.what) {

                case R.id.fragment_search_button_enlarge_size:
                    if (listener != null) {
                        listener.onEnlargeTextSize(buttonGroup.findViewById(R.id.fragment_search_button_enlarge_size),
                                buttonGroup.findViewById(R.id.fragment_search_button_reduce_size),
                                buttonGroup.findViewById(R.id.fragment_search_button_select_word));
                    }
                    break;

                case R.id.fragment_search_button_reduce_size:
                    if (listener != null) {
                        listener.onReducesTextSize(buttonGroup.findViewById(R.id.fragment_search_button_enlarge_size),
                                buttonGroup.findViewById(R.id.fragment_search_button_reduce_size),
                                buttonGroup.findViewById(R.id.fragment_search_button_select_word));
                    }
                    break;

                case R.id.fragment_search_button_select_word:
                    if (listener != null) {
                        boolean result = listener.onSelectedText(buttonGroup.findViewById(R.id.fragment_search_button_enlarge_size),
                                buttonGroup.findViewById(R.id.fragment_search_button_reduce_size),
                                buttonGroup.findViewById(R.id.fragment_search_button_select_word));
                        if (result) {
                            CustomToast.getInstance(getContext()).showToast(getContext().getString(R.string.tip_slected_world_is_open));
                        }
                    }
                    break;
            }

        }
    }

    public interface OnWordControlButtonListener {
        void onShow(View enlargeButton, View reduceButton, View selectButton);

        void onEnlargeTextSize(View enlargeButton, View reduceButton, View selectButton);

        void onReducesTextSize(View enlargeButton, View reduceButton, View selectButton);

        boolean onSelectedText(View enlargeButton, View reduceButton, View selectButton);
    }

}
