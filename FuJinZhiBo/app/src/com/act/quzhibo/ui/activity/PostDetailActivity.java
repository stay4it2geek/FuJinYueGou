package com.act.quzhibo.ui.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;

import com.act.quzhibo.R;
import com.act.quzhibo.adapter.InteretstPostPageAdapter;
import com.act.quzhibo.common.Constants;
import com.act.quzhibo.entity.InterestPost;
import com.act.quzhibo.entity.InterestPostPageParentData;
import com.act.quzhibo.okhttp.OkHttpUtils;
import com.act.quzhibo.okhttp.callback.StringCallback;
import com.act.quzhibo.util.CommonUtil;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import okhttp3.Call;

/**
 * Created by weiminglin on 17/6/4.
 */

public class PostDetailActivity extends AppCompatActivity {
    private InteretstPostPageAdapter adapter;
    private XRecyclerView recyclerview;
    private InterestPost post;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_postdetail);
        recyclerview = (XRecyclerView) findViewById(R.id.postRecyleview);
        recyclerview.setHasFixedSize(true);
        recyclerview.setPullRefreshEnabled(false);
        recyclerview.setLoadingMoreEnabled(false);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerview.setLayoutManager(linearLayoutManager);
        if (getIntent() != null) {
            post = (InterestPost)getIntent().getSerializableExtra(Constants.POST_ID);
        }
        getData();
    }

    private void getData() {
        OkHttpUtils.get().url(CommonUtil.getToggle(this, Constants.POST_ID).getToggleObject().replace(Constants.POST_ID, post.postId)).build().execute(new StringCallback() {
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
            adapter = new InteretstPostPageAdapter(post, PostDetailActivity.this, data.result);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(PostDetailActivity.this);
            linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerview.setLayoutManager(linearLayoutManager);
            recyclerview.setAdapter(adapter);
        }
    };

}
