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
import com.act.quzhibo.adapter.CommonSeeAdapter;
import com.act.quzhibo.common.Constants;
import com.act.quzhibo.entity.CommonSeePerson;
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

/**
 * Created by asus-pc on 2017/6/18.
 */

public class CommonSeeFragment extends BackHandledFragment {

    private XRecyclerView recyclerView;
    private CommonSeeAdapter commonSeeAdapter;
    private  View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_common, null, false);
        recyclerView = (XRecyclerView) view.findViewById(R.id.recycler_view);
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
                        queryData(Constants.LOADMORE);
                        recyclerView.loadMoreComplete();
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



    private int limit = 10; // 每页的数据是10条
    String lastTime = "";
    ArrayList<CommonSeePerson> commonSeePersonArrayList = new ArrayList<>();

    /**
     * 分页获取数据
     *
     * @param actionType
     */
    private void queryData(final int actionType) {
        final BmobQuery<CommonSeePerson> query = new BmobQuery<>();
        query.setLimit(limit);
        // 如果是加载更多
        if (actionType == Constants.LOADMORE) {
            // 只查询小于最后一个item发表时间的数据
            Date date = null;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                date = sdf.parse(lastTime);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            query.addWhereLessThanOrEqualTo("createdAt", new BmobDate(date));
        }
        query.order("-createdAt");
        query.findObjects(new FindListener<CommonSeePerson>() {
            @Override
            public void done(List<CommonSeePerson> list, BmobException e) {
                if (e == null) {
                    if (list.size() > 0) {
                        if (actionType == Constants.REFRESH) {
                            // 当是下拉刷新操作时，将当前页的编号重置为0，并把bankCards清空，重新添加
                            commonSeePersonArrayList.clear();
                            lastTime = list.get(list.size() - 1).getCreatedAt();
                            commonSeePersonArrayList.addAll(list);
                        } else if (actionType == Constants.LOADMORE) {
                            commonSeePersonArrayList.addAll(list);
                            lastTime = list.get(list.size() - 1).getCreatedAt();
                        }
                        Message message = new Message();
                        message.obj = commonSeePersonArrayList;
                        message.what = actionType;
                        handler.sendMessage(message);
                    } else {
                        handler.sendEmptyMessage(Constants.NO_MORE);
                    }
                }
            }
        });
    }

    private int seeMeSize;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            ArrayList<CommonSeePerson> commonSeePersonArrayList = (ArrayList<CommonSeePerson>) msg.obj;
            if (msg.what != Constants.NetWorkError) {
                if (msg.what != Constants.NO_MORE) {
                    if (commonSeePersonArrayList != null) {
                        seeMeSize = commonSeePersonArrayList.size();
                    }
                    if (seeMeSize > 0) {
                        if (commonSeeAdapter == null) {
                            commonSeeAdapter = new CommonSeeAdapter(getActivity(), commonSeePersonArrayList);
                            recyclerView.setAdapter(commonSeeAdapter);
                        } else {
                            commonSeeAdapter.notifyDataSetChanged();
                        }
                    }
                    recyclerView.setHasFixedSize(true);
                } else {
                    recyclerView.setNoMore(true);
                }
            } else {

            }
        }
    };

    @Override
    public boolean onBackPressed() {
        return false;
    }
}
