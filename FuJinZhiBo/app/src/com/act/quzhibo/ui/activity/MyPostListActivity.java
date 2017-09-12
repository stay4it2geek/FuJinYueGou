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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class MyPostListActivity extends AppCompatActivity {


    private XRecyclerView recyclerView;
    private MyPostListAdapter myPostListAdapter;
    private LoadNetView loadNetView;
    private int limit = 10; // 每页的数据是10条
    private String lastTime = "";
    private ArrayList<MyPost> myPosts = new ArrayList<>();
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
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    queryData(Constants.LOADMORE);
                                    recyclerView.loadMoreComplete();
                                }
                            }, 1000);
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
        queryData(Constants.REFRESH);
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
    }


    /**
     * 分页获取数据
     *
     * @param actionType
     */
    private void queryData(final int actionType) {
        BmobQuery<MyPost> query = new BmobQuery<>();
        BmobQuery<MyPost> query2 = new BmobQuery<>();
        List<BmobQuery<MyPost>> queries = new ArrayList<>();
        if (actionType == Constants.LOADMORE) {
            // 只查询小于最后一个item发表时间的数据
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
                    if (list.size() > 0) {
                        if (actionType == Constants.REFRESH) {
                            // 当是下拉刷新操作时，将当前页的编号重置为0，并把bankCards清空，重新添加
                            myPosts.clear();
                        }
                        lastTime = list.get(list.size() - 1).getUpdatedAt();
                        myPosts.addAll(list);
                        Message message = new Message();
                        message.obj = myPosts;
                        message.what = actionType;
                        handler.sendMessage(message);
                    } else {
                        handler.sendEmptyMessage(Constants.NO_MORE);
                    }
                }else {
                    handler.sendEmptyMessage(Constants.NetWorkError);
                }
            }
        });
    }

    public static final class ComparatorValues implements Comparator<MyPost> {

        @Override
        public int compare(MyPost post1, MyPost post2) {
            int m1 = Integer.parseInt(post1.ctime != null ? post1.ctime : "0");
            int m2 = Integer.parseInt(post2.ctime != null ? post2.ctime : "0");
            int result = 0;
            if (m1 > m2) {
                result = 1;
            }
            if (m1 < m2) {
                result = -1;
            }
            return result;
        }

    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            ArrayList<MyPost> myPosts = (ArrayList<MyPost>) msg.obj;
            if (msg.what != Constants.NetWorkError) {
                if (msg.what != Constants.NO_MORE) {
                    if (myPosts != null) {
                        myPostsSize = myPosts.size();
                    }else {
                        myPostsSize=0;
                    }
                    Collections.sort(myPosts, new ComparatorValues());
                    if (myPostsSize > 0) {
                        if (myPostListAdapter == null) {
                            myPostListAdapter = new MyPostListAdapter(MyPostListActivity.this, myPosts);
                            recyclerView.setAdapter(myPostListAdapter);
                            myPostListAdapter.setOnItemClickListener(new MyPostListAdapter.OnMyPostRecyclerViewItemClickListener() {
                                @Override
                                public void onItemClick(MyPost post) {
                                    Intent intent = new Intent();
                                    intent.putExtra(Constants.POST_ID, post);
                                    intent.setClass(MyPostListActivity.this, MyPostDetailActivity.class);
                                    startActivity(intent);
                                }
                            });
                        } else {
                            myPostListAdapter.notifyDataSetChanged();
                        }
                    }

                    loadNetView.setVisibility(View.GONE);
                } else {
                    loadNetView.setVisibility(View.VISIBLE);
                    loadNetView.setlayoutVisily(Constants.RELOAD);
                }
            } else {
                loadNetView.setVisibility(View.VISIBLE);
                loadNetView.setlayoutVisily(Constants.RELOAD);
            }
        }
    };


}
