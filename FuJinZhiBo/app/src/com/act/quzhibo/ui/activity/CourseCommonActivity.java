package com.act.quzhibo.ui.activity;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TabHost;

import com.act.quzhibo.R;
import com.act.quzhibo.common.Constants;
import com.act.quzhibo.event.ChangeEvent;
import com.act.quzhibo.event.CourseEvent;
import com.act.quzhibo.widget.SelfDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;


@SuppressWarnings("deprecation")
public class CourseCommonActivity extends TabActivity {


    private TabHost tabHost;

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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }


    @Override
    public void onBackPressed() {
        final SelfDialog selfDialog = new SelfDialog(CourseCommonActivity.this, false);
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
                selfDialog.dismiss();
                finish();
            }
        });
        selfDialog.show();
    }

    /**
     * 注册消息接收事件
     *
     * @param event
     */
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

