package com.readboy.library.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Message;
import android.os.Parcelable;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.readboy.library.utils.BaseHandler;
import com.readboy.library.utils.PhoneticUtils;
import com.readboy.library.widget.analy.AnalysisInfo;
import com.readboy.library.widget.analy.ImlAnalyze;
import com.sen.lib.view.BaseItemAdapter;
import com.sen.lib.view.VerticalScrollWidget;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;

/**
 * Created by Sen on 2015/6/15.
 */
public class TextScrollView extends VerticalScrollWidget implements ImlDrawText<Integer> {

    private final static int CURSOR_ELAPSE_TIME = 500;

    private TextHandler mHandler;

    /**
     * default size
     */
    private int textSize = 20;

    private int changeLine = -1;

    private int changeTextSize = 0;
    private int contentHeight;
    private int selectBackgroundColor = Color.YELLOW;
    private int targetX = 0;
    private int targetY = 0;

    private float cursorStartX;
    private float cursorStartY;
    private float cursorEndX;
    private float cursorEndY;
    private float currentTouchX;
    private float currentTouchY;

    private boolean isSelectText = false;
    private boolean hasTargetPosition;
    private boolean isBlnCursorVisible;
    private boolean blnCanCursorVisible = false;
    private boolean isBlnScrollWhenSelect = true;

    private long lngCursorLastTime = SystemClock.elapsedRealtime();
    private SelectSingleSpanInfo downSelectSingleSpanInfo;
    private SelectSingleSpanInfo moveSelectSingleSpanInfo;
    private ExecutorService singleExecutor;
    private RectF[] selectBackgroundRectFArray;
    private OnTextScrollViewListener onTextScrollViewListener;
    private AnalyzeCodeThread analyzeCodeThread;
    private AnalysisInfo analysisInfo;
    private List<PageInfo> pageList;
    private BaseItemAdapter adapter;
    private Paint mTextPaint;
    private Paint mCursorPaint;

    public void setOnTextScrollViewListener(OnTextScrollViewListener onTextScrollViewListener) {
        this.onTextScrollViewListener = onTextScrollViewListener;
    }

    public int getSelectBackgroundColor() {
        return selectBackgroundColor;
    }

    public void setSelectBackgroundColor(int selectBackgroundColor) {
        this.selectBackgroundColor = selectBackgroundColor;
    }

    public int getTextSize() {
        return textSize;
    }

    public void setTextSize(int textSize) {
        this.textSize = textSize;
        if (mTextPaint != null) {
            mTextPaint.setTextSize(this.textSize);
        }
        if (analysisInfo != null && mHandler != null) {
            AnalysisInfo copyAnalysisInfo = new AnalysisInfo();
            analysisInfo.copy(copyAnalysisInfo);
            mHandler.sendMessages(TextHandler.PARARSE_START, copyAnalysisInfo, 0, 0, 0);
        }
    }

    public void setTargetPositionAfterLoaded(int targetX, int targetY) {
        this.targetX = targetX;
        this.targetY = targetY;
        hasTargetPosition = true;
    }

    public boolean isSelectText() {
        return isSelectText;
    }

    public void setSelectText(boolean isSelectText) {
        this.isSelectText = isSelectText;
    }

    public TextScrollView(Context context) {
        super(context);
    }

    public TextScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TextScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setChangeLineTextSize(int changLine, int changeTextSize) {
        this.changeLine = changLine;
        this.changeTextSize = changeTextSize;
    }

    public void clearSelectedBackground() {
        selectBackgroundRectFArray = null;
        invalidate();
    }

    protected void setText(final ImlAnalyze<ISpanAnalysisObtainer> imlAnalyze) {
        if (imlAnalyze != null) {
            imlAnalyze.setObtainer(mHandler);
        }
        if (getTextSize() != 0) {

            if (getWidth() == 0 || getHeight() == 0) {
                this.post(new Runnable() {
                    @Override
                    public void run() {
                        setText(imlAnalyze, getTextSize());
                    }
                });
            } else {
                setText(imlAnalyze, getTextSize());
            }
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isSelectText) {

            touch(event);
            return true;
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        return super.onSaveInstanceState();
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        super.onRestoreInstanceState(state);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        init();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (selectBackgroundRectFArray != null) {
            Paint backgroundPaint = new Paint();
            backgroundPaint.setColor(getSelectBackgroundColor());
            for (RectF rectF : selectBackgroundRectFArray) {
                if (rectF != null) {
                    canvas.drawRect(rectF, backgroundPaint);
                }
            }
        }

        if (blnCanCursorVisible) {
            long currentTime = SystemClock.elapsedRealtime();
            if ((currentTime - lngCursorLastTime) > CURSOR_ELAPSE_TIME) {
                isBlnCursorVisible = !isBlnCursorVisible;
                lngCursorLastTime = currentTime;
            }
            if (isBlnCursorVisible) {
                canvas.drawLine(cursorStartX, cursorStartY, cursorEndX, cursorEndY, mCursorPaint);
            }
            postInvalidateDelayed(CURSOR_ELAPSE_TIME);
        }
    }

    @Override
    public void removeAllViews() {
        destroy();
        super.removeAllViews();
    }

    public void destroy() {
        stopAnalyze();
        if (singleExecutor != null) {
            singleExecutor.shutdown();
        }
        clear();
        onTextScrollViewListener = null;
        mHandler = null;
        setAdapter(null);
    }

    private void clear() {
        clearPageList();
        if (analysisInfo != null) {
            analysisInfo.destroy();
        }
        analysisInfo = null;
    }

    private void clearPageList() {
        if (pageList != null) {
            for (PageInfo pageInfo : pageList) {
                if (pageInfo.data != null) {
                    pageInfo.data.clear();
                }
            }
            pageList.clear();
        }
    }

    public void reset() {
        clearPageList();
        if (analysisInfo != null) {
            if (analysisInfo.textSizeMap != null) {
                analysisInfo.textSizeMap.clear();
            }
        }
        cursorStartX = 0;
        cursorStartY = 0;
        cursorEndX = 0;
        cursorEndY = 0;

        contentHeight = 0;
    }

    private void init() {

        singleExecutor = Executors.newSingleThreadExecutor();
        mTextPaint = new Paint();
        mTextPaint.setTextSize(textSize);
        mTextPaint.setColor(Color.BLACK);
        mTextPaint.setSubpixelText(true);
        mTextPaint.setAntiAlias(true);

        mCursorPaint = new Paint();
        mCursorPaint.setTextSize(textSize);
        mCursorPaint.setColor(Color.BLACK);
        mCursorPaint.setSubpixelText(true);

        mHandler = new TextHandler(this);
        pageList = new ArrayList<PageInfo>();
        adapter = new BaseItemAdapter() {
            @Override
            public View getView(View v, ViewGroup container, int postion) {
                if (v == null) {
                    v = new PageText(container.getContext());
                    ((PageText) v).setImlDrawText(TextScrollView.this);
                }

                ViewGroup.LayoutParams lp = v.getLayoutParams();
                if (lp == null) {
                    lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                }
                if (pageList != null && pageList.size() > postion) {
                    lp.width = pageList.get(postion).width;
                    lp.height = pageList.get(postion).height;
                    v.setLayoutParams(lp);
                    ((PageText) v).setIndex(postion);
                }
                return v;
            }

            @Override
            public int getCount() {
                if (pageList != null) {
                    return pageList.size();
                }
                return 0;
            }
        };
        setAdapter(adapter);
    }

    private void setText(final ImlAnalyze<ISpanAnalysisObtainer> imlAnalyze, final int textSize) {
        stopAnalyze();
        analyzeCodeThread = new AnalyzeCodeThread(imlAnalyze);
        analyzeCodeThread.start();
    }

    private void touch(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:

                downSelectSingleSpanInfo = null;
                moveSelectSingleSpanInfo = null;
                currentTouchX = 0.0f;
                currentTouchY = 0.0f;

                currentTouchX = event.getX();
                currentTouchY = event.getY();
                downSelectSingleSpanInfo = getSelectCharsInfo((int) event.getX() - getPaddingLeft(),
                        (int) event.getY() + getScrollY() - getPaddingTop() - getItemGroup().getPaddingTop());
                if (downSelectSingleSpanInfo != null) {
                    setCursorPosition(downSelectSingleSpanInfo);
                }
                if (onTextScrollViewListener != null) {
                    onTextScrollViewListener.onMoveSelect(event.getX(), event.getY());
                }
                break;

            case MotionEvent.ACTION_MOVE:
                currentTouchX = event.getX();
                currentTouchY = event.getY();
                if (isBlnScrollWhenSelect) {
                    if (textSize + 0.0f > (event.getY() - getPaddingTop() - getItemGroup().getPaddingTop())) {
                        scrollBy(0, -textSize);
                    } else if (textSize + 0.0f > (getHeight() - getPaddingBottom() - getItemGroup().getPaddingBottom() - event.getY())) {
                        scrollBy(0, textSize);
                    }
                }
                SelectSingleSpanInfo tempMoveSelectSingleSpanInfo = getSelectCharsInfo((int) event.getX() - getPaddingLeft(),
                        (int) event.getY() + getScrollY() - getPaddingTop() - getItemGroup().getPaddingTop());
                if (tempMoveSelectSingleSpanInfo != null) {
                    if (moveSelectSingleSpanInfo == null || (moveSelectSingleSpanInfo != null &&
                            (moveSelectSingleSpanInfo.start != tempMoveSelectSingleSpanInfo.start
                                    || moveSelectSingleSpanInfo.charsIndex != tempMoveSelectSingleSpanInfo.charsIndex
                                    || moveSelectSingleSpanInfo.relativeLineIndex != tempMoveSelectSingleSpanInfo.relativeLineIndex
                                    || moveSelectSingleSpanInfo.pageIndex != tempMoveSelectSingleSpanInfo.pageIndex))) {
                        moveSelectSingleSpanInfo = tempMoveSelectSingleSpanInfo;
                        computeSelectRegion(CaculateSelectionRegion.STATE_SELECT_BACKGROUND,
                                downSelectSingleSpanInfo, moveSelectSingleSpanInfo);
                    }
                }
                if (onTextScrollViewListener != null) {
                    onTextScrollViewListener.onMoveSelect(event.getX(), event.getY());
                }
                break;

            case MotionEvent.ACTION_UP:

                if (downSelectSingleSpanInfo != null && moveSelectSingleSpanInfo == null) {
//                    moveSelectSingleSpanInfo = downSelectSingleSpanInfo;
                    SelectSingleSpanInfo[] selectSingleSpanInfoArray = getSelectCharsInfoArray((int) event.getX() - getPaddingLeft(),
                            (int) event.getY() + getScrollY() - getPaddingTop() - getItemGroup().getPaddingTop());
                    if (selectSingleSpanInfoArray != null
                            && selectSingleSpanInfoArray.length == 2) {
                        if (selectSingleSpanInfoArray[0] == selectSingleSpanInfoArray[1]) {
                            computeSelectRegion(CaculateSelectionRegion.STATE_SINGLE_DOWN_SELECTED,
                                    selectSingleSpanInfoArray[0], null);
                        } else {
                            computeSelectRegion(CaculateSelectionRegion.STATE_SINGLE_DOWN_SELECTED,
                                    selectSingleSpanInfoArray[0], selectSingleSpanInfoArray[1]);
                        }
                        selectSingleSpanInfoArray = null;
                    } else {
//                        downSelectSingleSpanInfo = getSelectCharsInfoPrecise((int) currentTouchX - getPaddingLeft(),
//                                (int) currentTouchY + getScrollY() - getPaddingTop() - getItemGroup().getPaddingTop());
//                        if (downSelectSingleSpanInfo != null) {
//                            computeSelectRegion(CaculateSelectionRegion.STATE_SINGLE_DOWN_SELECTED,
//                                    downSelectSingleSpanInfo, null);
//                        }
                    }
                } else if (downSelectSingleSpanInfo != null && moveSelectSingleSpanInfo != null) {
                    computeSelectRegion(CaculateSelectionRegion.STATE_SELECT_WORLD,
                            downSelectSingleSpanInfo, moveSelectSingleSpanInfo);
                }
                downSelectSingleSpanInfo = null;
                moveSelectSingleSpanInfo = null;
                currentTouchX = 0.0f;
                currentTouchY = 0.0f;
                break;

            case MotionEvent.ACTION_CANCEL:

                /*if (downSelectSingleSpanInfo != null && moveSelectSingleSpanInfo == null) {
//                    moveSelectSingleSpanInfo = downSelectSingleSpanInfo;
                    downSelectSingleSpanInfo = getSelectCharsInfoPrecise((int) currentTouchX - getPaddingLeft(),
                            (int) currentTouchY + getScrollY() - getPaddingTop() - getItemGroup().getPaddingTop());
                    if (downSelectSingleSpanInfo != null) {
                        computeSelectRegion(CaculateSelectionRegion.STATE_SINGLE_DOWN_SELECTED,
                                downSelectSingleSpanInfo, null);
                    }
                } else if (downSelectSingleSpanInfo != null && moveSelectSingleSpanInfo != null) {
                    computeSelectRegion(CaculateSelectionRegion.STATE_SELECT_WORLD,
                            downSelectSingleSpanInfo, moveSelectSingleSpanInfo);
                }
                downSelectSingleSpanInfo = null;
                moveSelectSingleSpanInfo = null;
                currentTouchX = 0.0f;
                currentTouchY = 0.0f;*/
                break;
        }
    }

    private void setCursorPosition(SelectSingleSpanInfo selectSingleSpanInfo) {
        if (selectSingleSpanInfo != null) {
            mCursorPaint.setStrokeWidth(mCursorPaint.getStrokeMiter());
            cursorStartX = getPaddingLeft() + selectSingleSpanInfo.cursorRelativeX;
            cursorStartY = pageList.get(selectSingleSpanInfo.pageIndex).y + pageList.get(selectSingleSpanInfo.pageIndex).data.get(selectSingleSpanInfo.relativeLineIndex).y;
            cursorEndX = cursorStartX;
            cursorEndY = cursorStartY + mCursorPaint.getFontMetrics().bottom - mCursorPaint.getFontMetrics().top;
        }
    }

    private void stopAnalyze() {
        if (analyzeCodeThread != null) {
            analyzeCodeThread.setStop();
        }
    }

    private SelectSingleSpanInfo getSelectCharsInfo(int relativeX, int relativeY) {
        SelectSingleSpanInfo selectSingleSpanInfo = null;
        if (pageList != null) {
            PageInfo po = null;
            for (PageInfo pageInfo : pageList) {
                if (pageInfo.isInner(relativeX, relativeY)) {
                    po = pageInfo;
                    break;
                }
            }
            if (po != null) {
                List<LinesInfo> linesInfoList = po.data;
                LinesInfo lo = null;
                for (LinesInfo linesInfo : linesInfoList) {
                    if ((linesInfo.y + po.y) > relativeY) {
                        if (lo == null) {
                            lo = linesInfoList.get(0);
                        }
                        break;
                    }
                    lo = linesInfo;
                }
                if (lo != null) {
                    if (analysisInfo != null) {
                        measureWordHeight(0, mCursorPaint, lo.index);
                    }
                    SpanInfo ci = null;
                    for (SpanInfo spanInfo : lo.data) {
                        if (spanInfo.x > relativeX) {
                            if (ci == null) {
                                ci = lo.data.get(0);
                            }
                            break;
                        }
                        ci = spanInfo;
                    }
                    if (ci != null) {
                        float[] measureWidth = new float[1];
                        if (analysisInfo.charList.get(ci.index) instanceof char[]) {
                            char[] chars = (char[]) analysisInfo.charList.get(ci.index);
                            int offset = mCursorPaint.breakText(chars,
                                    ci.start, ci.offset, relativeX - ci.x, measureWidth);
                            if (offset > 0) {
                                if (relativeX - ci.x > measureWidth[0]) {
                                    if (ci.offset >= offset + 1) {
                                        offset++;
                                    }
                                }
                                selectSingleSpanInfo = new SelectSingleSpanInfo();
                                selectSingleSpanInfo.charsIndex = ci.index;
                                selectSingleSpanInfo.relativeLineIndex = po.data.indexOf(lo);
                                selectSingleSpanInfo.pageIndex = po.index;
                                selectSingleSpanInfo.start = ci.start + offset - 1;
                                selectSingleSpanInfo.offset = 1;
                                float charWidth = mCursorPaint.measureText(chars, ci.start + offset - 1, 1);
                                float charHeight = mCursorPaint.getFontMetrics().bottom - mCursorPaint.getFontMetrics().top;
                                float charX = getPaddingLeft();
                                if (offset > 1) {
                                    charX += ci.x + mCursorPaint.measureText(chars, ci.start, offset - 1);
                                } else {
                                    charX += ci.x;
                                }
                                float charY = lo.y + po.y;
                                selectSingleSpanInfo.singleCharRectF = new RectF(charX, charY, charX + charWidth, charY + charHeight);
                                selectSingleSpanInfo.singleCharRectF.offset(0, getItemGroup().getPaddingTop());

                                if (blnCanCursorVisible) {
                                    if ((relativeX - ci.x - measureWidth[0]) > charWidth / 2) {
                                        selectSingleSpanInfo.cursorRelativeX = ci.x + mCursorPaint.measureText(chars, ci.start, offset);
                                    } else {
                                        if (offset > 1) {
                                            selectSingleSpanInfo.cursorRelativeX = ci.x + mCursorPaint.measureText(chars, ci.start, offset - 1);
                                        } else {
                                            selectSingleSpanInfo.cursorRelativeX = ci.x;
                                        }
                                    }
                                }
                            } else {
                                selectSingleSpanInfo = new SelectSingleSpanInfo();
                                selectSingleSpanInfo.charsIndex = ci.index;
                                selectSingleSpanInfo.relativeLineIndex = po.data.indexOf(lo);
                                selectSingleSpanInfo.pageIndex = po.index;
                                selectSingleSpanInfo.start = ci.start;
                                selectSingleSpanInfo.offset = 1;
                                float charWidth = mCursorPaint.measureText(chars, ci.start, 1);
                                float charHeight = mCursorPaint.getFontMetrics().bottom - mCursorPaint.getFontMetrics().top;
                                float charX = getPaddingLeft() + ci.x;
                                float charY = lo.y + po.y;
                                selectSingleSpanInfo.singleCharRectF = new RectF(charX, charY, charX + charWidth, charY + charHeight);
                                selectSingleSpanInfo.singleCharRectF.offset(0, getItemGroup().getPaddingTop());

                                if (blnCanCursorVisible) {
                                    if ((relativeX - ci.x) > charWidth / 2) {
                                        selectSingleSpanInfo.cursorRelativeX = ci.x + mCursorPaint.measureText(chars, ci.start, 1);
                                    } else {
                                        selectSingleSpanInfo.cursorRelativeX = ci.x;
                                    }
                                }
                            }
                        } else if (analysisInfo.charList.get(ci.index) instanceof Character) {
                            Character character = (Character) analysisInfo.charList.get(ci.index);
                            selectSingleSpanInfo = new SelectSingleSpanInfo();
                            selectSingleSpanInfo.charsIndex = ci.index;
                            selectSingleSpanInfo.relativeLineIndex = po.data.indexOf(lo);
                            selectSingleSpanInfo.pageIndex = po.index;
                            float charWidth = mCursorPaint.measureText(character + "", ci.start, 1);
                            float charHeight = mCursorPaint.getFontMetrics().bottom - mCursorPaint.getFontMetrics().top;
                            float charX = getPaddingLeft() + ci.x;
                            float charY = lo.y + po.y;
                            selectSingleSpanInfo.singleCharRectF = new RectF(charX, charY, charX + charWidth, charY + charHeight);
                            selectSingleSpanInfo.singleCharRectF.offset(0, getItemGroup().getPaddingTop());

                            if (blnCanCursorVisible) {
                                if ((relativeX - ci.x) > charWidth / 2) {
                                    selectSingleSpanInfo.cursorRelativeX = ci.x + charWidth;
                                } else {
                                    selectSingleSpanInfo.cursorRelativeX = ci.x;
                                }
                            }
                        }
                    }
                }
            }
        }

        return selectSingleSpanInfo;
    }

    private SelectSingleSpanInfo getSelectCharsInfoPrecise(int relativeX, int relativeY) {
        SelectSingleSpanInfo selectSingleSpanInfo = null;
        if (pageList != null) {
            PageInfo po = null;
            for (PageInfo pageInfo : pageList) {
                if (pageInfo.isInner(relativeX, relativeY)) {
                    po = pageInfo;
                    break;
                }
            }
            if (po != null) {
                List<LinesInfo> linesInfoList = po.data;
                LinesInfo lo = null;
                for (LinesInfo linesInfo : linesInfoList) {
                    if ((linesInfo.y + po.y) > relativeY) {
                        if (lo == null) {
                            lo = linesInfoList.get(0);
                        }
                        break;
                    }
                    lo = linesInfo;
                }
                if (lo != null) {
                    if (analysisInfo != null) {
                        measureWordHeight(0, mCursorPaint, lo.index);
                    }
                    SpanInfo ci = null;
                    for (SpanInfo spanInfo : lo.data) {
                        if (spanInfo.x > relativeX) {
                            if (ci == null) {
                                ci = lo.data.get(0);
                            }
                            break;
                        }
                        ci = spanInfo;
                    }
                    if (ci != null) {
                        float[] measureWidth = new float[1];
                        if (analysisInfo.charList.get(ci.index) instanceof char[]) {
                            char[] chars = (char[]) analysisInfo.charList.get(ci.index);
                            int offset = mCursorPaint.breakText(chars,
                                    ci.start, ci.offset, relativeX - ci.x, measureWidth);
                            if (offset > 0) {
                                if (relativeX - ci.x > measureWidth[0]) {
                                    if (ci.offset == offset) {
                                        return null;
                                    }
                                    if (ci.offset >= offset + 1) {
                                        offset++;
                                    }
                                }
                                selectSingleSpanInfo = new SelectSingleSpanInfo();
                                selectSingleSpanInfo.charsIndex = ci.index;
                                selectSingleSpanInfo.relativeLineIndex = po.data.indexOf(lo);
                                selectSingleSpanInfo.pageIndex = po.index;
                                selectSingleSpanInfo.start = ci.start + offset - 1;
                                selectSingleSpanInfo.offset = 1;
                                float charWidth = mCursorPaint.measureText(chars, ci.start + offset - 1, 1);
                                float charHeight = mCursorPaint.getFontMetrics().bottom - mCursorPaint.getFontMetrics().top;
                                float charX = getPaddingLeft();
                                if (offset > 1) {
                                    charX += ci.x + mCursorPaint.measureText(chars, ci.start, offset - 1);
                                } else {
                                    charX += ci.x;
                                }
                                float charY = lo.y + po.y;
                                selectSingleSpanInfo.singleCharRectF = new RectF(charX, charY, charX + charWidth, charY + charHeight);
                                selectSingleSpanInfo.singleCharRectF.offset(0, getItemGroup().getPaddingTop());

                                if (blnCanCursorVisible) {
                                    if ((relativeX - ci.x - measureWidth[0]) > charWidth / 2) {
                                        selectSingleSpanInfo.cursorRelativeX = ci.x + mCursorPaint.measureText(chars, ci.start, offset);
                                    } else {
                                        if (offset > 1) {
                                            selectSingleSpanInfo.cursorRelativeX = ci.x + mCursorPaint.measureText(chars, ci.start, offset - 1);
                                        } else {
                                            selectSingleSpanInfo.cursorRelativeX = ci.x;
                                        }
                                    }
                                }
                            } else {
                                selectSingleSpanInfo = new SelectSingleSpanInfo();
                                selectSingleSpanInfo.charsIndex = ci.index;
                                selectSingleSpanInfo.relativeLineIndex = po.data.indexOf(lo);
                                selectSingleSpanInfo.pageIndex = po.index;
                                selectSingleSpanInfo.start = ci.start;
                                selectSingleSpanInfo.offset = 1;
                                float charWidth = mCursorPaint.measureText(chars, ci.start, 1);
                                float charHeight = mCursorPaint.getFontMetrics().bottom - mCursorPaint.getFontMetrics().top;
                                float charX = getPaddingLeft() + ci.x;
                                float charY = lo.y + po.y;
                                selectSingleSpanInfo.singleCharRectF = new RectF(charX, charY, charX + charWidth, charY + charHeight);
                                selectSingleSpanInfo.singleCharRectF.offset(0, getItemGroup().getPaddingTop());

                                if (blnCanCursorVisible) {
                                    if ((relativeX - ci.x) > charWidth / 2) {
                                        selectSingleSpanInfo.cursorRelativeX = ci.x + mCursorPaint.measureText(chars, ci.start, 1);
                                    } else {
                                        selectSingleSpanInfo.cursorRelativeX = ci.x;
                                    }
                                }
                            }
                        } else if (analysisInfo.charList.get(ci.index) instanceof Character) {
                            Character character = (Character) analysisInfo.charList.get(ci.index);
                            selectSingleSpanInfo = new SelectSingleSpanInfo();
                            selectSingleSpanInfo.charsIndex = ci.index;
                            selectSingleSpanInfo.relativeLineIndex = po.data.indexOf(lo);
                            selectSingleSpanInfo.pageIndex = po.index;
                            float charWidth = mCursorPaint.measureText(character + "", ci.start, 1);
                            if (relativeX > (ci.x + charWidth)) {
                                return null;
                            }
                            float charHeight = mCursorPaint.getFontMetrics().bottom - mCursorPaint.getFontMetrics().top;
                            float charX = getPaddingLeft() + ci.x;
                            float charY = lo.y + po.y;
                            selectSingleSpanInfo.singleCharRectF = new RectF(charX, charY, charX + charWidth, charY + charHeight);
                            selectSingleSpanInfo.singleCharRectF.offset(0, getItemGroup().getPaddingTop());

                            if (blnCanCursorVisible) {
                                if ((relativeX - ci.x) > charWidth / 2) {
                                    selectSingleSpanInfo.cursorRelativeX = ci.x + charWidth;
                                } else {
                                    selectSingleSpanInfo.cursorRelativeX = ci.x;
                                }
                            }
                        }
                    }
                }
            }
        }

        return selectSingleSpanInfo;
    }

    private SelectSingleSpanInfo[] getSelectCharsInfoArray(int relativeX, int relativeY) {
        SelectSingleSpanInfo[] selectSingleSpanInfoArray = null;
        if (pageList != null) {
            PageInfo po = null;
            for (PageInfo pageInfo : pageList) {
                if (pageInfo.isInner(relativeX, relativeY)) {
                    po = pageInfo;
                    break;
                }
            }
            if (po != null) {
                List<LinesInfo> linesInfoList = po.data;
                LinesInfo lo = null;
                for (LinesInfo linesInfo : linesInfoList) {
                    if ((linesInfo.y + po.y) > relativeY) {
                        if (lo == null) {
                            lo = linesInfoList.get(0);
                        }
                        break;
                    }
                    lo = linesInfo;
                }
                if (lo != null) {
                    if (analysisInfo != null) {
                        measureWordHeight(0, mCursorPaint, lo.index);
                    }
                    SpanInfo ci = null;
                    for (SpanInfo spanInfo : lo.data) {
                        if (spanInfo.x > relativeX) {
                            if (ci == null) {
                                ci = lo.data.get(0);
                            }
                            break;
                        }
                        ci = spanInfo;
                    }
                    if (ci != null) {
                        float[] measureWidth = new float[1];
                        if (analysisInfo.charList.get(ci.index) instanceof char[]) {
                            char[] chars = (char[]) analysisInfo.charList.get(ci.index);
                            int offset = mCursorPaint.breakText(chars,
                                    ci.start, ci.offset, relativeX - ci.x, measureWidth);
                            if (offset > 0) {
                                if (relativeX - ci.x > measureWidth[0]) {
                                    if (ci.offset == offset) {
                                        return null;
                                    }
                                    if (ci.offset >= offset + 1) {
                                        offset++;
                                    }
                                }
                                selectSingleSpanInfoArray = calculateSelectSingleSpanInfo(chars, ci, po, lo, measureWidth, relativeX, offset);
                            } else {
                                selectSingleSpanInfoArray = calculateSelectSingleSpanInfo(chars, ci, po, lo, measureWidth, relativeX, 1);
//                                getSelectSingleSpanInfo2(chars, ci, po, lo, relativeX);
                            }
                        } else if (analysisInfo.charList.get(ci.index) instanceof Character) {
                            selectSingleSpanInfoArray = new SelectSingleSpanInfo[2];
                            selectSingleSpanInfoArray[0] = getSelectSingleSpanInfo3(ci, po, lo, relativeX);
                            selectSingleSpanInfoArray[1] = selectSingleSpanInfoArray[0];
                        }
                    }
                }
            }
        }

        return selectSingleSpanInfoArray;
    }

    private SelectSingleSpanInfo[] calculateSelectSingleSpanInfo(char[] chars, SpanInfo ci, PageInfo po, LinesInfo lo, float[] measureWidth, int relativeX, int offset) {
        SelectSingleSpanInfo[] selectSingleSpanInfoArray = new SelectSingleSpanInfo[2];
        char[] startChars = chars;
        char[] endChars = chars;
        SpanInfo startSpanInfo = ci;
        SpanInfo endSpanInfo = ci;
        int startOffset = offset - 1;
        for (int i = startOffset; i >= 0 ; i--) {
            if (!isEnglishChar(startChars[startSpanInfo.start + i])) {
                if (startOffset > i) {
                    startOffset = i + 1;
                }
                break;
            }
            if (i == 0) {
                startOffset = 0;
                int index = lo.data.indexOf(startSpanInfo);
                if (index > 0) {
                    SpanInfo tempSpanInfo = lo.data.get(index - 1);
                    char[] tempChars = (char[]) analysisInfo.charList.get(tempSpanInfo.index);
                    int tempOffset = tempSpanInfo.offset;
                    if (tempOffset > 0) {
                        tempOffset--;
                    }
                    if (isEnglishChar(tempChars[tempSpanInfo.start + tempOffset])) {
                        startSpanInfo = tempSpanInfo;
                        i = tempOffset + 1;
                        startOffset = tempOffset;
                        startChars = tempChars;
                    }
                }
            }
        }
        int endOffset = offset - 1;
        for (int j = endOffset; endSpanInfo.offset > j ; j++) {
            if (!isEnglishChar(endChars[endSpanInfo.start + j])) {
                if (j > endOffset) {
                    endOffset = j - 1;
                }
                break;
            }
            if (j == (endSpanInfo.offset - 1)) {
                endOffset = (endSpanInfo.offset - 1);
                int index = lo.data.indexOf(endSpanInfo);
                if ((lo.data.size() - 1) > index) {
                    SpanInfo tempSpanInfo = lo.data.get(index + 1);
                    char[] tempChars = (char[]) analysisInfo.charList.get(tempSpanInfo.index);
                    if (isEnglishChar(tempChars[tempSpanInfo.start])) {
                        endSpanInfo = tempSpanInfo;
                        j = 0 - 1;
                        endOffset = 0;
                        endChars = tempChars;
                    }
                }
            }
        }
        if (startSpanInfo.offset >= startOffset + 1) {
            startOffset++;
        }
        if (endSpanInfo.offset >= endOffset + 1) {
            endOffset++;
        }
        if (startSpanInfo == ci && endSpanInfo == ci) {
            if (startOffset == endOffset) {
                SelectSingleSpanInfo selectSingleSpanInfo = getSelectSingleSpanInfo1(chars, ci, po, lo, measureWidth, relativeX, offset);
                selectSingleSpanInfoArray[0] = selectSingleSpanInfo;
                selectSingleSpanInfoArray[1] = selectSingleSpanInfo;
            } else {
                selectSingleSpanInfoArray[0] = getSelectSingleSpanInfo1(startChars, startSpanInfo, po, lo, measureWidth, relativeX, startOffset);
                selectSingleSpanInfoArray[1] = getSelectSingleSpanInfo1(endChars, endSpanInfo, po, lo, measureWidth, relativeX, endOffset);
            }
        } else {
            selectSingleSpanInfoArray[0] = getSelectSingleSpanInfo1(startChars, startSpanInfo, po, lo, measureWidth, relativeX, startOffset);
            selectSingleSpanInfoArray[1] = getSelectSingleSpanInfo1(endChars, endSpanInfo, po, lo, measureWidth, relativeX, endOffset);
        }
        return selectSingleSpanInfoArray;
    }

    private SelectSingleSpanInfo getSelectSingleSpanInfo1(char[] chars, SpanInfo ci, PageInfo po, LinesInfo lo, float[] measureWidth, int relativeX, int offset) {
        SelectSingleSpanInfo selectSingleSpanInfo = new SelectSingleSpanInfo();
        selectSingleSpanInfo.charsIndex = ci.index;
        selectSingleSpanInfo.relativeLineIndex = po.data.indexOf(lo);
        selectSingleSpanInfo.pageIndex = po.index;
        selectSingleSpanInfo.start = ci.start + offset - 1;
        selectSingleSpanInfo.offset = 1;
        float charWidth = mCursorPaint.measureText(chars, ci.start + offset - 1, 1);
        float charHeight = mCursorPaint.getFontMetrics().bottom - mCursorPaint.getFontMetrics().top;
        float charX = getPaddingLeft();
        if (offset > 1) {
            charX += ci.x + mCursorPaint.measureText(chars, ci.start, offset - 1);
        } else {
            charX += ci.x;
        }
        float charY = lo.y + po.y;
        selectSingleSpanInfo.singleCharRectF = new RectF(charX, charY, charX + charWidth, charY + charHeight);
        selectSingleSpanInfo.singleCharRectF.offset(0, getItemGroup().getPaddingTop());

        if (blnCanCursorVisible) {
            if ((relativeX - ci.x - measureWidth[0]) > charWidth / 2) {
                selectSingleSpanInfo.cursorRelativeX = ci.x + mCursorPaint.measureText(chars, ci.start, offset);
            } else {
                if (offset > 1) {
                    selectSingleSpanInfo.cursorRelativeX = ci.x + mCursorPaint.measureText(chars, ci.start, offset - 1);
                } else {
                    selectSingleSpanInfo.cursorRelativeX = ci.x;
                }
            }
        }
        return selectSingleSpanInfo;
    }

    private SelectSingleSpanInfo getSelectSingleSpanInfo2(char[] chars, SpanInfo ci, PageInfo po, LinesInfo lo, int relativeX) {
        SelectSingleSpanInfo selectSingleSpanInfo = new SelectSingleSpanInfo();
        selectSingleSpanInfo.charsIndex = ci.index;
        selectSingleSpanInfo.relativeLineIndex = po.data.indexOf(lo);
        selectSingleSpanInfo.pageIndex = po.index;
        selectSingleSpanInfo.start = ci.start;
        selectSingleSpanInfo.offset = 1;
        float charWidth = mCursorPaint.measureText(chars, ci.start, 1);
        float charHeight = mCursorPaint.getFontMetrics().bottom - mCursorPaint.getFontMetrics().top;
        float charX = getPaddingLeft() + ci.x;
        float charY = lo.y + po.y;
        selectSingleSpanInfo.singleCharRectF = new RectF(charX, charY, charX + charWidth, charY + charHeight);
        selectSingleSpanInfo.singleCharRectF.offset(0, getItemGroup().getPaddingTop());

        if (blnCanCursorVisible) {
            if ((relativeX - ci.x) > charWidth / 2) {
                selectSingleSpanInfo.cursorRelativeX = ci.x + mCursorPaint.measureText(chars, ci.start, 1);
            } else {
                selectSingleSpanInfo.cursorRelativeX = ci.x;
            }
        }
        return selectSingleSpanInfo;
    }

    private SelectSingleSpanInfo getSelectSingleSpanInfo3(SpanInfo ci, PageInfo po, LinesInfo lo, int relativeX) {
        Character character = (Character) analysisInfo.charList.get(ci.index);
        SelectSingleSpanInfo selectSingleSpanInfo = new SelectSingleSpanInfo();
        selectSingleSpanInfo.charsIndex = ci.index;
        selectSingleSpanInfo.relativeLineIndex = po.data.indexOf(lo);
        selectSingleSpanInfo.pageIndex = po.index;
        float charWidth = mCursorPaint.measureText(character + "", ci.start, 1);
        if (relativeX > (ci.x + charWidth)) {
            return null;
        }
        float charHeight = mCursorPaint.getFontMetrics().bottom - mCursorPaint.getFontMetrics().top;
        float charX = getPaddingLeft() + ci.x;
        float charY = lo.y + po.y;
        selectSingleSpanInfo.singleCharRectF = new RectF(charX, charY, charX + charWidth, charY + charHeight);
        selectSingleSpanInfo.singleCharRectF.offset(0, getItemGroup().getPaddingTop());

        if (blnCanCursorVisible) {
            if ((relativeX - ci.x) > charWidth / 2) {
                selectSingleSpanInfo.cursorRelativeX = ci.x + charWidth;
            } else {
                selectSingleSpanInfo.cursorRelativeX = ci.x;
            }
        }
        return selectSingleSpanInfo;
    }

    private boolean isEnglishChar(char c) {
        if ((c >= 'a' && 'z' >= c)
                || (c >= 'A' && 'Z' >= c)
                || PhoneticUtils.isHasChange(c)) {
            return true;
        }
        return false;
    }

    private void computeSelectRegion(int state, SelectSingleSpanInfo downSelectSingleSpanInfo, SelectSingleSpanInfo moveSelectSingleSpanInfo) {

        if (singleExecutor != null) {
            CaculateSelectionRegion caculateSelectionRegion = new CaculateSelectionRegion(state, downSelectSingleSpanInfo, moveSelectSingleSpanInfo);
            try {
                singleExecutor.execute(caculateSelectionRegion);
            } catch (RejectedExecutionException e1) {
                e1.printStackTrace();
            } catch (NullPointerException e2) {
                e2.printStackTrace();
            }
        }
    }

    @Override
    public void draw(Canvas canvas, Integer integer) {
        if (analysisInfo != null && analysisInfo.charList != null) {
            List<Object> objectLists = analysisInfo.charList;
            if (pageList == null || 0 >= pageList.size()) {
                return;
            }
            PageInfo pageInfo = pageList.get(integer);
            if (pageInfo != null && pageInfo.data != null) {
                for (LinesInfo linesInfo : pageInfo.data) {
                    if (analysisInfo != null) {
                        measureWordHeight(0, mTextPaint, linesInfo.index);
                    }
                    for (SpanInfo spanInfo : linesInfo.data) {
                        if (analysisInfo.colorMap.get(spanInfo.index) == null) {
                            if (mTextPaint.getColor() != analysisInfo.colorMap.get(-1)) {
                                mTextPaint.setColor(analysisInfo.colorMap.get(-1));
                            }
                        } else if (mTextPaint.getColor() != analysisInfo.colorMap.get(spanInfo.index)) {
                            mTextPaint.setColor(analysisInfo.colorMap.get(spanInfo.index));
                        }
                        if (objectLists.get(spanInfo.index) instanceof char[]) {
                            canvas.drawText((char[]) objectLists.get(spanInfo.index), spanInfo.start,
                                    spanInfo.offset, spanInfo.x, linesInfo.y - mTextPaint.getFontMetrics().top, mTextPaint);
                        } else if (objectLists.get(spanInfo.index) instanceof Character) {
                            canvas.drawText(objectLists.get(spanInfo.index) + "", spanInfo.start,
                                    spanInfo.offset, spanInfo.x, linesInfo.y - mTextPaint.getFontMetrics().top, mTextPaint);
                        }
                    }
                }
            }
        }
    }

    private float measureWordHeight(float wordHeight, Paint paint, int lineIndex) {
        if (resetPaint(paint, lineIndex)) {
            wordHeight = paint.getFontMetrics().bottom - paint.getFontMetrics().top;
        }
        return wordHeight;
    }

    private boolean resetPaint(Paint paint, int lineIndex) {
        boolean result = false;
        if (analysisInfo != null) {
            Map<Integer, Integer> textSizeMap = analysisInfo.textSizeMap;
            if (textSizeMap != null && paint != null) {
                Integer textSize = textSizeMap.get(lineIndex);
                Integer defaultTextSize = textSizeMap.get(-1);
                if (textSize != null && (int) paint.getTextSize() != textSize) {
                    paint.setTextSize(textSize);
                    result = true;
                } else if (textSize != null && (int) paint.getTextSize() == textSize) {
                    result = false;
                } else if (defaultTextSize != null && (int) paint.getTextSize() != defaultTextSize) {
                    paint.setTextSize(textSizeMap.get(-1));
                    result = true;
                }
            }
        }
        return result;
    }

    private String currentParaseTag;

    public static class TextHandler extends BaseHandler<TextScrollView> implements ISpanAnalysisObtainer {

        public final static int ANALYSIS_TEXT_FINISH = 0x001;
        public final static int PARARSE_START = ANALYSIS_TEXT_FINISH + 1;
        public final static int PARARSE_CHARS = PARARSE_START + 1;
        public final static int DRAW_SELECT_BACKGROUND = PARARSE_CHARS + 1;
        public final static int DRAW_SELECT_WORLD_START = DRAW_SELECT_BACKGROUND + 1;
        public final static int DRAW_SELECT_WORLD_END = DRAW_SELECT_WORLD_START + 1;
        public final static int PARARSE_END = DRAW_SELECT_WORLD_END + 1;

        private final static int MAX_CACHE_HEIGHT = 2000;

        private ParaseWorker paraseWorker;
        private List<PageInfo> cachePageInfoList;

        TextHandler(TextScrollView mTextScrollView) {
            super(mTextScrollView);
            cachePageInfoList = new ArrayList<PageInfo>();
        }

        @Override
        public void getResult(AnalysisInfo analysisInfo) {
            sendMessages(ANALYSIS_TEXT_FINISH, analysisInfo, 0, 0, 0);
        }

        @Override
        public void handleMessage(Message msg) {
            if (getReference() != null) {
                final TextScrollView mTextScrollView = getReference().get();
                switch (msg.what) {
                    case ANALYSIS_TEXT_FINISH:
                        if (mTextScrollView != null && msg.obj instanceof AnalysisInfo) {
                            if (paraseWorker != null) {
                                paraseWorker.setTag(null);
                            }
                            getReference().get().clear();
                            sendMessages(PARARSE_START, msg.obj, 0, 0, 0);
                        }
                        break;

                    case PARARSE_START:
                        if (mTextScrollView != null && msg.obj instanceof AnalysisInfo) {
                            if (paraseWorker != null) {
                                if (paraseWorker.getParase() != null) {
                                    paraseWorker.getParase().setCharsParaseObtainer(null);
                                }
                                paraseWorker.setListener(null);
                            }

                            AnalysisInfo analysisInfo = (AnalysisInfo) msg.obj;
                            analysisInfo.textSizeMap = new HashMap<Integer, Integer>();
                            analysisInfo.textSizeMap.put(-1, mTextScrollView.textSize);
                            if (mTextScrollView.changeLine != -1 && mTextScrollView.changeTextSize > 0) {
                                analysisInfo.textSizeMap.put(mTextScrollView.changeLine, mTextScrollView.changeTextSize);
                            }
                            IParaser spanParase = analysisInfo.createParaser();
                            Typeface typeface = analysisInfo.getPaintTypeface();
                            if (typeface != null) {
                                if (mTextScrollView.mTextPaint  != null) {
                                    mTextScrollView.mTextPaint.setTypeface(typeface);
                                }
                                if (mTextScrollView.mCursorPaint != null) {
                                    mTextScrollView.mCursorPaint.setTypeface(typeface);
                                }
                            }
                            if (spanParase != null) {
                                spanParase.init(mTextScrollView.getOriginWidth(), mTextScrollView.getOriginHeight() + 0.0f);
                                paraseWorker = new ParaseWorker(spanParase);
                                paraseWorker.setTag(analysisInfo);
                                paraseWorker.setListener(this);
                                paraseWorker.start();
                                if (cachePageInfoList != null) {
                                    cachePageInfoList.clear();
                                }
                                if (mTextScrollView.onTextScrollViewListener != null) {
                                    mTextScrollView.onTextScrollViewListener.startAnalyTextPage();
                                }
                            }
                        }
                        break;

                    case PARARSE_CHARS:
                        List<LinesInfo> data = (List<LinesInfo>) msg.obj;
                        if (mTextScrollView != null) {

                            if (msg.getData() != null) {
                                String tag = msg.getData().getString(BUNDLE_DEFAULT_STRING_KEY);
                                if (tag != null && paraseWorker != null
                                        && paraseWorker.getTag() instanceof AnalysisInfo
                                        && tag.contentEquals(paraseWorker.toString())) {
                                    if (data != null) {
                                        if (mTextScrollView.pageList.size() == 0
                                                || mTextScrollView.currentParaseTag == null
                                                || !mTextScrollView.currentParaseTag.contentEquals(tag)) {                                mTextScrollView.reset();
                                            mTextScrollView.reset();
                                            mTextScrollView.clearCacheItemsView();
                                            mTextScrollView.scrollTo(0, 0);
                                            mTextScrollView.analysisInfo = (AnalysisInfo) paraseWorker.getTag();
                                            mTextScrollView.currentParaseTag = tag;
                                        }
//                                        if (MAX_CACHE_HEIGHT >= mTextScrollView.contentHeight) {
                                            mTextScrollView.contentHeight += msg.arg1;
                                            PageInfo pageInfo = mTextScrollView.new PageInfo();
                                            pageInfo.data = data;
                                            pageInfo.height = msg.arg1;
                                            pageInfo.width = msg.arg2;
                                            if (mTextScrollView.pageList.size() > 0) {
                                                pageInfo.y = mTextScrollView.pageList.get(mTextScrollView.pageList.size() - 1).y
                                                        + mTextScrollView.pageList.get(mTextScrollView.pageList.size() - 1).height;
                                            }
                                            pageInfo.index = mTextScrollView.pageList.size();
                                            mTextScrollView.pageList.add(pageInfo);
                                            if (mTextScrollView.getAdpater() == null) {
                                                mTextScrollView.setAdapter(mTextScrollView.adapter);
                                            } else {
                                                mTextScrollView.getAdpater().notifyDataChange();
                                            }
//                                        } else {
//                                            PageInfo pageInfo = mTextScrollView.new PageInfo();
//                                            pageInfo.data = data;
//                                            pageInfo.height = msg.arg1;
//                                            pageInfo.width = msg.arg2;
//                                            pageInfo.index = mTextScrollView.pageList.size() + cachePageInfoList.size();
//                                            if (cachePageInfoList.size() > 0) {
//                                                pageInfo.y = cachePageInfoList.get(cachePageInfoList.size() - 1).y
//                                                        + cachePageInfoList.get(cachePageInfoList.size() - 1).height;
//                                            } else if (mTextScrollView.pageList.size() > 0) {
//                                                pageInfo.y = mTextScrollView.pageList.get(mTextScrollView.pageList.size() - 1).y
//                                                        + mTextScrollView.pageList.get(mTextScrollView.pageList.size() - 1).height;
//                                            }
//                                            cachePageInfoList.add(pageInfo);
//                                        }

                                        if (mTextScrollView.onTextScrollViewListener != null) {
                                            mTextScrollView.onTextScrollViewListener.loadingTextPage();
                                        }
                                    }
                                    if (msg.arg1 == SpanParase.UN_INVALUE
                                            && msg.arg2 == SpanParase.UN_INVALUE && mTextScrollView.hasTargetPosition) {
                                        mTextScrollView.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                if (mTextScrollView != null) {
                                                    mTextScrollView.scrollTo(mTextScrollView.targetX, mTextScrollView.targetY);
                                                    mTextScrollView.targetX = 0;
                                                    mTextScrollView.targetY = 0;
                                                    mTextScrollView.hasTargetPosition = false;
                                                }
                                            }
                                        });
                                    }
                                    return;
                                }
                            }
                        }
                        if (data != null) {
                            data.clear();
                        }
                        break;

                    case PARARSE_END:

                        if (mTextScrollView != null
                                && msg.getData() != null) {
                            String tag = msg.getData().getString(BUNDLE_DEFAULT_STRING_KEY);
                            if (tag != null && paraseWorker != null
                                    && paraseWorker.getTag() instanceof AnalysisInfo
                                    && tag.contentEquals(paraseWorker.toString())) {
                                if (cachePageInfoList != null) {
                                    for (PageInfo pageInfo : cachePageInfoList) {
                                        mTextScrollView.contentHeight += pageInfo.height;
                                        mTextScrollView.pageList.add(pageInfo);
                                    }
                                }
                                if (mTextScrollView.getAdpater() != null) {
                                    mTextScrollView.getAdpater().notifyDataChange();
                                }
                            }
                        }
                        if (cachePageInfoList != null) {
                            cachePageInfoList.clear();
                        }
                        break;

                    case DRAW_SELECT_BACKGROUND:
                        if (mTextScrollView != null &&
                                msg.obj instanceof RectF[]) {
                            mTextScrollView.selectBackgroundRectFArray = (RectF[]) msg.obj;
                            mTextScrollView.invalidate();
                        }
                        break;

                    case DRAW_SELECT_WORLD_START:
                        if (mTextScrollView != null && mTextScrollView.onTextScrollViewListener != null) {
                            mTextScrollView.onTextScrollViewListener.startSelectText();
                        }
                        break;

                    case DRAW_SELECT_WORLD_END:
                        if (mTextScrollView != null && mTextScrollView.onTextScrollViewListener != null) {
                            RectF[] rectFArray = (RectF[]) msg.getData().getParcelableArray(BaseHandler.BUNDLE_DEFAULT_PARCELABLEARRAY_KEY);
                            if (msg.obj instanceof String) {
                                if (rectFArray != null && rectFArray.length == 2) {

                                    mTextScrollView.onTextScrollViewListener.endSelectText((String) msg.obj, rectFArray[0], rectFArray[1]);
                                } else {
                                    mTextScrollView.onTextScrollViewListener.endSelectText((String) msg.obj, null, null);
                                }
                            } else {
                                mTextScrollView.onTextScrollViewListener.endSelectText(null, null, null);
                            }
                        }
                        break;
                }
            }
        }
    }

    class CaculateSelectionRegion implements Runnable {

        final static int STATE_SELECT_BACKGROUND = 0x01;
        final static int STATE_SELECT_WORLD = 0x02;
        final static int STATE_SINGLE_DOWN_SELECTED = 0x03;
        SelectSingleSpanInfo rStartSelectSingleSpanInfo;
        SelectSingleSpanInfo rEndSelectSingleSpanInfo;
        int selectState;

        /*CaculateSelectionRegion(SelectSingleSpanInfo rStartSelectSingleSpanInfo, SelectSingleSpanInfo rEndSelectSingleSpanInfo) {
            this.rStartSelectSingleSpanInfo = rStartSelectSingleSpanInfo;
            this.rEndSelectSingleSpanInfo = rEndSelectSingleSpanInfo;
        }*/

        CaculateSelectionRegion(int selectState, SelectSingleSpanInfo rStartSelectSingleSpanInfo, SelectSingleSpanInfo rEndSelectSingleSpanInfo) {
            this.selectState = selectState;
            this.rStartSelectSingleSpanInfo = rStartSelectSingleSpanInfo;
            this.rEndSelectSingleSpanInfo = rEndSelectSingleSpanInfo;
        }

        @Override
        public void run() {
            switch (selectState) {
                case STATE_SELECT_BACKGROUND:
                    selectedBackground();
                    break;

                case STATE_SELECT_WORLD:
                    String selectStr = getSelectString();
                    if (selectStr != null && selectStr.trim().length() > 0) {
                        selectedWord(selectStr);
                    }
                    break;

                case STATE_SINGLE_DOWN_SELECTED:
                    String selectedStr = getSelectString();
                    if (selectedStr != null && selectedStr.trim().length() > 0) {
                        selectedBackground();
                        selectedWord(selectedStr);
                    }
                    break;
            }
            rStartSelectSingleSpanInfo = null;
            rEndSelectSingleSpanInfo = null;
        }

        private void selectedBackground() {
            RectF[] rectFArray = selecetBackground();
            if (mHandler != null) {
                mHandler.sendMessages(TextHandler.DRAW_SELECT_BACKGROUND, rectFArray, 0, 0, 0);
            }
        }

        private void selectedWord(String str) {
            if (mHandler != null) {
                mHandler.sendEmptyMessage(TextHandler.DRAW_SELECT_WORLD_START);
            }
            if (mHandler != null) {
                /*if (rStartSelectSingleSpanInfo == null || rEndSelectSingleSpanInfo == null) {
                    mHandler.sendMessages(TextHandler.DRAW_SELECT_WORLD_END,
                            new RectF[]{},
                            str, 0, 0, 0);
                } else {
                    mHandler.sendMessages(TextHandler.DRAW_SELECT_WORLD_END,
                            new RectF[]{rStartSelectSingleSpanInfo.singleCharRectF, rEndSelectSingleSpanInfo.singleCharRectF},
                            str, 0, 0, 0);
                }*/
                if (rStartSelectSingleSpanInfo != null && rEndSelectSingleSpanInfo != null) {
                    mHandler.sendMessages(TextHandler.DRAW_SELECT_WORLD_END,
                            new RectF[]{rStartSelectSingleSpanInfo.singleCharRectF, rEndSelectSingleSpanInfo.singleCharRectF},
                            str, 0, 0, 0);
                } else if (rStartSelectSingleSpanInfo != null) {
                    mHandler.sendMessages(TextHandler.DRAW_SELECT_WORLD_END,
                            new RectF[]{rStartSelectSingleSpanInfo.singleCharRectF, rStartSelectSingleSpanInfo.singleCharRectF},
                            str, 0, 0, 0);
                }
            }
        }

        RectF[] selecetBackground() {
            RectF[] rectFArray = new RectF[3];
            if (rStartSelectSingleSpanInfo != null && rEndSelectSingleSpanInfo != null) {
                if (rStartSelectSingleSpanInfo.pageIndex > rEndSelectSingleSpanInfo.pageIndex) {
                    RectF firstRectF = new RectF(getPaddingLeft(), rEndSelectSingleSpanInfo.singleCharRectF.bottom,
                            rStartSelectSingleSpanInfo.singleCharRectF.right, rStartSelectSingleSpanInfo.singleCharRectF.bottom);
                    RectF sencondRecF = new RectF(rEndSelectSingleSpanInfo.singleCharRectF.left, rEndSelectSingleSpanInfo.singleCharRectF.top,
                            getWidth() - getPaddingRight(), rStartSelectSingleSpanInfo.singleCharRectF.top);
                    rectFArray[0] = firstRectF;
                    rectFArray[1] = sencondRecF;
                    if (sencondRecF.left > firstRectF.right) {
                        RectF thirdRectF = new RectF(firstRectF.right, firstRectF.top, sencondRecF.left, sencondRecF.bottom);
                        rectFArray[2] = thirdRectF;
                    }
                } else if (rEndSelectSingleSpanInfo.pageIndex > rStartSelectSingleSpanInfo.pageIndex) {
                    RectF firstRectF = new RectF(getPaddingLeft(), rStartSelectSingleSpanInfo.singleCharRectF.bottom,
                            rEndSelectSingleSpanInfo.singleCharRectF.right, rEndSelectSingleSpanInfo.singleCharRectF.bottom);
                    RectF sencondRecF = new RectF(rStartSelectSingleSpanInfo.singleCharRectF.left, rStartSelectSingleSpanInfo.singleCharRectF.top,
                            getWidth() - getPaddingRight(), rEndSelectSingleSpanInfo.singleCharRectF.top);
                    rectFArray[0] = firstRectF;
                    rectFArray[1] = sencondRecF;
                    if (sencondRecF.left > firstRectF.right) {
                        RectF thirdRectF = new RectF(firstRectF.right, firstRectF.top, sencondRecF.left, sencondRecF.bottom);
                        rectFArray[2] = thirdRectF;
                    }
                } else {
                    if (rStartSelectSingleSpanInfo.relativeLineIndex > rEndSelectSingleSpanInfo.relativeLineIndex) {
                        RectF firstRectF = new RectF(getPaddingLeft(), rEndSelectSingleSpanInfo.singleCharRectF.bottom,
                                rStartSelectSingleSpanInfo.singleCharRectF.right, rStartSelectSingleSpanInfo.singleCharRectF.bottom);
                        RectF sencondRecF = new RectF(rEndSelectSingleSpanInfo.singleCharRectF.left, rEndSelectSingleSpanInfo.singleCharRectF.top,
                                getWidth() - getPaddingRight(), rStartSelectSingleSpanInfo.singleCharRectF.top);
                        rectFArray[0] = firstRectF;
                        rectFArray[1] = sencondRecF;
                        if (sencondRecF.left > firstRectF.right) {
                            RectF thirdRectF = new RectF(firstRectF.right, firstRectF.top, sencondRecF.left, sencondRecF.bottom);
                            rectFArray[2] = thirdRectF;
                        }
                    } else if (rEndSelectSingleSpanInfo.relativeLineIndex > rStartSelectSingleSpanInfo.relativeLineIndex) {
                        RectF firstRectF = new RectF(getPaddingLeft(), rStartSelectSingleSpanInfo.singleCharRectF.bottom,
                                rEndSelectSingleSpanInfo.singleCharRectF.right, rEndSelectSingleSpanInfo.singleCharRectF.bottom);
                        RectF sencondRecF = new RectF(rStartSelectSingleSpanInfo.singleCharRectF.left, rStartSelectSingleSpanInfo.singleCharRectF.top,
                                getWidth() - getPaddingRight(), rEndSelectSingleSpanInfo.singleCharRectF.top);
                        rectFArray[0] = firstRectF;
                        rectFArray[1] = sencondRecF;
                        if (sencondRecF.left > firstRectF.right) {
                            RectF thirdRectF = new RectF(firstRectF.right, firstRectF.top, sencondRecF.left, sencondRecF.bottom);
                            rectFArray[2] = thirdRectF;
                        }
                    } else {

                        if (rStartSelectSingleSpanInfo.charsIndex > rEndSelectSingleSpanInfo.charsIndex) {
                            RectF rectF = new RectF(rEndSelectSingleSpanInfo.singleCharRectF.left, rEndSelectSingleSpanInfo.singleCharRectF.top,
                                    rStartSelectSingleSpanInfo.singleCharRectF.right, rStartSelectSingleSpanInfo.singleCharRectF.bottom);
                            rectFArray[0] = rectF;
                        } else if (rEndSelectSingleSpanInfo.charsIndex > rStartSelectSingleSpanInfo.charsIndex) {
                            RectF rectF = new RectF(rStartSelectSingleSpanInfo.singleCharRectF.left, rStartSelectSingleSpanInfo.singleCharRectF.top,
                                    rEndSelectSingleSpanInfo.singleCharRectF.right, rEndSelectSingleSpanInfo.singleCharRectF.bottom);
                            rectFArray[0] = rectF;
                        } else {
                            if (rStartSelectSingleSpanInfo.start > rEndSelectSingleSpanInfo.start) {
                                RectF rectF = new RectF(rEndSelectSingleSpanInfo.singleCharRectF.left, rEndSelectSingleSpanInfo.singleCharRectF.top,
                                        rStartSelectSingleSpanInfo.singleCharRectF.right, rStartSelectSingleSpanInfo.singleCharRectF.bottom);
                                rectFArray[0] = rectF;
                            } else if (rEndSelectSingleSpanInfo.start > rStartSelectSingleSpanInfo.start) {
                                RectF rectF = new RectF(rStartSelectSingleSpanInfo.singleCharRectF.left, rStartSelectSingleSpanInfo.singleCharRectF.top,
                                        rEndSelectSingleSpanInfo.singleCharRectF.right, rEndSelectSingleSpanInfo.singleCharRectF.bottom);
                                rectFArray[0] = rectF;
                            } else {
                                RectF rectF = rStartSelectSingleSpanInfo.singleCharRectF;
                                rectFArray[0] = rectF;
                            }
                        }
                    }
                }
            } else if (rStartSelectSingleSpanInfo != null) {
                rectFArray[0] = rStartSelectSingleSpanInfo.singleCharRectF;
                rectFArray[1] = rStartSelectSingleSpanInfo.singleCharRectF;
                rectFArray[2] = rStartSelectSingleSpanInfo.singleCharRectF;
            }
            return rectFArray;
        }

        String getSelectString() {
            String selectStr = null;
            List<Object> charList = null;
            if (analysisInfo != null) {
                charList = analysisInfo.charList;
            }
            if (rStartSelectSingleSpanInfo != null && rEndSelectSingleSpanInfo == null) {
                if (charList != null && charList.get(rStartSelectSingleSpanInfo.charsIndex) instanceof char[]) {
                    selectStr = new String((char[]) charList.get(rStartSelectSingleSpanInfo.charsIndex), rStartSelectSingleSpanInfo.start, 1);
                } else if (charList != null) {
                    selectStr = charList.get(rStartSelectSingleSpanInfo.charsIndex) + "";
                }
            } else if (rStartSelectSingleSpanInfo != null && rEndSelectSingleSpanInfo != null) {
                if (rStartSelectSingleSpanInfo.charsIndex > rEndSelectSingleSpanInfo.charsIndex) {
                    selectStr = getMultiSpanString(rEndSelectSingleSpanInfo, rStartSelectSingleSpanInfo);
                } else if (rEndSelectSingleSpanInfo.charsIndex > rStartSelectSingleSpanInfo.charsIndex) {
                    selectStr = getMultiSpanString(rStartSelectSingleSpanInfo, rEndSelectSingleSpanInfo);
                } else {
                    if (rStartSelectSingleSpanInfo.start > rEndSelectSingleSpanInfo.start) {
                        if (charList != null) {
                            selectStr = new String((char[]) charList.get(rStartSelectSingleSpanInfo.charsIndex),
                                    rEndSelectSingleSpanInfo.start, rStartSelectSingleSpanInfo.start - rEndSelectSingleSpanInfo.start + 1);
                        }
                    } else if (rEndSelectSingleSpanInfo.start > rStartSelectSingleSpanInfo.start) {
                        if (charList != null) {
                            selectStr = new String((char[]) charList.get(rStartSelectSingleSpanInfo.charsIndex),
                                    rStartSelectSingleSpanInfo.start, rEndSelectSingleSpanInfo.start - rStartSelectSingleSpanInfo.start + 1);
                        }
                    } else {
                        if (charList != null && charList.get(rStartSelectSingleSpanInfo.charsIndex) instanceof char[]) {
                            selectStr = new String((char[]) charList.get(rStartSelectSingleSpanInfo.charsIndex),
                                    rStartSelectSingleSpanInfo.start, 1);
                        } else if (charList != null) {
                            selectStr = charList.get(rStartSelectSingleSpanInfo.charsIndex) + "";
                        }
                    }
                }
            }
            return selectStr;
        }

        String getMultiSpanString(SelectSingleSpanInfo startSpan, SelectSingleSpanInfo endSpan) {
            if (analysisInfo != null && analysisInfo.charList != null) {
                List<Object> charList = null;
                StringBuilder stringBuilder = new StringBuilder();
                if (analysisInfo != null) {
                    charList = analysisInfo.charList;
                }
                /**
                 * start
                 */
                if (charList != null && charList.get(startSpan.charsIndex) instanceof char[]) {
                    char[] startCharArray = (char[]) charList.get(startSpan.charsIndex);
                    stringBuilder.append(startCharArray, startSpan.start, startCharArray.length - startSpan.start);
                } else if (charList != null) {
                    stringBuilder.append(charList.get(startSpan.charsIndex));
                }

                /**
                 * multi char[] array
                 */
                for (int i = startSpan.charsIndex + 1; i < endSpan.charsIndex; i++) {

                    if (stringBuilder.length() > 100) {
                        break;
                    }

                    if (charList != null && charList.get(i) instanceof char[]) {
                        stringBuilder.append((char[])charList.get(i));
                    } else if (charList != null) {
                        stringBuilder.append(charList.get(i));
                    }
                }

                /**
                 * end
                 */
                if (charList != null && charList.get(endSpan.charsIndex) instanceof char[]) {
                    char[] endCharArray = (char[]) charList.get(endSpan.charsIndex);
                    stringBuilder.append(endCharArray, 0, endSpan.start + 1);
                } else if (charList != null) {
                    stringBuilder.append(charList.get(endSpan.charsIndex));
                }
                return stringBuilder.toString();
            }
            return null;
        }

    }

    class PageInfo {
        List<LinesInfo> data;
        int width;
        int height;
        int y;
        int index;

        boolean isInner(int relativeX, int relativeY) {
            if (/*width >= relativeX && relativeX >= 0.0f
                    &&*/ (y + height) >= relativeY && relativeY > y) {
                return true;
            }
            return false;
        }
    }

    class SelectSingleSpanInfo {
        int pageIndex;
        int relativeLineIndex;
        int charsIndex;
        int start = -1;
        int offset = -1;
        /**
         * ���x����
         * cursor's x
         */
        float cursorRelativeX;
        RectF singleCharRectF;
    }

    public interface OnTextScrollViewListener {

        void startAnalyTextPage();

        void loadingTextPage();

        void startSelectText();

        void endSelectText(String text, RectF startRectF, RectF endRectF);

        void onMoveSelect(float x, float y);
    }
}
