package com.act.quzhibo.ui.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Display;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.act.quzhibo.R;
import com.act.quzhibo.adapter.PostImageAdapter;
import com.act.quzhibo.bean.InterstPostListInfoResult;
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
import com.act.quzhibo.widget.CircleImageView;
import com.act.quzhibo.widget.FragmentDialog;
import com.act.quzhibo.widget.HorizontialListView;
import com.act.quzhibo.widget.LoadNetView;
import com.act.quzhibo.widget.TitleBarView;
import com.bumptech.glide.Glide;
import com.youth.banner.Banner;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Random;

import butterknife.Bind;
import butterknife.OnClick;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

public class InfoInterestPersonActivity extends BaseActivity {

    @Bind(R.id.titlebar)
    TitleBarView titlebar;
    @Bind(R.id.loadview)
    LoadNetView loadNetView;
    @Bind(R.id.age)
    TextView ageText;
    @Bind(R.id.banner)
    Banner banner;
    @Bind(R.id.level)
    TextView level;
    @Bind(R.id.isCanDate)
    TextView isCanDate;
    @Bind(R.id.textpost)
    TextView textpost;
    @Bind(R.id.disMariState)
    TextView disMariState;
    @Bind(R.id.txt_img_listview)
    HorizontialListView listView;
    @Bind(R.id.userImage)
    CircleImageView userImage;
    @Bind(R.id.level_img)
    ImageView level_img;

    @Bind(R.id.online_time)
    TextView online_time;
    @Bind(R.id.nickName)
    TextView nickName;
    @Bind(R.id.focus)
    TextView focus;
    @Bind(R.id.disPurpose)
    TextView disPurpose;
    InterestPost post;
    RootUser user;
    MyFocusCommonPerson person;
    int randomAge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_common_layout);
        user = BmobUser.getCurrentUser(RootUser.class);
        initView();


    }

    void initView() {

        titlebar.setBarTitle("情趣达人档案");
        titlebar.setBackButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InfoInterestPersonActivity.this.finish();
            }
        });

        loadNetView.setReloadButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadNetView.setlayoutVisily(Constants.LOAD);
                initView();
            }
        });

        Point size = new Point();
        getWindowManager().getDefaultDisplay().getSize(size);

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
                setUserAvater(urls, R.drawable.women, MyApplicaition.maleKeySrc);
            } else {

                setUserAvater(urls, R.drawable.man, MyApplicaition.maleKeySrc);
            }

            int minute;
            if (CommonUtil.loadData(this, "time") > 0) {
                if (Integer.parseInt(post.user.userId) != CommonUtil.loadData(this, "userId")) {
                    int max = 800;
                    int min = 30;
                    Random random = new Random();
                    minute = random.nextInt(max) + random.nextInt(min);
                    CommonUtil.saveData(this, minute, "time");
                    CommonUtil.saveData(this, Integer.parseInt(post.user.userId), "userId");
                } else {
                    minute = CommonUtil.loadData(this, "time");
                }
            } else {
                int max = 800;
                int min = 30;
                Random random = new Random();
                minute = random.nextInt(max) + random.nextInt(min);
                CommonUtil.saveData(this, minute, "time");
                CommonUtil.saveData(this, Integer.parseInt(post.user.userId), "userId");
            }

            if (minute != 0) {
                if (minute % 60 == 0) {
                    online_time.setText(minute / 60 + "小时前在线");
                } else {
                    online_time.setText((minute - (minute % 60)) / 60 + "小时" + minute % 60 + "分前在线");
                }
            }
        }

        //todo ormlite dao
        ageText.setText(randomAge + "岁");


        int vip = Integer.parseInt(post.user.vipLevel);
        if (vip < 1) {
            level.setText("非会员");
        } else if (vip >= 1 && vip < 5) {
            level.setText("白银会员");
        } else if (vip >= 5 && vip < 7) {
            level.setText("铂金会员");
        } else if (vip >= 7 && vip < 9) {
            level.setText("黄金会员");
        } else if (vip >= 9) {
            level.setText("钻石会员");
        }

        if (vip == 0) {
            isCanDate.setText("先在软件里聊天试试");
        } else {
            isCanDate.setText("见面一起做爱做的事");
        }

        disPurpose.setText(post.user.disPurpose);
        disMariState.setText(post.user.disMariState);
        String nick = post.user.nick.replaceAll("\r|\n", "");
        nickName.setText(nick);

        getTextAndImageData();

    }

    @OnClick({})
    void buttonClicks(View view) {
        switch (view.getId()) {
            case R.id.talk_accese:
                startActivity(new Intent(this, GetVipPayActivity.class));
                break;
        }
    }

     void setUserAvater(ArrayList<String> urls, int resource, LinkedHashMap<String, Integer> keySrc) {
        level_img.setImageDrawable(getResources().getDrawable(keySrc.get(post.user.vipLevel)));
        banner.setImages(urls).setImageLoader(new GlideImageLoader(resource)).start();
        Glide.with(this).load(post.user.photoUrl).asBitmap().placeholder(resource).error(R.drawable.error_img).into(userImage);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (user != null) {
            BmobQuery<MyFocusCommonPerson> query = new BmobQuery<>();
            query.setLimit(1);
            query.addWhereEqualTo("userId", post.user.userId);
            query.addWhereEqualTo("rootUser", user);
            query.addWhereEqualTo("userType", Constants.INTEREST);
            query.findObjects(new FindListener<MyFocusCommonPerson>() {
                @Override
                public void done(List<MyFocusCommonPerson> myFocusCommonPersons, BmobException e) {
                    if (e == null) {
                        if (myFocusCommonPersons.size() >= 1) {
                            person = myFocusCommonPersons.get(0);
                            focus.setText("取消关注");
                        }
                    }
                }
            });
        }


        findViewById(R.id.focus).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (user != null) {
                    if (!(((TextView) findViewById(R.id.focus)).getText().toString().trim()).equals("取消关注")) {
                        if (person == null) {
                            MyFocusCommonPerson person = new MyFocusCommonPerson();
                            person.rootUser = user;
                            person.username = post.user.nick;
                            person.userId = post.user.userId;
                            person.photoUrl = post.user.photoUrl;
                            person.sex = post.user.sex;
                            person.userType = Constants.INTEREST;
                            person.save(new SaveListener<String>() {
                                @Override
                                public void done(String objectId, BmobException e) {
                                    if (e == null) {
                                        focus.setText("取消关注");
                                        if (user != null) {
                                            BmobQuery<MyFocusCommonPerson> query = new BmobQuery<>();
                                            query.setLimit(1);
                                            query.addWhereEqualTo("userId", post.user.userId);
                                            query.addWhereEqualTo("rootUser", user);
                                            query.findObjects(new FindListener<MyFocusCommonPerson>() {
                                                @Override
                                                public void done(List<MyFocusCommonPerson> myFocusCommonPersons, BmobException e) {
                                                    if (e == null) {
                                                        if (myFocusCommonPersons.size() >= 1) {
                                                            InfoInterestPersonActivity.this.person = myFocusCommonPersons.get(0);
                                                            focus.setText("取消关注");
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
                            person.update(person.getObjectId(), new UpdateListener() {
                                @Override
                                public void done(BmobException e) {
                                    if (e == null) {
                                        focus.setText("取消关注");
                                        if (user != null) {
                                            BmobQuery<MyFocusCommonPerson> query = new BmobQuery<>();
                                            query.setLimit(1);
                                            query.addWhereEqualTo("userId", post.user.userId);
                                            query.addWhereEqualTo("rootUser", user);
                                            query.findObjects(new FindListener<MyFocusCommonPerson>() {
                                                @Override
                                                public void done(List<MyFocusCommonPerson> myFocusCommonPersons, BmobException e) {
                                                    if (e == null) {
                                                        if (myFocusCommonPersons.size() >= 1) {
                                                            InfoInterestPersonActivity.this.person = myFocusCommonPersons.get(0);
                                                            focus.setText("取消关注");
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
                        FragmentDialog.newInstance(false, "是否取消关注", "真的要取消关注人家吗", "继续关注", "取消关注", "", "", false, new FragmentDialog.OnClickBottomListener() {
                            @Override
                            public void onPositiveClick(final Dialog dialog, boolean deleteFileSource) {

                                dialog.dismiss();
                            }

                            @Override
                            public void onNegtiveClick(Dialog dialog) {
                                if (person != null) {
                                    person.delete(person.getObjectId(), new UpdateListener() {
                                        @Override
                                        public void done(BmobException e) {
                                            if (e == null) {
                                                focus.setText("关注ta");
                                                person = null;
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


    void getTextAndImageData() {
        String url = CommonUtil.getToggle(this, Constants.TEXT_IMG_POST).getToggleObject().replace("USERID", post.user.userId).replace("CTIME", "0");
        OkHttpClientManager.parseRequest(this, url, handler, Constants.REFRESH);
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            InterestPostListInfoPersonParentData data =
                    CommonUtil.parseJsonWithGson((String) msg.obj, InterestPostListInfoPersonParentData.class);
            InterstPostListInfoResult result = null;
            if (data != null) {
                result = data.result;
            } else {
                return;
            }
            if (msg.what != Constants.NetWorkError) {

                if (!TextUtils.isEmpty(result.totalNums)) {
                    textpost.setText("图文动态(" + result.totalNums + ")");
                }

                if (result.posts != null && result.posts.size() > 0) {
                    listView = (HorizontialListView) findViewById(R.id.txt_img_listview);
                    ArrayList<String> imgs = new ArrayList<>();
                    for (InterestPost post : data.result.posts) {
                        if (post.images != null && post.images.size() > 0) {
                            imgs.addAll(post.images);
                        }
                    }

                    if (result.posts.size() > 0 && imgs.size() > 0) {
                        listView.setAdapter(new PostImageAdapter(InfoInterestPersonActivity.this, imgs, Constants.ITEM_USER_INFO_IMG, true, false));
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
        }
    };

}
