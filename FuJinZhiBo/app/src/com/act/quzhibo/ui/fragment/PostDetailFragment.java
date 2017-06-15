package com.act.quzhibo.ui.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.act.quzhibo.R;
import com.act.quzhibo.adapter.InteretstPostPageAdapter;
import com.act.quzhibo.common.Constants;
import com.act.quzhibo.entity.InterestPost;
import com.act.quzhibo.entity.InterestPostPageDetailAndComments;
import com.act.quzhibo.entity.InterestPostPageParentData;
import com.act.quzhibo.okhttp.OkHttpUtils;
import com.act.quzhibo.okhttp.callback.StringCallback;
import com.act.quzhibo.util.CommonUtil;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import okhttp3.Call;

/**
 * Created by weiminglin on 17/6/4.
 */

public class PostDetailFragment extends BackHandledFragment {
    private InteretstPostPageAdapter adapter;
    private XRecyclerView recyclerview;
    private InterestPost post;
    private View view;

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
            post = (InterestPost)getArguments().getSerializable(Constants.POST_ID);
        }
        getData();
        return view;
    }

    private void getData() {
        OkHttpUtils.get().url(CommonUtil.getToggle(getActivity(), Constants.POST_ID).getToggleObject().replace(Constants.POST_ID, post.postId)).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
            }

            @Override
            public void onResponse(String response, int id) {
                InterestPostPageParentData interestPostPageParentData =
                        CommonUtil.parseJsonWithGson(response, InterestPostPageParentData.class);
                Message message = handler.obtainMessage();
                message.obj = interestPostPageParentData;
                handler.sendMessage(message);
            }
        });
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            InterestPostPageParentData data = (InterestPostPageParentData) msg.obj;
            adapter = new InteretstPostPageAdapter(post, getActivity(), data.result);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
            linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerview.setLayoutManager(linearLayoutManager);
            recyclerview.setAdapter(adapter);
        }
    };

    @Override
    public boolean onBackPressed() {
        return false;
    }
}
