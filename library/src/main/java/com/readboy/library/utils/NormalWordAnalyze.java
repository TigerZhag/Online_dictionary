package com.readboy.library.utils;

import android.graphics.Color;
import android.graphics.Typeface;

import com.readboy.library.utils.PhoneticUtils;
import com.readboy.library.widget.IParaser;
import com.readboy.library.widget.SpanParase;
import com.readboy.library.widget.analy.AnalysisInfo;
import com.readboy.library.widget.analy.SpanBaseAnalyze;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Senny on 2015/10/29.
 */
public class NormalWordAnalyze extends SpanBaseAnalyze<String> {


    public NormalWordAnalyze(String string) {
        super(string);
    }

    @Override
    public void filterExplain(String string) {
        int defalutColor = Color.BLACK;
        AnalysisInfo analysisInfo = new AnalysisInfo();
        analysisInfo.setAnalyze(this);
        analysisInfo.charList = new ArrayList<Object>();
        analysisInfo.colorMap = new HashMap<Integer, Integer>();
        analysisInfo.newLineMap = new HashMap<Integer, Boolean>();
        analysisInfo.colorMap.put(-1, defalutColor);

        String[] strArray = string.split("\n");
        if (strArray != null) {
            for (String childStr : strArray) {
                analysisInfo.newLineMap.put(analysisInfo.charList.size(), true);
                analysisInfo.charList.add(PhoneticUtils.checkString(childStr).toCharArray());
            }
        }
        if (getObtainer() != null) {
            getObtainer().getResult(analysisInfo);
        }
        setObtainer(null);
    }

    @Override
    public IParaser createParaser(AnalysisInfo analysisInfo) {
        return new SpanParase(analysisInfo);
    }

    @Override
    public Typeface getPaintTypeface() {
        return null;
    }
}
