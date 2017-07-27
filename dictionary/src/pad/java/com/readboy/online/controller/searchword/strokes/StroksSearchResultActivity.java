package com.readboy.online.controller.searchword.strokes;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ActionMenuView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.readboy.Dictionary.R;
import com.readboy.online.model.HttpGetDataListener;
import com.readboy.online.model.HttpRequestTask;
import com.readboy.online.model.adapters.StroksListAdapter;
import com.readboy.online.model.data.PinYinAndZi;
import com.readboy.online.model.data.StroksResultData;
import com.readboy.online.model.utils.DbUtil;
import com.readboy.online.view.AutoNextLineLinearlayout;
import com.readboy.online.view.FixGridLayout;
import com.readboy.online.view.PinYinWordView;
import com.readboy.online.view.WordWrapView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class StroksSearchResultActivity extends Activity implements HttpGetDataListener, View.OnClickListener {

    /*   笔画查字URL,请求参数request格式案例:F_bihua=3或者F_bihua=3&F_page=1&F_pagesize=10    */
    private static final String URL_STROKES = "http://dreamtest.strongwind.cn:7150/chinese/bihua?";
    /*   网络请求异步任务对象   */
    private HttpRequestTask task;
    /*   URL参数key   */
    private static final String URL_PARAM_STROKS = "F_bihua=";
    private static final String URL_PARAM_PAGE = "F_page=";
    private static final String URL_PARAM_PAGESIZE = "F_pagesize=";
    /*   URL参数value   */
    private String mRequestParam;
    private int mPage = 1;
    private int mPagesize = 500;
    /*   带参请求地址   */
    private String mUrlRequest;
    /*   json对象   */
    private JSONObject jsonObject;
    private JSONArray jsonArray;
    /*   数据库操作对象   */
    private DbUtil mDbUtil;
    /*   笔画数   */
    private String requestNum;
    /*   相应笔画参数的部首列表   */
    private List<String> buShouList;
    /*   部首对应的拼音字列表   */
    private HashMap<String, List<PinYinAndZi>> buShouMap;
    private ListView lvStroks;
    private List<StroksResultData> mList;
    private StroksListAdapter adapter;
    private LinearLayout llStroks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stroks_search_result);
        initInfo();
        addView();
    }

    private void addView(){
        initBushouList(requestNum);
        setPinYinZiList();
        for(int i=0;i<buShouList.size();i++){
            addTvBushou(buShouList.get(i));
            addPinYinZi(buShouList.get(i));
        }
    }

    private void addTvBushou(String bushou){
        TextView tvBushou = new TextView(this);
        tvBushou.setText(bushou+"部");
        tvBushou.setTextSize(20);
        tvBushou.setTextColor(Color.parseColor("#ff8401"));
        tvBushou.setPadding(20,20,0,12);
        llStroks.addView(tvBushou);
    }

    private void addPinYinZi(String bushou){
        List<PinYinAndZi> list = buShouMap.get(bushou);
        AutoNextLineLinearlayout autoLayout = new AutoNextLineLinearlayout(this);
        AutoNextLineLinearlayout.LayoutParams lp = new ActionMenuView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMargins(50,0,50,0);
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
            tvPinYin.setLayoutParams(lp);
            autoLayout.addView(tvPinYin);
        }
        llStroks.addView(autoLayout);
    }

    /*   根据笔画和部首获取相应的字与拼音集合   */
    private List<PinYinAndZi> getPinYinAndZiList(String bihua, String bushou){
        List<PinYinAndZi> list = new ArrayList<PinYinAndZi>();
        Cursor c = mDbUtil.getResultFromBiHuaAndBushou(bihua,bushou);
        while (c.moveToNext()){
            PinYinAndZi point = new PinYinAndZi();
            point.setPinyin(c.getString(c.getColumnIndex("F_pinyin")));
            point.setZi(c.getString(c.getColumnIndex("F_zi")));
            list.add(point);
        }
        return list;
    }

    // TODO: 16-7-11 注意数据库有就从数据库拿 数据库没从网络获取并存入数据库再拿的细节过程
    /*   获取相应笔画对应的部首列表   */
    private void initBushouList(String bihua){
        buShouList = new ArrayList<String>();
        buShouList = mDbUtil.getBuShouFromBiHua(bihua);
        if(buShouList.size() == 0){
            mRequestParam = requestNum;
            mUrlRequest = URL_PARAM_STROKS+mRequestParam+"&"+URL_PARAM_PAGE+mPage+"&"+URL_PARAM_PAGESIZE+mPagesize;
            task = (HttpRequestTask) new HttpRequestTask(URL_STROKES,this).execute(mUrlRequest);
        }
    }

    /*   获取部首对应的拼音字列表   */
    private void setPinYinZiList() {
        if(buShouMap == null){
            buShouMap = new HashMap<String, List<PinYinAndZi>>();
        }
        for (String bushou : buShouList){
            buShouMap.put(bushou ,getPinYinAndZiList(requestNum,bushou));
        }
    }

    private void initInfo(){
        requestNum = getIntent().getStringExtra("stroks_num");
        mDbUtil = new DbUtil(this);
        mDbUtil.init();
        llStroks = (LinearLayout) findViewById(R.id.stroks_layout);
    }

    @Override
    public void getDataResult(String result) {
        try {
            jsonObject = new JSONObject(result);
            jsonArray = jsonObject.getJSONArray("F_list");
            for(int i = 0;i<jsonArray.length();i++){
                mDbUtil.insertBiHuaResult(jsonArray.getJSONObject(i).getString("F_bihua")
                        ,jsonArray.getJSONObject(i).getString("F_zi")
                        ,jsonArray.getJSONObject(i).getString("F_bushou")
                        ,jsonArray.getJSONObject(i).getString("F_pinyin"));
            }
            addView();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {

    }
}
