package com.act.quzhibo.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
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
import com.act.quzhibo.ui.activity.ShowerInfoActivity;
import com.act.quzhibo.ui.activity.ShowerListActivity;
import com.act.quzhibo.util.CommonUtil;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import java.util.ArrayList;

import okhttp3.Call;


public class ShowerListFragment extends BackHandledFragment {
    private String cataId;
    private OnCallShowViewListner onCallShowViewListner;
    private XRecyclerView recyclerView;
    private ArrayList<Room> rooms = new ArrayList<>();

    private int roomTotal = 0;
    private int mCurrentCounter = 0;
    private int mLastRequstRoomListSize;

    private RoomListAdapter adapter;
    private int page;
    private String offset;
    private String cataTitle;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ShowerListActivity) {
            onCallShowViewListner = (OnCallShowViewListner) context;
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.showlist_fragment, null);
        recyclerView = (XRecyclerView) v.findViewById(R.id.recycler_view);
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
                        getData(cataId, "0", Constants.REFRESH);
                        recyclerView.refreshComplete();
                    }
                }, 1000);
            }

            @Override
            public void onLoadMore() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (mCurrentCounter < roomTotal && mLastRequstRoomListSize == 0) {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    getData(cataId, String.valueOf(page), Constants.LOADMORE);
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
        cataId = getArguments().getString(Constants.CATAID);
        cataTitle = getArguments().getString(Constants.CATATITLE);
        if (cataTitle.equals("手机达人")) {
            offset = "20";
            GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 1);
            gridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(gridLayoutManager);
        } else {
            offset = "40";
            GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
            gridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(gridLayoutManager);
        }
        getData(cataId, "0", Constants.REFRESH);
        return v;
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            final RoomList roomList = CommonUtil.parseJsonWithGson((String) msg.obj, RoomList.class);
            if (msg.what != Constants.NetWorkError) {
                if (msg.what == Constants.REFRESH) {
                    page = 20;
                    rooms.clear();
                    mCurrentCounter = 0;
                    roomTotal = Integer.parseInt(roomList.roomTotal);
                    mLastRequstRoomListSize = 0;
                } else if (msg.what == Constants.LOADMORE) {
                    page = page * 2;
                    mCurrentCounter += roomList.roomList.size();
                    mLastRequstRoomListSize = roomList.roomList.size();
                }
                if (roomList != null && roomList.roomList.size() > 0) {
                    rooms.addAll(roomList.roomList);
                    if (adapter == null) {
                        Display display = getActivity().getWindowManager().getDefaultDisplay();
                        Point size = new Point();
                        display.getSize(size);
                        int screenWidth = size.x;
                        adapter = new RoomListAdapter(getActivity(), rooms, roomList.pathPrefix, screenWidth, cataTitle);
                        adapter.setOnItemClickListener(new RoomListAdapter.OnRecyclerViewItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                if (cataTitle.equals("手机达人")) {
                                    getExtrance(position, "2", roomList.pathPrefix);
                                } else {
                                    getExtrance(position, "1", roomList.pathPrefix);
                                }
                            }
                        });
                        recyclerView.setAdapter(adapter);
                    } else {
                        adapter.notifyDataSetChanged();
                    }
                }
            } else {
                getData(cataId, "1", 0);
            }
        }
    };

    public void getData(String cataId, String startPage, final int what) {
        String url = CommonUtil.getToggle(getActivity(), Constants.COMMON_TAB_DETAIL).getToggleObject().replace("CATAID", cataId).replace("NUM", String.valueOf(startPage)).replace("OFFSET", offset);
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

    private void getExtrance(int position, String other, String pathPrefix) {
        if (rooms.get(position).liveType.equals(other)) {
            onCallShowViewListner.onShowVideo(rooms.get(position), pathPrefix, rooms.get(position).screenType);
        } else {
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putSerializable("room", rooms.get(position));
            intent.putExtra(Constants.ROOM_BUNDLE, bundle);
            startActivity(new Intent(getActivity(), ShowerInfoActivity.class));
        }
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    public interface OnCallShowViewListner {
        void onShowVideo(Room room, String pathPrefix, String screenType);
    }
    
}