package com.act.quzhibo.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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
import com.act.quzhibo.view.RecyclerViewListener;
import com.wuxiaolong.pullloadmorerecyclerview.PullLoadMoreRecyclerView;

import java.util.ArrayList;

import okhttp3.Call;

import static android.R.transition.move;

public class ShowerListFragment extends Fragment implements PullLoadMoreRecyclerView.PullLoadMoreListener {
    private String cataId;
    private OnCallShowViewListner onCallShowViewListner;
    private PullLoadMoreRecyclerView pullloadmorerecyclerview;
    private ArrayList<Room> rooms = new ArrayList<>();

    private RoomListAdapter adapter;
    private int page;
    private String offset;
    private String cataTitle;

    private void getData(String cataId, String startPage, final int what) {
        String url = CommonUtil.getToggle(getActivity(), Constants.COMMON_TAB_DETAIL).getToggleObject().replace("CATAID", cataId).replace("NUM", String.valueOf(startPage)).replace("OFFSET", offset);
        Log.e("url", url);
        OkHttpUtils.get().url(url).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                handler.sendEmptyMessage(2);
                Log.e("onError", "onError");
            }

            @Override
            public void onResponse(String response, int id) {
                Message message = handler.obtainMessage();
                message.obj = response;
                message.what = what;
                handler.sendMessage(message);
                Log.e("response", response + "----");
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
                getData(cataId, "0", 0);
                pullloadmorerecyclerview.setPullLoadMoreCompleted();
            }
        }, 1500);
    }

    @Override
    public void onLoadMore() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getData(cataId, String.valueOf(page), 1);
                pullloadmorerecyclerview.setPullLoadMoreCompleted();
            }
        }, 1500);
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if (msg.what != 2) {
                if (msg.what == 0) {
                    page = 20;
                    rooms.clear();
                } else if (msg.what == 1) {
                    page = page * 2;
                }
                final RoomList roomList = CommonUtil.parseJsonWithGson((String) msg.obj, RoomList.class);
                if (roomList != null && roomList.roomList.size() > 0) {
                    rooms.addAll(roomList.roomList);
                    if (adapter == null) {
                      int  screenWidth = getActivity().getWindowManager().getDefaultDisplay().getHeight();
                        adapter = new RoomListAdapter(getActivity(), rooms, roomList.pathPrefix,screenWidth,cataTitle);
                        adapter.setOnItemClickListener(new RoomListAdapter.OnRecyclerViewItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                if(cataTitle.equals("手机达人")){
                                    getExtrance(position, "2", roomList.pathPrefix);
                                }else {
                                    getExtrance(position, "1", roomList.pathPrefix);
                                }
                            }
                        });
                        pullloadmorerecyclerview.setAdapter(adapter);
                    } else {
                        adapter.notifyDataSetChanged();
                    }
                } else {
                    pullloadmorerecyclerview.setHasMore(false);
                }
            } else {
                getData(cataId,"1",0);
            }
        }
    };

    private void getExtrance(int position, String other, String pathPrefix) {
        if (rooms.get(position).liveType.equals(other)) {
            onCallShowViewListner.onShowVideo(rooms.get(position), pathPrefix);
        } else {
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putSerializable("room", rooms.get(position));
            intent.putExtra(Constants.ROOM_BUNDLE, bundle);
            startActivity(new Intent(getActivity(), ShowerInfoActivity.class));
        }
    }

    public interface OnCallShowViewListner {
        void onShowVideo(Room room, String pathPrefix);
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
        pullloadmorerecyclerview.setPushRefreshEnable(true);
        pullloadmorerecyclerview.setPullRefreshEnable(true);
        pullloadmorerecyclerview.setLinearLayout();
        cataId = getArguments().getString(Constants.CATAID);
        cataTitle = getArguments().getString(Constants.CATATITLE);
        if (cataTitle.equals("手机达人")) {
            offset = "20";
            pullloadmorerecyclerview.setGridLayout(1);//参数为列数
        } else {
            offset = "40";
            pullloadmorerecyclerview.setGridLayout(2);//参数为列数
        }
        getData(cataId, "0", 0);
        return v;
    }

}