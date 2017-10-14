package com.act.quzhibo.ui.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Environment;
import android.view.Display;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.act.quzhibo.R;
import com.act.quzhibo.adapter.NearSeeHer20Adapter;
import com.act.quzhibo.adapter.PostImageAdapter;
import com.act.quzhibo.base.BaseActivity;
import com.act.quzhibo.bean.AddFriendMessage;
import com.act.quzhibo.common.Constants;
import com.act.quzhibo.common.MyApplicaition;
import com.act.quzhibo.entity.MyFocusCommonPerson;
import com.act.quzhibo.entity.NearPhotoEntity;
import com.act.quzhibo.entity.NearSeeHerEntity;
import com.act.quzhibo.entity.NearVideoEntity;
import com.act.quzhibo.entity.RootUser;
import com.act.quzhibo.util.GlideImageLoader;
import com.act.quzhibo.entity.InterestSubPerson;
import com.act.quzhibo.util.CommonUtil;
import com.act.quzhibo.util.ToastUtil;
import com.act.quzhibo.view.CircleImageView;
import com.act.quzhibo.view.FragmentDialog;
import com.act.quzhibo.view.HorizontialListView;
import com.act.quzhibo.view.LoadNetView;
import com.act.quzhibo.view.TitleBarView;
import com.bumptech.glide.Glide;
import com.youth.banner.Banner;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import cn.bingoogolapple.photopicker.activity.BGAPhotoPreviewActivity;
import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMMessage;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.newim.core.BmobIMClient;
import cn.bmob.newim.listener.MessageSendListener;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;


public class InfoNearPersonActivity extends BaseActivity {

    private Banner banner;
    private InterestSubPerson currentNearInfoUser;
    private LoadNetView loadNetView;
    private MyFocusCommonPerson mMyFocusCommonPerson;
    private File downloadDir = new File(Environment.getExternalStorageDirectory(), "PhotoPickerDownload");
    private BmobIMUserInfo info;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_common_layout);
        initViews();

        loadNetView.setReloadButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadNetView.setlayoutVisily(Constants.LOAD);
                initViews();
            }
        });

        TitleBarView titlebar = (TitleBarView) findViewById(R.id.titlebar);
        titlebar.setVisibility(View.VISIBLE);
        titlebar.setBarTitle("情趣达人档案");
        titlebar.setBackButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InfoNearPersonActivity.this.finish();
            }
        });
        if (!downloadDir.exists()) {
            downloadDir.mkdirs();
        }
        findViewById(R.id.addFriend).setVisibility(View.VISIBLE);
        findViewById(R.id.addFriend).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendAddFriendMessage();
            }
        });

        findViewById(R.id.comment_private).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chatPrivate();
            }
        });

    }


    private void initViews() {
        loadNetView = (LoadNetView) findViewById(R.id.loadview);
        currentNearInfoUser = (InterestSubPerson) getIntent().getSerializableExtra(Constants.NEAR_USER);
        BmobQuery<RootUser> rootUserBmobQuery=new BmobQuery<>();

        if(currentNearInfoUser.user!=null){
            rootUserBmobQuery.getObject(currentNearInfoUser.user.getObjectId(), new QueryListener<RootUser>() {
                @Override
                public void done(RootUser dbRootUser, BmobException e) {
                    if(dbRootUser!=null){
                        info = new BmobIMUserInfo(dbRootUser.getObjectId(), dbRootUser.getUsername(), dbRootUser.photoFileUrl);
                    }
                }
            });

        }
        findViewById(R.id.audio_layout).setVisibility(View.VISIBLE);
        findViewById(R.id.rl_zipai_img_layout).setVisibility(View.VISIBLE);
        findViewById(R.id.rl_self_video_layout).setVisibility(View.VISIBLE);
        findViewById(R.id.last_see_20_rl).setVisibility(View.VISIBLE);
        Glide.with(this).load(currentNearInfoUser.absCoverPic).asBitmap().placeholder(R.drawable.women).error(R.drawable.error_img).into((CircleImageView) findViewById(R.id.userImage));
        findViewById(R.id.talk_accese).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(InfoNearPersonActivity.this, GetVipPayActivity.class));
            }
        });

        Display display = this.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        banner = (Banner) findViewById(R.id.banner);
        banner.setLayoutParams(new FrameLayout.LayoutParams(size.x - 10, size.x - 10));

        int vip = Integer.parseInt(currentNearInfoUser.vipType);
        if (vip < 1) {
            ((TextView) findViewById(R.id.level)).setText("非会员");
        } else if (vip >= 1 && vip < 5) {
            ((TextView) findViewById(R.id.level)).setText("初级趣会员");
        } else if (vip >= 5 && vip < 7) {
            ((TextView) findViewById(R.id.level)).setText("中级趣会员");
        } else if (vip >= 7) {
            ((TextView) findViewById(R.id.level)).setText("超级趣会员");
        }

        int minute;
        if (CommonUtil.loadData(this, "near_time") > 0) {
            if (Integer.parseInt(currentNearInfoUser.userId) != CommonUtil.loadData(this, "near_userId")) {
                int max = 800;
                int min = 30;
                Random random = new Random();
                minute = random.nextInt(max) + random.nextInt(min);
                CommonUtil.saveData(this, minute, "near_time");
                CommonUtil.saveData(this, Integer.parseInt(currentNearInfoUser.userId), "near_userId");
            } else {
                minute = CommonUtil.loadData(this, "near_time");
            }
        } else {
            int max = 800;
            int min = 30;
            Random random = new Random();
            minute = random.nextInt(max) + random.nextInt(min);
            CommonUtil.saveData(this, minute, "near_time");
            CommonUtil.saveData(this, Integer.parseInt(currentNearInfoUser.userId), "near_userId");
        }

        if (minute != 0) {
            if (minute % 60 == 0) {
                ((TextView) findViewById(R.id.online_time)).setText(minute / 60 + "小时前在线");
            } else {
                ((TextView) findViewById(R.id.online_time)).setText((minute - (minute % 60)) / 60 + "小时" + minute % 60 + "分前在线");
            }
        }
        if (currentNearInfoUser.vipType.equals("0")) {
            ((TextView) findViewById(R.id.isCanDate)).setText("先在软件里聊天试试");
        } else {
            ((TextView) findViewById(R.id.isCanDate)).setText("见面一起做爱做的事");
        }

         ((TextView) findViewById(R.id.soundLen)).setText(currentNearInfoUser.soundLen == null ? "" : currentNearInfoUser.soundLen+"秒");
        ((TextView) findViewById(R.id.disPurpose)).setText(currentNearInfoUser.disPurpose == null ? "" : currentNearInfoUser.disPurpose);
        ((TextView) findViewById(R.id.disMariState)).setText(currentNearInfoUser.disMariState == null ? "" : currentNearInfoUser.disMariState);
        ((TextView) findViewById(R.id.nickName)).setText(currentNearInfoUser.username == null ? "" : currentNearInfoUser.username);
        if (getIntent() != null) {
            currentNearInfoUser = (InterestSubPerson) getIntent().getSerializableExtra(Constants.NEAR_USER);
            findViewById(R.id.brocast).setVisibility(View.VISIBLE);
            findViewById(R.id.brocast).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    String uri = "http://file.nidong.com/" + currentNearInfoUser.soundUrl;
                    intent.setClass(InfoNearPersonActivity.this, AudioPlayActivity.class);
                    intent.putExtra(Constants.AUDIO, uri);
                    startActivity(intent);
                }
            });

            if (currentNearInfoUser.pics.contains(";")) {
                ArrayList<String> bannerUrls = new ArrayList<>();
                for (String url : Arrays.asList(currentNearInfoUser.pics.split(";"))) {
                    url = "http://file.nidong.com//" + url;
                    bannerUrls.add(url);
                }
                if (currentNearInfoUser.sex.equals("2")) {
                    banner.setImages(bannerUrls).setImageLoader(new GlideImageLoader(R.drawable.women)).start();
                } else {
                    banner.setImages(bannerUrls).setImageLoader(new GlideImageLoader(R.drawable.man)).start();
                }
            } else {
                if (currentNearInfoUser.sex.equals("2")) {
                    banner.setImages(Arrays.asList(new String[]{currentNearInfoUser.pics})).setImageLoader(new GlideImageLoader(R.drawable.women)).start();
                } else {
                    banner.setImages(Arrays.asList(new String[]{"http://file.nidong.com//" + currentNearInfoUser.pics})).setImageLoader(new GlideImageLoader(R.drawable.man)).start();
                }

            }

            if (currentNearInfoUser.sex.equals("2")) {
                ((ImageView) findViewById(R.id.level_img)).setImageDrawable(getResources().getDrawable(MyApplicaition.femaleKeySrc.get(currentNearInfoUser.vipType)));
            } else {
                ((ImageView) findViewById(R.id.level_img)).setImageDrawable(getResources().getDrawable(MyApplicaition.maleKeySrc.get(currentNearInfoUser.vipType)));
            }
            loadNetView.setVisibility(View.GONE);
        }
        getImageAndText();
        getPhotoLibs();
        getSeeHer20person();
        getVideoLibs();


    }

    @Override
    protected void onResume() {
        super.onResume();
        if (BmobUser.getCurrentUser(RootUser.class) != null) {
            BmobQuery<MyFocusCommonPerson> query = new BmobQuery<>();
            query.setLimit(1);
            query.addWhereEqualTo("userId", currentNearInfoUser.userId);
            query.addWhereEqualTo("rootUser", BmobUser.getCurrentUser(RootUser.class));
            query.addWhereEqualTo("userType", Constants.NEAR);
            query.findObjects(new FindListener<MyFocusCommonPerson>() {
                @Override
                public void done(List<MyFocusCommonPerson> myFcPersons, BmobException e) {
                    if (e == null) {
                        if (myFcPersons.size() >= 1) {
                            mMyFocusCommonPerson = myFcPersons.get(0);
                            ((TextView) findViewById(R.id.focus)).setText("已关注");
                        }
                    }
                }
            });
        }

        findViewById(R.id.focus).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (BmobUser.getCurrentUser(RootUser.class) != null) {
                    if (!(((TextView) findViewById(R.id.focus)).getText().toString().trim()).equals("已关注")) {
                        if (mMyFocusCommonPerson == null) {
                            MyFocusCommonPerson myFcPerson = new MyFocusCommonPerson();
                            myFcPerson.rootUser = BmobUser.getCurrentUser(RootUser.class);
                            myFcPerson.username = currentNearInfoUser.username;
                            myFcPerson.userId = currentNearInfoUser.userId;
                            myFcPerson.photoUrl = currentNearInfoUser.absCoverPic;
                            myFcPerson.sex = currentNearInfoUser.sex;
                            myFcPerson.userType = Constants.NEAR;
                            myFcPerson.save(new SaveListener<String>() {
                                @Override
                                public void done(String objectId, BmobException e) {
                                    if (e == null) {
                                        ((TextView) findViewById(R.id.focus)).setText("已关注");
                                        if (BmobUser.getCurrentUser(RootUser.class) != null) {
                                            BmobQuery<MyFocusCommonPerson> query = new BmobQuery<>();
                                            query.setLimit(1);
                                            query.addWhereEqualTo("userId", currentNearInfoUser.userId);
                                            query.addWhereEqualTo("rootUser", BmobUser.getCurrentUser(RootUser.class));
                                            query.findObjects(new FindListener<MyFocusCommonPerson>() {
                                                @Override
                                                public void done(List<MyFocusCommonPerson> myFcPersons, BmobException e) {
                                                    if (e == null) {
                                                        if (myFcPersons.size() >= 1) {
                                                            InfoNearPersonActivity.this.mMyFocusCommonPerson = myFcPersons.get(0);
                                                            ((TextView) findViewById(R.id.focus)).setText("已关注");
                                                        }
                                                    }
                                                }
                                            });
                                        }
                                        ToastUtil.showToast(InfoNearPersonActivity.this, "关注成功");
                                    } else {
                                        ToastUtil.showToast(InfoNearPersonActivity.this, "关注失败");
                                    }
                                }
                            });
                        } else {
                            mMyFocusCommonPerson.update(mMyFocusCommonPerson.getObjectId(), new UpdateListener() {
                                @Override
                                public void done(BmobException e) {
                                    if (e == null) {
                                        ((TextView) findViewById(R.id.focus)).setText("已关注");
                                        if (BmobUser.getCurrentUser(RootUser.class) != null) {
                                            BmobQuery<MyFocusCommonPerson> query = new BmobQuery<>();
                                            query.setLimit(1);
                                            query.addWhereEqualTo("userId", currentNearInfoUser.userId);
                                            query.addWhereEqualTo("rootUser", BmobUser.getCurrentUser(RootUser.class));
                                            query.findObjects(new FindListener<MyFocusCommonPerson>() {
                                                @Override
                                                public void done(List<MyFocusCommonPerson> myFcPersons, BmobException e) {
                                                    if (e == null) {
                                                        if (myFcPersons.size() >= 1) {
                                                            InfoNearPersonActivity.this.mMyFocusCommonPerson = myFcPersons.get(0);
                                                            ((TextView) findViewById(R.id.focus)).setText("已关注");
                                                        }
                                                    }
                                                }
                                            });
                                        }
                                        ToastUtil.showToast(InfoNearPersonActivity.this, "关注成功");
                                    } else {
                                        ToastUtil.showToast(InfoNearPersonActivity.this, "关注失败");
                                    }
                                }
                            });
                        }

                    } else {
                        FragmentDialog.newInstance(false, "是否取消关注", "真的要取消关注人家吗？", "继续关注", "取消关注", "", "", false, new FragmentDialog.OnClickBottomListener() {
                            @Override
                            public void onPositiveClick(final Dialog dialog, boolean deleteFileSource) {
                                dialog.dismiss();
                            }

                            @Override
                            public void onNegtiveClick(Dialog dialog) {
                                if (mMyFocusCommonPerson != null) {
                                    mMyFocusCommonPerson.delete(mMyFocusCommonPerson.getObjectId(), new UpdateListener() {
                                        @Override
                                        public void done(BmobException e) {
                                            if (e == null) {
                                                mMyFocusCommonPerson = null;
                                                ((TextView) findViewById(R.id.focus)).setText("关注ta");
                                                ToastUtil.showToast(InfoNearPersonActivity.this, "取消关注成功");
                                            }

                                        }
                                    });
                                }
                                dialog.dismiss();
                            }
                        }).show(getSupportFragmentManager(), "");
                    }
                } else {
                    startActivity(new Intent(InfoNearPersonActivity.this, LoginActivity.class));
                }
            }
        });
    }

    ArrayList<String> photoImgs = new ArrayList<>();

    private void getPhotoLibs() {

        final ArrayList<NearPhotoEntity> nearPhotoEntities = CommonUtil.jsonToArrayList(currentNearInfoUser.photoLibraries, NearPhotoEntity.class);
        HorizontialListView listView = (HorizontialListView) findViewById(R.id.photoLibsList);

        for (NearPhotoEntity entity : nearPhotoEntities) {
            photoImgs.add(entity.url);
        }
        listView.setAdapter(new PostImageAdapter(InfoNearPersonActivity.this, photoImgs, Constants.ITEM_USER_INFO_IMG, true, false));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (nearPhotoEntities.size() == 1) {
                    startActivity(BGAPhotoPreviewActivity.newIntent(InfoNearPersonActivity.this, downloadDir,nearPhotoEntities.get(i).url));
                } else if (nearPhotoEntities.size() > 1) {
                    startActivity(BGAPhotoPreviewActivity.newIntent(InfoNearPersonActivity.this, downloadDir,photoImgs,i));
                }
            }
        });
    }

    private void getVideoLibs() {
        final ArrayList<NearVideoEntity> nearVideoEntities = CommonUtil.jsonToArrayList(currentNearInfoUser.videoLibraries, NearVideoEntity.class);
        HorizontialListView listView = (HorizontialListView) findViewById(R.id.videoLibsList);
        ArrayList<String> imgs = new ArrayList<>();
        for (NearVideoEntity entity : nearVideoEntities) {
            imgs.add(entity.videoPic);
        }
        listView.setAdapter(new PostImageAdapter(InfoNearPersonActivity.this, imgs, Constants.ITEM_USER_INFO_IMG, true, true));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent();
                intent.putExtra(Constants.NEAR_USER_VIDEO, nearVideoEntities.get(i));
                intent.setClass(InfoNearPersonActivity.this, NearMediaVideoListActivity.class);
                startActivity(intent);
            }
        });
    }

    private void getImageAndText() {

        HorizontialListView listView = (HorizontialListView) findViewById(R.id.txt_img_listview);
        ArrayList<String> imgs = new ArrayList<>();
        if (currentNearInfoUser.pics != null && currentNearInfoUser.sharePics.length() > 0) {
            for (String imgUlr : currentNearInfoUser.sharePics.split(";")) {
                imgs.add(imgUlr);
            }
        }

        listView.setAdapter(new PostImageAdapter(InfoNearPersonActivity.this, imgs, Constants.ITEM_USER_INFO_IMG, true, false));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent();
                intent.putExtra(Constants.COMMON_USER_ID, currentNearInfoUser.userId);
//                intent.setClass(InfoNearPersonActivity.this,NearPersonPostListActivity.class);
//                startActivity(intent);
            }
        });
    }

    private void getSeeHer20person() {

        final ArrayList<NearSeeHerEntity> nearSeeHerEntities = CommonUtil.jsonToArrayList(currentNearInfoUser.viewUsers, NearSeeHerEntity.class);
        HorizontialListView listView = (HorizontialListView) findViewById(R.id.who_see_her_imglist);
        NearSeeHer20Adapter mAdapter = new NearSeeHer20Adapter(nearSeeHerEntities, InfoNearPersonActivity.this, true);
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                showDialog(nearSeeHerEntities.get(i));
            }
        });

    }

    private void showDialog(NearSeeHerEntity nearSeeHerEntity) {
        FragmentDialog.newInstance(false, nearSeeHerEntity.nickname, "", "关闭", "", nearSeeHerEntity.headUrl, nearSeeHerEntity.createTime, true, new FragmentDialog.OnClickBottomListener() {
            @Override
            public void onPositiveClick(Dialog dialog, boolean needDelete) {
                dialog.dismiss();
            }

            @Override
            public void onNegtiveClick(Dialog dialog) {
                dialog.dismiss();
            }
        }).show(getSupportFragmentManager(), "");
    }





    /**
     * 发送添加好友的请求
     */
    //TODO 好友管理：9.7、发送添加好友请求
    private void sendAddFriendMessage() {
        //TODO 会话：4.1、创建一个暂态会话入口，发送好友请求
        BmobIMConversation conversationEntrance = BmobIM.getInstance().startPrivateConversation(info, true, null);
        //TODO 消息：5.1、根据会话入口获取消息管理，发送好友请求
        BmobIMConversation messageManager = BmobIMConversation.obtain(BmobIMClient.getInstance(), conversationEntrance);
        AddFriendMessage msg = new AddFriendMessage();
        RootUser currentUser = BmobUser.getCurrentUser(RootUser.class);
        msg.setContent("很高兴认识你，可以加个好友吗?");//给对方的一个留言信息
        //TODO 这里只是举个例子，其实可以不需要传发送者的信息过去
        Map<String, Object> map = new HashMap<>();
        map.put("name", currentUser.getUsername());//发送者姓名
        map.put("avatar", currentUser.photoFileUrl);//发送者的头像
        map.put("uid", currentUser.getObjectId());//发送者的uid
        msg.setExtraMap(map);
        messageManager.sendMessage(msg, new MessageSendListener() {
            @Override
            public void done(BmobIMMessage msg, BmobException e) {
                if (e == null) {//发送成功
                    ToastUtil.showToast(InfoNearPersonActivity.this,"好友请求发送成功，等待验证");
                } else {//发送失败
                    ToastUtil.showToast(InfoNearPersonActivity.this,"发送失败:" + e.getMessage());
                }
            }
        });
    }

    /**
     * 与陌生人私聊
     */
    private void chatPrivate() {

        //TODO 会话：4.1、创建一个常态会话入口，陌生人聊天
        BmobIMConversation conversationEntrance = BmobIM.getInstance().startPrivateConversation(info, null);
        Bundle bundle = new Bundle();
        bundle.putSerializable("chat", conversationEntrance);
        startActivity(ChatActivity.class, bundle, false);
    }
}


