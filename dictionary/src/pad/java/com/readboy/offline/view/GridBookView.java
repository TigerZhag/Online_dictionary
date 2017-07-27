package com.readboy.offline.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.FrameLayout;

import com.readboy.Dictionary.R;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class GridBookView extends FrameLayout{

    private Button langWen;   //当代高级英语辞典
    private Button biHua;     //学生笔画字典
    private Button dongMan;    //学生动漫英语词典
    private Button shiYongYingHan;    //实用英汉词典
    private Button shiYongYingYu;    //实用英英词典
    private Button shiYongHanYing;    //实用汉英词典
    private Button jianMing;    //简明汉英词典
    private Button xianDaiHanYu;    //汉语全功能词典
    private Button xueShengHanYu;    //学生汉语词典
    private Button chengYu;    //汉语成语词典
    private Button guHanYu;    //古汉语
    private Button shengCiKu;    //生词库

    public GridBookView(Context context) {
        super(context);
    }

    public GridBookView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GridBookView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

    }

    @Override
    public void onFinishInflate() {
        super.onFinishInflate();
        init(getContext());
    }

    private void init(Context context) {
        langWen = (Button)findViewById(R.id.langwen);
        biHua = (Button)findViewById(R.id.bihua);
        dongMan = (Button)findViewById(R.id.dongman);
        shiYongYingHan = (Button)findViewById(R.id.syyh);
        shiYongYingYu = (Button)findViewById(R.id.syyy);
        shiYongHanYing = (Button)findViewById(R.id.syhy);
        jianMing = (Button)findViewById(R.id.jianming);
        xianDaiHanYu = (Button)findViewById(R.id.xdhy);
        xueShengHanYu = (Button)findViewById(R.id.xshy);
        chengYu = (Button)findViewById(R.id.chengyu);
        guHanYu = (Button)findViewById(R.id.gdhy);
        shengCiKu = (Button)findViewById(R.id.newdict);

        langWen.setOnClickListener((OnClickListener) context);
        biHua.setOnClickListener((OnClickListener) context);
        dongMan.setOnClickListener((OnClickListener) context);
        shiYongYingHan.setOnClickListener((OnClickListener) context);
        shiYongYingYu.setOnClickListener((OnClickListener) context);
        shiYongHanYing.setOnClickListener((OnClickListener) context);
        jianMing.setOnClickListener((OnClickListener) context);
        xianDaiHanYu.setOnClickListener((OnClickListener) context);
        xueShengHanYu.setOnClickListener((OnClickListener) context);
        chengYu.setOnClickListener((OnClickListener) context);
        guHanYu.setOnClickListener((OnClickListener) context);
        shengCiKu.setOnClickListener((OnClickListener) context);

    }

}
