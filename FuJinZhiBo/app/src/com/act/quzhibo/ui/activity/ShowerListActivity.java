package com.act.quzhibo.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.act.quzhibo.R;
import com.act.quzhibo.common.Constants;
import com.act.quzhibo.entity.PlateCatagory;
import com.act.quzhibo.entity.PlateList;
import com.act.quzhibo.entity.Room;
import com.act.quzhibo.okhttp.OkHttpUtils;
import com.act.quzhibo.okhttp.callback.StringCallback;
import com.act.quzhibo.ui.fragment.BackHandledFragment;
import com.act.quzhibo.ui.fragment.ShowerListFragment;
import com.act.quzhibo.util.CommonUtil;
import com.act.quzhibo.util.ViewFindUtils;
import com.flyco.tablayout.SlidingTabLayout;


import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

/**
 * 主播列表画面
 */
public class ShowerListActivity extends AppCompatActivity implements ShowerListFragment.OnCallShowViewListner , BackHandledFragment.BackHandledInterface {

    private ArrayList<Fragment> mFragments = new ArrayList<>();
    private MyPagerAdapter mAdapter;
    private BackHandledFragment mBackHandedFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sliding_tab_video);
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
            if (!plateCatagory.getTitleName().contains("VR") && !plateCatagory.getTitleName().contains("游戏")) {
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
        View decorView = getWindow().getDecorView();
        ViewPager pager = ViewFindUtils.find(decorView, R.id.viewpager);
        mAdapter = new MyPagerAdapter(getSupportFragmentManager(), tabTitles.toArray(new String[tabTitles.size()]));
        pager.setAdapter(mAdapter);
        final SlidingTabLayout tabLayout = ViewFindUtils.find(decorView, R.id.showerListLayout);
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
        if(mBackHandedFragment == null||!mBackHandedFragment.onBackPressed()){
            if(getSupportFragmentManager().getBackStackEntryCount() == 0){
                super.onBackPressed();
            }else{
                getSupportFragmentManager().popBackStack();
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
