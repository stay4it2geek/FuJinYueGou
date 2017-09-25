package com.act.quzhibo.ui.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.act.quzhibo.R;
import com.act.quzhibo.adapter.InteretstPostDetailAdapter;
import com.act.quzhibo.common.Constants;
import com.act.quzhibo.common.OkHttpClientManager;
import com.act.quzhibo.entity.InterestPost;
import com.act.quzhibo.entity.InterestPostDetailParentData;
import com.act.quzhibo.util.CommonUtil;
import com.act.quzhibo.view.LoadNetView;
import com.jcodecraeer.xrecyclerview.XRecyclerView;


public class PostDetailFragment extends BackHandledFragment {
    private InteretstPostDetailAdapter adapter;
    private XRecyclerView recyclerview;
    private InterestPost post;
    private View view;
    private LoadNetView loadNetView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_postdetail, null, false);
        recyclerview = (XRecyclerView) view.findViewById(R.id.postRecyleview);
        recyclerview.setHasFixedSize(true);
        recyclerview.setPullRefreshEnabled(false);
        recyclerview.setLoadingMoreEnabled(false);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerview.setLayoutManager(linearLayoutManager);
        if (getArguments() != null) {
            post = (InterestPost) getArguments().getSerializable(Constants.POST);
        }
        getData();
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        loadNetView = (LoadNetView) view.findViewById(R.id.loadview);
        loadNetView.setReloadButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadNetView.setlayoutVisily(Constants.LOAD);
                getData();
            }
        });
        return view;
    }


    private void getData() {
        String url = CommonUtil.getToggle(getActivity(), Constants.POST).getToggleObject().replace(Constants.POST, post.postId);
        OkHttpClientManager.parseRequest(getActivity(), url, handler, Constants.REFRESH);
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what != Constants.NetWorkError) {
                InterestPostDetailParentData data = CommonUtil.parseJsonWithGson((String) msg.obj, InterestPostDetailParentData.class);
                adapter = new InteretstPostDetailAdapter(post, getActivity(), data.result);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
                linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                recyclerview.setLayoutManager(linearLayoutManager);
                recyclerview.setAdapter(adapter);
                loadNetView.setVisibility(View.GONE);
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
