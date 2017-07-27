package com.readboy.online.controller.searchword.redical;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.readboy.Dictionary.R;
import com.readboy.online.model.HttpGetDataListener;
import com.readboy.online.model.HttpRequestTask;
import com.readboy.online.model.data.PinYinAndZi;
import com.readboy.online.view.AutoNextLineLinearlayout;
import com.readboy.online.view.PinYinWordView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class RedicalSearchResultActivity extends Activity implements HttpGetDataListener, View.OnClickListener {

    /*   部首查字URL,请求参数request格式案例:F_bushou=艹或者F_bushou=艹&F_page=1&F_pagesize=10,参数中文需utf-8编码   */
    private static final String URL_RADICAL = "http://dreamtest.strongwind.cn:7150/chinese/bushou?";
    /*   网络请求异步任务对象   */
    private HttpRequestTask task;
    private String redical;
    private String urlRequestParam;
    /*   拼音字列表   */
    private List<PinYinAndZi> mPinYinZiList;
    /*   json解析对象   */
    private JSONObject jsonObject;
    private JSONArray jsonArray;
    private LinearLayout viewRoot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_redical_search_result);
        findViewById();
        initInfo();
    }

    private void findViewById(){
        viewRoot = (LinearLayout) findViewById(R.id.ll_redical_result);
        mPinYinZiList = new ArrayList<PinYinAndZi>();
    }

    private void initInfo(){
        redical = getIntent().getStringExtra("redical");
        Log.i("dvsvdf",redical);
        try {
            String redicalTmp = URLEncoder.encode(redical,"utf-8");
            Log.i("dvsvdf",redicalTmp);
            urlRequestParam = "F_bushou="+redicalTmp+"&F_page=1&F_pagesize=50";
            task = (HttpRequestTask) new HttpRequestTask(URL_RADICAL,this).execute(urlRequestParam);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void addTvTitle(String title){
        TextView tvTitle = new TextView(this);
        tvTitle.setText(title);
        tvTitle.setTextSize(20);
        tvTitle.setTextColor(Color.parseColor("#ff8401"));
        tvTitle.setPadding(20,20,0,12);
        viewRoot.addView(tvTitle);
    }

    private void addPinYinZiList(List<PinYinAndZi> list){
        AutoNextLineLinearlayout autoLayout = new AutoNextLineLinearlayout(this);
        for(int i=0;i<list.size();i++){
            PinYinWordView tvPinYin = new PinYinWordView(this);
            tvPinYin.setPinyin(list.get(i).getPinyin());
            tvPinYin.setZi(list.get(i).getZi());
            tvPinYin.setWidth(100);
            tvPinYin.setHeight(100);
            tvPinYin.setPadding(5,5,5,5);
            tvPinYin.setTextSize(22);
            tvPinYin.setOnClickListener(this);
            tvPinYin.setBackgroundResource(R.drawable.btn_searchlist_item_bg2);
            autoLayout.addView(tvPinYin);
        }
        viewRoot.addView(autoLayout);
    }

    @Override
    public void getDataResult(String result) {
        Log.i("dvsvdf",result.toString());
        try {
            jsonObject = new JSONObject(result);
            jsonArray = jsonObject.getJSONArray("F_list");
            for(int i=0;i<jsonArray.length();i++){
                PinYinAndZi point = new PinYinAndZi();
                point.setPinyin(jsonArray.getJSONObject(i).getString("F_pinyin"));
                point.setZi(jsonArray.getJSONObject(i).getString("F_zi"));
                mPinYinZiList.add(point);
            }
            addTvTitle(redical);
            addPinYinZiList(mPinYinZiList);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {

    }
}
