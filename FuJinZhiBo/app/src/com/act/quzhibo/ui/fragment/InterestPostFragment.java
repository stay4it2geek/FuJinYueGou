package com.act.quzhibo.ui.fragment;

import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Display;
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
import com.act.quzhibo.ui.activity.SquareActivity;
import com.act.quzhibo.util.CommonUtil;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import java.util.ArrayList;

import okhttp3.Call;

/**
 * Created by weiminglin on 17/6/1.
 * 情趣帖子
 */

public class InterestPostFragment extends BackHandledFragment {
//    private View view;
//    private String pid;
//
//    @Nullable
//    @Override
//    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        super.onCreateView(inflater, container, savedInstanceState);
//        view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_interest_post, null, false);
//        pid = ((SquareActivity) getActivity()).getPid();
//        recyclerView = (XRecyclerView) view.findViewById(R.id.interest_post_list);
//        recyclerView.setPullRefreshEnabled(true);
//        recyclerView.setLoadingMoreEnabled(true);
//        recyclerView.setLoadingMoreProgressStyle(R.style.Small);
//        recyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
//            @Override
//            public void onRefresh() {
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        recyclerView.setNoMore(false);
//                        recyclerView.setLoadingMoreEnabled(true);
//                        getData(pid, Constants.REFRESH);
//                        recyclerView.refreshComplete();
//                    }
//                }, 1000);
//            }
//
//            @Override
//            public void onLoadMore() {
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        if (mCurrentCounter < roomTotal && mLastRequstRoomListSize == 0) {
//                            new Handler().postDelayed(new Runnable() {
//                                @Override
//                                public void run() {
//                                    getData(cataId, Constants.LOADMORE);
//                                    recyclerView.loadMoreComplete();
//                                }
//                            }, 1000);
//                        } else {
//                            recyclerView.setNoMore(true);
//                        }
//
//                    }
//                }, 1000);
//            }
//        });
//        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 1);
//        gridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
//        recyclerView.setLayoutManager(gridLayoutManager);
//        getData(cataId, Constants.REFRESH);
//        return view;
//    }
//
//    private XRecyclerView recyclerView;
//    private ArrayList<Room> rooms = new ArrayList<>();
//
//    private int roomTotal = 0;
//    private int mCurrentCounter = 0;
//    private int mLastRequstRoomListSize;
//
//    private RoomListAdapter adapter;
//    private int page;
//    private String offset;
//    private String cataTitle;
//
//
//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        if (context instanceof ShowerListActivity) {
//            onCallShowViewListner = (ShowerListFragment.OnCallShowViewListner) context;
//        }
//    }
//
//    Handler handler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            final RoomList roomList = CommonUtil.parseJsonWithGson((String) msg.obj, RoomList.class);
//            if (msg.what != Constants.NetWorkError) {
//                if (msg.what == Constants.REFRESH) {
//                    page = 20;
//                    rooms.clear();
//                    mCurrentCounter = 0;
//                    roomTotal = Integer.parseInt(roomList.roomTotal);
//                    mLastRequstRoomListSize = 0;
//                } else if (msg.what == Constants.LOADMORE) {
//                    page = page * 2;
//                    mCurrentCounter += roomList.roomList.size();
//                    mLastRequstRoomListSize = roomList.roomList.size();
//                }
//                if (roomList != null && roomList.roomList.size() > 0) {
//                    rooms.addAll(roomList.roomList);
//                    if (adapter == null) {
//                        Display display = getActivity().getWindowManager().getDefaultDisplay();
//                        Point size = new Point();
//                        display.getSize(size);
//                        int screenWidth = size.x;
//                        adapter = new RoomListAdapter(getActivity(), rooms, roomList.pathPrefix, screenWidth, cataTitle);
//                        adapter.setOnItemClickListener(new RoomListAdapter.OnRecyclerViewItemClickListener() {
//                            @Override
//                            public void onItemClick(View view, int position) {
//
//                            }
//                        });
//                        recyclerView.setAdapter(adapter);
//                    } else {
//                        adapter.notifyDataSetChanged();
//                    }
//                }
//            } else {
//               //todo error
//            }
//        }
//    };
//
//    public void getData(String cataId, final int what) {
//        String url = CommonUtil.getToggle(getActivity(), Constants.COMMON_TAB_DETAIL).getToggleObject().replace("CATAID", cataId).replace("NUM", String.valueOf(startPage)).replace("OFFSET", offset);
//        OkHttpUtils.get().url(url).build().execute(new StringCallback() {
//            @Override
//            public void onError(Call call, Exception e, int id) {
//                handler.sendEmptyMessage(Constants.NetWorkError);
//            }
//
//            @Override
//            public void onResponse(String response, int id) {
//                Message message = handler.obtainMessage();
//                message.obj = response;
//                message.what = what;
//                handler.sendMessage(message);
//            }
//        });
//    }

    @Override
    public boolean onBackPressed() {
        return false;
    }
}
