package com.act.quzhibo.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.act.quzhibo.R;
import com.act.quzhibo.common.Constants;
import com.act.quzhibo.entity.PlateCatagory;
import com.act.quzhibo.entity.PlateList;
import com.act.quzhibo.entity.Room;
import com.act.quzhibo.okhttp.OkHttpUtils;
import com.act.quzhibo.okhttp.callback.StringCallback;
import com.act.quzhibo.ui.fragment.ShowerListFragment;
import com.act.quzhibo.util.CommonUtil;
import com.act.quzhibo.util.ViewFindUtils;
import com.flyco.tablayout.SlidingTabLayout;


import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

/**
 * 主播列表画面
 */
public class ShowerListActivity extends AppCompatActivity implements ShowerListFragment.OnCallShowViewListner {

    private ArrayList<Fragment> mFragments = new ArrayList<>();
    //6.0权限处理
    private boolean bPermission = false;
    private final int WRITE_PERMISSION_REQ_CODE = 100;
    private MyPagerAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bPermission = checkPublishPermission();
        if (!bPermission) {
            Toast.makeText(getApplication(), "请先允许app所需要的权限", Toast.LENGTH_LONG).show();
            bPermission = checkPublishPermission();
            return;
        }
        setContentView(R.layout.activity_sliding_tab_video);
        initView();
    }
    private void initView() {
        PlateList plates = CommonUtil.parseJsonWithGson(getIntent().getStringExtra(Constants.TAB_PLATE_LIST), PlateList.class);
        ArrayList<String> tabTitles = new ArrayList<>();
        if(plates==null){
            return;
        }
        for (PlateCatagory plateCatagory : plates.plateList) {
            if (!TextUtils.equals("VR直播", plateCatagory.getTitleName()) && !TextUtils.equals("vr直播", plateCatagory.getTitleName())) {
                tabTitles.add(plateCatagory.getTitleName());
                ShowerListFragment fragment=new ShowerListFragment();
                Bundle bundle=new Bundle();
                bundle.putString(Constants.CATAID,plateCatagory.getTitleId());
                bundle.putString(Constants.CATATITLE,plateCatagory.getTitleName());
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
    public void onShowVideo(Room room,String pathPrefix) {
        Intent intent = new Intent(ShowerListActivity.this, VideoPlayerActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("room", room);
        bundle.putString("pathPrefix",pathPrefix);
        intent.putExtras(bundle);
        startActivity(intent);
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


    private boolean checkPublishPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            List<String> permissions = new ArrayList<>();
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(ShowerListActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(ShowerListActivity.this, Manifest.permission.CAMERA)) {
                permissions.add(Manifest.permission.CAMERA);
            }
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(ShowerListActivity.this, Manifest.permission.READ_PHONE_STATE)) {
                permissions.add(Manifest.permission.READ_PHONE_STATE);
            }
            if (permissions.size() != 0) {
                ActivityCompat.requestPermissions(ShowerListActivity.this,
                        (String[]) permissions.toArray(new String[0]),
                        WRITE_PERMISSION_REQ_CODE);
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case WRITE_PERMISSION_REQ_CODE:
                for (int ret : grantResults) {
                    if (ret != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                }
                bPermission = true;
                break;
            default:
                break;
        }
    }
}
