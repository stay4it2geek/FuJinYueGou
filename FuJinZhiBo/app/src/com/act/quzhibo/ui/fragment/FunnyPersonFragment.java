package com.act.quzhibo.ui.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.act.quzhibo.R;
import com.act.quzhibo.adapter.InterestListAdapter;
import com.act.quzhibo.common.Constants;
import com.act.quzhibo.entity.InterestPlates;
import com.act.quzhibo.entity.InterestPlatesDetail;
import com.act.quzhibo.okhttp.OkHttpUtils;
import com.act.quzhibo.okhttp.callback.StringCallback;
import com.act.quzhibo.util.CommonUtil;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import java.util.ArrayList;

import okhttp3.Call;


/**
 * 情趣板块
 */
public class FunnyPersonFragment extends Fragment {
    private XRecyclerView recyclerview;
    private ArrayList<InterestPlatesDetail> interestPlatesDetails = new ArrayList<>();
    private InterestListAdapter adapter;
    private ArrayList<InterestPlatesDetail> details = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_square_common, null, false);
        recyclerview = (XRecyclerView) view.findViewById(R.id.recycler_view);
        recyclerview.setHasFixedSize(true);
        recyclerview.setLoadingMoreProgressStyle(R.style.Small);
        recyclerview.setPullRefreshEnabled(true);
        recyclerview.setLoadingMoreEnabled(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerview.setLayoutManager(linearLayoutManager);
        recyclerview.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getData(Constants.REFRESH);
                    }
                }, 1000);
            }

            @Override
            public void onLoadMore() {
                if (mCurrentCounter < TOTAL_COUNTER) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            getData(Constants.LOADMORE);
                        }
                    }, 1000);
                } else {
                    recyclerview.setNoMore(true);
                }

            }
        });
        getData(Constants.REFRESH);
        return view;
    }

    private int mCurrentCounter = 0;
    private int TOTAL_COUNTER = 100;

    private void getData(final int what) {
        OkHttpUtils.get().url(CommonUtil.getToggle(getActivity(), Constants.SQUARE_INTERES_TAB).getToggleObject()).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
            }

            @Override
            public void onResponse(String response, int id) {
                interestPlatesDetails.clear();
                InterestPlates interestPlates = CommonUtil.parseJsonWithGson(response, InterestPlates.class);
                interestPlatesDetails.addAll(interestPlates.result.plates);
                Message message = handler.obtainMessage();
                message.obj = interestPlatesDetails;
                message.what = what;
                handler.sendMessage(message);
            }
        });
    }


    Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case Constants.LOADMORE:
                    details.addAll((ArrayList<InterestPlatesDetail>) msg.obj);
                    recyclerview.loadMoreComplete();
                    mCurrentCounter += details.size();
                    break;

                case Constants.REFRESH:
                    details.clear();
                    mCurrentCounter = 0;
                    details.addAll((ArrayList<InterestPlatesDetail>) msg.obj);
                    recyclerview.refreshComplete();
                    recyclerview.setNoMore(false);
                    recyclerview.setLoadingMoreEnabled(true);
                    break;
            }

            if (adapter == null) {
                adapter = new InterestListAdapter(getContext(), details);
                adapter.setOnItemClickListener(new InterestListAdapter.OnRecyclerViewItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position, String pid) {
                        initFragment(pid);
                    }
                });
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
                linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                recyclerview.setLayoutManager(linearLayoutManager);
                recyclerview.setAdapter(adapter);
            } else {
                adapter.notifyDataSetChanged();
            }


        }
    };


    InterestPostFragment interestPostFragment;

    private void initFragment(String pid) {
        //开启事务，fragment的控制是由事务来实现的
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        //add,初始化fragment并添加到事务中，如果为null就new一个
        if (interestPostFragment == null) {
            interestPostFragment = new InterestPostFragment();
            Bundle bundle = new Bundle();
            bundle.putString(Constants.PID, pid);
            interestPostFragment.setArguments(bundle);
            transaction.add(R.id.main_frame_layout, interestPostFragment);
        }
        transaction.addToBackStack("post");
        //提交事务
        transaction.commitAllowingStateLoss();
    }

}
