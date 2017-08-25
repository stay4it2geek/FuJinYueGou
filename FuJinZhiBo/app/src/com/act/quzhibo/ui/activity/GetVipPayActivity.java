package com.act.quzhibo.ui.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.act.quzhibo.R;
import com.act.quzhibo.entity.PayConis;
import com.act.quzhibo.entity.RootUser;
import com.act.quzhibo.entity.VipOrders;
import com.act.quzhibo.util.CommonUtil;
import com.act.quzhibo.util.ToastUtil;
import com.act.quzhibo.view.FragmentDialog;
import com.act.quzhibo.view.TitleBarView;
import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;

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

import static android.widget.AbsListView.CHOICE_MODE_SINGLE;


public class GetVipPayActivity extends FragmentActivity {
    private TextView alipay;
    private ProgressDialog dialog;
    private String mGoodsDescription;
    ArrayList<PayConis> payConisList = new ArrayList<>();
    private double mPayMoney = 0.01;
    private TextView userSelectText;
    private VipOrders vipOrders;
    private SwipeMenuListView listView;
    private MyAdapter mAdapter;
    boolean hasShow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_vip_pay);
        //解决Android 7.0 FileUriExposedException url异常
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
        }
        TitleBarView titlebar = (TitleBarView) findViewById(R.id.titlebar);
        titlebar.setBarTitle(" 充值趣币,享受VIP特权");
        titlebar.setBackButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetVipPayActivity.this.finish();
            }
        });
        vipOrders = new VipOrders();
        listView = (SwipeMenuListView) findViewById(R.id.viplist);
        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                // create "open" item
                SwipeMenuItem openItem = new SwipeMenuItem(getApplicationContext());
                openItem.setBackground(new ColorDrawable(Color.RED));
                openItem.setWidth(300);
                openItem.setTitle("选择");
                openItem.setTitleSize(18);
                openItem.setTitleColor(Color.WHITE);
                menu.addMenuItem(openItem);

            }
        };

        listView.setMenuCreator(creator);
        listView.setChoiceMode(CHOICE_MODE_SINGLE);
        listView.setSwipeDirection(SwipeMenuListView.DIRECTION_LEFT);

        alipay = (TextView) findViewById(R.id.alipay);
        userSelectText = (TextView) findViewById(R.id.userSelectText);


        alipay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alipay();
            }
        });

        queryData();
    }

    class MyAdapter extends BaseAdapter {
        ArrayList<PayConis> payConises;
        Context context;
        LayoutInflater inflater;

        public MyAdapter(Context context, ArrayList<PayConis> payConises) {
            super();
            this.context = context;
            this.payConises = payConises;
            inflater = LayoutInflater.from(context);

        }

        @Override
        public int getCount() {
            return payConises.size();
        }

        @Override
        public PayConis getItem(int position) {
            return payConises.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            convertView = inflater.inflate(R.layout.item_buyvip, null, false);
            TextView title = (TextView) convertView.findViewById(R.id.payConisCount);
            title.setText(payConises.get(position).payConisCount + "趣币");
            TextView price_vip = (TextView) convertView.findViewById(R.id.priceConis);
            price_vip.setText(payConises.get(position).priceConis + "元");
            TextView conisPresent = (TextView) convertView.findViewById(R.id.conisPresent);
            conisPresent.setText("赠送" + payConises.get(position).conisPresent + "趣币");
            TextView price_vip_market = (TextView) convertView.findViewById(R.id.price_vip_maket);
            price_vip_market.setText(payConises.get(position).price_vip_maket + "元");
            ((TextView) convertView.findViewById(R.id.price_vip_maket)).getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);

            return convertView;
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (vipOrders.orderStatus && !hasShow) {
            hideDialog();
            FragmentDialog.newInstance("", "尊敬的用户您好,您" + mGoodsDescription + "已支付成功!", "我知道了!", "", -1, true, new FragmentDialog.OnClickBottomListener() {
                @Override
                public void onPositiveClick(Dialog dialog) {
                    dialog.dismiss();
                    GetVipPayActivity.this.finish();
                }

                @Override
                public void onNegtiveClick(Dialog dialog) {
                    dialog.dismiss();
                }
            }).show(getSupportFragmentManager(), "dialog");
            hasShow = true;
        }
    }

    RootUser rootUser = BmobUser.getCurrentUser(RootUser.class);
    RootUser updateUser = new RootUser();

    private void alipay() {
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
                        startActivity(new Intent(GetVipPayActivity.this,LoginActivity.class));
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
                vipOrders.orderStatus = true;
                vipOrders.update(vipOrders.getObjectId(), new UpdateListener() {
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
                vipOrders.orderId = orderId;
                vipOrders.orderPrice = mPayMoney + "";
                vipOrders.goodsDescription = mGoodsDescription;
                vipOrders.save(new SaveListener<String>() {
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
                vipOrders.delete(vipOrders.getObjectId(), new UpdateListener() {
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

    private boolean checkPackageInstalled(String packageName, String browserUrl) {
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


    private void queryData() {
        final BmobQuery<PayConis> query = new BmobQuery<>();
        query.order("-payConisCount");
        query.findObjects(new FindListener<PayConis>() {
            @Override
            public void done(List<PayConis> list, BmobException e) {
                if (e == null) {
                    if (list.size() > 0) {
                        payConisList.addAll(list);
                        Message message = new Message();
                        message.obj = payConisList;
                        handler.sendMessage(message);
                    }
                }
            }
        });
    }

    private Integer mPayConisCount;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            final ArrayList<PayConis> payConises = (ArrayList<PayConis>) msg.obj;
            if (payConises == null) {
                return;
            }
            if (mAdapter == null) {
                listView.setAdapter(mAdapter = new MyAdapter(GetVipPayActivity.this, payConises));
                vipOrders.user = BmobUser.getCurrentUser(RootUser.class);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, final int position, long l) {
                        mGoodsDescription = "充值" + payConises.get(position).payConisCount + "趣币送" + payConises.get(position).conisPresent + "趣币";
                        userSelectText.setText(mGoodsDescription);
                        mPayMoney = payConises.get(position).priceConis;
                        mPayConisCount = payConises.get(position).payConisCount + payConises.get(position).conisPresent;
                    }
                });
                listView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(final int position, SwipeMenu menu, int index) {
                        switch (index) {
                            case 0:
                                mGoodsDescription = "充值" + payConises.get(position).payConisCount + "趣币送" + payConises.get(position).conisPresent + "趣币";
                                userSelectText.setText(mGoodsDescription);
                                mPayMoney = payConises.get(position).priceConis;
                                mPayConisCount = payConises.get(position).payConisCount + payConises.get(position).conisPresent;
                                break;
                        }
                        return false;
                    }
                });
            }
        }
    };
}

