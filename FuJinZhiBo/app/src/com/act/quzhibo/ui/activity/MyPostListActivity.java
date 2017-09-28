package com.act.quzhibo.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.act.quzhibo.R;
import com.act.quzhibo.adapter.MyPostListAdapter;
import com.act.quzhibo.common.Constants;
import com.act.quzhibo.entity.MyPost;
import com.act.quzhibo.entity.RootUser;
import com.act.quzhibo.view.LoadNetView;
import com.act.quzhibo.view.TitleBarView;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.mabeijianxi.smallvideorecord2.MediaRecorderActivity;
import com.mabeijianxi.smallvideorecord2.model.MediaRecorderConfig;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

import static com.act.quzhibo.ui.activity.PostAddActivity.EXTRA_MOMENT;
import static com.mabeijianxi.smallvideorecord2.MediaRecorderActivity.MEDIA_RECORDER_CONFIG_KEY;
import static com.mabeijianxi.smallvideorecord2.MediaRecorderActivity.OVER_ACTIVITY_NAME;

public class MyPostListActivity extends AppCompatActivity {
    public static final int UPLOAD_POST = 1;
    private ArrayList<MyPost> myPostList = new ArrayList<>();
    private XRecyclerView recyclerView;
    private MyPostListAdapter myPostListAdapter;
    private LoadNetView loadNetView;
    private String lastTime = "";
    private int myPostsSize;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_common);
        recyclerView = (XRecyclerView) findViewById(R.id.recycler_view);
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
                        queryData(Constants.REFRESH);
                        recyclerView.refreshComplete();
                    }
                }, 1000);
            }

            @Override
            public void onLoadMore() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (myPostsSize > 0) {
                            queryData(Constants.LOADMORE);
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

        loadNetView = (LoadNetView) findViewById(R.id.loadview);
        loadNetView.setReloadButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadNetView.setlayoutVisily(Constants.LOAD);
                queryData(Constants.REFRESH);
            }
        });
        TitleBarView titlebar = (TitleBarView) findViewById(R.id.titlebar);
        titlebar.setVisibility(View.VISIBLE);
        findViewById(R.id.postButton).setVisibility(View.VISIBLE);
        titlebar.setBarTitle("我 的 状 态");
        titlebar.setBackButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyPostListActivity.this.finish();
            }
        });
        findViewById(R.id.textBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyPostListActivity.this, PostAddActivity.class);
                intent.putExtra("postType", 1);
                startActivityForResult(intent, UPLOAD_POST);
            }
        });
        findViewById(R.id.videoBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyPostListActivity.this, PostAddActivity.class);
                intent.putExtra("postType", 2);
                startActivityForResult(intent, UPLOAD_POST);


            }
        });
        findViewById(R.id.photoBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyPostListActivity.this, PostAddActivity.class);
                startActivityForResult(intent, UPLOAD_POST);
            }
        });

        queryData(Constants.REFRESH);
    }


    private void queryData(final int actionType) {
        BmobQuery<MyPost> query = new BmobQuery<>();
        BmobQuery<MyPost> query2 = new BmobQuery<>();
        List<BmobQuery<MyPost>> queries = new ArrayList<>();
        if (actionType == Constants.LOADMORE) {
            Date date;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                date = sdf.parse(lastTime);
                query2.addWhereLessThanOrEqualTo("updatedAt", new BmobDate(date));
                queries.add(query2);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        BmobQuery<MyPost> query3 = new BmobQuery<>();

        query3.addWhereEqualTo("user", BmobUser.getCurrentUser(RootUser.class));
        queries.add(query3);
        query.and(queries);
        query.setLimit(10);
        query.order("-updatedAt");
        query.findObjects(new FindListener<MyPost>() {
            @Override
            public void done(List<MyPost> list, BmobException e) {
                if (e == null) {
                    if (actionType == Constants.REFRESH) {
                        myPostList.clear();
                    }
                    lastTime = list.get(list.size() - 1).getUpdatedAt();
                    Message message = new Message();
                    message.obj = list;
                    message.what = actionType;
                    handler.sendMessage(message);
                } else {
                    handler.sendEmptyMessage(Constants.NetWorkError);
                }
            }
        });
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            ArrayList<MyPost> myPosts = (ArrayList<MyPost>) msg.obj;
            if (msg.what != Constants.NetWorkError) {

                if (myPosts != null) {
                    myPostList.addAll(myPosts);
                    myPostsSize = myPosts.size();
                } else {
                    myPostsSize = 0;
                    if (msg.what == Constants.LOADMORE) {
                        recyclerView.setNoMore(true);
                    }
                }

                if (myPostListAdapter == null) {
                    myPostListAdapter = new MyPostListAdapter(MyPostListActivity.this, myPostList);
                    recyclerView.setAdapter(myPostListAdapter);
                    myPostListAdapter.setOnItemClickListener(new MyPostListAdapter.OnMyPostRecyclerViewItemClickListener() {
                        @Override
                        public void onItemClick(MyPost post) {
                            Intent intent = new Intent();
                            intent.putExtra(Constants.POST, post);
                            intent.setClass(MyPostListActivity.this, MyPostDetailActivity.class);
                            startActivity(intent);
                        }
                    });
                } else {
                    myPostListAdapter.notifyDataSetChanged();
                }

                loadNetView.setVisibility(View.GONE);
                if (myPostList.size() == 0) {
                    loadNetView.setVisibility(View.VISIBLE);
                    loadNetView.setlayoutVisily(Constants.BUY_VIP);
                    return;
                }
            } else {
                loadNetView.setVisibility(View.VISIBLE);
                loadNetView.setlayoutVisily(Constants.RELOAD);
            }

        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == UPLOAD_POST && resultCode == RESULT_OK) {
            MyPost myPost = (MyPost) data.getSerializableExtra(EXTRA_MOMENT);
            myPostList.add(0, myPost);
            if (myPostListAdapter == null) {
                myPostListAdapter = new MyPostListAdapter(MyPostListActivity.this, myPostList);
                recyclerView.setAdapter(myPostListAdapter);
                myPostListAdapter.setOnItemClickListener(new MyPostListAdapter.OnMyPostRecyclerViewItemClickListener() {
                    @Override
                    public void onItemClick(MyPost post) {
                        Intent intent = new Intent();
                        intent.putExtra(Constants.POST, post);
                        intent.setClass(MyPostListActivity.this, MyPostDetailActivity.class);
                        startActivityForResult(intent, UPLOAD_POST);
                    }
                });
            } else {
                myPostListAdapter.notifyDataSetChanged();
            }
        }
    }
}
