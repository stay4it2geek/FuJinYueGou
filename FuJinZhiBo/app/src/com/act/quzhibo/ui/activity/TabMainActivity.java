package com.act.quzhibo.ui.activity;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TabHost;

import com.act.quzhibo.R;
import com.act.quzhibo.common.Constants;
import com.act.quzhibo.entity.PlateCatagory;
import com.act.quzhibo.entity.TabEntity;
import com.act.quzhibo.util.CommonUtil;
import com.act.quzhibo.util.ViewFindUtils;
import com.flyco.tablayout.CommonTabLayout;
import com.flyco.tablayout.listener.CustomTabEntity;
import com.flyco.tablayout.listener.OnTabSelectListener;

import java.util.ArrayList;


@SuppressWarnings("ALL")
public class TabMainActivity extends TabActivity {
    private TabHost tabHost;
    private View mDecorView;
    private String[] mTitles = {"多媒体", "直播", "广场", "赚钱", "我的"};
    private String[] mTitlesSpecial = {"多媒体", "直播", "我的"};
    private int[] mIconUnselectIds = {R.drawable.home, R.drawable.zhibo, R.drawable.square, R.drawable.money, R.drawable.mine};
    private int[] mIconUnselectIdsSpecial = {R.drawable.home, R.drawable.zhibo, R.drawable.mine};
    private int[] mIconSelectIds = {R.drawable.home_s, R.drawable.zhibo_s, R.drawable.square_s, R.drawable.money_s, R.drawable.mine_s};
    private int[] mIconSelectIdsSpecial = {R.drawable.home_s, R.drawable.zhibo_s, R.drawable.mine_s};
    private ArrayList<CustomTabEntity> mTabEntities = new ArrayList<>();
    private CommonTabLayout mTabLayout;
    private StringBuffer catagory;
    private PlateCatagory plateCatagory;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabmain);
        tabHost = TabMainActivity.this.getTabHost();
        Intent showListIntent = new Intent(TabMainActivity.this, ShowerListActivity.class);
        showListIntent.putExtra(Constants.TAB_PLATE_LIST, getIntent().getStringExtra(Constants.TAB_PLATE_LIST));
        tabHost.addTab(tabHost.newTabSpec("多媒体")
                .setIndicator(null, null)
                .setContent(new Intent(TabMainActivity.this, MultipleMeideaActivity.class)));
        tabHost.addTab(tabHost.newTabSpec("直播")
                .setIndicator(null, null)
                .setContent(showListIntent));
        tabHost.addTab(tabHost.newTabSpec("广场")
                .setIndicator(null, null)
                .setContent(new Intent(TabMainActivity.this, SquareActivity.class)));
        tabHost.addTab(tabHost.newTabSpec("赚钱")
                .setIndicator(null, null)
                .setContent(new Intent(TabMainActivity.this, MoneyActivity.class)));
        tabHost.addTab(tabHost.newTabSpec("我的")
                .setIndicator(null, null)
                .setContent(new Intent(TabMainActivity.this, MineActivity.class)));
        SetIndexButton();
        tabHost.setCurrentTab(0);
    }

    public void SetIndexButton() {
        if (CommonUtil.getToggle(this, Constants.SQUARE_AND_MONEY).getIsOpen().equals("true")) {
            for (int i = 0; i < mTitles.length; i++) {
                mTabEntities.add(new TabEntity(mTitles[i], mIconSelectIds[i], mIconUnselectIds[i]));
            }
        } else {
            for (int i = 0; i < mTitlesSpecial.length; i++) {
                mTabEntities.add(new TabEntity(mTitlesSpecial[i], mIconSelectIdsSpecial[i], mIconUnselectIdsSpecial[i]));
            }
        }
        mDecorView = getWindow().getDecorView();
        mTabLayout = ViewFindUtils.find(mDecorView, R.id.tabMain);
        mTabLayout.setTabData(mTabEntities);
        mTabLayout.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                mTabLayout.setCurrentTab(position);
                tabHost.setCurrentTab(position);
            }

            @Override
            public void onTabReselect(int position) {
            }
        });
    }

}
