package com.act.quzhibo.ui.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.act.quzhibo.R;
import com.act.quzhibo.adapter.InterestPostListAdapter;
import com.act.quzhibo.common.Constants;
import com.act.quzhibo.entity.InterestPost;
import com.act.quzhibo.entity.InterestPostListInfoParentData;
import com.act.quzhibo.entity.InterstPostListResult;

import com.act.quzhibo.okhttp.OkHttpUtils;
import com.act.quzhibo.okhttp.callback.StringCallback;
import com.act.quzhibo.ui.activity.CommonPersonPostActivity;
import com.act.quzhibo.ui.activity.SquareActivity;
import com.act.quzhibo.util.CommonUtil;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import okhttp3.Call;

/**
 * Created by weiminglin on 17/6/1.
 * 情趣帖子
 */

public class InterestPostListFragment extends BackHandledFragment {
    private XRecyclerView recyclerView;
    private ArrayList<InterestPost> posts = new ArrayList<>();
    private int interestPostSize;
    private InterestPostListAdapter adapter;
    private View view;
    private String pid;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_interest_post, null, false);
        pid = ((SquareActivity) getActivity()).getPid();
        recyclerView = (XRecyclerView) view.findViewById(R.id.interest_post_list);
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
                        getData(pid, "0", Constants.REFRESH);
                        recyclerView.refreshComplete();
                    }
                }, 1000);
            }

            @Override
            public void onLoadMore() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (interestPostSize > 0) {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    getData(pid, htime, Constants.LOADMORE);
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
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 1);
        gridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(gridLayoutManager);
        getData(pid, "0", Constants.REFRESH);
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;     //截断事件的传递
            }
        });
        return view;
    }

    public static final class ComparatorValues implements Comparator<InterestPost> {

        @Override
        public int compare(InterestPost post1, InterestPost post2) {
            long m1 = Long.parseLong(post1.ctime != null ? post1.ctime : "0l");
            long m2= Long.parseLong(post2.ctime != null ? post2.ctime : "0l");
            int result = 0;
            if (m1 > m2) {
                result = -1;
            }
            if (m1 < m2) {
                result = 1;
            }
            return result;
        }

    }

    private String htime;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if (msg.what != Constants.NetWorkError) {
                final InterestPostListInfoParentData data =
                        CommonUtil.parseJsonWithGson((String) msg.obj, InterestPostListInfoParentData.class);

                if (data.result!= null && data.result.size() > 0) {
                    interestPostSize = data.result.size();
                    htime = data.result.get(interestPostSize - 1).htime;
                }
                if (data.result.size() > 0) {
                    htime = data.result.get(data.result.size() - 1).htime;
                }
                if (msg.what == Constants.REFRESH) {
                    posts.clear();
                }
                if (data.result!= null) {
                    interestPostSize = data.result.size();
                }
                if (posts != null && data.result.size() > 0) {
                    posts.addAll(data.result);
                    Collections.sort(posts, new ComparatorValues());
                    if (adapter == null) {
                        adapter = new InterestPostListAdapter(getActivity(), posts);
                        adapter.setOnItemClickListener(new InterestPostListAdapter.OnInterestPostRecyclerViewItemClickListener() {
                            @Override
                            public void onItemClick(InterestPost post) {
                                PostDetailFragment fragment = new PostDetailFragment();
                                Bundle bundle = new Bundle();
                                bundle.putSerializable(Constants.POST_ID, post);
                                fragment.setArguments(bundle);
                                CommonUtil.switchFragment(fragment, R.id.square_interest_plates_layout, getActivity());
                            }
                        });
                        recyclerView.setAdapter(adapter);
                    } else {
                        adapter.notifyDataSetChanged();
                    }
                } else if (data.result.size() == 0) {
                    recyclerView.setNoMore(true);
                }
            } else {
                //todo error
            }
        }
    };


    public void getData(String pid, String htime, final int what) {
        String url = CommonUtil.getToggle(getActivity(), Constants.SQUARE_INTERES_POST).getToggleObject().replace("PID", pid).replace("HTIME", htime);
      Log.e("url",url);
        OkHttpUtils.get().url(url).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                handler.sendEmptyMessage(Constants.NetWorkError);
            }

            @Override
            public void onResponse(String response, int id) {
                Message message = handler.obtainMessage();
                message.obj = response;
                message.what = what;
                handler.sendMessage(message);
            }
        });
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }
}
