package com.act.quzhibo.ui.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.act.quzhibo.R;
import com.act.quzhibo.common.Constants;
import com.act.quzhibo.entity.PlateCatagory;
import com.act.quzhibo.entity.PlateList;
import com.act.quzhibo.entity.Room;
import com.act.quzhibo.ui.fragment.BackHandledFragment;
import com.act.quzhibo.ui.fragment.MiBoFragement;
import com.act.quzhibo.ui.fragment.ShowerListFragment;
import com.act.quzhibo.util.CommonUtil;
import com.act.quzhibo.util.ViewFindUtils;
import com.act.quzhibo.view.FragmentDialog;
import com.flyco.tablayout.SlidingTabLayout;

import java.util.ArrayList;

public class ShowerListActivity extends FragmentActivity implements ShowerListFragment.OnCallShowViewListner, BackHandledFragment.BackHandledInterface {

    private ArrayList<Fragment> mFragments = new ArrayList<>();
    private MyPagerAdapter mAdapter;
    private BackHandledFragment mBackHandedFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sliding_tab);
        initView();
    }

    private void initView() {
        PlateList plates = CommonUtil.parseJsonWithGson(getIntent().getStringExtra(Constants.TAB_PLATE_LIST), PlateList.class);
        ArrayList<String> tabTitles = new ArrayList<>();
        final ArrayList<String> tabTitleIds = new ArrayList<>();
        if (plates == null) {
            return;
        }
        for (PlateCatagory plateCatagory : plates.plateList) {
            if (plateCatagory.getTitleName().contains("VR") || plateCatagory.getTitleName().contains("游戏") ||
                    plateCatagory.getTitleName().contains("交友") || plateCatagory.getTitleName().contains("非遗")) {
               continue;
            } else {
                tabTitles.add(plateCatagory.getTitleName());
                tabTitleIds.add(plateCatagory.getTitleId());
                ShowerListFragment fragment = new ShowerListFragment();
                Bundle bundle = new Bundle();
                bundle.putString(Constants.CATAID, plateCatagory.getTitleId());
                bundle.putString(Constants.CATATITLE, plateCatagory.getTitleName());
                fragment.setArguments(bundle);
                mFragments.add(fragment);
            }

        }
        tabTitles.add(2,"秘播");
        mFragments.add(2,new MiBoFragement());
        View decorView = getWindow().getDecorView();
        ViewPager pager = ViewFindUtils.find(decorView, R.id.viewpager);
        mAdapter = new MyPagerAdapter(getSupportFragmentManager(), tabTitles.toArray(new String[tabTitles.size()]));
        pager.setAdapter(mAdapter);
        final SlidingTabLayout tabLayout = ViewFindUtils.find(decorView, R.id.sListLayout);
        tabLayout.setViewPager(pager, tabTitles.toArray(new String[tabTitles.size()]), this, mFragments);
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                tabLayout.setCurrentTab(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        pager.setCurrentItem(0);
    }


    @Override
    public void onShowVideo(Room room, String pathPrefix, String screenType) {
        if (screenType.equals("2")) {
            Intent intent = new Intent(ShowerListActivity.this, VideoPlayerActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("room", room);
            bundle.putString("pathPrefix", pathPrefix);
            intent.putExtras(bundle);
            startActivity(intent);
        } else {
            Intent intent = new Intent(ShowerListActivity.this, VideoPlayerActivityLanscape.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("room", room);
            bundle.putString("pathPrefix", pathPrefix);
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }

    @Override
    public void onBackPressed() {
        if (mBackHandedFragment == null || !mBackHandedFragment.onBackPressed()) {
            if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                    FragmentDialog.newInstance("", "客官再看一会儿呗", "再欣赏下", "有事要忙", -1, false, new FragmentDialog.OnClickBottomListener() {
                        @Override
                        public void onPositiveClick(Dialog dialog) {
                            dialog.dismiss();
                        }

                        @Override
                        public void onNegtiveClick(Dialog dialog) {
                            dialog.dismiss();
                            ShowerListActivity.super.onBackPressed();
                        }
                    }).show(getSupportFragmentManager(), "dialog");
                } else {
                    getSupportFragmentManager().popBackStack();
                }
            }
        }
    }


    @Override
    public void setSelectedFragment(BackHandledFragment selectedFragment) {
        this.mBackHandedFragment = selectedFragment;
    }

    private class MyPagerAdapter extends FragmentPagerAdapter {
        private String[] tabTitles;

        public MyPagerAdapter(FragmentManager fm, String[] tabTitles) {
            super(fm);
            this.tabTitles = tabTitles;
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }
    }

}
