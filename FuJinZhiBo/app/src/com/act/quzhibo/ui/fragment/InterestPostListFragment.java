package com.act.quzhibo.ui.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.act.quzhibo.R;
import com.act.quzhibo.adapter.InterestPostListAdapter;
import com.act.quzhibo.common.Constants;
import com.act.quzhibo.common.OkHttpClientManager;
import com.act.quzhibo.bean.InterestPost;
import com.act.quzhibo.bean.InterestPostListInfoParentData;
import com.act.quzhibo.ui.activity.SquareActivity;
import com.act.quzhibo.util.CommonUtil;
import com.act.quzhibo.custom.LoadNetView;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class InterestPostListFragment extends BackHandledFragment {
    private XRecyclerView recyclerView;
    private ArrayList<InterestPost> posts = new ArrayList<>();
    private int interestPostSize;
    private InterestPostListAdapter adapter;
    private View view;
    private String pid;
    private LoadNetView loadNetView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        view = LayoutInflater.from(getActivity()).inflate(R.layout.interest_post_layout, null, false);
        loadNetView = (LoadNetView) view.findViewById(R.id.loadview);
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
                            getData(pid, ctime, Constants.LOADMORE);
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
        getData(pid, "0", Constants.REFRESH);
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;     //截断事件的传递
            }
        });
        view.findViewById(R.id.sort).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Collections.sort(posts, new ComparatorValues());
                if (adapter != null) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        });

        loadNetView.setReloadButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadNetView.setlayoutVisily(Constants.LOAD);
                getData(pid, "0", Constants.REFRESH);
            }
        });

        loadNetView.setLoadButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadNetView.setlayoutVisily(Constants.LOAD);
                getData(pid, "0", Constants.REFRESH);
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public static  class ComparatorValues implements Comparator<InterestPost> {
        @Override
        public int compare(InterestPost post1, InterestPost post2) {
            long m1 = Long.parseLong(post1.ctime != null ? post1.ctime : "0l");
            long m2 = Long.parseLong(post2.ctime != null ? post2.ctime : "0l");
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

    private String ctime;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what != Constants.NetWorkError) {
                final InterestPostListInfoParentData data =
                        CommonUtil.parseJsonWithGson((String) msg.obj, InterestPostListInfoParentData.class);
                if (msg.what == Constants.REFRESH) {
                    posts.clear();
                }
                if (data.result != null) {
                    interestPostSize = data.result.size();
                    posts.addAll(data.result);
                } else {
                    interestPostSize = 0;
                }

                if (interestPostSize > 0) {
                    ctime = data.result.get(interestPostSize - 1).ctime;
                }
                if (adapter == null) {
                    adapter = new InterestPostListAdapter(getActivity(), posts,false);
                    adapter.setOnItemClickListener(new InterestPostListAdapter.OnInterestPostRecyclerViewItemClickListener() {
                        @Override
                        public void onItemClick(InterestPost post) {
                            IntersetPostDetailFragment fragment = new IntersetPostDetailFragment();
                            Bundle bundle = new Bundle();
                            bundle.putSerializable(Constants.POST, post);
                            fragment.setArguments(bundle);
                            CommonUtil.switchFragment(fragment, R.id.square_interest_plates_layout, getActivity());
                        }
                    });
                    recyclerView.setAdapter(adapter);
                } else {
                    adapter.notifyDataSetChanged();
                }
                loadNetView.setVisibility(View.GONE);

                if(msg.what==Constants.LOADMORE){
                    recyclerView.setNoMore(true);
                }
                if (posts.size() == 0) {
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

    public void getData(String pid, String htime, int what) {
        if (pid == null) {
            handler.sendEmptyMessage(Constants.NetWorkError);
            return;
        }
        String url = CommonUtil.getToggle(getActivity(), Constants.SQUARE_INTERES_POST).getToggleObject().replace("PID", pid).replace("HTIME", htime);
        OkHttpClientManager.parseRequest(getActivity(), url, handler,what);

    }

    @Override
    public boolean onBackPressed() {
        return false;
    }
}
