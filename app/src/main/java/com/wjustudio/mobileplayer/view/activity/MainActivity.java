package com.wjustudio.mobileplayer.view.activity;

import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;
import com.wjustudio.mobileplayer.R;
import com.wjustudio.mobileplayer.appBase.BaseActivity;
import com.wjustudio.mobileplayer.utils.CommonUtil;
import com.wjustudio.mobileplayer.view.adapter.MainPagerAdapter;
import com.wjustudio.mobileplayer.view.fragment.AudioListFragment;
import com.wjustudio.mobileplayer.view.fragment.VideoListFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends BaseActivity {

    private TextView mTvAudio;
    private TextView mTvVideo;
    private View mIndicateLine;

    /**
     * ViewPager状态发生变化时会调用这个类
     */
    private class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            // 移动距离 = 起始位置 + 偏移大小
            // 起始位置 = position * 指示器宽度
            // 偏移大小 = 手指划过屏幕的百分比 * 指示器宽度
            int startX = position * mIndicateLine.getWidth();
            float offsetX = positionOffset * mIndicateLine.getWidth();
            ViewHelper.setTranslationX(mIndicateLine, offsetX + startX);
        }

        @Override
        public void onPageSelected(int position) {
            updateTabs(position);

        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }

    private ViewPager mMainViewPager;
    private MainPagerAdapter mPagerAdapter;
    private List<Fragment> mFragments;

    @Override
    public int onBindLayout() {
        return R.layout.activity_main;
    }

    @Override
    public void onInitView() {
        mMainViewPager = (ViewPager) findViewById(R.id.main_viewpager);
        mTvAudio = (TextView) findViewById(R.id.main_tv_audio);
        mTvVideo = (TextView) findViewById(R.id.main_tv_video);
        mIndicateLine = findViewById(R.id.main_indicate_line);
    }

    @Override
    protected void onSetViewData() {
        mFragments = new ArrayList<>();
        mPagerAdapter = new MainPagerAdapter(getSupportFragmentManager(), mFragments);
        mMainViewPager.setAdapter(mPagerAdapter);
        mMainViewPager.addOnPageChangeListener(new MyOnPageChangeListener());
        //初始化标签的高亮状态
        updateTabs(0);
        //初始化指示器为屏幕宽度的一半
        HashMap<String, Integer> windowSize = CommonUtil.getWindowSize(MainActivity.this);
        Integer width = windowSize.get(CommonUtil.WIDTH);
        mIndicateLine.getLayoutParams().width = width / 2;
        mIndicateLine.requestLayout();

        mTvAudio.setOnClickListener(this);
        mTvVideo.setOnClickListener(this);
    }

    @Override
    protected void onInitData() {
        mFragments.add(new VideoListFragment());
        mFragments.add(new AudioListFragment());
        mPagerAdapter.notifyDataSetChanged();
    }

    @Override
    public void onProcessClick(View v) {
        switch (v.getId()){
            case R.id.main_tv_video:
                mMainViewPager.setCurrentItem(0);
                break;
            case R.id.main_tv_audio:
                mMainViewPager.setCurrentItem(1);
                break;
        }
    }

    /**
     * 根据选中的position修改全部的tab。
     * @param position
     */
    private void updateTabs(int position) {
        updateTab(0, mTvVideo, position);
        updateTab(1, mTvAudio, position);
    }

    /**
     * 更新tab的颜色和字体的大小
     * @param position
     * @param tab
     * @param tabPosition
     */
    private void updateTab(int position, TextView tab, int tabPosition) {
        int green = CommonUtil.getColor(R.color.green);
        int halfWhite = CommonUtil.getColor(R.color.half_white);
        if (position == tabPosition){
            tab.setTextColor(green);
            ViewPropertyAnimator.animate(tab).scaleX(1.2f).scaleY(1.2f);
        }else {
            tab.setTextColor(halfWhite);
            ViewPropertyAnimator.animate(tab).scaleX(1.0f).scaleY(1.0f);
        }
    }

}
