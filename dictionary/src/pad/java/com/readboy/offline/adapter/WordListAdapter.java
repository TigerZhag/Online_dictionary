package com.readboy.offline.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.readboy.Dictionary.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2014/7/26.
 */
public class WordListAdapter extends BaseDictListAdapter {

        private Context mContext;
        private List<String> mItems = new ArrayList<String>();
        private int selectPosition = 0;
        private int mStartId;

        public WordListAdapter(Context context){
            mContext = context;
        }
        public int getCount() {
            // TODO Auto-generated method stub
            return mItems.size();
        }
        public void setListItems(List<String> items) {
            mItems.clear();
            selectPosition = 0;
            if(items != null && items.size() > 0) {
                mItems.addAll(items);
            }
        }

        public void addListItems(List<String> items, boolean behind) {
            if(behind) {
                mItems.addAll(items);
            } else {
                mItems.addAll(0,items);
            }
        }
        public void setListItems(List<String> items, int startId) {
            mStartId = startId;
            mItems.clear();
            if(items != null && items.size() > 0) {
                mItems.addAll(items);
            }
        }

        public String getWordItem() {
            if(getItem(selectPosition) == null) {
                return null;
            }
            return getItem(selectPosition).toString();
        }

        public void addListItems(List<String> items, int start) {
            if(mStartId < start) {
                mItems.addAll(items);
            } else {
                mStartId = start;
                mItems.addAll(0,items);
            }
        }

        public int getStartId() {
            return mStartId;
        }

        public Object getItem(int p) {
            // TODO Auto-generated method stub
            int size = mItems.size();
            if(p >= size) {
                return null;
            }
            return mItems.get(p);
        }
        public long getItemId(int p) {
            // TODO Auto-generated method stub
            return p;
        }

        public void setSelection(int position) {
            selectPosition = position;
        }

        public int getSelection() {
            return selectPosition;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null){
                LayoutInflater inflate = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflate.inflate(R.layout.dict_key_item, null);
            }
            ((TextView)convertView.findViewById(R.id.dict_key_text)).setText(mItems.get(position));

            if(position == selectPosition) {
                ((TextView)convertView.findViewById(R.id.dict_key_text)).setTextColor(Color.WHITE);
                convertView.findViewById(R.id.dict_key_text).setSelected(true);
                convertView.setBackgroundResource(R.drawable.list_selector_fs);
            } else {
                ((TextView)convertView.findViewById(R.id.dict_key_text)).setTextColor(Color.BLACK);
                convertView.findViewById(R.id.dict_key_text).setSelected(false);
                convertView.setBackgroundResource(R.drawable.unselect);
            }
            notifyDataSetChanged();
            return convertView;
        }

        public void onDistory() {
            mStartId = 0;
            selectPosition = 0;
            mItems.clear();

        }


}
