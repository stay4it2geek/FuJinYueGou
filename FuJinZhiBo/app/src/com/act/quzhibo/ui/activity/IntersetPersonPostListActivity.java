package com.act.quzhibo.ui.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;

import com.act.quzhibo.R;
import com.act.quzhibo.adapter.InterestPostListAdapter;
import com.act.quzhibo.common.Constants;
import com.act.quzhibo.common.OkHttpClientManager;
import com.act.quzhibo.entity.InterestPost;
import com.act.quzhibo.entity.InterestPostListInfoPersonParentData;
import com.act.quzhibo.entity.RootUser;
import com.act.quzhibo.util.CommonUtil;
import com.act.quzhibo.view.FragmentDialog;
import com.act.quzhibo.view.LoadNetView;
import com.act.quzhibo.view.TitleBarView;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import cn.bmob.v3.BmobUser;
import okhttp3.Call;
import okhttp3.Request;


public class IntersetPersonPostListActivity extends FragmentActivity {

    private XRecyclerView recyclerView;
    private ArrayList<InterestPost> posts = new ArrayList<>();
    private int interestPostSize;
    private InterestPostListAdapter adapter;
    private String userId;
    private String ctime = "0";
    private LoadNetView loadNetView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_interest_post);
        findViewById(R.id.titlebar).setVisibility(View.VISIBLE);
        TitleBarView titlebar = (TitleBarView) findViewById(R.id.titlebar);
        titlebar.setVisibility(View.VISIBLE);
        titlebar.setBarTitle("ta的情趣状态");
        titlebar.setBackButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntersetPersonPostListActivity.this.finish();
            }
        });
        loadNetView = (LoadNetView) findViewById(R.id.loadview);
        userId = getIntent().getStringExtra(Constants.COMMON_USER_ID);
        recyclerView = (XRecyclerView) findViewById(R.id.interest_post_list);
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
                        getData("0", Constants.REFRESH);
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
                            getData(ctime, Constants.LOADMORE);
                            recyclerView.loadMoreComplete();
                        } else {
                            recyclerView.setNoMore(true);
                        }
                    }
                }, 1000);
            }
        });
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 1);
        gridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(gridLayoutManager);
        getData("0", Constants.REFRESH);
        findViewById(R.id.sort).setOnClickListener(new View.OnClickListener() {
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
                getData("0", Constants.REFRESH);
            }
        });
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what != Constants.NetWorkError) {
                final InterestPostListInfoPersonParentData data =
                        CommonUtil.parseJsonWithGson((String) msg.obj, InterestPostListInfoPersonParentData.class);
                if (data != null && data.result != null) {
                    interestPostSize = data.result.posts.size();
                } else {
                    interestPostSize = 0;
                    if (msg.what == Constants.LOADMORE) {
                        recyclerView.setNoMore(true);
                    }
                }
                if (data.result.posts != null && interestPostSize > 0) {
                    ctime = data.result.posts.get(interestPostSize - 1).ctime;
                }
                if (msg.what == Constants.REFRESH) {
                    posts.clear();
                }
                if (posts != null && interestPostSize > 0) {
                    posts.addAll(data.result.posts);
                    if (adapter == null) {
                        adapter = new InterestPostListAdapter(IntersetPersonPostListActivity.this, posts);
                        adapter.setOnItemClickListener(new InterestPostListAdapter.OnInterestPostRecyclerViewItemClickListener() {
                            @Override
                            public void onItemClick(InterestPost post) {
                                RootUser rootUser = BmobUser.getCurrentUser(RootUser.class);
                                if (rootUser == null) {
                                    FragmentDialog.newInstance(false, "请确认您的权限", "你是否未注册或者登录？", "去注册", "去登录", -1, false, new FragmentDialog.OnClickBottomListener() {
                                        @Override
                                        public void onPositiveClick(Dialog dialog, boolean needDelete) {
                                            startActivity(new Intent(IntersetPersonPostListActivity.this, RegisterActivity.class));

                                        }

                                        @Override
                                        public void onNegtiveClick(Dialog dialog) {
                                            startActivity(new Intent(IntersetPersonPostListActivity.this, LoginActivity.class));

                                        }
                                    }).show(getSupportFragmentManager(), "");
                                } else if (rootUser != null && rootUser.vipConis < 1000) {
                                    FragmentDialog.newInstance(false, "请确认您的趣币数量", "您的趣币少于1000个了", "去充值", "取消", -1, false, new FragmentDialog.OnClickBottomListener() {
                                        @Override
                                        public void onPositiveClick(Dialog dialog, boolean needDelete) {
                                            startActivity(new Intent(IntersetPersonPostListActivity.this, GetVipPayActivity.class));
                                        }

                                        @Override
                                        public void onNegtiveClick(Dialog dialog) {
                                            dialog.dismiss();
                                        }
                                    }).show(getSupportFragmentManager(), "");

                                } else if (rootUser != null && rootUser.vipConis > 1000) {
                                    Intent intent = new Intent();
                                    intent.putExtra(Constants.POST, post);
                                    intent.setClass(IntersetPersonPostListActivity.this, PostDetailActivity.class);
                                    startActivity(intent);
                                }
                            }
                        });
                        recyclerView.setAdapter(adapter);
                    } else {
                        adapter.notifyDataSetChanged();
                    }
                } else if (interestPostSize == 0) {
                    recyclerView.setNoMore(true);
                }
                loadNetView.setVisibility(View.GONE);
            } else {
                loadNetView.setVisibility(View.VISIBLE);
                loadNetView.setlayoutVisily(Constants.RELOAD);
            }


        }
    };


    public static final class ComparatorValues implements Comparator<InterestPost> {

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

    public void getData(String ctime,int what) {
        if (userId == null) {
            handler.sendEmptyMessage(Constants.NetWorkError);
            return;
        }
        String url = CommonUtil.getToggle(this, Constants.TEXT_IMG_POST).getToggleObject().replace("USERID", userId).replace("CTIME", ctime);
        OkHttpClientManager.parseRequest(this, url, handler, what);
    }

}
