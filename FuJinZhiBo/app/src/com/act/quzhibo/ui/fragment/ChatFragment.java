package com.act.quzhibo.ui.fragment;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.act.quzhibo.R;
import com.act.quzhibo.adapter.MemberAdapter;
import com.act.quzhibo.entity.Member;
import com.act.quzhibo.entity.Room;
import com.act.quzhibo.ui.activity.VideoPlayerActivity;
import com.act.quzhibo.view.FragmentDialog;
import com.act.quzhibo.view.HorizontialListView;
import com.bumptech.glide.Glide;

import java.util.Random;

import tyrantgit.widget.HeartLayout;

public class ChatFragment extends Fragment implements View.OnClickListener {
    private HorizontialListView horizontialListView;
    private MemberAdapter mAdapter;
    private HeartLayout heartLayout;
    private Random mRandom;
    private View view;
    private Room room;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_chat, null, false);
        room = (Room) getArguments().getSerializable("room");
        initView();
        return view;
    }

    public void setViewVisily(boolean flag) {
        if (view != null) {
            if (flag) {
                horizontialListView.setVisibility(View.VISIBLE);
            } else {
                horizontialListView.setVisibility(View.GONE);
                view.findViewById(R.id.hasNotshow).setVisibility(View.VISIBLE);
                ((TextView) view.findViewById(R.id.onlineCount)).setText("- -");
            }
        }
    }

    private void initView() {
        mRandom = new Random();
        ImageView zhuboAvatar = (ImageView) view.findViewById(R.id.zhuboAvatar);
        Glide.with(getActivity()).load(getArguments().getString("pathPrefix") + room.poster_path_1280).into(zhuboAvatar);//加载网络图片
        int startValue = mRandom.nextInt(10);
        if (startValue == 0) {
            startValue = 2;
        }
        int value = mRandom.nextInt(100);
        String finalValue = startValue+"" + value * 2560;
        ((TextView) view.findViewById(R.id.starValue)).setText("🌟："+finalValue);
        ((TextView) view.findViewById(R.id.liveId)).setText("房间号:" + room.roomId);
        ((TextView) view.findViewById(R.id.onlineCount)).setText(room.onlineCount + "人在线");
        ((TextView) view.findViewById(R.id.userNickName)).setText(room.nickname);
        view.findViewById(R.id.close).setOnClickListener(this);
        horizontialListView = (HorizontialListView) view.findViewById(R.id.list);
//        mAdapter = new MemberAdapter(getActivity(),datas);
//        horizontialListView.setAdapter(mAdapter);
//        horizontialListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                showDialog(mAdapter.datas.get(i));
//            }
//        });
        heartLayout = (HeartLayout) view.findViewById(R.id.heart_layout);
    }

    private void showDialog(Member m) {
        FragmentDialog.newInstance(m.name, m.sig, "确定", "取消", -1, false, new FragmentDialog.OnClickBottomListener() {
            @Override
            public void onPositiveClick() {
            }
            @Override
            public void onNegtiveClick() {

            }
        }).show(getChildFragmentManager(), "dialog");

    }

    Handler heartHandler = new Handler();
    Runnable heartRunnable = new Runnable() {
        @Override
        public void run() {
            heartLayout.post(new Runnable() {
                @Override
                public void run() {
                    heartLayout.addHeart(randomColor());
                }
            });
            heartHandler.postDelayed(this, 600);
        }
    };

    @Override
    public void onPause() {
        super.onPause();
        heartHandler.removeCallbacks(heartRunnable);
    }

    @Override
    public void onResume() {
        super.onResume();
        heartHandler.postDelayed(heartRunnable, 600);
    }

    private int randomColor() {
        return Color.rgb(mRandom.nextInt(255), mRandom.nextInt(255), mRandom.nextInt(255));
    }




    @Override
    public void onDestroy() {
        super.onDestroy();
        heartHandler.removeCallbacks(heartRunnable);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
       if (id == R.id.close) {
            onFinishVideoCallbak.finishVideo();
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof VideoPlayerActivity) {
            onFinishVideoCallbak = (OnFinishVideoCallbak) activity;
        }
    }

    OnFinishVideoCallbak onFinishVideoCallbak;

    public interface OnFinishVideoCallbak {
        void finishVideo();
    }


}
