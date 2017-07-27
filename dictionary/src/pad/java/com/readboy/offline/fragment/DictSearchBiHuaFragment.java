package com.readboy.offline.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.readboy.offline.adapter.BiHuaListAdapter;
import com.readboy.offline.adapter.BiHuaListAdapter.BiHuaWordInfo;
import com.readboy.depict.data.HanziPackage;
import com.readboy.depict.model.HanziInfo;
import com.readboy.depict.widget.DemonstrationGroup;
import com.readboy.dict.DictWordView;
import com.readboy.library.io.DictFile;
import com.readboy.library.io.DictIOFile;
import com.readboy.library.search.DictWordSearch;
import com.readboy.library.utils.DelayClick;
import com.readboy.library.utils.DictInfo;
import com.readboy.library.utils.PhoneticUtils;

import java.util.ArrayList;
import java.util.List;

import com.readboy.offline.mode.DictModleButton;
import com.readboy.offline.view.DictSearchBiHuaView;

import com.readboy.Dictionary.R;

/**
 * Editor: sgc
 * Date: 2015/01/23
 */
public class DictSearchBiHuaFragment extends BaseDictFragment implements DictSearchBiHuaView.DictSearchBiHuaClickListener, DemonstrationGroup.OnDialogShowListener {

    private static final int UPDATE_FINISH_HANZI_DATA = 0x101;
    private static final int UPDATE_FAILD_HANZI_DATA = 0x102;

    private DictSearchBiHuaView dictSearchBiHuaView = null;
    private List<HanziInfo> hanziInfoList = null;

    private String mKeyWord;

    private Message currentMessage = null;
    private boolean isPause = false;
    /**
     * 描红框是否能弹出
     */
    private boolean canDecpicDialogShow = true;

    private HanziPackage.DataChangeListener dataChangeListener = new HanziPackage.DataChangeListener() {
        @Override
        public void onDataChange(boolean b) {
            if (b) {
                sendMessages(UPDATE_FINISH_HANZI_DATA, mKeyWord, 50);
            } else {
                sendMessages(UPDATE_FAILD_HANZI_DATA, null, 0);
            }
        }
    };

    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {

                case UPDATE_FINISH_HANZI_DATA:
                    if (isPause) {
                        currentMessage = Message.obtain();
                        currentMessage.what = msg.what;
                        currentMessage.obj = msg.obj;
                        break;
                    }
                    String keyWord = (String) msg.obj;
                    if (keyWord != null) {
                        reset(keyWord);
                    } else {
                        searchFaild();
                    }
                    break;

                case UPDATE_FAILD_HANZI_DATA:
                    loadFaild();
                    break;

            }

        }

    };

    public void setKeyWord(String mKeyWord) {
        this.mKeyWord = mKeyWord;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mDictID = DictFile.ID_BHDICT;
        dictSearchBiHuaView = (DictSearchBiHuaView) inflater.inflate(R.layout.layout_dict_search_bihua, null, false);
        dictSearchBiHuaView.setDictSearchBiHuaClickListener(this);
        dictSearchBiHuaView.getBiHuaView().setOnDialogShowListener(this);
        if (dictWordSearch.open(mDictID, getActivity(), dataChangeListener)) {
        } else {
            loadFaild();
        }
        return dictSearchBiHuaView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onResume() {
        if (isPause && dictWordSearch != null && dictWordSearch.getHanziPackage() != null) {
            try {
                dictWordSearch.getHanziPackage().checkDataChange(getActivity());
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
        if (currentMessage != null) {
            sendMessages(currentMessage.what, currentMessage.obj, 0);
            currentMessage = null;
        }
        isPause = false;
        super.onResume();
    }

    @Override
    public void onPause() {
        isPause = true;
        super.onPause();
        if (dictSearchBiHuaView != null) {
            if (dictSearchBiHuaView.getBiHuaView() != null &&
                    dictSearchBiHuaView.getBiHuaView().getVisibility() == View.VISIBLE) {
                dictSearchBiHuaView.getBiHuaView().pause();
            }
            if (dictSearchBiHuaView.getmExpView() != null) {
                dictSearchBiHuaView.getmExpView().onPause(false);
            }
        }
    }

    @Override
    public void onResumeView() {
        if (dictSearchBiHuaView != null) {
            if (dictSearchBiHuaView.getBiHuaView() != null &&
                    dictSearchBiHuaView.getBiHuaView().getVisibility() == View.VISIBLE) {
                dictSearchBiHuaView.getBiHuaView().resume();
            }
            if (dictSearchBiHuaView.getmExpView() != null) {
                dictSearchBiHuaView.getmExpView().onResume();
            }
        }

    }

    @Override
    public void onPauseView() {
        if (dictSearchBiHuaView != null) {
            if (dictSearchBiHuaView.getBiHuaView() != null &&
                    dictSearchBiHuaView.getBiHuaView().getVisibility() == View.VISIBLE) {
                dictSearchBiHuaView.getBiHuaView().pause();
            }
            if (dictSearchBiHuaView.getmExpView() != null) {
                dictSearchBiHuaView.getmExpView().onPause(false);
            }
        }
    }

    @Override
    public void onDestroyView() {
        handler = null;
        isPause = true;
        super.onDestroyView();
    }

    @Override
    public void listSelect(int position) {
        super.dismissAllDialog();
        if (dictSearchBiHuaView != null) {

            BiHuaListAdapter biHuaListAdapter = (BiHuaListAdapter) dictSearchBiHuaView.getWordList().getAdapter();
            if (biHuaListAdapter != null) {
                BiHuaWordInfo biHuaWordInfo = biHuaListAdapter.getSelectItem();
                if (biHuaWordInfo != null && hanziInfoList != null) {
                    dictSearchBiHuaView.getBiHuaView().dismissDialog();
                    showView(biHuaWordInfo.index, hanziInfoList.get(biHuaWordInfo.index), dictSearchBiHuaView.getmExpView(), dictSearchBiHuaView.getDictModleButton());
                }
            }
        }

    }

    @Override
    public void onClickFaYin(String str) {
        if (dictWordSound != null) {
            if (mDictID != DictInfo.DICT_BIHUA) {
                dictWordSound.playWord(str, super.mAudioRate);
            } else {
//                if (mKeyWordPhonetic != null) {
//                    dictWordSound.playBihuaSound(mKeyWordPhonetic, getActivity(), super.mAudioRate);
//                }
//                byte[] soundBytes = dictWordSearch.getHanziSoundBytes(mKeyIndex);
                byte[] soundBytes = dictWordSearch.getHanziSoundBytes(hanziInfoList.get(mKeyIndex).phonetic);
                if (soundBytes != null) {
                    dictWordSound.playSound(soundBytes, super.mAudioRate);
                }
                dictSearchBiHuaView.getBiHuaView().showCompletedHanzi();
            }
        }
    }

    @Override
    public void onClickZiHao() {
        if (dictSearchBiHuaView != null) {
            DictWordView scrollView = dictSearchBiHuaView.getmExpView();
            /*if (scrollView != null && dictWordSearch != null) {
                byte[] byteData = dictWordSearch.getExplainByte(hanziInfoList.get(mKeyIndex));
                scrollView.showText(mKeyWord, byteData, 0, mDictID, getTextSize());
            }*/
            if (scrollView != null) {
                scrollView.setTextSize(getTextSize());
            }
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

    }

    @Override
    public void selectText(String selectStr) {
        if (dictSearchBiHuaView != null && dictSearchBiHuaView.getBiHuaView() != null) {
            dictSearchBiHuaView.getBiHuaView().dismissDialog();
        }
        if (dictSearchBiHuaView != null) {
            super.showSelectWordDialog(selectStr, DictWordSearch.getDictType(selectStr), dictSearchBiHuaView.getmExpView());
        }
    }

    @Override
    public void selectWordToIntent(int mDictID, String searchword) {
        canDecpicDialogShow = false;
        super.selectWordToIntent(mDictID, searchword);
    }

    @Override
    public void getTouchScollTextView() {
        super.dismissAllDialog();
    }

    @Override
    public boolean dispatchDialogTouch(MotionEvent motionEvent) {
        if (!canDecpicDialogShow) {
            return true;
        }
        return false;
    }

    @Override
    public void onDialogShow() {
        super.dismissAllDialog();
    }

    @Override
    public void onResultFromFragment(int requestCode, int resultCode, Intent data) {
        super.onResultFromFragment(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            dictSearchBiHuaView.cancleQuciStatu();
        }
        canDecpicDialogShow = true;
    }

    private void reset(String keyWord) {

        dictSearchBiHuaView.setView(DictFile.ID_BHDICT);
        dictWordSearch.reset();
        hanziInfoList = dictWordSearch.getHanziKeyList(keyWord);
        if (hanziInfoList != null && hanziInfoList.size() > 0) {
            BiHuaListAdapter biHuaListAdapter = new BiHuaListAdapter();
            List<BiHuaWordInfo> items = new ArrayList<BiHuaWordInfo>();

            int i = 0;
            for (HanziInfo hanziInfo : hanziInfoList) {
                BiHuaWordInfo biHuaWordInfo = biHuaListAdapter.new BiHuaWordInfo();
                biHuaWordInfo.text = hanziInfo.word + " " + PhoneticUtils.checkString(hanziInfo.phonetic);
                biHuaWordInfo.index = i;
                items.add(biHuaWordInfo);
                i++;
            }
            biHuaListAdapter.setListItems(items);
            dictSearchBiHuaView.getWordList().setAdapter(biHuaListAdapter);
            showView(0, hanziInfoList.get(0), dictSearchBiHuaView.getmExpView(), dictSearchBiHuaView.getDictModleButton());
            return;
        }
        searchFaild();
    }

    private void loadFaild() {
        Toast.makeText(getActivity(), R.string.toast_load_data_failed, Toast.LENGTH_SHORT).show();
        if (getActivity() != null) {
            getActivity().finish();
        }
    }

    private void searchFaild() {
        Toast.makeText(getActivity(), R.string.select_search_failed, Toast.LENGTH_SHORT).show();
        if (getActivity() != null) {
            getActivity().finish();
        }
    }

    private void sendMessages(int what, Object obj, int delayMillis) {
        Message msg = Message.obtain();
        msg.what = what;
        msg.obj = obj;
        if (handler != null) {
            handler.sendMessageDelayed(msg, delayMillis);
        }
    }

    private void showView(int position, HanziInfo hanziInfo, DictWordView scrollView, DictModleButton modleButton) {
        if (this.mKeyIndex == position) {
            return;
        }
        this.mKeyIndex = position;
        byte[] byteData;
        if (mDictID == DictIOFile.ID_BHDICT) {
            if (dictWordSound != null && hanziInfo != null) {
                dictWordSearch.getHanziPackage().setHanzi(hanziInfo);
//                dictWordSearch.getHanziPackage().setHanzi(hanziInfo.word, hanziInfo.phonetic);
                dictSearchBiHuaView.getBiHuaView().initView(getActivity(), dictWordSearch.getHanziPackage());
                if (dictSearchBiHuaView.getBiHuaView().getVisibility() != View.VISIBLE) {
                    dictSearchBiHuaView.getBiHuaView().setVisibility(View.VISIBLE);
                }

                modleButton.faYin.setEnabled(true);
                modleButton.yuSu.setEnabled(true);
                mCanPlaySound = true;
                byteData = dictWordSearch.getExplainByte(hanziInfo);
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
                    scrollView.showText(mKeyWord, byteData, 0, mDictID, getTextSize());
                }
            }
        }


    }
}
