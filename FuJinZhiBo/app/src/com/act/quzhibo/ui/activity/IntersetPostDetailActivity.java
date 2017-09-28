package com.act.quzhibo.ui.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.act.quzhibo.R;
import com.act.quzhibo.adapter.InteretstPostDetailAdapter;
import com.act.quzhibo.common.Constants;
import com.act.quzhibo.common.OkHttpClientManager;
import com.act.quzhibo.entity.InterestPost;
import com.act.quzhibo.entity.InterestPostDetailParentData;
import com.act.quzhibo.util.CommonUtil;
import com.act.quzhibo.util.ToastUtil;
import com.act.quzhibo.view.LoadNetView;
import com.act.quzhibo.view.TitleBarView;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import io.github.rockerhieu.emojicon.EmojiconEditText;


public class IntersetPostDetailActivity extends AppCompatActivity {
    private InteretstPostDetailAdapter adapter;
    private XRecyclerView recyclerview;
    private InterestPost post;
    private LoadNetView loadNetView;
    private EmojiconEditText commentET;
    private TextView commentBtn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.interest_post_detail_layout);
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
        findViewById(R.id.root_view).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;     //截断事件的传递
            }
        });
        TitleBarView titlebar = (TitleBarView) findViewById(R.id.titlebar);
        titlebar.setBarTitle("状 态 详 情");
        titlebar.setBackButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntersetPostDetailActivity.this.finish();
            }
        });findViewById(R.id.commentBtn);
        commentET = (EmojiconEditText) findViewById(R.id.comment_et);
        commentBtn = (TextView)findViewById(R.id.commentBtn);
        commentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((commentET.getText().equals("点击这里评论她/他") || commentET.getText().length() == 0)) {
                    ToastUtil.showToast(IntersetPostDetailActivity.this, "您是否忘记了评论内容?");
                } else {
                    ToastUtil.showToast(IntersetPostDetailActivity.this, "正在评论...");
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtil.showToast(IntersetPostDetailActivity.this, "评论已提交审核");
                            commentET.setText("");
                            InputMethodManager imm = (InputMethodManager) IntersetPostDetailActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(commentBtn.getWindowToken(), 0);
                        }
                    }, 1000);
                }
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
                adapter = new InteretstPostDetailAdapter(post, IntersetPostDetailActivity.this, data.result);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(IntersetPostDetailActivity.this);
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