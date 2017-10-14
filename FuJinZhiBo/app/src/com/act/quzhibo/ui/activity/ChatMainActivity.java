package com.act.quzhibo.ui.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.act.quzhibo.R;
import com.act.quzhibo.db.NewFriendManager;
import com.act.quzhibo.entity.RootUser;
import com.act.quzhibo.event.RefreshEvent;
import com.act.quzhibo.ui.fragment.ContactFragment;
import com.act.quzhibo.ui.fragment.ConversationFragment;
import com.act.quzhibo.ui.fragment.NoDataViewFragment;
import com.act.quzhibo.util.IMMLeaks;
import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

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

public class ChatMainActivity extends TabSlideDifferentBaseActivity {
    RootUser user;
    private PromptDialog promptDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        promptDialog = new PromptDialog(ChatMainActivity.this);
        //解决leancanary提示InputMethodManager内存泄露的问题
        IMMLeaks.fixFocusedViewLeak(getApplication());
    }

    @Override
    public boolean getIsMineActivityType() {
        return false;
    }

    @Override
    protected boolean isNeedShowBackDialog() {
        return true;
    }

    @Override
    protected String[] getTitles() {
        return new String[]{"会话", "好友"};
    }

    @Override
    protected ArrayList<Fragment> getFragments() {
        ArrayList<Fragment> fragments = new ArrayList<>();

            ConversationFragment conversationFragment = new ConversationFragment();
            ContactFragment contactFragment = new ContactFragment();
            fragments.add(conversationFragment);
            fragments.add(contactFragment);


        return fragments;
    }

    @Override
    protected void onResume() {
        super.onResume();
        user = BmobUser.getCurrentUser(RootUser.class);
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

        //每次进来应用都检查会话和好友请求的情况
        checkRedPoint();
        //进入应用后，通知栏应取消
        BmobNotificationManager.getInstance(this).cancelNotification();
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
//            iv_conversation_tips.setVisibility(View.VISIBLE);
        } else {
//            iv_conversation_tips.setVisibility(View.GONE);
        }

        //TODO 好友管理：是否有好友添加的请求
        if (NewFriendManager.getInstance(this).hasNewFriendInvitation()) {
//            iv_contact_tips.setVisibility(View.VISIBLE);
        } else {
//            iv_contact_tips.setVisibility(View.GONE);
        }
    }

}
