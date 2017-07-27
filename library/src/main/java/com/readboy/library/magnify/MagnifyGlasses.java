package com.readboy.library.magnify;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by 123 on 2016/7/8.
 */

public class MagnifyGlasses implements IMagnifier{

    private Path mPath = new Path();
    private Matrix matrix = new Matrix();
    //radius
    private static final int RADIUS = 80;
    //scale
    private static final float FACTOR = 1.2f;
    private float mCurrentX, mCurrentY;
    private View drawView;
    private View targetView;
    private Rect mRect;
    private boolean canDdraw = false;


    public MagnifyGlasses(View drawView, View targetView) {
        this.drawView = drawView;
        this.targetView = targetView;
        mPath.addCircle(RADIUS, RADIUS, RADIUS, Path.Direction.CW);
        matrix.setScale(FACTOR, FACTOR);
        mRect = new Rect();
        if (drawView.willNotDraw()) {
            drawView.setWillNotDraw(false);
        }
    }

    private Bitmap cutScreenShot(View view, float x, float y, int radius) {
        Bitmap bitmap = Bitmap.createBitmap(radius * 2, radius * 2, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.save();
        float dx = - (x - radius);
        float dy = - (y - radius);
        canvas.translate(dx, dy);
        mPath.reset();
        mPath.addCircle(-dx + radius, -dy + radius, radius, Path.Direction.CW);
        canvas.clipPath(mPath);
        view.draw(canvas);
        canvas.restore();
        return bitmap;
    }

    @Override
    public boolean onTouch(MotionEvent event) {
        return false;
    }

    @Override
    public void onTouch(float x, float y) {
        canDdraw = true;
        mCurrentX = x;
        mCurrentY = y;
        drawView.invalidate();
    }

    @Override
    public void onDraw(Canvas canvas) {
        if (canDdraw) {
            Bitmap bitmap = cutScreenShot(targetView, mCurrentX, mCurrentY, RADIUS);
            canvas.save();
            canvas.translate(mCurrentX - (RADIUS * 2) * FACTOR, mCurrentY - (RADIUS * 2) * FACTOR);
            canvas.drawBitmap(bitmap, matrix, null);
            canvas.restore();
            if (!bitmap.isRecycled()) {
                bitmap.recycle();
            }
        }
    }

    @Override
    public void cancel() {
        canDdraw = false;
        drawView.invalidate();
    }
}
