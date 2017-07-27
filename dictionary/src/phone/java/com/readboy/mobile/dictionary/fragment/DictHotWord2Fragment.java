package com.readboy.mobile.dictionary.fragment;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.readboy.Dictionary.R;
import com.readboy.library.io.DictIOFile;
import com.readboy.library.utils.PhoneticUtils;
import com.readboy.mobile.dictionary.control.DictManagerControler;
import com.readboy.mobile.dictionary.provider.DictHotWordTableInfo;
import com.readboy.mobile.dictionary.provider.MobileProviderManager;
import com.readboy.mobile.dictionary.utils.BaseHandler;

import java.util.List;

/**
 * Created by Senny on 2015/11/3.
 */
public class DictHotWord2Fragment extends Fragment implements View.OnClickListener{

    private HotWordHandler hotWordHandler;
    private FragmentManager fragmentManager;
    private Context parentContext;
    private GridView gridView;
    private List<DictHotWordTableInfo> data;
    private OnHotWord2SearchListener listener;

    public void setOnHotWord2SearchListener(OnHotWord2SearchListener listener) {
        this.listener = listener;
    }

    public void startSearchHotWord(ContentResolver cr, FragmentManager fragmentManager, Context parentContext) {
        this.fragmentManager = fragmentManager;
        this.parentContext = parentContext;
        init();
        SearchHotWordRunnable searchHotWordRunnable = new SearchHotWordRunnable(cr);
        DictManagerControler.getInstance().execute(searchHotWordRunnable);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_hot_word_2, null);
        gridView = (GridView) root.findViewById(R.id.fragment_hot_word_2_gridview);
        gridView.setAdapter(new GridViewAdapter());
        gridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        return root;
    }

    @Override
    public void onDestroyView() {
        listener = null;
        super.onDestroyView();
    }

    private void init() {
        hotWordHandler = new HotWordHandler(this);

    }

    @Override
    public void onClick(View v) {
        /*if (v.getTag() instanceof GridViewAdapter.ViewHolder) {
            GridViewAdapter.ViewHolder viewHolder = (GridViewAdapter.ViewHolder) v.getTag();
            if (data != null && data.size() > viewHolder.index) {
                if (listener != null) {
                    DictHotWordTableInfo dictHotWordTableInfo = data.get(viewHolder.index);
                    listener.loadWord(Integer.valueOf(dictHotWordTableInfo.dictType), dictHotWordTableInfo.dictWord, Integer.valueOf(dictHotWordTableInfo.dictWordIndex));
                }
            }
        }*/
        if (v.getTag() instanceof Integer) {
            int index = (Integer) v.getTag();
            if (data != null && data.size() > index) {
                if (listener != null) {
                    DictHotWordTableInfo dictHotWordTableInfo = data.get(index);
                    listener.loadWord(Integer.valueOf(dictHotWordTableInfo.dictType), dictHotWordTableInfo.dictWord, Integer.valueOf(dictHotWordTableInfo.dictWordIndex));
                }
            }
        }
    }

    private class GridViewAdapter extends BaseAdapter implements View.OnTouchListener{

        @Override
        public int getCount() {
            if (data != null) {
                return data.size();
            }
            return 0;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {

            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder ;
            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.btn_grid_view_item, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
                viewHolder.word.setOnClickListener(DictHotWord2Fragment.this);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            if (data != null && data.size() > position) {
                DictHotWordTableInfo dictHotWordTableInfo = data.get(position);
                viewHolder.index = position;
                viewHolder.word.setText(PhoneticUtils.checkString(dictHotWordTableInfo.dictWord));
                viewHolder.word.setTag(position);
                /*if (dictHotWordTableInfo.dictType.contentEquals(DictIOFile.ID_BHDICT+"")) {
                    viewHolder.background.setBackgroundResource(R.drawable.bg_dict_bh_item_color_normal);
                } else if (dictHotWordTableInfo.dictType.contentEquals(DictIOFile.ID_LWDICT+"")) {
                    viewHolder.background.setBackgroundResource(R.drawable.bg_dict_lw_item_color_normal);
                } else if (dictHotWordTableInfo.dictType.contentEquals(DictIOFile.ID_DMDICT+"")) {
                    viewHolder.background.setBackgroundResource(R.drawable.bg_dict_dm_item_color_normal);
                } else if (dictHotWordTableInfo.dictType.contentEquals(DictIOFile.ID_YHDICT+"")) {
                    viewHolder.background.setBackgroundResource(R.drawable.bg_dict_yh_item_color_normal);
                } else if (dictHotWordTableInfo.dictType.contentEquals(DictIOFile.ID_CYDICT+"")) {
                    viewHolder.background.setBackgroundResource(R.drawable.bg_dict_cy_item_color_normal);
                } else if (dictHotWordTableInfo.dictType.contentEquals(DictIOFile.ID_HYDICT+"")) {
                    viewHolder.background.setBackgroundResource(R.drawable.bg_dict_hy_item_color_normal);
                } else if (dictHotWordTableInfo.dictType.contentEquals(DictIOFile.ID_XDDICT+"")) {
                    viewHolder.background.setBackgroundResource(R.drawable.bg_dict_xd_item_color_normal);
                }*/
            }
            return convertView;
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            return false;
        }

        private class ViewHolder {

            TextView word;
            View background;
            int index;

            ViewHolder(View v) {
                word = (TextView) v.findViewById(R.id.btn_grid_view_item_text);
//                background = v.findViewById(R.id.btn_grid_view_item_dict_bg);
            }

        }
    }

    private class HotWordHandler extends BaseHandler<DictHotWord2Fragment> {

        final static int LOAD_DATA_SUCCESS = 0x01;
        final static int LOAD_DATA_FAILED = LOAD_DATA_SUCCESS + 1;

        public HotWordHandler(DictHotWord2Fragment dictHotWord2Fragment) {
            super(dictHotWord2Fragment);
        }

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {

                case LOAD_DATA_SUCCESS:

                    data = (List<DictHotWordTableInfo>) msg.obj;
                    if (data != null && data.size() > 0) {
                        if (listener != null) {
                            listener.loadSuccess();
                        }
                        /*if (fragmentManager.findFragmentByTag(DictHotWord2Fragment
                                .class.getSimpleName()) != null
                                || fragmentManager.findFragmentByTag(DictWordItemFragment.class.getSimpleName()) != null) {
                            fragmentManager.popBackStackImmediate();
                        }*/
                        DictManagerControler.replaceFragmentWithAnim(fragmentManager.beginTransaction(),
                                R.id.fragment_main_item_content,
                                DictHotWord2Fragment.class.getSimpleName(),
                                DictHotWord2Fragment.class.getSimpleName(),
                                DictHotWord2Fragment.this, R.anim.right_in, R.anim.left_out, 0, 0);
                    } else {
                        if (listener != null) {
                            listener.loadFailed();
                        }
                    }

                    break;

                case LOAD_DATA_FAILED:
                    if (listener != null) {
                        listener.loadFailed();
                    }
                    break;

            }

        }
    }

    private class SearchHotWordRunnable implements Runnable {

        ContentResolver cr;

        SearchHotWordRunnable(ContentResolver cr) {
            this.cr = cr;
        }

        @Override
        public void run() {
            List<DictHotWordTableInfo> data = MobileProviderManager.getInstance().getHotWordRecordList(cr);
            if (data != null && data.size() > 0) {
                if (hotWordHandler != null) {
                    hotWordHandler.sendMessages(HotWordHandler.LOAD_DATA_SUCCESS, data, 0, 0, 0);
                }
            } else {
                if (hotWordHandler != null) {
                    hotWordHandler.sendMessages(HotWordHandler.LOAD_DATA_FAILED, null, 0, 0, 0);
                }
            }
        }
    }

    public interface OnHotWord2SearchListener {

        void loadSuccess();
        void loadFailed();
        void loadWord(int dictID, String word, int index);

    }

}

