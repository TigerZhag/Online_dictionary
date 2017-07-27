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
import com.readboy.depict.widget.DemonstrationGroup;
import com.readboy.dict.DictWordView;
import com.readboy.offline.adapter.WordListAdapter;
import com.readboy.offline.mode.DictModleButton;
import com.readboy.offline.mode.TitleNameModle;

/**
 * Created by Administrator on 2014/7/30.
 */
public class DictJumpView extends LinearLayout implements View.OnClickListener,DictWordView.OnScrollTextViewListener{

    private DictWordView textView;
    private DemonstrationGroup biHuaView;
    private ImageView titleName;
    private ListView wordList;
    private Button quCiButton;
    private Button faYin;
    private Button ziHao;
    private Button yuSu;
    private Button addNewWord;

    private DictModleButton jumpButtonModle;

    private boolean quCiState = false;
    private int startPosition = 0;
    private int currentPosition = 0;

    private JumpListener jumpListener;

    public DictJumpView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public DictJumpView(Context context) {
        super(context);
    }

    public DictJumpView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void onFinishInflate() {
        super.onFinishInflate();
        init(getContext());
    }

    private void init(Context context){
        titleName = (ImageView)findViewById(R.id.dict_name_id);
        wordList = (ListView)findViewById(R.id.dict_list_id);
        wordList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startPosition = ((WordListAdapter) parent.getAdapter()).getStartId();
                currentPosition = startPosition + position;
                //textView.setBackgroundColor(Color.WHITE);
                ((WordListAdapter) parent.getAdapter()).setSelection(position);
                parent.getAdapter().getView(position, view, parent);
                jumpListener.listSelect(currentPosition, textView, jumpButtonModle);
            }
        });
        biHuaView = (DemonstrationGroup)findViewById(R.id.wordStrokes);
        textView = (DictWordView)findViewById(R.id.dict_exp_id);
        textView.setOnScrollTextViewListener(this);
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

        jumpButtonModle = new DictModleButton(faYin,ziHao,yuSu,addNewWord);

    }

    public void cancleQuciStatu() {
        if(quCiState) {
            quCiState = false;
            quCiButton.setBackgroundResource(R.drawable.dict_qc);
            textView.setSelectTextState(quCiState);
        }
    }

    public void setView(int dicID) {
        titleName.setBackgroundResource(TitleNameModle.DICT_IMAGE_VIEW[dicID]);
        titleName.setVisibility(View.VISIBLE);
        if(quCiState) {
            quCiState = false;
            quCiButton.setBackgroundResource(R.drawable.dict_qc);
            textView.setSelectTextState(quCiState);
        }
        textView.clear();
        //textView.setBackgroundColor(Color.WHITE);
        faYin.setEnabled(false);
        ziHao.setEnabled(false);
        yuSu.setEnabled(false);
        addNewWord.setEnabled(false);
    }

    public ListView getWordList() {
        return wordList;
    }


    public void setDicID(int dicID) {
        titleName.setBackgroundResource(TitleNameModle.DICT_IMAGE_VIEW[dicID]);
        titleName.setVisibility(View.VISIBLE);
    }

    public DictWordView getTextView() {
        return textView;
    }

    public DictModleButton getJumpButtonModle() {
        return jumpButtonModle;
    }

    public DemonstrationGroup getBiHuaView() {
        return biHuaView;
    }

    @Override
    public void getSelectText(String selectStr) {
        jumpListener.selectText(selectStr,textView);
    }

    @Override
    public void touchedView() {
        jumpListener.getTouchScollTextView();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id == R.id.dict_sound_id) {
            jumpListener.onClickFaYin();
        }else if(id == R.id.dict_font_id) {
            jumpListener.onClickZiHao(textView);
        }else if(id == R.id.dict_speed_id) {
            jumpListener.onClickYuSu();
        }else if(id == R.id.dict_addnewword_id) {
            jumpListener.addNewWordButton();
        }else if(id == R.id.dict_quci_id) {
            if(quCiState) {
                quCiState = false;
                quCiButton.setBackgroundResource(R.drawable.qc1);
            }else {
                quCiButton.setBackgroundResource(R.drawable.qc3);
                quCiState = true;
            }
            textView.setSelectTextState(quCiState);
        }
    }

    public void setJumpListener(JumpListener jumpListener) {
        this.jumpListener = jumpListener;
    }

    public interface JumpListener{
        //点击单词列表时调用的方法
        public void listSelect(int position, DictWordView scrollView, DictModleButton modleButton);
        public void selectText(String selectStr, View scrollTextView);
        //当点击发信按钮调用的方法
        public void onClickFaYin();
        //当点击字号按钮调用的方法
        public void onClickZiHao(DictWordView scrollView);
        //点击语速按钮调用的方法
        public void onClickYuSu();
        //点击新增按钮
        public void addNewWordButton();

        public void getTouchScollTextView();
    }
}
