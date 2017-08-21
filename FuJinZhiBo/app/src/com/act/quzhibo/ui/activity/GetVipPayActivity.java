package com.act.quzhibo.ui.activity;

import android.Manifest;
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
import android.os.StrictMode;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.act.quzhibo.R;
import com.act.quzhibo.entity.RootUser;
import com.act.quzhibo.entity.VipOrders;
import com.act.quzhibo.util.CommonUtil;
import com.act.quzhibo.view.FragmentDialog;
import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;

import java.util.ArrayList;

import c.b.BP;
import c.b.PListener;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FetchUserInfoListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

import static android.widget.AbsListView.CHOICE_MODE_SINGLE;


public class GetVipPayActivity extends FragmentActivity {
    private TextView alipay;
    ProgressDialog dialog;
    private String mGoodsDescription;
    ArrayList<String> titles = new ArrayList<>();
    ArrayList<Integer> imgs = new ArrayList<>();
    ArrayList<String> types = new ArrayList<>();
    ArrayList<String> prices = new ArrayList<>();
    ArrayList<String> prices_maket = new ArrayList<>();
    private double mPayMoney = 0.01;
    private TextView userSelectText;
    VipOrders vipOrders;
    private String orderType;
    private SwipeMenuListView listView;
    MyAdapter mAdapter;
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

                // create "delete" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(getApplicationContext());
                deleteItem.setBackground(new ColorDrawable(getResources().getColor(R.color.blue)));
                deleteItem.setWidth(300);
                deleteItem.setTitle("查看");
                deleteItem.setTitleSize(18);
                deleteItem.setTitleColor(Color.WHITE);

                menu.addMenuItem(openItem);
                menu.addMenuItem(deleteItem);
            }
        };

        listView.setMenuCreator(creator);
        alipay = (TextView) findViewById(R.id.alipay);
        userSelectText = (TextView) findViewById(R.id.userSelectText);
        listView.setChoiceMode(CHOICE_MODE_SINGLE);
        listView.setAdapter(mAdapter = new MyAdapter(this));
        vipOrders = new VipOrders();
        vipOrders.user = BmobUser.getCurrentUser(RootUser.class);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int position, long l) {
                userSelectText.setText(titles.get(position));
                mGoodsDescription = titles.get(position);
                orderType = types.get(position);
                mPayMoney = Double.parseDouble(prices.get(position));
            }
        });

        listView.setSwipeDirection(SwipeMenuListView.DIRECTION_LEFT);

        listView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(final int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        userSelectText.setText(titles.get(position));
                        mGoodsDescription = titles.get(position);
                        orderType = types.get(position);
                        mPayMoney = Double.parseDouble(prices.get(position));
                        break;
                    case 1:
                        Toast.makeText(GetVipPayActivity.this, "show", Toast.LENGTH_SHORT).show();
                        break;
                }
                return false;
            }
        });

        alipay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alipay();
            }
        });
    }

    class MyAdapter extends BaseAdapter {
        Context context;
        LayoutInflater inflater;

        public MyAdapter(Context context) {
            super();
            this.context = context;
            inflater = LayoutInflater.from(context);

            titles.add("超级VIP");
            titles.add("特级VIP");
            titles.add("初级VIP");
            titles.add("初级升级到超级");
            titles.add("特级升级到超级");
            titles.add("初级升级到特级");

            types.add("0");
            types.add("1");
            types.add("2");
            types.add("3");
            types.add("4");
            types.add("5");

            prices_maket.add("499");
            prices_maket.add("399");
            prices_maket.add("199");
            prices_maket.add("300");
            prices_maket.add("200");
            prices_maket.add("200");


            prices.add("299");
            prices.add("199");
            prices.add("99");
            prices.add("200");
            prices.add("100");
            prices.add("100");

            imgs.add(R.drawable.supervip);
            imgs.add(R.drawable.midvip);
            imgs.add(R.drawable.chuvip);
            imgs.add(R.drawable.up);
            imgs.add(R.drawable.up);
            imgs.add(R.drawable.up);
        }

        @Override
        public int getCount() {
            return imgs.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            convertView = inflater.inflate(R.layout.item_buyvip, null, false);
            TextView title = (TextView) convertView.findViewById(R.id.title_vip);
            title.setText(titles.get(position));
            TextView price_vip = (TextView) convertView.findViewById(R.id.price_vip);
            price_vip.setText(prices.get(position) + "元");
            TextView price_vip_market = (TextView) convertView.findViewById(R.id.price_vip_maket);
            price_vip_market.setText(prices_maket.get(position) + "元");
            ((TextView) convertView.findViewById(R.id.price_vip_maket)).getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
            ImageView vip_img = (ImageView) convertView.findViewById(R.id.vip_img);
            vip_img.setBackgroundResource(imgs.get(position));
            return convertView;
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (vipOrders.orderStatus && !hasShow) {
            hideDialog();
            FragmentDialog.newInstance("", "尊敬的用户您好，你购买" + "\"" + mGoodsDescription + "\"" + "已支付成功!", "我知道了!", "", -1, true, new FragmentDialog.OnClickBottomListener() {
                @Override
                public void onPositiveClick(Dialog dialog) {
                    dialog.dismiss();
                }

                @Override
                public void onNegtiveClick(Dialog dialog) {
                    dialog.dismiss();
                }
            }).show(getSupportFragmentManager(), "dialog");
            hasShow = true;
        }
    }

    RootUser user = BmobUser.getCurrentUser(RootUser.class);

    private void alipay() {
        if (BmobUser.getCurrentUser(RootUser.class) == null) {
            Toast.makeText(GetVipPayActivity.this, "您还没有登录或注册哦!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(GetVipPayActivity.this, LoginActivity.class));
            return;
        }
        if (TextUtils.isEmpty(userSelectText.getText())) {
            Toast.makeText(GetVipPayActivity.this, "您还没有选择VIP种类哦!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!checkPackageInstalled("com.eg.android.AlipayGphone", "https://mobile.alipay.com/index.htm")) {
            Toast.makeText(GetVipPayActivity.this, "您还没安装支付宝客户端哦!", Toast.LENGTH_SHORT).show();
            return;
        }

        showDialog("正在生成订单，请您稍候...");
        BP.pay(mGoodsDescription, mGoodsDescription, 0.01, true, new PListener() {

            // 因为网络等原因,支付结果未知(小概率事件),出于保险起见稍后手动查询
            @Override
            public void unknow() {
                Toast.makeText(GetVipPayActivity.this, "支付结果未知,请您稍后手动查询", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(GetVipPayActivity.this, "VIP支付信息更新成功", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(GetVipPayActivity.this, "VIP支付信息更新失败：" + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                user.vipType = orderType;
                user.vipTypeName = mGoodsDescription;
                user.update(user.getObjectId(), new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if (e == null) {
                            CommonUtil.fecth(GetVipPayActivity.this);
                            ;
                            Toast.makeText(GetVipPayActivity.this, "VIP信息更新成功" + user.vipTypeName + "kkkkk" + mGoodsDescription, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(GetVipPayActivity.this, "VIP信息更新失败，原因是:" + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
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
                vipOrders.orderType = orderType + "";
                vipOrders.save(new SaveListener<String>() {
                    @Override
                    public void done(String objectId, BmobException e) {
                        if (e == null) {
                            showDialog("生成订单成功!请等待跳转到支付页面");
                        } else {
                            Toast.makeText(GetVipPayActivity.this, "添加订单数据失败：" + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(GetVipPayActivity.this, "交易关闭!原因是" + reason, Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(GetVipPayActivity.this, "您的手机上没有没有应用市场也没有浏览器，请您去想办法安装支付宝/微信吧",
                            Toast.LENGTH_SHORT).show();
                }
            }
        }
        return false;
    }


}

