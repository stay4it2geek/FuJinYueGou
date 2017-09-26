package com.act.quzhibo.ui.activity;

import android.app.Dialog;
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
import com.act.quzhibo.view.FragmentDialog;
import com.act.quzhibo.view.PsdInputView;
import com.act.quzhibo.view.SelfDialog;

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
        final SelfDialog selfDialog = new SelfDialog(WelcomeActivity.this);
        askCompactPermissions(new String[]{PermissionUtils.Manifest_CAMERA, PermissionUtils.Manifest_ACCESS_COARSE_LOCATION, PermissionUtils.Manifest_ACCESS_FINE_LOCATION, PermissionUtils.Manifest_WRITE_EXTERNAL_STORAGE}, new PermissionResult() {
            @Override
            public void permissionGranted() {
                doRequest();
            }

            @Override
            public void permissionDenied() {
                selfDialog.setTitle("同意权限后才能正常使用哦");
                selfDialog.setMessage("您需要同意以下权限:\n   1. 访问设备上的照片\n   2. 访问手机存储功能\n   3. 获取您的大概位置\n   4. 拍照");
                selfDialog.setYesOnclickListener("立即同意", new SelfDialog.onYesOnclickListener() {
                    @Override
                    public void onYesClick() {
                        grantPermission();
                        selfDialog.dismiss();
                    }
                });
                selfDialog.setNoOnclickListener("取消", new SelfDialog.onNoOnclickListener() {
                    @Override
                    public void onNoClick() {
                        finish();
                    }
                });
                selfDialog.show();
            }

            @Override
            public void permissionForeverDenied() {
                selfDialog.setTitle("同意权限后才能正常使用哦");
                selfDialog.setMessage("您需要同意以下权限:\n   1. 访问设备上的照片\n   2. 访问手机存储功能\n   3. 获取您的大概位置\n   4. 拍照");
                selfDialog.setYesOnclickListener("立即同意", new SelfDialog.onYesOnclickListener() {
                    @Override
                    public void onYesClick() {
                        grantPermission();
                        selfDialog.dismiss();
                    }
                });
                selfDialog.setNoOnclickListener("取消", new SelfDialog.onNoOnclickListener() {
                    @Override
                    public void onNoClick() {
                        selfDialog.dismiss();
                        finish();
                    }
                });
                selfDialog.show();
            }
        });
    }

    private void doRequest() {
        psdInputView = (PsdInputView) findViewById(R.id.psdInputView);
        user = BmobUser.getCurrentUser(RootUser.class);
        if (user != null) {
            CommonUtil.fecth(this);
            if (user.secretScan) {
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