package com.readboy.offline.view;

import android.content.Context;
import android.graphics.Rect;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.TouchDelegate;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.readboy.Dictionary.R;
import com.readboy.depict.widget.DemonstrationGroup;
import com.readboy.dict.DictWordView;
import com.readboy.library.gif.GifView;
import com.readboy.library.utils.PhoneticUtils;
import com.readboy.offline.adapter.WordListAdapter;
import com.readboy.offline.mode.DictModleButton;
import com.readboy.offline.mode.TitleNameModle;

/**
 * Created by Administrator on 2014/7/25.
 */
public class DictMainView extends LinearLayout implements View.OnClickListener,DictWordView.OnScrollTextViewListener{
    private DictModleButton dictModleButton;

    private boolean quCiState = false;          //是否可以取词

    private ImageView mainImageView;
//    private View mainDiction;                 //用来保存当前的View
    private ImageView titleName;
    private DictListView wordList;               //单词列表
    private DictWordView mExpView;				// 解释View
    private EditText editText;                  //输入框
    private Button cleanButton;                //清除的按钮
    private Button searchButton;               //搜索按钮
    private Button quCiButton;                 //取词按钮
    //private Button fanYi;                       //双向翻译按钮
    private Button faYin;                     //发音按钮
    private Button ziHao;                     //字号按钮
    private Button yuSu;                      //语速按钮
    private Button addNewWord;                 //添加新词库按钮
    private TextWatcher textWatcher;           //用来监听text
    private DemonstrationGroup biHuaView;       //笔画的界面
    private GifView dongMainView;               //动漫的界面
    private int startPosition = 0;               //
    private int currentPosition;

    private MainClickListener mainClickListener;
    private Context mContext;

    private static final int[] DICT_EXPLAIN_DEFAULT_BG = {	// 字典解释的默认背景图
            R.drawable.f_bh,   R.drawable.f_cy,   R.drawable.f_longman,
            R.drawable.f_dm,  R.drawable.f_ghy, R.drawable.f_syhy,
            R.drawable.f_xdhy, R.drawable.f_jmhy, R.drawable.f_xshy,
            R.drawable.f_syyh, R.drawable.f_yy,
    };

    public DictMainView(Context context) {
        super(context);
    }

    public DictMainView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DictMainView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void onFinishInflate() {
        super.onFinishInflate();
        mContext = getContext();
        init(mContext);
    }

    /*public DictMainView(Context context, int bookShelfID) {//此构造方法为DictActivity提供，如果不是则会直接返回
        mainDiction = LayoutInflater.from(context).inflate(bookShelfID, null);
        setMainClickListener((MainClickListener)context);
        init(context);
        mContext = context;
    }

    public DictMainView(Context context, MainClickListener mainClickListener, int bookShelfID){
        mainDiction = LayoutInflater.from(context).inflate(bookShelfID, null);
        setMainClickListener(mainClickListener);
        init(context);
        mContext = context;
    }*/


    private void init(Context context) {
        biHuaView = (DemonstrationGroup)findViewById(R.id.wordStrokes);
        dongMainView = (GifView)findViewById(R.id.dict_explain_gif);
        titleName = (ImageView)findViewById(R.id.dict_name_id);
        mainImageView = (ImageView)findViewById(R.id.view_bg);
        mainImageView.setClickable(true);
        mExpView = (DictWordView) findViewById(R.id.dict_exp_id);
        mExpView.setClickable(true);
        mExpView.setOnScrollTextViewListener(this);
        cleanButton = (Button)findViewById(R.id.dict_clear_id);
        cleanButton.setOnClickListener(this);
        searchButton = (Button)findViewById(R.id.dict_search_id);
        searchButton.setOnClickListener(this);
        editText = (EditText)findViewById(R.id.dict_input_id);
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
        wordList = (DictListView)findViewById(R.id.dict_list_id);
        wordList.setDictListOnScrollListener(new DictListView.DictListOnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    hideSoftInput();
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
        textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {                  //用来监听搜索时，输入框内的内容变化
                PhoneticUtils.checkCharseqence(s);
                mainImageView.setVisibility(View.GONE);
                if(!s.toString().trim().equals("")) {
                    cleanButton.setVisibility(View.VISIBLE);
                }else {
                    cleanButton.setVisibility(View.INVISIBLE);
                }
                mainClickListener.onKeyWordChange(wordList, s);
            }
        };
        editText.addTextChangedListener(textWatcher);
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                mainImageView.setVisibility(View.GONE);

                mainClickListener.keyEnter(v.getText().toString(), ((WordListAdapter) wordList.getAdapter()).getWordItem());

                return false;
            }
        });

        editText.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                int isVisiable = mainImageView.getVisibility();
                if (isVisiable == View.VISIBLE) {
                    mainImageView.setVisibility(View.GONE);
                    //mExpView.setBackgroundColor(Color.WHITE);
                    mainClickListener.listSelect(0, ((WordListAdapter) wordList.getAdapter()).getWordItem());
                }
            }
        });
        Rect rect = new Rect();
        if (editText.getLocalVisibleRect(rect)) {
            int offset = (((View)editText.getParent()).getHeight() - editText.getHeight())/2;
            if (offset > 0) {
                rect.set(rect.left, rect.top - offset, rect.right, rect.bottom + offset);
                TouchDelegate touchDelegate = new TouchDelegate(rect, editText);
                ((View)editText.getParent()).setTouchDelegate(touchDelegate);
            }
        }

        mainImageView.setOnClickListener(new OnClickListener() {//当点击主页背景图片时的相应操作
            @Override
            public void onClick(View v) {
                mainImageView.setVisibility(View.GONE);
                editText.removeTextChangedListener(textWatcher);
                if (((WordListAdapter) wordList.getAdapter()).getCount() > 0) {
                    editText.setText(((WordListAdapter) wordList.getAdapter()).getItem(0).toString());
                }
                editText.setSelection(editText.getText().toString().length());
                editText.addTextChangedListener(textWatcher);
                cleanButton.setVisibility(View.VISIBLE);
                //mExpView.setBackgroundColor(Color.WHITE);
                startPosition = ((WordListAdapter) wordList.getAdapter()).getStartId();
                mainClickListener.listSelect(startPosition+0,((WordListAdapter)wordList.getAdapter()).getWordItem());
            }

        });
        wordList.setOnItemClickListener(new AdapterView.OnItemClickListener() {//用来监听点击单词列表选项时的操作
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mainImageView.setVisibility(View.GONE);
                startPosition = ((WordListAdapter) parent.getAdapter()).getStartId();
                currentPosition = startPosition + position;
                cleanButton.setVisibility(View.VISIBLE);
                //mExpView.setBackgroundColor(Color.WHITE);
                editText.removeTextChangedListener(textWatcher);
                editText.setText(((WordListAdapter) parent.getAdapter()).getItem(position).toString());
                editText.setSelection(editText.getText().toString().length());
                editText.addTextChangedListener(textWatcher);
                ((WordListAdapter) parent.getAdapter()).setSelection(position);
                parent.getAdapter().getView(position, view, parent);
                mainClickListener.listSelect(currentPosition, ((WordListAdapter) parent.getAdapter()).getWordItem());
                hideSoftInput();
            }
        });
    }

    public void cancleQuciStatu() {
        if(quCiState) {
            quCiState = false;
            quCiButton.setBackgroundResource(R.drawable.dict_qc);
            mExpView.setSelectTextState(quCiState);
        }
    }

    public void hideSoftInput() {
        if(editText != null) {
            InputMethodManager imm = (InputMethodManager)mContext.getSystemService(mContext.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        }
    }

    public void setView(int dicID) {
        titleName.setBackgroundResource(TitleNameModle.DICT_IMAGE_VIEW[dicID]);
        titleName.setVisibility(View.VISIBLE);
        editText.removeTextChangedListener(textWatcher);
        editText.requestFocus();
        editText.setText("");
        editText.addTextChangedListener(textWatcher);

        if(quCiState) {
            quCiState = false;
            quCiButton.setBackgroundResource(R.drawable.dict_qc);
            mExpView.setSelectTextState(quCiState);
        }

        if(cleanButton.getVisibility() == View.VISIBLE) {
            cleanButton.setVisibility(View.INVISIBLE);
        }

        mExpView.clear();
        //mExpView.setBackgroundColor(Color.WHITE);

        //fanYi.setEnabled(false);
        faYin.setEnabled(false);
        ziHao.setEnabled(false);
        yuSu.setEnabled(false);
        addNewWord.setEnabled(false);
        if(mainImageView.getVisibility() == View.GONE) {
            mainImageView.setVisibility(View.VISIBLE);
            mainImageView.setBackgroundResource(DICT_EXPLAIN_DEFAULT_BG[dicID]);
        }else{
            mainImageView.setBackgroundResource(DICT_EXPLAIN_DEFAULT_BG[dicID]);
        }
    }

    public DictWordView getmExpView() {
        return mExpView;
    }

    public void cleanEditeFouces() {
        editText.clearFocus();
    }

    public void requestEditFouces() {
        editText.requestFocus();
    }

    public boolean getEditFoucesStatus() {
        return editText.isFocused();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id == R.id.dict_clear_id) {
            cleanButton.setVisibility(View.INVISIBLE);
            editText.setText("");
            wordList.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(),
                    SystemClock.uptimeMillis(), MotionEvent.ACTION_CANCEL, 0, 0, 0));	// 强制停止滚动
            wordList.setAdapter(((WordListAdapter)wordList.getAdapter()));
        }else if(id == R.id.dict_search_id) {
            mainClickListener.searchButton(editText);
        }else if(id == R.id.dict_quci_id) {
            int b = mainImageView.getVisibility();
            if(b == View.GONE) {
                if(quCiState) {
                    quCiState = false;
                    quCiButton.setBackgroundResource(R.drawable.dict_qc);
                }else {
                    quCiButton.setBackgroundResource(R.drawable.qc3);
                    quCiState = true;
                }
                mExpView.setSelectTextState(quCiState);
            }
            hideSoftInput();
        }else if(id == R.id.dict_sound_id) {
            mainClickListener.onClickFaYin(((WordListAdapter) wordList.getAdapter()).getWordItem());
        }else if(id == R.id.dict_font_id) {
            mainClickListener.onClickZiHao();
        }else if(id == R.id.dict_speed_id) {
            mainClickListener.onClickYuSu();
        }else if(id == R.id.dict_addnewword_id) {
            mainClickListener.addNewWordButton(currentPosition);
        }
    }

    /*public View getMainDiction() {
        return mainDiction;
    }*/

    public void setMainClickListener(MainClickListener mainClickListener) {
        this.mainClickListener = mainClickListener;
    }

    @Override
    public void getSelectText(String selectStr) {
        mainClickListener.selectText(selectStr);
    }



    @Override
    public void touchedView() {
        mainClickListener.getTouchScollTextView();
    }

    public GifView getDongMainView() {
        return dongMainView;
    }

    public DemonstrationGroup getBiHuaView() {
        return biHuaView;
    }

    public DictListView getWordList() {
        return wordList;
    }

    public DictModleButton getDictModleButton() {
        return dictModleButton;
    }

    public interface MainClickListener {
        //点击单词列表时调用的方法
        public void listSelect(int position, String str);
        //点击搜索按钮时调用的方法
        public void searchButton(EditText text);
        //当在输入框内的内容有改变时调用此方法
        public void onKeyWordChange(DictListView wordList, Editable s);
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

        public void keyEnter(String s, String itemString);

        public void getTouchScollTextView();
    }

    public void closeDictMain() {
        dictModleButton = null;
        if(cleanButton != null) {
            cleanButton.setOnClickListener(null);
            cleanButton = null;
        }
        titleName = null;
        if(searchButton != null) {
            searchButton.setOnClickListener(null);
            searchButton = null;
        }
        if(quCiButton != null) {
            quCiButton.setOnClickListener(null);
            quCiButton = null;
        }
        if(mainImageView != null) {
            mainImageView.setOnClickListener(null);
            mainImageView = null;
        }
        if(mExpView != null) {
            mExpView.clear();
        }
        /*if(fanYi != null) {
            fanYi.setOnClickListener(null);
            fanYi = null;
        }*/
        if(faYin != null) {
            faYin.setOnClickListener(null);
            faYin = null;
        }
        if(ziHao != null) {
            ziHao.setOnClickListener(null);
            ziHao = null;
        }
        if(yuSu != null) {
            yuSu.setOnClickListener(null);
            yuSu = null;
        }
        if(addNewWord != null) {
            addNewWord.setOnClickListener(null);
            addNewWord = null;
        }
        System.gc();
    }
}
