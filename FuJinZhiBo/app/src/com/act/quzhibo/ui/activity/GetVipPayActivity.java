package com.act.quzhibo.ui.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.act.quzhibo.R;
import com.act.quzhibo.common.Constants;
import com.act.quzhibo.bean.PayConis;
import com.act.quzhibo.bean.RootUser;
import com.act.quzhibo.bean.Orders;
import com.act.quzhibo.util.CommonUtil;
import com.act.quzhibo.util.ToastUtil;
import com.act.quzhibo.widget.FragmentDialog;
import com.act.quzhibo.widget.LoadNetView;
import com.act.quzhibo.widget.MyListView;
import com.act.quzhibo.widget.TitleBarView;

import java.util.ArrayList;
import java.util.List;

import c.b.BP;
import c.b.PListener;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;


public class GetVipPayActivity extends FragmentActivity {
    ProgressDialog dialog;
    String mGoodsDescription;
    TextView userSelectText;
    Orders Orders;
    boolean hasShow;
    LoadNetView loadNetView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_vip_pay);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
        }
        TitleBarView titlebar = (TitleBarView) findViewById(R.id.titlebar);
        titlebar.setBarTitle("VIP会员中心");
        titlebar.setBackButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetVipPayActivity.this.finish();
            }
        });
        Orders = new Orders();
        findViewById(R.id.alipay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alipay();
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
//        if (!TextUtils.isEmpty(Orders.orderStatus) && Orders.orderStatus.equals("1") && !hasShow) {
//            hideDialog();
//            FragmentDialog.newInstance(false, "尊敬的用户您好,您" + mGoodsDescription + "已支付成功!", "", "我知道了!", "", "", "", true, new FragmentDialog.OnClickBottomListener() {
//                @Override
//                public void onPositiveClick(Dialog dialog, boolean needDelete) {
//                    dialog.dismiss();
//                    GetVipPayActivity.this.finish();
//                }
//
//                @Override
//                public void onNegtiveClick(Dialog dialog) {
//                    dialog.dismiss();
//                }
//            }).show(getSupportFragmentManager(), "dialog");
//            hasShow = true;
//        }
    }

    RootUser rootUser = BmobUser.getCurrentUser(RootUser.class);
    RootUser updateUser = new RootUser();

    void alipay() {
        if (rootUser == null) {
            ToastUtil.showToast(GetVipPayActivity.this, "您还没有登录或注册哦!");
            startActivity(new Intent(GetVipPayActivity.this, LoginActivity.class));
            return;
        } else {
            CommonUtil.fecth(this);
        }
        if (TextUtils.isEmpty(userSelectText.getText())) {
            ToastUtil.showToast(GetVipPayActivity.this, "您还没有选择充值种类哦!");
            return;
        }
        if (!checkPackageInstalled("com.eg.android.AlipayGphone", "https://mobile.alipay.com/index.htm")) {
            ToastUtil.showToast(GetVipPayActivity.this, "您还没安装支付宝客户端哦!");
            return;
        }
        updateUser.lastLoginTime = System.currentTimeMillis() + "";
        updateUser.update(rootUser.getObjectId(), new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e != null) {
                    if (e.getErrorCode() == 206) {
                        ToastUtil.showToast(GetVipPayActivity.this, "缓存已过期，请重新登录后修改" + e.getLocalizedMessage());
                        BmobUser.logOut();
                        startActivity(new Intent(GetVipPayActivity.this, LoginActivity.class));
                    }
                }
            }
        });
        showDialog("正在生成订单，请您稍候...");
        BP.pay(mGoodsDescription, mGoodsDescription, 0.01, true, new PListener() {
            // 因为网络等原因,支付结果未知(小概率事件),出于保险起见稍后手动查询
            @Override
            public void unknow() {
                ToastUtil.showToast(GetVipPayActivity.this, "支付结果未知,请您稍后手动查询");
                hideDialog();
            }

            // 支付成功,如果金额较大请手动查询确认
            @Override
            public void succeed() {
                hideDialog();
                Orders.orderStatus = "1";
                Orders.update(Orders.getObjectId(), new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if (e == null) {
                            ToastUtil.showToast(GetVipPayActivity.this, "订单充值支付信息更新成功");
                            if (rootUser.vipConis > 0) {
                                updateUser.vipConis = mPayConisCount + rootUser.vipConis;
                            }
                            updateUser.update(rootUser.getObjectId(), new UpdateListener() {
                                @Override
                                public void done(BmobException e) {
                                    if (e == null) {
                                        ToastUtil.showToast(GetVipPayActivity.this, "趣币信息更新成功，您还有" + updateUser.vipConis + "趣币");
                                    } else {
                                        ToastUtil.showToast(GetVipPayActivity.this, "趣币信息更新失败，原因是:" + e.getErrorCode());
                                    }
                                }
                            });
                            CommonUtil.fecth(GetVipPayActivity.this);
                        } else {
                            ToastUtil.showToast(GetVipPayActivity.this, "订单充值支付信息更新失败：" + e.getLocalizedMessage());
                        }
                    }
                });


            }

            // 无论成功与否,返回订单号
            @Override
            public void orderId(String orderId) {
                Orders.orderId = orderId;
//                Orders.orderPrice = mPayMoney + "";
//                Orders.goodsDescription = mGoodsDescription;
                Orders.save(new SaveListener<String>() {
                    @Override
                    public void done(String objectId, BmobException e) {
                        if (e == null) {
                            showDialog("生成订单成功!请等待跳转到支付页面");
                        } else {
                            ToastUtil.showToast(GetVipPayActivity.this, "添加订单数据失败：" + e.getLocalizedMessage());
                        }
                    }
                });
            }

            // 支付失败,原因可能是用户中断支付操作,也可能是网络原因
            @Override
            public void fail(int code, final String reason) {
                Orders.delete(Orders.getObjectId(), new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if (e == null) {
                            ToastUtil.showToast(GetVipPayActivity.this, "交易关闭!原因是" + reason);
                        }
                    }
                });
                hideDialog();
            }
        });
    }


    void showDialog(String message) {
        try {
            if (dialog == null) {
                dialog = new ProgressDialog(this);
                dialog.setCancelable(true);
            }
            dialog.setMessage(message);
            dialog.show();
        } catch (Exception e) {
            // 在其他线程调用dialog会报错
        }
    }

    void hideDialog() {
        if (dialog != null && dialog.isShowing())
            try {
                dialog.dismiss();
            } catch (Exception e) {
            }
    }

    boolean checkPackageInstalled(String packageName, String browserUrl) {
        try {
            // 检查是否有支付宝客户端
            getPackageManager().getPackageInfo(packageName, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            // 没有安装支付宝，跳转到应用市场
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("market://details?id=" + packageName));
                startActivity(intent);
            } catch (Exception ee) {// 连应用市场都没有，用浏览器去支付宝官网下载
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(browserUrl));
                    startActivity(intent);
                } catch (Exception eee) {
                    ToastUtil.showToast(GetVipPayActivity.this, "您的手机上没有没有应用市场也没有浏览器，请您去想办法安装支付宝/微信吧");
                }
            }
        }
        return false;
    }


    Integer mPayConisCount;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            final ArrayList<PayConis> payConises = (ArrayList<PayConis>) msg.obj;
            if (payConises == null) {
                return;
            }
        }
    };
}

