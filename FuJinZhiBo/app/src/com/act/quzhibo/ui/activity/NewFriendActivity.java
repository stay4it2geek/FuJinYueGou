package com.act.quzhibo.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.act.quzhibo.R;
import com.act.quzhibo.adapter.NewFriendAdapter;
import com.act.quzhibo.adapter.OnRecyclerViewListener;
import com.act.quzhibo.adapter.base.IMutlipleItem;
import com.act.quzhibo.base.BaseActivity;
import com.act.quzhibo.db.NewFriend;
import com.act.quzhibo.db.NewFriendManager;

import java.util.List;

import butterknife.Bind;
import cn.bmob.newim.BmobIM;


/**
 * 新朋友
 *
 */
public class NewFriendActivity extends BaseActivity {

    @Bind(R.id.ll_root)
    FrameLayout ll_root;
    @Bind(R.id.rc_view)
    RecyclerView rc_view;
    @Bind(R.id.sw_refresh)
    SwipeRefreshLayout sw_refresh;
    NewFriendAdapter adapter;
    LinearLayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_conversation);
        if (BmobIM.getInstance().getCurrentStatus().getMsg().equals("connected")) {
            findViewById(R.id.tips_rl).setVisibility(View.GONE);
            findViewById(R.id.sw_refresh).setVisibility(View.VISIBLE);
        }
        //单一布局
        IMutlipleItem<NewFriend> mutlipleItem = new IMutlipleItem<NewFriend>() {

            @Override
            public int getItemViewType(int position, NewFriend c) {
                return 0;
            }

            @Override
            public int getItemLayoutId(int viewtype) {
                return R.layout.item_new_friend;
            }

            @Override
            public int getItemCount(List<NewFriend> list) {
                return list.size();
            }
        };
        adapter = new NewFriendAdapter(this, mutlipleItem, null);
        rc_view.setAdapter(adapter);
        layoutManager = new LinearLayoutManager(this);
        rc_view.setLayoutManager(layoutManager);
        sw_refresh.setEnabled(true);
        //批量更新未读未认证的消息为已读状态
        NewFriendManager.getInstance(this).updateBatchStatus();
        setListener();
    }

    private void setListener() {
        findViewById(R.id.goToLogin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(NewFriendActivity.this, LoginActivity.class));
            }
        });
        ll_root.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                ll_root.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                sw_refresh.setRefreshing(true);
                query();
            }
        });
        sw_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                query();
            }
        });
        adapter.setOnRecyclerViewListener(new OnRecyclerViewListener() {
            @Override
            public void onItemClick(int position) {

            }

            @Override
            public boolean onItemLongClick(int position) {
                NewFriendManager.getInstance(NewFriendActivity.this).deleteNewFriend(adapter.getItem(position));
                adapter.remove(position);
                return true;
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        sw_refresh.setRefreshing(true);
        query();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    /**
     * 查询本地会话
     */
    public void query() {
        adapter.bindDatas(NewFriendManager.getInstance(this).getAllNewFriend());
        adapter.notifyDataSetChanged();
        sw_refresh.setRefreshing(false);
    }

}
