package com.readboy.scantranslate.widght;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;

import com.readboy.scantranslate.R;
import com.readboy.scantranslate.Utils.StringUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Zhang shixin
 * @date 16-7-7.
 */
public class ScannerOverlayView extends View {

    private Rect frame;
    private DisplayMetrics metrics;

    private Paint paint;

//    private String aboveText;
    private List<String> result = new ArrayList<>();

    private int textSize = 16;
    private int textPaddingTop = 10;

    /**
     * the max text width
     */
    private int textWidth = 700;
    private int btn_cover_height = 480;
//    private static final int frame_width = 432;
//    private static final int frame_height = 126;

    private int frame_stroke_width = 2;
    private int frame_stroke_length = 23;
    private int frame_stroke_height = 10;

    private int crosshair_length = 20;
    private int textGapPadding = 10;

    /**
     * colors
     */
    private int trans_half;
    private int white;
    private int scanner_mask;
    private int frame_blue;
    private int black;
//    private int frame_cover;

    private String toast;

    public ScannerOverlayView(Context context) {
        super(context);
        init();
    }

    public ScannerOverlayView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ScannerOverlayView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        //init the frame
        metrics = getResources().getDisplayMetrics();

        //调整长宽
        frame_stroke_length *= metrics.density;
        frame_stroke_width *= metrics.density;
        frame_stroke_height *= metrics.density;
        crosshair_length *= metrics.density;

        int width = metrics.widthPixels * 2 / 5;
        int height = width * 7 / 24;
        int leftOffset = (metrics.widthPixels - width) / 2;
        int topOffset = (metrics.heightPixels - height) / 4;
        Log.e(TAG, "init: device metrics : density : " + metrics.density + " width : " + metrics.widthPixels + " height : " + metrics.heightPixels);
        frame = new Rect(leftOffset, topOffset, leftOffset + width, topOffset + height);
        paint = new Paint();
        paint.setTypeface(Typeface.create("readboy-code",Typeface.NORMAL));

        trans_half = getResources().getColor(R.color.trans_half);
        white = getResources().getColor(R.color.white);
        scanner_mask = getResources().getColor(R.color.scanner_mask);
        frame_blue = getResources().getColor(R.color.frame_blue);
        black = getResources().getColor(R.color.black);
//        frame_cover = getResources().getColor(R.color.frame_cover_grey);
        toast = getResources().getString(R.string.scan_toast);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // draw the overlay
        /*-
          -------------------------------------
          |                top                |
          -------------------------------------
          |      |                    |       |
          |      |                    |       |
          | left |        -|-         | right |
          |      |                    |       |
          |      |                    |       |
          -------------------------------------
          |              bottom               |
          -------------------------------------
         */
//        paint.setColor(scanner_mask);
//        canvas.drawRect(0, 0, metrics.widthPixels, frame.top, paint);
//        canvas.drawRect(0, frame.top, frame.left, frame.bottom, paint);
//        canvas.drawRect(frame.right, frame.top, metrics.widthPixels, frame.bottom, paint);
//        canvas.drawRect(0, frame.bottom, metrics.widthPixels, metrics.heightPixels - btn_cover_height, paint);

//        paint.setColor(trans_half);
//        canvas.drawRect(0, metrics.heightPixels - btn_cover_height, metrics.widthPixels, metrics.heightPixels, paint);

        //draw the frame
        paint.setColor(frame_blue);
        canvas.drawRect(frame.left - frame_stroke_width, frame.top - frame_stroke_width, frame.left + frame_stroke_length - frame_stroke_width, frame.top, paint);
        canvas.drawRect(frame.right - frame_stroke_length, frame.top - frame_stroke_width, frame.right + frame_stroke_width, frame.top, paint);
        canvas.drawRect(frame.left - frame_stroke_width, frame.bottom, frame.left + frame_stroke_length, frame.bottom + frame_stroke_width, paint);
        canvas.drawRect(frame.right - frame_stroke_length, frame.bottom, frame.right + frame_stroke_width, frame.bottom + frame_stroke_width, paint);

        canvas.drawRect(frame.left - frame_stroke_width, frame.top, frame.left, frame.top + frame_stroke_height, paint);
        canvas.drawRect(frame.right, frame.top, frame.right + frame_stroke_width, frame.top + frame_stroke_height, paint);
        canvas.drawRect(frame.left - frame_stroke_width, frame.bottom - frame_stroke_height, frame.left, frame.bottom, paint);
        canvas.drawRect(frame.right, frame.bottom - frame_stroke_height, frame.right + frame_stroke_width, frame.bottom, paint);

        canvas.drawRect(frame.left + frame.width() / 2 - crosshair_length / 2, frame.top + frame.height() / 2 - frame_stroke_width / 2, frame.left + frame.width() / 2 + crosshair_length / 2, frame.top + frame.height() / 2 + frame_stroke_width / 2, paint);
        canvas.drawRect(frame.left + frame.width() / 2 - frame_stroke_width / 2, frame.top + frame.height() / 2 - crosshair_length / 2, frame.left + frame.width() / 2 + frame_stroke_width / 2, frame.top + frame.height() / 2 + crosshair_length / 2, paint);

//        paint.setColor(frame_cover);
//        canvas.drawRect(frame.left,frame.top,frame.right,frame.bottom,paint);

        //draw the text
        paint.setColor(white);
        paint.setTextSize(textSize * metrics.density);
        if (result.size() > 0) {
            paint.setAlpha(0xFF);
            int texttop = (int) (frame.bottom + textPaddingTop * metrics.density);
            Rect rect = new Rect();
            List<String> temp = new ArrayList<>();
            for (String text : result) {
                if (text == null) break;
                int times = (int) (Math.floor(paint.measureText(text, 0, text.length()) / textWidth) + 1);
                if (times == 1) temp.add(text);
                else cutString(text, temp,paint);
            }

            int maxWidth = 0;
            int maxHeight = 0;
            int titleWidth = 0;
            for (String text : temp) {
                paint.getTextBounds(text, 0, text.length(), rect);
                if (titleWidth == 0) titleWidth = rect.width();
                if (rect.width() > maxWidth) maxWidth = rect.width();
                if (rect.height() > maxHeight) maxHeight = rect.height() + textGapPadding;
            }
            canvas.drawText(temp.get(0).replaceAll("[^(A-Za-z)]", ""), (getWidth() - titleWidth) / 2, texttop + maxHeight, paint);
            texttop += maxHeight;
            for (int i = 1; i < temp.size(); i++) {
                String text = temp.get(i);
                canvas.drawText(text, (getWidth() - maxWidth) / 2, texttop + maxHeight, paint);
                texttop += maxHeight;
                if (i >= 6){

                    break;
                }
            }

        }

//        //draw text above the frame
        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
//        if (aboveText != null && aboveText.length() > 0) {
//            Rect rect = new Rect();
//            paint.getTextBounds(aboveText, 0, aboveText.length(), rect);
//
//            float baseline = frame.top - textPaddingTop * metrics.density - rect.height(); // padding基础上增加一个rect的高度
//
//            RectF rectF = new RectF((getWidth() - rect.width()) / 2 - 40, baseline + fontMetrics.top - 20, (getWidth() + rect.width()) / 2 + 40, baseline + fontMetrics.bottom + 20);
//            paint.setColor(getResources().getColor(R.color.trans_half));
//            canvas.drawRoundRect(rectF, 60, 60, paint);
//            paint.setColor(getResources().getColor(R.color.white));
//            canvas.drawText(aboveText, (getWidth() - rect.width()) / 2, baseline, paint);
//        }

        // draw toast
        if (showToast) {
            float toastWidth = paint.measureText(toast);
            float toastBgWidth = 260 * metrics.density;//
            float toastHeight = 50 * metrics.density;
            float toastBaseline = frame.bottom + 78 + fontMetrics.bottom - fontMetrics.top;

//            RectF rectF = new RectF((getWidth() - toastWidth) / 2 - 40, toastBaseline + fontMetrics.top - 20, (getWidth() + toastWidth) / 2 + 40, toastBaseline + fontMetrics.bottom + 20);
            RectF rectF = new RectF((getWidth() - toastBgWidth) / 2 - 40, toastBaseline + fontMetrics.top - 30, (getWidth() + toastBgWidth) / 2 + 40, toastBaseline + fontMetrics.bottom + 30);
            paint.setColor(white);
            canvas.drawRoundRect(rectF, 40, 40, paint);
            paint.setColor(black);
            canvas.drawText(toast, (getWidth() - toastWidth) / 2, toastBaseline, paint);
        }
    }

    private void cutString(String text, List<String> list,Paint paint) {
        List<String> temp = StringUtil.split(text);
        StringBuilder lineBuilder = new StringBuilder();
        for (String tmp : temp){
            lineBuilder.append(tmp);
            if (paint.measureText(lineBuilder.toString()) >= textWidth){
                lineBuilder.delete(lineBuilder.length() - tmp.length(),lineBuilder.length());//[start,end)
                list.add(lineBuilder.toString());
                lineBuilder = new StringBuilder();
                lineBuilder.append(tmp);
            }
        }
        if (lineBuilder.length() > 0){
            list.add(lineBuilder.toString());
        }
    }

    private boolean showToast = false;

    public void showToast() {
        this.showToast = true;
        invalidate();
    }

    public void showToast(String toast) {
        this.showToast = true;
        this.toast = toast;
        invalidate();
    }

    public void hideToast() {
        this.showToast = false;
        invalidate();
    }

    public Rect getFrame() {
        return frame;
    }

    public List<String> getResult() {
        return result;
    }


//    public void setAboveText(String text) {
//        Log.e(TAG, "setAboveText: " + text);
//        this.aboveText = text;
//        invalidate(frame.left, (int) (frame.top - frame.height() - textPaddingTop * metrics.density), frame.right, (int) (frame.top - textPaddingTop * metrics.density));
//    }

    public void setToast(String text) {
        this.showToast = true;
        toast = text;
        invalidate();
    }

    public void setResult(List<String> results) {
        showToast = false;
        this.result = results;
        invalidate((getWidth() - textWidth) / 2, (int) (frame.bottom + textPaddingTop * metrics.density), (getWidth() + textWidth) / 2, (int) (frame.bottom + textPaddingTop * metrics.density + btn_cover_height));
    }

    private static final String TAG = "ScannerOverlayView";

    public Bitmap getScanedImage(Bitmap bitmap) {
        Bitmap croppedBitmap = Bitmap.createBitmap(bitmap, frame.left, frame.top, frame.width(), frame.height());
//        ImageUtil.saveBitmap(croppedBitmap, SystemClock.currentThreadTimeMillis() + "ocr.jpg");
//        ImageUtil.recycleBmp(bitmap);
        return croppedBitmap;
    }

    public String getToast() {
        return toast;
    }
}
