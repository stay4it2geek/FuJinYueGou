package com.act.quzhibo.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.act.quzhibo.R;
import com.act.quzhibo.adapter.RoomListAdapter;
import com.act.quzhibo.common.Constants;
import com.act.quzhibo.entity.Room;
import com.act.quzhibo.entity.RoomList;
import com.act.quzhibo.okhttp.OkHttpUtils;
import com.act.quzhibo.okhttp.callback.StringCallback;
import com.act.quzhibo.ui.activity.ShowerListActivity;
import com.act.quzhibo.util.CommonUtil;
import com.wuxiaolong.pullloadmorerecyclerview.PullLoadMoreRecyclerView;

import java.util.ArrayList;

import okhttp3.Call;

public class ShowerListFragment extends Fragment implements PullLoadMoreRecyclerView.PullLoadMoreListener {
    String cataId;
    OnCallShowViewListner onCallShowViewListner;
    private PullLoadMoreRecyclerView pullloadmorerecyclerview;
    private static int num;

    private void getData(String cataId) {
        String url = CommonUtil.getToggle(getActivity(), Constants.COMMON_TAB_DETAIL).getToggleObject().replace("CATAID", cataId).replace("NUM", String.valueOf(num));
        OkHttpUtils.get().url(url).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
            }

            @Override
            public void onResponse(String response, int id) {
                Message message = handler.obtainMessage();
                message.obj = response;
                handler.sendMessage(message);
                num++;
            }
        });
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
                getData(cataId);
                num=0;
                pullloadmorerecyclerview.setPullLoadMoreCompleted();
            }
        }, 1500);
    }

    @Override
    public void onLoadMore() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getData(cataId);
                adapter.notifyItemInserted(rooms.size());
                pullloadmorerecyclerview.setPullLoadMoreCompleted();
            }
        }, 1500);
    }
    public ArrayList<Room> rooms;
    public RoomListAdapter adapter;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            RoomList roomList = CommonUtil.parseJsonWithGson((String) msg.obj, RoomList.class);
            rooms = new ArrayList<>();
            rooms.addAll(roomList.roomList);
            adapter = new RoomListAdapter(getActivity(), rooms, roomList.pathPrefix);
            adapter.setOnItemClickListener(new RoomListAdapter.OnRecyclerViewItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    onCallShowViewListner.onShowVideo(rooms.get(position).liveStream);
                }
            });
            pullloadmorerecyclerview.setAdapter(adapter);
        }
    };

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
        pullloadmorerecyclerview = (PullLoadMoreRecyclerView) v.findViewById(R.id.recycler_view);
        pullloadmorerecyclerview.setOnPullLoadMoreListener(this);
        pullloadmorerecyclerview.setLinearLayout();
        pullloadmorerecyclerview.setGridLayout(2);//参数为列数
        cataId = getArguments().getString("cataId");
        getData(cataId);
        return v;
    }

}