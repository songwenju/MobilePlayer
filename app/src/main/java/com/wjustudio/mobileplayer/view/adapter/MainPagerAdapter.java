package com.wjustudio.mobileplayer.view.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * 作者： songwenju on 2016/6/26 11:16.
 * 邮箱： songwenju@outlook.com
 */
public class MainPagerAdapter extends FragmentPagerAdapter{
    List<Fragment> mFragments ;
    public MainPagerAdapter(FragmentManager fm,List<Fragment> fragments) {
        super(fm);
        this.mFragments = fragments;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }
}
