package com.act.quzhibo.ui.fragment;

import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.act.quzhibo.R;
import com.act.quzhibo.adapter.InterestPostListAdapter;
import com.act.quzhibo.common.Constants;
import com.act.quzhibo.entity.InterestPost;
import com.act.quzhibo.entity.InterstPostResult;
import com.act.quzhibo.entity.InterstUser;
import com.act.quzhibo.okhttp.OkHttpUtils;
import com.act.quzhibo.okhttp.callback.StringCallback;
import com.act.quzhibo.ui.activity.SquareActivity;
import com.act.quzhibo.util.CommonUtil;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.Call;

/**
 * Created by weiminglin on 17/6/1.
 * 情趣帖子
 */

public class InterestPostFragment extends BackHandledFragment {
    private XRecyclerView recyclerView;
    private ArrayList<InterestPost> posts = new ArrayList<>();
    private int interestPostSize;
    private InterestPostListAdapter adapter;
    private View view;
    private String pid;
    private int num;

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
                        getData(pid, 0, Constants.REFRESH);
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
                                    getData(pid, num, Constants.LOADMORE);
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
        getData(pid, 0, Constants.REFRESH);
        return view;
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            final InterstPostResult interstPostResult = CommonUtil.parseJsonWithGson((String) msg.obj, InterstPostResult.class);
            if (msg.what != Constants.NetWorkError) {
                if (msg.what == Constants.REFRESH) {
                    posts.clear();
                    num = 1;
                } else if (msg.what == Constants.LOADMORE) {
                    num += 1;
                }
                interestPostSize = interstPostResult.result.size();
                if (posts != null && interstPostResult.result.size() > 0) {
                    posts.addAll(interstPostResult.result);
                    if (adapter == null) {
                        adapter = new InterestPostListAdapter(getActivity(), posts);
                        adapter.setOnItemClickListener(new InterestPostListAdapter.OnInterestPostRecyclerViewItemClickListener() {
                            @Override
                            public void onItemClick(InterestPost post) {
                                PostDetailFragment fragment = new PostDetailFragment();
                                Bundle bundle = new Bundle();
                                bundle.putSerializable(Constants.POST_USER, post);
                                fragment.setArguments(bundle);
                                CommonUtil.switchFragment(fragment, R.id.square_interest_plates_layout, getActivity());
                                getActivity().getSupportFragmentManager().beginTransaction().hide(InterestPostFragment.this);
                            }
                        });
                        recyclerView.setAdapter(adapter);
                    } else {
                        adapter.notifyDataSetChanged();
                    }
                } else if (interstPostResult.result.size() == 0) {
                    recyclerView.setNoMore(true);
                }
            } else {
                //todo error
            }
        }
    };


    public void getData(String pid, int num, final int what) {
        String htime = CommonUtil.dateToStamp(CommonUtil.getDataString(num));
        String url = CommonUtil.getToggle(getActivity(), Constants.SQUARE_INTERES_POST).getToggleObject().replace("PID", pid).replace("HTIME", htime);
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
        getActivity().getSupportFragmentManager().beginTransaction().show(InterestPostFragment.this);
        return false;
    }
}
