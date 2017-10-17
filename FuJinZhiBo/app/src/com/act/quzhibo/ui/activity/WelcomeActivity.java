package com.act.quzhibo.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.act.quzhibo.R;
import com.act.quzhibo.common.Constants;
import com.act.quzhibo.common.OkHttpClientManager;
import com.act.quzhibo.bean.RootUser;
import com.act.quzhibo.bean.Toggle;
import com.act.quzhibo.util.CommonUtil;
import com.act.quzhibo.util.ToastUtil;
import com.act.quzhibo.lock_view.LockIndicatorView;
import com.act.quzhibo.lock_view.LockViewGroup;
import com.act.quzhibo.custom.SelfDialog;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import permission.auron.com.marshmallowpermissionhelper.ActivityManagePermission;
import permission.auron.com.marshmallowpermissionhelper.PermissionResult;
import permission.auron.com.marshmallowpermissionhelper.PermissionUtils;

public class WelcomeActivity extends ActivityManagePermission {

    private String plateListStr;
    RootUser user;
    private LockIndicatorView mLockIndicator;
    private LockViewGroup mLockViewGroup;
    private TextView mTvTips;
    private LinearLayout secretView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        secretView = (LinearLayout) findViewById(R.id.secret_view);

        mLockIndicator = (LockIndicatorView) findViewById(R.id.indicator);

        mTvTips = (TextView) findViewById(R.id.tv_tips);

        mLockViewGroup = (LockViewGroup) findViewById(R.id.lockgroup);
        grantPermission();

    }

    private void initData() {


        String[] passWords = user.secretPassword.split(";");

        int[] nums = new int[passWords.length];
        for (int i = 0; i < passWords.length; i++) {
            nums[i] = Integer.parseInt(passWords[i]);
        }

        mLockViewGroup.setAnswer(nums);

        // 设置尝试次数
        mLockViewGroup.setMaxTryTimes(5);

        mLockViewGroup.setOnLockListener(new LockViewGroup.OnLockListener() {

            @Override
            public void onLockSelected(int id) {
            }

            @Override
            public void onLess4Points() {
                mLockViewGroup.clear2ResetDelay(1200L); //清除错误

                mTvTips.setTextColor(Color.RED);
                mTvTips.setText("至少连接4个点 , 请重新输入");
            }

            @Override
            public void onSaveFirstAnswer(int[] answer) {
                mTvTips.setTextColor(Color.GRAY);
                mTvTips.setText("再次绘制 , 确认解锁图案");
                // 设置给指示器view
                mLockIndicator.setAnswer(answer);
            }

            @Override
            public void onSucessed(int[] answers) {
                mTvTips.setTextColor(Color.BLACK);
                mTvTips.setText("验证成功");
                secretView.setVisibility(View.GONE);
                secretView.setAnimation(AnimationUtils.makeOutAnimation(WelcomeActivity.this, true));
                request();
            }

            @Override
            public void onFailed(int mTryTimes) {
                mLockViewGroup.clear2ResetDelay(1400L); //清除错误
                mLockViewGroup.setHapticFeedbackEnabled(true); //手机振动
                mTvTips.setTextColor(Color.RED);
                mTvTips.setText("与上一次绘制不一致 , 请重新绘制");

                if (mTryTimes > 0) {
                    Toast.makeText(WelcomeActivity.this, "剩余尝试机会: " + mTryTimes + " 次", Toast.LENGTH_SHORT).show();
                } else {
                    ToastUtil.showToast(getApplicationContext(), "设置失败");
                    finish();
                }

                // 左右移动动画
                Animation shakeAnimation = AnimationUtils.loadAnimation(WelcomeActivity.this, R.anim.shake);
                mTvTips.startAnimation(shakeAnimation);

            }

            @Override
            public void onSetAnswerInit() {
                mTvTips.setText("绘制解锁图案");
            }
        });

    }

    private void grantPermission() {
        final SelfDialog selfDialog = new SelfDialog(WelcomeActivity.this);
        askCompactPermissions(new String[]{PermissionUtils.Manifest_CAMERA, PermissionUtils.Manifest_RECORD_AUDIO, PermissionUtils.Manifest_ACCESS_COARSE_LOCATION, PermissionUtils.Manifest_ACCESS_FINE_LOCATION, PermissionUtils.Manifest_WRITE_EXTERNAL_STORAGE}, new PermissionResult() {
            @Override
            public void permissionGranted() {
                doRequest();
            }

            @Override
            public void permissionDenied() {
                selfDialog.setTitle("同意权限后才能正常使用哦");
                selfDialog.setMessage("您需要同意以下权限:\n   1. 访问设备上的照片\n   2. 访问手机存储功能\n   3. 获取您的大概位置\n   4. 拍照\n   5. 录像");
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
                selfDialog.setMessage("您需要同意以下权限:\n   1. 访问设备上的照片\n   2. 访问手机存储功能\n   3. 获取您的大概位置\n   4. 拍照\n   5. 录像");
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

        user = BmobUser.getCurrentUser(RootUser.class);
        if (user != null) {
            CommonUtil.fecth(this);
            if (user.secretScan) {
                secretView.setVisibility(View.VISIBLE);
                initData();
            } else {
                secretView.setVisibility(View.GONE);
                request();
            }
        } else {
            secretView.setVisibility(View.GONE);
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
                            ToastUtil.showToast(getApplicationContext(), "网络异常,正在重试");
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