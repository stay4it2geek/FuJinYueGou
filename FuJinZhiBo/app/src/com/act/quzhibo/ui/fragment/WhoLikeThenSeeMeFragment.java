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
import com.act.quzhibo.adapter.WhoLikeMeAdapter;
import com.act.quzhibo.common.Constants;
import com.act.quzhibo.entity.InterestParentPerson;
import com.act.quzhibo.entity.InterestSubPerson;
import com.act.quzhibo.view.LoadNetView;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class WhoLikeThenSeeMeFragment extends BackHandledFragment {
    private ArrayList<InterestSubPerson> interestPersonList = new ArrayList<>();
    private WhoLikeMeAdapter whoLikeMeAdapter;
    private XRecyclerView recyclerView;
    private LoadNetView loadNetView;
    private int liekThenSeeMeSize;
    private String lastTime = "";
    private View view;

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
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 1);
        gridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(gridLayoutManager);
        queryData(Constants.REFRESH);
        return view;
    }

    private void queryData(final int actionType) {
        List<BmobQuery<InterestSubPerson>> queries = new ArrayList<>();
        BmobQuery<InterestSubPerson> query = new BmobQuery<>();
        query.setLimit(10);
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
        query.and(queries);
        query.order("-updatedAt");
        query.findObjects(new FindListener<InterestSubPerson>() {
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

    public static class ComparatorValues implements Comparator<InterestParentPerson> {
        @Override
        public int compare(InterestParentPerson person1, InterestParentPerson person2) {
            int m1 = Integer.parseInt(person1.viewTime != null ? person1.viewTime : "0");
            int m2 = Integer.parseInt(person2.viewTime != null ? person2.viewTime : "0");
            int result = 0;
            if (m1 > m2) {
                result = 1;
            }
            if (m1 < m2) {
                result = -1;
            }
            return result;
        }
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
                }
                Collections.sort(interestPersonList, new ComparatorValues());
                if (whoLikeMeAdapter == null) {
                    whoLikeMeAdapter = new WhoLikeMeAdapter(getActivity(), interestPersonList);
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

    @Override
    public boolean onBackPressed() {
        return false;
    }
}
