package com.act.quzhibo.ui.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Display;
import android.view.View;

import com.act.quzhibo.R;
import com.act.quzhibo.adapter.MyFocusPersonListAdapter;
import com.act.quzhibo.common.Constants;
import com.act.quzhibo.entity.MyFocusCommonPerson;
import com.act.quzhibo.entity.MyFocusCommonPerson;
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
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;


public class MyFocusPersonActivity extends FragmentActivity {

    private XRecyclerView recyclerView;
    private MyFocusPersonListAdapter myFocusPersonListAdapter;
    private LoadNetView loadNetView;
    private String lastTime = "";
    private ArrayList<MyFocusCommonPerson> myFocusCommonPersons = new ArrayList<>();
    private int myfocusSize;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_common);
        TitleBarView titlebar = (TitleBarView) findViewById(R.id.titlebar);
        titlebar.setVisibility(View.VISIBLE);
        titlebar.setBarTitle("我关注的情趣达人");
        titlebar.setBackButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyFocusPersonActivity.this.finish();
            }
        });
        loadNetView = (LoadNetView) findViewById(R.id.loadview);
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
                        queryData(Constants.REFRESH);
                        recyclerView.refreshComplete();
                    }
                }, 1000);
            }

            @Override
            public void onLoadMore() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (myfocusSize > 0) {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    queryData(Constants.LOADMORE);
                                    recyclerView.loadMoreComplete();
                                }
                            }, 1000);
                        } else {
                            recyclerView.setNoMore(true);
                        }
                    }
                }, 1000);
            }
        });
        GridLayoutManager gridLayoutManager = new GridLayoutManager(MyFocusPersonActivity.this, 2);
        gridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(gridLayoutManager);
        queryData(Constants.REFRESH);
    }

    private void queryData(final int actionType) {
        final BmobQuery<MyFocusCommonPerson> query = new BmobQuery<>();
        query.setLimit(10);
        if (actionType == Constants.LOADMORE) {
            // 只查询小于最后一个item发表时间的数据
            Date date = null;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                date = sdf.parse(lastTime);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            query.addWhereLessThanOrEqualTo("updatedAt", new BmobDate(date));
        }
        query.order("-updatedAt");
        query.findObjects(new FindListener<MyFocusCommonPerson>() {
            @Override
            public void done(List<MyFocusCommonPerson> list, BmobException e) {
                if (e == null) {
                    if (list.size() > 0) {
                        if (actionType == Constants.REFRESH) {
                            // 当是下拉刷新操作时，将当前页的编号重置为0，并清空，重新添加
                            myFocusCommonPersons.clear();
                        }
                        lastTime = list.get(list.size() - 1).getUpdatedAt();
                        Message message = new Message();
                        message.obj = list;
                        message.what = actionType;
                        handler.sendMessage(message);
                    } else {
                        handler.sendEmptyMessage(Constants.NO_MORE);
                    }
                }
            }
        });
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            ArrayList<MyFocusCommonPerson> showerses = (ArrayList<MyFocusCommonPerson>) msg.obj;
            if (msg.what != Constants.NetWorkError) {

                if (showerses != null) {
                    myFocusCommonPersons.addAll(showerses);
                    myfocusSize = showerses.size();
                } else {
                    myfocusSize = 0;
                }

                Display display = MyFocusPersonActivity.this.getWindowManager().getDefaultDisplay();
                Point size = new Point();
                display.getSize(size);
                int screenWidth = size.x;
                if (myFocusPersonListAdapter == null) {
                    myFocusPersonListAdapter = new MyFocusPersonListAdapter(MyFocusPersonActivity.this, myFocusCommonPersons, screenWidth);
                    recyclerView.setAdapter(myFocusPersonListAdapter);
                    myFocusPersonListAdapter.setOnItemClickListener(new MyFocusPersonListAdapter.OnRecyclerViewItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position, final MyFocusCommonPerson myFocusCommonPerson) {
                            Intent intent = new Intent();
                            intent.putExtra(Constants.COMMON_USER_ID, myFocusCommonPerson.userId);
                            if (myFocusCommonPerson.userType.equals(Constants.INTEREST)) {
                                intent.setClass(MyFocusPersonActivity.this, IntersetPersonPostListActivity.class);
                            } else {
//                                intent.setClass(MyFocusPersonActivity.this, NearPersonPostListActivity.class);
                            }

                            startActivity(intent);
                        }
                    });
                    if (myFocusPersonListAdapter != null) {
                        myFocusPersonListAdapter.setDelteListener(new MyFocusPersonListAdapter.OnDeleteListener() {
                            @Override
                            public void onDelete(final int position) {
                                FragmentDialog.newInstance(false, "是否取消关注", "真的要取消关注人家吗", "确定", "取消", -1, false, new FragmentDialog.OnClickBottomListener() {
                                    @Override
                                    public void onPositiveClick(Dialog dialog, boolean deleteFileSource) {
                                        myFocusCommonPersons.get(position).delete(myFocusCommonPersons.get(position).getObjectId(), new UpdateListener() {
                                            @Override
                                            public void done(BmobException e) {
                                                if (e == null) {
                                                    myFocusCommonPersons.remove(position);
                                                    myFocusPersonListAdapter.notifyDataSetChanged();
                                                    if (myFocusCommonPersons.size() == 0) {
                                                        loadNetView.setVisibility(View.VISIBLE);
                                                        loadNetView.setlayoutVisily(Constants.NO_DATA);
                                                        return;
                                                    }
                                                }
                                            }
                                        });
                                        dialog.dismiss();
                                    }

                                    @Override
                                    public void onNegtiveClick(Dialog dialog) {
                                        dialog.dismiss();
                                    }
                                }).show(getSupportFragmentManager(),"");
                            }
                        });
                    }
                } else {
                    myFocusPersonListAdapter.notifyDataSetChanged();
                }
                loadNetView.setVisibility(View.GONE);

            } else {
                loadNetView.setVisibility(View.VISIBLE);
                loadNetView.setlayoutVisily(Constants.NO_DATA);
            }
        }
    };

}
