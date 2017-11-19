package com.act.quzhibo.ui.activity;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TabHost;

import com.act.quzhibo.R;
import com.act.quzhibo.bean.RootUser;
import com.act.quzhibo.common.Constants;
import com.act.quzhibo.event.ChangeEvent;
import com.act.quzhibo.event.CourseEvent;
import com.act.quzhibo.util.ToastUtil;
import com.act.quzhibo.widget.SelfDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

@SuppressWarnings("deprecation")
public class CourseCommonActivity extends TabActivity {
    TabHost tabHost;
    SelfDialog selfDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabmain);
        findViewById(R.id.tabMain).setVisibility(View.GONE);
        tabHost = CourseCommonActivity.this.getTabHost();
        tabHost.addTab(tabHost.newTabSpec("泡妞")
                .setIndicator(null, null)
                .setContent(new Intent(CourseCommonActivity.this, PuaCoursesActivity.class)));
        tabHost.addTab(tabHost.newTabSpec("赚钱")
                .setIndicator(null, null)
                .setContent(new Intent(CourseCommonActivity.this, MoneyCourseActivity.class)));
        tabHost.setCurrentTab(0);
        EventBus.getDefault().register(this);
        selfDialog = new SelfDialog(CourseCommonActivity.this, false);
        selfDialog.setTitle("客官再看一会儿呗");
        selfDialog.setMessage("还是留下来再看看吧");
        selfDialog.setYesOnclickListener("再欣赏下", new SelfDialog.onYesOnclickListener() {
            @Override
            public void onYesClick() {
                selfDialog.dismiss();

            }
        });
        selfDialog.setNoOnclickListener("有事要忙", new SelfDialog.onNoOnclickListener() {
            @Override
            public void onNoClick() {
                RootUser user = new RootUser();
                if (user != null) {
                    user.lastLoginTime = System.currentTimeMillis() + "";
                    user.update(BmobUser.getCurrentUser(RootUser.class).getObjectId(), new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if (e == null) {
                                selfDialog.dismiss();
                                ToastUtil.showToast(CourseCommonActivity.this, "退出时间" + System.currentTimeMillis());
                                finish();
                            }else {
                                finish();
                            }
                        }
                    });
                }else {
                    finish();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && !selfDialog.isShowing()) {
            selfDialog.show();
        }
        return true;
    }

    @Subscribe
    public void onEventMain(ChangeEvent event) {
        if ("pua".equals(event.type)) {
            tabHost.setCurrentTab(1);
        } else {
            tabHost.setCurrentTab(0);
        }
    }

    @Subscribe
    public void onEventMain(CourseEvent event) {
        Intent intent = new Intent(this, CourseDetailActivity.class);
        intent.putExtra(Constants.COURSE, event.course);
        startActivity(intent);

    }

}

