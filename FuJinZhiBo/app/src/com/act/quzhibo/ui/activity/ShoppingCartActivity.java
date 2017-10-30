package com.act.quzhibo.ui.activity;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.act.quzhibo.R;
import com.act.quzhibo.adapter.ShoppingCartAdapter;
import com.act.quzhibo.bean.CommonCourse;
import com.act.quzhibo.bean.RootUser;
import com.act.quzhibo.bean.ShoppingCart;
import com.act.quzhibo.common.Constants;
import com.act.quzhibo.util.CommonUtil;
import com.act.quzhibo.util.ToastUtil;
import com.act.quzhibo.widget.FragmentDialog;
import com.act.quzhibo.widget.LoadNetView;
import com.act.quzhibo.widget.TitleBarView;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import c.b.BP;
import c.b.PListener;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

public class ShoppingCartActivity extends FragmentActivity implements
        ShoppingCartAdapter.CheckInterface,
        ShoppingCartAdapter.ModifyListInterface {

    private ShoppingCartAdapter cartAdapter;
    private boolean flag = false;
    private double totalPrice = 0.00;// 购买的商品总价
    private int totalCount = 0;// 购买的商品总数量

    @Bind(R.id.recyclerview)
    XRecyclerView mRecyclerview;

    @Bind(R.id.cbSelectAll)
    CheckBox ckAll;
    @Bind(R.id.tv_show_price)
    TextView tvShowPrice;
    @Bind(R.id.tv_settlement)
    TextView tvSettlement;
    @Bind(R.id.loadview)
    LoadNetView loadNetView;
    @Bind(R.id.tvEditor)
    TextView tvEditor;

    ArrayList<ShoppingCart> shoppingCarts = new ArrayList<>();
    private RootUser user;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shopping_cart_activity);
        ButterKnife.bind(this);
        user=BmobUser.getCurrentUser(RootUser.class);
        TitleBarView titlebar = (TitleBarView) findViewById(R.id.titlebar);
        titlebar.setBarTitle("购物车");
        titlebar.setBackButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShoppingCartActivity.this.finish();
            }
        });
        findViewById(R.id.tvEditor).setVisibility(View.VISIBLE);
        initData();

    }

    @OnClick(R.id.tvEditor)
    public void updateEditStatus() {
        flag = !flag;
        if (flag) {
            tvEditor.setText("完成");
            cartAdapter.isShow(false);
        } else {
            tvEditor.setText("编辑");
            cartAdapter.isShow(true);
        }

    }

    @OnClick(R.id.cbSelectAll)
    public void selectAll() {
        if (shoppingCarts.size() != 0) {
            if (ckAll.isChecked()) {
                for (int i = 0; i < shoppingCarts.size(); i++) {
                    shoppingCarts.get(i).setChoosed(true);
                }
                cartAdapter.notifyDataSetChanged();
            } else {
                for (int i = 0; i < shoppingCarts.size(); i++) {
                    shoppingCarts.get(i).setChoosed(false);
                }
                cartAdapter.notifyDataSetChanged();
            }
        }
        statistics();


    }

    @OnClick(R.id.tv_settlement)
    public void settlement() {
        settlementOrder();
    }

    /**
     * 单选
     *
     * @param position  组元素位置
     * @param isChecked 组元素选中与否
     */
    @Override
    public void checkGroup(int position, boolean isChecked) {
        shoppingCarts.get(position).setChoosed(isChecked);
        if (isAllCheck()) {
            ckAll.setChecked(true);
        } else {
            ckAll.setChecked(false);
        }
        cartAdapter.notifyDataSetChanged();
        statistics();
    }

    @Override
    public void childDelete(final int position) {
        FragmentDialog.newInstance(false, "是否删除？", "删除后不可恢复，需重新下单", "取消", "删除", "", "", false, new FragmentDialog.OnClickBottomListener() {
            @Override
            public void onPositiveClick(Dialog dialog, boolean deleteFileSource) {
                dialog.dismiss();
            }

            @Override
            public void onNegtiveClick(Dialog dialog) {
                shoppingCarts.get(position).delete(shoppingCarts.get(position).getObjectId(), new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if (e == null) {
                            shoppingCarts.remove(position);
                            cartAdapter.notifyDataSetChanged();
                            statistics();
                            if (shoppingCarts.size() == 0) {
                                loadNetView.setVisibility(View.VISIBLE);
                                loadNetView.setlayoutVisily(Constants.BUY_VIP);
                                return;
                            }

                        } else {
                            ToastUtil.showToast(ShoppingCartActivity.this, "删除失败" + e.getMessage());
                        }
                    }
                });
                dialog.dismiss();
            }
        }).show(getSupportFragmentManager(), "");

    }


    private void initData() {

        mRecyclerview.setPullRefreshEnabled(true);
        mRecyclerview.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        requestShoppingCartData();
                        mRecyclerview.refreshComplete();


                    }
                }, 1000);
            }

            @Override
            public void onLoadMore() {
            }
        });
        GridLayoutManager gridLayoutManager = new GridLayoutManager(ShoppingCartActivity.this, 1);
        gridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerview.setLayoutManager(gridLayoutManager);

        cartAdapter = new ShoppingCartAdapter(this);
        cartAdapter.setCheckInterface(this);
        cartAdapter.setModifyListInterface(this);
        mRecyclerview.setAdapter(cartAdapter);

        requestShoppingCartData();
    }

    private void requestShoppingCartData() {
        BmobQuery<ShoppingCart> query = new BmobQuery<>();
        query.addWhereEqualTo("user", user);
        query.findObjects(new FindListener<ShoppingCart>() {
            @Override
            public void done(List<ShoppingCart> list, BmobException e) {

                if (e == null) {

                    if (list != null && list.size() > 0) {
                        shoppingCarts.clear();
                        cartAdapter.notifyDataSetChanged();
                        for (int i = 0; i < list.size(); i++) {
                            BmobQuery<CommonCourse> query1 = new BmobQuery<>();
                            query1.getObject(list.get(i).course.getObjectId(), new MyQueryListener(list, i));
                        }

                        loadNetView.setVisibility(View.GONE);
                    } else {
                        loadNetView.setlayoutVisily(Constants.BUY_VIP);
                    }
                }
            }
        });
    }

    class MyQueryListener extends QueryListener<CommonCourse> {
        List<ShoppingCart> list;
        int i;

        public MyQueryListener(List<ShoppingCart> list, int i) {
            this.list = list;
            this.i = i;
        }

        @Override
        public void done(CommonCourse course, BmobException e) {
            if (e == null && course != null) {
                ShoppingCart cart = new ShoppingCart();
                cart.setObjectId(list.get(i).getObjectId());
                cart.user = user;
                cart.course = course;
                cart.price = Double.parseDouble(course.courseAppPrice);
                shoppingCarts.add(cart);
                cartAdapter.setCartBeanListAndNotify(shoppingCarts);
            }
        }
    }

    /**
     * 遍历list集合
     */
    private boolean isAllCheck() {
        for (ShoppingCart group : shoppingCarts) {
            if (!group.isChoosed())
                return false;
        }
        return true;
    }

    /**
     * 统计操作
     * 1.先清空全局计数器
     * 2.遍历所有子元素，只要是被选中状态的，就进行相关的计算操作
     * 3.给底部的textView进行数据填充
     */
    public void statistics() {
        totalCount = 0;
        totalPrice = 0.00;
        for (int i = 0; i < shoppingCarts.size(); i++) {
            ShoppingCart shoppingCartBean = shoppingCarts.get(i);
            if (shoppingCartBean.isChoosed()) {
                totalCount++;
                totalPrice += shoppingCartBean.price;
            }
        }
        tvShowPrice.setText("合计:" + totalPrice);
        tvSettlement.setText("结算(" + totalCount + ")");
    }

    /**
     * 结算订单、支付
     */
    private void settlementOrder() {
        //选中的需要提交的商品清单
        for (ShoppingCart bean : shoppingCarts) {
            boolean choosed = bean.isChoosed();
            if (choosed) {
//                String shoppingName = bean.getShoppingName();
//                int count = bean.getCount();
//                double price = bean.getPrice();
//                int size = bean.getDressSize();
//                String attribute = bean.getAttribute();
//                int id = bean.getId();
            }
        }
        ToastUtil.showToast(this, "总价：" + totalPrice);


//        showDialog("正在生成订单，请您稍候...");
//        BP.pay(mGoodsDescription, mGoodsDescription, 0.01, true, new PListener() {
//
//            // 因为网络等原因,支付结果未知(小概率事件),出于保险起见稍后手动查询
//            @Override
//            public void unknow() {
//                ToastUtil.showToast(GetVipPayActivity.this, "支付结果未知,请您稍后手动查询");
//                hideDialog();
//            }
//
//            // 支付成功,如果金额较大请手动查询确认
//            @Override
//            public void succeed() {
//                hideDialog();
//                vipOrders.orderStatus = true;
//                vipOrders.update(vipOrders.getObjectId(), new UpdateListener() {
//                    @Override
//                    public void done(BmobException e) {
//                        if (e == null) {
//                            ToastUtil.showToast(GetVipPayActivity.this, "订单充值支付信息更新成功");
//                            if (rootUser.vipConis > 0) {
//                                updateUser.vipConis = mPayConisCount + rootUser.vipConis;
//                            }
//                            updateUser.update(rootUser.getObjectId(), new UpdateListener() {
//                                @Override
//                                public void done(BmobException e) {
//                                    if (e == null) {
//                                        ToastUtil.showToast(GetVipPayActivity.this, "趣币信息更新成功，您还有" + updateUser.vipConis + "趣币");
//                                    } else {
//                                        ToastUtil.showToast(GetVipPayActivity.this, "趣币信息更新失败，原因是:" + e.getErrorCode());
//                                    }
//                                }
//                            });
//                            CommonUtil.fecth(GetVipPayActivity.this);
//                        } else {
//                            ToastUtil.showToast(GetVipPayActivity.this, "订单充值支付信息更新失败：" + e.getLocalizedMessage());
//                        }
//                    }
//                });
//
//
//            }
//
//            // 无论成功与否,返回订单号
//            @Override
//            public void orderId(String orderId) {
//                vipOrders.orderId = orderId;
//                vipOrders.orderPrice = mPayMoney + "";
//                vipOrders.goodsDescription = mGoodsDescription;
//                vipOrders.save(new SaveListener<String>() {
//                    @Override
//                    public void done(String objectId, BmobException e) {
//                        if (e == null) {
//                            showDialog("生成订单成功!请等待跳转到支付页面");
//                        } else {
//                            ToastUtil.showToast(GetVipPayActivity.this, "添加订单数据失败：" + e.getLocalizedMessage());
//                        }
//                    }
//                });
//            }
//
//            // 支付失败,原因可能是用户中断支付操作,也可能是网络原因
//            @Override
//            public void fail(int code, final String reason) {
//                vipOrders.delete(vipOrders.getObjectId(), new UpdateListener() {
//                    @Override
//                    public void done(BmobException e) {
//                        if (e == null) {
//                            ToastUtil.showToast(GetVipPayActivity.this, "交易关闭!原因是" + reason);
//                        }
//                    }
//                });
//                hideDialog();
//            }
//        });
    }


}
