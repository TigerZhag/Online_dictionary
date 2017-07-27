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
import com.readboy.library.gif.GifView;
import com.readboy.library.io.DictIOFile;
import com.readboy.library.io.DongManParam;
import com.readboy.library.provider.info.PlugWorldInfo;
import com.readboy.library.search.DictWordSearch;
import com.readboy.library.search.DictWordSound;
import com.readboy.library.widget.TextScrollView;
import com.readboy.mobile.dictionary.control.DictManagerControler;
import com.readboy.mobile.dictionary.dict.SelectedDictionaryManager;
import com.readboy.mobile.dictionary.utils.BaseHandler;
import com.readboy.mobile.dictionary.utils.IWordContentGetter;
import com.readboy.mobile.dictionary.utils.TextSizeManage;
import com.readboy.mobile.dictionary.dict.DictWordView;
import com.readboy.mobile.dictionary.view.CustomToast;
import com.readboy.mobile.dictionary.view.SelectDictionaryDialog;

/**
 * Created by Senny on 2015/10/28.
 */
public class DictAnimationWordContentFragment extends Fragment implements IWordContentGetter {

    private DictAnmationWordHandler dictAnmationWordHandler;
    private PlugWorldInfo plugWorldInfo;
    private GifView gifView;
    private DictWordView dictWordView;
    private SelectDictionaryDialog selectDictionaryDialog;
    private String selectString;
    private int selectDictType;
    private TextSizeManage textSizeManage;
    private DictWordSound dictWordSound;

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
        View root = inflater.inflate(R.layout.fragment_animation_word, null);
        gifView = (GifView) root.findViewById(R.id.fragment_animation_word_gif);
        dictWordView = (DictWordView) root.findViewById(R.id.fragment_animation_word_view);
        dictAnmationWordHandler = new DictAnmationWordHandler(this);
        root.post(new Runnable() {
            @Override
            public void run() {
                DictManagerControler.getInstance().execute(new LoadData(plugWorldInfo.index));
            }
        });

        if (!dictWordSound.canPlay(plugWorldInfo.world)) {
            root.findViewById(R.id.fragment_animation_word_sound).setVisibility(View.GONE);
        }
        root.findViewById(R.id.fragment_animation_word_sound).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dictWordSound.playWord2(plugWorldInfo.world, null, null);
                /*if (getParentFragment() != null) {
                    SoundAnimationFragment.show(getParentFragment().getFragmentManager());
                }*/if (v.getBackground() instanceof AnimationDrawable) {
                    AnimationDrawable animationDrawable = (AnimationDrawable) v.getBackground();
                    animationDrawable.stop();
                    animationDrawable.start();
                }

            }
        });
        dictWordView.setTextSize(textSizeManage.getCurrentTextSize());
        dictWordView.setChangeLineTextSize(0, textSizeManage.getFirstLineTextSize());
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
                if (endRectF != null && startRectF != null) {
                    selectDictType = DictWordSearch.getDictType(text);
                    selectString = text;
                    String[] items = SelectedDictionaryManager.getDictionaryLabelArray(getActivity(),
                            selectDictType);
                    if (endRectF.top > startRectF.top) {
                        int bottom = ((int) endRectF.bottom - dictWordView.getScrollY());
                        selectDictionaryDialog
                                .showDialog(new int[]{(int) endRectF.right, bottom}, items, true);
                    } else if (startRectF.top > endRectF.top) {
                        int top = ((int) endRectF.top - dictWordView.getScrollY());
                        selectDictionaryDialog
                                .showDialog(Gravity.TOP, new int[]{(int) endRectF.left, top}, items, false);
                    } else {
                        int top = ((int) endRectF.top - dictWordView.getScrollY());
                        selectDictionaryDialog
                                .showDialog(Gravity.TOP, new int[]{(int) endRectF.left, top}, items, false);
                    }
                } else {
                    selectString = null;
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
        if (gifView != null) {
            gifView.destroy();
        }
        dismissAllDialog();
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

    private class DictAnmationWordHandler extends BaseHandler<DictAnimationWordContentFragment> {

        final static int LOAD_DATA_SUCCESS = 0x01;

        public DictAnmationWordHandler(DictAnimationWordContentFragment dictAnimationWordContentFragment) {
            super(dictAnimationWordContentFragment);
        }

        @Override
        public void handleMessage(Message msg) {

            DictAnimationWordContentFragment instance = getReference().get();
            if (instance == null) {
                return;
            }

            switch (msg.what) {

                case LOAD_DATA_SUCCESS:
                    if (msg.obj instanceof DongManParam) {
                        DongManParam dongManParam = (DongManParam) msg.obj;
                        gifView.setGifImage(dongManParam.dmData);
                        dictWordView.setDictWordText(dongManParam.explainData);
                    }
                    break;

            }
        }
    }

    private class LoadData implements Runnable {

        int index = -1;

        LoadData(int index) {
            this.index = index;
        }

        @Override
        public void run() {
            if (index != -1) {
                DictWordSearch dictWordSearch = new DictWordSearch();
                if (getActivity() != null) {
                    dictWordSearch.open(DictIOFile.ID_DMDICT, getActivity(), null);
                    DongManParam dongManParam = dictWordSearch.getDongManParam(index);
                    dictAnmationWordHandler.sendMessages(DictAnmationWordHandler.LOAD_DATA_SUCCESS,
                            dongManParam, 0, 0, 0);
                }
            }
            if (dictAnmationWordHandler != null) {
                dictAnmationWordHandler.sendMessages(DictAnmationWordHandler.LOAD_DATA_SUCCESS,
                        null, 0, 0, 0);
            }

        }
    }

}
