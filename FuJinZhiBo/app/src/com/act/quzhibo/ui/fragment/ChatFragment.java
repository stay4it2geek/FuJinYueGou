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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.act.quzhibo.R;
import com.act.quzhibo.adapter.MemberAdapter;
import com.act.quzhibo.common.Constants;
import com.act.quzhibo.download.event.FocusChangeEvent;
import com.act.quzhibo.entity.Member;
import com.act.quzhibo.entity.MyFocusShower;
import com.act.quzhibo.entity.InterestSubPerson;
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
import com.act.quzhibo.view.UPMarqueeView;
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
import cn.bmob.v3.listener.UpdateListener;
import tyrantgit.widget.HeartLayout;

public class ChatFragment extends Fragment implements View.OnClickListener {
    private MemberAdapter mAdapter;
    private HeartLayout heartLayout;
    private Random mRandom;
    private Room room;
    private View view;
    private int onlineCount = 0;
    private String photoUrl;
    private ArrayList<InterestSubPerson> interestSubPersonArrayList = new ArrayList<>();
    public String lastTime;
    private UPMarqueeView upview1;
    private List<String> data = new ArrayList<>();
    private List<View> views = new ArrayList<>();
    private LinearLayout moreView;
    private MyFocusShower mMyFocusShower;

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
        Glide.with(getActivity()).load(photoUrl).error(R.drawable.error_img).into(zhuboAvatar);//加载网络图片

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
        ((TextView) view.findViewById(R.id.starValue)).setText(("星光值：" + (Long.parseLong(room.roomId) - 92015634l)).toString().replaceAll("-", ""));
        ((TextView) view.findViewById(R.id.liveId)).setText("房间号:" + room.roomId);
        ((TextView) view.findViewById(R.id.userNickName)).setText(room.nickname);
        view.findViewById(R.id.close).setOnClickListener(this);
        queryData();
        heartLayout = (HeartLayout) view.findViewById(R.id.heart_layout);
        view.findViewById(R.id.send_message).setOnClickListener(this);
        view.findViewById(R.id.gift).setOnClickListener(this);
        view.findViewById(R.id.message).setOnClickListener(this);
        view.findViewById(R.id.addherat).setOnClickListener(this);

        EventBus.getDefault().register(this);
        handler.postDelayed(task, 2000);//延迟调用
        upview1 = (UPMarqueeView) view.findViewById(R.id.marqueeView);
        initdata();
        setView();
        upview1.setViews(views);
    }

    Handler handler = new Handler();

    private Runnable task = new Runnable() {
        public void run() {
            handler.postDelayed(this, 5 * 1000);
            int countRandom = mRandom.nextInt(10);
            onlineCount = Integer.parseInt(room.onlineCount) + countRandom;
            ((TextView) view.findViewById(R.id.onlineCount)).setText(onlineCount + "人");
        }
    };

    private void queryData() {
        final BmobQuery<InterestSubPerson> query = new BmobQuery<>();
        query.setLimit(20);
        query.order("-updatedAt");
        query.findObjects(new FindListener<InterestSubPerson>() {

            @Override
            public void done(List<InterestSubPerson> list, BmobException e) {
                if (e == null) {
                    if (list.size() > 0) {
                        interestSubPersonArrayList.clear();
                        lastTime = list.get(list.size() - 1).getUpdatedAt();
                        interestSubPersonArrayList.addAll(list);
                        Message message = new Message();
                        message.obj = interestSubPersonArrayList;
                        memberHandler.sendMessage(message);
                    } else {
                        memberHandler.sendEmptyMessage(Constants.NO_MORE);
                    }
                }
            }
        });
    }

    private HorizontialListView horizontialListView;
    Handler memberHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            ArrayList<InterestSubPerson> interestSubPersonArrayList = (ArrayList<InterestSubPerson>) msg.obj;
            ArrayList<String> views = new ArrayList<>();
            if (msg.what != Constants.NetWorkError) {
                for (InterestSubPerson person : interestSubPersonArrayList) {
                    views.add(person.viewUsers);
                }
            }
            int max = views.size();
            final ArrayList<Member> members = CommonUtil.jsonToArrayList(views.get(new Random().nextInt(max - 1)), Member.class);
            horizontialListView = (HorizontialListView) view.findViewById(R.id.list);
            mAdapter = new MemberAdapter(members, getActivity());
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
        FragmentDialog.newInstance(false, m.nickname, "", "关闭", "", -1, true, new FragmentDialog.OnClickBottomListener() {
            @Override
            public void onPositiveClick(Dialog dialog, boolean needDelete) {
                dialog.dismiss();
            }

            @Override
            public void onNegtiveClick(Dialog dialog) {
                dialog.dismiss();
            }
        }).show(getChildFragmentManager(), "");
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
            heartHandler.postDelayed(this, 200 * mRandom.nextInt(5));
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
        if (BmobUser.getCurrentUser(RootUser.class) != null) {
            BmobQuery<MyFocusShower> query = new BmobQuery<>();
            query.setLimit(1);
            query.addWhereEqualTo("userId", room.userId);
            query.addWhereEqualTo("rootUser", BmobUser.getCurrentUser(RootUser.class));
            query.findObjects(new FindListener<MyFocusShower>() {
                @Override
                public void done(List<MyFocusShower> myFocusShowers, BmobException e) {
                    if (e == null) {
                        if (myFocusShowers.size() >= 1) {
                            mMyFocusShower = myFocusShowers.get(0);
                            ((TextView) view.findViewById(R.id.focus_top)).setText("已关注");
                        }
                    }
                }
            });
        }

        view.findViewById(R.id.focus_top).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                if (BmobUser.getCurrentUser(RootUser.class) != null) {
                    if (!(((TextView) view.findViewById(R.id.focus_top)).getText().toString().trim()).equals("已关注")) {
                        if (mMyFocusShower == null) {
                            MyFocusShower myFocusShower = new MyFocusShower();
                            myFocusShower.rootUser = BmobUser.getCurrentUser(RootUser.class);
                            myFocusShower.portrait_path_1280 = photoUrl;
                            myFocusShower.nickname = room.nickname;
                            myFocusShower.roomId = room.roomId;
                            myFocusShower.userId = room.userId;
                            myFocusShower.gender = room.gender;
                            myFocusShower.liveStream = room.liveStream;
                            myFocusShower.city = room.city;
                            myFocusShower.save(new SaveListener<String>() {
                                @Override
                                public void done(String objectId, BmobException e) {
                                    if (e == null) {
                                        ToastUtil.showToast(getActivity(), "关注成功");
                                        ((TextView) view.findViewById(R.id.focus_top)).setText("已关注");
                                        if (BmobUser.getCurrentUser(RootUser.class) != null) {
                                            BmobQuery<MyFocusShower> query = new BmobQuery<>();
                                            query.addWhereEqualTo("userId", room.userId);
                                            query.addWhereEqualTo("rootUser", BmobUser.getCurrentUser(RootUser.class));
                                            query.findObjects(new FindListener<MyFocusShower>() {
                                                @Override
                                                public void done(List<MyFocusShower> myFocusShowers, BmobException e) {
                                                    if (e == null) {
                                                        if (myFocusShowers.size() >= 1) {
                                                            ChatFragment.this.mMyFocusShower = myFocusShowers.get(0);
                                                            ((TextView) view.findViewById(R.id.focus_top)).setText("已关注");
                                                        }
                                                    }

                                                }
                                            });
                                        }
                                    } else {
                                        ToastUtil.showToast(getActivity(), "关注失败");
                                    }
                                }
                            });
                        } else {
                            mMyFocusShower.update(mMyFocusShower.getObjectId(), new UpdateListener() {
                                @Override
                                public void done(BmobException e) {
                                    if (e == null) {
                                        ToastUtil.showToast(getActivity(), "关注成功");
                                        ((TextView) view.findViewById(R.id.focus_top)).setText("已关注");
                                        if (BmobUser.getCurrentUser(RootUser.class) != null) {
                                            BmobQuery<MyFocusShower> query = new BmobQuery<>();
                                            query.addWhereEqualTo("userId", room.userId);
                                            query.addWhereEqualTo("rootUser", BmobUser.getCurrentUser(RootUser.class));
                                            query.findObjects(new FindListener<MyFocusShower>() {
                                                @Override
                                                public void done(List<MyFocusShower> myFocusShowers, BmobException e) {
                                                    if (e == null) {
                                                        if (myFocusShowers.size() >= 1) {
                                                            ChatFragment.this.mMyFocusShower = myFocusShowers.get(0);
                                                            ((TextView) view.findViewById(R.id.focus_top)).setText("已关注");
                                                        }
                                                    }

                                                }
                                            });
                                        }
                                    } else {
                                        ToastUtil.showToast(getActivity(), "关注失败");
                                    }
                                }
                            });
                        }

                    } else {
                        FragmentDialog.newInstance(false, "是否取消关注", "真的要取消关注人家吗", "继续关注", "取消关注", -1, false, new FragmentDialog.OnClickBottomListener() {
                            @Override
                            public void onPositiveClick(final Dialog dialog, boolean deleteFileSource) {
                                dialog.dismiss();
                            }

                            @Override
                            public void onNegtiveClick(Dialog dialog) {
                                if (mMyFocusShower != null) {
                                    mMyFocusShower.delete(mMyFocusShower.getObjectId(), new UpdateListener() {
                                        @Override
                                        public void done(BmobException e) {
                                            if (e == null) {
                                                mMyFocusShower = null;
                                                ((TextView) view.findViewById(R.id.focus_top)).setText("关注ta");
                                                ToastUtil.showToast(getContext(), "取消关注成功");
                                            }

                                        }
                                    });
                                }
                                dialog.dismiss();
                            }
                        }).show(getActivity().getSupportFragmentManager(), "");
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

        if (event.focus) {
            ((TextView) view.findViewById(R.id.focus_top)).setText("已关注");
        } else {
            ((TextView) view.findViewById(R.id.focus_top)).setText("关注ta");

        }
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
        } else if (R.id.send_message == v.getId() || R.id.message == v.getId() || R.id.gift == v.getId()) {
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


    private void setView() {
        for (int i = 0; i < data.size(); i = i + 2) {
            moreView = (LinearLayout) LayoutInflater.from(getContext()).inflate(R.layout.item_view, null);
            TextView tv1 = (TextView) moreView.findViewById(R.id.message_fail);
            final Handler handler = new Handler();
            moreView.findViewById(R.id.retrylayout).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            moreView.findViewById(R.id.connetFaillayout).setVisibility(View.VISIBLE);
                            moreView.findViewById(R.id.retrylayout).setVisibility(View.GONE);

                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    moreView.findViewById(R.id.connetFaillayout).setVisibility(View.GONE);
                                    moreView.findViewById(R.id.retrylayout).setVisibility(View.VISIBLE);
                                }
                            }, 2000);
                        }
                    });

                }
            });
            tv1.setText(data.get(i).toString());
            views.add(moreView);
        }
    }


    private void initdata() {
        data = new ArrayList<>();
        data.add("消息连接异常，正在重试连接");
        data.add("消息连接异常，正在重试连接");
    }
}
