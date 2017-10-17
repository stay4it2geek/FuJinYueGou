package com.act.quzhibo.ui.activity;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.act.quzhibo.R;
import com.act.quzhibo.db.NewFriendManager;
import com.act.quzhibo.bean.RootUser;
import com.act.quzhibo.event.RefreshEvent;
import com.act.quzhibo.ui.fragment.BackHandledFragment;
import com.act.quzhibo.ui.fragment.ContactFragment;
import com.act.quzhibo.ui.fragment.ConversationFragment;
import com.act.quzhibo.util.IMMLeaks;
import com.act.quzhibo.util.ToastUtil;
import com.act.quzhibo.view.FragmentDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.newim.core.ConnectionStatus;
import cn.bmob.newim.event.MessageEvent;
import cn.bmob.newim.event.OfflineMessageEvent;
import cn.bmob.newim.listener.ConnectListener;
import cn.bmob.newim.listener.ConnectStatusChangeListener;
import cn.bmob.newim.notification.BmobNotificationManager;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import me.leefeng.promptlibrary.PromptDialog;


public class ChatFriendsActivity extends FragmentActivity implements BackHandledFragment.BackHandledInterface {

    private MyPagerAdapter mAdapter;
    private BackHandledFragment mBackHandedFragment;
    private ViewPager viewPager;



    @Bind(R.id.btn_conversation)
    TextView btn_conversation;
    @Bind(R.id.btn_contact)
    TextView btn_contact;

    @Bind(R.id.tv_conversation_tips)
    TextView tv_conversation_tips;
    @Bind(R.id.tv_contact_tips)
    TextView tv_contact_tips;

    private TextView[] mTabs;
    private ConversationFragment conversationFragment;
    ContactFragment contactFragment;
    private Fragment[] fragments;
    private int index;
    private int currentTabIndex;
    RootUser user;
    PromptDialog promptDialog;


    @Override
    protected void onDestroy() {
        super.onDestroy();
        //清理导致内存泄露的资源
        BmobIM.getInstance().clear();
        EventBus.getDefault().unregister(this);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_im_main);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        initTab();
        user = BmobUser.getCurrentUser(RootUser.class);
        promptDialog = new PromptDialog(this);
        if (user == null) {
            promptDialog.dismissImmediately();
            return;
        } else {
            //TODO 连接：3.1、登录成功、注册成功或处于登录状态重新打开应用后执行连接IM服务器的操作
            if (!TextUtils.isEmpty(user.getObjectId())) {
                BmobIM.connect(user.getObjectId(), new ConnectListener() {
                    @Override
                    public void done(String uid, BmobException e) {
                        if (e == null) {
                            //TODO 连接成功后再进行修改本地用户信息的操作，并查询本地用户信息
                            EventBus.getDefault().post(new RefreshEvent());
                            //服务器连接成功就发送一个更新事件，同步更新会话及主页的小红点
                            //TODO 会话：3.6、更新用户资料，用于在会话页面、聊天页面以及个人信息页面显示
                            BmobIM.getInstance().
                                    updateUserInfo(new BmobIMUserInfo(user.getObjectId(),
                                            user.getUsername(), user.photoFileUrl));

                        } else {
                            ToastUtil.showToast(ChatFriendsActivity.this, e.getMessage());
                        }
                    }
                });

                promptDialog.showLoading("正在通讯中");
                //TODO 连接：3.3、监听连接状态，可通过BmobIM.getInstance().getCurrentStatus()来获取当前的长连接状态
                BmobIM.getInstance().setOnConnectStatusChangeListener(new ConnectStatusChangeListener() {
                    @Override
                    public void onChange(ConnectionStatus status) {
                        ToastUtil.showToast(ChatFriendsActivity.this, status.getMsg());
                        if (!status.getMsg().equals("connecting")) {
                            promptDialog.dismissImmediately();
                        }
                    }
                });
            }
        }
        IMMLeaks.fixFocusedViewLeak(getApplication());
    }


    private void initTab() {
        mTabs = new TextView[2];
        mTabs[0] = btn_conversation;
        mTabs[1] = btn_contact;
        mTabs[0].setSelected(true);
        conversationFragment = new ConversationFragment();
        contactFragment = new ContactFragment();
        fragments = new Fragment[]{conversationFragment, contactFragment};
        mAdapter = new MyPagerAdapter(getSupportFragmentManager());
        viewPager = ((ViewPager) findViewById(R.id.viewpager));
        viewPager.setCurrentItem(0);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                viewPager.setCurrentItem(position);
                onTabIndex(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        viewPager.setAdapter(mAdapter);
    }

    public void onTabSelect(View view) {
        switch (view.getId()) {
            case R.id.btn_conversation:
                index = 0;
                break;
            case R.id.btn_contact:
                index = 1;
                break;

        }
        onTabIndex(index);
    }


    private void onTabIndex(int index) {
        if (currentTabIndex != index) {
            mTabs[currentTabIndex].setSelected(false);
            mTabs[index].setSelected(true);
        }
        currentTabIndex = index;
        viewPager.setCurrentItem(index);
    }

    @Override
    protected void onResume() {
        super.onResume();
        user = BmobUser.getCurrentUser(RootUser.class);
        //每次进来应用都检查会话和好友请求的情况
        if (user != null) {
            if (!BmobIM.getInstance().getCurrentStatus().getMsg().equals("connected")) {
                BmobIM.connect(user.getObjectId(), new ConnectListener() {
                    @Override
                    public void done(String uid, BmobException e) {
                        if (e == null) {
                            //TODO 连接成功后再进行修改本地用户信息的操作，并查询本地用户信息
                            EventBus.getDefault().post(new RefreshEvent());
                            //服务器连接成功就发送一个更新事件，同步更新会话及主页的小红点
                            //TODO 会话：3.6、更新用户资料，用于在会话页面、聊天页面以及个人信息页面显示
                            BmobIM.getInstance().updateUserInfo(new BmobIMUserInfo(user.getObjectId(), user.getUsername(), user.photoFileUrl));

                        } else {
                            ToastUtil.showToast(ChatFriendsActivity.this, e.getMessage());
                        }
                    }
                });
                checkRedPoint();
                //进入应用后，通知栏应取消
                BmobNotificationManager.getInstance(this).cancelNotification();
            }
            checkRedPoint();
            //进入应用后，通知栏应取消
            BmobNotificationManager.getInstance(this).cancelNotification();
        }
    }


    private class MyPagerAdapter extends FragmentPagerAdapter {
        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return fragments.length;
        }

        @Override
        public Fragment getItem(int position) {
            return fragments[position];
        }
    }

    @Override
    public void setSelectedFragment(BackHandledFragment selectedFragment) {
        this.mBackHandedFragment = selectedFragment;
    }


    @Override
    public void onBackPressed() {
        if (mBackHandedFragment == null || !mBackHandedFragment.onBackPressed()) {
            if (getSupportFragmentManager().getBackStackEntryCount() == 0) {

                FragmentDialog.newInstance(false, "客官再看一会儿呗", "还是留下来再看看吧", "再欣赏下", "有事要忙", "", "", false, new FragmentDialog.OnClickBottomListener() {
                    @Override
                    public void onPositiveClick(Dialog dialog, boolean needDelete) {
                        dialog.dismiss();
                    }

                    @Override
                    public void onNegtiveClick(Dialog dialog) {
                        dialog.dismiss();
                        ChatFriendsActivity.this.finish();
                    }
                }).show(getSupportFragmentManager(), "");
            }
        } else {
            getSupportFragmentManager().popBackStack();
        }
    }

    /**
     * 注册消息接收事件
     *
     * @param event
     */
    //TODO 消息接收：8.3、通知有在线消息接收
    @Subscribe
    public void onEventAsync(MessageEvent event) {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                checkRedPoint();
            }
        });
    }

    /**
     * 注册离线消息接收事件
     *
     * @param event
     */
    //TODO 消息接收：8.4、通知有离线消息接收
    @Subscribe
    public void onEventAsync(OfflineMessageEvent event) {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                checkRedPoint();
            }
        });
    }

    /**
     * 注册自定义消息接收事件
     *
     * @param event
     */
    //TODO 消息接收：8.5、通知有自定义消息接收
    @Subscribe
    public void onEventAsync(RefreshEvent event) {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                checkRedPoint();
            }
        });
    }

    /**
     *
     */
    private void checkRedPoint() {
        //TODO 会话：4.4、获取全部会话的未读消息数量
        int count = (int) BmobIM.getInstance().getAllUnReadCount();
        if (count > 0) {
            tv_conversation_tips.setVisibility(View.VISIBLE);
            tv_conversation_tips.setText(count + "");
        } else {
            tv_conversation_tips.setVisibility(View.GONE);
        }
        //TODO 好友管理：是否有好友添加的请求
        if (NewFriendManager.getInstance(this).hasNewFriendInvitation()) {
            tv_contact_tips.setVisibility(View.VISIBLE);
            tv_contact_tips.setText(count + "1");
        } else {
            tv_contact_tips.setVisibility(View.GONE);
        }
    }

}
