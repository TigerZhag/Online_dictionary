package com.readboy.online.model;

import android.os.AsyncTask;

import com.readboy.online.model.utils.HttpUtil;

/**
 * 异步请求数据
 * 使用例子:1.在需要网络请求获得数据的类继承HttpGetDataListener接口
 *         2.实例异步任务对象执行异步请求HttpRequestTask task = (HttpRequestTask) new HttpRequestTask(url,this).execute(request);
 *         3.重写接口的getDataResult方法,对参数进行解析等处理
 */
public class HttpRequestTask extends AsyncTask<String,Integer,String>{

    /*   大家都再查URL,请求参数request格式案例:F_grade=3    */
    private static final String URL_HOT_WORD_SEARCH = "http://dreamtest.strongwind.cn:7140/v1/hotSearchWord?";
    /*   汉字查询URL,请求参数request格式案例:F_grade=5&F_hanzi=啊&F_user_id=480703,参数中文需utf-8编码   */
    private static final String URL_CHINESE_CHARACTER = "http://dreamtest.strongwind.cn:7150/chinese/hanzi?";
    /*   部首查字URL,请求参数request格式案例:F_bushou=艹或者F_bushou=艹&F_page=1&F_pagesize=10,参数中文需utf-8编码   */
    private static final String URL_RADICAL = "http://dreamtest.strongwind.cn:7150/chinese/bushou?";
    /*   拼音查字URL,请求参数request格式案例:F_pinyin=cao或者F_pinyin=cao&F_page=1&F_pagesize=10    */
    private static final String URL_PINYIN = "http://dreamtest.strongwind.cn:7150/chinese/pinyin?";
    /*   笔画查字URL,请求参数request格式案例:F_bihua=3或者F_bihua=3&F_page=1&F_pagesize=10    */
    private static final String URL_STROKES = "http://dreamtest.strongwind.cn:7150/chinese/bihua?";
    /*   拼音列表URL,无参请求   */
    private static final String URL_PINYIN_LIST = "http://dreamtest.strongwind.cn:7150/chinese/pinyinList";
    /*   部首列表URL,无参请求   */
    private static final String URL_RADICAL_LIST = "http://dreamtest.strongwind.cn:7150/chinese/bushouList";
    /*   笔画列表URL,无参请求   */
    private static final String URL_STROKES_LIST = "http://dreamtest.strongwind.cn:7150/chinese/bihuaList";
    /*   翻译英文URL,请求参数request格式案例:F_grade=5&F_user_id=480703&F_word=hello    */
    private static final String URL_ENGLISH_TRANSLATION = "http://dreamtest.strongwind.cn:7150/eng/translate?";


    /*   带参数的数据请求url地址   */
    private String httpRequestUrl;
    /*   请求参数   */
    private String requestQuery;
    /*   网络请求工具对象   */
    private HttpUtil httpUtil = new HttpUtil();;
    /*   请求结果   */
    private String result;
    /*   获取请求数据接口   */
    private HttpGetDataListener listener;
    /*   不带参数的数据请求url地址   */
    private String url;

    public HttpRequestTask(String url, HttpGetDataListener listener) {
        this.url = url;
        this.listener = listener;
    }

    /**
     * 异步任务进行网络请求数据
     * @param params 请求参数
     * @return  请求返回结果,可能为null
     */
    @Override
    protected String doInBackground(String... params) {
        requestQuery = params[0];
        httpRequestUrl = url + requestQuery;
        result = httpUtil.getConResult(httpRequestUrl,requestQuery);
        return result;
    }

    /**
     * 网络请求结束后,以参数形式传递结果为s,在主线程中回调此方法
     * @param s 请求结果
     */
    @Override
    protected void onPostExecute(String s) {
        /*   由此可见,只要继承HttpGetDataListener接口并且实现getDataResult方法就能获得数据   */
        listener.getDataResult(s);
        super.onPostExecute(s);
    }
}
