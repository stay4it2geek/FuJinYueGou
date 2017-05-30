package com.act.quzhibo.ui;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TabHost;

import com.act.quzhibo.R;
import com.act.quzhibo.entity.TabEntity;
import com.act.quzhibo.util.ViewFindUtils;
import com.flyco.tablayout.CommonTabLayout;
import com.flyco.tablayout.listener.CustomTabEntity;
import com.flyco.tablayout.listener.OnTabSelectListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends TabActivity {

    private TabHost tabHost;
    private LinearLayout ll;
    private List default_imagelist;
    private List list1 = new ArrayList();
    private View mDecorView;
    private String[] mTitles = {"首页", "直播", "广场", "我的"};
    private int[] mIconUnselectIds = {
            R.mipmap.tab_home_unselect, R.drawable.zhibo_u,
            R.drawable.guangchang, R.drawable.mine_u};
    private int[] mIconSelectIds = {
            R.mipmap.tab_home_select, R.drawable.zhibo,
            R.drawable.guangchang_s, R.drawable.mine};
    private ArrayList<CustomTabEntity> mTabEntities = new ArrayList<>();

    private CommonTabLayout mTabLayout_1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comm);
        tabHost = this.getTabHost();
        tabHost.addTab(tabHost.newTabSpec("tab1")
                .setIndicator(null, null)
                .setContent(new Intent(this, SegmentTabActivity.class)));//����tag�����ݣ���һ��ѡ�ʹ��һ��ActivityGroup,����������ͼ��

        tabHost.addTab(tabHost.newTabSpec("tab2")
                .setIndicator(null, null)
                .setContent(new Intent(this, ViewShowActivity.class)));//

        tabHost.addTab(tabHost.newTabSpec("tab3")
                .setIndicator(null, null)
                .setContent(new Intent(this, CommonTabActivity.class)));//

        tabHost.addTab(tabHost.newTabSpec("tab4")
                .setIndicator(null, null)
                .setContent(new Intent(this, MineActivity.class)));//


        SetIndexButton();
        tabHost.setCurrentTab(0);


    }


    public void SetIndexButton() {
        for (int i = 0; i < mTitles.length; i++) {
            mTabEntities.add(new TabEntity(mTitles[i], mIconSelectIds[i], mIconUnselectIds[i]));
        }
        mDecorView = getWindow().getDecorView();
        mTabLayout_1 = ViewFindUtils.find(mDecorView, R.id.tl_1);
        mTabLayout_1.setTabData(mTabEntities);
        mTabLayout_1.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                mTabLayout_1.setCurrentTab(position);
                tabHost.setCurrentTab(position);

            }

            @Override
            public void onTabReselect(int position) {

            }
        });
    }
}
