package com.readboy.mobile.dictionary.view;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.readboy.Dictionary.R;


/**
 * Created by Senny on 2015/11/26.
 */
public class RippleView extends View {

    private float mDownX;
    private float mDownY;
    private float mAlphaFactor;
    private float mDensity;
    private float mRadius;
    private float mMaxRadius;

    private int mRippleColor;
    private boolean mIsAnimating = false;
    private boolean mHover = true;

    private OnRippleViewAnimationListener listener;
    private RadialGradient mRadialGradient1;
    private RadialGradient mRadialGradient2;
    private RadialGradient mRadialGradient3;
    private Paint mPaint;
    private ObjectAnimator mRadiusAnimator;
    private final static int DEFAULT_DP = 50;
    private final static float DEFAULT_ALPHA = 255 * 0.6f;
    private int currentDp = DEFAULT_DP;

    private int dp(int dp) {
        return (int) (dp * mDensity + 0.5f);
    }

    public RippleView(Context context) {
        this(context, null);
    }

    public RippleView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RippleView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.RippleView);
        mRippleColor = a.getColor(R.styleable.RippleView_rippleColor,
                mRippleColor);
        mAlphaFactor = a.getFloat(R.styleable.RippleView_alphaFactor,
                mAlphaFactor);
        mHover = a.getBoolean(R.styleable.RippleView_hover, mHover);
        a.recycle();
    }

    public void setOnRippleViewAnimationListener(OnRippleViewAnimationListener listener) {
        this.listener = listener;
    }

    public void init() {
        mDensity = getContext().getResources().getDisplayMetrics().density;

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
//        mPaint.setAlpha(100);
        mPaint.setAlpha((int) DEFAULT_ALPHA);
        setRippleColor(Color.BLACK, 0.2f);

    }

    public void setRippleColor(int rippleColor, float alphaFactor) {
        mRippleColor = rippleColor;
        mAlphaFactor = alphaFactor;
    }

    public void setHover(boolean enabled) {
        mHover = enabled;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mMaxRadius = (float) Math.sqrt(w * w + h * h);
        mDownX = w / 2;
        mDownY = h / 2;
    }

    @Override
    protected void onDetachedFromWindow() {
        if (mRadiusAnimator != null) {
            mRadiusAnimator.cancel();
        }
        super.onDetachedFromWindow();
    }

    private boolean mAnimationIsCancel;
    private Rect mRect;

    @Override
    public boolean onTouchEvent(final MotionEvent event) {
        /*Log.d("TouchEvent", String.valueOf(event.getActionMasked()));
        Log.d("mIsAnimating", String.valueOf(mIsAnimating));
        Log.d("mAnimationIsCancel", String.valueOf(mAnimationIsCancel));*/
        boolean superResult = super.onTouchEvent(event);
        if (event.getActionMasked() == MotionEvent.ACTION_DOWN
                && this.isEnabled() && mHover) {
            mRect = new Rect(getLeft(), getTop(), getRight(), getBottom());
            mAnimationIsCancel = false;
            mDownX = event.getX();
            mDownY = event.getY();

            mRadiusAnimator = ObjectAnimator.ofFloat(this, "radius", 0, dp(DEFAULT_DP))
                    .setDuration(400);
            mRadiusAnimator
                    .setInterpolator(new AccelerateDecelerateInterpolator());
            mRadiusAnimator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {
                    mIsAnimating = true;
                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    setRadius(0);
                    RippleView.this.setAlpha(1.0f);
                    mIsAnimating = false;
                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });
            mRadiusAnimator.start();
            if (!superResult) {
                return true;
            }
        } else if (event.getActionMasked() == MotionEvent.ACTION_MOVE
                && this.isEnabled() && mHover) {
            mDownX = event.getX();
            mDownY = event.getY();

            // Cancel the ripple animation when moved outside
            if (mAnimationIsCancel = !mRect.contains(
                    getLeft() + (int) event.getX(),
                    getTop() + (int) event.getY())) {
                setRadius(0);
            } else {
                setRadius(dp(DEFAULT_DP));
            }
            if (!superResult) {
                return true;
            }
        } else if (event.getActionMasked() == MotionEvent.ACTION_UP
                && !mAnimationIsCancel && this.isEnabled()) {
            mDownX = event.getX();
            mDownY = event.getY();

            final float tempRadius = (float) Math.sqrt(mDownX * mDownX + mDownY
                    * mDownY);
            float targetRadius = Math.max(tempRadius, mMaxRadius);

            if (mIsAnimating) {
                mRadiusAnimator.cancel();
            }
            mRadiusAnimator = ObjectAnimator.ofFloat(this, "radius", dp(DEFAULT_DP),
                    targetRadius);
            mRadiusAnimator.setDuration(500);
            mRadiusAnimator
                    .setInterpolator(new AccelerateDecelerateInterpolator());
            mRadiusAnimator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {
                    mIsAnimating = true;
                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    setRadius(0);
                    RippleView.this.setAlpha(1.0f);
                    mIsAnimating = false;
                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });
            mRadiusAnimator.start();
            if (!superResult) {
                return true;
            }
        }
        return superResult;
    }

    private float fromRadius;
    private float toRadius;

    public void startWaveAnimation(final float fromRadius, final float toRadius, final int duration, final OnRippleViewAnimationListener listener) {

        this.fromRadius = fromRadius;
        this.toRadius = toRadius;

        if (mIsAnimating) {
            mRadiusAnimator.cancel();
        }
        mRadiusAnimator = ObjectAnimator.ofFloat(this, "radius", fromRadius,
                toRadius);
        mRadiusAnimator.setDuration(duration);
        mRadiusAnimator
                .setInterpolator(new AccelerateDecelerateInterpolator());
        mRadiusAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                mIsAnimating = true;
            }

            @Override
            public void onAnimationEnd(Animator animator) {
//                setRadius(0);
//                RippleView.this.setAlpha(1.0f);
                if (listener != null) {
                    listener.onAnimationEnd();
                }
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        mRadiusAnimator.start();
    }

    public void startRepeatWaveAnimation(final float fromRadius, final float toRadius, final int duration) {

        this.fromRadius = fromRadius;
        this.toRadius = toRadius;

        /*if (mIsAnimating) {
            if (mRadiusAnimator != null) {
                mRadiusAnimator.cancel();
            }
        }*/
        if (mRadiusAnimator != null) {
            mRadiusAnimator.cancel();
        }
        mRadiusAnimator = ObjectAnimator.ofFloat(this, "radius", fromRadius,
                toRadius);
        mRadiusAnimator.setDuration(duration);
        mRadiusAnimator.setRepeatCount(ObjectAnimator.INFINITE);
        mRadiusAnimator.setRepeatMode(ObjectAnimator.RESTART);
        mRadiusAnimator
                .setInterpolator(new AccelerateDecelerateInterpolator());
        mRadiusAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                mIsAnimating = true;
            }

            @Override
            public void onAnimationEnd(Animator animator) {
//                setRadius(0);
//                RippleView.this.setAlpha(1.0f);
//                startRepeatWaveAnimation(fromRadius, toRadius, duration);
                mIsAnimating = false;

            }

            @Override
            public void onAnimationCancel(Animator animator) {
                mIsAnimating = false;
            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        mRadiusAnimator.start();
    }

    public int adjustAlpha(int color, float factor) {
        int alpha = Math.round(Color.alpha(color) * factor);
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        return Color.argb(alpha, red, green, blue);
    }

    public void setRadius(final float radius) {
        mRadius = radius;
        if (mRadius > 0) {
            mRadialGradient1 = new RadialGradient(mDownX, mDownY, mRadius,
                    adjustAlpha(mRippleColor, mAlphaFactor), mRippleColor,
                    Shader.TileMode.MIRROR);
            mRadialGradient2 = new RadialGradient(mDownX, mDownY, mRadius - 20,
                    adjustAlpha(mRippleColor, mAlphaFactor), mRippleColor,
                    Shader.TileMode.MIRROR);
            mRadialGradient3 = new RadialGradient(mDownX, mDownY, mRadius - 40,
                    adjustAlpha(mRippleColor, mAlphaFactor), mRippleColor,
                    Shader.TileMode.MIRROR);
//            mPaint.setShader(mRadialGradient);
            mPaint.setAlpha((int) ((toRadius - radius) * DEFAULT_ALPHA / (toRadius - fromRadius)));
        }
        invalidate();
    }

    private Path mPath1 = new Path();
    private Path mPath2 = new Path();
    private Path mPath3 = new Path();

    @Override
    protected void onDraw(final Canvas canvas) {
        super.onDraw(canvas);

        if (isInEditMode()) {
            return;
        }

        canvas.save(Canvas.CLIP_SAVE_FLAG);

        mPath1.reset();
        mPath1.addCircle(mDownX, mDownY, mRadius, Path.Direction.CW);
        canvas.clipPath(mPath1);
        canvas.restore();
        mPaint.setShader(mRadialGradient1);
        canvas.drawCircle(mDownX, mDownY, mRadius, mPaint);

        canvas.save(Canvas.CLIP_SAVE_FLAG);
        mPath2.reset();
        mPath2.addCircle(mDownX, mDownY, mRadius - 20, Path.Direction.CW);
        canvas.clipPath(mPath2);
        canvas.restore();
        mPaint.setShader(mRadialGradient2);
        canvas.drawCircle(mDownX, mDownY, mRadius - 20, mPaint);

        canvas.save(Canvas.CLIP_SAVE_FLAG);
        mPath3.reset();
        mPath3.addCircle(mDownX, mDownY, mRadius - 40, Path.Direction.CW);
        canvas.clipPath(mPath3);
        canvas.restore();
        mPaint.setShader(mRadialGradient3);
        canvas.drawCircle(mDownX, mDownY, mRadius - 40, mPaint);
    }

    public interface OnRippleViewAnimationListener {
        void onAnimationEnd();
    }
}
