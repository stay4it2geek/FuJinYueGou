package com.act.quzhibo.ui.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.act.quzhibo.R;
import com.act.quzhibo.adapter.ShoppingCartAdapter;
import com.act.quzhibo.bean.RootUser;
import com.act.quzhibo.bean.ShoppingCart;
import com.act.quzhibo.common.Constants;
import com.act.quzhibo.event.ChangeEvent;
import com.act.quzhibo.i.OnQueryDataListner;
import com.act.quzhibo.i.OnReturnTotalListner;
import com.act.quzhibo.util.ToastUtil;
import com.act.quzhibo.util.ViewDataUtil;
import com.act.quzhibo.widget.FragmentDialog;
import com.act.quzhibo.widget.LoadNetView;
import com.act.quzhibo.widget.TitleBarView;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import org.greenrobot.eventbus.EventBus;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

public class ShoppingCartActivity extends BaseActivity implements ShoppingCartAdapter.ModifyListListner {

    ShoppingCartAdapter cartAdapter;
    boolean flag = false;
    double totalPrice = 0.00;// 购买的商品总价
    int totalCount = 0;// 购买的商品总数量

    @Bind(R.id.recyclerview)
    XRecyclerView mRecyclerview;
    @Bind(R.id.tv_show_price)
    TextView tvShowPrice;
    @Bind(R.id.tv_settlement)
    TextView tvSettlement;
    @Bind(R.id.loadview)
    LoadNetView loadNetView;
    @Bind(R.id.tvEditor)
    TextView tvEditor;
    ArrayList<ShoppingCart> shoppingCarts = new ArrayList<>();

    RootUser user;
    private int handlerCartsSize;
    String lastTime = "";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shopping_cart_activity);
        user = BmobUser.getCurrentUser(RootUser.class);
        TitleBarView titlebar = (TitleBarView) findViewById(R.id.titlebar);
        titlebar.setBarTitle("购物车");
        titlebar.setBackButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShoppingCartActivity.this.finish();
            }
        });
        ViewDataUtil.setLayManager(handlerCartsSize, new OnQueryDataListner() {
            @Override
            public void onRefresh() {
                requestShoppingCartData(Constants.REFRESH);
            }

            @Override
            public void onLoadMore() {
                requestShoppingCartData(Constants.LOADMORE);
            }
        }, this, mRecyclerview, 1, true, true);
        tvEditor.setVisibility(View.VISIBLE);
        loadNetView.setBuyButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ShoppingCartActivity.this, TabMainActivity.class);
                startActivity(intent);
                EventBus.getDefault().post(new ChangeEvent("buy"));
            }
        });
        requestShoppingCartData(Constants.REFRESH);
    }

    @OnClick(R.id.tvEditor)
    public void updateEditStatus() {
        flag = !flag;
        if (flag) {
            tvEditor.setText("完成");
            cartAdapter.isCanbeEdite(false);
        } else {
            tvEditor.setText("编辑");
            cartAdapter.isCanbeEdite(true);
        }
    }

    @OnClick(R.id.tv_settlement)
    public void settlement() {
        settlementOrder();
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
                            cartAdapter.setCartData(shoppingCarts);
                            cartAdapter.notifyDataSetChanged();
                            statistics(shoppingCarts);
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

    void requestShoppingCartData(final int actionType) {
        BmobQuery<ShoppingCart> query = new BmobQuery<>();
        BmobQuery<ShoppingCart> query2 = new BmobQuery<>();
        List<BmobQuery<ShoppingCart>> queries = new ArrayList<>();
        if (actionType == Constants.LOADMORE) {
            Date date;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                date = sdf.parse(lastTime);
                query2.addWhereLessThanOrEqualTo("updatedAt", new BmobDate(date));
                queries.add(query2);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        BmobQuery<ShoppingCart> query3 = new BmobQuery<>();
        query.addWhereEqualTo("user", user);
        queries.add(query3);
        query.and(queries);
        query.setLimit(10);
        query.order("-updatedAt");
        query.findObjects(new FindListener<ShoppingCart>() {
            @Override
            public void done(List<ShoppingCart> list, BmobException e) {
                if (e == null) {
                    if (actionType == Constants.REFRESH) {
                        shoppingCarts.clear();
                        if (cartAdapter != null) {
                            cartAdapter.notifyDataSetChanged();
                        }
                    }
                    if (list != null && list.size() > 0) {
                        lastTime = list.get(list.size() - 1).getUpdatedAt();
                        Message message = new Message();
                        message.obj = list;
                        message.what = actionType;
                        handler.sendMessage(message);
                    } else {
                        tvEditor.setText("编辑");
                        cartAdapter.isCanbeEdite(true);
                        loadNetView.setlayoutVisily(Constants.BUY_VIP);
                    }
                } else {
                    handler.sendEmptyMessage(Constants.NetWorkError);
                }
            }
        });
    }
    int cartsSize;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            ArrayList<ShoppingCart> carts = (ArrayList<ShoppingCart>) msg.obj;
            if (msg.what != Constants.NetWorkError) {
                if (shoppingCarts != null) {
                    shoppingCarts.addAll(carts);
                    handlerCartsSize = carts.size();
                } else {
                    handlerCartsSize = 0;
                }
              cartsSize=shoppingCarts.size();
                if (cartAdapter == null) {
                    cartAdapter = new ShoppingCartAdapter(ShoppingCartActivity.this);
                    cartAdapter.setModifyListListner(ShoppingCartActivity.this);
                    if(cartAdapter!=null){
                        cartAdapter.setReturnPriceListner(new OnReturnTotalListner() {
                            @Override
                            public void onReturnPrice(int size,int index, BmobObject o) {
                                shoppingCarts.remove(index);
                                shoppingCarts.add(index,(ShoppingCart)o);
                                if(size==cartsSize){
                                    cartAdapter.setCartData(shoppingCarts);
                                    cartAdapter.notifyDataSetChanged();
                                }
                                statistics(shoppingCarts);
                            }
                        });
                    }
                    cartAdapter.setCartData(shoppingCarts);
                    mRecyclerview.setAdapter(cartAdapter);
                } else {
                    cartAdapter.setCartData(shoppingCarts);
                    cartAdapter.notifyDataSetChanged();
                }

                if (msg.what == Constants.LOADMORE) {
                    mRecyclerview.setNoMore(true);
                }
               handler.postDelayed(new Runnable() {
                   @Override
                   public void run() {
                       loadNetView.setVisibility(View.GONE);
                   }
               },1500);
                if (shoppingCarts.size() == 0) {
                    tvEditor.setText("编辑");
                    cartAdapter.isCanbeEdite(true);
                    loadNetView.setVisibility(View.VISIBLE);
                    loadNetView.setlayoutVisily(Constants.BUY_VIP);
                    return;
                }
            } else {
                loadNetView.setVisibility(View.VISIBLE);
                loadNetView.setlayoutVisily(Constants.RELOAD);
            }
        }
    };

    /**
     * 统计操作
     * 1.先清空全局计数器
     * 2.遍历所有子元素，只要是被选中状态的，就进行相关的计算操作
     * 3.给底部的textView进行数据填充
     */
    public void statistics(List<ShoppingCart> shoppingCarts) {
        totalCount = 0;
        totalPrice = 0.00;
        for (int i = 0; i < shoppingCarts.size(); i++) {
            ShoppingCart cart = shoppingCarts.get(i);
                totalCount++;
                totalPrice += TextUtils.isEmpty(cart.courseAppPrice)? 0.00 : Double.parseDouble(cart.courseAppPrice);
        }
        tvShowPrice.setText("合计:" + totalPrice);
        tvSettlement.setText("结算(" + totalCount + ")");
    }

    /**
     * 结算订单、支付
     */
    void settlementOrder() {
        if (totalCount == 0) {
            return;
        }
        //选中的需要提交的商品清单
        for (ShoppingCart bean : shoppingCarts) {
//                String shoppingName = bean.getShoppingName();
//                int count = bean.getCount();
//                double price = bean.getPrice();
//                int size = bean.getDressSize();
//                String attribute = bean.getAttribute();
//                int id = bean.getId();
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
