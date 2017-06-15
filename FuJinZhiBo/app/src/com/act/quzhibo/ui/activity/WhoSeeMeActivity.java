package com.act.quzhibo.ui.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;

import com.act.quzhibo.R;
import com.act.quzhibo.adapter.InterestPostListAdapter;
import com.act.quzhibo.common.Constants;
import com.act.quzhibo.entity.InterestPost;
import com.act.quzhibo.entity.InterstPostListResult;
import com.act.quzhibo.okhttp.OkHttpUtils;
import com.act.quzhibo.okhttp.callback.StringCallback;
import com.act.quzhibo.ui.fragment.PostDetailFragment;
import com.act.quzhibo.util.CommonUtil;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import java.util.ArrayList;

import okhttp3.Call;

/**
 * Created by weiminglin on 17/6/15.
 */

public class WhoSeeMeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_who_see_me);
        pid ="56dd5198d4c6bbfe55f28290"; 
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
                        getData(pid, 0, Constants.REFRESH);
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
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    getData(pid, num, Constants.LOADMORE);
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
        getData(pid, 0, Constants.REFRESH);
    }

    private XRecyclerView recyclerView;
    private ArrayList<InterestPost> posts = new ArrayList<>();
    private int interestPostSize;
    private InterestPostListAdapter adapter;
    private String pid;
    private int num;


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            final InterstPostListResult interstPostListResult = CommonUtil.parseJsonWithGson((String) msg.obj, InterstPostListResult.class);
            if (msg.what != Constants.NetWorkError) {
                if (msg.what == Constants.REFRESH) {
                    posts.clear();
                    num = 1;
                } else if (msg.what == Constants.LOADMORE) {
                    num += 1;
                }
                if (interstPostListResult.result != null)
                    interestPostSize = interstPostListResult.result.size();
                if (posts != null && interstPostListResult.result.size() > 0) {
                    posts.addAll(interstPostListResult.result);
                    if (adapter == null) {
                        adapter = new InterestPostListAdapter(WhoSeeMeActivity.this, posts,false);
                        adapter.setOnItemClickListener(new InterestPostListAdapter.OnInterestPostRecyclerViewItemClickListener() {
                            @Override
                            public void onItemClick(InterestPost post) {
                                PostDetailFragment fragment = new PostDetailFragment();
                                Bundle bundle = new Bundle();
                                bundle.putSerializable(Constants.POST_ID, post);
                                fragment.setArguments(bundle);
                                CommonUtil.switchFragment(fragment, R.id.square_interest_plates_layout, WhoSeeMeActivity.this);
                            }
                        });
                        recyclerView.setAdapter(adapter);
                    } else {
                        adapter.notifyDataSetChanged();
                    }
                } else if (interstPostListResult.result.size() == 0) {
                    recyclerView.setNoMore(true);
                }
            } else {
                //todo error
            }
        }
    };


    public void getData(String pid, int num, final int what) {
        String htime = CommonUtil.dateToStamp(CommonUtil.getDataString(num));
        Log.e("htmo3", htime);       Log.e("pid",pid);
        String url = CommonUtil.getToggle(WhoSeeMeActivity.this, Constants.SQUARE_INTERES_POST).getToggleObject().replace("PID", pid).replace("HTIME", htime);
        OkHttpUtils.get().url(url).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                handler.sendEmptyMessage(Constants.NetWorkError);
            }

            @Override
            public void onResponse(String response, int id) {
                Message message = handler.obtainMessage();
                message.obj = response;
                message.what = what;
                handler.sendMessage(message);
            }
        });
    }

}
