package com.readboy.offline.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.readboy.Dictionary.R;
import com.readboy.depict.widget.DemonstrationGroup;
import com.readboy.dict.DictWordView;
import com.readboy.offline.mode.DictModleButton;
import com.readboy.offline.mode.TitleNameModle;

/**
 * Created by Administrator on 2014/7/30.
 */
public class DictSearchView extends LinearLayout implements View.OnClickListener,DictWordView.OnScrollTextViewListener{
//    private View qiCiView;
    private ImageView titleName;                  //显示标题的的图片
    private Button quCiButton;                    //取词的按钮
    private DemonstrationGroup biHuaView;         //用来显示笔画的界面
    private DictWordView mTextView;             //用来显示内容的部分
    private Button faYin;                         //发音按钮
    private Button ziHao;                         //字号按钮
    private Button yuSu;                          //语速按钮
    private Button addNewWord;                    //添加生词按钮
    private QuCiListener mQuCiListener;
    private DictModleButton quCiButtonModle;

    private boolean quCiState = false;

    public DictSearchView(Context context) {
        super(context);
    }

    public DictSearchView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DictSearchView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void onFinishInflate() {
        super.onFinishInflate();
        init(getContext());
    }

    private void init(Context context) {
        titleName = (ImageView)findViewById(R.id.dict_name_id);
        quCiButton = (Button)findViewById(R.id.dict_quci_id);
        quCiButton.setOnClickListener(this);
        biHuaView = (DemonstrationGroup)findViewById(R.id.search_bihua);
        mTextView = (DictWordView)findViewById(R.id.dict_exp_id);
        mTextView.setOnScrollTextViewListener(this);
        faYin = (Button)findViewById(R.id.dict_sound_id);
        faYin.setOnClickListener(this);
        ziHao = (Button)findViewById(R.id.dict_font_id);
        ziHao.setOnClickListener(this);
        yuSu = (Button)findViewById(R.id.dict_speed_id);
        yuSu.setOnClickListener(this);
        addNewWord = (Button)findViewById(R.id.dict_addnewword_id);
        addNewWord.setOnClickListener(this);

        quCiButtonModle = new DictModleButton(faYin,ziHao,yuSu,addNewWord);
    }

    public void cancleQuciStatu() {
        if(quCiState) {
            quCiState = false;
            quCiButton.setBackgroundResource(R.drawable.qc_select_1);
            mTextView.setSelectTextState(quCiState);
        }
    }

    public void setView(int dicID) {
        titleName.setBackgroundResource(TitleNameModle.DICT_IMAGE_VIEW[dicID]);
        titleName.setVisibility(View.VISIBLE);
        if(quCiState) {
            quCiState = false;
            quCiButton.setBackgroundResource(R.drawable.qc_select_1);
            mTextView.setSelectTextState(quCiState);
        }
        mTextView.clear();
        //mTextView.setBackgroundColor(Color.WHITE);
        if(biHuaView != null) {
            mQuCiListener.cleanBiHua();
        }
        faYin.setEnabled(false);
        ziHao.setEnabled(false);
        yuSu.setEnabled(false);
        addNewWord.setEnabled(false);
    }

    public void setDicId(int dicID) {
        titleName.setBackgroundResource(TitleNameModle.DICT_IMAGE_VIEW[dicID]);
        titleName.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id == R.id.dict_sound_id) {
            mQuCiListener.onClickFaYin();
        }else if(id == R.id.dict_font_id) {
            mQuCiListener.onClickZiHao(mTextView);
        }else if(id == R.id.dict_speed_id) {
            mQuCiListener.onClickYuSu();
        }else if(id == R.id.dict_addnewword_id) {
            mQuCiListener.addNewWordButton();
        }else if(id == R.id.dict_quci_id) {
            if(quCiState) {
                quCiState = false;
                quCiButton.setBackgroundResource(R.drawable.qc_select_1);
            }else {
                quCiButton.setBackgroundResource(R.drawable.qc_select_3);
                quCiState = true;
            }
            mTextView.setSelectTextState(quCiState);
        }
    }

    public DemonstrationGroup getBiHuaView () {
        return biHuaView;
    }

    public DictModleButton getQuCiButtonModle() {
        return quCiButtonModle;
    }

    public DictWordView getmTextView() {
        return mTextView;
    }

    public void setmQuCiListener(QuCiListener quCiListener) {
        this.mQuCiListener = quCiListener;
    }

    @Override
    public void getSelectText(String selectStr) {
        mQuCiListener.selectText(selectStr,mTextView);
    }

    @Override
    public void touchedView() {
        mQuCiListener.getTouchScollTextView();
    }

    public interface QuCiListener{
        //当点击发信按钮调用的方法
        public void onClickFaYin();
        //当点击字号按钮调用的方法
        public void onClickZiHao(DictWordView scrollView);
        //点击语速按钮调用的方法
        public void onClickYuSu();
        //点击新增按钮
        public void addNewWordButton();
        //取词得到选择的内容
        public void selectText(String selectStr, View scrollTextView);

        public void cleanBiHua();

        public void getTouchScollTextView();
    }

}
