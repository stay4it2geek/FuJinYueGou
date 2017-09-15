package com.act.quzhibo.ui.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.act.quzhibo.R;
import com.act.quzhibo.adapter.NearPersonAdapter;
import com.act.quzhibo.common.Constants;
import com.act.quzhibo.entity.NearPerson;
import com.act.quzhibo.view.LoadNetView;
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


public class NearFragment extends BackHandledFragment {

    private LoadNetView loadNetView;
    View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = LayoutInflater.from(getActivity()).inflate(R.layout.layout_common, null, false);
        recyclerView = (XRecyclerView) view.findViewById(R.id.recycler_view);
        loadNetView = (LoadNetView) view.findViewById(R.id.loadview);
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
                        if (nearPersonSize > 0) {
                            queryData(Constants.LOADMORE);
                            recyclerView.loadMoreComplete();
                        } else {
                            recyclerView.setNoMore(true);
                        }
                    }
                }, 1000);
            }
        });
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        gridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(gridLayoutManager);
        queryData(Constants.REFRESH);
//        view.findViewById(R.id.sort).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Collections.sort(posts,ComparatorValues());
//                if(adapter!=null){
//                    handler.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            adapter.notifyDataSetChanged();
//                        }
//                    });
//                }
//            }
//        });
        loadNetView.setReloadButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadNetView.setlayoutVisily(Constants.LOAD);
                queryData(Constants.REFRESH);
            }
        });
        return view;
    }

    private XRecyclerView recyclerView;
    NearPersonAdapter nearPersonAdapter;
    ArrayList<NearPerson> nearArrayList = new ArrayList<>();
    public String lastTime;

    private void queryData(final int actionType) {
        final BmobQuery<NearPerson> query = new BmobQuery<>();
        query.setLimit(10);
        if (actionType == Constants.LOADMORE) {
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
        query.findObjects(new FindListener<NearPerson>() {

            @Override
            public void done(List<NearPerson> list, BmobException e) {
                if (e == null) {
                    if (actionType == Constants.REFRESH) {
                        nearArrayList.clear();
                    }
                    lastTime = list.get(list.size() - 1).getUpdatedAt();
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

    private int nearPersonSize;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            ArrayList<NearPerson> nearPersonList = (ArrayList<NearPerson>) msg.obj;
            if (msg.what != Constants.NetWorkError) {
                if (nearArrayList != null) {
                    nearPersonSize = nearPersonList.size();
                    nearArrayList.addAll(nearPersonList);
                } else {
                    nearPersonSize = 0;
                }
                if (nearPersonSize > 0) {
                    if (nearPersonAdapter == null) {
                        nearPersonAdapter = new NearPersonAdapter(getActivity(), nearArrayList);
                        recyclerView.setAdapter(nearPersonAdapter);
                    } else {
                        nearPersonAdapter.notifyDataSetChanged();
                    }
                }
                loadNetView.setVisibility(View.GONE);
                if (nearArrayList.size() == 0) {
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

    @Override
    public boolean onBackPressed() {
        return false;
    }

}
