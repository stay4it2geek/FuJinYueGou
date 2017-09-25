package com.act.quzhibo.ui.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.act.quzhibo.R;
import com.act.quzhibo.adapter.InteretstPostDetailAdapter;
import com.act.quzhibo.common.Constants;
import com.act.quzhibo.common.OkHttpClientManager;
import com.act.quzhibo.entity.InterestPost;
import com.act.quzhibo.entity.InterestPostDetailParentData;
import com.act.quzhibo.util.CommonUtil;
import com.act.quzhibo.view.LoadNetView;
import com.act.quzhibo.view.TitleBarView;
import com.jcodecraeer.xrecyclerview.XRecyclerView;


public class PostDetailActivity extends AppCompatActivity {
    private InteretstPostDetailAdapter adapter;
    private XRecyclerView recyclerview;
    private InterestPost post;
    private LoadNetView loadNetView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_postdetail);
        recyclerview = (XRecyclerView) findViewById(R.id.postRecyleview);
        findViewById(R.id.titlebar).setVisibility(View.VISIBLE);
        recyclerview.setHasFixedSize(true);
        recyclerview.setPullRefreshEnabled(false);
        recyclerview.setLoadingMoreEnabled(false);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerview.setLayoutManager(linearLayoutManager);
        if (getIntent() != null) {
            post = (InterestPost) getIntent().getSerializableExtra(Constants.POST);
        }
        getData();
        loadNetView = (LoadNetView) findViewById(R.id.loadview);
        loadNetView.setReloadButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadNetView.setlayoutVisily(Constants.LOAD);
                getData();
            }
        });
        TitleBarView titlebar = (TitleBarView) findViewById(R.id.titlebar);
        titlebar.setBarTitle("状 态 详 情");
        titlebar.setBackButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PostDetailActivity.this.finish();
            }
        });
    }

    private void getData() {
        String url = CommonUtil.getToggle(this, Constants.POST).getToggleObject().replace(Constants.POST, post.postId);
        OkHttpClientManager.parseRequest(this, url, handler, Constants.REFRESH);
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what != Constants.NetWorkError) {
                InterestPostDetailParentData data = CommonUtil.parseJsonWithGson((String) msg.obj, InterestPostDetailParentData.class);
                adapter = new InteretstPostDetailAdapter(post, PostDetailActivity.this, data.result);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(PostDetailActivity.this);
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

}
