package com.act.quzhibo.ui.activity;

import android.Manifest;
import android.app.TabActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
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
    private String[] mTitles = {"多媒体", "直播", "广场", "财富", "我的"};
    private String[] mTitlesSpecial = {"多媒体", "直播", "我的"};
    private int[] mIconUnselectIds = {R.drawable.home, R.drawable.zhibo, R.drawable.square, R.drawable.money, R.drawable.mine};
    private int[] mIconUnselectIdsSpecial = {R.drawable.home, R.drawable.zhibo, R.drawable.mine};
    private int[] mIconSelectIds = {R.drawable.home_s, R.drawable.zhibo_s, R.drawable.square_s, R.drawable.money_s, R.drawable.mine_s};
    private int[] mIconSelectIdsSpecial = {R.drawable.home_s, R.drawable.zhibo_s, R.drawable.mine_s};
    private ArrayList<CustomTabEntity> mTabEntities = new ArrayList<>();
    private CommonTabLayout mTabLayout;
    private StringBuffer catagory;
    private PlateCatagory plateCatagory;
    public static final int REQUEST_PERMISSION_SEETING = 200;
    //6.0权限处理
    private boolean bPermission = false;
    private final int WRITE_PERMISSION_REQ_CODE = 100;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("你确定退出吗？")
                    .setCancelable(false)
                    .setPositiveButton("确定",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.dismiss();
                                    TabMainActivity.this.finish();
                                }
                            });
            AlertDialog alert = builder.create();
            alert.show();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

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
        bPermission = checkPublishPermission();
        if (!bPermission) {
            Snackbar.make(findViewById(R.id.snack), "请设置权限", Snackbar.LENGTH_INDEFINITE).setAction("确定", new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                    intent.setData(uri);
                    startActivityForResult(intent, REQUEST_PERMISSION_SEETING);
                }
            }).show();
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

            if (permissions.size() != 0) {
                return false;
            }
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!bPermission) {
            findViewById(R.id.snack).setVisibility(View.VISIBLE);
            Snackbar.make(findViewById(R.id.snack), "请允许全部权限", Snackbar.LENGTH_INDEFINITE).setAction("确定", new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                    intent.setData(uri);
                    startActivityForResult(intent, REQUEST_PERMISSION_SEETING);
                }
            }).show();
            return;
        } else {
            findViewById(R.id.snack).setVisibility(View.VISIBLE);
            Snackbar.make(findViewById(R.id.snack), "权限设置成功", Snackbar.LENGTH_SHORT).show();
        }
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //如果是从设置界面返回,就继续判断权限
        if (requestCode == REQUEST_PERMISSION_SEETING) {
            bPermission = checkPublishPermission();
        }
    }

}
