package com.readboy.offline.fragment;

import android.content.Intent;
import android.database.ContentObserver;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.readboy.Dictionary.R;
import com.readboy.dict.DictWordView;
import com.readboy.library.search.DictWordSearch;
import com.readboy.offline.adapter.WordsAdapter;
import com.readboy.offline.data.CollectionManager;
import com.readboy.offline.dialog.ReciteDialog;
import com.readboy.offline.mode.WordInfo;

import static com.readboy.offline.data.CollectionManager.YY_CHS;
import static com.readboy.offline.data.CollectionManager.YY_ENG;
import static com.readboy.offline.data.CollectionManager.clear;
import static com.readboy.offline.data.CollectionManager.getChineseWords;
import static com.readboy.offline.data.CollectionManager.getChineseWordsNumber;
import static com.readboy.offline.data.CollectionManager.getEnglishWords;
import static com.readboy.offline.data.CollectionManager.getEnglishWordsNumber;
import static com.readboy.offline.data.CollectionManager.getReadableWords;
import static com.readboy.offline.data.CollectionManager.getReadableWordsNumber;
import static com.readboy.offline.data.CollectionManager.sUri;

import java.util.ArrayList;
import java.util.List;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

/**
 * Created by Administrator on 14-11-19.
 */
public class NewWordFragment extends BaseDictFragment implements View.OnClickListener, AdapterView.OnItemClickListener,
        DictWordView.OnScrollTextViewListener, WordsAdapter.OnDeleteClickListener{

    private static final int DIALOG_DELETE_WORD = 0x01;

    private ListView mWordList;
    private DictWordView mContentView;
    private View mChinese;
    private View mEnglish;
    private View mPronounce;
    private View mRecite;
    private View mClear;
    private TextView mLibName;
    private CheckBox mSelectMode;

    private String[] mLibNames;
    private WordsObserver mWordsObserver;
    private WordsAdapter mWordsAdapter;
    private boolean misChinese;
    private WordInfo mCurrentWord;
    private boolean misNewIntent;
    private ReciteDialog mReciteDialog;

    private View rootView;

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case DIALOG_DELETE_WORD:
                    List<WordInfo> data = (List<WordInfo>)msg.obj;
                    int position = msg.arg1;
                    if (data != null && position >= 0 && data.size() > position) {
                        showDilalogByDeleteWord(data, position);
                    }
                     break;
            }

        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        rootView = View.inflate(getActivity(), R.layout.layout_dict_new_word, null);
        mLibNames = getResources().getStringArray(R.array.dict_name_list);

        initWidget();

        misChinese = getEnglishWordsNumber(this.getActivity()) == 0
                && !(getChineseWordsNumber(this.getActivity()) == 0);
        refreshList();

        mWordsObserver = new WordsObserver(new Handler());
        getActivity().getContentResolver().registerContentObserver(sUri, true, mWordsObserver);

        dictWordSound.open();
        return rootView;
    }

    @Override
    public void onDestroy()
    {
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
        if (mWordsAdapter != null) {
            mWordsAdapter.setOnDeleteClickListener(null);
        }
        getActivity().getContentResolver().unregisterContentObserver(mWordsObserver);
        super.onDestroy();
//        DictWordSearch.clearDictCache();
    }

    @Override
    public void onNewIntent(Intent intent)
    {
//        getActivity().setIntent(intent);
        misNewIntent = true;

        mContentView.clear();
        refreshList();
        mSelectMode.setChecked(false);
        mContentView.setSelectTextState(false);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (!misNewIntent)
        {
            if (mContentView != null)
            {
                mContentView.onResume();
            }
        }
        else
        {
            misNewIntent = false;
        }
    }

    @Override
    public void onPause()
    {
        super.onPause();
        super.dismissSelectWordDialog();
        if (mContentView != null)
        {
            mContentView.onPause(true);
        }
    }

    @Override
    public void onResumeView()
    {
        super.onResume();
        if (!misNewIntent)
        {
            if (mContentView != null)
            {
                mContentView.onResume();
            }
        }
        else
        {
            misNewIntent = false;
        }
    }

    @Override
    public void onPauseView()
    {
        super.onPause();
        super.dismissSelectWordDialog();
        if (mContentView != null)
        {
            mContentView.onPause(true);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.chinese)
        {
            misChinese = true;
            refreshList();
        }
        else if (id == R.id.english)
        {
            misChinese = false;
            refreshList();
        }
        else if (id == R.id.clear)
        {
            if (isDialogShow()) {
                return;
            }
            View.OnClickListener onClickListener = new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    int type = misChinese ? YY_CHS : YY_ENG;
                    clear(getActivity().getApplicationContext(), type);
                    dismissRemindDialog();
                }
            };
            showDialog(false, R.string.remind_title_text,
                    R.string.clear_all_confirm, onClickListener);
        }
        else if (id == R.id.pronounce)
        {
            dictWordSound.playWord(mCurrentWord.word);
        }
        else if (id == R.id.recite)
        {
            ArrayList<WordInfo> wordList = getReadableWords(this.getActivity());
            if (wordList != null && wordList.size() > 0)
            {
                int length = wordList.size();
                String words[] = new String[length];
                for(int i=0;i<length;i++) {
                    words[i] = wordList.get(i).word;
                }
                wordList.clear();
                mReciteDialog = new ReciteDialog(this.getActivity(), words);
                mReciteDialog.show();
            }
        }
        else if (id == R.id.select_mode)
        {
            if (mContentView != null)
            {
                mContentView.setSelectTextState(!mContentView.getSelectTextState());
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mWordList.setItemChecked(position, true);
        loadWordContent(position);
    }

    @Override
    public void getSelectText(String selectStr) {
        if (selectStr != null)
        {
            super.showSelectWordDialog(selectStr, DictWordSearch.getDictType(selectStr),
                    mContentView);
        }
    }

    @Override
    public void touchedView() {

    }

    @Override
    public void selectWordToIntent(int mDictID, String searchword) {
//        mSelectMode.setChecked(false);
//        mContentView.setSelectTextState(false);
        super.selectWordToIntent(mDictID, searchword);
    }

    @Override
    public void onResultFromFragment(int requestCode, int resultCode, Intent data) {
        super.onResultFromFragment(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            mSelectMode.setChecked(false);
            mContentView.setSelectTextState(false);
        }
    }

    @Override
    public void delete(List<WordInfo> data, int posititon) {
        sendMessages(DIALOG_DELETE_WORD, data, posititon, 50);
    }

    private void initWidget()
    {
        mWordList = (ListView) rootView.findViewById(R.id.word_list);
        mWordsAdapter = new WordsAdapter(this.getActivity());
        mWordsAdapter.setOnDeleteClickListener(this);
        mWordList.setAdapter(mWordsAdapter);
        mWordList.setOnItemClickListener(this);

        mLibName = (TextView) rootView.findViewById(R.id.dictionary_name);
        mPronounce = rootView.findViewById(R.id.pronounce);
        mPronounce.setOnClickListener(this);
        mContentView = (DictWordView) rootView.findViewById(R.id.content);
        mContentView.setOnScrollTextViewListener(this);
        mSelectMode = (CheckBox) rootView.findViewById(R.id.select_mode);
        mSelectMode.setOnClickListener(this);

        mChinese = rootView.findViewById(R.id.chinese);
        mChinese.setOnClickListener(this);
        mEnglish = rootView.findViewById(R.id.english);
        mEnglish.setOnClickListener(this);
        mClear = rootView.findViewById(R.id.clear);
        mClear.setOnClickListener(this);
        mRecite = rootView.findViewById(R.id.recite);
        mRecite.setOnClickListener(this);
    }

    private void refreshList()
    {
        if (mReciteDialog != null && mReciteDialog.isShowing())
        {
            mReciteDialog.dismiss();
        }
        ArrayList<WordInfo> words;
        if (misChinese)
        {
            words = getChineseWords(this.getActivity());
            mChinese.setEnabled(words.size() > 0);
            mEnglish.setEnabled(getEnglishWordsNumber(this.getActivity()) > 0);
            mRecite.setEnabled(false);
        }
        else
        {
            words = getEnglishWords(this.getActivity());
            mChinese.setEnabled(getChineseWordsNumber(this.getActivity()) > 0);
            mEnglish.setEnabled(words.size() > 0);
            mRecite.setEnabled(getReadableWordsNumber(this.getActivity()) != 0);
        }
        mWordsAdapter.refresh(words);

        mClear.setEnabled(words.size() > 0);

        mWordList.setItemChecked(0, true);
        mWordList.setSelection(0);
        loadWordContent(0);
    }

    private void showDilalogByDeleteWord(final List<WordInfo> mItems, final int position) {
        if (isDialogShow()) {
            return;
        }
        View.OnClickListener onClickListener = new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                CollectionManager.deleteWord(getActivity(),
                        mItems.get(position).dictionaryId,
                        mItems.get(position).wordIndex);
                dismissRemindDialog();
            }
        };
        showDialog(false, R.string.remind_title_text,
                R.string.tip_delete_key, onClickListener);
    }

    private void sendMessages(int what, Object obj, int arg1, int delayMillis) {
        Message msg = Message.obtain();
        msg.what = what;
        msg.obj = obj;
        msg.arg1 = arg1;
        if (mHandler != null) {
            mHandler.removeMessages(what, obj);
            mHandler.sendMessageDelayed(msg, delayMillis);
        }
    }

    private class WordsObserver extends ContentObserver
    {
        public WordsObserver(Handler handler)
        {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange)
        {
            ArrayList<WordInfo> words;
            if (misChinese)
            {
                words = getChineseWords(getActivity().getApplicationContext());
                mChinese.setEnabled(words.size() > 0);
                mEnglish.setEnabled(getEnglishWordsNumber(getActivity().getApplicationContext()) > 0);
                mRecite.setEnabled(false);
            }
            else
            {
                words = getEnglishWords(getActivity().getApplicationContext());
                mChinese.setEnabled(getChineseWordsNumber(getActivity().getApplicationContext()) > 0);
                mEnglish.setEnabled(words.size() > 0);
                mRecite.setEnabled(getReadableWordsNumber(getActivity().getApplicationContext()) != 0);
            }
            mWordsAdapter.refresh(words);

            mClear.setEnabled(words.size() > 0);

            int index = words.indexOf(mCurrentWord);
            if (index == -1)
            {
                mWordList.setItemChecked(0, true);
                loadWordContent(0);
            }
            else
            {
                mWordList.setItemChecked(index, true);
            }
        }
    }

    private void loadWordContent(int position)
    {
        if (position >= mWordsAdapter.getCount())
        {
            mSelectMode.setVisibility(INVISIBLE);
            mLibName.setText("");
            mPronounce.setVisibility(INVISIBLE);
            mContentView.clear();
        }
        else
        {
            mCurrentWord = (WordInfo) mWordsAdapter.getItem(position);

            mSelectMode.setVisibility(VISIBLE);
            mLibName.setText(mLibNames[mCurrentWord.dictionaryId]);
            mPronounce.setVisibility(mCurrentWord.readable ? VISIBLE : INVISIBLE);

            if (mCurrentWord != null) {
                byte[] byteData;
                DictWordSearch dictWordSearch = new DictWordSearch();
                if (dictWordSearch.open(mCurrentWord.dictionaryId, getActivity(), null)) {
                    byteData =  dictWordSearch.getExplainByte(mCurrentWord.wordIndex);
                    if (byteData != null) {
                        mContentView.showText(mCurrentWord.word,
                                byteData,
                                dictWordSearch.getExplainByteStart(byteData),
                                mCurrentWord.dictionaryId, getTextSize());
                    }
                }
                dictWordSearch.close();
            }
            /*mContentView.showText(mCurrentWord.wordIndex, mCurrentWord.word,
                    mCurrentWord.dictionaryId, 25);*/
        }
    }

}
