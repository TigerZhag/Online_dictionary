package com.readboy.online.test;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.readboy.Dictionary.R;
import com.readboy.online.model.HttpGetDataListener;
import com.readboy.online.model.HttpRequestTask;
import com.readboy.online.view.SynopsisView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity implements HttpGetDataListener {

    SynopsisView synopsisView;
    /*   汉字查询URL,请求参数request格式案例:F_grade=5&F_hanzi=啊&F_user_id=480703,参数中文需utf-8编码   */
    private static final String URL_CHINESE_CHARACTER = "http://dreamtest.strongwind.cn:7150/chinese/hanzi?";
    /*   异步任务对象   */
    private HttpRequestTask task;
    private List<String> list;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 22:

                    synopsisView.setmList(list);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String a= "啊";
        try {
            a = URLEncoder.encode(a,"utf-8");
            task = (HttpRequestTask) new HttpRequestTask(URL_CHINESE_CHARACTER,this).execute("F_grade=5&F_hanzi="+a+"&F_user_id=480703");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }



    }

    @Override
    public void getDataResult(String result) {
        try {
            synopsisView = (SynopsisView) findViewById(R.id.test);
            list = new ArrayList<String>();
            JSONObject jsonObject = new JSONObject(result);
            JSONArray jsonArray = jsonObject.getJSONArray("F_jianjie");
            for(int i=0;i<jsonArray.length();i++){
                list.add(jsonArray.get(i).toString());
            }
            Log.i("cadscs",list.toString());
            handler.sendEmptyMessage(22);
            list.add("啊");
            list.add("【a】");
            list.add("叹词，表示赞叹或者惊讶");
            list.add("");
            list.add("啊");
            list.add("【a】");
            list.add("叹词，表示赞叹或者惊讶");
            list.add("");
            list.add("啊");
            list.add("【a】");
            list.add("叹词，表示赞叹或者惊讶");
            list.add("叹词，表示赞叹或者惊讶");
            list.add("");
            list.add("啊");
            list.add("【a】");
            list.add("叹词，表示赞叹或者惊讶");
            list.add("叹词，表示赞叹或者惊讶");
            list.add("叹词，表示赞叹或者惊讶");
            synopsisView.setmList(list);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}