package com.act.quzhibo.ui.activity;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.act.quzhibo.R;
import com.act.quzhibo.ui.fragment.BackHandledFragment;
import com.act.quzhibo.util.CommonUtil;
import com.act.quzhibo.util.ViewFindUtils;
import com.act.quzhibo.view.FragmentDialog;

import java.util.ArrayList;

public abstract class TabSlideBaseActivity extends FragmentActivity implements BackHandledFragment.BackHandledInterface {
    protected MyPagerAdapter mAdapter;
    protected View decorView;
    private BackHandledFragment mBackHandedFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common_tab);
        decorView = getWindow().getDecorView();
        mAdapter = new MyPagerAdapter(getSupportFragmentManager());
        CommonUtil.initView(getTitles(), decorView, (ViewPager) ViewFindUtils.find(decorView, R.id.viewpager), mAdapter, getActivityType());

    }

    protected void setPage(int positon) {
        ((ViewPager) ViewFindUtils.find(decorView, R.id.viewpager)).setCurrentItem(positon);
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

    @Override
    public void setSelectedFragment(BackHandledFragment selectedFragment) {
        this.mBackHandedFragment = selectedFragment;
    }

    @Override
    public void onBackPressed() {
        if (mBackHandedFragment == null || !mBackHandedFragment.onBackPressed()) {
            if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                if (isNeedShowBackDialog()) {
                    FragmentDialog.newInstance("", "客官再看一会儿呗", "再欣赏下", "有事要忙", -1, false, new FragmentDialog.OnClickBottomListener() {
                        @Override
                        public void onPositiveClick(Dialog dialog) {
                            dialog.dismiss();
                        }

                        @Override
                        public void onNegtiveClick(Dialog dialog) {
                            dialog.dismiss();
                            TabSlideBaseActivity.super.onBackPressed();
                        }
                    }).show(getSupportFragmentManager(), "dialog");
                } else {
                    TabSlideBaseActivity.super.onBackPressed();
                }
            } else {
                getSupportFragmentManager().popBackStack();
            }
        }
    }

    public abstract boolean getActivityType();

    protected abstract boolean isNeedShowBackDialog();

    protected abstract String[] getTitles();

    protected abstract ArrayList<Fragment> getFragments();
}
