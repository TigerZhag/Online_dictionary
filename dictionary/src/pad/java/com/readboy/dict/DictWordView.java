package com.readboy.dict;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.OverScroller;

import com.readboy.Dictionary.R;
import com.readboy.library.io.DictFile;
import com.readboy.library.utils.DictEllipsisWordAnalyze;
import com.readboy.library.utils.DictInfo;
import com.readboy.library.utils.DictWordAnalyze;
import com.readboy.library.utils.NormalWordAnalyze;
import com.readboy.library.widget.TextScrollView;

/**
 * Created by Senny on 2015/10/27.
 */
public class DictWordView extends TextScrollView implements TextScrollView.OnTextScrollViewListener{

    private int radius = 16;
    private int strokeWidth = 2;
    private Paint linePaint;
    private Paint bgPaint;
    private boolean isBlnFrame;

    private OverScroller mOverScroller;
    private OnScrollTextViewListener listener;

    public DictWordView(Context context) {
        super(context);
    }

    public DictWordView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DictWordView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        mOverScroller = new OverScroller(context);

    }


    public void setDictWordText(byte[] buffer) {
        DictWordAnalyze dictWordAnalyze = new DictWordAnalyze(buffer);
        setText(dictWordAnalyze);
    }

    public void setDictWordText(String keyWord, byte[] buffer) {
        DictWordAnalyze dictWordAnalyze = new DictWordAnalyze(keyWord, buffer);
        setText(dictWordAnalyze);
    }

    public void setDictEllipsisWordText(byte[] buffer) {
        DictEllipsisWordAnalyze dictWordAnalyze = new DictEllipsisWordAnalyze(buffer);
        setText(dictWordAnalyze);
    }

    public void setDictEllipsisWordText(String keyWord, byte[] buffer) {
        DictEllipsisWordAnalyze dictWordAnalyze = new DictEllipsisWordAnalyze(keyWord, buffer);
        setText(dictWordAnalyze);
    }

    /*public void setDictWordText(String string) {
        NormalWordAnalyze normalWordAnalyze = new NormalWordAnalyze(string);
        setText(normalWordAnalyze);
    }*/

    public void setDictWordText(String keyWord, String expain) {
        expain = keyWord + "\n" + expain;
        NormalWordAnalyze normalWordAnalyze = new NormalWordAnalyze(expain);
        setText(normalWordAnalyze);
    }

    public void setFrame(boolean isBlnFrame) {
        this.isBlnFrame = isBlnFrame;
    }

    /**
     * 滑动事件
     */
    /*@Override
    public void fling(int velocityY) {
        super.fling(velocityY / 3);
    }*/

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        setSelectBackgroundColor(getResources().getColor(R.color.light_blue));
        /*getItemGroup().setPadding(getItemGroup().getPaddingLeft(),
                30, getItemGroup().getPaddingRight(),
                30);*/

        linePaint = new Paint();
        linePaint.setColor(getResources().getColor(R.color.light_grey_4));
        linePaint.setStrokeWidth(strokeWidth);
        bgPaint = new Paint();
        bgPaint.setColor(getResources().getColor(R.color.middle_grey));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (isBlnFrame) {
//            canvas.drawLine(radius, 0, getWidth() - radius, 0, linePaint);
            canvas.drawLine(strokeWidth / 2, radius, 0, getHeight(), linePaint);
            canvas.drawLine(getWidth() - strokeWidth / 2, radius, getWidth() - strokeWidth / 2, getHeight(), linePaint);
            canvas.drawLine(0, getHeight() - strokeWidth / 2, getWidth(), getHeight() - strokeWidth / 2, linePaint);
            canvas.drawCircle(0, 0, radius, linePaint);
            canvas.drawCircle(getWidth(), 0, radius, linePaint);
            canvas.drawCircle(0, 0, radius - strokeWidth, bgPaint);
            canvas.drawCircle(getWidth(), 0, radius - strokeWidth, bgPaint);
        }


    }

    public boolean getSelectTextState() {
        return isSelectText();
    }

    public void showText(String keyWord, byte[] data, int start, int dictID, int textSize) {
        setText(keyWord, data, start, dictID);
    }

    public void showTextByBigFirstLine(String keyWord, byte[] data, int start, int dictID, int textSize) {
        setText(keyWord, data, start, dictID);
        setChangeLineTextSize(0, 50);
    }

    private void setText(String keyWord, byte[] data, int start, int dictID) {
        if (dictID == DictFile.ID_BHDICT) {
            setDictWordText(keyWord, new String(data));
            return;
        }
        if (data != null && data.length > start) {
            for (int i = 0 ; i < start ; i++) {
                data[i] = 0;
            }
        }
        boolean isAddKeyWord = false;
        for (int id: DictInfo.EXPLAIN_COPY_KEY_ID) {
            if (id == dictID) {
                isAddKeyWord = true;
                break;
            }
        }
        if (isAddKeyWord) {
            setDictWordText(keyWord, data);
        } else {
            setDictWordText(data);
        }
    }

    public void clearSelectWords() {
        clearSelectedBackground();
    }

    public void setOnScrollTextViewListener(OnScrollTextViewListener listener) {
        this.listener = listener;
        setOnTextScrollViewListener(this);
    }

    public void setSelectTextState(boolean value) {
        setSelectText(value);
    }

    public void clear() {

    }

    public void onResume() {

    }

    public void onPause(boolean value) {

    }

    @Override
    public void startAnalyTextPage() {

    }

    @Override
    public void loadingTextPage() {

    }

    @Override
    public void startSelectText() {

    }

    @Override
    public void endSelectText(String text, RectF startRectF, RectF endRectF) {
        if (listener != null) {
            listener.getSelectText(text);
        }
    }

    @Override
    public void onMoveSelect(float x, float y) {

    }

    public interface OnScrollTextViewListener {
        void getSelectText(String selectStr);
        void touchedView();
    }
}
