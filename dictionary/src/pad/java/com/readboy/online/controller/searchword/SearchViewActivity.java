package com.readboy.online.controller.searchword;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.readboy.Dictionary.R;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class SearchViewActivity extends Activity implements View.OnClickListener{

    private static final int HANZI = 1;
    private static final int TERM = 2;
    private static final int IDIOM = 1;
    private static final int WORD = 1;
    private TextView tvCancel;
    private Button btnClearText,btnSearch;
    private EditText etSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_view);
        findViewById();
    }

    private void findViewById(){
        tvCancel = (TextView) this.findViewById(R.id.tv_search_cancel);
        tvCancel.setOnClickListener(this);
        btnClearText = (Button) this.findViewById(R.id.btn_clear_text);
        btnClearText.setOnClickListener(this);
        etSearch = (EditText) this.findViewById(R.id.et_search);
        addEdiTextChangeListener(etSearch);
        btnSearch = (Button) this.findViewById(R.id.btn_search);
        btnSearch.setOnClickListener(this);
    }

    private void addEdiTextChangeListener(EditText editText){
        editText.addTextChangedListener(new TextWatcher() {
            // TODO: 16-7-6 对输入数据的限制格式处理
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Log.i("reftr","beforeTextChanged");
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(etSearch.getText().toString().length() != 0){
                    btnClearText.setVisibility(View.VISIBLE);
                }else {
                    btnClearText.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                    Log.i("reftr","afterTextChanged");
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_search_cancel:
                finish();
                break;
            case R.id.btn_clear_text:
                String text;
                text = etSearch.getText().toString();
                int selection = etSearch.getSelectionStart();
                if(selection > 0 && text.length() != 0){
                    text = text.substring(0,selection-1)+text.substring(selection,text.length());
                    etSearch.setText(text);
                    etSearch.setSelection(selection-1);
                }
                break;
            case R.id.btn_search:
                // TODO: 16-7-6 要判断查询的是单词.成语.词语还是汉字.返回相应标示.再根据标示来请求数据
                String request;
                request = etSearch.getText().toString();
                Log.i("zazaz",request);
                try {
                    request = URLEncoder.encode(request,"utf-8");
                } catch (UnsupportedEncodingException e) {e.printStackTrace();

                }
                Intent toSearchResult = new Intent(this,SearchResultActivity.class);
                Log.i("zazaz",request);
                toSearchResult.putExtra("request",request);
                startActivity(toSearchResult);
                break;
        }
    }
}
