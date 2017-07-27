package com.readboy.mobile.dictionary.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.HeaderViewListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.readboy.Dictionary.R;
import com.readboy.library.provider.info.PlugWorldInfo;
import com.readboy.library.utils.PhoneticUtils;

import java.util.List;

/**
 * Created by Administrator on 15-4-15.
 */
public class DictListPopupWindow {

    private final int MAX_HEIGHT = 200;
    private PopupWindow popupWindow;
    private int itemHeight = 0;

    private OnSelectedItemWordListener onSelectedItemWordListener;

    private View anchor;

    public View getAnchor() {
        return anchor;
    }

    public void setAnchor(View anchor) {
        this.anchor = anchor;
    }

    public OnSelectedItemWordListener getOnSelectedItemWordListener() {
        return onSelectedItemWordListener;
    }

    public void setOnSelectedItemWordListener(OnSelectedItemWordListener onSelectedItemWordListener) {
        this.onSelectedItemWordListener = onSelectedItemWordListener;
    }

    public DictListPopupWindow(Context context, int width, int height) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_popu_spanel, null);
        ListView listView = (ListView)view.findViewById(R.id.popu_spanel_list);
        listView.setSelector(new ColorDrawable(Color.GRAY));
        View footerView = new View(context);
        listView.addFooterView(footerView, null, true);
        popupWindow = new PopupWindow(view, width, height, false);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable(context.getResources(), (Bitmap) null)/*new ColorDrawable(Color.WHITE)*/);
//        popupWindow.setAnimationStyle(R.style.popwin_anim_style);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                if (onSelectedItemWordListener != null) {
                    onSelectedItemWordListener.onDismiss();
                }
            }
        });
        popupWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
        popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        /*view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (popupWindow != null && popupWindow.isShowing()) {
                    popupWindow.dismiss();
                }
            }
        });*/
    }

    /*public void showDropWindow(List<PlugWorldInfo> data) {
        if (popupWindow != null && data != null && data.size() > 0) {
            ListView listView = (ListView)popupWindow.getContentView().findViewById(R.id.popu_spanel_list);
            if (listView != null) {
                ItemAdapter itemAdapter = (ItemAdapter)listView.getAdapter();
                if (itemAdapter != null) {
                    itemAdapter.clear();
                }
                itemAdapter = new ItemAdapter();
                itemAdapter.setData(data);
                listView.setAdapter(itemAdapter);
                if (!popupWindow.isShowing() && getAnchor() != null) {
                    resetWindowHeight(popupWindow.getContentView().getContext(), data.size());
                    int p[] = new int[2];
                    getAnchor().getLocationOnScreen(p);
                    popupWindow.showAtLocation(getAnchor(), Gravity.NO_GRAVITY, p[0]-15, p[1]+anchor.getHeight()+15);
                }
            }
        }

    }*/

    public void showDropWindow(List<PlugWorldInfo> data, int offsetTop) {
        if (popupWindow != null && data != null && data.size() > 0) {
            ListView listView = (ListView)popupWindow.getContentView().findViewById(R.id.popu_spanel_list);
            if (listView != null) {
                ItemAdapter itemAdapter = null;
                if (listView.getAdapter() instanceof HeaderViewListAdapter) {
                    HeaderViewListAdapter ha = (HeaderViewListAdapter) listView.getAdapter();
                    if (ha != null) {
                        itemAdapter = (ItemAdapter)ha.getWrappedAdapter();
                    }
                } else {
                    itemAdapter = (ItemAdapter) listView.getAdapter();
                }
                if (itemAdapter != null) {
                    itemAdapter.clear();
                }
                itemAdapter = new ItemAdapter();
                itemAdapter.setData(data);
                listView.setAdapter(itemAdapter);
                if (!popupWindow.isShowing() && getAnchor() != null) {
                    resetWindowHeight(popupWindow.getContentView().getContext(), data.size());
                    int p[] = new int[2];
                    getAnchor().getLocationOnScreen(p);
                    popupWindow.showAtLocation(getAnchor(), Gravity.NO_GRAVITY, p[0], p[1]+anchor.getHeight() + offsetTop);
                }

            }
        }

    }

    public void showDropWindow(List<PlugWorldInfo> data, int offsetTop, int maxHeight) {
        if (popupWindow != null && data != null && data.size() > 0) {
            ListView listView = (ListView)popupWindow.getContentView().findViewById(R.id.popu_spanel_list);
            if (listView != null) {
                ItemAdapter itemAdapter = null;
                if (listView.getAdapter() instanceof HeaderViewListAdapter) {
                    HeaderViewListAdapter ha = (HeaderViewListAdapter) listView.getAdapter();
                    if (ha != null) {
                        itemAdapter = (ItemAdapter)ha.getWrappedAdapter();
                    }
                } else {
                    itemAdapter = (ItemAdapter) listView.getAdapter();
                }
                if (itemAdapter != null) {
                    itemAdapter.clear();
                }
                itemAdapter = new ItemAdapter();
                itemAdapter.setData(data);
                listView.setAdapter(itemAdapter);
                if (popupWindow.isShowing()) {
                    updateWindowHeight(popupWindow.getContentView(), popupWindow.getContentView().getContext(), data.size(), maxHeight);
                } else if (!popupWindow.isShowing() && getAnchor() != null) {
                    resetWindowHeight(popupWindow.getContentView(), popupWindow.getContentView().getContext(), data.size(), maxHeight);

//                    int p[] = new int[2];
//                    getAnchor().getLocationOnScreen(p);
//                    popupWindow.showAtLocation(getAnchor(), Gravity.NO_GRAVITY, p[0], p[1] + anchor.getHeight() + offsetTop);
                    popupWindow.showAsDropDown(getAnchor());
                }
            }
        }

    }

    public void update(int maxHeight) {
        if (popupWindow != null && popupWindow.isShowing()) {
            ListView listView = (ListView)popupWindow.getContentView().findViewById(R.id.popu_spanel_list);
            if (listView != null) {
                ItemAdapter itemAdapter = null;
                if (listView.getAdapter() instanceof HeaderViewListAdapter) {
                    HeaderViewListAdapter ha = (HeaderViewListAdapter) listView.getAdapter();
                    if (ha != null) {
                        itemAdapter = (ItemAdapter)ha.getWrappedAdapter();
                    }
                } else {
                    itemAdapter = (ItemAdapter)listView.getAdapter();
                }

                if (itemAdapter != null) {
                    int count = itemAdapter.getCount();
                    updateWindowHeight(popupWindow.getContentView(), popupWindow.getContentView().getContext(), count, maxHeight);
                }
            }
        }
    }

    public boolean dismiss() {
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
            return true;
        }
        return false;
    }

    private void resetWindowHeight(Context context, int num) {
        int contentHeight = num*computeItemHeight(context);
        if (contentHeight > MAX_HEIGHT) {
            setPopupWindowHeight(MAX_HEIGHT);
        } else {
            setPopupWindowHeight(contentHeight);
        }
    }

    private void resetWindowHeight(Context context, int num, int maxHeight) {
        int contentHeight = num*computeItemHeight(context);
        if (contentHeight > maxHeight) {
            setPopupWindowHeight(maxHeight);
        } else {
            setPopupWindowHeight(contentHeight);
        }
    }

    private void updateWindowHeight(Context context, int num, int maxHeight) {
        int contentHeight = num*computeItemHeight(context);
        if (contentHeight > maxHeight) {
            updatePopupWindowHeight(maxHeight);
        } else {
            updatePopupWindowHeight(contentHeight);
        }
    }

    private void resetWindowHeight(View view, Context context, int num, int maxHeight) {
        int contentHeight = num*computeItemHeight(context);
        /*if (contentHeight > maxHeight) {
            setPopupWindowHeight(view, maxHeight);
        } else {
            setPopupWindowHeight(view, contentHeight);
        }*/
        setPopupWindowHeight(view, maxHeight);
    }

    private void updateWindowHeight(View view, Context context, int num, int maxHeight) {
        int contentHeight = num*computeItemHeight(context);
        /*if (contentHeight > maxHeight) {
            updatePopupWindowHeight(view, maxHeight);
        } else {
            updatePopupWindowHeight(view, contentHeight);
        }*/
        updatePopupWindowHeight(view, maxHeight);
    }

    private int computeItemHeight(Context context) {
        if (popupWindow != null && itemHeight == 0) {
            View item = LayoutInflater.from(context).inflate(R.layout.adapter_item_auto_text, null);
            item.measure(popupWindow.getContentView().getMeasuredWidthAndState(), popupWindow.getContentView().getMeasuredHeight());
            itemHeight = item.getMeasuredHeight();
        }
        return itemHeight;
    }

    private void setPopupWindowHeight(int height) {
        if (popupWindow != null && popupWindow.getHeight() != height) {
            popupWindow.setHeight(height);
            popupWindow.update();
        }
    }

    private void updatePopupWindowHeight(int height) {
        if (popupWindow != null && popupWindow.getHeight() != height) {
            popupWindow.setHeight(height);
            popupWindow.update(popupWindow.getWidth(), height);
        }
    }

    private void setPopupWindowHeight(View view, int height) {
        if (view != null) {
            ViewGroup.LayoutParams lp = view.getLayoutParams();
            if (lp == null) {
                lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);
            } else {
                lp.height = height;
            }
            view.setLayoutParams(lp);
        }
    }

    private void updatePopupWindowHeight(View view, int height) {
        /*if (popupWindow != null && popupWindow.getHeight() != height) {
            popupWindow.setHeight(height);
            popupWindow.update(popupWindow.getWidth(), height);
        }*/
        if (view != null) {
            ViewGroup.LayoutParams lp = view.getLayoutParams();
            if (lp == null) {
                lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);
            } else {
                lp.height = height;
            }
            view.setLayoutParams(lp);
        }
    }

    private class ItemAdapter extends BaseAdapter implements View.OnClickListener{

        private List<PlugWorldInfo> data;

        public void setData(List<PlugWorldInfo> data) {
            this.data = data;
        }

        public List<PlugWorldInfo> getData() {
            return data;
        }

        public void clear() {
            if (data != null) {
                data.clear();
                data = null;
            }
        }

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
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = View.inflate(parent.getContext(), R.layout.adapter_item_auto_text, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
                convertView.setOnClickListener(this);
            } else {
                viewHolder = (ViewHolder)convertView.getTag();
            }
            if (data != null && data.size() > position) {
                viewHolder.text.setText(PhoneticUtils.checkString(data.get(position).world));
                viewHolder.word = data.get(position).world;
                viewHolder.worldIndex = data.get(position).index;
                viewHolder.dictID = data.get(position).dictID;
            }
            return convertView;
        }

        @Override
        public void onClick(View v) {
            if (popupWindow != null) {
                popupWindow.dismiss();
            }
            ViewHolder viewHolder = (ViewHolder)v.getTag();
            if (onSelectedItemWordListener != null && viewHolder != null) {
                onSelectedItemWordListener.onSelected(viewHolder.dictID, viewHolder.word, viewHolder.worldIndex);
            }
        }
    }

    private class ViewHolder {

        TextView text;
        String word;
        int worldIndex;
        int dictID;

        public ViewHolder(View v) {
            text = (TextView)v.findViewById(R.id.text1);
        }

    }

    public interface OnSelectedItemWordListener {
        void onSelected(int dictID, String word, int worldIndex);
        void onDismiss();
    }
}
