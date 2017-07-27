package com.readboy.offline.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.readboy.Dictionary.R;

import java.util.List;

/**
 * Editor: sgc
 * Date: 2015/01/23
 */
public class BiHuaListAdapter extends BaseAdapter {

    private List<BiHuaWordInfo> mItems;

    private int selectPosition = 0;

    public void setListItems(List<BiHuaWordInfo> mItems) {
        selectPosition = 0;
        this.mItems = mItems;
    }

    public BiHuaWordInfo getSelectItem() {
        if (mItems != null && mItems.size() > selectPosition) {
            return mItems.get(selectPosition);
        }
        return null;
    }

    public void setSelection(int position) {
        selectPosition = position;
    }

    public int getSelection() {
        return selectPosition;
    }

    @Override
    public int getCount() {
        if (mItems != null) {
            return mItems.size();
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
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = parent.inflate(parent.getContext(), R.layout.dict_key_item, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)convertView.getTag();
        }

        if (selectPosition == position) {
            viewHolder.item.setSelected(true);
            convertView.setBackgroundResource(R.drawable.list_selector_fs);
        } else {
            viewHolder.item.setSelected(false);
            convertView.setBackgroundResource(R.drawable.unselect);
        }

        if (mItems != null && mItems.size() > position) {
            viewHolder.item.setText(mItems.get(position).text);
        }

        return convertView;
    }


    public class BiHuaWordInfo {
        public String text;
        public int index;
    }

    private class ViewHolder {

        public TextView item;

        public ViewHolder(View view) {
            item = (TextView)view.findViewById(R.id.dict_key_text);
        }
    }

}
