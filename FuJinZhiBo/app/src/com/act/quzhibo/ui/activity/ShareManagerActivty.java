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
import com.act.quzhibo.i.OnQueryDataListner;
import com.act.quzhibo.util.CommonUtil;
import com.act.quzhibo.util.ViewDataUtil;
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

    ArrayList<Promotion> myProList = new ArrayList<>();

    XRecyclerView recyclerView;
    MyTeamListAdapter myTeamListAdapter;
    LoadNetView loadNetView;
    String lastTime = "";
    int handlerMyteamsSize;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_common);
        recyclerView = (XRecyclerView) findViewById(R.id.recyclerview);
        ViewDataUtil.setLayManager(handlerMyteamsSize, new OnQueryDataListner() {
            @Override
            public void onRefresh() {
                queryData(Constants.REFRESH);
            }

            @Override
            public void onLoadMore() {
                queryData(Constants.LOADMORE);
            }
        }, this, recyclerView, 1, true, true);

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


    void queryData(final int actionType) {
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
            public void done(final List<Promotion> list, BmobException e) {
                if (e == null) {
                    if (actionType == Constants.REFRESH) {
                        myProList.clear();
                        if (myTeamListAdapter != null) {
                            myTeamListAdapter.notifyDataSetChanged();
                        }
                    }

                    if (list != null && list.size() > 0) {
                        lastTime = list.get(list.size() - 1).getUpdatedAt();
                       setAdapterView(actionType, list);
                    }
                } else {
                    setAdapterView(Constants.NetWorkError, null);
                }
            }
        });
    }

    void setAdapterView(int what, List<Promotion> promotions) {
        if (what != Constants.NetWorkError) {
            if (myProList != null) {
                myProList.addAll(promotions);
                handlerMyteamsSize = promotions.size();
            } else {
                handlerMyteamsSize = 0;
            }
            if (myTeamListAdapter == null) {
                myTeamListAdapter = new MyTeamListAdapter(ShareManagerActivty.this, myProList);
                recyclerView.setAdapter(myTeamListAdapter);

            } else {
                myTeamListAdapter.notifyDataSetChanged();
            }
            if (what == Constants.LOADMORE) {
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
}



