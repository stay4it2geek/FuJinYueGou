package com.act.quzhibo.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.act.quzhibo.R;
import com.act.quzhibo.common.Constants;
import com.act.quzhibo.common.OkHttpClientManager;
import com.act.quzhibo.entity.RootUser;
import com.act.quzhibo.entity.Toggle;
import com.act.quzhibo.util.CommonUtil;
import com.act.quzhibo.util.ToastUtil;
import com.act.quzhibo.view.PsdInputView;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import permission.auron.com.marshmallowpermissionhelper.ActivityManagePermission;
import permission.auron.com.marshmallowpermissionhelper.PermissionResult;
import permission.auron.com.marshmallowpermissionhelper.PermissionUtils;

import static com.act.quzhibo.common.Constants.REQUEST_SETTING;

public class WelcomeActivity extends ActivityManagePermission {

    private String plateListStr;
    RootUser user;
    PsdInputView psdInputView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        grantPermission();
    }

    private void grantPermission() {

        askCompactPermissions(new String[]{PermissionUtils.Manifest_CAMERA, PermissionUtils.Manifest_ACCESS_COARSE_LOCATION, PermissionUtils.Manifest_ACCESS_FINE_LOCATION, PermissionUtils.Manifest_WRITE_EXTERNAL_STORAGE}, new PermissionResult() {
            @Override
            public void permissionGranted() {
                doRequest();
            }

            @Override
            public void permissionDenied() {
                findViewById(R.id.container).setVisibility(View.VISIBLE);
                Snackbar.make(findViewById(R.id.container), "图片选择需要以下权限:\n\n1.访问设备上的照片\n\n2.拍照\n\n3. 获取您的大概位置", Snackbar.LENGTH_LONG).setAction("去设置", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivityForResult(intent, REQUEST_SETTING);
                    }
                }).setDuration(50000).show();
            }

            @Override
            public void permissionForeverDenied() {
                findViewById(R.id.container).setVisibility(View.VISIBLE);
                Snackbar.make(findViewById(R.id.container), "图片选择需要以下权限:\n\n1.访问设备上的照片\n\n2.拍照\n\n3. 获取您的大概位置", Snackbar.LENGTH_LONG).setAction("去设置", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivityForResult(intent, REQUEST_SETTING);
                    }
                }).setDuration(50000).show();

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        grantPermission();
    }

    private void doRequest() {
        psdInputView = (PsdInputView) findViewById(R.id.psdInputView);
        user = BmobUser.getCurrentUser(RootUser.class);
        if (user != null && user.secretScan) {
            CommonUtil.fecth(this);
            findViewById(R.id.psdInputViewLayout).setVisibility(View.VISIBLE);
            final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (!imm.isActive(psdInputView)) {
                imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
            }
            psdInputView.setComparePassword(new PsdInputView.onPasswordListener() {
                @Override
                public void onSettingMode(String text) {
                    if (text.equals(user.secretPassword)) {
                        if (imm != null) {
                            imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
                        }
                        findViewById(R.id.psdInputViewLayout).setVisibility(View.GONE);
                        request();
                    } else {
                        ToastUtil.showToast(WelcomeActivity.this, "密码不正确");
                    }
                }
            });
        } else {
            findViewById(R.id.psdInputViewLayout).setVisibility(View.GONE);
            request();
        }
    }

    private void request() {
        BmobQuery<Toggle> query = new BmobQuery<>();
        query.findObjects(new FindListener<Toggle>() {
            @Override
            public void done(List<Toggle> Toggles, BmobException bmobException) {
                if (bmobException == null) {
                    SharedPreferences mySharedPreferences = getSharedPreferences(Constants.SAVE, Context.MODE_PRIVATE);
                    SharedPreferences.Editor edit = mySharedPreferences.edit();
                    String liststr = CommonUtil.SceneList2String(Toggles);
                    edit.putString(Constants.TOGGLES, liststr);
                    edit.commit();
                    getPlateList();
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtil.showToast(getApplicationContext(), "请求超时,正在重试");
                        }
                    });
                    request();
                }
            }
        });
    }

    private void getPlateList() {
        String url = CommonUtil.getToggle(this, "tabCatagory").getToggleObject();
        OkHttpClientManager.parseRequest(this, url, handler, Constants.REFRESH);
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            plateListStr = (String) msg.obj;
            this.postDelayed(runnable, 1000);
        }
    };


    Runnable runnable = new Runnable() {
        public void run() {
            findViewById(R.id.progressBar).setVisibility(View.GONE);
            Intent intent = new Intent();
            intent.setClass(WelcomeActivity.this, TabMainActivity.class);
            intent.putExtra(Constants.TAB_PLATE_LIST, plateListStr);
            startActivity(intent);
            finish();
        }
    };


}