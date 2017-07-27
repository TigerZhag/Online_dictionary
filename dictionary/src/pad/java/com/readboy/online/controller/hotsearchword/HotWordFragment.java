package com.readboy.online.controller.hotsearchword;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextPaint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.readboy.Dictionary.R;
import com.readboy.online.view.FixGridLayout;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class HotWordFragment extends Fragment implements View.OnClickListener {

    /*   热搜词汇显示布局   */
    private View viewRoot;
    /*   显示热搜词的第一二行   */
    private FixGridLayout firstLineLayout;
    private FixGridLayout secondLineLayout;
    /*   用于时刻记录第一二行所剩长度   */
    private float canUseWidth1;
    private float canUseWidth2;
    /*   当前fragment放置的内容   */
    private List<String> mHotWordList;
    private int start,end;
    private String[] textviewColor = {"#ff4925","#ffb700","#22ed28","#ff1965","#0ee9c8"};

    private Timer timer = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        viewRoot = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_hot_word,container,false);
        findViewById();

        //因为父容器宽度固定,得到后感觉可以不要这个
        final Handler myHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(msg.what == 1) {
                    if(firstLineLayout.getWidth()!=0 && secondLineLayout.getWidth()!=0) {
                        Log.i("sdxsdxsd",firstLineLayout.getWidth()+"");
                        timer.cancel();
                        init();
                        //传个list过来 一个一个遍历加
                        for(int i=start;i<end;i++){
                            //需要把父容器的宽度得出来后才能开始添加textview
                            if(i<5){
                                addTextView(mHotWordList.get(i),textviewColor[i]);
                            }else{
                                addTextView(mHotWordList.get(i),null);
                            }
                        }
                    }
                }
            }
        };
        timer = new Timer();
        TimerTask task = new TimerTask(){
            public void run() {
                Message message = new Message();
                message.what = 1;
                myHandler.sendMessage(message);
            }
        };
        timer.schedule(task,10,1000);

        return viewRoot;
    }

    private void findViewById(){
        firstLineLayout = (FixGridLayout) viewRoot.findViewById(R.id.layout_hot_word_first_line);
        secondLineLayout = (FixGridLayout) viewRoot.findViewById(R.id.layout_hot_word_second_line);
        firstLineLayout.setmCellHeight(50);
        firstLineLayout.setmCellWidth(140);
        secondLineLayout.setmCellHeight(50);
        secondLineLayout.setmCellWidth(140);
    }

    public void init(){
        canUseWidth1 = firstLineLayout.getWidth();
        canUseWidth2 = secondLineLayout.getWidth();
    }

    /*   添加textview   */
    public void addTextView(String text,String color){
        float tvWidth;
        TextView tvHotWord = new TextView(getActivity());
        /*   设置textView样式和监听器   */
        if(color != null){
            tvHotWord.setTextColor(Color.parseColor(color));
        }
        tvHotWord.setHeight(47);
        tvHotWord.setGravity(View.TEXT_ALIGNMENT_CENTER);
        tvHotWord.setText(text);
        tvHotWord.setTextSize(22);
        tvHotWord.setOnClickListener(this);
//        tvHotWord.setBackgroundColor(Color.parseColor("#f2f2f2"));
        //获取了textview宽度
        TextPaint paint = tvHotWord.getPaint();
        tvWidth = paint.measureText(text);
        tvWidth = tvWidth + 90;
        /*   如果第一行够位置就放,不够就判断第二行位置够不够放,还是不够就新建fragment   */
        if(tvWidth <= canUseWidth1){
            firstLineLayout.addView(tvHotWord, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            canUseWidth1 = canUseWidth1 - tvWidth;
        }else if(tvWidth <= canUseWidth2){
            secondLineLayout.addView(tvHotWord, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            canUseWidth2 = canUseWidth2 - tvWidth;
        }
    }

    @Override
    public void onClick(View v) {

    }

    /*   设置当前fragment放置的list内容   */
    public void setmHotWordList(List<String> mHotWordList, int start, int end) {
        this.mHotWordList = mHotWordList;
        this.start = start;
        this.end = end;
    }
}