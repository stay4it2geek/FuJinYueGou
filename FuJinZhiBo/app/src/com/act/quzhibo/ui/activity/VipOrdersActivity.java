package com.act.quzhibo.ui.activity;

import android.app.Dialog;
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
import com.act.quzhibo.entity.RootUser;
import com.act.quzhibo.entity.VipOrders;
import com.act.quzhibo.util.ToastUtil;
import com.act.quzhibo.view.FragmentDialog;
import com.act.quzhibo.view.LoadNetView;
import com.act.quzhibo.view.TitleBarView;
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

    private XRecyclerView recyclerView;
    private OrderAdapter orderAdapter;
    private LoadNetView loadNetView;
    private ArrayList<VipOrders> vipOrderSList = new ArrayList<>();
    private String lastTime = "";
    private int orderSize;
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
                VipOrdersActivity.this.finish();
            }
        });
        titlebar.setVisibility(View.VISIBLE);
        recyclerView = (XRecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setPullRefreshEnabled(true);
        recyclerView.setLoadingMoreEnabled(true);
        recyclerView.setLoadingMoreProgressStyle(R.style.Small);
        recyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        recyclerView.setNoMore(false);
                        recyclerView.setLoadingMoreEnabled(true);
                        queryDatas(Constants.REFRESH);
                        recyclerView.refreshComplete();
                    }
                }, 1000);
            }

            @Override
            public void onLoadMore() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (orderSize > 0) {
                            queryDatas(Constants.LOADMORE);
                            recyclerView.loadMoreComplete();
                        } else {
                            recyclerView.setNoMore(true);
                        }
                    }
                }, 1000);
            }
        });

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 1);
        gridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(gridLayoutManager);
        loadNetView.setReloadButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadNetView.setlayoutVisily(Constants.LOAD);
                queryDatas(Constants.REFRESH);
            }
        });
        queryRootUserData();
    }

    private void queryRootUserData() {
        BmobQuery<VipOrders> query = new BmobQuery<>();
        query.addWhereEqualTo("user", BmobUser.getCurrentUser(RootUser.class));
        query.order("-updatedAt");
        query.findObjects(new FindListener<VipOrders>() {
            @Override
            public void done(List<VipOrders> list, BmobException e) {
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


    private void queryDatas(final int actionType) {
        BmobQuery<VipOrders> query = new BmobQuery<>();
        BmobQuery<VipOrders> query2 = new BmobQuery<>();
        List<BmobQuery<VipOrders>> queries = new ArrayList<>();
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
        BmobQuery<VipOrders> query3 = new BmobQuery<>();
        query3.addWhereEqualTo("user", BmobUser.getCurrentUser(RootUser.class));
        queries.add(query3);
        query.and(queries);
        query.order("-updatedAt");
        query.findObjects(new FindListener<VipOrders>() {
            @Override
            public void done(List<VipOrders> list, BmobException e) {
                if (e == null) {
                    if (actionType == Constants.REFRESH) {
                        vipOrderSList.clear();
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
            ArrayList<VipOrders> vipOrderses = (ArrayList<VipOrders>) msg.obj;
            if (msg.what != Constants.NetWorkError) {
                if (vipOrderses != null) {
                    vipOrderSList.addAll(vipOrderses);
                    orderSize = vipOrderses.size();
                } else {
                    orderSize = 0;
                }

                if (orderAdapter == null) {
                    orderAdapter = new OrderAdapter(VipOrdersActivity.this, vipOrderSList);
                    recyclerView.setAdapter(orderAdapter);
                    orderAdapter.setDeleteListener(new OrderAdapter.OnDeleteListener() {
                        @Override
                        public void onDelete(final int position) {
                            FragmentDialog.newInstance(false, "是否取消关注", "真的要取消关注人家吗","继续关注", "取消关注", -1, false, new FragmentDialog.OnClickBottomListener() {
                                @Override
                                public void onPositiveClick(Dialog dialog, boolean deleteFileSource) {
                                    dialog.dismiss();
                                }

                                @Override
                                public void onNegtiveClick(Dialog dialog) {

                                    vipOrderSList.get(position).delete(vipOrderSList.get(position).getObjectId(), new UpdateListener() {
                                        @Override
                                        public void done(BmobException e) {
                                            if (e == null) {
                                                vipOrderSList.remove(position);
                                                orderAdapter.notifyDataSetChanged();
                                                if (vipOrderSList.size() == 0) {
                                                    loadNetView.setVisibility(View.VISIBLE);
                                                    loadNetView.setlayoutVisily(Constants.BUY_VIP);
                                                    return;
                                                }

                                            }else{
                                                ToastUtil.showToast(VipOrdersActivity.this,"删除失败");
                                            }
                                        }
                                    });
                                    dialog.dismiss();
                                }
                            }).show(getSupportFragmentManager(), "");

                        }
                    });
                } else {
                    orderAdapter.notifyDataSetChanged();
                }

                loadNetView.setVisibility(View.GONE);
                if (vipOrderSList.size() == 0) {
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
