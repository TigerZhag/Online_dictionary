package com.readboy.library.widget;

import android.graphics.Paint;
import android.graphics.Typeface;

import com.readboy.library.utils.PhoneticUtils;
import com.readboy.library.widget.analy.AnalysisInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Sgc on 2015/6/16.
 */
public class SpanParase implements IParaser {

    public final static int UN_INVALUE = -1;

    private float mScreenWidth;
    private float mScreenHeight;

    private AnalysisInfo analysisInfo;
    private ISpanParaseObtainer iObtainer;
    private List<SpanInfo> lineSpanInfoList;
    private List<LinesInfo> linesInfoList;

    public void setCharsParaseObtainer(ISpanParaseObtainer iObtainer) {
        this.iObtainer = iObtainer;
    }

    public SpanParase(AnalysisInfo analysisInfo) {
        this.analysisInfo = analysisInfo;
    }

    public void init(float mScreenWidth, float mScreenHeight) {
        this.mScreenWidth = mScreenWidth;
        this.mScreenHeight = mScreenHeight;
    }

    public void start() {
        parase(0, UN_INVALUE, UN_INVALUE, 0);
    }

    public void parase(int intCharListIndex, int intCharStart, int intCharOffset, int intLine) {
        if (analysisInfo != null && mScreenWidth > 0 && mScreenHeight > 0) {
            Paint mPaint = new Paint();
            Typeface typeface = analysisInfo.getPaintTypeface();
            if (typeface != null) {
                mPaint.setTypeface(typeface);
            }
            mPaint.setSubpixelText(true);
            if (analysisInfo.charList != null && iObtainer != null) {
                String tag = iObtainer.toString();
                Map newLineMap = analysisInfo.newLineMap;
                List<Object> objects = analysisInfo.charList;
                int number = objects.size();
                float textWidth = 0.0f;
                float textHeight = 0.0f;
                float[] measureWidth = new float[1];
                float h = measureWordHeight(0, mPaint, intLine);
                for (int ii = intCharListIndex ; ii < number ; ii++) {
                    boolean isBlnContinue = false;
                    Object obj = null;
                    if (objects != null && objects.size() > ii) {
                        obj = objects.get(ii);
                    }
                    if (obj != null) {
                        if (obj instanceof char[]) {
                            char[] chars = (char[]) obj;
                            int start = 0;
                            int length = chars.length;
                            int leave = length;
                            if (intCharStart > UN_INVALUE && intCharOffset > UN_INVALUE) {
                                start = intCharStart;
                                leave = intCharOffset;
                            }
                            int ccc = 0;
                            while (leave != 0) {
                                int offset = UN_INVALUE;
                                if (chars != null) {
                                    offset = mPaint.breakText(chars, start, leave,
                                            mScreenWidth - textWidth, measureWidth);
                                    /**
                                     * check again, beacause the bug of Paint.breakText().
                                     */
                                    if (leave >= (offset + 1)) {
                                        float checkWidth = mPaint.measureText(chars, start, offset + 1);
                                        if ((mScreenWidth- textWidth) >= checkWidth) {
                                            measureWidth[0] = checkWidth;
                                            offset += 1;
                                        }
                                    }
                                } else {
                                    break;
                                }
                                if (offset > 0) {
                                    int end = start + offset - 1;
                                    int sufIndex = sufMatchSymbol(chars, end);
                                    if (sufIndex > end) {
                                        int preIndex = preMatchSymbol(chars, end);
                                        if (preIndex != start) {
                                            addCharsInfoToLine(ii, start,
                                                    preIndex - start, textWidth, textHeight);
                                        }

                                        if (isLineAllSpaceOrBlank(lineSpanInfoList)) {
                                            textWidth = 0.0f;
                                            isBlnContinue = true;
                                            break;
                                        }

                                        addLineToList(intLine, textHeight, mPaint.getTextSize());
                                        intLine++;

                                        start = preIndex;
                                        leave = length - start;
                                        textHeight += h;
                                        textWidth = 0.0f;
                                        if (textHeight >= mScreenHeight) {
                                            addPage(tag, textHeight);
                                            textHeight = 0.0f;
                                        }
                                        if (analysisInfo != null) {
                                            if (analysisInfo.textSizeMap.get(intLine) == null
                                                    && analysisInfo.textSizeMap.get(intLine - 1) != null) {
                                                analysisInfo.textSizeMap.put(intLine, analysisInfo.textSizeMap.get(intLine - 1));
                                            }
                                            h = measureWordHeight(h, mPaint, intLine);
                                        }
                                    } else {
                                        addCharsInfoToLine(ii, start,
                                                end + 1 - start, textWidth, textHeight);
                                        textWidth += measureWidth[0];
                                        start = end + 1;
                                        leave = length - start;
                                    }
                                } else if (offset != UN_INVALUE){

                                    if (isLineAllSpaceOrBlank(lineSpanInfoList)) {
                                        textWidth = 0.0f;
                                        isBlnContinue = true;
                                        break;
                                    }

                                    addLineToList(intLine, textHeight, mPaint.getTextSize());
                                    intLine++;

                                    textHeight += h;
                                    textWidth = 0.0f;
                                    if (textHeight >= mScreenHeight) {
                                        addPage(tag, textHeight);
                                        textHeight = 0.0f;
                                    }
                                    if (analysisInfo != null) {
                                        if (analysisInfo.textSizeMap.get(intLine) == null
                                                && analysisInfo.textSizeMap.get(intLine - 1) != null) {
                                            analysisInfo.textSizeMap.put(intLine, analysisInfo.textSizeMap.get(intLine - 1));
                                        }
                                        h = measureWordHeight(h, mPaint, intLine);
                                    }
                                }
                            }
                        } else if (obj instanceof Character) {
                            char c;
                            try {
                                c = (Character)obj;
                                measureWidth[0] = mPaint.measureText(c+"");
                            } catch (ClassCastException e) {
                                e.printStackTrace();
                                break;
                            }
                            if (textWidth + measureWidth[0] > mScreenWidth) {

                                if (isLineAllSpaceOrBlank(lineSpanInfoList)) {
                                    textWidth = 0.0f;
                                    continue;
                                }

                                addLineToList(intLine, textHeight, mPaint.getTextSize());
                                intLine++;

                                textHeight += h;
                                textWidth = 0;

                                if (textHeight >= mScreenHeight) {
                                    addPage(tag, textHeight);
                                    textHeight = 0.0f;
                                }
                                if (analysisInfo != null) {
                                    if (analysisInfo.textSizeMap.get(intLine) == null
                                            && analysisInfo.textSizeMap.get(intLine - 1) != null) {
                                        analysisInfo.textSizeMap.put(intLine, analysisInfo.textSizeMap.get(intLine - 1));
                                    }
                                    h = measureWordHeight(h, mPaint, intLine);

                                }

                                addCharsInfoToLine(ii, 0, 1, textWidth, textHeight);
                                textWidth = measureWidth[0];
                            } else {
                                addCharsInfoToLine(ii, 0, 1, textWidth, textHeight);

                                textWidth += measureWidth[0];
                            }
                        } else {
                            continue;
                        }

                        if (isBlnContinue) {
                            continue;
                        }

                        if (newLineMap != null && newLineMap.get(ii) instanceof Boolean) {

                            if (isLineAllSpaceOrBlank(lineSpanInfoList)) {
                                textWidth = 0.0f;
                                continue;
                            }

                            addLineToList(intLine, textHeight, mPaint.getTextSize());
                            intLine++;

                            textHeight += h;
                            textWidth = 0.0f;
                            if (textHeight >= mScreenHeight) {
                                addPage(tag, textHeight);
                                textHeight = 0.0f;
                            }
                            if (analysisInfo != null) {
                                h = measureWordHeight(h, mPaint, intLine);
                            }
                        }
                    } else {
                        break;
                    }
                    if (isStop()) {
                        break;
                    }
                }
                if (!isStop()) {

                    if (addLineToList(intLine, textHeight, mPaint.getTextSize())) {
                        intLine++;
                        textHeight += h;
                    }
                    addPage(tag, textHeight);
                    addResult(tag, null, UN_INVALUE, UN_INVALUE);
//                    charsInfoList = null;
                    linesInfoList = null;
                    textHeight = 0.0f;
                } else {
                    if (lineSpanInfoList != null) {
                        lineSpanInfoList.clear();
                        lineSpanInfoList = null;
                    }
                }
            }
            analysisInfo = null;
            iObtainer = null;
        }
    }

    private boolean isStop() {
        if (analysisInfo == null || iObtainer == null || iObtainer.isStop()) {
            return true;
        }
        return false;
    }

    /*private void addPageCharsList(String tag, List<CharsInfo> charsInfoList, float height, float width) {
        if (iObtainer != null) {
            iObtainer.getParaseResult(tag, charsInfoList, height, width);
        }
    }*/

    private boolean addLineToList(int lineIndex, float textHeight, float textSize) {
        if (lineSpanInfoList != null) {
            LinesInfo linesInfo = new LinesInfo();
            linesInfo.index = lineIndex;
            linesInfo.y = textHeight;
            linesInfo.data = lineSpanInfoList;
            if (linesInfoList == null) {
                linesInfoList = new ArrayList<LinesInfo>();
            }
            linesInfoList.add(linesInfo);
            lineSpanInfoList = null;

            return true;
        }
        return false;
    }

    private void addPage(String tag, float textHeight) {
        if (linesInfoList != null) {
            addResult(tag, linesInfoList, textHeight, mScreenWidth);
        }
        linesInfoList = null;
    }

    private void addResult(String tag, Object data, float height, float width) {
        if (iObtainer != null) {
            iObtainer.getParaseResult(tag, data, height, width);
        }
    }

    private void addCharsInfoToLine(int index, int start, int offset, float x, float y) {
        SpanInfo spanInfo = new SpanInfo();
        spanInfo.index = index;
        spanInfo.start = start;
        spanInfo.offset = offset;
        spanInfo.x = x;
//        charsInfo.y = y;
        if (lineSpanInfoList == null) {
            lineSpanInfoList = new ArrayList<SpanInfo>();
        }
        lineSpanInfoList.add(spanInfo);
    }

    private boolean isLineAllSpaceOrBlank(List<SpanInfo> data) {
        if (analysisInfo != null && data != null) {
            if (analysisInfo.charList != null) {
                for (SpanInfo spanInfo : data) {
                    if (analysisInfo.charList.size() > spanInfo.index) {
                        Object obj = analysisInfo.charList.get(spanInfo.index);
                        if (obj instanceof char[]) {
                            char[] chars = (char[]) obj;
                            String str = new String(chars, spanInfo.start, spanInfo.offset);
                            if (str.trim().length() != 0) {
                                return false;
                            }
                        } else if (obj instanceof Character) {
                            return false;
                        }
                    }
                }
                return true;
            }
        }
        /*if (analysisInfo != null && data != null) {
            if (analysisInfo.blankOrSpaceList != null) {
                for (SpanInfo spanInfo : data) {
                    if (analysisInfo.blankOrSpaceList.indexOf(spanInfo.index) == -1) {
                        return false;
                    }
                }
                data.clear();
                return true;
            }
        }*/
        return false;
    }

    private float measureWordHeight(float wordHeight, Paint paint, int lineIndex) {
        if (resetPaint(paint, lineIndex) ) {
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
                if(textSize != null && (int) paint.getTextSize() == textSize) {
                } else if (textSize != null && (int) paint.getTextSize() != textSize) {
                    paint.setTextSize(textSize);
                    result = true;
                } else if (defaultTextSize != null && (int) paint.getTextSize() != defaultTextSize) {
                    paint.setTextSize(textSizeMap.get(-1));
                    result = true;
                }
            }
        }
        return result;
    }

    /*private CharsInfo getCharsInfo(int index, int start, int offset, float x, float y) {
        CharsInfo charsInfo = new CharsInfo();
        charsInfo.index = index;
        charsInfo.start = start;
        charsInfo.offset = offset;
        charsInfo.x = x;
        charsInfo.y = y;
        return charsInfo;
    }*/

    /*private float getWordHeight(float wordHeight, Paint mPaint,
                                Map textSizeMap, int lineIndex) {
        if (resetPaint(mPaint, textSizeMap, lineIndex) ) {
            wordHeight = mPaint.getFontMetrics().bottom - mPaint.getFontMetrics().top;
        }
        return wordHeight;
    }

    private boolean resetPaint(Paint paint, Map textSizeMap, int lineIndex) {
        boolean result = false;
        if (textSizeMap != null && textSizeMap.get(lineIndex) instanceof Integer) {
            if ((int) paint.getTextSize() != (Integer) textSizeMap.get(lineIndex)) {
                paint.setTextSize((Integer) textSizeMap.get(lineIndex));
                result = true;
            }
        } else {
            if (textSizeMap != null && textSizeMap.get(-1) instanceof Integer
                    && (Integer) textSizeMap.get(-1) != (int) paint.getTextSize()) {
                paint.setTextSize((Integer) textSizeMap.get(-1));
                result = true;
            }
        }
        return result;
    }*/

    /**
     * ��ǰ����
     *
     * @param chars
     * @param index
     * @return index
     */
    private int preMatchSymbol(char[] chars, int index) {

        if (chars != null && chars.length > index && index >= 0) {
            if (isSpecialSymbol(chars[index])) {
                while ((index - 1) >= 0 && chars != null && isSpecialSymbol(chars[index - 1])) {
                    index--;
                }
            }
        }

        return index;
    }

    /**
     * �������
     *
     * @param chars
     * @param index
     * @return index
     */
    private int sufMatchSymbol(char[] chars, int index) {

        if (chars != null && chars.length > index && index >= 0) {
            if (isSpecialSymbol(chars[index])) {
                while (chars != null && chars.length > (index + 1) && isSpecialSymbol(chars[index + 1])) {
                    index++;
                }
            }
        }

        return index;
    }

    private boolean isSpecialSymbol(char c) {
        if (isEnglish(c) || c == '\'' || PhoneticUtils.isHasChange(c)) {
            return true;
        }
        return false;
    }

    private boolean isEnglish(char c) {
        if ('z' >= c && c >= 'a' || 'Z' >= c && c >= 'A') {
            return true;
        }
        return false;
    }

}
