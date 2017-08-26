package com.act.quzhibo.ui.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.act.quzhibo.R;
import com.act.quzhibo.adapter.MyPostPageAdapter;
import com.act.quzhibo.common.Constants;
import com.act.quzhibo.entity.MyPost;
import com.act.quzhibo.view.LoadNetView;
import com.act.quzhibo.view.TitleBarView;
import com.jcodecraeer.xrecyclerview.XRecyclerView;


public class MyPostDetailActivity extends AppCompatActivity {
    private MyPostPageAdapter adapter;
    private XRecyclerView recyclerview;
    private MyPost post;
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
        loadNetView = (LoadNetView) findViewById(R.id.loadview);
        loadNetView.setVisibility(View.VISIBLE);
        if (getIntent() != null) {
            post = (MyPost) getIntent().getSerializableExtra(Constants.POST_ID);
            if (post == null) {
                return;
            }
            adapter = new MyPostPageAdapter(post, MyPostDetailActivity.this);
            recyclerview.setAdapter(adapter);
            loadNetView.setVisibility(View.GONE);
        }


        TitleBarView titlebar = (TitleBarView) findViewById(R.id.titlebar);
        titlebar.setBarTitle("状 态 详 情");
        titlebar.setBackButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyPostDetailActivity.this.finish();
            }
        });
    }

}


