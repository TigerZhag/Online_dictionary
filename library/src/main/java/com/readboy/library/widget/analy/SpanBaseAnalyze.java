package com.readboy.library.widget.analy;

import com.readboy.library.widget.ISpanAnalysisObtainer;

/**
 * Editor: sgc
 * Date: 2015/01/20
 */
public abstract class SpanBaseAnalyze<T> implements ImlAnalyze<ISpanAnalysisObtainer> {

    /**
     * 数据
     */
    private T t = null;

    /**
     * 停止解析
     */
    private boolean isStop = false;

    /**
     * 计算字体位置和颜色的监听器
     */
    private ISpanAnalysisObtainer iSpanAnalysisObtainer = null;

    public SpanBaseAnalyze(T t){
        this.t = t;
    }

    @Override
    public void setObtainer(ISpanAnalysisObtainer iSpanAnalysisObtainer) {
        this.iSpanAnalysisObtainer = iSpanAnalysisObtainer;
    }

    @Override
    public void start() {

        /**
         * step 2
         */
        filterExplain(t);
        iSpanAnalysisObtainer = null;
        t = null;
        isStop = true;
    }

    @Override
    public void stop() {
        isStop = true;
    }

    @Override
    public void clear() {
        iSpanAnalysisObtainer = null;
        t = null;
    }

    public boolean isStop() {
        return isStop;
    }

    public ISpanAnalysisObtainer getObtainer() {
        return iSpanAnalysisObtainer;
    }

    public abstract void filterExplain(T t);

}
