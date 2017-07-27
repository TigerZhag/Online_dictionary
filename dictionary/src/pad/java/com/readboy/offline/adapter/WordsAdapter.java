package com.readboy.offline.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.readboy.Dictionary.R;
import com.readboy.offline.mode.WordInfo;

import java.util.ArrayList;
import java.util.List;

public class WordsAdapter extends BaseAdapter
{
    private Context mContext;
    private LayoutInflater mInflater;
    private List<WordInfo> mItems;

    private OnDeleteClickListener onDeleteClickListener;

    public WordsAdapter(Context context)
    {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mItems = new ArrayList<WordInfo>();
    }

    public void setOnDeleteClickListener(OnDeleteClickListener onDeleteClickListener) {
        this.onDeleteClickListener = onDeleteClickListener;
    }

    @Override
    public int getCount()
    {
        return mItems.size();
    }

    @Override
    public Object getItem(int position)
    {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return mItems.get(position).wordIndex;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        ViewHolder holder;
        if (convertView == null)
        {
            convertView = mInflater.inflate(R.layout.item_list_word, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.delete.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (onDeleteClickListener != null) {
                    onDeleteClickListener.delete(mItems, position);
                }
            }
        });
        holder.wordText.setText(mItems.get(position).word);

        return convertView;
    }

    public void refresh(ArrayList<WordInfo> items)
    {
        mItems.clear();
        if (items != null)
        {
            mItems.addAll(items);
        }
        notifyDataSetChanged();
    }

    private class ViewHolder
    {
        public TextView wordText;
        public ImageView delete;

        public ViewHolder(View rootView)
        {
            wordText = (TextView) rootView.findViewById(R.id.word);
            delete = (ImageView) rootView.findViewById(R.id.delete);
        }
    }

    public interface OnDeleteClickListener {
        public void delete(List<WordInfo> data, int posititon);
    }
}
