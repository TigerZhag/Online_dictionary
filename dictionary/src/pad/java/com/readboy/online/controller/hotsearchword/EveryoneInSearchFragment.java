package com.readboy.online.controller.hotsearchword;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextPaint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.readboy.Dictionary.R;
import com.readboy.online.model.adapters.FragmentAdapter;

import java.util.ArrayList;
import java.util.List;

// TODO: 16-7-6 大家都再查未完成点:1.TextView排版 2.TextView点击事件(显示热搜词详情和post热搜词到服务器) 3.判断小于20取其他年级的处理
// TODO: 16-7-6 大家都再查未完成点:4.对热搜词做缓存,因为当无网络的时候获取不了服务器数据就要读取缓存内容,缓存内容跟新时机需要考虑
/**
 * 大家都再查Fragment
 */
public class EveryoneInSearchFragment extends Fragment {

    private static final String TAG = EveryoneInSearchFragment.class.getSimpleName();
    /*   大家都再查布局   */
    private View mViewRoot;
    /*   指示点的父容器   */
    private LinearLayout mLayoutDot;
    /*   指示灯集合   */
    private List<ImageView> mLvDotList;
    /*   热词页Fragment集合   */
    private List<Fragment> mPageFragmentLsit;

    /*   热词集合   */
    private ArrayList<String> mHotWordList;//遍历每个json数据封装成HotWord对象加入集合中
    /*   暂无热搜   */
    private ImageView ivHotWordNull;
    /*   放置mPageFragmentLsit   */
    private ViewPager mViewPager;
    private PagerAdapter mPagerAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mViewRoot = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_everysearch,container,false);
        findViewById();
        init();
        /*   获取热词list并且判空   */
        mHotWordList = getArguments().getStringArrayList("hotWord");
        if(mHotWordList.size() == 0){
            Log.i(TAG,"--------- HotSearchWord is null ---------");
            initShow();//一定要先有fragment才能实例viewpage
        }else {
            jduge(mHotWordList,0,mHotWordList.size());
            initShow();//一定要先有fragment才能实例viewpage
        }

        return mViewRoot;
    }

    /*   初始化控件   */
    private void findViewById(){
        mLayoutDot = (LinearLayout) mViewRoot.findViewById(R.id.layout_dot);
        ivHotWordNull = (ImageView) mViewRoot.findViewById(R.id.iv_hot_word_null);
        mViewPager = (ViewPager) mViewRoot.findViewById(R.id.vpager_tv);
    }

    /*   初始化集合   */
    private void init(){
        mLvDotList = new ArrayList<ImageView>();
        mHotWordList = new ArrayList<String>();
        mPageFragmentLsit = new ArrayList<Fragment>();
    }

    /*   初始化界面显示内容   */
    private void initShow(){
        /*   如果没有热词则相应没有放置热词的Fragment,那么显示暂无热搜的图片,且返回不继续初始化界面控件   */
        if(mHotWordList.size() == 0){
            ivHotWordNull.setVisibility(View.VISIBLE);
            return;
        }
        initViewPager();
        /*   初始第一页指示点被选择   */
        mLvDotList.get(0).setImageResource(R.drawable.page_indicator_focused);
    }

    /*   初始化viewPager用于放置热搜词Fragment   */
    private void initViewPager(){

        mPagerAdapter = new FragmentAdapter(getActivity().getSupportFragmentManager(), mPageFragmentLsit);
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setCurrentItem(0);
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            /*   更换fragment时,指示点相应的更换   */
            @Override
            public void onPageSelected(int position) {
                resetDot(mLvDotList);
                switch (position){
                    case 0:
                    case 1:
                    case 2:
                    case 3:
                    case 4:
                        mLvDotList.get(position).setImageResource(R.drawable.page_indicator_focused);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {}
        });
    }

    /*   添加放置热搜词的Fragment   */
    public void addFragment(List<String> list,int start,int end){
        HotWordFragment hotWordFragment  = new HotWordFragment();
        hotWordFragment.setmHotWordList(list,start,end);
        mPageFragmentLsit.add(hotWordFragment);
        //指示点个数和热搜词Fragment的个数相同,因此可以根据Fragment个数增加指示点个数
        addDot(mLayoutDot);
    }

    /*   添加指示点   */
    private void addDot(LinearLayout layout){
        ImageView ivDot = new ImageView(getActivity());
        ivDot.setImageResource(R.drawable.page_indicator_unfocused);
        ivDot.setPadding(5,10,5,10);
        layout.addView(ivDot, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mLvDotList.add(ivDot);
    }

    /*   重置指示点样式   */
    private void resetDot(List<ImageView> list){
        for(ImageView dot:list){
            dot.setImageResource(R.drawable.page_indicator_unfocused);
        }
    }

    /*   此乃失败之作,由于fragment的getActivity总null,不会解决,出此高耦合的下下策..用于生成fragment并且加入list   */
    /*   思路:两行位置用于放词语,只要有一行满足位置需求就放进去,放完了并且位置还有就新建fragment,没放完并且位置不够,就新建fragment然后递归下去   */
    private void jduge(List<String> list,int start,int end){
        //这个padding和canUseWidth1都是可能需要改变的,值得留意,没办法,此乃失败之作,是我能力不足
        float padding = 90;
        float width;
        float canUseWidth1 = 700;
        float canUseWidth2 = 700;
        TextView textView = new TextView(getActivity());
        TextPaint paint = textView.getPaint();
        for(int i=start;i<end;i++){
            width = paint.measureText(list.get(i)) + padding;
            if(width <= canUseWidth1){
                canUseWidth1 = canUseWidth1 - width;
                if(i == end-1)
                    addFragment(list,start,end);
            }else if(width <= canUseWidth2){
                canUseWidth2 = canUseWidth2 - width;
                if(i == end-1)
                    addFragment(list,start,end);
            }else {
                addFragment(list,start,i);
                jduge(list,i,end);
                break;
            }
        }
    }
}