package com.act.quzhibo.ui;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TabHost;

import com.act.quzhibo.R;
import com.act.quzhibo.entity.TabEntity;
import com.act.quzhibo.entity.Toggle;
import com.act.quzhibo.util.ViewFindUtils;
import com.flyco.tablayout.CommonTabLayout;
import com.flyco.tablayout.listener.CustomTabEntity;
import com.flyco.tablayout.listener.OnTabSelectListener;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

@SuppressWarnings("ALL")
public class TabMainActivity extends TabActivity {
    private TabHost tabHost;
    private LinearLayout ll;
    private List default_imagelist;
    private List list1 = new ArrayList();
    private View mDecorView;
    private String[] mTitles = {"多媒体", "直播", "广场", "赚钱","我的"};
    private String[] mTitlesSpecial = {"多媒体", "直播","我的"};
    private int[] mIconUnselectIds = {
            R.drawable.home, R.drawable.zhibo,
            R.drawable.square, R.drawable.money, R.drawable.mine};
    private int[] mIconUnselectIdsSpecial = {
            R.drawable.home, R.drawable.zhibo, R.drawable.mine};
    private int[] mIconSelectIds = {
            R.drawable.home_s, R.drawable.zhibo_s,
            R.drawable.square_s,R.drawable.money_s, R.drawable.mine_s};
    private int[] mIconSelectIdsSpecial = {
            R.drawable.home_s, R.drawable.zhibo_s, R.drawable.mine_s};
    private ArrayList<CustomTabEntity> mTabEntities = new ArrayList<>();
    private CommonTabLayout mTabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comm);
        tabHost = this.getTabHost();
        tabHost.addTab(tabHost.newTabSpec("多媒体")
                .setIndicator(null, null)
                .setContent(new Intent(this,KonwlegeActivity.class)));
        tabHost.addTab(tabHost.newTabSpec("直播")
                .setIndicator(null, null)
                .setContent(new Intent(this, ViewShowActivity.class)));
        tabHost.addTab(tabHost.newTabSpec("广场")
                .setIndicator(null, null)
                .setContent(new Intent(this, ViewShowActivity.class)));
        tabHost.addTab(tabHost.newTabSpec("赚钱")
                .setIndicator(null, null)
                .setContent(new Intent(this, ViewShowActivity.class)));
        tabHost.addTab(tabHost.newTabSpec("我的")
                .setIndicator(null, null)
                .setContent(new Intent(this, MineActivity.class)));
        SetIndexButton();
        tabHost.setCurrentTab(0);
    }
    public void SetIndexButton() {
        String isOpen=getIntent().getStringExtra("isOpen");
        if(isOpen.equals("true")){
            for (int i = 0; i < mTitles.length; i++) {
                mTabEntities.add(new TabEntity(mTitles[i], mIconSelectIds[i], mIconUnselectIds[i]));
            }
        }else{
            for (int i = 0; i < mTitlesSpecial.length; i++) {
                mTabEntities.add(new TabEntity(mTitlesSpecial[i], mIconSelectIdsSpecial[i], mIconUnselectIdsSpecial[i]));
            }
        }
        mDecorView = getWindow().getDecorView();
        mTabLayout = ViewFindUtils.find(mDecorView, R.id.tl_1);
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
