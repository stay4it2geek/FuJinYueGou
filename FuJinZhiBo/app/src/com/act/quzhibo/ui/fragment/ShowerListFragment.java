package com.act.quzhibo.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.act.quzhibo.R;
import com.act.quzhibo.ui.activity.ShowerListActivity;
import com.wuxiaolong.pullloadmorerecyclerview.PullLoadMoreRecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ShowerListFragment extends Fragment implements PullLoadMoreRecyclerView.PullLoadMoreListener {
    private String cataId;
    OnCallShowViewListner onCallShowViewListner;
    private PullLoadMoreRecyclerView pullloadmorerecyclerview;
    private MyAdapter adapter;

    public static ShowerListFragment getInstance(String cataId) {
        ShowerListFragment sf = new ShowerListFragment();
        sf.cataId = cataId;
        return sf;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ShowerListActivity) {
            onCallShowViewListner = (OnCallShowViewListner) context;
        }
    }

    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                pullloadmorerecyclerview.setPullLoadMoreCompleted();
            }
        }, 1500);
    }

    @Override
    public void onLoadMore() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 10; i++) {
                    list.add("item" + i);
                }
                adapter.notifyItemInserted(list.size());
                pullloadmorerecyclerview.setPullLoadMoreCompleted();
            }
        }, 1500);
    }


    public interface OnCallShowViewListner {
        void onShowVideo(String url);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.showlist_fragment, null);
         pullloadmorerecyclerview= (PullLoadMoreRecyclerView) v.findViewById(R.id.recycler_view);
        pullloadmorerecyclerview.setOnPullLoadMoreListener(this);
        pullloadmorerecyclerview.setLinearLayout();
        pullloadmorerecyclerview.setGridLayout(2);//参数为列数
        adapter=new MyAdapter();
        pullloadmorerecyclerview.setAdapter(adapter);
        return v;
    }

    private List<String> list = new ArrayList<>();

    public class MyAdapter extends RecyclerView.Adapter {

        public MyAdapter() {
            for (int i = 0; i < 50; i++) {
                list.add("item" + i);
            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View itemView = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
            MyViewHolder holder = (MyViewHolder) viewHolder;
            holder.tv.setText(list.get(position));
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            public TextView tv;

            public MyViewHolder(View itemView) {
                super(itemView);
                tv = (TextView) itemView;
            }
        }
    }

}