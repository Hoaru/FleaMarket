package com.doyle.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

/**
 * Created by 30579 on 2018/2/4.
 */

public class FragmentAdapter_goods extends FragmentPagerAdapter {

    private ArrayList<Fragment> fragmentsList;
    public FragmentAdapter_goods(FragmentManager fm) {super(fm);}
    public FragmentAdapter_goods(FragmentManager fm, ArrayList<Fragment> fragments) {
        super(fm);
        this.fragmentsList = fragments;
    }


    @Override
    public Fragment getItem(int index) {
        return fragmentsList.get(index);
    }

    @Override
    public int getCount() {
        return fragmentsList.size();
    }
}
