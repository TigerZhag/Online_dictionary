package com.readboy.online.controller.searchword;

import android.graphics.Typeface;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.readboy.Dictionary.R;
import com.readboy.online.model.HttpGetDataListener;
import com.readboy.online.model.HttpRequestTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by liujiawei on 16-7-7.
 */
public class ZhSearchResultFragment extends Fragment implements HttpGetDataListener{

    /*   汉字查询URL,请求参数request格式案例:F_grade=5&F_hanzi=啊&F_user_id=480703,参数中文需utf-8编码   */
    private static final String URL_CHINESE_CHARACTER = "http://dreamtest.strongwind.cn:7150/chinese/hanzi?";
    /*   请求参数名   */
    private static final String URL_PARAMS_GRADE = "F_grade=";
    private static final String URL_PARAMS_USER_ID = "F_user_id=";
    private static final String URL_PARAMS_HANZI= "F_hanzi=";
    /*   请求参数值   */
    private int grade = 5;
    private int userId = 480703;
    private String request;
    /*   带参数请求地址   */
    private String urlRequest;
    /*   异步任务对象   */
    private HttpRequestTask task;
    /*   布局控件   */
    private TextView tvWuBi,tvStrokes,tvRedical,tvResultWord;
    /*   json解析对象   */
    private JSONObject jsonObject;
    private JSONArray jsonArray;
    private View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_zh_search_result,null);
        findViewById();
        initInfo();
        return rootView;
    }

    private void findViewById(){
        tvRedical = (TextView) rootView.findViewById(R.id.tv_redical);
        tvStrokes = (TextView) rootView.findViewById(R.id.tv_strokes);
        tvWuBi = (TextView) rootView.findViewById(R.id.tv_wubi);
        tvResultWord = (TextView) rootView.findViewById(R.id.tv_result_word);
    }

    private void initInfo(){
        /*   设置字体样式   */
        Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), "font/2.TTF");
        tvResultWord.setTypeface(typeface);

        request = getArguments().getString("request");
        Log.i("zazaz",request);
        urlRequest = URL_PARAMS_GRADE+grade+"&"+URL_PARAMS_HANZI+request+"&"+URL_PARAMS_USER_ID+userId;
        Log.i("zazaz",urlRequest);
        task = (HttpRequestTask) new HttpRequestTask(URL_CHINESE_CHARACTER,this).execute(urlRequest);
    }

    @Override
    public void getDataResult(String result) {
        // TODO: 16-7-6 判断返回码 10000 成功 10905服务器记录字错误但依然有数据返回,客户端需要post一次记录给服务器.
        Log.i("zazaz",result.toString());
        try {
            jsonObject = new JSONObject(result);
            Log.i("zazaz","F_responseNo="+jsonObject.getString("F_responseNo")+" F_responseMsg"+jsonObject.getString("F_responseMsg"));
            tvWuBi.setText(jsonObject.getString("F_wubi"));
            tvStrokes.setText(jsonObject.getString("F_bihua"));
            tvRedical.setText(jsonObject.getString("F_bushou"));
            tvResultWord.setText(jsonObject.getString("F_zi"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
