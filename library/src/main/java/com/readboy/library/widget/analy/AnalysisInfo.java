package com.readboy.library.widget.analy;

import android.graphics.Typeface;

import com.readboy.library.widget.IParaser;

import java.util.List;
import java.util.Map;

/**
 * Created by Sgc on 2015/6/15.
 */
public class AnalysisInfo {

    public List<Object> charList;
    public Map<Integer, Integer> colorMap;
    public Map<Integer, Integer> textSizeMap;
    public Map<Integer, Boolean> newLineMap;
    public List<Integer> blankOrSpaceList;

    private ImlAnalyze imlAnalyze;

    public void setAnalyze(ImlAnalyze imlAnalyze) {
        this.imlAnalyze = imlAnalyze;
    }

    public IParaser createParaser() {
        if (imlAnalyze != null) {
            return imlAnalyze.createParaser(this);
        }
        return null;
    }

    public Typeface getPaintTypeface() {
        if (imlAnalyze != null) {
            return imlAnalyze.getPaintTypeface();
        }
        return null;
    }

    public void destroy() {
        if (charList != null) {
            charList.clear();
        }
        if (colorMap != null) {
            colorMap.clear();
        }
        if (textSizeMap != null) {
            textSizeMap.clear();
        }
        if (newLineMap != null) {
            newLineMap.clear();
        }
        imlAnalyze = null;
    }

    public void copy(AnalysisInfo analysisInfo) {
        analysisInfo.charList = charList;
        analysisInfo.colorMap = colorMap;
        analysisInfo.textSizeMap = textSizeMap;
        analysisInfo.newLineMap = newLineMap;
        analysisInfo.blankOrSpaceList = blankOrSpaceList;
        analysisInfo.imlAnalyze = imlAnalyze;
    }

}
