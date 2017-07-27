package com.readboy.online;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.readboy.Dictionary.DictActivity;
import com.readboy.Dictionary.R;
import com.readboy.online.controller.searchword.SearchListActivity;
import com.readboy.online.controller.searchword.SearchViewActivity;
import com.readboy.online.controller.hotsearchword.EveryoneInSearchFragment;
import com.readboy.online.model.HttpGetDataListener;
import com.readboy.online.model.HttpRequestTask;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class OnlineMainActivity extends FragmentActivity implements HttpGetDataListener, View.OnClickListener {

    /*   查字类型   */
    private static final int TO_PINYIN_SEARCH_LIST = 1;
    private static final int TO_STROKS_SEARCH_LIST = 2;
    private static final int TO_REDICAL_SEARCH_LIST = 3;
    private static final int TO_FINGER_SEARCH_LIST = 4;
    /*   大家都再查URL   */
    private static final String URL_HOT_WORD_SEARCH = "http://dreamtest.strongwind.cn:7140/v1/hotSearchWord?";
    /*   热搜词集合   */
    private ArrayList<String> list;
    /*   大家都再查Fragment   */
    private EveryoneInSearchFragment everyoneInSearch;
    /*   请求参数串   */
    private String request;
    /*   网络请求异步任务对象   */
    private HttpRequestTask task;
    /*   json解析对象   */
    private JSONArray array;
    private JSONObject jsonObject;
    /*   控件   */
    private TextView ivToSearch;
    private ImageView ivToPinyin,ivToRedical,ivToStroks,ivToFinger;
    private Button btnCopyRight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_online);
        initObjects();
        findViewById();
        initInfo();
    }

    private void findViewById(){
        ivToSearch = (TextView) findViewById(R.id.iv_to_search);
        ivToSearch.setOnClickListener(this);
        ivToFinger = (ImageView) findViewById(R.id.iv_main_shouxie);
        ivToPinyin = (ImageView) findViewById(R.id.iv_main_pinyin);
        ivToRedical = (ImageView) findViewById(R.id.iv_main_bushou);
        ivToStroks = (ImageView) findViewById(R.id.iv_main_bihua);
        ivToFinger.setOnClickListener(this);
        ivToPinyin.setOnClickListener(this);
        ivToRedical.setOnClickListener(this);
        ivToStroks.setOnClickListener(this);
        btnCopyRight = (Button) findViewById(R.id.btn_copyright);
        btnCopyRight.setOnClickListener(this);

    }

    private void initObjects(){
        list = new ArrayList<String>();
        everyoneInSearch = new EveryoneInSearchFragment();
    }

    private void initInfo(){
        //年级和用户id之后要用jar包获取
        request = "F_grade=3";
        task = (HttpRequestTask) new HttpRequestTask(URL_HOT_WORD_SEARCH,this).execute(request);
    }

    /*   等布局完成后再弄缓存,每隔一天刷新一次数据(从网络获取热词),一天之内从本地缓存拿   */
    @Override
    public void getDataResult(String result) {
        if(result == null){
            Log.i("OnlineMainActivity","result is null");
        }else
            try {
                Log.i("zazazaza",result.toString());
                jsonObject = new JSONObject(result);
                int i = jsonObject.getInt("F_responseNo");
                String ii = jsonObject.getString("F_responseMsg");
                array = jsonObject.getJSONArray("F_list");
                for(int j=0;j<array.length();j++){
                    list.add(array.getString(j));
                }
                Bundle bundle = new Bundle();
                bundle.putStringArrayList("hotWord",list);
                everyoneInSearch.setArguments(bundle);
                getSupportFragmentManager().beginTransaction().add(R.id.hot_word_display_layout,everyoneInSearch).commit();
            } catch (JSONException e) {
                e.printStackTrace();
                Log.i("zazzaa",e.toString());
            }
    }

    @Override
    public void onClick(View v) {
        Intent toSearchList = new Intent(this, SearchListActivity.class);
        switch (v.getId()){
            case R.id.iv_to_search:
                Intent toSearch = new Intent(this, SearchViewActivity.class);
                startActivity(toSearch);
                break;
            case R.id.iv_main_bihua:
                toSearchList.putExtra("list_type",TO_STROKS_SEARCH_LIST);
                startActivity(toSearchList);
                break;
            case R.id.iv_main_bushou:
                toSearchList.putExtra("list_type",TO_REDICAL_SEARCH_LIST);
                startActivity(toSearchList);
                break;
            case R.id.iv_main_pinyin:
                toSearchList.putExtra("list_type",TO_PINYIN_SEARCH_LIST);
                startActivity(toSearchList);
                break;
            case R.id.iv_main_shouxie:
                toSearchList.putExtra("list_type",TO_FINGER_SEARCH_LIST);
                startActivity(toSearchList);
                break;
            case R.id.btn_copyright:
                Intent toCopyRight = new Intent(this, DictActivity.class);
                startActivity(toCopyRight);
        }
    }
}