package com.readboy.online.controller.searchword;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.readboy.Dictionary.R;

public class SearchResultActivity extends Activity implements View.OnClickListener {

    /*   收藏标示   */
    private boolean collecting = false;
    /*   取词标示   */
    private boolean getWorded = false;
    /*   布局控件   */
    private Button btnClearText,btnSearch;
    private EditText etSearch;
    private Button btnCollect,btnGetWord;
    private String request;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);
        findViewById();
        initInfo();

    }

    private void initInfo(){
        // TODO: 16-7-6 判断输入数据类型选择搜索结果页面fragment,成语.词语或英文句子跳版权词典.单词跳英文界面.汉字跳中文界面
        request = getIntent().getStringExtra("request");
        ZhSearchResultFragment resultFragment = new ZhSearchResultFragment();
        Bundle bundle = new Bundle();
        bundle.putString("request",request);
        resultFragment.setArguments(bundle);
        getFragmentManager().beginTransaction().add(R.id.layout_display_search_result,resultFragment).commit();
    }

    private void findViewById(){
        btnClearText = (Button) this.findViewById(R.id.btn_clear_text);
        btnClearText.setOnClickListener(this);
        etSearch = (EditText) this.findViewById(R.id.et_search);
        addEdiTextChangeListener(etSearch);
        btnSearch = (Button) this.findViewById(R.id.btn_search);
        btnSearch.setOnClickListener(this);
        btnCollect = (Button) findViewById(R.id.btn_collect);
        btnCollect.setOnClickListener(this);
        btnGetWord = (Button) findViewById(R.id.btn_get_word);
        btnGetWord.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
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
            case R.id.btn_collect:
                // TODO: 16-7-6 功能再续
                if(!collecting){
                    btnCollect.setBackgroundResource(R.drawable.img_collected);
                    collecting = true;
                }else {
                    btnCollect.setBackgroundResource(R.drawable.img_collect);
                    collecting = false;
                }
                break;
            case R.id.btn_get_word:
                // TODO: 16-7-6 功能再续
                if(!getWorded){
                    btnGetWord.setBackgroundResource(R.drawable.img_get_wording);
                    getWorded = true;
                }else {
                    btnGetWord.setBackgroundResource(R.drawable.img_get_word);
                    getWorded = false;
                }
                break;
        }
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
}
