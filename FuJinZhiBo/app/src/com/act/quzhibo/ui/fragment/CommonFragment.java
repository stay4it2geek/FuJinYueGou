package com.act.quzhibo.ui.fragment;

import android.content.Context;
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

public class CommonFragment extends Fragment implements PullLoadMoreRecyclerView.PullLoadMoreListener {
    String cataId;
    OnCallShowViewListner onCallShowViewListner;
    private PullLoadMoreRecyclerView pullloadmorerecyclerview;

    public static CommonFragment getInstance(String cataId) {
        CommonFragment sf = new CommonFragment();
        sf.cataId = cataId;

        return sf;
    }

    private void getData(String cataId) {
        String url = CommonUtil.getToggle(getActivity(), Constants.COMMON_TAB_DETAIL).getToggleObject().replace("CATAID", cataId).replace("NUM", "0");
        OkHttpUtils.get().url(url).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
            }

            @Override
            public void onResponse(String response, int id) {
                RoomList roomList = CommonUtil.parseJsonWithGson(response, RoomList.class);
                ArrayList<Room> rooms = new ArrayList<Room>();
                rooms.addAll(roomList.roomList);
                Message message = handler.obtainMessage();
                message.obj = rooms;
                handler.sendMessage(message);
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
                pullloadmorerecyclerview.setPullLoadMoreCompleted();
            }
        }, 1500);
    }

    @Override
    public void onLoadMore() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
//                for (int i = 0; i < 10; i++) {
//                    list.add("item" + i);
//                }
//                adapter.notifyItemInserted(list.size());
                pullloadmorerecyclerview.setPullLoadMoreCompleted();
            }
        }, 1500);
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

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
        return v;
    }

}