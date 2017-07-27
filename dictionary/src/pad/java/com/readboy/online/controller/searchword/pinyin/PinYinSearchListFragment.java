package com.readboy.online.controller.searchword.pinyin;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextPaint;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.readboy.Dictionary.R;
import com.readboy.online.controller.searchword.strokes.StroksSearchResultActivity;
import com.readboy.online.model.HttpGetDataListener;
import com.readboy.online.model.HttpRequestTask;
import com.readboy.online.model.data.PinYinAndZi;
import com.readboy.online.model.utils.DbUtil;
import com.readboy.online.view.AutoNextLineLinearlayout;
import com.readboy.online.view.FixGridLayout;
import com.readboy.online.view.ObservableScrollView;
import com.readboy.online.view.OnTouchingLetterChangedListener;
import com.readboy.online.view.RulerWidget;
import com.readboy.online.view.ScrollViewListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PinYinSearchListFragment extends Fragment implements HttpGetDataListener, View.OnClickListener, OnTouchingLetterChangedListener, ScrollViewListener {

    /*   拼音列表URL,无参请求   */
    private static final String URL_PINYIN_LIST = "http://dreamtest.strongwind.cn:7150/chinese/pinyinList";
    /*   网络请求异步任务对象   */
    private HttpRequestTask task;
    /*   json解析对象   */
    private JSONArray array;
    private JSONObject jsonObject;
    /*   数据库操作对象   */
    private DbUtil mDbUtil;
    /*   首字母列表   */
    private String[] mFirstCharString;
    private View viewRoot;
    /*   字母对应拼音列表   */
    private HashMap<String, List<String>> pinyinMap;
    /*   动态装载控件的父view   */
    private LinearLayout parentLayout;
    /*   字母索引表,重写触摸事件接口，setindexStr设置索引表内容，获取scollView位置表，匹配触摸的index进行滑动   */
    private RulerWidget ruler;
    /*   字母索引点击后显示在布局中间的tv   */
    private TextView tvIndexCenter;
    /*   用于显示和隐藏tvIndexCenter   */
    private Handler handler;
    private OverlayThread overlayThread;
    /*   自定义scrollView为了设置滑动监听事件判断滑动位置   */
    private ObservableScrollView scrollView;
    /*   scrollView每个节点的位置，用于滑动到节点指定位置   */
    private int[] scrollY={0,135,356,645,855,995,1138,1350,1567,1777,1991,2208,2421,2636,2775,2985,3203,3415,3704,3916,4057,4098,4098};
    /*   索引表对应的内容   */
    private String[] indexChar = {
            "a", "b", "c", "d", "e", "f", "g", "h",
            "j", "k", "l", "m", "n", "o", "p", "q",
            "r", "s", "t", "w", "x", "y", "z"
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        viewRoot = inflater.inflate(R.layout.fragment_pinyin_list,null);
        initInfo();
        getData();
        return viewRoot;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        handler = new Handler();
        overlayThread = new OverlayThread();
    }

    /*   动态加载控件   */
    private void getData(){
        getFirstCharList();
        setPinYinZiList();
        for(int i=0;i<mFirstCharString.length;i++){
            addFirstChar(mFirstCharString[i]);
            addPinYin(mFirstCharString[i]);
        }

    }

    /*   动态加载首字母布局   */
    private void addFirstChar(String str){
        TextView firstChar = new TextView(getActivity());
        firstChar.setText(str);
        firstChar.setTextSize(20);
        firstChar.setTextColor(Color.parseColor("#ff8401"));
        firstChar.setPadding(20,20,0,12);
        parentLayout.addView(firstChar);
    }

    /*   动态加载拼音列表   */
    private void addPinYin(String str){
        List<String> list = pinyinMap.get(str);
        AutoNextLineLinearlayout fixGridLayout = new AutoNextLineLinearlayout(getActivity());
        for(int i=0;i<list.size();i++){
            TextView pinyin = new TextView(getActivity());
            String pinyinTextTag = list.get(i);
            pinyin.setText(pinyinTextTag);
            pinyin.setTag(pinyinTextTag);
            pinyin.setBackgroundResource(R.drawable.btn_searchlist_item_bg);
            pinyin.setTextSize(22);
            pinyin.setPadding(10,5,10,5);
            pinyin.setHeight(72);
            pinyin.setWidth(96);
            pinyin.setOnClickListener(this);
            pinyin.setGravity(Gravity.CENTER);
            fixGridLayout.addView(pinyin);
        }
        parentLayout.addView(fixGridLayout);
    }

    /*   获取首字母列表   */
    private void getFirstCharList(){
        mFirstCharString = new String[]{};
        mFirstCharString = mDbUtil.getFirstCharString();
    }

    /*   设置首字母对应的拼音列表   */
    private void setPinYinZiList() {
        if(pinyinMap == null){
            pinyinMap = new HashMap<String, List<String>>();
        }
        for (String firstChar : mFirstCharString){
            pinyinMap.put(firstChar ,getPinYinList(firstChar));
        }
    }

    /*   获取拼音列表   */
    private List<String> getPinYinList(String firstChar){
        List<String> pinyinList = new ArrayList<String>();
        pinyinList = mDbUtil.getCharListFromFirstChar(firstChar);
        if(pinyinList.size() == 0){
            task = (HttpRequestTask) new HttpRequestTask(URL_PINYIN_LIST,this).execute("");
        }
        return pinyinList;
    }

    /*   初始化   */
    private void initInfo(){
        mDbUtil = new DbUtil(getActivity());
        mDbUtil.init();
        parentLayout = (LinearLayout) viewRoot.findViewById(R.id.ll_pinyin);
        ruler = (RulerWidget) viewRoot.findViewById(R.id.sidrbar);
        ruler.setOnTouchingLetterChangedListener(this);
        tvIndexCenter = (TextView) viewRoot.findViewById(R.id.tv);
        initOverlay();
        scrollView = (ObservableScrollView) viewRoot.findViewById(R.id.scroll);
        scrollView.setScrollViewListener(this);
        indexChar = ruler.getIndexStr();
    }


    private void initOverlay() {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
//    	overlay = (TextView) inflater.inflate(R.layout.overlay, null);
        tvIndexCenter.setVisibility(View.INVISIBLE);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                PixelFormat.TRANSLUCENT);
        WindowManager windowManager = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
//		windowManager.addView(textView, lp);
    }

    @Override
    public void getDataResult(String result) {
        Log.i("azazaaza",result.toString());
        String key;
        String pinyin;
        try {
            jsonObject = new JSONObject(result);
            array = jsonObject.getJSONArray("F_list");
            for(int i=0;i<array.length();i++){
                key = array.getJSONObject(i).getString("F_pinyin_key");
                pinyin = array.getJSONObject(i).getString("F_pinyin");
                mDbUtil.insertPinYin(key,pinyin);
            }
            getData();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        Intent toPinYinResult = new Intent(getActivity(),PinYinSearchResultActivity.class);
        toPinYinResult.putExtra("pinyin", v.getTag().toString());
        startActivity(toPinYinResult);
    }

    @Override
    public void onTouchingLetterChanged(String s) {
        tvIndexCenter.setText(s);
        tvIndexCenter.setVisibility(View.VISIBLE);
        handler.removeCallbacks(overlayThread);
        handler.postDelayed(overlayThread, 1500);
        for(int i=0;i<indexChar.length;i++){
            if(indexChar[i].equals(s)){
                scrollView.scrollTo(0,scrollY[i]);
            }
        }
    }

    @Override
    public void onScrollChanged(ObservableScrollView scrollView, int x, int y, int oldx, int oldy) {
//        Log.i("asferfer",scrollView.getScrollY()+"");
//        Log.i("asferfer",scrollView.getScrollX()+"");
    }

    /*   隐藏indexCenter布局线程   */
    private class OverlayThread implements Runnable {
        @Override
        public void run() {
            tvIndexCenter.setVisibility(View.GONE);
        }
    }
}
