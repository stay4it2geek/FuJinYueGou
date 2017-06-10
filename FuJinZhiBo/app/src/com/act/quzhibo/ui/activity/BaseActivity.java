package com.act.quzhibo.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.act.quzhibo.R;
import com.act.quzhibo.util.CommonUtil;
import com.act.quzhibo.util.ViewFindUtils;

import java.util.ArrayList;

/**
 * Created by weiminglin on 17/5/30.
 */

public abstract class BaseActivity extends FragmentActivity {
    protected MyPagerAdapter mAdapter;
    protected View decorView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common_tab_common);
        decorView = getWindow().getDecorView();
        mAdapter = new MyPagerAdapter(getSupportFragmentManager());
        CommonUtil.initView(getTitles(),getFragments(),decorView, (ViewPager) ViewFindUtils.find(decorView, R.id.viewpager),mAdapter);

    }

    private class MyPagerAdapter extends FragmentPagerAdapter {
        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return getFragments().size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return getTitles()[position];
        }

        @Override
        public Fragment getItem(int position) {
            return getFragments().get(position);
        }
    }

    protected abstract String[] getTitles();
    protected abstract ArrayList<Fragment> getFragments();
}
