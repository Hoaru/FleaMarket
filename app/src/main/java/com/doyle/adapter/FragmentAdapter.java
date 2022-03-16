package com.doyle.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

/**
 * Created by 30579 on 2018/1/29.
 */

public class FragmentAdapter extends FragmentPagerAdapter {

    private ArrayList<Fragment> fragmentsList;
    public FragmentAdapter(FragmentManager fm) {super(fm);}
    public FragmentAdapter(FragmentManager fm, ArrayList<Fragment> fragments) {
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
