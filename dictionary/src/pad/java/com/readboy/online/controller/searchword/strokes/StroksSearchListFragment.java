package com.readboy.online.controller.searchword.strokes;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.readboy.Dictionary.R;
import com.readboy.online.model.HttpGetDataListener;
import com.readboy.online.model.HttpRequestTask;
import com.readboy.online.model.utils.DbUtil;
import com.readboy.online.view.FixGridLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class StroksSearchListFragment extends Fragment implements HttpGetDataListener, View.OnClickListener {

    /*   笔画列表URL,无参请求   */
    private static final String URL_STROKES_LIST = "http://dreamtest.strongwind.cn:7150/chinese/bihuaList";
    /*   网络请求异步任务对象   */
    private HttpRequestTask task;
    /*   json解析对象   */
    private JSONArray array;
    private JSONObject jsonObject;
    /*   数据库操作对象   */
    private DbUtil mDbUtil;
    private View viewRoot;
    private FixGridLayout stroksListLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        viewRoot = inflater.inflate(R.layout.fragment_bihua_list,null);
        initInfo();
        getStroksList();
        return viewRoot;
    }

    /**
     * 获取笔画列表,先从本地拿数据,没有再访问访问网络,需要注意的是这个列表版本应该隔一段时间刷新一次数据.
     */
    private void getStroksList(){
        mDbUtil = new DbUtil(getActivity());
        mDbUtil.init();
        List<String> stroksList = new ArrayList<String>();
        stroksList = mDbUtil.getBiHuaList();
        if(stroksList.size() == 0){
            task = (HttpRequestTask) new HttpRequestTask(URL_STROKES_LIST,this).execute("");
        }else {
            String tvBihuaText;
            String tvBihuaTextTag;
            for (int i=0;i<stroksList.size();i++){
                TextView tvBihua = new TextView(getActivity());
                tvBihuaText = stroksList.get(i).toString();
                tvBihuaTextTag = tvBihuaText.substring(0,tvBihuaText.length()-1);
                tvBihua.setText(tvBihuaText);
                tvBihua.setTag(tvBihuaTextTag);
                tvBihua.setBackgroundResource(R.drawable.btn_searchlist_item_bg);
                tvBihua.setTextSize(22);
                tvBihua.setPadding(10,5,10,5);
                tvBihua.setHeight(72);
                tvBihua.setWidth(96);
                tvBihua.setOnClickListener(this);
                tvBihua.setGravity(Gravity.CENTER);
                stroksListLayout.addView(tvBihua);
            }
        }
    }

    private void initInfo(){
        stroksListLayout = (FixGridLayout) viewRoot.findViewById(R.id.ll);
        FrameLayout.LayoutParams llp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        llp.gravity = Gravity.CENTER;
        stroksListLayout.setLayoutParams(llp);
        stroksListLayout.setmCellHeight(98);
        stroksListLayout.setmCellWidth(137);
        stroksListLayout.setGravity(Gravity.CENTER);
    }

    /*   重写接口方法获取异步请求得到的笔画列表   */
    @Override
    public void getDataResult(String result) {
        try {
            jsonObject = new JSONObject(result);
            Log.i("zzzzz","responseNo = " + jsonObject.getString("F_responseNo"));
            Log.i("zzzzz","responseMsg = " + jsonObject.getString("F_responseMsg"));
            array = jsonObject.getJSONArray("F_list");
            FixGridLayout.LayoutParams  lp = new FixGridLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            lp.gravity = Gravity.CENTER;
            for (int i = 0; i < array.length(); i++) {
                TextView tvBihua = new TextView(getActivity());
                tvBihua.setText(array.getJSONObject(i).getString("F_bihua")+"画");
                mDbUtil.insertBiHua(array.getJSONObject(i).getString("F_bihua")+"画");
                tvBihua.setTag(array.getJSONObject(i).getString("F_bihua"));
                tvBihua.setBackgroundResource(R.drawable.btn_searchlist_item_bg);
                tvBihua.setTextSize(22);
                tvBihua.setPadding(10,5,10,5);
                tvBihua.setHeight(72);
                tvBihua.setWidth(96);
                tvBihua.setOnClickListener(this);
                tvBihua.setLayoutParams(lp);
                stroksListLayout.addView(tvBihua);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        Intent toStroksResult = new Intent(getActivity(),StroksSearchResultActivity.class);
        toStroksResult.putExtra("stroks_num", v.getTag().toString());
        startActivity(toStroksResult);
    }
}
