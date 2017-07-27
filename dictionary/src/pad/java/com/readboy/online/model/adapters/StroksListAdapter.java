package com.readboy.online.model.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.readboy.Dictionary.R;
import com.readboy.online.model.data.PinYinAndZi;
import com.readboy.online.model.data.StroksResultData;
import com.readboy.online.view.FixGridLayout;

import java.util.List;

public class StroksListAdapter extends BaseAdapter{

    private static final int GROUP = 0;
    private static final int ITEM = 1;
    private Context context;
    private List<StroksResultData> list;

    public StroksListAdapter(Context context, List<StroksResultData> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public StroksResultData getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        if (list == null) {
            return ITEM;
        }
        if(list.get(position).isGroup())
            return GROUP;
        else
            return ITEM;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(context);
        StroksResultData bean = list.get(position);
        PinYinAndZi point = bean.getPoint();
        switch (getItemViewType(position)){
            case GROUP:
                if(convertView != null){
                    viewHolder = (ViewHolder) convertView.getTag();
                }
                if(convertView == null || !viewHolder.isGroup()){
                    viewHolder = new ViewHolder();
                    convertView = inflater.inflate(R.layout.stroks_group_list_adapter,null);
                    initGroupViewHolder(viewHolder,convertView);
                }
                viewHolder.bushou.setText(bean.getBushou());
                break;
            case ITEM:
                if(convertView != null){
                    viewHolder = (ViewHolder) convertView.getTag();
                }
                if(convertView == null || viewHolder.isGroup()){
                    viewHolder = new ViewHolder();
                    convertView = inflater.inflate(R.layout.stroks_item_list_adapter, null);
                    initItemViewHolder(viewHolder,convertView);
                }
                TextView tvPinYin = new TextView(context);
                viewHolder.pinyin = tvPinYin;

                viewHolder.pinyin.setText(point.getPinyin());
//                viewHolder.zi.setText(point.getZi());
                viewHolder.llItem.addView(viewHolder.pinyin);
//                viewHolder.llItem.addView(viewHolder.zi);
                break;
        }
        return convertView;
    }

    class ViewHolder{
        public LinearLayout llGroup;
        public FixGridLayout llItem;
        public TextView bushou;
        public TextView pinyin;
//        public TextView zi;
        public boolean isGroup;
        public boolean isGroup() {  return isGroup; }
        public void setGroup(boolean group) {   isGroup = group;    }
    }

    private void initItemViewHolder(ViewHolder viewHolder,View convertView){
        viewHolder.llItem = (FixGridLayout) convertView.findViewById(R.id.ll_item);
        viewHolder.llItem.setmCellHeight(50);
        viewHolder.llItem.setmCellWidth(100);
//        TextView tvPinYin = new TextView(context);
//        viewHolder.pinyin = tvPinYin;
//        TextView tvZi = new TextView(context);
//        viewHolder.zi = tvZi;
        viewHolder.setGroup(false);
        convertView.setTag(viewHolder);
    }

    private void initGroupViewHolder(ViewHolder viewHolder,View convertView){
        viewHolder.llGroup = (LinearLayout) convertView.findViewById(R.id.ll_group);
        viewHolder.bushou = (TextView) convertView.findViewById(R.id.tv_bushou);
        viewHolder.setGroup(true);
        convertView.setTag(viewHolder);
    }
}
