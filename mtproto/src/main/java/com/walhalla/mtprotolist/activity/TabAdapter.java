package com.walhalla.mtprotolist.activity;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.walhalla.mtprotolist.fragment.GlypeProxy;
import com.walhalla.mtprotolist.fragment.f1;

public class TabAdapter extends androidx.fragment.app.FragmentPagerAdapter{

    Context context;
    int totalTabs;

    public TabAdapter(Context c, FragmentManager fm, int totalTabs) {
        super(fm);
        context = c;
        this.totalTabs = totalTabs;
    }
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                Fragment homeFragment0 = f1.newInstance("", "");
                return homeFragment0;
            case 1:
                Fragment homeFragment = GlypeProxy.newInstance("", "");
                return homeFragment;
            default:
                return null;
        }
    }
    @Override
    public int getCount() {
        return totalTabs;
    }
}