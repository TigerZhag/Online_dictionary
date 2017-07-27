package com.readboy.offline.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.readboy.Dictionary.R;
import com.readboy.offline.adapter.BiHuaListAdapter;
import com.readboy.depict.widget.DemonstrationGroup;
import com.readboy.dict.DictWordView;
import com.readboy.offline.mode.DictModleButton;
import com.readboy.offline.mode.TitleNameModle;

/**
 * Editor: sgc
 * Date: 2015/01/23
 */
public class DictSearchBiHuaView extends LinearLayout implements View.OnClickListener, DictWordView.OnScrollTextViewListener {

    private DictModleButton dictModleButton;

    private boolean quCiState = false;          //是否可以取词

    //    private View mainDiction;                 //用来保存当前的View
    private ImageView titleName;
    private ListView wordList;               //单词列表
    private DictWordView mExpView;				// 解释View
    private Button quCiButton;                 //取词按钮
    //private Button fanYi;                       //双向翻译按钮
    private Button faYin;                     //发音按钮
    private Button ziHao;                     //字号按钮
    private Button yuSu;                      //语速按钮
    private Button addNewWord;                 //添加新词库按钮
    private DemonstrationGroup biHuaView;       //笔画的界面

    private DictSearchBiHuaClickListener dictSearchBiHuaClickListener;
    private int currentPosition;

    private static final int[] DICT_EXPLAIN_DEFAULT_BG = {	// 字典解释的默认背景图
            R.drawable.f_bh,   R.drawable.f_cy,   R.drawable.f_longman,
            R.drawable.f_dm,  R.drawable.f_ghy, R.drawable.f_syhy,
            R.drawable.f_xdhy, R.drawable.f_jmhy, R.drawable.f_xshy,
            R.drawable.f_syyh, R.drawable.f_yy,
    };

    public void setDictSearchBiHuaClickListener(DictSearchBiHuaClickListener dictSearchBiHuaClickListener) {
        this.dictSearchBiHuaClickListener = dictSearchBiHuaClickListener;
    }

    public DictSearchBiHuaView(Context context) {
        super(context);
    }

    public DictSearchBiHuaView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DictSearchBiHuaView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    private void init() {
        biHuaView = (DemonstrationGroup)findViewById(R.id.wordStrokes);
        titleName = (ImageView)findViewById(R.id.dict_name_id);
        mExpView = (DictWordView)findViewById(R.id.dict_exp_id);
        mExpView.setClickable(true);
        mExpView.setOnScrollTextViewListener(this);
        quCiButton = (Button)findViewById(R.id.dict_quci_id);
        quCiButton.setOnClickListener(this);
        faYin = (Button)findViewById(R.id.dict_sound_id);
        faYin.setOnClickListener(this);
        ziHao = (Button)findViewById(R.id.dict_font_id);
        ziHao.setOnClickListener(this);
        yuSu = (Button)findViewById(R.id.dict_speed_id);
        yuSu.setOnClickListener(this);
        addNewWord = (Button)findViewById(R.id.dict_addnewword_id);
        addNewWord.setOnClickListener(this);
        dictModleButton = new DictModleButton(faYin,ziHao,yuSu,addNewWord);
        wordList = (ListView)findViewById(R.id.dict_list_id);
        wordList.setOnItemClickListener(new AdapterView.OnItemClickListener() {//用来监听点击单词列表选项时的操作
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (wordList != null && wordList.getAdapter() != null) {
                    ((BiHuaListAdapter) parent.getAdapter()).setSelection(position);
                    ((BiHuaListAdapter) parent.getAdapter()).notifyDataSetChanged();
                }
                if (dictSearchBiHuaClickListener != null) {
                    currentPosition = position;
                    dictSearchBiHuaClickListener.listSelect(position);
                }
            }
        });
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        init();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id == R.id.dict_quci_id) {
            if(quCiState) {
                quCiState = false;
                quCiButton.setBackgroundResource(R.drawable.dict_qc);
            }else {
                quCiButton.setBackgroundResource(R.drawable.qc3);
                quCiState = true;
            }
            mExpView.setSelectTextState(quCiState);
        }else if(id == R.id.dict_sound_id) {
            if (dictSearchBiHuaClickListener != null) {
                dictSearchBiHuaClickListener.onClickFaYin(null);
            }
        }else if(id == R.id.dict_font_id) {
            if (dictSearchBiHuaClickListener != null) {
                dictSearchBiHuaClickListener.onClickZiHao();
            }
        }else if(id == R.id.dict_speed_id) {
            if (dictSearchBiHuaClickListener != null) {
                dictSearchBiHuaClickListener.onClickYuSu();
            }
        }else if(id == R.id.dict_addnewword_id) {
            if (dictSearchBiHuaClickListener != null) {
                dictSearchBiHuaClickListener.addNewWordButton(currentPosition);
            }
        }

    }

    @Override
    public void getSelectText(String selectStr) {
        dictSearchBiHuaClickListener.selectText(selectStr);
    }

    @Override
    public void touchedView() {
        dictSearchBiHuaClickListener.getTouchScollTextView();
    }

    public void cancleQuciStatu() {
        if (quCiButton != null) {
            quCiState = false;
            quCiButton.setBackgroundResource(R.drawable.dict_qc);
            mExpView.setSelectTextState(quCiState);
        }
    }

    public void setView(int dicID) {
        titleName.setBackgroundResource(TitleNameModle.DICT_IMAGE_VIEW[dicID]);
        titleName.setVisibility(View.VISIBLE);
        if(quCiState) {
            quCiState = false;
            quCiButton.setBackgroundResource(R.drawable.dict_qc);
            mExpView.setSelectTextState(quCiState);
        }

        mExpView.clear();

        faYin.setEnabled(false);
        ziHao.setEnabled(false);
        yuSu.setEnabled(false);
        addNewWord.setEnabled(false);
    }

    public DictWordView getmExpView() {
        return mExpView;
    }

    public DemonstrationGroup getBiHuaView() {
        return biHuaView;
    }

    public ListView getWordList() {
        return wordList;
    }

    public DictModleButton getDictModleButton() {
        return dictModleButton;
    }

    public interface DictSearchBiHuaClickListener {
        //点击单词列表时调用的方法
        public void listSelect(int position);
        //当点击发信按钮调用的方法
        public void onClickFaYin(String str);
        //当点击字号按钮调用的方法
        public void onClickZiHao();
        //点击语速按钮调用的方法
        public void onClickYuSu();
        //点击新增按钮
        public void addNewWordButton(int position);
        //取词得到选择的内容
        public void selectText(String selectStr);

        public void getTouchScollTextView();
    }
}
