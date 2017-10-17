package com.act.quzhibo.ui.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Display;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.act.quzhibo.R;
import com.act.quzhibo.adapter.PostImageAdapter;
import com.act.quzhibo.common.Constants;
import com.act.quzhibo.common.MyApplicaition;
import com.act.quzhibo.common.OkHttpClientManager;
import com.act.quzhibo.bean.MyFocusCommonPerson;
import com.act.quzhibo.bean.RootUser;
import com.act.quzhibo.util.GlideImageLoader;
import com.act.quzhibo.bean.InterestPost;
import com.act.quzhibo.bean.InterestPostListInfoPersonParentData;
import com.act.quzhibo.util.CommonUtil;
import com.act.quzhibo.util.ToastUtil;
import com.act.quzhibo.view.CircleImageView;
import com.act.quzhibo.view.FragmentDialog;
import com.act.quzhibo.view.HorizontialListView;
import com.act.quzhibo.view.LoadNetView;
import com.act.quzhibo.view.TitleBarView;
import com.bumptech.glide.Glide;
import com.youth.banner.Banner;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

public class InfoInterestPersonActivity extends AppCompatActivity {

    private InterestPost post;
    private HorizontialListView listView;
    private Banner banner;
    private int second;
    private LoadNetView loadNetView;

    MyFocusCommonPerson myFocusCommonPerson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_common_layout);
        initView();
        TitleBarView titlebar = (TitleBarView) findViewById(R.id.titlebar);
        titlebar.setBarTitle("情趣达人档案");
        titlebar.setBackButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InfoInterestPersonActivity.this.finish();
            }
        });

    }

    private void initView() {
        loadNetView = (LoadNetView) findViewById(R.id.loadview);
        loadNetView.setReloadButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadNetView.setlayoutVisily(Constants.LOAD);
                initView();
            }
        });
        Display display = this.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        banner = (Banner) findViewById(R.id.banner);
        banner.setLayoutParams(new FrameLayout.LayoutParams(size.x - 10, size.x - 10));
        if (getIntent() != null) {
            post = (InterestPost) getIntent().getSerializableExtra(Constants.POST);
            ArrayList<String> urls = new ArrayList<>();
            if (post.images != null && post.images.size() > 0) {
                urls.addAll(post.images);
            } else {
                urls.add(post.user.photoUrl);
            }
            if (post.user.sex.equals("2")) {
                ((ImageView) findViewById(R.id.level_img)).setImageDrawable(getResources().getDrawable(MyApplicaition.femaleKeySrc.get(post.user.vipLevel)));
                banner.setImages(urls).setImageLoader(new GlideImageLoader(R.drawable.women)).start();
                Glide.with(this).load(post.user.photoUrl).asBitmap().placeholder(R.drawable.women).error(R.drawable.error_img).into((CircleImageView) findViewById(R.id.userImage));
            } else {
                ((ImageView) findViewById(R.id.level_img)).setImageDrawable(getResources().getDrawable(MyApplicaition.maleKeySrc.get(post.user.vipLevel)));
                banner.setImages(urls).setImageLoader(new GlideImageLoader(R.drawable.man)).start();
                Glide.with(this).load(post.user.photoUrl).asBitmap().placeholder(R.drawable.man).error(R.drawable.error_img).into((CircleImageView) findViewById(R.id.userImage));

            }

            int minute;
            if (CommonUtil.loadData(this, "time") > 0) {
                if (Integer.parseInt(post.user.userId) != CommonUtil.loadData(this, "userId")) {
                    int max = 800;
                    int min = 30;
                    Random random = new Random();
                    minute = random.nextInt(max) +  random.nextInt(min) ;
                    CommonUtil.saveData(this, minute, "time");
                    CommonUtil.saveData(this, Integer.parseInt(post.user.userId), "userId");
                } else {
                    minute = CommonUtil.loadData(this, "time");
                }
            } else {
                int max = 800;
                int min = 30;
                Random random = new Random();
                minute = random.nextInt(max) +  random.nextInt(min) ;
                CommonUtil.saveData(this, minute, "time");
                CommonUtil.saveData(this, Integer.parseInt(post.user.userId), "userId");
            }

            if (minute != 0) {
                if (minute % 60 == 0) {
                    ((TextView) findViewById(R.id.online_time)).setText(minute / 60 + "小时前在线");
                } else {
                    ((TextView) findViewById(R.id.online_time)).setText((minute - (minute % 60)) / 60 + "小时" + minute % 60 + "分前在线");
                }
            }

        }

        int vip = Integer.parseInt(post.user.vipLevel);
        if (vip < 1) {
            ((TextView) findViewById(R.id.level)).setText("非会员");
        } else if (vip >= 1 && vip < 5) {
            ((TextView) findViewById(R.id.level)).setText("初级趣会员");
        } else if (vip >= 5 && vip < 7) {
            ((TextView) findViewById(R.id.level)).setText("中级趣会员");
        } else if (vip >= 7) {
            ((TextView) findViewById(R.id.level)).setText("超级趣会员");
        }

        if (post.user.vipLevel.equals("0")) {
            ((TextView) findViewById(R.id.isCanDate)).setText("先在软件里聊天试试");
        } else {
            ((TextView) findViewById(R.id.isCanDate)).setText("见面一起做爱做的事");
        }
        ((TextView) findViewById(R.id.disPurpose)).setText(post.user.disPurpose);
        ((TextView) findViewById(R.id.disMariState)).setText(post.user.disMariState);
        String nick = post.user.nick.replaceAll("\r|\n", "");
        ((TextView) findViewById(R.id.nickName)).setText(nick);
        findViewById(R.id.talk_accese).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(InfoInterestPersonActivity.this, GetVipPayActivity.class));
            }
        });


        getTextAndImageData();

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (BmobUser.getCurrentUser(RootUser.class) != null) {
            BmobQuery<MyFocusCommonPerson> query = new BmobQuery<>();
            query.setLimit(1);
            query.addWhereEqualTo("userId", post.user.userId);
            query.addWhereEqualTo("rootUser", BmobUser.getCurrentUser(RootUser.class));
            query.addWhereEqualTo("userType", Constants.INTEREST);
            query.findObjects(new FindListener<MyFocusCommonPerson>() {
                @Override
                public void done(List<MyFocusCommonPerson> myFocusCommonPersons, BmobException e) {
                    if (e == null) {
                        if (myFocusCommonPersons.size() >= 1) {
                            myFocusCommonPerson = myFocusCommonPersons.get(0);
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
                        if (myFocusCommonPerson == null) {
                            MyFocusCommonPerson myFocusCommonPerson = new MyFocusCommonPerson();
                            myFocusCommonPerson.rootUser = BmobUser.getCurrentUser(RootUser.class);
                            myFocusCommonPerson.username = post.user.nick;
                            myFocusCommonPerson.userId = post.user.userId;
                            myFocusCommonPerson.photoUrl = post.user.photoUrl;
                            myFocusCommonPerson.sex = post.user.sex;
                            myFocusCommonPerson.userType = Constants.INTEREST;
                            myFocusCommonPerson.save(new SaveListener<String>() {
                                @Override
                                public void done(String objectId, BmobException e) {
                                    if (e == null) {
                                        ((TextView) findViewById(R.id.focus)).setText("已关注");
                                        if (BmobUser.getCurrentUser(RootUser.class) != null) {
                                            BmobQuery<MyFocusCommonPerson> query = new BmobQuery<>();
                                            query.setLimit(1);
                                            query.addWhereEqualTo("userId", post.user.userId);
                                            query.addWhereEqualTo("rootUser", BmobUser.getCurrentUser(RootUser.class));
                                            query.findObjects(new FindListener<MyFocusCommonPerson>() {
                                                @Override
                                                public void done(List<MyFocusCommonPerson> myFocusCommonPersons, BmobException e) {
                                                    if (e == null) {
                                                        if (myFocusCommonPersons.size() >= 1) {
                                                            InfoInterestPersonActivity.this.myFocusCommonPerson = myFocusCommonPersons.get(0);
                                                            ((TextView) findViewById(R.id.focus)).setText("已关注");
                                                        }
                                                    }
                                                }
                                            });
                                        }
                                        ToastUtil.showToast(InfoInterestPersonActivity.this, "关注成功");
                                    } else {
                                        ToastUtil.showToast(InfoInterestPersonActivity.this, "关注失败");
                                    }
                                }
                            });
                        } else {
                            myFocusCommonPerson.update(myFocusCommonPerson.getObjectId(), new UpdateListener() {
                                @Override
                                public void done(BmobException e) {
                                    if (e == null) {
                                        ((TextView) findViewById(R.id.focus)).setText("已关注");
                                        if (BmobUser.getCurrentUser(RootUser.class) != null) {
                                            BmobQuery<MyFocusCommonPerson> query = new BmobQuery<>();
                                            query.setLimit(1);
                                            query.addWhereEqualTo("userId", post.user.userId);
                                            query.addWhereEqualTo("rootUser", BmobUser.getCurrentUser(RootUser.class));
                                            query.findObjects(new FindListener<MyFocusCommonPerson>() {
                                                @Override
                                                public void done(List<MyFocusCommonPerson> myFocusCommonPersons, BmobException e) {
                                                    if (e == null) {
                                                        if (myFocusCommonPersons.size() >= 1) {
                                                            InfoInterestPersonActivity.this.myFocusCommonPerson = myFocusCommonPersons.get(0);
                                                            ((TextView) findViewById(R.id.focus)).setText("已关注");
                                                        }
                                                    }
                                                }
                                            });
                                        }
                                        ToastUtil.showToast(InfoInterestPersonActivity.this, "关注成功");
                                    } else {
                                        ToastUtil.showToast(InfoInterestPersonActivity.this, "关注失败");
                                    }
                                }
                            });
                        }
                    } else {
                        FragmentDialog.newInstance(false, "是否取消关注", "真的要取消关注人家吗", "继续关注", "取消关注","","",false, new FragmentDialog.OnClickBottomListener() {
                            @Override
                            public void onPositiveClick(final Dialog dialog, boolean deleteFileSource) {

                                dialog.dismiss();
                            }

                            @Override
                            public void onNegtiveClick(Dialog dialog) {
                                if (myFocusCommonPerson != null) {
                                    myFocusCommonPerson.delete(myFocusCommonPerson.getObjectId(), new UpdateListener() {
                                        @Override
                                        public void done(BmobException e) {
                                            if (e == null) {
                                                ((TextView) findViewById(R.id.focus)).setText("关注ta");
                                                myFocusCommonPerson = null;
                                                ToastUtil.showToast(InfoInterestPersonActivity.this, "取消关注成功");
                                            }

                                        }
                                    });
                                }
                                dialog.dismiss();
                            }
                        }).show(getSupportFragmentManager(), "");
                    }
                } else {
                    startActivity(new Intent(InfoInterestPersonActivity.this, LoginActivity.class));
                }
            }
        });
    }


    private void getTextAndImageData() {
        String url = CommonUtil.getToggle(this, Constants.TEXT_IMG_POST).getToggleObject().replace("USERID", post.user.userId).replace("CTIME", "0");
        OkHttpClientManager.parseRequest(this, url, handler, Constants.REFRESH);
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            final InterestPostListInfoPersonParentData data =
                    CommonUtil.parseJsonWithGson((String) msg.obj, InterestPostListInfoPersonParentData.class);
            if (msg.what != Constants.NetWorkError) {
                if (data != null && data.result != null) {
                    if (!TextUtils.isEmpty(data.result.totalNums)) {
                        ((TextView) findViewById(R.id.textpost)).setText("图文动态(" + data.result.totalNums + ")");
                    }
                    if (data.result.posts != null && data.result.posts.size() > 0) {
                        listView = (HorizontialListView) findViewById(R.id.txt_img_listview);
                        ArrayList<String> imgs = new ArrayList<>();
                        for (InterestPost post : data.result.posts) {
                            if (post.images != null && post.images.size() > 0) {
                                imgs.addAll(post.images);
                            }
                        }

                        if (data.result.posts.size() > 0 && imgs.size() > 0) {
                            listView.setAdapter(new PostImageAdapter(InfoInterestPersonActivity.this, imgs, Constants.ITEM_USER_INFO_IMG,true,false));
                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                    Intent intent = new Intent();
                                    intent.putExtra(Constants.COMMON_USER_ID, post.user.userId);
                                    intent.setClass(InfoInterestPersonActivity.this, IntersetPersonPostListActivity.class);
                                    startActivity(intent);
                                }
                            });
                        }
                    }
                    loadNetView.setVisibility(View.GONE);
                } else {
                    loadNetView.setVisibility(View.VISIBLE);
                    loadNetView.setlayoutVisily(Constants.RELOAD);
                }

            } else {
                loadNetView.setVisibility(View.VISIBLE);
                loadNetView.setlayoutVisily(Constants.RELOAD);
            }
        }
    };

}
