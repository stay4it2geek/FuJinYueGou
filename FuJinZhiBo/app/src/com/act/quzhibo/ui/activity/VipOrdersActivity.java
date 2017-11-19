package com.act.quzhibo.ui.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.act.quzhibo.R;
import com.act.quzhibo.adapter.OrderAdapter;
import com.act.quzhibo.common.Constants;
import com.act.quzhibo.bean.RootUser;
import com.act.quzhibo.bean.Orders;
import com.act.quzhibo.i.OnQueryDataListner;
import com.act.quzhibo.util.CommonUtil;
import com.act.quzhibo.util.ToastUtil;
import com.act.quzhibo.util.ViewDataUtil;
import com.act.quzhibo.widget.FragmentDialog;
import com.act.quzhibo.widget.LoadNetView;
import com.act.quzhibo.widget.TitleBarView;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

public class VipOrdersActivity extends FragmentActivity {

    XRecyclerView recyclerView;
    OrderAdapter orderAdapter;
    LoadNetView loadNetView;
    ArrayList<Orders> OrdersList = new ArrayList<>();
    String lastTime = "";
    int handlerOrderSize;
    RootUser user;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_common);
        loadNetView = (LoadNetView) findViewById(R.id.loadview);
        TitleBarView titlebar = (TitleBarView) findViewById(R.id.titlebar);
        titlebar.setBarTitle("订单列表");
        titlebar.setBackButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               finish();
            }
        });
        titlebar.setVisibility(View.VISIBLE);
        recyclerView = (XRecyclerView) findViewById(R.id.recyclerview);
        ViewDataUtil.setLayManager(handlerOrderSize, new OnQueryDataListner() {
            @Override
            public void onRefresh() {
                queryDatas(Constants.REFRESH);
            }

            @Override
            public void onLoadMore() {
                queryDatas(Constants.LOADMORE);

            }
        }, this, recyclerView, 1, true, true);

        loadNetView.setReloadButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadNetView.setlayoutVisily(Constants.LOAD);
                queryDatas(Constants.REFRESH);
            }
        });
        loadNetView.setBuyButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(VipOrdersActivity.this, GetVipPayActivity.class));
            }
        });
        user = (RootUser) getIntent().getSerializableExtra("user");
        queryRootUserData();
    }

    void queryRootUserData() {
        BmobQuery<Orders> query = new BmobQuery<>();
        query.addWhereEqualTo("user", user);
        query.order("-updatedAt");
        query.findObjects(new FindListener<Orders>() {
            @Override
            public void done(List<Orders> list, BmobException e) {
                if (e == null) {
                    if (list.size() > 0) {
                        queryDatas(Constants.REFRESH);
                    } else {
                        loadNetView.setlayoutVisily(Constants.BUY_VIP);
                    }
                } else {
                    handler.sendEmptyMessage(Constants.NetWorkError);
                }
            }
        });
    }


    void queryDatas(final int actionType) {
        BmobQuery<Orders> query = new BmobQuery<>();
        BmobQuery<Orders> query2 = new BmobQuery<>();
        List<BmobQuery<Orders>> queries = new ArrayList<>();
        query2.setLimit(10);
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
        BmobQuery<Orders> query3 = new BmobQuery<>();
        query3.addWhereEqualTo("user", BmobUser.getCurrentUser(RootUser.class));
        queries.add(query3);
        query.and(queries);
        query.order("-updatedAt");
        query.findObjects(new FindListener<Orders>() {
            @Override
            public void done(List<Orders> list, BmobException e) {
                if (e == null) {
                    if (actionType == Constants.REFRESH) {
                        OrdersList.clear();
                        if(orderAdapter!=null){
                            orderAdapter.notifyDataSetChanged();
                        }
                    }
                    if (list.size() > 0) {
                        lastTime = list.get(list.size() - 1).getUpdatedAt();
                    }
                    Message message = new Message();
                    message.obj = list;
                    message.what = actionType;
                    handler.sendMessage(message);
                } else {
                    handler.sendEmptyMessage(Constants.NetWorkError);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (orderAdapter != null) {
            queryDatas(Constants.REFRESH);
        }
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            ArrayList<Orders> Orderses = (ArrayList<Orders>) msg.obj;
            if (msg.what != Constants.NetWorkError) {
                if (Orderses != null) {
                    OrdersList.addAll(Orderses);
                    handlerOrderSize = Orderses.size();
                } else {
                    handlerOrderSize = 0;
                }
                if (orderAdapter == null) {
                    orderAdapter = new OrderAdapter(VipOrdersActivity.this, OrdersList);
                    recyclerView.setAdapter(orderAdapter);
                    if (user == BmobUser.getCurrentUser(RootUser.class)) {
                        orderAdapter.setDeleteListener(new OrderAdapter.OnDeleteListener() {
                            @Override
                            public void onDelete(final int position) {
                                FragmentDialog.newInstance(false, getResources().getString(R.string.ifChecked), getResources().getString(R.string.deleteCanNotRecover), getResources().getString(R.string.cancel), getResources().getString(R.string.main__delete), "", "", false, new FragmentDialog.OnClickBottomListener() {
                                    @Override
                                    public void onPositiveClick(Dialog dialog, boolean deleteFileSource) {
                                        dialog.dismiss();
                                    }

                                    @Override
                                    public void onNegtiveClick(Dialog dialog) {

                                        OrdersList.get(position).delete(OrdersList.get(position).getObjectId(), new UpdateListener() {
                                            @Override
                                            public void done(BmobException e) {
                                                if (e == null) {
                                                    OrdersList.remove(position);
                                                    orderAdapter.notifyDataSetChanged();
                                                    if (OrdersList.size() == 0) {
                                                        loadNetView.setVisibility(View.VISIBLE);
                                                        loadNetView.setlayoutVisily(Constants.BUY_VIP);
                                                        return;
                                                    }

                                                } else {
                                                    ToastUtil.showToast(VipOrdersActivity.this, "删除失败");
                                                }
                                            }
                                        });
                                        dialog.dismiss();
                                    }
                                }).show(getSupportFragmentManager(), "");
                            }
                        });
                    }
                } else {
                    orderAdapter.notifyDataSetChanged();
                }
                if (msg.what == Constants.LOADMORE) {
                    recyclerView.setNoMore(true);
                }
                loadNetView.setVisibility(View.GONE);
                if (OrdersList.size() == 0) {
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
}
