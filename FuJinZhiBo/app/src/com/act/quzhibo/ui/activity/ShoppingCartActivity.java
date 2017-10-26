package com.act.quzhibo.ui.activity;

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
import com.act.quzhibo.util.ToastUtil;
import com.act.quzhibo.widget.LoadNetView;
import com.act.quzhibo.widget.TitleBarView;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shopping_cart_activity);
        ButterKnife.bind(this);
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
    public void childDelete(int position) {
        shoppingCarts.remove(position);
        cartAdapter.notifyDataSetChanged();
        statistics();
    }


    private void initData() {

        mRecyclerview.setPullRefreshEnabled(true);
        mRecyclerview.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
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

        BmobQuery<ShoppingCart> query = new BmobQuery<>();
        query.addWhereEqualTo("user", BmobUser.getCurrentUser(RootUser.class));
        query.findObjects(new FindListener<ShoppingCart>() {
            @Override
            public void done(List<ShoppingCart> list, BmobException e) {
                if (e == null) {

                    if (list != null && list.size() > 0) {
                        for (int i = 0; i < list.size(); i++) {
                            BmobQuery<CommonCourse> query1 = new BmobQuery<>();
                            query1.getObject(list.get(i).course.getObjectId(), new QueryListener<CommonCourse>() {
                                @Override
                                public void done(CommonCourse course, BmobException e) {
                                    if (e == null && course != null) {
                                        ShoppingCart cart = new ShoppingCart();
                                        cart.user = BmobUser.getCurrentUser(RootUser.class);
                                        cart.course = course;
                                        cart.price = Double.parseDouble(course.courseAppPrice);
                                        shoppingCarts.add(cart);
                                        cartAdapter.setCartBeanListAndNotify(shoppingCarts);
                                    }
                                }
                            });
                        }



                    } else {
                        loadNetView.setlayoutVisily(Constants.NO_DATA);
                    }
                }
            }
        });
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

        //跳转到支付界面
    }


}
