package com.readboy.offline.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.readboy.Dictionary.R;
import com.readboy.depict.data.HanziPackage;
import com.readboy.depict.model.HanziInfo;
import com.readboy.depict.widget.DemonstrationGroup;
import com.readboy.dict.DictWordView;
import com.readboy.library.gif.GifView;
import com.readboy.library.io.DictFile;
import com.readboy.library.io.DictIOFile;
import com.readboy.library.io.DongManParam;
import com.readboy.library.search.DictWordSearch;
import com.readboy.library.utils.DelayClick;
import com.readboy.library.utils.DictInfo;
import com.readboy.offline.adapter.WordListAdapter;
import com.readboy.offline.data.CollectionManager;
import com.readboy.offline.mode.DictModleButton;
import com.readboy.offline.view.DictListView;
import com.readboy.offline.view.DictMainView;

/**
 * Created by Administrator on 14-8-27.
 */
public class DictMainFragment extends BaseDictFragment implements DictMainView.MainClickListener, DemonstrationGroup.OnDialogShowListener{

    private static final int SELECT_WORD = 0x01;
    private static final int UPDATE_FINISH_HANZI_DATA = 0x02;
    private static final int UPDATE_FAILD_HANZI_DATA = 0x03;
    private static final int EDITTEXT_LOAD_DATA = 0x04;

    private static final int SHOW_TOAST_TIP = 1;

//    private HanziPackage mHanziPackage = null;

    private DictMainView mainView = null;
    private boolean isNewDict = false;

    /**
     * 描红框是否能弹出
     */
    private boolean canDecpicDialogShow = true;

    private SearchWorldThread searchWorldThread;

    private HanziPackage.DataChangeListener dataChangeListener = new HanziPackage.DataChangeListener() {
        @Override
        public void onDataChange(boolean b) {
            if (b) {
                sendMessages(UPDATE_FINISH_HANZI_DATA, null, 0, 0, 0);
            } else {
                sendMessages(UPDATE_FAILD_HANZI_DATA, null, 0, 0, 0);
            }
        }
    };

    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case SELECT_WORD:
                     String word = (String)msg.obj;
                     int position = msg.arg1;
                     if (word != null && mainView != null) {
                         mainView.getBiHuaView().dismissDialog();
//                         mainView.getBiHuaView().clearMessage();
                         showView(word, position, mainView.getmExpView(), mainView.getDictModleButton());
                     }
                     break;

                case UPDATE_FINISH_HANZI_DATA:
                    if (mainView != null) {
                        DictListView dictListView = mainView.getWordList();
                        WordListAdapter dictKeyAdapter = (WordListAdapter)dictListView.getAdapter();
                        if (dictKeyAdapter == null) {
                            dictKeyAdapter = new WordListAdapter(getActivity());
                        } else {
                            dictKeyAdapter.onDistory();
                        }
                        dictWordSearch.reset();
                        dictListView.init(dictWordSearch);
                        dictListView.setAdapter(dictKeyAdapter);
                    }
                     break;

                case UPDATE_FAILD_HANZI_DATA:
                    if (getActivity() != null) {
                        Toast.makeText(getActivity(), R.string.toast_load_data_failed, Toast.LENGTH_SHORT).show();
                        getActivity().finish();
                    }
                     break;
                case EDITTEXT_LOAD_DATA:
                    int tempIndex = msg.arg1;
                    if (tempIndex != -1 && mainView != null && mainView.getWordList() != null) {
                        String tempKeyWord = dictWordSearch.getKeyWord(tempIndex);
                        if (mDictID == DictInfo.DICT_BIHUA) {
                            if (tempKeyWord.contentEquals(mKeyWord) && mKeyIndex == tempIndex) {
                                return;
                            }
                        }
                        if (mKeyIndex != tempIndex) {// 避免重复显示
                            mainView.getWordList().setKeySelectByTextWathcer(tempIndex);
                            if (mainView != null) {
                                showView(tempKeyWord, tempIndex);
                            }
                        }
                    } else {
                        if (msg.arg2 == SHOW_TOAST_TIP) {
                            showReminDialog(R.string.select_search_failed);
                        }
                    }

                    break;
            }

        }

    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        mDictID = getActivity().getIntent().getIntExtra("rbapk_extradata",0);
        mainView = (DictMainView)inflater.inflate(R.layout.layout_dict_main, null);
        mainView.setMainClickListener(this);
        if (mainView.getBiHuaView() != null) {
            mainView.getBiHuaView().setOnDialogShowListener(this);
        }
        mainView.post(new Runnable() {
            @Override
            public void run() {
                mainView.setView(mDictID);
            }
        });
        return mainView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (mDictID == DictFile.ID_BHDICT && dictWordSearch.open(mDictID, getActivity(), dataChangeListener)) {

        } else if (mDictID != DictFile.ID_BHDICT && dictWordSearch.open(mDictID, getActivity(), null)) {
            WordListAdapter dictKeyAdapter = new WordListAdapter(getActivity());
            DictListView dictListView = mainView.getWordList();
            dictListView.init(dictWordSearch);
            dictListView.setAdapter(dictKeyAdapter);
        } else {
//            Toast.makeText(getActivity(), getResources().getString(R.string.open_file_failed), Toast.LENGTH_LONG).show();
            showToastText(R.string.open_file_failed, Toast.LENGTH_LONG);
            getActivity().finish();
            return;
        }
        if (!dictWordSound.open()) {
//            Toast.makeText(getActivity(), getResources().getString(R.string.open_file_failed), Toast.LENGTH_LONG).show();
            showToastText(R.string.open_file_failed, Toast.LENGTH_LONG);
            getActivity().finish();
            return;
        }
        /*if (mDictID == DictInfo.DICT_BIHUA) {
            if (dictWordSearch.openHanziPackage(getActivity())) {
                mHanziPackage = dictWordSearch.getHanziPackage();
            } else {
                showToastText(R.string.open_file_failed, Toast.LENGTH_LONG);
                getActivity().finish();
            }
        }*/

    }

    @Override
    public void onResume() {
        super.onResume();
        /*if (mHanziPackage != null) {
            mHanziPackage.checkDataChange(getActivity());
        }*/
        if (mDictID == DictIOFile.ID_BHDICT && dictWordSearch != null && dictWordSearch.getHanziPackage() != null) {
            try {
                dictWordSearch.getHanziPackage().checkDataChange(getActivity());
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
        if (mainView.getWordList() != null && !mainView.getWordList().isEnabled()) {
            mainView.getWordList().setEnabled(true);
        }

    }

    @Override
    public void onNewIntent(Intent intent) {
        isNewDict = true;

    }

    @Override
    public void onResumeView() {
        if (isNewDict) {
            isNewDict = false;
            reset();
        } else {
            if (mainView != null) {
                if (mainView.getBiHuaView() != null &&
                        mainView.getBiHuaView().getVisibility() == View.VISIBLE) {
                    /*if (mHanziPackage != null) {
                        mHanziPackage.checkDataChange(getActivity());
                    }*/
                    mainView.getBiHuaView().resume();
                }
                if (mainView.getmExpView() != null) {
                    mainView.getmExpView().onResume();
                }
            }
        }
//        DictTextColorInfo.reset(getActivity());
    }

    @Override
    public void onPauseView() {
        if (mainView != null) {
            if (mainView.getBiHuaView() != null &&
                    mainView.getBiHuaView().getVisibility() == View.VISIBLE) {
                mainView.getBiHuaView().pause();
            }
            if (mainView.getmExpView() != null) {
                mainView.getmExpView().onPause(false);
            }
        }
        if (handler != null) {
            handler.removeMessages(EDITTEXT_LOAD_DATA);
        }
        if (searchWorldThread != null) {
            searchWorldThread.stop = true;
        }
    }

//        @Override
//    public void onResume() {
//        super.onResume();
//            if (isNewDict) {
//                isNewDict = false;
//                reset();
//            } else {
//                if (mainView != null) {
//                    if (mainView.getBiHuaView() != null &&
//                            mainView.getBiHuaView().getVisibility() == View.VISIBLE) {
//                        mainView.getBiHuaView().resume();
//                    }
//                    if (mainView.getmExpView() != null) {
//                        mainView.getmExpView().onResume();
//                    }
//                }
//            }
//    }

    @Override
    public void onPause() {
        super.onPause();
        if (mainView != null) {
            if (mainView.getBiHuaView() != null &&
                    mainView.getBiHuaView().getVisibility() == View.VISIBLE) {
                mainView.getBiHuaView().pause();
            }
            if (mainView.getmExpView() != null) {
                mainView.getmExpView().onPause(true);
            }
            if (mainView.getWordList() != null) {
                mainView.getWordList().setEnabled(false);
            }
        }
        if (handler != null) {
            handler.removeMessages(EDITTEXT_LOAD_DATA);
        }
        if (searchWorldThread != null) {
            searchWorldThread.stop = true;
        }
    }

    @Override
    public void onDestroyView() {
        if (mainView != null && mainView.getWordList() != null) {
            WordListAdapter wordListAdapter = (WordListAdapter)mainView.getWordList().getAdapter();
            if (wordListAdapter != null) {
                wordListAdapter.onDistory();
            }
        }

        /*if (mHanziPackage != null) {
            mHanziPackage.recycle();
            mHanziPackage = null;
        }*/

        mKeyWord = null;
        mKeyWordPhonetic = null;
        if (mainView != null) {
            mainView.closeDictMain();
            mainView = null;
        }
        if (handler != null) {
            handler.removeMessages(EDITTEXT_LOAD_DATA);
        }
        if (searchWorldThread != null) {
            searchWorldThread.stop = true;
        }
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        if (mainView != null && mDictID == DictIOFile.ID_BHDICT) {
            if (dictWordSearch != null && dictWordSearch.getHanziPackage() != null) {
                dictWordSearch.getHanziPackage().colse();
            }
        }
        super.onDestroy();
    }

    @Override
    public void selectWordToIntent(int mDictID, String searchword) {
//        mainView.cancleQuciStatu();
        canDecpicDialogShow = false;
        super.selectWordToIntent(mDictID, searchword);
    }

    @Override
    public void listSelect(int position, String str) {
        super.dismissAllDialog();
        String word = str;
        if (mainView != null) {
//            showView(word, position, mainView.getmExpView(), mainView.getDictModleButton());
            showView(word, position);
        }
    }

    @Override
    public void searchButton(EditText text) {
        if (text == null || text.length() == 0 || DictWordSearch.isBlankString(text.toString())) {
//            if (remindDialog == null) {
//                remindDialog = new RemindDialog(getActivity());
//            }
//            remindDialog.remindShow(null, getResources().getString(R.string.key_is_null_warning));
            showReminDialog(R.string.key_is_null_warning);
        } else {
//            intent.setAction("com.readboy.Dictionary.DICTJUMP");
//            Intent intent = new Intent(this, DictJumpActivity.class);
//            Intent intent = new Intent("com.readboy.Dictionary.DICTJUMP");
//            intent.putExtra("key_text", text.getText().toString());// string为要查找的内容
//            startActivity(intent);
//            overridePendingTransition(R.anim.push_left_in, R.anim.non_animation);

            if (mainView != null && mainView.getBiHuaView() != null
                    && mainView.getBiHuaView().getVisibility() == View.VISIBLE) {
                mainView.getBiHuaView().dismissDialog();
                mainView.getBiHuaView().findViewById(R.id.depict).setEnabled(false);
            }

            if (mainView != null) {
                mainView.hideSoftInput();
                if (mainView.getEditFoucesStatus()) {
                    mainView.cleanEditeFouces();
                    mainView.requestEditFouces();
                }
            }
            BaseDictFragment resultFragment = showFragment(BaseDictFragment.FRAGMENT_TAG_DICT_JUMP,
                                                           BaseDictFragment.FRAGMENT_DICT_JUMP,
                                                           -1, text.getText().toString());
            if (resultFragment != null) {
                startForResult(REQUEST_CODE, resultFragment);
            }
        }
    }

    @Override
    public void onKeyWordChange(DictListView wordList, Editable s) {
        /*int tempIndex = dictWordSearch.getKeyIndex(s.toString());
        if (tempIndex != -1) {
            String tempKeyWord = dictWordSearch.getKeyWord(tempIndex);
            if (mDictID == DictInfo.DICT_BIHUA && !s.toString().trim().isEmpty()) {
                if (tempKeyWord.contentEquals(mKeyWord)) {
                    return;
                }
            }
            if (this.mKeyIndex != tempIndex) {// 避免重复显示
                wordList.setKeySelectByTextWathcer(tempIndex);
                if (mainView != null) {
//                    showView(tempKeyWord, tempIndex, mainView.getmExpView(), mainView.getDictModleButton());
                    showView(tempKeyWord, tempIndex);
                }
            }
        }*/
        if (searchWorldThread != null) {
            searchWorldThread.stop = true;
        }
        searchWorldThread = new SearchWorldThread(false, mDictID, 0, s.toString().trim());
        searchWorldThread.start();
    }

    @Override
    public void onClickFaYin(String str) {
        if (dictWordSound != null) {
            if (mDictID != DictInfo.DICT_BIHUA) {
                dictWordSound.playWord(str, super.mAudioRate);
            } else {
                byte[] soundBytes = dictWordSearch.getHanziSoundBytes(mKeyIndex);
                if (soundBytes != null) {
//                    dictWordSound.playBihuaSound(mKeyWordPhonetic, getActivity(), super.mAudioRate);
                    dictWordSound.playSound(soundBytes, super.mAudioRate);
                }
                mainView.getBiHuaView().showCompletedHanzi();
            }
        }
    }

    @Override
    public void onClickZiHao() {
        if (mainView != null) {
            DictWordView scrollView = mainView.getmExpView();
            /*if (scrollView != null && dictWordSearch != null) {
                int start=0;
                byte[] byteData = null;
                if (mDictID == DictInfo.DICT_DONGMAN) {
                    DongManParam dongManParam = dictWordSearch.getDongManParam(mKeyIndex);
                    if (dongManParam == null) {
                        return;
                    }
                    byteData = dongManParam.explainData;
                } else {
                    byteData =  dictWordSearch.getExplainByte(mKeyIndex);
                    if (mDictID != DictIOFile.ID_BHDICT) {
                        start = dictWordSearch.getExplainByteStart(byteData);
                    }
                }
                if (byteData != null) {
                    scrollView.showText(mKeyWord, byteData, start, mDictID, getTextSize());
                }
            }*/
            scrollView.setTextSize(getTextSize());
        }
    }

    @Override
    public void onClickYuSu() {
        if (super.delayClick.canClickByTime(DelayClick.DELAY_300MS)) {
            super.showSoundSpeedDialog();
        }
    }

    @Override
    public void addNewWordButton(int position) {
        if (!super.delayClick.canClickByTime(DelayClick.DELAY_300MS)) {
            return;
        }
//        if (remindDialog == null) {
//            remindDialog = new RemindDialog(getActivity());
//        }
        if (mKeyWord != null) {
            int saveResult = CollectionManager.addWord(getActivity(), mKeyWord, mDictID, mKeyIndex, mCanPlaySound);
            switch (saveResult) {
                case CollectionManager.SUCCESSFULLY:
//                    remindDialog.setRemind(null, getResources().getString(R.string.key_add_success));
//                    remindDialog.remindShow();
                    showReminDialog(R.string.key_add_success);
                    break;
                case CollectionManager.OVERFLOW:
//                    remindDialog.setRemind(null, getResources().getString(R.string.key_is_full));
//                    remindDialog.remindShow();
                    showReminDialog(R.string.key_is_full);
                    break;
                case CollectionManager.EXISTED:
//                    remindDialog.setRemind(null, getResources().getString(R.string.key_has_exist_ch));
//                    remindDialog.remindShow();
                    /*if (BaseDictFragment.getDictType(mDictID) == DictWordSearch.DICT_TYPE_CHI) {
                        showReminDialog(R.string.key_has_exist_ch);
                    } else {
                        showReminDialog(R.string.key_has_exist_en);
                    }*/
                    showReminDialog(R.string.key_has_exist_ch);
                    break;
            }

        }
    }

    @Override
    public void selectText(String selectStr) {
        if (mainView != null && mainView.getBiHuaView() != null) {
            mainView.getBiHuaView().dismissDialog();
        }
        if (mainView != null) {
            super.showSelectWordDialog(selectStr, DictWordSearch.getDictType(selectStr), mainView.getmExpView());
        }
    }

    @Override
    public void keyEnter(String s, String itemString) {
//        if (remindDialog == null) {
//            remindDialog = new RemindDialog(getActivity());
//        }

        if (s != null && itemString != null) {
            if (s.contentEquals(itemString)) {
                return;
            }
        }
        if (s != null && s.length() > 0 && !DictWordSearch.isBlankString(s)) {

            if (searchWorldThread != null) {
                searchWorldThread.stop = true;
            }
            searchWorldThread = new SearchWorldThread(true, mDictID, SHOW_TOAST_TIP, s.toString().trim());
            searchWorldThread.start();
        } else {
//            if (remindDialog != null) {
//                remindDialog.remindShow(null, getResources().getString(R.string.key_is_null_warning));
//            }
            showReminDialog(R.string.key_is_null_warning);
        }
    }

    @Override
    public void getTouchScollTextView() {
        super.dismissAllDialog();
    }

    @Override
    public boolean dispatchDialogTouch(MotionEvent motionEvent) {
        if (mainView != null) {
            mainView.hideSoftInput();
        }
        if (!canDecpicDialogShow) {
            return true;
        }
        return false;
    }

    /**
     * 描红框弹出
     */
    @Override
    public void onDialogShow() {
        if (mainView != null && mainView.getBiHuaView() != null
                && mainView.getBiHuaView().getVisibility() == View.VISIBLE) {
            mainView.getBiHuaView().showCompletedHanzi();
        }
        super.dismissAllDialog();
    }

    @Override
    public void onResultFromFragment(int requestCode, int resultCode, Intent data) {
        super.onResultFromFragment(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            mainView.cancleQuciStatu();
        }
        if (mainView != null && mainView.getBiHuaView() != null
                && mainView.getBiHuaView().getVisibility() == View.VISIBLE) {
            mainView.getBiHuaView().findViewById(R.id.depict).setEnabled(true);
        }
        canDecpicDialogShow = true;
    }

    private void sendMessages(int what, Object obj, int arg1, int arg2, int delayMillis) {
        Message message = Message.obtain();
        message.what = what;
        message.obj = obj;
        message.arg1 = arg1;
        message.arg2 = arg2;
        if (handler != null) {
            handler.removeMessages(what);
            handler.sendMessageDelayed(message, delayMillis);
        }
    }

    private void reset() {
        int tempDict = getActivity().getIntent().getIntExtra("rbapk_extradata",-1);
//        if (mDictID == tempDict || tempDict == -1) {
//            if (mDictID == DictInfo.DICT_BIHUA) {
//                if (mainView != null && mainView.getBiHuaView() != null &&
//                        mainView.getBiHuaView().getVisibility() == View.VISIBLE) {
//                    mainView.getBiHuaView().resume();
//                }
//            }
//            return;
//        }
        if (tempDict == -1) {
            return;
        }
        dismissSelectWordDialog();
        dismissSoundSpeedDialog();
        dismissRemindDialog();
//        if (remindDialog != null) {
//            remindDialog.remindDismiss();
//        }
        mDictID = tempDict;
        mKeyIndex = -1;
        if (mDictID == DictInfo.DICT_DONGMAN) {
            if (mainView != null) {
                if (mainView.getDongMainView() != null) {
                    mainView.getDongMainView().setVisibility(View.INVISIBLE);
                }
                    /*if (mHanziPackage != null) {
                        mHanziPackage.recycle();
                        mHanziPackage = null;
                    }*/
                if (mainView.getBiHuaView() != null) {
                    mainView.getBiHuaView().setVisibility(View.GONE);
                }
            }
        } else if (mDictID == DictInfo.DICT_BIHUA) {
                /*mHanziPackage = new HanziPackage(getActivity());*/
//                mHanziPackage = new HanziPackage();
            if (mainView != null) {
                if (mainView.getBiHuaView() != null) {
                    mainView.getBiHuaView().setVisibility(View.INVISIBLE);
                }
                if (mainView.getDongMainView() != null) {
                    mainView.getDongMainView().setVisibility(View.GONE);
                }
            }
        } else {
                /*if (mHanziPackage != null) {
                    mHanziPackage.recycle();
                    mHanziPackage = null;
                }*/
            if (mainView != null) {
                if (mainView.getBiHuaView() != null) {
                    mainView.getBiHuaView().setVisibility(View.GONE);
                }
                if (mainView.getDongMainView() != null) {
                    mainView.getDongMainView().setVisibility(View.GONE);
                }
            }
        }

        if (dictWordSearch != null) {
            dictWordSearch.close();
            dictWordSearch = null;
        }
        dictWordSearch = new DictWordSearch();
        if (mDictID == DictFile.ID_BHDICT && dictWordSearch.open(mDictID, getActivity(), dataChangeListener)) {

        } else if (mDictID != DictFile.ID_BHDICT && dictWordSearch.open(mDictID, getActivity(), null)) {
            WordListAdapter wordListAdapter = (WordListAdapter)mainView.getWordList().getAdapter();
            wordListAdapter.onDistory();
            mainView.getWordList().init(dictWordSearch);
            mainView.getWordList().setAdapter(wordListAdapter);
        }
        mainView.setView(mDictID);
    }



    private void showView(String keyWord, int position) {
        sendMessages(SELECT_WORD, keyWord, position, 0, 50);
    }

    private void showView(String keyWord, int position, DictWordView scrollView, DictModleButton modleButton) {
        if (this.mKeyIndex == position) {
            return;
        }
        this.mKeyWord = dictWordSearch.getKeyWord(position);
        this.mKeyIndex = position;
        byte[] byteData = null;
        int start = 0;
        int length = 0;
        if (mDictID == DictInfo.DICT_DONGMAN) {
            DongManParam dongManParam = dictWordSearch.getDongManParam(position);
            if (dongManParam == null) {
                return;
            }
            byteData = dongManParam.explainData;
            if (mainView.getDongMainView().getVisibility() != View.VISIBLE) {
                mainView.getDongMainView().setVisibility(View.VISIBLE);
            }
            mainView.getDongMainView().setGifImageType(GifView.GifImageType.COVER);
            mainView.getDongMainView().setGifImage(dongManParam.dmData);

            if (mainView.getBiHuaView().getVisibility() == View.VISIBLE) {
                mainView.getBiHuaView().setVisibility(View.GONE);
            }

            if (byteData != null) {
                start = dictWordSearch.getExplainByteStart(byteData);
                scrollView.showText(mKeyWord, byteData, start, mDictID, textSize);
            }

            if (dictWordSound != null && dictWordSound.canPlay(keyWord)) {
                modleButton.faYin.setEnabled(true);
                modleButton.yuSu.setEnabled(true);
            } else {
                modleButton.faYin.setEnabled(false);
                modleButton.yuSu.setEnabled(false);
            }
        } else {
            if (mainView.getDongMainView().getVisibility() == View.VISIBLE) {
                mainView.getDongMainView().setVisibility(View.GONE);
            }

            byteData =  dictWordSearch.getExplainByte(position);

            if (mDictID != DictInfo.DICT_BIHUA) {
                mCanPlaySound = dictWordSound.canPlay(this.mKeyWord);
                if (dictWordSound != null && mCanPlaySound) {
                    modleButton.faYin.setEnabled(true);
                    modleButton.yuSu.setEnabled(true);
                } else {
                    modleButton.faYin.setEnabled(false);
                    modleButton.yuSu.setEnabled(false);
                }
                if (mainView.getBiHuaView().getVisibility() == View.VISIBLE) {
                    mainView.getBiHuaView().setVisibility(View.GONE);
                }
                if (mainView.getBiHuaView() != null && mainView.getBiHuaView().getVisibility() == View.VISIBLE) {
                    mainView.getBiHuaView().pause();
                    mainView.getBiHuaView().setVisibility(View.GONE);
                }
                if (byteData != null) {
                    start = dictWordSearch.getExplainByteStart(byteData);
                    scrollView.showText(mKeyWord, byteData, start, mDictID, textSize);
                }
            } else {
                /**
                 * bihua's dictionary
                 */
                if (dictWordSound != null) {
                    HanziInfo hanziInfo = dictWordSearch.getHanziInfo(position);
                    if (hanziInfo != null) {
                        mKeyWord = hanziInfo.word;
                        dictWordSearch.getHanziPackage().setHanzi(hanziInfo);
                        mainView.getBiHuaView().initView(getActivity(), dictWordSearch.getHanziPackage());
                        if (mainView.getBiHuaView().getVisibility() != View.VISIBLE) {
                            mainView.getBiHuaView().setVisibility(View.VISIBLE);
                        }

                        modleButton.faYin.setEnabled(true);
                        modleButton.yuSu.setEnabled(true);
                        mCanPlaySound = true;
                    }
                }
                if (byteData != null) {
                    scrollView.showText(mKeyWord, byteData, start, mDictID, textSize);
                }
            }
        }

        if (byteData != null) {
            if (!modleButton.ziHao.isEnabled()) {
                modleButton.ziHao.setEnabled(true);
            }
            if (mDictID != DictInfo.DICT_BIHUA && mDictID != DictInfo.DICT_DONGMAN) {
                if (!modleButton.addNewWord.isEnabled()) {
                    modleButton.addNewWord.setEnabled(true);
                }
            } else {
                if (modleButton.addNewWord.isEnabled()) {
                    modleButton.addNewWord.setEnabled(false);
                }
            }
        }

    }

    private class SearchWorldThread extends Thread {

        int searchDictID = -1;
        int canShowToastTip = 0;
        String str = null;
        boolean stop = false;
        boolean isAccurate = false; //是否精确搜索

        /*SearchWorldThread(int searchDictID, int canShowToastTip, String str) {
            this.searchDictID = searchDictID;
            this.str = str;
            this.canShowToastTip = canShowToastTip;
        }*/

        SearchWorldThread(boolean isAccurate, int searchDictID, int canShowToastTip, String str) {
            this.isAccurate = isAccurate;
            this.searchDictID = searchDictID;
            this.str = str;
            this.canShowToastTip = canShowToastTip;
        }

        @Override
        public void run() {
            if (searchDictID != -1 && str != null) {
                DictWordSearch tempDictWordSearch = new DictWordSearch();
                if (tempDictWordSearch.open(searchDictID, getActivity(), null)) {
                    try {
                        int tempIndex = -1;
                        if (!isAccurate) {
                            tempIndex = tempDictWordSearch.getKeyIndex(str.toString());
                        } else {
                            tempIndex = tempDictWordSearch.getKeyIndexAccurate(str.toString());
                        }
                        if (!stop) {
                            sendMessages(EDITTEXT_LOAD_DATA, null, tempIndex, canShowToastTip, 50);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        if (tempDictWordSearch != null) {
                            tempDictWordSearch.close();
                        }
                    }
                }

            }
        }
    }

}
