package com.readboy.mobile.dictionary.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.readboy.Dictionary.R;
import com.readboy.depict.model.HanziInfo;
import com.readboy.library.io.DictFile;
import com.readboy.library.io.DictIOFile;
import com.readboy.library.provider.info.PlugWorldInfo;
import com.readboy.library.search.DictWordSearch;
import com.readboy.mobile.dictionary.control.DictManagerControler;
import com.readboy.mobile.dictionary.provider.MobileProviderManager;
import com.readboy.mobile.dictionary.utils.BaseHandler;
import com.readboy.mobile.dictionary.utils.IWordContentController;
import com.readboy.mobile.dictionary.utils.TextSizeManage;
import com.readboy.mobile.dictionary.view.DictWordControlViewGroup;
import com.readboy.mobile.dictionary.view.LocalHorizontalItemTab;
import com.sen.lib.support.CustomViewPager;
import com.sen.lib.view.HorizontalItemTab;
import com.sen.lib.view.ItemTabAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Senny on 2015/10/28.
 */
public class DictWordItemFragment extends Fragment implements IWordContentController {

    private static final int DICT_ENGLISH_LIST[] = {DictIOFile.ID_LWDICT,/* DictIOFile.ID_DMDICT,*/
            DictIOFile.ID_YHDICT/*, DictIOFile.ID_YYDICT*/};
    private static final int DICT_CHINESE_LIST[] = {DictIOFile.ID_XDDICT, DictIOFile.ID_BHDICT, DictIOFile.ID_CYDICT,
            DictIOFile.ID_HYDICT};
    private String[] dictsName;
    private LocalHorizontalItemTab horizontalItemTab;
    private CustomViewPager customViewPager;
    private PlugWorldInfo plugWorldInfo;
    private List<PlugWorldInfo> data;
    private DictWordItemHandler dictWordItemHandler;
    private WorldItemPageAdapter worldItemPageAdapter;
    private FragmentManager parentFragmentManager;
    private OnDictWordItemFragmentListener listener;
    private Context parentContext;
    private int contentWidth;
    private Message tempMessage;

    private DictWordControlViewGroup dictWordControlViewGroup;

    public void setOnDictWordItemFragmentListener(OnDictWordItemFragmentListener listener) {
        this.listener = listener;
    }

    public void init(PlugWorldInfo plugWorldInfo) {
        this.plugWorldInfo = plugWorldInfo;
    }

    public void initBackground(PlugWorldInfo plugWorldInfo, FragmentManager parentFragmentManager, Context parentContext) {
        if (this.plugWorldInfo == null) {
            this.plugWorldInfo = plugWorldInfo;
            this.parentFragmentManager = parentFragmentManager;
            this.parentContext = parentContext;
            init();
            DictManagerControler.getInstance().execute(new SelectWordItemRunnable(this.plugWorldInfo));
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    @Override
    public void onViewCreated(View root, @Nullable Bundle savedInstanceState) {

        horizontalItemTab = (LocalHorizontalItemTab) root.findViewById(R.id.fragment_word_item_tab);
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) horizontalItemTab.getLayoutParams();
        if (lp != null) {
            contentWidth = contentWidth - (lp.leftMargin + lp.rightMargin);
        }
        horizontalItemTab.setAdapter(new HorizontalItemTabAdapter());
        customViewPager = (CustomViewPager) root.findViewById(R.id.fragment_word_item_viewpager);
        worldItemPageAdapter = new WorldItemPageAdapter(getChildFragmentManager());
        customViewPager.setOffscreenPageLimit(3);
        customViewPager.setAdapter(worldItemPageAdapter);
        customViewPager.addPagerChangeListener(horizontalItemTab);
        dictsName = getActivity().getResources().getStringArray(R.array.dictNames);
        root.post(new Runnable() {
            @Override
            public void run() {
                if (data != null) {
                    if (dictWordItemHandler != null) {
                        dictWordItemHandler.sendEmptyMessage(DictWordItemHandler.QEUEST_LAYOUT_VIEW);
                    }
                } else {
                    DictManagerControler.getInstance().execute(new SelectWordItemRunnable(plugWorldInfo));
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        contentWidth = container.getWidth();
        View root = inflater.inflate(R.layout.fragment_word_item, null);
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (tempMessage != null &&
                dictWordItemHandler != null) {
            dictWordItemHandler.sendMessageDelayed(tempMessage, 100);
            tempMessage = null;
        }
    }

    @Override
    public void onDestroyView() {

        plugWorldInfo = null;
        listener = null;
        super.onDestroyView();
    }

    private void init() {
        if (dictWordItemHandler == null) {
            dictWordItemHandler = new DictWordItemHandler(this);
        }
    }

    public Fragment getCurrentWordContentFragment() {
        if (customViewPager != null) {
            try {
                return (Fragment) customViewPager
                        .getAdapter()
                        .instantiateItem(customViewPager,
                                customViewPager.getCurrentItem());
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public void setViewPagerEnAble(boolean isEnable) {
        if (customViewPager != null) {
            customViewPager.setScrollAble(isEnable);
        }
    }

    @Override
    public int setEnlargeTextSize() {
        int textSize = 0;
        Fragment wordContentFragment = getCurrentWordContentFragment();
        if (wordContentFragment instanceof DictWordContentFragment) {
            ((DictWordContentFragment) wordContentFragment).enlargeTextSize();
            textSize = ((DictWordContentFragment) wordContentFragment).getTextSize();
        } else if (wordContentFragment instanceof DictAnimationWordContentFragment) {
            ((DictAnimationWordContentFragment) wordContentFragment).enlargeTextSize();
            textSize = ((DictAnimationWordContentFragment) wordContentFragment).getTextSize();
        } else if (wordContentFragment instanceof DictStrokeWordFragment) {
            ((DictStrokeWordFragment) wordContentFragment).enlargeTextSize();
            textSize = ((DictStrokeWordFragment) wordContentFragment).getTextSize();
        }
        return textSize;
    }

    @Override
    public int setReduceTextSize() {
        int textSize = 0;
        Fragment wordContentFragment = getCurrentWordContentFragment();
        if (wordContentFragment instanceof DictWordContentFragment) {
            ((DictWordContentFragment) wordContentFragment).reduceTextSize();
            textSize = ((DictWordContentFragment) wordContentFragment).getTextSize();
        } else if (wordContentFragment instanceof DictAnimationWordContentFragment) {
            ((DictAnimationWordContentFragment) wordContentFragment).reduceTextSize();
            textSize = ((DictAnimationWordContentFragment) wordContentFragment).getTextSize();
        } else if (wordContentFragment instanceof DictStrokeWordFragment) {
            ((DictStrokeWordFragment) wordContentFragment).reduceTextSize();
            textSize = ((DictStrokeWordFragment) wordContentFragment).getTextSize();
        }
        return textSize;
    }

    @Override
    public boolean setSelectTextState() {
        boolean isSelectedText = false;
        Fragment wordContentFragment = getCurrentWordContentFragment();
        if (wordContentFragment instanceof DictWordContentFragment) {
            DictWordContentFragment dictWordContentFragment = (DictWordContentFragment) wordContentFragment;
            isSelectedText = !dictWordContentFragment.isSelectedText();
            dictWordContentFragment.setSelectedText(isSelectedText);
//            setViewPagerEnAble(!isSelectedText);
        } else if (wordContentFragment instanceof DictAnimationWordContentFragment) {
            DictAnimationWordContentFragment dictAnimationWordContentFragment = (DictAnimationWordContentFragment) wordContentFragment;
            isSelectedText = !dictAnimationWordContentFragment.isSelectedText();
            dictAnimationWordContentFragment.setSelectedText(isSelectedText);
//            setViewPagerEnAble(!isSelectedText);
        } else if (wordContentFragment instanceof DictStrokeWordFragment) {
            DictStrokeWordFragment dictStrokeWordFragment = (DictStrokeWordFragment) wordContentFragment;
            isSelectedText = !dictStrokeWordFragment.isSelectedText();
            dictStrokeWordFragment.setSelectedText(isSelectedText);
//            setViewPagerEnAble(!isSelectedText);
        }
        return isSelectedText;
    }

    @Override
    public boolean getSelectTextState() {
        boolean isSelectedText = false;
        Fragment wordContentFragment = getCurrentWordContentFragment();
        if (wordContentFragment instanceof DictWordContentFragment) {
            DictWordContentFragment dictWordContentFragment = (DictWordContentFragment) wordContentFragment;
            isSelectedText = dictWordContentFragment.isSelectedText();
        } else if (wordContentFragment instanceof DictAnimationWordContentFragment) {
            DictAnimationWordContentFragment dictAnimationWordContentFragment = (DictAnimationWordContentFragment) wordContentFragment;
            isSelectedText = dictAnimationWordContentFragment.isSelectedText();
        } else if (wordContentFragment instanceof DictStrokeWordFragment) {
            DictStrokeWordFragment dictStrokeWordFragment = (DictStrokeWordFragment) wordContentFragment;
            isSelectedText = dictStrokeWordFragment.isSelectedText();
        }
        return isSelectedText;
    }

    @Override
    public int getTextSize() {
        int textSize = 0;
        Fragment wordContentFragment = getCurrentWordContentFragment();
        if (wordContentFragment instanceof DictWordContentFragment) {
            textSize = ((DictWordContentFragment) wordContentFragment).getTextSize();
        } else if (wordContentFragment instanceof DictAnimationWordContentFragment) {
            textSize = ((DictAnimationWordContentFragment) wordContentFragment).getTextSize();
        } else if (wordContentFragment instanceof DictStrokeWordFragment) {
            textSize = ((DictStrokeWordFragment) wordContentFragment).getTextSize();
        }
        return textSize;
    }

    private class DictWordItemHandler extends BaseHandler<DictWordItemFragment> {

        final static int SELECT_WORD_ITEM_SUCCESS = 0x01;
        final static int SELECT_WORD_ITEM_FAIL = SELECT_WORD_ITEM_SUCCESS + 1;
        final static int QEUEST_LAYOUT_VIEW = SELECT_WORD_ITEM_FAIL + 1;

        public DictWordItemHandler(DictWordItemFragment dictWordItemFragment) {
            super(dictWordItemFragment);
        }

        @Override
        public void handleMessage(final Message msg) {

            final DictWordItemFragment instance = getReference().get();
            if (instance == null) {
                return;
            }

            switch (msg.what) {

                case SELECT_WORD_ITEM_SUCCESS:
                    if (msg.obj instanceof List) {
                        if (msg.obj instanceof List) {

                            if (instance.getFragmentManager()!= null) {
                                sendMessages(QEUEST_LAYOUT_VIEW, msg.obj, 0, 0, 0);
                            } else if (instance.parentFragmentManager != null){
                                /*if (instance.parentFragmentManager.findFragmentByTag(DictHotWord2Fragment.class.getSimpleName()) != null
                                        || instance.parentFragmentManager.findFragmentByTag(DictRecordWordFragment.class.getSimpleName()) != null
                                        || instance.parentFragmentManager.findFragmentByTag(DictWordItemFragment.class.getSimpleName()) != null) {
                                    instance.parentFragmentManager.popBackStackImmediate();
                                }*/
                                /*instance.data = (List<PlugWorldInfo>) msg.obj;
                                DictManagerControler.replaceFragment(instance.parentFragmentManager.beginTransaction(),
                                        R.id.fragment_main_item_content, DictWordItemFragment.class.getSimpleName(),
                                        DictWordItemFragment.class.getSimpleName(), instance);*/
                                if (listener != null) {
                                    instance.data = (List<PlugWorldInfo>) msg.obj;
                                    listener.loadSuccess(DictWordItemFragment.this);
                                }
                            }
                            break;
                        }
                    }
                    /*if (instance.getChildFragmentManager() != null) {
                        getChildFragmentManager().popBackStackImmediate();
                    }*/
                    break;

                case SELECT_WORD_ITEM_FAIL:
                    if (listener != null) {
                        listener.loadFailed();
                    }
                    if (getReference().get().getFragmentManager() != null) {
                        getFragmentManager().popBackStackImmediate();
                    }
                    break;

                case QEUEST_LAYOUT_VIEW:
                    if (!isResumed()) {
                        tempMessage = Message.obtain(msg);
                        return;
                    }
                    final Object tempData = msg.obj;

                    getReference().get().horizontalItemTab.post(new Runnable() {
                        @Override
                        public void run() {
                            if (instance != null) {
                                if (tempData instanceof List) {
                                    instance.data = (List<PlugWorldInfo>) tempData;
                                }
//                                horizontalItemTab.setTabWidth(contentWidth / (instance.data.size()));
                                int itemTabContentWidth = getReference().get().data.size() * contentWidth / 4;
                                getReference().get().horizontalItemTab.setMinimumWidth(itemTabContentWidth);
                                getReference().get().horizontalItemTab.requestLayout();

                                try {
                                    getReference().get().horizontalItemTab.getAdpater().notifyDataAll();
                                    getReference().get().customViewPager.getAdapter().notifyDataSetChanged();
                                } catch (IllegalArgumentException e) {
                                    e.printStackTrace();
                                    getFragmentManager().popBackStackImmediate();
                                }
                            }
                        }
                    });
                    /*if (dictWordControlViewGroup.getVisibility() != View.VISIBLE) {
                        dictWordControlViewGroup.setVisibility(View.VISIBLE);
                    }*/
                    break;

            }

        }
    }

    private class HorizontalItemTabAdapter extends ItemTabAdapter {

        boolean isScrolling = false;
        int tabIndex = -1;
        List<Integer> cacheIndex;

        HorizontalItemTabAdapter() {
            cacheIndex = new ArrayList<Integer>();
        }

        @Override
        public void onScroll(int position, float positionOffset) {
            if (horizontalItemTab != null) {
                horizontalItemTab.invalidate();
            }
            if (positionOffset == 0.0f) {
                isScrolling = false;
                if (horizontalItemTab != null) {
                    View lastItemView = horizontalItemTab.getItemView(position - 1);
                    if (lastItemView != null) {
//                        ((TextView) lastItemView.findViewById(R.id.item_dict_name_text)).setTextColor(getResources().getColor(R.color.white));
//                        ((TextView) lastItemView.findViewById(R.id.item_dict_name_text)).setSelected(false);
                    }
                    View currentItemView = horizontalItemTab.getItemView(position);
                    if (currentItemView != null) {
//                        ((TextView) currentItemView.findViewById(R.id.item_dict_name_text)).setTextColor(getResources().getColor(R.color.dark_yellow_1));
//                        ((TextView) currentItemView.findViewById(R.id.item_dict_name_text)).setSelected(true);
                    }
                    View nextItemView = horizontalItemTab.getItemView(position + 1);
                    if (nextItemView != null) {
//                        ((TextView) nextItemView.findViewById(R.id.item_dict_name_text)).setTextColor(getResources().getColor(R.color.white));
//                        ((TextView) nextItemView.findViewById(R.id.item_dict_name_text)).setSelected(false);
                    }
                }
            }
            if (tabIndex == -1 && cacheIndex.indexOf(position) == -1) {
                MobileProviderManager.getInstance().addWordRecord(getActivity().getContentResolver(),
                        data.get(position).dictID + "",
                        data.get(position).world,
                        data.get(position).index);
                cacheIndex.add(position);
            }
            tabIndex = position;
        }

        @Override
        public void onScrolledStateChange(int state) {
            switch (state) {
                case ViewPager.SCROLL_STATE_IDLE:
                    isScrolling = false;
                    for (int i=0;i<getCount();i++) {
                        View view = horizontalItemTab.getItemView(i);
                        if (tabIndex != i) {
                            if (view != null) {
//                                ((TextView) view.findViewById(R.id.item_dict_name_text)).setTextColor(getResources().getColor(R.color.light_grey_5));
//                                ((TextView) view.findViewById(R.id.item_dict_name_text)).setSelected(false);
                            }
                        } else {
//                            ((TextView) view.findViewById(R.id.item_dict_name_text)).setTextColor(getResources().getColor(R.color.dark_yellow_1));
//                            ((TextView) view.findViewById(R.id.item_dict_name_text)).setSelected(true);
                        }

                    }
                    if (cacheIndex.indexOf(tabIndex) == -1) {
                        MobileProviderManager.getInstance().addWordRecord(getActivity().getContentResolver(),
                                data.get(tabIndex).dictID + "",
                                data.get(tabIndex).world,
                                data.get(tabIndex).index);
                        cacheIndex.add(tabIndex);
                    } else {
                        MobileProviderManager.getInstance().addWordLately(getActivity().getContentResolver(),
                                data.get(tabIndex).dictID + "",
                                data.get(tabIndex).world,
                                data.get(tabIndex).index);
                    }
                    break;
            }
        }

        @Override
        public View getView(View view, ViewGroup viewGroup, final int position) {
            if (view == null) {
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_dict_tab_name, null);
                view.setMinimumWidth(contentWidth / data.size());
                view.setMinimumHeight(horizontalItemTab.getHeight());
            }
            if (data != null && data.size() > position && position != -1) {
                ((TextView) view.findViewById(R.id.item_dict_name_text)).setText(dictsName[data.get(position).dictID]);

            }
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    customViewPager.setCurrentItem(position, true);
                    DictManagerControler.getInstance().hideInput(getActivity());
                }
            });
            /*if (cacheIndex.indexOf(tabIndex) == -1 && getActivity() != null) {
                MobileProviderManager.getInstance().addWordRecord(getActivity().getContentResolver(),
                        data.get(tabIndex).dictID + "",
                        data.get(tabIndex).world,
                        data.get(tabIndex).index);
                cacheIndex.add(tabIndex);
            }*/
            return view;
        }

        @Override
        public int getCount() {
            if (data != null) {
                return data.size();
            }
            return 0;
        }
    }

    private class WorldItemPageAdapter extends FragmentStatePagerAdapter {

        private final FragmentManager mFragmentManager;
        private ArrayList<Fragment> mFragments = new ArrayList<Fragment>();

        public WorldItemPageAdapter(FragmentManager fm) {
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
            if (data != null && data.size() > position) {
                if (data.get(position).dictID == DictIOFile.ID_DMDICT) {
                    DictAnimationWordContentFragment dictAnimationWordContentFragment = new DictAnimationWordContentFragment();
                    dictAnimationWordContentFragment.setWord(data.get(position));
                    return dictAnimationWordContentFragment;
                } else if (data.get(position).dictID == DictIOFile.ID_BHDICT) {
                    DictStrokeWordFragment dictStrokeWordFragment = new DictStrokeWordFragment();
                    dictStrokeWordFragment.setWord(data.get(position));
                    return dictStrokeWordFragment;
                } else {
                    DictWordContentFragment dictWordContentFragment = new DictWordContentFragment();
                    dictWordContentFragment.setWord(data.get(position));
                    return dictWordContentFragment;
                }
            }
            return null;
        }

        @Override
        public int getCount() {
            if (data != null) {
                return data.size();
            }
            return 0;
        }
    }

    private class SelectWordItemRunnable implements Runnable {

        PlugWorldInfo plugWorldInfo;
        boolean isStop = false;

        SelectWordItemRunnable(PlugWorldInfo plugWorldInfo) {
            this.plugWorldInfo = plugWorldInfo;
        }

        @Override
        public void run() {
            Context mContext = getActivity();
            if (mContext == null) {
                mContext = parentContext;
            }
            DictWordSearch dictWordSearch = new DictWordSearch();
            int searchDictID = plugWorldInfo.dictID;
            String searchWorld = plugWorldInfo.world;

            List<PlugWorldInfo> data = new ArrayList<PlugWorldInfo>();
            if (searchDictID != -1) {
                if (searchDictID== DictIOFile.ID_BHDICT) {
                    if (dictWordSearch.open(DictIOFile.ID_BHDICT, mContext, null)) {

                        List<HanziInfo> hanziKeyList = dictWordSearch.getHanziKeyList(searchWorld.charAt(0) + "");
                        if (hanziKeyList != null && hanziKeyList.size() > 0) {
                            plugWorldInfo.polyphone = hanziKeyList;
                            data.add(plugWorldInfo);
                        }
                        dictWordSearch.close();
                    }
                } else {
                    data.add(plugWorldInfo);
                }
            }

            int type = -1;
            if (searchDictID != -1) {
                type = getDictType(searchDictID);
            } else {
                type = DictWordSearch.getDictType(searchWorld);
            }

            if (type == -1) {
                if (dictWordItemHandler != null) {
                    dictWordItemHandler.sendMessages(DictWordItemHandler.SELECT_WORD_ITEM_FAIL, null, 0, 0, 0);
                }
                return;
            }

            int dicts[] = null;
            String filterSearchWord = null;
            if (type == DictWordSearch.DICT_TYPE_CHI) {
                dicts = DICT_CHINESE_LIST;
                if (searchWorld != null && data.size() == 0) {
                    filterSearchWord = DictIOFile.getFilterChineseWord(searchWorld);
                }
            } else if (type == DictWordSearch.DICT_TYPE_ENG) {
                dicts = DICT_ENGLISH_LIST;
            }
            if (dicts != null) {
                for (int id : dicts) {
                    if (dictWordSearch != null && id != searchDictID && !isStop) {
                        if (dictWordSearch.open(id, mContext, null)) {
                            if (id != DictIOFile.ID_BHDICT) {
                                int index = dictWordSearch.getSearchKeyIndexByAccurate(searchWorld);
                                if (index >= 0 && dictWordSearch.getKeyWord(index) != null) {
                                    PlugWorldInfo plugWorldInfo = new PlugWorldInfo();
                                    plugWorldInfo.dictID = id;
                                    plugWorldInfo.world = dictWordSearch.getKeyWord(index);
                                    plugWorldInfo.index = index;
                                    plugWorldInfo.order = id;
                                    if (id != DictIOFile.ID_DMDICT) {
                                        byte[] bytes = dictWordSearch.getExplainByte(index);
                                        int start = dictWordSearch.getExplainByteStart(bytes);
                                        for (int i = 0;i < start;i++) {
                                            bytes[i] = 0;
                                        }
                                        plugWorldInfo.data = bytes;
                                    }
                                    data.add(plugWorldInfo);
                                }
                            } else {
                                List<HanziInfo> hanziKeyList = dictWordSearch.getHanziKeyList(searchWorld.charAt(0) + "");
                                if (hanziKeyList != null && hanziKeyList.size() > 0) {
                                    PlugWorldInfo plugWorldInfo = new PlugWorldInfo();
                                    plugWorldInfo.dictID = id;
                                    plugWorldInfo.world = hanziKeyList.get(0).word;
//                                    plugWorldInfo.index = hanziKeyList.get(0).index;
                                    plugWorldInfo.order = id;
                                    plugWorldInfo.polyphone = hanziKeyList;

                                    data.add(plugWorldInfo);
                                }
                            }
                        }
                        dictWordSearch.close();
                    }
                }

                if (data != null && data.size() > 0) {
                    if (filterSearchWord != null) {
                        int MAX_ORDER = -40;
                        int MIN_ORDER = -1;
                        for (PlugWorldInfo plugWorldInfo : data) {
                            if (plugWorldInfo.world != null) {
                                if(plugWorldInfo.world.contentEquals(filterSearchWord)) {
                                    if (plugWorldInfo.dictID == DictIOFile.ID_BHDICT) {
                                        plugWorldInfo.order = MAX_ORDER;
                                    } else {
                                        plugWorldInfo.order = MAX_ORDER - 1;
                                    }
                                } else if (plugWorldInfo.dictID == DictIOFile.ID_BHDICT) {
                                    plugWorldInfo.order = MAX_ORDER - 2 - 101;
                                } else if (plugWorldInfo.world.contains(filterSearchWord)) {
                                    plugWorldInfo.order = MAX_ORDER - 2;
                                } else if (filterSearchWord.contains(plugWorldInfo.world)) {
                                    plugWorldInfo.order = MAX_ORDER - 2 - (100 - plugWorldInfo.world.length());
                                } else {
                                    plugWorldInfo.order = MAX_ORDER - 2 - 100;
                                }

                            }
                        }
                        Collections.sort(data);
                    }

                }
            }
            if (!isStop) {
                if (data != null && data.size() > 0) {
                    if (dictWordItemHandler != null) {
                        dictWordItemHandler.sendMessages(DictWordItemHandler.SELECT_WORD_ITEM_SUCCESS, data, 0, 0, 0);
                        return;
                    }
                }
            }
            if (dictWordItemHandler != null) {
                dictWordItemHandler.sendMessages(DictWordItemHandler.SELECT_WORD_ITEM_FAIL, null, 0, 0, 0);
            }
        }

        int getDictType(int dictID) {
            int dictType = -1;
            for (int et : DICT_ENGLISH_LIST) {
                if (plugWorldInfo.dictID == et) {
                    dictType = DictWordSearch.DICT_TYPE_ENG;
                    break;
                }
            }
            if (dictType == -1) {
                for (int et : DICT_CHINESE_LIST) {
                    if (plugWorldInfo.dictID == et) {
                        dictType = DictWordSearch.DICT_TYPE_CHI;
                        break;
                    }
                }
            }
            return dictType;
        }

    }

    public interface OnDictWordItemFragmentListener {
        void loadSuccess(DictWordItemFragment dictWordItemFragment);
        void loadFailed();
    }
}
