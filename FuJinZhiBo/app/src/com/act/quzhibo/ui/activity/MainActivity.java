package com.act.quzhibo.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.act.quzhibo.R;
import com.act.quzhibo.base.BaseActivity;
import com.act.quzhibo.db.NewFriendManager;
import com.act.quzhibo.entity.RootUser;
import com.act.quzhibo.event.RefreshEvent;
import com.act.quzhibo.ui.fragment.ContactFragment;
import com.act.quzhibo.ui.fragment.ConversationFragment;
import com.act.quzhibo.util.IMMLeaks;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.Bind;
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


public class MainActivity extends BaseActivity {

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_im_main);
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
                            toast(e.getMessage());
                        }
                    }
                });

                promptDialog.showLoading("正在通讯中");
                //TODO 连接：3.3、监听连接状态，可通过BmobIM.getInstance().getCurrentStatus()来获取当前的长连接状态
                BmobIM.getInstance().setOnConnectStatusChangeListener(new ConnectStatusChangeListener() {
                    @Override
                    public void onChange(ConnectionStatus status) {
                        toast(status.getMsg());
                        if (!status.getMsg().equals("connecting")) {
                            promptDialog.dismissImmediately();
                        }
                    }
                });
            }
        }
        //解决leancanary提示InputMethodManager内存泄露的问题
        IMMLeaks.fixFocusedViewLeak(getApplication());
    }


    @Override
    protected void initView() {
        super.initView();
        mTabs = new TextView[2];
        mTabs[0] = btn_conversation;
        mTabs[1] = btn_contact;
        mTabs[0].setSelected(true);
        initTab();
    }

    private void initTab() {
        conversationFragment = new ConversationFragment();
        contactFragment = new ContactFragment();
        fragments = new Fragment[]{conversationFragment, contactFragment};
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, conversationFragment)
                .add(R.id.fragment_container, contactFragment)
                .hide(contactFragment)
                .show(conversationFragment).commit();
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
            FragmentTransaction trx = getSupportFragmentManager().beginTransaction();
            trx.hide(fragments[currentTabIndex]);
            if (!fragments[index].isAdded()) {
                trx.add(R.id.fragment_container, fragments[index]);
            }
            trx.show(fragments[index]).commit();
        }
        mTabs[currentTabIndex].setSelected(false);
        mTabs[index].setSelected(true);
        currentTabIndex = index;
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
                            toast(e.getMessage());
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //清理导致内存泄露的资源
        BmobIM.getInstance().clear();
    }

    /**
     * 注册消息接收事件
     *
     * @param event
     */
    //TODO 消息接收：8.3、通知有在线消息接收
    @Subscribe
    public void onEventMainThread(MessageEvent event) {
        checkRedPoint();
    }

    /**
     * 注册离线消息接收事件
     *
     * @param event
     */
    //TODO 消息接收：8.4、通知有离线消息接收
    @Subscribe
    public void onEventMainThread(OfflineMessageEvent event) {
        checkRedPoint();
    }

    /**
     * 注册自定义消息接收事件
     *
     * @param event
     */
    //TODO 消息接收：8.5、通知有自定义消息接收
    @Subscribe
    public void onEventMainThread(RefreshEvent event) {
        checkRedPoint();
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
            tv_contact_tips.setText(count + "");

        } else {
            tv_contact_tips.setVisibility(View.GONE);
        }
    }

}
