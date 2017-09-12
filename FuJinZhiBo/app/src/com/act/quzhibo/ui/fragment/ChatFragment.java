package com.act.quzhibo.ui.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.act.quzhibo.R;
import com.act.quzhibo.adapter.MemberAdapter;
import com.act.quzhibo.common.Constants;
import com.act.quzhibo.download.event.DownloadStatusChanged;
import com.act.quzhibo.download.event.FocusChangeEvent;
import com.act.quzhibo.entity.Member;
import com.act.quzhibo.entity.MyFocusShowers;
import com.act.quzhibo.entity.NearPerson;
import com.act.quzhibo.entity.Room;
import com.act.quzhibo.entity.RootUser;
import com.act.quzhibo.ui.activity.LoginActivity;
import com.act.quzhibo.ui.activity.ShowerInfoActivity;
import com.act.quzhibo.ui.activity.VideoPlayerActivity;
import com.act.quzhibo.util.CommonUtil;
import com.act.quzhibo.util.ToastUtil;
import com.act.quzhibo.view.CircleImageView;
import com.act.quzhibo.view.FragmentDialog;
import com.act.quzhibo.view.HorizontialListView;
import com.bumptech.glide.Glide;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import tyrantgit.widget.HeartLayout;

public class ChatFragment extends Fragment implements View.OnClickListener {
    HorizontialListView listview;
    private MemberAdapter mAdapter;
    private HeartLayout heartLayout;
    private Random mRandom;
    private Room room;
    View view;
    int onlineCount = 0;
    private String photoUrl;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_chat, null, false);
        room = (Room) getArguments().getSerializable("room");
        initView();
        return view;
    }

    private void initView() {
        mRandom = new Random();
        CircleImageView zhuboAvatar = (CircleImageView) view.findViewById(R.id.zhuboAvatar);
        if (!room.portrait_path_1280.contains("http://ures.kktv8.com/kktv")) {
            photoUrl = "http://ures.kktv8.com/kktv" + room.portrait_path_1280;
        } else {
            photoUrl = room.portrait_path_1280;
        }
        Glide.with(getActivity()).load(photoUrl).into(zhuboAvatar);//加载网络图片
        int startValue = mRandom.nextInt(10);
        if (startValue == 0) {
            startValue = 2;
        }
        int value = mRandom.nextInt(30);
        final String finalValue = startValue + "" + value * 2560;
        zhuboAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("room", room);
                intent.putExtra("FromChatFragment", true);
                intent.putExtra("photoUrl", photoUrl);
                intent.setClass(getActivity(), ShowerInfoActivity.class);
                startActivity(intent);
            }

        });


        onlineCount = Integer.parseInt(room.onlineCount);
        ((TextView) view.findViewById(R.id.onlineCount)).setText(onlineCount + "人");
        ((TextView) view.findViewById(R.id.starValue)).setText("⭐：" + finalValue);
        ((TextView) view.findViewById(R.id.liveId)).setText("房间号:" + room.roomId);
        ((TextView) view.findViewById(R.id.userNickName)).setText(room.nickname);
        view.findViewById(R.id.close).setOnClickListener(this);
        listview = (HorizontialListView) view.findViewById(R.id.list);
        queryData();
        heartLayout = (HeartLayout) view.findViewById(R.id.heart_layout);
        view.findViewById(R.id.send_message).setOnClickListener(this);
        view.findViewById(R.id.gift).setOnClickListener(this);
        view.findViewById(R.id.message).setOnClickListener(this);
        view.findViewById(R.id.addherat).setOnClickListener(this);
        view.findViewById(R.id.retrylayout).setOnClickListener(this);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                view.findViewById(R.id.connetFaillayout).setVisibility(View.GONE);
                view.findViewById(R.id.retrylayout).setVisibility(View.VISIBLE);
            }
        }, 5000);

        EventBus.getDefault().register(this);

    }


    private void queryData() {
        final BmobQuery<NearPerson> query = new BmobQuery<>();
        query.setLimit(20);
        // 如果是加载更多
        query.order("-updatedAt");
        query.findObjects(new FindListener<NearPerson>() {

            @Override
            public void done(List<NearPerson> list, BmobException e) {
                if (e == null) {
                    if (list.size() > 0) {
                        nearPersonArrayList.clear();
                        lastTime = list.get(list.size() - 1).getUpdatedAt();
                        nearPersonArrayList.addAll(list);
                        Message message = new Message();
                        message.obj = nearPersonArrayList;
                        memberHandler.sendMessage(message);
                    } else {
                        memberHandler.sendEmptyMessage(Constants.NO_MORE);
                    }
                }
            }
        });
    }

    ArrayList<NearPerson> nearPersonArrayList = new ArrayList<>();
    public String lastTime;


    private HorizontialListView horizontialListView;
    Handler memberHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            ArrayList<NearPerson> nearPersonArrayList = (ArrayList<NearPerson>) msg.obj;
            ArrayList<String> views = new ArrayList<>();
            if (msg.what != Constants.NetWorkError) {
                for (NearPerson person : nearPersonArrayList) {
                    views.add(person.viewUsers);
                }
            }
            int max = views.size();
            final ArrayList<Member> members = CommonUtil.jsonToArrayList(views.get(new Random().nextInt(max - 1)), Member.class);
            horizontialListView = (HorizontialListView) view.findViewById(R.id.list);
            mAdapter = new MemberAdapter(getContext().getApplicationContext(), members);
            mAdapter.setDatas(members);
            horizontialListView.setAdapter(mAdapter);
            horizontialListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    showDialog(members.get(i));
                }
            });
        }
    };

    private void showDialog(Member m) {
        FragmentDialog.newInstance(false, m.nickname, "", "确定", "取消", -1, true, new FragmentDialog.OnClickBottomListener() {
            @Override
            public void onPositiveClick(Dialog dialog, boolean needDelete) {
                dialog.dismiss();
            }

            @Override
            public void onNegtiveClick(Dialog dialog) {
                dialog.dismiss();
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
                    Random random = new Random();
                }
            });
            heartHandler.postDelayed(this, 1000);
        }
    };
    Handler countHandler = new Handler();
    Runnable countRunnable = new Runnable() {
        @Override
        public void run() {
            countHandler.post(new Runnable() {
                @Override
                public void run() {
                    Random random = new Random();
                    onlineCount = onlineCount + random.nextInt(4);
                    ((TextView) view.findViewById(R.id.onlineCount)).setText(onlineCount + "人");
                }
            });
            countHandler.postDelayed(this, 10000);
        }
    };

    @Override
    public void onPause() {
        super.onPause();
        countHandler.removeCallbacks(countRunnable);
        heartHandler.removeCallbacks(heartRunnable);

    }

    @Override
    public void onResume() {
        super.onResume();
        countHandler.postDelayed(countRunnable, 10000);
        heartHandler.postDelayed(heartRunnable, 1000);
        BmobQuery<MyFocusShowers> query = new BmobQuery<>();
        query.addWhereEqualTo("userId", room.userId);
        query.addWhereEqualTo("rootUser", BmobUser.getCurrentUser(RootUser.class));
        query.findObjects(new FindListener<MyFocusShowers>() {
            @Override
            public void done(List<MyFocusShowers> myFocusShowers, BmobException e) {
                if (e == null) {
                    if (myFocusShowers.size() >= 1) {
                        ((TextView) view.findViewById(R.id.focus_top)).setText("已关注");
                    }
                } else {
                    ToastUtil.showToast(getContext(), "请求异常");
                }
            }
        });


        view.findViewById(R.id.focus_top).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                if (BmobUser.getCurrentUser(RootUser.class) != null) {
                    if (!(((TextView) view.findViewById(R.id.focus_top)).getText().toString().trim()).equals("已关注")) {
                        MyFocusShowers myFocusShowers = new MyFocusShowers();
                        myFocusShowers.rootUser = BmobUser.getCurrentUser(RootUser.class);
                        myFocusShowers.portrait_path_1280 = photoUrl;
                        myFocusShowers.nickname = room.nickname;
                        myFocusShowers.roomId = room.roomId;
                        myFocusShowers.userId = room.userId;
                        myFocusShowers.gender = room.gender;
                        myFocusShowers.liveStream = room.liveStream;
                        myFocusShowers.city = room.city;
                        myFocusShowers.save(new SaveListener<String>() {
                            @Override
                            public void done(String objectId, BmobException e) {
                                if (e == null) {
                                    ToastUtil.showToast(getActivity(), "关注成功");
                                    ((TextView) view.findViewById(R.id.focus_top)).setText("已关注");
                                } else {
                                    ToastUtil.showToast(getActivity(), "关注失败");
                                }
                            }
                        });
                    }else{
                        ToastUtil.showToast(getActivity(), "您已经关注");

                    }
                } else {
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                }
            }
        });


    }

    private int randomColor() {
        return Color.rgb(mRandom.nextInt(255), mRandom.nextInt(255), mRandom.nextInt(255));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onEventMainThread(FocusChangeEvent event) {
        ((TextView) view.findViewById(R.id.focus_top)).setText("已关注");
    }

    @Override
    public void onClick(View v) {
        if (R.id.close == v.getId()) {
            onFinishVideoCallbak.finishVideo();
        } else if (R.id.addherat == v.getId()) {
            heartLayout.post(new Runnable() {
                @Override
                public void run() {
                    heartLayout.addHeart(randomColor());
                }
            });
        } else if (R.id.retrylayout == v.getId()) {
            view.findViewById(R.id.connetFaillayout).setVisibility(View.VISIBLE);
            view.findViewById(R.id.retrylayout).setVisibility(View.GONE);
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    view.findViewById(R.id.connetFaillayout).setVisibility(View.GONE);
                    view.findViewById(R.id.retrylayout).setVisibility(View.VISIBLE);
                }
            }, 5000);

        } else {
            ToastUtil.showToast(getActivity(), "消息加载异常，暂时无法操作该功能");
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
