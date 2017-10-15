package com.act.quzhibo.ui.activity;

import android.Manifest;
import android.app.TabActivity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
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
import java.util.List;

@SuppressWarnings("ALL")
public class TabMainActivity extends TabActivity {
    private TabHost tabHost;
    private View mDecorView;
    private String[] mTitles = {"课程", "直播", "广场", "钱袋子","聊天", "我的"};
    private String[] mTitlesSpecial = {"课程", "广场", "我的"};

    private int[] mIconUnselectIds = {R.drawable.courses, R.drawable.show, R.drawable.square, R.drawable.money, R.mipmap.icon_message_normal,R.drawable.mine};

    private int[] mIconSelectIds = {R.drawable.courses_s, R.drawable.show_s, R.drawable.square_s, R.drawable.money_s, R.mipmap.icon_message_press, R.drawable.mine_s};

    private int[] mIconUnselectIdsSpecial = {R.drawable.courses, R.drawable.money, R.drawable.mine};
    private int[] mIconSelectIdsSpecial = {R.drawable.courses_s, R.drawable.money_s, R.drawable.mine_s};

    private ArrayList<CustomTabEntity> mTabEntities = new ArrayList<>();
    private CommonTabLayout mTabLayout;
    private StringBuffer catagory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_tabmain);
        tabHost = TabMainActivity.this.getTabHost();
        Intent showListIntent = new Intent(TabMainActivity.this, ShowerListActivity.class);
        showListIntent.putExtra(Constants.TAB_PLATE_LIST, getIntent().getStringExtra(Constants.TAB_PLATE_LIST));
        if (CommonUtil.getToggle(this, Constants.SQUARE_AND_MONEY).getIsOpen().equals("true")) {
            tabHost.addTab(tabHost.newTabSpec("课程")
                    .setIndicator(null, null)
                    .setContent(new Intent(TabMainActivity.this, PuaCoursesActivity.class)));
            tabHost.addTab(tabHost.newTabSpec("直播")
                    .setIndicator(null, null)
                    .setContent(showListIntent));
            tabHost.addTab(tabHost.newTabSpec("广场")
                    .setIndicator(null, null)
                    .setContent(new Intent(TabMainActivity.this, SquareActivity.class)));

            tabHost.addTab(tabHost.newTabSpec("聊天")
                    .setIndicator(null, null)
                    .setContent(new Intent(TabMainActivity.this, MoneyCourseActivity.class)));
            tabHost.addTab(tabHost.newTabSpec("钱袋子")
                    .setIndicator(null, null)
                    .setContent(new Intent(TabMainActivity.this, MainActivity.class)));
            tabHost.addTab(tabHost.newTabSpec("我的")
                    .setIndicator(null, null)
                    .setContent(new Intent(TabMainActivity.this, MineActivity.class)));
        } else {
            tabHost.addTab(tabHost.newTabSpec("课堂")
                    .setIndicator(null, null)
                    .setContent(new Intent(TabMainActivity.this, PuaCoursesActivity.class)));
            tabHost.addTab(tabHost.newTabSpec("广场")
                    .setIndicator(null, null)
                    .setContent(new Intent(TabMainActivity.this, SquareActivity.class)));
            tabHost.addTab(tabHost.newTabSpec("我的")
                    .setIndicator(null, null)
                    .setContent(new Intent(TabMainActivity.this, MineActivity.class)));
        }
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

    private boolean checkPublishPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            List<String> permissions = new ArrayList<>();
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(TabMainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(TabMainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
            }
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(TabMainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
            }
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(TabMainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                permissions.add(Manifest.permission.CAMERA);
            }
            if (permissions.size() != 0) {
                return false;
            }
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CommonUtil.clearData(this);
    }
}
