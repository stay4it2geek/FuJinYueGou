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
import com.act.quzhibo.adapter.WhoLikeMeAdapter;
import com.act.quzhibo.common.Constants;
import com.act.quzhibo.entity.InterestSubPerson;
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

public class WhoLikeThenSeeMeActivity extends FragmentActivity {
    private ArrayList<InterestSubPerson> interestPersonList = new ArrayList<>();
    private WhoLikeMeAdapter whoLikeMeAdapter;
    private XRecyclerView recyclerView;
    private LoadNetView loadNetView;
    private int liekThenSeeMeSize;
    private String lastTime = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_common);
        TitleBarView titlebar = (TitleBarView) findViewById(R.id.titlebar);
        titlebar.setVisibility(View.VISIBLE);
        titlebar.setBarTitle("谁来看过我");
        titlebar.setBackButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WhoLikeThenSeeMeActivity.this.finish();
            }
        });
        recyclerView = (XRecyclerView) findViewById(R.id.recycler_view);
        loadNetView = (LoadNetView) findViewById(R.id.loadview);
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
                        if (liekThenSeeMeSize > 0) {
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
        queryData(Constants.REFRESH);
        loadNetView.setReloadButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadNetView.setlayoutVisily(Constants.LOAD);
                queryData(Constants.REFRESH);
            }
        });
        loadNetView.setLoadButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadNetView.setlayoutVisily(Constants.LOAD);
                queryData(Constants.REFRESH);
            }
        });
    }

    

    private void queryData(final int actionType) {
        List<BmobQuery<InterestSubPerson>> queries = new ArrayList<>();
        BmobQuery<InterestSubPerson> query = new BmobQuery<>();
        BmobQuery<InterestSubPerson> query3 = new BmobQuery<>();

        if (actionType == Constants.LOADMORE) {
            Date date;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                date = sdf.parse(lastTime);
                query.addWhereLessThanOrEqualTo("updatedAt", new BmobDate(date));
                queries.add(query);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        BmobQuery<InterestSubPerson> query2 = new BmobQuery<>();
        query2.addWhereEqualTo("seeMeFlag", true);
        queries.add(query2);
        query3.and(queries);
        query3.setLimit(10);
        query3.order("-distance");
        query3.findObjects(new FindListener<InterestSubPerson>() {
            @Override
            public void done(List<InterestSubPerson> list, BmobException e) {
                if (e == null) {
                    if (actionType == Constants.REFRESH) {
                        interestPersonList.clear();
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


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            ArrayList<InterestSubPerson> interestSubPersonsn = (ArrayList<InterestSubPerson>) msg.obj;
            if (msg.what != Constants.NetWorkError) {
                if (interestSubPersonsn != null) {
                    interestPersonList.addAll(interestSubPersonsn);
                    liekThenSeeMeSize = interestPersonList.size();
                } else {
                    liekThenSeeMeSize = 0;
                    if(msg.what==Constants.LOADMORE){
                        recyclerView.setNoMore(true);
                    }
                }
                if (whoLikeMeAdapter == null) {
                    whoLikeMeAdapter = new WhoLikeMeAdapter(WhoLikeThenSeeMeActivity.this, interestPersonList);
                    recyclerView.setAdapter(whoLikeMeAdapter);
                } else {
                    whoLikeMeAdapter.notifyDataSetChanged();
                }
                loadNetView.setVisibility(View.GONE);
                if (interestPersonList.size() == 0) {
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
}