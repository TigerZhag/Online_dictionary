package com.readboy.library.utils;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;

import com.readboy.library.io.DictIOFile;
import com.readboy.library.widget.IParaser;
import com.readboy.library.widget.SpanParase;
import com.readboy.library.widget.analy.AnalysisInfo;
import com.readboy.library.widget.analy.SpanBaseAnalyze;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 解析数据
 * Editor: sgc
 * Date: 2015/01/04
 */
public class DictWordAnalyze extends SpanBaseAnalyze<byte[]> {

    /**
     * 是设置颜色标志
     */
    private final static int RICHEDIT_SETCOLOR = 1;

    /**
     * 空域,等与:"";字符为空，但不为null
     */
    private final static int BLANK_AREA = 0x00;

    /**
     * 音标区域
     */
    private final static int YINBIAO_AREA = 0x02;

    /**
     * 0x14之前的预留其它标识符号
     */
    private byte YinBiaoStartAddress = 0x14;

    private boolean lastIsAfterNewLine = false;

    private String keyWord;

    public DictWordAnalyze(byte[] byteBuffer){
        super(byteBuffer);
    }

    public DictWordAnalyze(String keyWord, byte[] byteBuffer){
        super(byteBuffer);
        this.keyWord = keyWord;
    }

    @Override
    public void filterExplain(byte[] byteBuffer) {
        filterExplain(keyWord, byteBuffer);
    }

    public void filterExplain(String keyWord, byte[] byteBuffer) {
        int defalutColor = Color.BLACK;
        AnalysisInfo analysisInfo = new AnalysisInfo();
        analysisInfo.setAnalyze(this);
        analysisInfo.charList = new ArrayList<Object>();
        analysisInfo.colorMap = new HashMap<Integer, Integer>();
        analysisInfo.newLineMap = new HashMap<Integer, Boolean>();
        analysisInfo.blankOrSpaceList = new ArrayList<Integer>();
        analysisInfo.colorMap.put(-1, defalutColor);

        if (keyWord != null) {
            try {
                addText(analysisInfo, keyWord.getBytes(DictIOFile.DECODE_GB2312), 0,
                        keyWord.getBytes(DictIOFile.DECODE_GB2312).length, DictTextColorInfo.COLOR_LIST[0], defalutColor, true);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        int i;
        int value;
        int length = byteBuffer.length;
        int startIndex = 0;// 截取开始位置
        int colorIndex = DictTextColorInfo.COLOR_LIST[0];// 初始化黑色
        for(i = 0;i<length;i++) {
            if (isStop()) {
                clear();
                break;
            }
            value = byteBuffer[i]&0xff;
            if (value == 0x0D || value == 0x0A) {// 换行
                int offset = 1;
                int tempValue = byteBuffer[i+offset]&0xff;
                while (tempValue == 0x0D || tempValue == 0x0A) {
                    offset++;
                    tempValue = byteBuffer[i+offset]&0xff;
                }
                offset--;
                i += offset;
                startIndex = i;
            }  else if (value == RICHEDIT_SETCOLOR) {
                if (length > (i+2)) {
                    if(byteBuffer[i+2] == BLANK_AREA) {// 设置前面文本的颜色
                        addText(analysisInfo, byteBuffer, startIndex, i - startIndex, colorIndex, defalutColor, false);
                        startIndex = i + 3;
                        colorIndex = DictTextColorInfo.COLOR_LIST[0];// 初始化黑色
                    } else {
                        addText(analysisInfo, byteBuffer, startIndex, i - startIndex, colorIndex, defalutColor, false);
                        colorIndex = byteBuffer[i+2];
                        colorIndex = DictTextColorInfo.COLOR_LIST[colorIndex];
                        startIndex = i + 3;
                    }
                    i += 2;
                }
            } else if (value == 0xFE) {// 判断音标,注意可能是汉字
                value = byteBuffer[++i]&0xff;
                if(value == 0xBB) {		// 换行
                    byteBuffer[i-1] = BLANK_AREA;//置为空域
                    byteBuffer[i] = BLANK_AREA;
                    addText(analysisInfo, byteBuffer, startIndex, i - startIndex, colorIndex, defalutColor, true);
                    startIndex = i + 1;
                } else if (value >= 0x50 && mSymbolList.length > (value-0x50) && mSymbolList[value-0x50] != 0) {
                    byteBuffer[i-1] = BLANK_AREA;
                    byteBuffer[i] = BLANK_AREA;
                    addText(analysisInfo, byteBuffer, startIndex, i - startIndex, colorIndex, defalutColor, false);
                    addText(analysisInfo, PhoneticUtils.checkDefineChar(mSymbolList[value - 0x50]), colorIndex, defalutColor);
                    startIndex = i + 1;
                }
            } else if (value >= 0x80) {// 汉字
                i++;// 汉字占两个字节
            }
        }

        if (length > startIndex) {
            addText(analysisInfo, byteBuffer, startIndex, length - startIndex, colorIndex, defalutColor, false);
        }
        if (getObtainer() != null) {
            getObtainer().getResult(analysisInfo);
        }
        setObtainer(null);
    }

    private void addText(AnalysisInfo analysisInfo, byte[] bytes, int start, int offset,
                         int color, int defaultColor, boolean isAfterNewLine) {
        boolean isTrimZero = false;
        if (bytes != null) {
            char[] chars = null;
            try {

                String str = new String(bytes, start, offset, DictIOFile.DECODE_GB2312);
                isTrimZero = str.trim().length() == 0;
                if (lastIsAfterNewLine && isAfterNewLine && isTrimZero) {
                    return;
                }
                chars = str.toCharArray();
                for(int i = 0;i < chars.length;i++){
                    chars[i] = PhoneticUtils.checkDefineChar(chars[i]);
                    if (chars[i] == 0xE847) {
                        chars[i] = 0x6c;//'l'
                    }
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            if (chars != null && chars.length > 0) {
                if (color != defaultColor) {
                    analysisInfo.colorMap.put(analysisInfo.charList.size(), color);
                }
                if (isAfterNewLine) {
                    analysisInfo.newLineMap.put(analysisInfo.charList.size(), true);
                }
                if (isTrimZero) {
                    analysisInfo.blankOrSpaceList.add(analysisInfo.charList.size());
                }
                analysisInfo.charList.add(chars);
            }
            lastIsAfterNewLine = isAfterNewLine;
        }
    }

    private void addText(AnalysisInfo analysisInfo, char c, int color, int defaultColor) {
        if (color != defaultColor) {
            analysisInfo.colorMap.put(analysisInfo.charList.size(), color);
        }
        analysisInfo.charList.add(c);
    }

    /**
     * 特殊字符
     */
    private char mSymbolList[] = {
            0x0062,		/* 0x50 'b' */
            0x0064,		/* 0x51 'd' */
            0x00f0,		/* 0x52 'e'*/
            0,		/* 0x53 ''*/
            0x0066,		/* 0x54 'f'*/
            0x0067,		/* 0x55 'g' */
            0x0068,		/* 0x56 'h'*/
            0x006a,		/* 0x57 'j'*/
            0x006b,		/* 0x58 'k'*/
            0x006c,		/* 0x59 'l'*/
            0x006d,		/* 0x5a 'm'*/
            0x006e,		/* 0x5b 'n'*/
            0x014b,		/* 0x5c '?'*/
            0x0070,		/* 0x5d 'p'*/
            0x0072,		/* 0x5e 'r'*/
            0x0073,		/* 0x5f 's'*/
            0x0283,		/* 0x60 '?'*/
            0x0074,		/* 0x61 't'*/
            0,		/* 0x62 ''*/
            0x03b8,		/* 0x63 'θ'*/
            0x0076,		/* 0x64 'v'*/
            0x0077,		/* 0x65 'w'*/
            0x0078,		/* 0x66 'x'*/
            0x007a,		/* 0x67 'z'*/
            0x0292,		/* 0x68 '?'*/
            0x0069,		/* 0x69 'i'*/
            0x026a,		/* 0x6a '?'*/
            0x025b,		/* 0x6b '?'*/
            0x00e6,		/* 0x6c '?'*/
            0x0061,		/* 0x6d 'a'*/
            0x0254,		/* 0x6e '?'*/
            0x0075,		/* 0x6f 'u'*/
            0x006d,		/* 0x70 'm'*/
            0,		/* 0x71 ''*/
            0x028c,		/* 0x72 '?'*/
            0x025c,		/* 0x73 '?'*/
            0x0259,		/* 0x74 '?'*/
            0x0259,		/* 0x75 '?'*/
            0,		/* 0x76 ''*/
            0x006f,		/* 0x77 'o'*/
            0x25c,		/* 0x78 '?'*/
            0,		/* 0x79 ''*/
            0,		/* 0x7a ''*/
            0x02d0,		/* 0x7b '?'*/
            0x02c8,		/* 0x7c '?'*/
            0,		/* 0x7d ''*/
            0,		/* 0x7e ''*/
            0,		/* 0x7f ''*/
            0x0259,		/* 0x80 '?'*/
            0x0254,		/* 0x81 '?'*/
            0x02cc,		/* 0x82 '?'*/
            0,		/* 0x83 '?'*/
            0x0075,		/* 0x84 'u'*/
            0x0075,		/* 0x85 'u'*/
            0x006e,		/* 0x86 'n'*/
            0,		/* 0x87 ''*/
            0,		/* 0x88 ''*/

            /**
             * 中式字符
             */
            /*0xE289,*/0x0101,		/* 0x89 'ā'*/
            /*0xE28A,*/0x00e1,		/* 0x8a 'á'*/
            /*0xE28B,*/0x0103,		/* 0x8b '?'*/
            /*0xE28C,*/0x00e0,		/* 0x8c 'à'*/
            /*0xE28D,*/0x0251,		/* 0x8d 'ɑ'*/


            0x0062,		/* 0x8e 'b'*/
            0x0063,		/* 0x8f 'c'*/
            0x0064,		/* 0x90 'd'*/
            0x0113,		/* 0x91 'ē'*/
            0x00e9,		/* 0x92 'é'*/
            0x011b,		/* 0x93 'ě'*/
            0x00e8,		/* 0x94 'è'*/
            0x0065,		/* 0x95 'e'*/
            0x0066,		/* 0x96 'f'*/
            0x0067,		/* 0x97 'g'*/
            0x0068,		/* 0x98 'h'*/
            0x012b,		/* 0x99 'ī'*/
            0x00ed,		/* 0x9a 'í'*/
            0x01d0,		/* 0x9b 'ǐ'*/
            0x00ec,		/* 0x9c 'ì'*/
            0x0069,		/* 0x9d 'i'*/
            0x006a,		/* 0x9e 'j'*/
            0x006b,		/* 0x9f 'k'*/
            0x006c,		/* 0xa0 'l'*/
            0x006d,		/* 0xa1 'm'*/
            0x006e,		/* 0xa2 'n'*/
            0x014d,		/* 0xa3 'ō'*/
            0x00f3,		/* 0xa4 'ó'*/
            0x01d2,		/* 0xa5 'ǒ'*/
            0x00f2,		/* 0xa6 'ò'*/
            0x006f,		/* 0xa7 'o'*/
            0x0070,		/* 0xa8 'p'*/
            0x0071,		/* 0xa9 'q'*/
            0x0072,		/* 0xaa 'r'*/
            0x0073,		/* 0xab 's'*/
            0x0074,		/* 0xac 't'*/
            0x016b,		/* 0xad 'ū'*/
            0x00fa,		/* 0xae 'ú'*/
            0x016d,		/* 0xaf '?'*/
            0x00f9,		/* 0xb0 'ù'*/
            0x0075,		/* 0xb1 'u'*/
            0,		/* 0xb2 ''*/
            0x01d8,		/* 0xb3 'ǘ'*/
            0x01da,		/* 0xb4 'ǚ'*/
            0x01dc,		/* 0xb5 'ǜ'*/
            0x00fc,		/* 0xb6 'ü'*/
            0x0077,		/* 0xb7 'w'*/
            0x0078,		/* 0xb8 'x'*/
            0x0079,		/* 0xb9 'y'*/
            0x007a,		/* 0xba 'z'*/
            0,		/* 0xbb ''*/
            0,		/* 0xbc ''*/
            0,		/* 0xbd ''*/
            0,		/* 0xbe ''*/
            0,		/* 0xbf ''*/
    };

    @Override
    public IParaser createParaser(AnalysisInfo analysisInfo) {
        return new SpanParase(analysisInfo);
    }

    @Override
    public Typeface getPaintTypeface() {
        return null;
    }
}
