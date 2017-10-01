package com.act.quzhibo.ui.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.act.quzhibo.R;
import com.act.quzhibo.adapter.NearSeeHer20Adapter;
import com.act.quzhibo.adapter.PostImageAdapter;
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
import java.util.List;
import java.util.Random;

import cn.bingoogolapple.photopicker.activity.BGAPhotoPreviewActivity;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;


public class InfoNearPersonActivity extends AppCompatActivity {

    private Banner banner;
    private InterestSubPerson user;
    private LoadNetView loadNetView;
    private MyFocusCommonPerson mMyFocusCommonPerson;
    private File downloadDir = new File(Environment.getExternalStorageDirectory(), "PhotoPickerDownload");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_common_layout);
        initView();

        loadNetView.setReloadButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadNetView.setlayoutVisily(Constants.LOAD);
                initView();
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
    }


    private void initView() {
        loadNetView = (LoadNetView) findViewById(R.id.loadview);
        user = (InterestSubPerson) getIntent().getSerializableExtra(Constants.NEAR_USER);

        findViewById(R.id.audio_layout).setVisibility(View.VISIBLE);
        findViewById(R.id.rl_zipai_img_layout).setVisibility(View.VISIBLE);
        findViewById(R.id.rl_self_video_layout).setVisibility(View.VISIBLE);
        findViewById(R.id.last_see_20_rl).setVisibility(View.VISIBLE);
        Glide.with(this).load(user.absCoverPic).asBitmap().placeholder(R.drawable.women).error(R.drawable.error_img).into((CircleImageView) findViewById(R.id.userImage));
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

        int vip = Integer.parseInt(user.vipType);
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
            if (Integer.parseInt(user.userId) != CommonUtil.loadData(this, "near_userId")) {
                int max = 800;
                int min = 30;
                Random random = new Random();
                minute = random.nextInt(max) + random.nextInt(min);
                CommonUtil.saveData(this, minute, "near_time");
                CommonUtil.saveData(this, Integer.parseInt(user.userId), "near_userId");
            } else {
                minute = CommonUtil.loadData(this, "near_time");
            }
        } else {
            int max = 800;
            int min = 30;
            Random random = new Random();
            minute = random.nextInt(max) + random.nextInt(min);
            CommonUtil.saveData(this, minute, "near_time");
            CommonUtil.saveData(this, Integer.parseInt(user.userId), "near_userId");
        }

        if (minute != 0) {
            if (minute % 60 == 0) {
                ((TextView) findViewById(R.id.online_time)).setText(minute / 60 + "小时前在线");
            } else {
                ((TextView) findViewById(R.id.online_time)).setText((minute - (minute % 60)) / 60 + "小时" + minute % 60 + "分前在线");
            }
        }
        if (user.vipType.equals("0")) {
            ((TextView) findViewById(R.id.isCanDate)).setText("先在软件里聊天试试");
        } else {
            ((TextView) findViewById(R.id.isCanDate)).setText("见面一起做爱做的事");
        }
        ((TextView) findViewById(R.id.disPurpose)).setText(user.disPurpose == null ? "" : user.disPurpose);
        ((TextView) findViewById(R.id.disMariState)).setText(user.disMariState == null ? "" : user.disMariState);
        ((TextView) findViewById(R.id.nickName)).setText(user.username == null ? "" : user.username);
        if (getIntent() != null) {
            user = (InterestSubPerson) getIntent().getSerializableExtra(Constants.NEAR_USER);
            findViewById(R.id.brocast).setVisibility(View.VISIBLE);
            findViewById(R.id.brocast).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    String uri = "http://file.nidong.com/" + user.soundUrl;
                    intent.setClass(InfoNearPersonActivity.this, AudioPlayActivity.class);
                    intent.putExtra(Constants.AUDIO, uri);
                    startActivity(intent);
                }
            });

            if (user.pics.contains(";")) {
                ArrayList<String> bannerUrls = new ArrayList<>();
                for (String url : Arrays.asList(user.pics.split(";"))) {
                    url = "http://file.nidong.com//" + url;
                    bannerUrls.add(url);
                }
                if (user.sex.equals("2")) {
                    banner.setImages(bannerUrls).setImageLoader(new GlideImageLoader(R.drawable.women)).start();
                } else {
                    banner.setImages(bannerUrls).setImageLoader(new GlideImageLoader(R.drawable.man)).start();
                }
            } else {
                if (user.sex.equals("2")) {
                    banner.setImages(Arrays.asList(new String[]{user.pics})).setImageLoader(new GlideImageLoader(R.drawable.women)).start();
                } else {
                    banner.setImages(Arrays.asList(new String[]{"http://file.nidong.com//" + user.pics})).setImageLoader(new GlideImageLoader(R.drawable.man)).start();
                }

            }

            if (user.sex.equals("2")) {
                ((ImageView) findViewById(R.id.level_img)).setImageDrawable(getResources().getDrawable(MyApplicaition.femaleKeySrc.get(user.vipType)));
            } else {
                ((ImageView) findViewById(R.id.level_img)).setImageDrawable(getResources().getDrawable(MyApplicaition.maleKeySrc.get(user.vipType)));
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
            query.addWhereEqualTo("userId", user.userId);
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
                            myFcPerson.username = user.username;
                            myFcPerson.userId = user.userId;
                            myFcPerson.photoUrl = user.absCoverPic;
                            myFcPerson.sex = user.sex;
                            myFcPerson.userType = Constants.NEAR;
                            myFcPerson.save(new SaveListener<String>() {
                                @Override
                                public void done(String objectId, BmobException e) {
                                    if (e == null) {
                                        ((TextView) findViewById(R.id.focus)).setText("已关注");
                                        if (BmobUser.getCurrentUser(RootUser.class) != null) {
                                            BmobQuery<MyFocusCommonPerson> query = new BmobQuery<>();
                                            query.setLimit(1);
                                            query.addWhereEqualTo("userId", user.userId);
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
                                            query.addWhereEqualTo("userId", user.userId);
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

        final ArrayList<NearPhotoEntity> nearPhotoEntities = CommonUtil.jsonToArrayList(user.photoLibraries, NearPhotoEntity.class);
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
        final ArrayList<NearVideoEntity> nearVideoEntities = CommonUtil.jsonToArrayList(user.videoLibraries, NearVideoEntity.class);
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
        if (user.pics != null && user.sharePics.length() > 0) {
            for (String imgUlr : user.sharePics.split(";")) {
                imgs.add(imgUlr);
            }
        }

        listView.setAdapter(new PostImageAdapter(InfoNearPersonActivity.this, imgs, Constants.ITEM_USER_INFO_IMG, true, false));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent();
                intent.putExtra(Constants.COMMON_USER_ID, user.userId);
//                intent.setClass(InfoNearPersonActivity.this,NearPersonPostListActivity.class);
//                startActivity(intent);
            }
        });
    }

    private void getSeeHer20person() {

        final ArrayList<NearSeeHerEntity> nearSeeHerEntities = CommonUtil.jsonToArrayList(user.viewUsers, NearSeeHerEntity.class);
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
}


