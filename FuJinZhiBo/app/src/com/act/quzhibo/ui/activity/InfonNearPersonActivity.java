package com.act.quzhibo.ui.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.act.quzhibo.R;
import com.act.quzhibo.common.Constants;
import com.act.quzhibo.common.MyApplicaition;
import com.act.quzhibo.entity.MyFocusCommonPerson;
import com.act.quzhibo.entity.RootUser;
import com.act.quzhibo.util.GlideImageLoader;
import com.act.quzhibo.entity.InterestSubPerson;
import com.act.quzhibo.util.CommonUtil;
import com.act.quzhibo.util.ToastUtil;
import com.act.quzhibo.view.CircleImageView;
import com.act.quzhibo.view.FragmentDialog;
import com.act.quzhibo.view.LoadNetView;
import com.act.quzhibo.view.TitleBarView;
import com.bumptech.glide.Glide;
import com.youth.banner.Banner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;


public class InfonNearPersonActivity extends AppCompatActivity {

    private Banner banner;
    private InterestSubPerson user;
    private int second;
    private LoadNetView loadNetView;
    private MyFocusCommonPerson mMyFocusCommonPerson;

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
                InfonNearPersonActivity.this.finish();
            }
        });
    }


    private void initView() {
        loadNetView = (LoadNetView) findViewById(R.id.loadview);
        user = (InterestSubPerson) getIntent().getSerializableExtra(Constants.NEAR_USER);

        findViewById(R.id.audio_layout).setVisibility(View.VISIBLE);
        findViewById(R.id.rl_zipai_img_layout).setVisibility(View.VISIBLE);
        findViewById(R.id.rl_zipai_video_layout).setVisibility(View.VISIBLE);
        findViewById(R.id.last_see_20_rl).setVisibility(View.VISIBLE);
        Glide.with(this).load(user.absCoverPic).asBitmap().placeholder(R.drawable.women).into((CircleImageView) findViewById(R.id.userImage));
        findViewById(R.id.rl_zipai_img_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        findViewById(R.id.rl_zipai_video_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        findViewById(R.id.last_see_20_rl).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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

        if (Integer.parseInt(user.userId) != CommonUtil.loadData(this, "userId")) {
            int max = 400;
            int min = 50;
            Random random = new Random();
            second = random.nextInt(max) % (max - min + 4) + min;
            CommonUtil.saveData(this, second, "time");
            CommonUtil.saveData(this, Integer.parseInt(user.userId), "userId");
        } else {
            second = CommonUtil.loadData(this, "time");
        }
        if (second != 0) {
            if (second % 60 == 0) {
                ((TextView) findViewById(R.id.online_time)).setText(second / 60 + "时前在线");
            } else {
                ((TextView) findViewById(R.id.online_time)).setText((second - (second % 60)) / 60 + "分" + second % 60 + "秒前在线");
            }
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
                    intent.setClass(InfonNearPersonActivity.this, AudioPlayActivity.class);
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
                ((ImageView) findViewById(R.id.level_img)).setImageDrawable(getResources().getDrawable(MyApplicaition.femaleKeySrc.get(user.vipLevel)));
            } else {
                ((ImageView) findViewById(R.id.level_img)).setImageDrawable(getResources().getDrawable(MyApplicaition.maleKeySrc.get(user.vipLevel)));
            }
            loadNetView.setVisibility(View.GONE);
        }
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
                                                            InfonNearPersonActivity.this.mMyFocusCommonPerson = myFcPersons.get(0);
                                                            ((TextView) findViewById(R.id.focus)).setText("已关注");
                                                        }
                                                    }
                                                }
                                            });
                                        }
                                        ToastUtil.showToast(InfonNearPersonActivity.this, "关注成功");
                                    } else {
                                        ToastUtil.showToast(InfonNearPersonActivity.this, "关注失败");
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
                                                            InfonNearPersonActivity.this.mMyFocusCommonPerson = myFcPersons.get(0);
                                                            ((TextView) findViewById(R.id.focus)).setText("已关注");
                                                        }
                                                    }
                                                }
                                            });
                                        }
                                        ToastUtil.showToast(InfonNearPersonActivity.this, "关注成功");
                                    } else {
                                        ToastUtil.showToast(InfonNearPersonActivity.this, "关注失败");
                                    }
                                }
                            });
                        }

                    } else {
                        FragmentDialog.newInstance(false, "是否取消关注", "真的要取消关注人家吗？", "继续关注", "取消关注", -1, false, new FragmentDialog.OnClickBottomListener() {
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
                                                ToastUtil.showToast(InfonNearPersonActivity.this, "取消关注成功");
                                            }

                                        }
                                    });
                                }
                                dialog.dismiss();
                            }
                        }).show(getSupportFragmentManager(), "");
                    }
                } else {
                    startActivity(new Intent(InfonNearPersonActivity.this, LoginActivity.class));
                }
            }
        });
    }


}


