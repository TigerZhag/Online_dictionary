package com.readboy.online.controller.searchword.redical;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.readboy.Dictionary.R;
import com.readboy.online.controller.searchword.pinyin.PinYinSearchResultActivity;
import com.readboy.online.model.HttpGetDataListener;
import com.readboy.online.model.HttpRequestTask;
import com.readboy.online.model.utils.DbUtil;
import com.readboy.online.view.AutoNextLineLinearlayout;
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


public class RedicalSearchListFragment extends Fragment implements HttpGetDataListener, View.OnClickListener, OnTouchingLetterChangedListener, ScrollViewListener {

    /*   部首列表URL,无参请求   */
    private static final String URL_RADICAL_LIST = "http://dreamtest.strongwind.cn:7150/chinese/bushouList";
    /*   网络请求异步任务对象   */
    private HttpRequestTask task;
    /*   json解析对象   */
    private JSONArray array;
    private JSONObject jsonObject;
    /*   数据库操作对象   */
    private DbUtil mDbUtil;
    /*   笔画数列表   */
    private String[] mStroksNumStringChinese = new String[]{"一","二","三","四","五","六","七","八","九","十","十一","十二","十三","十四","十五"};;
    private String[] mStroksNumString = new String[]{"1","2","3","4","5","6","7","8","9","10","11","12","13","14","15",};
    private View viewRoot;
    /*   笔画对应部首列表   */
    private HashMap<String, List<String>> redicalMap;
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
    private int[] scrollY={0,140,435,800,1163,1458,1744,1958,2176,2308,2448,2464,2464,2464,2464};
    /*   索引表对应的内容   */
    private String[] indexChar = {
            "a", "b", "c", "d", "e", "f", "g", "h",
            "j", "k", "l", "m", "n", "o", "p", "q",
            "r", "s", "t", "w", "x", "y", "z"
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        viewRoot =  inflater.inflate(R.layout.fragment_redical_search_list, container, false);
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

    private void initInfo(){
        mDbUtil = new DbUtil(getActivity());
        mDbUtil.init();
        parentLayout = (LinearLayout) viewRoot.findViewById(R.id.ll_redical_viewroot);

        ruler = (RulerWidget) viewRoot.findViewById(R.id.sidrbar_redical);
        ruler.setOnTouchingLetterChangedListener(this);
        tvIndexCenter = (TextView) viewRoot.findViewById(R.id.tvCenter);
        initOverlay();
        scrollView = (ObservableScrollView) viewRoot.findViewById(R.id.scroll_redical);
        scrollView.setScrollViewListener(this);
        ruler.setIndexStr(mStroksNumStringChinese);
    }

    private void getData(){
        setRedicalList();
        for(int i=0;i<mStroksNumString.length;i++){
            addTvBiHua(mStroksNumStringChinese[i]);
            addRedicalList(mStroksNumString[i]);
        }
    }

    private List<String> getBushouList(String bihua){
        List<String> list = new ArrayList<String>();
        list = mDbUtil.getRedicalFromBihua(bihua);
        if(list.size() == 0){
            task = (HttpRequestTask) new HttpRequestTask(URL_RADICAL_LIST,this).execute("");
        }
        return list;
    }

    /*   设置首字母对应的拼音列表   */
    private void setRedicalList() {
        if(redicalMap == null){
            redicalMap = new HashMap<String, List<String>>();
        }
        for (String bihua : mStroksNumString){
            redicalMap.put(bihua ,getBushouList(bihua));
        }
    }

    private void addTvBiHua(String str){
        TextView tvBiHua = new TextView(getActivity());
        tvBiHua.setText(str);
        tvBiHua.setTextSize(20);
        tvBiHua.setTextColor(Color.parseColor("#ff8401"));
        tvBiHua.setPadding(20,20,0,12);
        parentLayout.addView(tvBiHua);
    }

    private void addRedicalList(String str){
        List<String> list = redicalMap.get(str);
        AutoNextLineLinearlayout fixGridLayout = new AutoNextLineLinearlayout(getActivity());
        for(int i=0;i<list.size();i++){
            TextView redical = new TextView(getActivity());
            String pinyinTextTag = list.get(i);
            redical.setText(pinyinTextTag);
            redical.setTag(pinyinTextTag);
            redical.setBackgroundResource(R.drawable.btn_searchlist_item_bg);
            redical.setTextSize(22);
            redical.setPadding(10,5,10,5);
            redical.setHeight(72);
            redical.setWidth(96);
            redical.setOnClickListener(this);
            redical.setGravity(Gravity.CENTER);
            fixGridLayout.addView(redical);
        }
        parentLayout.addView(fixGridLayout);
    }

    @Override
    public void getDataResult(String result) {
        Log.i("sadvdsvcsd",result.toString());
        try {
            jsonObject = new JSONObject(result);
            array = jsonObject.getJSONArray("F_list");
            for(int i=0;i<array.length();i++){
                Log.i("sadvdsvcsd",array.getJSONObject(i).getString("F_bihua"));
                mDbUtil.insertRedical(array.getJSONObject(i).getString("F_bihua"),array.getJSONObject(i).getString("F_bushou"));
            }
            // TODO: 16-7-12本地数据addview还没取消 有点浪费 记得取消
            getData();
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
    public void onClick(View v) {
        Intent toRedicalResult = new Intent(getActivity(),RedicalSearchResultActivity.class);
        toRedicalResult.putExtra("redical", v.getTag().toString());
        startActivity(toRedicalResult);
    }

    @Override
    public void onTouchingLetterChanged(String s) {
        tvIndexCenter.setText(s);
        tvIndexCenter.setVisibility(View.VISIBLE);
        handler.removeCallbacks(overlayThread);
        handler.postDelayed(overlayThread, 1500);
        for(int i=0;i<mStroksNumStringChinese.length;i++){
            if(mStroksNumStringChinese[i].equals(s)){
                scrollView.scrollTo(0,scrollY[i]);
            }
        }
    }

    @Override
    public void onScrollChanged(ObservableScrollView scrollView, int x, int y, int oldx, int oldy) {
//                Log.i("asferfer",scrollView.getScrollY()+"");
    }

    /*   隐藏indexCenter布局线程   */
    private class OverlayThread implements Runnable {
        @Override
        public void run() {
            tvIndexCenter.setVisibility(View.GONE);
        }
    }
}