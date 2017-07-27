package com.readboy.mobile.dictionary.fragment;

import android.graphics.RectF;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.readboy.Dictionary.R;
import com.readboy.library.io.DictIOFile;
import com.readboy.library.provider.info.PlugWorldInfo;
import com.readboy.library.search.DictWordSearch;
import com.readboy.library.search.DictWordSound;
import com.readboy.library.widget.TextScrollView;
import com.readboy.mobile.dictionary.control.DictManagerControler;
import com.readboy.mobile.dictionary.dict.DictWordView;
import com.readboy.mobile.dictionary.dict.SelectedDictionaryManager;
import com.readboy.mobile.dictionary.utils.BaseHandler;
import com.readboy.mobile.dictionary.utils.IWordContentGetter;
import com.readboy.mobile.dictionary.utils.TextSizeManage;
import com.readboy.mobile.dictionary.view.CustomToast;
import com.readboy.mobile.dictionary.view.SelectDictionaryDialog;

/**
 * Created by Senny on 2015/10/27.
 */
public class DictWordContentFragment extends Fragment implements IWordContentGetter {

    private DictWordView dictWordView;
    private DictWordContentHandler dictWordContentHandler;

    private PlugWorldInfo plugWorldInfo;
    private SelectDictionaryDialog selectDictionaryDialog;
    private byte[] data;
    private String selectString;
    private int selectDictType;
    private TextSizeManage textSizeManage;
    private DictWordSound dictWordSound;

    private Message tempMessage;

    public void setWord(PlugWorldInfo plugWorldInfo) {
        this.plugWorldInfo = plugWorldInfo;
    }

    public void setSelectedText(boolean isSelected) {
        if (dictWordView != null) {
            dictWordView.setSelectText(isSelected);
        }
    }

    @Override
    public boolean isSelectedText() {
        if (dictWordView != null) {
            return dictWordView.isSelectText();
        }
        return false;
    }

    public void enlargeTextSize() {
        if (textSizeManage != null && dictWordView != null) {
            textSizeManage.enlargeTextSize();
            if (textSizeManage.getCurrentTextSize() != dictWordView.getTextSize()) {
                dictWordView.setTextSize(textSizeManage.getCurrentTextSize());
                dictWordView.setChangeLineTextSize(0, textSizeManage.getFirstLineTextSize());
            }
        }
    }

    public void reduceTextSize() {
        if (textSizeManage != null && dictWordView != null) {
            textSizeManage.reduceTextSize();
            if (textSizeManage.getCurrentTextSize() != dictWordView.getTextSize()) {
                dictWordView.setTextSize(textSizeManage.getCurrentTextSize());
                dictWordView.setChangeLineTextSize(0, textSizeManage.getFirstLineTextSize());
            }
        }
    }

    public int getTextSize() {
        if (dictWordView != null) {
            return dictWordView.getTextSize();
        }
        return 0;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        textSizeManage = new TextSizeManage();
        dictWordSound = new DictWordSound();
        dictWordSound.open();
        View root = inflater.inflate(R.layout.fragment_word_content, null);
        dictWordView = (DictWordView) root.findViewById(R.id.fragment_word_content_view);
        dictWordView.setTextSize(textSizeManage.getCurrentTextSize());
        dictWordView.setChangeLineTextSize(0, textSizeManage.getFirstLineTextSize());
        dictWordContentHandler = new DictWordContentHandler(this);
        root.post(new Runnable() {
            @Override
            public void run() {
                if (plugWorldInfo.data != null) {
                    if (plugWorldInfo.dictID == DictIOFile.ID_XDDICT
                            || plugWorldInfo.dictID == DictIOFile.ID_CYDICT
                            || plugWorldInfo.dictID == DictIOFile.ID_YHDICT) {
                        dictWordView.setDictWordText(plugWorldInfo.world, plugWorldInfo.data);
                    } else {
                        dictWordView.setDictWordText(plugWorldInfo.data);
                    }
                } else {
                    DictManagerControler.getInstance()
                            .execute(new LoadData(plugWorldInfo.index, plugWorldInfo.dictID));
                }

            }
        });
        if (!dictWordSound.canPlay(plugWorldInfo.world)) {
            root.findViewById(R.id.fragment_word_content_sound).setVisibility(View.GONE);
        }
        root.findViewById(R.id.fragment_word_content_sound).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dictWordSound.playWord2(plugWorldInfo.world, null, null);
                /*if (getParentFragment() != null) {
                    SoundAnimationFragment.show(getParentFragment().getFragmentManager());
                }*/
                if (v.getBackground() instanceof AnimationDrawable) {
                    AnimationDrawable animationDrawable = (AnimationDrawable) v.getBackground();
                    animationDrawable.stop();
                    animationDrawable.start();
                }
            }
        });
        dictWordView.setOnTextScrollViewListener(new TextScrollView.OnTextScrollViewListener() {
            @Override
            public void startAnalyTextPage() {

            }

            @Override
            public void loadingTextPage() {

            }

            @Override
            public void startSelectText() {

            }

            @Override
            public void endSelectText(String text, RectF startRectF, RectF endRectF) {
                if (endRectF != null && endRectF != null) {
                    selectDictType = DictWordSearch.getDictType(text);
                    selectString = text;
                    String[] items = SelectedDictionaryManager.getDictionaryLabelArray(getActivity(),
                            DictWordSearch.getDictType(text));
                    if (endRectF.top > startRectF.top) {
                        int bottom = ((int) endRectF.bottom - dictWordView.getScrollY());
                        if (getView().getHeight() > (bottom + 200)) {
                            selectDictionaryDialog
                                    .showDialog(new int[]{(int) endRectF.right, bottom}, items, true);
                        } else {
                            int top = ((int) startRectF.top - dictWordView.getScrollY());
                            if (0 > top) {
                                top = 0;
                            }
                            selectDictionaryDialog
                                    .showDialog(Gravity.TOP, new int[]{(int) startRectF.left, top}, items, false);
                        }

                    } else if (startRectF.top > endRectF.top) {
                        int top = ((int) endRectF.top - dictWordView.getScrollY());
                        if (0 > top) {
                            top = 0;
                        }
                        selectDictionaryDialog
                                .showDialog(Gravity.TOP, new int[]{(int) endRectF.left, top}, items, false);
                    } else {
                        int top = ((int) endRectF.top - dictWordView.getScrollY());
                        selectDictionaryDialog
                                .showDialog(Gravity.TOP, new int[]{(int) endRectF.left, top}, items, false);
                    }
                }
                DictManagerControler.getInstance().hideInput(getActivity());
            }

            @Override
            public void onMoveSelect(float x, float y) {

            }
        });

        selectDictionaryDialog = new SelectDictionaryDialog(getActivity(), dictWordView);
        selectDictionaryDialog.setOnSelectedItemListener(new SelectDictionaryDialog.OnSelectedItemListener() {
            @Override
            public void onSelected(int itemIndex) {
                if (selectDictType != -1 && selectString != null) {
                    int dictId = SelectedDictionaryManager.getDictionaryId(getActivity(), selectDictType, itemIndex);
                    DictSelectWordContentFragment dictSelectWordContentFragment = new DictSelectWordContentFragment();
                    if (getParentFragment() != null) {
                        dictSelectWordContentFragment.startSearch(getParentFragment().getFragmentManager(), getActivity(), dictId, selectString);
                    }
                } else {
                    if (getActivity() != null) {
                        CustomToast.getInstance(getActivity()).showToast(getActivity().getString(R.string.tip_searched_world_failed));
                    }
                }
                if (dictWordView != null) {
                    dictWordView.clearSelectedBackground();
                }
                selectDictType = -1;
                selectString = null;
            }

            @Override
            public void dismiss() {
                if (dictWordView != null) {
                    dictWordView.clearSelectedBackground();
                }
                selectDictType = -1;
                selectString = null;
            }
        });
        root.setTag(this);
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (tempMessage != null &&
                dictWordContentHandler != null) {
            dictWordContentHandler.sendMessageDelayed(tempMessage, 100);
            tempMessage = null;
        }
    }

    public void dismissAllDialog() {
        if (selectDictionaryDialog != null) {
            selectDictionaryDialog.dismiss();
        }
    }

    @Override
    public void onDestroyView() {
        if (dictWordSound != null) {
            dictWordSound.close();
        }
        if (plugWorldInfo != null) {
            plugWorldInfo.data = null;
        }
        dismissAllDialog();
        data = null;
        if (getView() != null) {
            getView().setTag(null);
        }
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        if (getView() != null) {
            getView().setTag(null);
        }
        super.onDestroy();
    }

    private class DictWordContentHandler extends BaseHandler<DictWordContentFragment> {

        final static int LOAD_DATA_SUCCESS = 0x01;
        final static int LOAD_DATA_FAIL = LOAD_DATA_SUCCESS + 1;

        public DictWordContentHandler(DictWordContentFragment dictWordContentFragment) {
            super(dictWordContentFragment);
        }

        @Override
        public void handleMessage(Message msg) {

            DictWordContentFragment instance = getReference().get();
            if (instance == null) {
                return;
            }

            if (!isResumed()) {
                tempMessage = Message.obtain(msg);
                return;
            }

            switch (msg.what) {

                case LOAD_DATA_SUCCESS:
                    if (msg.obj instanceof byte[]) {
                        if (instance.plugWorldInfo.dictID == DictIOFile.ID_XDDICT
                                || instance.plugWorldInfo.dictID == DictIOFile.ID_CYDICT
                                || instance.plugWorldInfo.dictID == DictIOFile.ID_YHDICT) {
                            instance.dictWordView.setDictWordText(instance.plugWorldInfo.world, (byte[]) msg.obj);
                        } else {
                            instance.dictWordView.setDictWordText((byte[]) msg.obj);
                        }
                        if (data == null) {
                            data = (byte[]) msg.obj;
                        }
                    }
                    break;

                case LOAD_DATA_FAIL:

                    break;

            }

        }
    }

    private class LoadData implements Runnable {

        private int index = -1;
        private int dictID = -1;

        LoadData(int index, int dictID) {
            this.index = index;
            this.dictID = dictID;
        }

        @Override
        public void run() {
            if (index != -1 && dictID != -1) {
                DictWordSearch dictWordSearch = new DictWordSearch();
                dictWordSearch.open(dictID, getActivity(), null);
                byte[] buffer = dictWordSearch.getExplainByte(index);
                int start = dictWordSearch.getExplainByteStart(buffer);
                for (int i=0;i<start;i++) {
                    buffer[i] = 0;
                }
                if (dictWordContentHandler != null) {
                    dictWordContentHandler.sendMessages(DictWordContentHandler.LOAD_DATA_SUCCESS, buffer, 0, 0, 0);
                }
                dictWordSearch.close();
            } else {
                if (dictWordContentHandler != null) {
                    dictWordContentHandler.sendMessages(DictWordContentHandler.LOAD_DATA_SUCCESS, null, 0, 0, 0);
                }
            }
        }

    }
}
