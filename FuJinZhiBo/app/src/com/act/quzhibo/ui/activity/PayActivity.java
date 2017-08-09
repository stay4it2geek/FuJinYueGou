package com.act.quzhibo.ui.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.ListViewAutoScrollHelper;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.act.quzhibo.R;
import com.act.quzhibo.adapter.MemberAdapter;
import com.bumptech.glide.Glide;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import c.b.BP;
import c.b.PListener;
import c.b.QListener;

import static android.widget.AbsListView.CHOICE_MODE_SINGLE;


public class PayActivity extends AppCompatActivity {
    //配置你的ApplicationId
    String APPID = "e37264d2646046d9158d3800afd548f3";
    private Button bt_alipay;
    private Button bt_query;
    private TextView tv;
    private EditText order;
    ProgressDialog dialog;
    private static final int REQUESTPERMISSION = 101;
    private String mGoodsDescription;
    private double mPayMoney;
    private String mGoodsName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);
        ListView listView = (ListView) findViewById(R.id.viplist);
        listView.setChoiceMode(CHOICE_MODE_SINGLE);
        listView.setAdapter(new MyAdapter(this));
        initData();
        // 初始化BmobPay对象,可以在支付时再初始化
        BP.init(APPID);
        //解决Android 7.0 FileUriExposedException url异常
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
        }
        initUI();
    }

    class MyAdapter extends BaseAdapter {
         Context context;
         LayoutInflater inflater;
        ArrayList<String> titles = new ArrayList<>();
        ArrayList<Integer> imgs = new ArrayList<>();
        ArrayList<String> prices = new ArrayList<>();
        ArrayList<String> prices_maket = new ArrayList<>();

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

            prices_maket.add("498元");
            prices_maket.add("398元");
            prices_maket.add("198元");
            prices_maket.add("300元");
            prices_maket.add("200元");
            prices_maket.add("200元");


            prices.add("298元");
            prices.add("198元");
            prices.add("98元");
            prices.add("200元");
            prices.add("100元");
            prices.add("100元");

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
            price_vip.setText(prices.get(position));

            TextView price_vip_market = (TextView) convertView.findViewById(R.id.price_vip_maket);
            price_vip_market.setText(prices_maket.get(position));

            ((TextView) convertView.findViewById(R.id.price_vip_maket)).getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);

            ImageView vip_img = (ImageView) convertView.findViewById(R.id.vip_img);

            vip_img.setBackgroundResource(imgs.get(position));

            return convertView;
        }
    }

    private void initData() {
        //获取传来的商品描述及金额
        Intent intent = getIntent();
        mGoodsDescription = "gg";
        mPayMoney = 9.0;
        mGoodsName = "fdgf";
        Log.i("PayActivity", "名称：" + mGoodsName + "总额：" + mPayMoney);
    }

    private void initUI() {
        bt_alipay = (Button) findViewById(R.id.bt_alipay);
        bt_query = (Button) findViewById(R.id.bt_query);
        order = (EditText) findViewById(R.id.order);
        tv = (TextView) findViewById(R.id.tv);
        bt_alipay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alipay();

            }
        });

        bt_query.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                query();    //查询订单
            }
        });
    }

    private void alipay() {


        if (!checkPackageInstalled("com.eg.android.AlipayGphone",
                "https://www.alipay.com")) { // 支付宝支付要求用户已经安装支付宝客户端
            Toast.makeText(PayActivity.this, "请安装支付宝客户端", Toast.LENGTH_SHORT)
                    .show();
            return;
        }

        showDialog("正在获取订单...\nSDK版本号:" + BP.getPaySdkVersion());
        final String name = mGoodsName;

        BP.pay(name, mGoodsDescription, mPayMoney, true, new PListener() {

            // 因为网络等原因,支付结果未知(小概率事件),出于保险起见稍后手动查询
            @Override
            public void unknow() {
                Toast.makeText(PayActivity.this, "支付结果未知,请稍后手动查询", Toast.LENGTH_SHORT)
                        .show();
                tv.append(name + "'s pay status is unknow\n\n");
                hideDialog();
            }

            // 支付成功,如果金额较大请手动查询确认
            @Override
            public void succeed() {
                Toast.makeText(PayActivity.this, "支付成功!", Toast.LENGTH_SHORT).show();
                tv.append(name + "'s pay status is success\n\n");
                hideDialog();
            }

            // 无论成功与否,返回订单号
            @Override
            public void orderId(String orderId) {
                // 此处应该保存订单号,比如保存进数据库等,以便以后查询
                order.setText(orderId);
                tv.append(name + "'s orderid is " + orderId + "\n\n");
                showDialog("获取订单成功!请等待跳转到支付页面~");
            }

            // 支付失败,原因可能是用户中断支付操作,也可能是网络原因
            @Override
            public void fail(int code, String reason) {

                // 当code为-2,意味着用户中断了操作
                Toast.makeText(PayActivity.this, "支付中断!", Toast.LENGTH_SHORT)
                        .show();
                tv.append(name + "'s pay status is fail, error code is \n"
                        + code + " ,reason is " + reason + "\n\n");
                hideDialog();
            }
        });
    }

    // 执行订单查询
    void query() {
        showDialog("正在查询订单...");
        final String orderId = getOrder();

        BP.query(orderId, new QListener() {

            @Override
            public void succeed(String status) {
                Toast.makeText(PayActivity.this, "查询成功!该订单状态为 : " + status,
                        Toast.LENGTH_SHORT).show();
                tv.append("pay status of" + orderId + " is " + status + "\n\n");
                hideDialog();
            }

            @Override
            public void fail(int code, String reason) {
                Toast.makeText(PayActivity.this, "查询失败", Toast.LENGTH_SHORT).show();
                tv.append("query order fail, error code is " + code
                        + " ,reason is \n" + reason + "\n\n");
                hideDialog();
            }
        });
    }

    // 默认为0.02
    double getPrice() {
        double price = 0.02;    //默认价格为0.02
        try {
            price = mPayMoney;
        } catch (NumberFormatException e) {
        }
        return price;
    }

    // 商品详情(可不填)
    String getBody() {
        return mGoodsDescription;
    }

    // 商品名称
    String getName() {
        return mGoodsName;
    }

    // 支付订单号(查询时必填)
    String getOrder() {
        return this.order.getText().toString();
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


    /**
     * 检查某包名应用是否已经安装
     *
     * @param packageName 包名
     * @param browserUrl  如果没有应用市场，去官网下载
     * @return
     */
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
                    Toast.makeText(PayActivity.this,
                            "您的手机上没有没有应用市场也没有浏览器，我也是醉了，你去想办法安装支付宝/微信吧",
                            Toast.LENGTH_SHORT).show();
                }
            }
        }
        return false;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUESTPERMISSION) {
            if (permissions[0].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {

                }
            }
        }
    }
}

