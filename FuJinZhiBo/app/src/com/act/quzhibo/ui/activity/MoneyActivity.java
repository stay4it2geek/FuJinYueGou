package com.act.quzhibo.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.act.quzhibo.R;
import com.act.quzhibo.ui.fragment.NoFragment;
import com.flyco.tablayout.CommonTabLayout;

import java.util.ArrayList;

/**
 * 财富
 */
public class MoneyActivity extends TabSlideBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    protected String[] getTitles() {
        return new String[]{"资源", "随笔", "项目"};
    }

    @Override
    protected ArrayList<Fragment> getFragments() {
        ArrayList<Fragment> fragments=new ArrayList<>();
        fragments.add(new NoFragment());
        fragments.add(new NoFragment());
        fragments.add(new NoFragment());
        return fragments;
    }

    @Override
    public boolean getActivityType() {
        return false;
    }
}
