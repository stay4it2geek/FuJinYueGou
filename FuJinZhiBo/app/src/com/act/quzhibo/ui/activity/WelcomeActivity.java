package com.act.quzhibo.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.act.quzhibo.R;
import com.act.quzhibo.common.Constants;
import com.act.quzhibo.entity.RootUser;
import com.act.quzhibo.entity.Toggle;
import com.act.quzhibo.okhttp.OkHttpUtils;
import com.act.quzhibo.okhttp.callback.Callback;
import com.act.quzhibo.util.CommonUtil;
import com.act.quzhibo.view.PsdInputView;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FetchUserInfoListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import okhttp3.Call;
import okhttp3.Response;

public class WelcomeActivity extends Activity {

    private String plateListStr;
    RootUser user;
    PsdInputView psdInputView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        psdInputView = (PsdInputView) findViewById(R.id.psdInputView);
        user = BmobUser.getCurrentUser(RootUser.class);
        if (user != null && user.secretScan) {
            fecth();

            findViewById(R.id.psdInputViewLayout).setVisibility(View.VISIBLE);
            psdInputView.setComparePassword(new PsdInputView.onPasswordListener() {
                @Override
                public void onSettingMode(String text) {
                    if (text.equals(user.secretPassword)) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        if (imm != null) {
                            imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
                        }
                        findViewById(R.id.psdInputViewLayout).setVisibility(View.GONE);
                        request();
                    } else {
                        Toast.makeText(WelcomeActivity.this, "密码不正确", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            findViewById(R.id.psdInputViewLayout).setVisibility(View.GONE);
            request();
        }

    }

    private void fecth() {
        BmobUser.fetchUserInfo(new FetchUserInfoListener<RootUser>() {
            @Override
            public void done(RootUser user, BmobException e) {
                if (e == null) {
                    Toast.makeText(WelcomeActivity.this, "缓存同步成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(WelcomeActivity.this, "缓存同步失败，请先登录", Toast.LENGTH_SHORT).show();
                }
            }
        });
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
                            Toast.makeText(getApplicationContext(), "请求超时,正在重试", Toast.LENGTH_SHORT).show();
                        }
                    });
                    request();
                }
            }
        });


    }

    private void getPlateList() {
        OkHttpUtils.get().url(CommonUtil.getToggle(this, "tabCatagory").getToggleObject()).build().execute(new Callback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                getPlateList();
            }

            @Override
            public void onResponse(Object response, int id) {
            }

            @Override
            public Object parseNetworkResponse(Response response, int id) throws Exception {
                plateListStr = response.body().string();
                Message message = handler.obtainMessage();
                message.obj = plateListStr;
                handler.sendMessage(message);
                return null;
            }
        });
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            plateListStr = (String) msg.obj;
            this.postDelayed(runnable, 2000);
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