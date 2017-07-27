package com.readboy.mobile.dictionary.fragment;

import android.content.Intent;
import android.graphics.RectF;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.readboy.Dictionary.R;
import com.readboy.depict.data.HanziPackage;
import com.readboy.depict.model.HanziInfo;
import com.readboy.depict.widget.HanziDemonstrationView;
import com.readboy.library.io.DictIOFile;
import com.readboy.library.provider.info.PlugWorldInfo;
import com.readboy.library.search.DictWordSearch;
import com.readboy.library.search.DictWordSound;
import com.readboy.library.utils.PhoneticUtils;
import com.readboy.library.widget.TextScrollView;
import com.readboy.mobile.dictionary.activity.StrokeViewAcitivity;
import com.readboy.mobile.dictionary.control.DictManagerControler;
import com.readboy.mobile.dictionary.dict.DictWordView;
import com.readboy.mobile.dictionary.dict.SelectedDictionaryManager;
import com.readboy.mobile.dictionary.utils.BaseHandler;
import com.readboy.mobile.dictionary.utils.IWordContentGetter;
import com.readboy.mobile.dictionary.utils.TextSizeManage;
import com.readboy.mobile.dictionary.view.LocalHorizontalItemTab;
import com.readboy.mobile.dictionary.view.SelectDictionaryDialog;
import com.sen.lib.support.CustomViewPager;
import com.sen.lib.view.ItemTabAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Senny on 2015/10/29.
 */
public class DictStrokeWordFragment extends Fragment implements IWordContentGetter {

    private DictStrokeWordHandler dictStrokeWordHandler;
    private PlugWorldInfo plugWorldInfo;
    private DictWordSearch dictWordSearch;
    private List<PolyphoneInfo> polyphoneInfoList;

    private CustomViewPager customViewPager;
    private LocalHorizontalItemTab horizontalItemTab;
    private DictWordView dictWordView;
    private View hanziDemonstrationGroup;
    private HanziDemonstrationView hanziDemonstrationView;
    private SelectDictionaryDialog selectDictionaryDialog;
    private TextSizeManage textSizeManage;
    private DictWordSound dictWordSound;
    private int contentWidth;
    private int selectDictType;
    private String selectString;

    /*private HanziPackage.DataChangeListener dataChangeListener = new HanziPackage.DataChangeListener() {
        @Override
        public void onDataChange(boolean b) {

        }
    };*/
    private Message tempMessage;

    public void setWord(PlugWorldInfo plugWorldInfo) {
        this.plugWorldInfo = plugWorldInfo;
    }

    public void setSelectedText(boolean isSelected) {
        if (dictWordView != null && dictWordView.getVisibility() == View.VISIBLE) {
            dictWordView.setSelectText(isSelected);
        } else if (getView().findViewById(R.id.fragment_stroke_multi_word_view) != null
                && getView().findViewById(R.id.fragment_stroke_multi_word_view).getVisibility() == View.VISIBLE) {
            if (customViewPager != null && customViewPager.getAdapter() != null) {
                DictStrokeWordContentFragment dictStrokeWordContentFragment = (DictStrokeWordContentFragment) customViewPager
                        .getAdapter().instantiateItem(customViewPager, customViewPager.getCurrentItem());
                if (dictStrokeWordContentFragment != null) {
                    dictStrokeWordContentFragment.setSelectedText(isSelected);
                    customViewPager.setScrollAble(false);
                }
            }
        }
    }

    @Override
    public boolean isSelectedText() {
        if (dictWordView != null && dictWordView.getVisibility() == View.VISIBLE) {
            return dictWordView.isSelectText();
        } else if (getView().findViewById(R.id.fragment_stroke_multi_word_view) != null
                && getView().findViewById(R.id.fragment_stroke_multi_word_view).getVisibility() == View.VISIBLE) {
            DictStrokeWordContentFragment dictStrokeWordContentFragment = (DictStrokeWordContentFragment) customViewPager
                    .getAdapter().instantiateItem(customViewPager, customViewPager.getCurrentItem());
            if (dictStrokeWordContentFragment != null) {
                return dictStrokeWordContentFragment.isSelectedText();
            }
        }
        return false;
    }

    public void enlargeTextSize() {
        if (dictWordView != null && dictWordView.getVisibility() == View.VISIBLE) {
            textSizeManage.enlargeTextSize();
            if (textSizeManage.getCurrentTextSize() != dictWordView.getTextSize()) {
                dictWordView.setTextSize(textSizeManage.getCurrentTextSize());
                dictWordView.setChangeLineTextSize(0, textSizeManage.getFirstLineTextSize());
            }
        } else if (getView().findViewById(R.id.fragment_stroke_multi_word_view) != null
                && getView().findViewById(R.id.fragment_stroke_multi_word_view).getVisibility() == View.VISIBLE) {
            DictStrokeWordContentFragment dictStrokeWordContentFragment = (DictStrokeWordContentFragment) customViewPager
                    .getAdapter().instantiateItem(customViewPager, customViewPager.getCurrentItem());
            if (dictStrokeWordContentFragment != null) {
                dictStrokeWordContentFragment.enlargeTextSize();
            }
        }
    }

    public void reduceTextSize() {
        if (dictWordView != null && dictWordView.getVisibility() == View.VISIBLE) {
            textSizeManage.reduceTextSize();
            if (textSizeManage.getCurrentTextSize() != dictWordView.getTextSize()) {
                dictWordView.setTextSize(textSizeManage.getCurrentTextSize());
                dictWordView.setChangeLineTextSize(0, textSizeManage.getFirstLineTextSize());
            }
        } else if (getView().findViewById(R.id.fragment_stroke_multi_word_view) != null
                && getView().findViewById(R.id.fragment_stroke_multi_word_view).getVisibility() == View.VISIBLE) {
            DictStrokeWordContentFragment dictStrokeWordContentFragment = (DictStrokeWordContentFragment) customViewPager
                    .getAdapter().instantiateItem(customViewPager, customViewPager.getCurrentItem());
            if (dictStrokeWordContentFragment != null) {
                dictStrokeWordContentFragment.reduceTextSize();
            }
        }
    }

    public int getTextSize() {
        if (dictWordView != null && dictWordView.getVisibility() == View.VISIBLE) {
            return dictWordView.getTextSize();
        } else if (getView().findViewById(R.id.fragment_stroke_multi_word_view) != null
                && getView().findViewById(R.id.fragment_stroke_multi_word_view).getVisibility() == View.VISIBLE) {
            DictStrokeWordContentFragment dictStrokeWordContentFragment = (DictStrokeWordContentFragment) customViewPager
                    .getAdapter().instantiateItem(customViewPager, customViewPager.getCurrentItem());
            if (dictStrokeWordContentFragment != null) {
                return dictStrokeWordContentFragment.getTextSize();
            }
        }
        return 0;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dictWordSearch = new DictWordSearch();
        dictWordSearch.open(DictIOFile.ID_BHDICT, getActivity(), null);
    }

    public void dismissAllDialog() {
        if (selectDictionaryDialog != null) {
            selectDictionaryDialog.dismiss();
        }
        DictStrokeWordContentFragment dictStrokeWordContentFragment = (DictStrokeWordContentFragment) customViewPager
                .getAdapter().instantiateItem(customViewPager, customViewPager.getCurrentItem());
        if (dictStrokeWordContentFragment != null) {
            dictStrokeWordContentFragment.dismissAllDialog();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        /*DictWordSearch checkDictWordSearch = new DictWordSearch();
        checkDictWordSearch.open(DictIOFile.ID_BHDICT, getContext(), dataChangeListener);
        checkDictWordSearch.close();*/
        if (tempMessage != null &&
                dictStrokeWordHandler != null) {
            dictStrokeWordHandler.sendMessageDelayed(tempMessage, 100);
            tempMessage = null;
        }
    }

    @Override
    public void onDestroyView() {
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
        if (dictWordSearch != null) {
            dictWordSearch.close();
        }
        dictWordSearch = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        textSizeManage = new TextSizeManage();
        dictWordSound = new DictWordSound();
        View root = inflater.inflate(R.layout.fragment_stroke_word, null);
        customViewPager = (CustomViewPager) root.findViewById(R.id.fragment_stroke_word_viewpager);
        horizontalItemTab = (LocalHorizontalItemTab) root.findViewById(R.id.fragment_stroke_word_tab);
        dictWordView = (DictWordView) root.findViewById(R.id.fragment_stroke_single_word_view);
        hanziDemonstrationView = (HanziDemonstrationView) root.findViewById(R.id.fragment_stroke_word_demo_view);
        hanziDemonstrationGroup = root.findViewById(R.id.fragment_stroke_word_demo_content);
        dictStrokeWordHandler = new DictStrokeWordHandler(this);

        dictWordView.setTextSize(textSizeManage.getCurrentTextSize());
        dictWordView.setChangeLineTextSize(0, textSizeManage.getFirstLineTextSize());
        horizontalItemTab.setAdapter(new DictStrokeWordTabAdapter());
//        horizontalItemTab.setTabColor(getResources().getColor(R.color.dark_red));
//        horizontalItemTab.setTabWidth(contentWidth / 4);
//        horizontalItemTab.setTabHeight(4);
//        horizontalItemTab.setItemBackground(R.drawable.polyphone_background);

//        horizontalItemTab.setItemTabHeight(4);
//        horizontalItemTab.setItemTabColor(getResources().getColor(R.color.dark_red_1));
        customViewPager.addPagerChangeListener(horizontalItemTab);
        customViewPager.setScrollAble(false);
        customViewPager.setAdapter(new DictStrokeWordAdapter(getChildFragmentManager()));
        root.findViewById(R.id.fragment_stroke_view_play).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playStrokeView();
            }
        });

        hanziDemonstrationView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*DictStrokeViewFragment dictStrokeViewFragment = new DictStrokeViewFragment();
                dictStrokeViewFragment.setStrokePackage(dictWordSearch.getHanziPackage());
                if (getParentFragment() != null) {
                    DictManagerControler.addStackFragmentWithAnim(getParentFragment().getFragmentManager().beginTransaction(),
                            R.id.fragment_main_extend_stroke_view,
                            DictStrokeViewFragment.class.getSimpleName(),
                            DictStrokeViewFragment.class.getSimpleName(),
                            dictStrokeViewFragment, R.anim.right_in, 0, 0, R.anim.right_out);
                }*/
                DictManagerControler.getInstance().hideInput(getActivity());
                playStrokeView();
            }
        });

        root.findViewById(R.id.fragment_stroke_view_sound).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (polyphoneInfoList != null) {
                    byte[] soundBytes = null;
                    if (polyphoneInfoList.size() == 1) {
                        soundBytes = dictWordSearch.getHanziSoundBytes(polyphoneInfoList.get(0).phonetic);

                    } else if (polyphoneInfoList.size() > 1){
                        soundBytes = dictWordSearch.getHanziSoundBytes(polyphoneInfoList.get(customViewPager.getCurrentItem()).phonetic);
                    }
                    if (soundBytes != null && soundBytes.length > 0) {
                        dictWordSound.playNewSound(soundBytes, 0);
                    }
                    /*if (getParentFragment() != null) {
                        SoundAnimationFragment.show(getParentFragment().getFragmentManager());
                    }*/
                    if (v.getBackground() instanceof AnimationDrawable) {
                        AnimationDrawable animationDrawable = (AnimationDrawable) v.getBackground();
                        animationDrawable.stop();
                        animationDrawable.start();
                    }
                }
            }
        });

        root.post(new Runnable() {
            @Override
            public void run() {
//                DictManagerControler.getInstance().execute(new LoadData(plugWorldInfo.polyphone));
                contentWidth = container.getWidth();
//                horizontalItemTab.setItemBackgroundWidth(contentWidth / 4);
                if (polyphoneInfoList == null) {
                    polyphoneInfoList = new ArrayList<PolyphoneInfo>();
                    loadData(plugWorldInfo.polyphone);
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
                if (endRectF != null && startRectF != null) {
                    selectDictType = DictWordSearch.getDictType(text);
                    selectString = text;
                    String[] items = SelectedDictionaryManager.getDictionaryLabelArray(getActivity(),
                            DictWordSearch.getDictType(text));
                    if (endRectF.top > startRectF.top) {
                        /*int bottom = ((int) endRectF.bottom - dictWordView.getScrollY());
                        selectDictionaryDialog
                                .showDialog(new int[]{(int) endRectF.right, bottom}, items, true);*/
                        int bottom = ((int) endRectF.bottom - dictWordView.getScrollY());
                        if (((View) dictWordView.getParent()).getHeight() > (bottom + 100)) {
                            selectDictionaryDialog
                                    .showDialog(new int[]{(int) endRectF.right, bottom}, items, true);
                        } else {
                            int top = ((int) startRectF.top - dictWordView.getScrollY());
                            selectDictionaryDialog
                                    .showDialog(Gravity.TOP, new int[]{(int) startRectF.left, top}, items, false);
                        }
                    } else if (startRectF.top > endRectF.top) {
                        int top = ((int) endRectF.top - dictWordView.getScrollY());
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

    private void loadData(List<HanziInfo> hanziInfoList) {
        List<PolyphoneInfo> data = new ArrayList<>();
        if (hanziInfoList != null && dictWordSearch != null) {
            for (HanziInfo hanziInfo : hanziInfoList) {
                PolyphoneInfo polyphoneInfo = new PolyphoneInfo();
                if (hanziInfo != null) {
                    dictWordSearch.getHanziPackage().setHanzi(hanziInfo);
                    String expain = dictWordSearch.getHanziPackage().getRemark();
                    if (expain != null) {
                        polyphoneInfo.phonetic = hanziInfo.phonetic;
                        polyphoneInfo.expain = expain;
                        data.add(polyphoneInfo);
                    }
                }

            }
        }
        if (dictStrokeWordHandler != null) {
            dictStrokeWordHandler.sendMessages(DictStrokeWordHandler.LOAD_DATA_SUCCESS, data, 0, 0, 0);
        }
    }

    private void playStrokeView() {
//        DictStrokeViewFragment dictStrokeViewFragment = new DictStrokeViewFragment();
        if (customViewPager != null) {
            int currentIndex = customViewPager.getCurrentItem();
            if (dictWordSearch.getHanziPackage() != null) {
                HanziInfo hanziInfo = dictWordSearch.getHanziPackage().getHanziInfo();
                if (hanziInfo != null && polyphoneInfoList != null && polyphoneInfoList.size() > currentIndex) {
                    PolyphoneInfo polyphoneInfo = polyphoneInfoList.get(currentIndex);
                    if (polyphoneInfo != null) {
                        hanziInfo.phonetic = polyphoneInfo.phonetic;
                        byte[] soundBytes = dictWordSearch.getHanziSoundBytes(polyphoneInfo.phonetic);
                        dictWordSearch.getHanziPackage().setHanzi(hanziInfo.word, soundBytes);

                        Intent intent = new Intent(getActivity(), StrokeViewAcitivity.class);
                        intent.putExtra(StrokeViewAcitivity.EXTRA_WORLD, hanziInfo.word);
                        intent.putExtra(StrokeViewAcitivity.EXTRA_SOUND_BYTES, soundBytes);
                        intent.putExtra(StrokeViewAcitivity.EXTRA_PHONETIC, hanziInfo.phonetic);
                        startActivity(intent);
                        getActivity().overridePendingTransition(R.anim.right_in, R.anim.fake_anim);
                    }
                }
            }
        }
        /*dictStrokeViewFragment.setStrokePackage(dictWordSearch.getHanziPackage());
        if (getParentFragment() != null
                && getParentFragment().getFragmentManager().findFragmentByTag(DictStrokeViewFragment.class.getSimpleName()) == null) {
            DictManagerControler.addStackFragmentWithAnim(getParentFragment().getFragmentManager().beginTransaction(),
                    R.id.fragment_main_extend_stroke_view,
                    DictStrokeViewFragment.class.getSimpleName(),
                    DictStrokeViewFragment.class.getSimpleName(),
                    dictStrokeViewFragment, R.anim.right_in, 0, 0, R.anim.right_out);
        }*/
    }

    private class DictStrokeWordHandler extends BaseHandler<DictStrokeWordFragment> {

        final static int LOAD_DATA_SUCCESS = 0x01;

        public DictStrokeWordHandler(DictStrokeWordFragment dictStrokeWordFragment) {
            super(dictStrokeWordFragment);
        }

        @Override
        public void handleMessage(Message msg) {
            if (getReference().get() == null) {
                return;
            }

            if (!isResumed()) {
                tempMessage = Message.obtain(msg);
                return;
            }

            switch (msg.what) {
                case LOAD_DATA_SUCCESS:
                    if (msg.obj instanceof List) {
                        final List<PolyphoneInfo> data = (List<PolyphoneInfo>) msg.obj;
                        if (data != null) {
                            /*getReference().get().hanziDemonstrationView.initView(getActivity(), dictWordSearch.getHanziPackage());
                            getReference().get().hanziDemonstrationView.findViewById(R.id.continuous).setVisibility(View.GONE);
                            getReference().get().hanziDemonstrationView.findViewById(R.id.step_by_step).setVisibility(View.GONE);
                            getReference().get().hanziDemonstrationView.findViewById(R.id.speed).setVisibility(View.GONE);
                            getReference().get().hanziDemonstrationView.findViewById(R.id.depict).setVisibility(View.GONE);*/
//                            getReference().get().hanziDemonstrationView.setVisibility(View.VISIBLE);
                            getReference().get().hanziDemonstrationGroup.setVisibility(View.VISIBLE);
                            getReference().get().hanziDemonstrationView.setHanziWithoutSound(dictWordSearch.getHanziPackage());
                            getReference().get().hanziDemonstrationView.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    getReference().get().hanziDemonstrationView.forceEnd();
                                }
                            }, 50);

                            if (data.size() > 1) {
                                getReference().get().getView().findViewById(R.id.fragment_stroke_multi_word_view).setVisibility(View.VISIBLE);
                                getReference().get().dictWordView.setVisibility(View.GONE);
                                int offset = 0;
                                FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) horizontalItemTab.getLayoutParams();
                                if (lp != null) {
                                    offset = lp.leftMargin + lp.rightMargin;
                                }
                                int itemTabContentWidth = data.size() * (contentWidth - offset) / 4;
                                if (4 > data.size()) {
                                    itemTabContentWidth = data.size() * (contentWidth - offset) / data.size();
                                }
                                getReference().get().horizontalItemTab.setMinimumWidth(itemTabContentWidth);
//                                getReference().get().horizontalItemTab.setItemBackgroundWidth(itemTabContentWidth);
                                getReference().get().horizontalItemTab.requestLayout();
                                getReference().get().hanziDemonstrationView.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            polyphoneInfoList = data;
                                            getReference().get().horizontalItemTab.getAdpater().notifyDataAll();
                                            getReference().get().customViewPager.getAdapter().notifyDataSetChanged();
                                        } catch (IllegalArgumentException e) {
                                            e.printStackTrace();
                                            getFragmentManager().popBackStackImmediate();
                                        }
                                    }
                                });
                            } else {
                                polyphoneInfoList = data;
                                getReference().get().getView().findViewById(R.id.fragment_stroke_multi_word_view).setVisibility(View.GONE);
                                getReference().get().dictWordView.setVisibility(View.VISIBLE);
                                getReference().get().dictWordView.setTextSize(textSizeManage.getCurrentTextSize());
                                getReference().get().dictWordView.setDictWordText(plugWorldInfo.world, data.get(0).expain);
                            }

                        }
                    }
                    break;
            }
        }

    }

    private class DictStrokeWordAdapter extends FragmentStatePagerAdapter {

        final FragmentManager mFragmentManager;
        ArrayList<Fragment> mFragments = new ArrayList<Fragment>();

        DictStrokeWordAdapter(FragmentManager fm) {
            super(fm);
            mFragmentManager = fm;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Fragment fragment = (Fragment) super.instantiateItem(container, position);
            if (mFragments.size() > position) {
                if (mFragments.get(position) != null) {
                    return fragment;
                }
            }
            while (mFragments.size() <= position) {
                mFragments.add(null);
            }
            mFragments.set(position, fragment);
            mFragmentManager.beginTransaction().remove(fragment);
            mFragmentManager.beginTransaction().add(container.getId(), fragment, fragment.toString());
            return fragment;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            super.destroyItem(container, position, object);
            mFragments.set(position, null);
        }

        @Override
        public void restoreState(Parcelable state, ClassLoader loader) {
            super.restoreState(state, loader);
            if (state != null) {
                Bundle bundle = (Bundle) state;
                bundle.setClassLoader(loader);
                Iterable<String> keys = bundle.keySet();
                for (String key : keys) {
                    if (key.startsWith("f")) {
                        int index = Integer.parseInt(key.substring(1));
                        Fragment f = mFragmentManager.getFragment(bundle, key);
                        if (f != null) {
                            while (mFragments.size() <= index) {
                                mFragments.add(null);
                            }
                            f.setMenuVisibility(false);
                            mFragments.set(index, f);
                        }
                    }
                }
            }
        }

        @Override
        public Fragment getItem(int position) {
            DictStrokeWordContentFragment dictStrokeWordContentFragment = new DictStrokeWordContentFragment();
            if (polyphoneInfoList != null && polyphoneInfoList.size() > position) {
                dictStrokeWordContentFragment.setExpain(plugWorldInfo.world, polyphoneInfoList.get(position).expain);
            }
            return dictStrokeWordContentFragment;
        }

        @Override
        public int getCount() {
            if (polyphoneInfoList != null) {
                return polyphoneInfoList.size();
            }
            return 0;
        }
    }

    private class DictStrokeWordTabAdapter extends ItemTabAdapter {

        boolean isScrolling = false;
        int tabIndex;

        @Override
        public void onScroll(int position, float positionOffset) {
            if (horizontalItemTab != null) {
                horizontalItemTab.invalidate();
            }
            if (positionOffset == 0.0f) {
                isScrolling = false;
                /*if (horizontalItemTab != null) {
                    View lastItemView = horizontalItemTab.getItemView(position - 1);
                    if (lastItemView != null) {
                        ((TextView) lastItemView.findViewById(R.id.item_dict_stroke_tab_name_text)).setTextColor(getResources().getColor(R.color.light_grey_5));
                    }
                    View currentItemView = horizontalItemTab.getItemView(position);
                    if (currentItemView != null) {
                        ((TextView) currentItemView.findViewById(R.id.item_dict_stroke_tab_name_text)).setTextColor(getResources().getColor(R.color.dark_yellow_1));
                    }
                    View nextItemView = horizontalItemTab.getItemView(position + 1);
                    if (nextItemView != null) {
                        ((TextView) nextItemView.findViewById(R.id.item_dict_stroke_tab_name_text)).setTextColor(getResources().getColor(R.color.light_grey_5));
                    }
                }*/
            }
            tabIndex = position;
        }

        @Override
        public void onScrolledStateChange(int state) {
            switch (state) {
                case ViewPager.SCROLL_STATE_IDLE:
                    isScrolling = false;
                    /*for (int i = 0; i < getCount(); i++) {
                        View view = horizontalItemTab.getItemView(i);
                        if (tabIndex != i) {
                            if (view != null) {
                                ((TextView) view.findViewById(R.id.item_dict_stroke_tab_name_text)).setTextColor(getResources().getColor(R.color.light_grey_5));
                            }
                        } else {
                            ((TextView) view.findViewById(R.id.item_dict_stroke_tab_name_text)).setTextColor(getResources().getColor(R.color.dark_yellow_1));
                        }

                    }*/
                    break;
            }
        }

        @Override
        public View getView(View view, ViewGroup viewGroup, final int i) {
            if (view == null) {
                view = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.item_dict_stroke_tab_name, null);
                int offset = 0;
                FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) horizontalItemTab.getLayoutParams();
                if (lp != null) {
                    offset = lp.leftMargin + lp.rightMargin;
                }
                if (4 > polyphoneInfoList.size()) {
                    view.setMinimumWidth((contentWidth - offset) / polyphoneInfoList.size());
                } else {
                    view.setMinimumWidth((contentWidth - offset) / 4);
                }
                view.setMinimumHeight(horizontalItemTab.getHeight());
            }
            if (polyphoneInfoList != null && polyphoneInfoList.size() > i && i >= 0) {
                ((TextView) view.findViewById(R.id.item_dict_stroke_tab_name_text)).setText(PhoneticUtils.checkString(polyphoneInfoList.get(i).phonetic));
            }
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    customViewPager.setCurrentItem(i, true);
                    DictManagerControler.getInstance().hideInput(getActivity());
                }
            });
            return view;
        }

        @Override
        public int getCount() {
            if (polyphoneInfoList != null) {
                return polyphoneInfoList.size();
            }
            return 0;
        }
    }

    private class PolyphoneInfo {
        String phonetic;
        String expain;
    }
}
