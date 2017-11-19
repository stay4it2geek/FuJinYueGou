package com.act.quzhibo.ui.activity;


import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;

import com.act.quzhibo.R;
import com.act.quzhibo.bean.RootUser;
import com.act.quzhibo.widget.FragmentDialog;
import com.act.quzhibo.widget.TitleBarView;
import com.elbbbird.android.socialsdk.SocialSDK;
import com.elbbbird.android.socialsdk.model.SocialShareScene;
import com.elbbbird.android.socialsdk.otto.BusProvider;
import com.elbbbird.android.socialsdk.otto.ShareBusEvent;
import com.squareup.otto.Subscribe;

import cn.bmob.v3.BmobUser;
import me.leefeng.promptlibrary.PromptButton;
import me.leefeng.promptlibrary.PromptButtonListener;
import me.leefeng.promptlibrary.PromptDialog;

public class ShareForMoneyActivity extends FragmentActivity {

     PromptDialog promptDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.money_introduce);
        TitleBarView titlebar = (TitleBarView) findViewById(R.id.titlebar);
        titlebar.setVisibility(View.VISIBLE);
        titlebar.setBarTitle("如何赚钱");
        titlebar.setBackButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareForMoneyActivity.this.finish();
            }
        });
        promptDialog = new PromptDialog(this);
        final Button moneywayBtn = (Button) findViewById(R.id.moneyway_btn);
        moneywayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectShareWay();
            }
        });

        BusProvider.getInstance().register(this);
    }

    private SocialShareScene scene = new SocialShareScene(0, "情趣社区购", SocialShareScene.SHARE_TYPE_WECHAT, "Android 开源社会化登录 SDK，支持微信，微博， QQ",
            "像友盟， ShareSDK 等平台也提供类似的 SDK ，之所以造轮子是因为这些平台的 SDK 内部肯定会带有数据统计功能，不想给他们共享数据。",
            "http://cdn.v2ex.co/gravatar/becb0d5c59469a34a54156caef738e90?s=73&d=retro", "http://www.v2ex.com/t/238165");

    private void selectShareWay() {
        if (BmobUser.getCurrentUser(RootUser.class) == null) {
            FragmentDialog.newInstance(false, "登录提示", "中级趣会员才能推广赚钱哦", "登录", "注册", "", "", false, new FragmentDialog.OnClickBottomListener() {
                @Override
                public void onPositiveClick(Dialog dialog, boolean deleteFileSource) {
                    startActivity(new Intent(ShareForMoneyActivity.this, LoginActivity.class));
                }

                @Override
                public void onNegtiveClick(Dialog dialog) {
                    startActivity(new Intent(ShareForMoneyActivity.this, RegisterActivity.class));
                }
            }).show(getSupportFragmentManager(),"");
            return;
        }
        promptDialog.getAlertDefaultBuilder().sheetCellPad(5).round(10);
        if (BmobUser.getCurrentUser(RootUser.class).vipConis < 3000) {
            promptDialog.showWarn("您趣币不足,不是中级趣会员以上级别", true);
            return;
        }
        promptDialog.getAlertDefaultBuilder().sheetCellPad(5).round(10);
        PromptButton cancle = new PromptButton("取消", new PromptButtonListener() {
            @Override
            public void onClick(PromptButton promptButton) {
                promptDialog.dismiss();
            }
        });

        PromptButton btnQrcode = new PromptButton("分享到陌陌、探探、遇见", new PromptButtonListener() {
            @Override
            public void onClick(PromptButton promptButton) {
//                public static Bitmap syncEncodeQRCode(String content, int size, int foregroundColor, Bitmap logo)
                promptDialog.dismiss();
            }
        });
        PromptButton btnQQShare = new PromptButton("分享到老司机QQ群", new PromptButtonListener() {
            @Override
            public void onClick(PromptButton promptButton) {
                SocialSDK.setDebugMode(false);
                SocialSDK.shareToQQ(ShareForMoneyActivity.this, "1104664609", scene);
                promptDialog.dismiss();
            }
        });

        btnQQShare.setTextColor(getResources().getColor(R.color.darkgray));
        cancle.setTextColor(Color.parseColor("#0076ff"));
        promptDialog.showAlertSheet("", true, cancle, btnQQShare, btnQrcode);
    }


    @Subscribe
    public void onShareResult(ShareBusEvent event) {
        switch (event.getType()) {
            case ShareBusEvent.TYPE_SUCCESS:
//                Log.i(TAG, "onShareResult#ShareBusEvent.TYPE_SUCCESS " + event.getId());
                break;
            case ShareBusEvent.TYPE_FAILURE:
                Exception e = event.getException();
//                Log.e("TAG", "onShareResult#ShareBusEvent.TYPE_FAILURE " + e.toString());
                break;

            case ShareBusEvent.TYPE_CANCEL:
//                Log.i(TAG, "onShareResult#ShareBusEvent.TYPE_CANCEL");
                break;
        }
    }

    @Override
    protected void onDestroy() {
        BusProvider.getInstance().unregister(this);
        super.onDestroy();
    }

}
