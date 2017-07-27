package com.readboy.offline.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.readboy.Dictionary.R;
import com.readboy.offline.data.CollectionManager;
import com.readboy.depict.data.HanziPackage;
import com.readboy.depict.widget.DemonstrationGroup;
import com.readboy.dict.DictWordView;
import com.readboy.library.io.DictFile;
import com.readboy.library.search.DictWordSearch;
import com.readboy.library.search.DictWordSound;
import com.readboy.library.utils.DelayClick;
import com.readboy.library.utils.DictInfo;
import com.readboy.offline.view.DictSearchView;

/**
 * 不包含动漫词典
 */
public class DictSearchFragment extends BaseDictFragment implements DictSearchView.QuCiListener, DemonstrationGroup.OnDialogShowListener{

    private static final int LOAD_DATA = 0x01;
    private static final int UPDATE_FINISH_HANZI_DATA = 0x02;
    private static final int UPDATE_FAILD_HANZI_DATA = 0x03;

    private String searchKey = null;
    private int searchID = -1;

    private DictSearchView dictSearchView = null;
    /*private HanziPackage mHaziPackage = null;*/

    private boolean isPause = false;
    private Message currentMessage = null;

    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {

                case LOAD_DATA:
                    if (isPause) {
                        currentMessage = Message.obtain();
                        currentMessage.what = msg.what;
                        currentMessage.obj = msg.obj;
                        currentMessage.arg1 = msg.arg1;
                        break;
                    }
                    if (msg.obj instanceof String) {
                        mKeyWord = (String)msg.obj;
                        mDictID = msg.arg1;
                        showView(mDictID, mKeyWord, true);
                    }
                     break;

                case UPDATE_FINISH_HANZI_DATA:
                     mDictID = searchID;
                     mKeyIndex =  dictWordSearch.getSearchKeyIndex(searchKey);
                     mKeyWord = dictWordSearch.getKeyWord(mKeyIndex);

                     searchKey = null;
                     searchID = -1;
                     if (mKeyIndex == -1) {
                        setResult(RESULT_FAILED);
                        showToastText(R.string.select_search_failed, Toast.LENGTH_SHORT);
                        getActivity().getSupportFragmentManager().popBackStack();
                        return;
                     }
                     break;

                case UPDATE_FAILD_HANZI_DATA:
                    Toast.makeText(getActivity(), R.string.toast_load_data_failed, Toast.LENGTH_SHORT).show();
                    if (getActivity() != null) {
                        getActivity().finish();
                    }
                     break;

            }
        }
    };

    private HanziPackage.DataChangeListener dataChangeListener = new HanziPackage.DataChangeListener() {
        @Override
        public void onDataChange(boolean b) {
            if (b) {
                sendMessages(UPDATE_FINISH_HANZI_DATA, null, 0, 50);
            } else {
                sendMessages(UPDATE_FAILD_HANZI_DATA, null, 0, 0);
            }
        }
    };

    public void setSearchInfo(int searchID, String searchKey) {
        this.searchKey = searchKey;
        this.searchID = searchID;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        dictSearchView = (DictSearchView)inflater.inflate(R.layout.layout_dict_search, null);
        dictSearchView.setmQuCiListener(this);
        if (dictSearchView.getBiHuaView() != null) {
            dictSearchView.getBiHuaView().setOnDialogShowListener(this);
        }
        return dictSearchView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        reset();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (dictSearchView != null) {
            if (dictSearchView.getBiHuaView() != null &&
                    dictSearchView.getBiHuaView().getVisibility() == View.VISIBLE) {
                dictSearchView.getBiHuaView().resume();
            }
            if (dictSearchView.getmTextView() != null) {
                dictSearchView.getmTextView().onResume();
            }
        }
        if (currentMessage != null) {
            sendMessages(currentMessage.what, currentMessage.obj, currentMessage.arg1, 0);
            currentMessage = null;
        }
        isPause = false;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (dictSearchView != null) {
            if (dictSearchView.getBiHuaView() != null &&
                    dictSearchView.getBiHuaView().getVisibility() == View.VISIBLE) {
                dictSearchView.getBiHuaView().pause();
            }
            if (dictSearchView.getmTextView() != null) {
                dictSearchView.getmTextView().onPause(true);
            }
        }
        isPause = true;
    }

    @Override
    public void onDestroyView() {
        /*if (mHaziPackage != null) {
            mHaziPackage.recycle();
        }*/
        if (dictSearchView != null) {
            if (dictSearchView.getBiHuaView() != null &&
                    dictSearchView.getBiHuaView().getVisibility() == View.VISIBLE) {
                dictSearchView.getBiHuaView().dismissDialog();
            }
        }
        searchKey = null;
        mKeyWord = null;
        currentMessage = null;
        super.onDestroyView();
    }

    @Override
    public void animationStart() {
    }

    @Override
    public void animationEnd() {
        if (mKeyWord != null) {
            sendMessages(LOAD_DATA, mKeyWord, mDictID, 0);
//            showView(mDictID, mKeyWord, true);
        }
    }

    @Override
    public void onClickFaYin() {
        if (mKeyWord != null && dictWordSound != null) {
            if (mDictID != DictInfo.DICT_BIHUA) {
                dictWordSound.playWord(mKeyWord, super.mAudioRate);
            } else {
                if (mKeyWordPhonetic != null) {
                    dictWordSound.playBihuaSound(mKeyWordPhonetic, getActivity(), super.mAudioRate);
                }
                dictSearchView.getBiHuaView().showCompletedHanzi();
            }
        }
    }

    @Override
    public void onClickZiHao(DictWordView scrollView) {
        if (scrollView != null) {
            /*if (dictWordSearch != null) {
                dictWordSearch.close();
            } else {
                dictWordSearch = new DictWordSearch();
            }
            if (dictWordSearch.open(mDictID, getActivity(), null)) {
                byte[] exp = dictWordSearch.getExplainByte(mKeyIndex);
                if (exp != null) {
                    scrollView.showTextByBigFirstLine(mKeyWord, exp, dictWordSearch.getExplainByteStart(exp), mDictID,
                            getTextSize());
                }
            } else {
                sendMessages(UPDATE_FAILD_HANZI_DATA, null, 0, 0);
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
    public void addNewWordButton() {
        if (!super.delayClick.canClickByTime(DelayClick.DELAY_300MS)) {
            return;
        }
        if (mKeyWord != null) {
            int saveResult = CollectionManager.addWord(getActivity(), mKeyWord, mDictID, mKeyIndex, mCanPlaySound);
            switch (saveResult) {
                case CollectionManager.SUCCESSFULLY:
                    showReminDialog(R.string.key_add_success);
                    break;
                case CollectionManager.OVERFLOW:
                    showReminDialog(R.string.key_is_full);
                    break;
                case CollectionManager.EXISTED:
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
    public void selectText(String selectStr, View scrollTextView) {
        if (selectStr != null) {
            if (dictSearchView != null && dictSearchView.getBiHuaView() != null) {
                dictSearchView.getBiHuaView().dismissDialog();
            }
            super.showSelectWordDialog(selectStr, DictWordSearch.getDictType(selectStr), scrollTextView);
        }
    }

    @Override
    public void selectWordToIntent(int dictID, String searchword) {
        showView(dictID, searchword, false);
    }

    @Override
    public void cleanBiHua() {

    }

    @Override
    public void getTouchScollTextView() {
        super.dismissAllDialog();
    }

    @Override
    public boolean dispatchDialogTouch(MotionEvent motionEvent) {
        return false;
    }

    /**
     * 描红框弹出
     */
    @Override
    public void onDialogShow() {
        super.dismissAllDialog();
    }

    private void reset() {
        if (searchKey != null && searchID != -1) {

            if (dictWordSearch != null) {
                dictWordSearch.close();
            } else {
                dictWordSearch = new DictWordSearch();
            }
            if (dictWordSound == null) {
                dictWordSound = new DictWordSound();
            }

            dictWordSearch = new DictWordSearch();
            dictWordSound = new DictWordSound();
            if (searchID == DictFile.ID_BHDICT && dictWordSearch.open(searchID, getActivity(), dataChangeListener)) {

            } else if (searchID != DictFile.ID_BHDICT && dictWordSearch.open(searchID, getActivity(), null)) {
                mDictID = searchID;
                mKeyIndex =  dictWordSearch.getSearchKeyIndex(searchKey);
                mKeyWord = dictWordSearch.getKeyWord(mKeyIndex);

                searchKey = null;
                searchID = -1;
                if (mKeyIndex == -1) {
                    setResult(RESULT_FAILED);
                    showToastText(R.string.select_search_failed, Toast.LENGTH_SHORT);
                    getActivity().getSupportFragmentManager().popBackStack();
                    return;
                }
            } else {
                setResult(RESULT_FAILED);
                showToastText(R.string.open_file_failed, Toast.LENGTH_SHORT);
                getActivity().getSupportFragmentManager().popBackStack();
                return;
            }
            if (!dictWordSound.open()) {
                setResult(RESULT_FAILED);
                showToastText(R.string.open_file_failed, Toast.LENGTH_SHORT);
                getActivity().getSupportFragmentManager().popBackStack();
                return;
            }

        } else {
            setResult(RESULT_FAILED);
            showToastText(R.string.select_search_failed, Toast.LENGTH_SHORT);
            getActivity().getSupportFragmentManager().popBackStack();
            return;
        }
        setResult(RESULT_OK);
    }

    private void sendMessages(int what, Object obj, int arg1, int delayMillis) {
        Message message = Message.obtain();
        message.what = what;
        message.obj = obj;
        message.arg1 = arg1;
        if (handler != null) {
            handler.removeMessages(what);
            handler.sendMessageDelayed(message, delayMillis);
        }
    }

    private void showView(int dictID, String searchword, boolean isNewIntent) {
        if (isNewIntent) {
            dictSearchView.setView(mDictID);
        } else {
            if (dictWordSearch != null) {
                dictWordSearch.close();
            } else {
                dictWordSearch = new DictWordSearch();
            }
            if (dictWordSearch.open(dictID, getActivity(), null)) {
                int seacrhKeyIndex = dictWordSearch.getSearchKeyIndex(searchword);
                if (seacrhKeyIndex == -1) {
                    showToastText(R.string.select_search_failed, Toast.LENGTH_SHORT);
                    dictWordSearch.close();
                    return;
                }
                mDictID = dictID;
                mKeyIndex = seacrhKeyIndex;
                mKeyWord = dictWordSearch.getKeyWord(mKeyIndex);
                dictSearchView.setView(mDictID);
            } else {
                sendMessages(UPDATE_FAILD_HANZI_DATA, null, 0, 0);
            }
        }
        byte[] exp = null;
        if (mKeyIndex != -1 && dictWordSearch != null) {
            exp = dictWordSearch.getExplainByte(mKeyIndex);
            if (exp != null) {
                dictSearchView.getmTextView().showTextByBigFirstLine(mKeyWord, exp, dictWordSearch.getExplainByteStart(exp), this.mDictID,
                        textSize);
            }
        }
        mCanPlaySound = dictWordSound.canPlay(mKeyWord);
        if (mCanPlaySound) {
            dictSearchView.getQuCiButtonModle().faYin.setEnabled(true);
            dictSearchView.getQuCiButtonModle().yuSu.setEnabled(true);
        } else {
            dictSearchView.getQuCiButtonModle().faYin.setEnabled(false);
            dictSearchView.getQuCiButtonModle().yuSu.setEnabled(false);
        }
        if (exp != null) {
            if (!dictSearchView.getQuCiButtonModle().ziHao.isEnabled()) {
                dictSearchView.getQuCiButtonModle().ziHao.setEnabled(true);
            }

            if (mDictID == DictInfo.DICT_BIHUA) {
                if (dictSearchView.getQuCiButtonModle().addNewWord.isEnabled()) {
                    dictSearchView.getQuCiButtonModle().addNewWord.setEnabled(false);
                }
                /*if (mHaziPackage == null) {
//                        mHaziPackage = new HanziPackage(getActivity());
//                        mHaziPackage = new HanziPackage();
                    if (dictWordSearch.openHanziPackage(getActivity())) {
                        mHaziPackage = dictWordSearch.getHanziPackage();
                    }
                }*/
                /*mKeyWordPhonetic = dictWordSearch.getBihuaPhonetic(exp,
                        dictWordSearch.getExplainByteStart(exp), exp.length);*/
//                mHaziPackage.setHanzi(mKeyWord, dictWordSound.getBihuaSound(mKeyWordPhonetic, getActivity()));
//                mKeyWordPhonetic = dictWordSearch.getBihuaPhonetic(po)
                dictWordSearch.getHanziPackage().setHanzi(mKeyWord, mKeyWordPhonetic);
                dictSearchView.getBiHuaView().initView(getActivity(), dictWordSearch.getHanziPackage());
                dictSearchView.getBiHuaView().setVisibility(View.VISIBLE);
                dictSearchView.getQuCiButtonModle().faYin.setEnabled(true);
                dictSearchView.getQuCiButtonModle().yuSu.setEnabled(true);

            } else {
                if (!dictSearchView.getQuCiButtonModle().addNewWord.isEnabled()) {
                    dictSearchView.getQuCiButtonModle().addNewWord.setEnabled(true);
                }

                /*if (mHaziPackage != null) {
                    mHaziPackage.recycle();
                }*/
                if (dictSearchView.getBiHuaView() != null && dictSearchView.getBiHuaView().getVisibility() == View.VISIBLE) {
                    dictSearchView.getBiHuaView().pause();
                    dictSearchView.getBiHuaView().setVisibility(View.GONE);
                }
            }
        }

    }
}
