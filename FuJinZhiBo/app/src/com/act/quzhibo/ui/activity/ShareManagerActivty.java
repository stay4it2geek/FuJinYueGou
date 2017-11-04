package com.act.quzhibo.ui.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.act.quzhibo.R;
import com.act.quzhibo.adapter.MyTeamListAdapter;
import com.act.quzhibo.bean.Promotion;
import com.act.quzhibo.bean.RootUser;
import com.act.quzhibo.common.Constants;
import com.act.quzhibo.widget.LoadNetView;
import com.act.quzhibo.widget.TitleBarView;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
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
import cn.bmob.v3.listener.QueryListener;


public class ShareManagerActivty extends FragmentActivity {

    private ArrayList<Promotion> myProList = new ArrayList<>();
    private XRecyclerView recyclerView;
    private MyTeamListAdapter myTeamListAdapter;
    private LoadNetView loadNetView;
    private String lastTime = "";
    private int handlerMyteamsSize;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_common);
        recyclerView = (XRecyclerView) findViewById(R.id.recyclerview);
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
                        if (handlerMyteamsSize > 0) {
                            queryData(Constants.LOADMORE);
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

        loadNetView = (LoadNetView) findViewById(R.id.loadview);
        loadNetView.setReloadButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadNetView.setlayoutVisily(Constants.LOAD);
                queryData(Constants.REFRESH);
            }
        });
        TitleBarView titlebar = (TitleBarView) findViewById(R.id.titlebar);
        titlebar.setVisibility(View.VISIBLE);
        titlebar.setBarTitle("我 的 团 队");
        titlebar.setBackButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareManagerActivty.this.finish();
            }
        });


        queryData(Constants.REFRESH);
    }


    private void queryData(final int actionType) {
        BmobQuery<Promotion> query = new BmobQuery<>();
        BmobQuery<Promotion> query2 = new BmobQuery<>();
        List<BmobQuery<Promotion>> queries = new ArrayList<>();
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
        BmobQuery<Promotion> query3 = new BmobQuery<>();

        query3.addWhereEqualTo("referralsUser", BmobUser.getCurrentUser(RootUser.class));
        queries.add(query3);
        query.and(queries);
        query.setLimit(10);
        query.order("-updatedAt");
        query.findObjects(new FindListener<Promotion>() {
            @Override
            public void done(List<Promotion> list, BmobException e) {
                if (e == null) {
                    if (actionType == Constants.REFRESH) {
                        myProList.clear();
                    }
                    if (list.size() > 0) {
                        lastTime = list.get(list.size() - 1).getUpdatedAt();
                        for (final Promotion pro : list) {
                            BmobQuery<RootUser> user=new BmobQuery<>();
                            user.getObject(pro.refereeUser.getObjectId(), new QueryListener<RootUser>() {
                                @Override
                                public void done(RootUser rootUser, BmobException e) {
                                    if(e==null){
                                        pro.refereeUser=rootUser;
                                    }
                                }
                            });
                        }
                    }
                    Message message = new Message();
                    message.obj = list;
                    message.what = actionType;
                    handler.sendMessageDelayed(message,2000);
                } else {
                    handler.sendEmptyMessage(Constants.NetWorkError);
                }
            }
        });
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            ArrayList<Promotion> myPosts = (ArrayList<Promotion>) msg.obj;
            if (msg.what != Constants.NetWorkError) {

                if (myPosts != null) {
                    myProList.addAll(myPosts);
                    handlerMyteamsSize = myPosts.size();
                } else {
                    handlerMyteamsSize = 0;
                }

                setAdapterView();
                if (msg.what == Constants.LOADMORE) {
                    recyclerView.setNoMore(true);
                }
                loadNetView.setVisibility(View.GONE);
                if (myProList.size() == 0) {
                    loadNetView.setVisibility(View.VISIBLE);
                    loadNetView.setlayoutVisily(Constants.NO_DATA);
                    return;
                }
            } else {
                loadNetView.setVisibility(View.VISIBLE);
                loadNetView.setlayoutVisily(Constants.RELOAD);
            }

        }
    };

    private void setAdapterView() {
        if (myTeamListAdapter == null) {
            myTeamListAdapter = new MyTeamListAdapter(ShareManagerActivty.this, myProList);
            recyclerView.setAdapter(myTeamListAdapter);

        } else {
            myTeamListAdapter.notifyDataSetChanged();
        }
    }



}
