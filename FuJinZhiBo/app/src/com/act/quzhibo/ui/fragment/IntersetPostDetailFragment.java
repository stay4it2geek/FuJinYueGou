package com.act.quzhibo.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.act.quzhibo.R;
import com.act.quzhibo.adapter.InteretstPostDetailAdapter;
import com.act.quzhibo.common.Constants;
import com.act.quzhibo.common.OkHttpClientManager;
import com.act.quzhibo.bean.InterestPost;
import com.act.quzhibo.bean.InterestPostDetailParentData;
import com.act.quzhibo.i.OnQueryDataListner;
import com.act.quzhibo.util.CommonUtil;
import com.act.quzhibo.util.ToastUtil;
import com.act.quzhibo.util.ViewDataUtil;
import com.act.quzhibo.widget.LoadNetView;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.rockerhieu.emojicon.EmojiconEditText;


public class IntersetPostDetailFragment extends BackHandledFragment {
     InteretstPostDetailAdapter adapter;
     XRecyclerView recyclerview;
     InterestPost post;
     View view;
     LoadNetView loadNetView;
     EmojiconEditText commentET;
     TextView commentBtn;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        view = LayoutInflater.from(getActivity()).inflate(R.layout.interest_post_detail_layout, null, false);
        recyclerview = (XRecyclerView) view.findViewById(R.id.postRecyleview);
        ViewDataUtil.setLayManager(0, null, getActivity(), recyclerview, 1, false, false);
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
        commentBtn = (TextView) view.findViewById(R.id.commentBtn);
        commentET = (EmojiconEditText) view.findViewById(R.id.comment_et);
        commentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((commentET.getText().equals("点击这里评论她/他") || commentET.getText().length() == 0)) {
                    ToastUtil.showToast(getActivity(), "您是否忘记了评论内容?");
                } else {
                    ToastUtil.showToast(getActivity(), "正在评论...");
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtil.showToast(getActivity(), "评论已提交审核");
                            commentET.setText("");
                            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(commentBtn.getWindowToken(), 0);
                        }
                    }, 1000);
                }
            }
        });

        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        return view;
    }


     void getData() {
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
