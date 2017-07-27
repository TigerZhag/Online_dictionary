package com.readboy.offline.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

public abstract class BaseDictListAdapter extends BaseAdapter{

	public abstract int getStartId();
	public abstract void setSelection(int selectIndex);
	public abstract int getSelection();
	public abstract void setListItems(List<String> list, int startIndex);
	public abstract void addListItems(List<String> list, int startIndex);
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		return null;
	}

	

}
