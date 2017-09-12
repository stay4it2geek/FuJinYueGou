package com.act.quzhibo.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.act.quzhibo.R;
import com.act.quzhibo.adapter.InterestPostListAdapter;
import com.act.quzhibo.common.Constants;
import com.act.quzhibo.download.event.FocusChangeEvent;
import com.act.quzhibo.entity.MyFocusShowers;
import com.act.quzhibo.entity.RootUser;
import com.act.quzhibo.entity.Toggle;
import com.act.quzhibo.entity.VipOrders;
import com.act.quzhibo.util.GlideImageLoader;
import com.act.quzhibo.entity.Room;
import com.act.quzhibo.okhttp.OkHttpUtils;
import com.act.quzhibo.okhttp.callback.StringCallback;
import com.act.quzhibo.util.CommonUtil;
import com.act.quzhibo.util.ToastUtil;
import com.act.quzhibo.view.CircleImageView;
import com.act.quzhibo.view.LoadNetView;
import com.act.quzhibo.view.TitleBarView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.youth.banner.Banner;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;
import okhttp3.Call;


public class ShowerInfoActivity extends Activity {
    private Room room;
    private LoadNetView loadNetView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shower_info);
        room = (Room) getIntent().getSerializableExtra("room");
        if (room != null) {
            getData(room.userId);
        }
        TitleBarView titlebar = (TitleBarView) findViewById(R.id.titlebar);
        titlebar.setBarTitle("主 播 档 案");
        titlebar.setBackButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowerInfoActivity.this.finish();
            }
        });
        loadNetView = (LoadNetView) findViewById(R.id.loadview);
        loadNetView.setReloadButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadNetView.setlayoutVisily(Constants.LOAD);
                getData(room.userId);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        BmobQuery<MyFocusShowers> query = new BmobQuery<>();
        query.addWhereEqualTo("userId", room.userId);
        query.addWhereEqualTo("rootUser", BmobUser.getCurrentUser(RootUser.class));
        query.findObjects(new FindListener<MyFocusShowers>() {
            @Override
            public void done(List<MyFocusShowers> myFocusShowers, BmobException e) {
                if (e == null) {
                    if (myFocusShowers.size() >= 1) {
                        ((TextView) findViewById(R.id.focus)).setText("已关注");
                    }
                } else {
                    ToastUtil.showToast(ShowerInfoActivity.this, "请求异常");
                }
            }
        });

        findViewById(R.id.focus).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (BmobUser.getCurrentUser(RootUser.class) != null) {
                    if (!(((TextView) findViewById(R.id.focus)).getText().toString().trim()).equals("已关注")) {
                        MyFocusShowers myFocusShowers = new MyFocusShowers();

                        if (getIntent().getBooleanExtra("FromChatFragment", false)) {
                            myFocusShowers.portrait_path_1280 = getIntent().getStringExtra("photoUrl");
                        } else if (getIntent().getBooleanExtra("FromShowListFragment", false)) {
                            myFocusShowers.portrait_path_1280 = "http://ures.kktv8.com/kktv" + room.portrait_path_1280;

                        } else {
                            myFocusShowers.portrait_path_1280 = room.portrait_path_1280;
                        }
                        myFocusShowers.rootUser = BmobUser.getCurrentUser(RootUser.class);
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
                                    ((TextView) findViewById(R.id.focus)).setText("已关注");
                                    EventBus.getDefault().post(new FocusChangeEvent());
                                    ToastUtil.showToast(ShowerInfoActivity.this, "关注成功");
                                } else {
                                    ToastUtil.showToast(ShowerInfoActivity.this, "关注失败");
                                }
                            }
                        });
                    } else {
                        ToastUtil.showToast(ShowerInfoActivity.this, "已关注");
                    }
                } else {
                    startActivity(new Intent(ShowerInfoActivity.this, LoginActivity.class));
                }

            }
        });

    }

    public void getData(String userId) {
        String url = CommonUtil.getToggle(this, Constants.SHOWER_INFO).getToggleObject().replace("USERID", userId);
        OkHttpUtils.get().url(url).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                handler.sendEmptyMessage(Constants.NetWorkError);
            }

            @Override
            public void onResponse(String response, int id) {
                Message message = handler.obtainMessage();
                message.obj = response;
                message.what = Constants.REFRESH;
                handler.sendMessage(message);
            }
        });
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what != Constants.NetWorkError) {
                try {
                    JSONObject jsonObject = new JSONObject((String) msg.obj);
                    String fansCount = jsonObject.getString("fansCount");
                    String nickname = jsonObject.getString("nickname");
                    String introduce = jsonObject.getString("introduce");
                    String portrait_img = jsonObject.getString("portrait_path_1280");
                    String gender = jsonObject.getString("gender");
                    JSONObject jsonObject1 = jsonObject.getJSONObject("getPhotoListResult");
                    if (msg.what != Constants.NetWorkError) {
                        ((TextView) findViewById(R.id.fansCount)).setText(fansCount != null ? "粉丝 " + fansCount : "");
                        if (introduce != null && !introduce.equals("")) {
                            findViewById(R.id.introduce).setVisibility(View.VISIBLE);
                            ((TextView) findViewById(R.id.introduce)).setText(introduce != null ? introduce : "");
                        }
                        ((TextView) findViewById(R.id.nickName)).setText(nickname != null ? nickname : "");

                        JSONArray photos = jsonObject1.getJSONArray("photoList");

                        List<String> urls = new ArrayList<>();
                        if (photos != null && photos.length() > 0) {
                            for (int i = 0; i < photos.length(); i++) {
                                JSONObject jsonObject2 = photos.getJSONObject(i);
                                String url = (String) jsonObject2.get("photo_path_original");
                                urls.add(url);
                            }
                        } else {
                            urls.clear();
                            urls.add(portrait_img);
                        }
                        final ImageView zhuboAvatar = (ImageView) findViewById(R.id.userImage);

                        if (gender.equals("0")) {
                            Glide.with(ShowerInfoActivity.this).load(portrait_img).asBitmap().placeholder(R.drawable.women).into(new SimpleTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                    zhuboAvatar.setBackgroundDrawable(new BitmapDrawable(resource));
                                }

                                @Override
                                public void onLoadStarted(Drawable placeholder) {
                                    super.onLoadStarted(placeholder);
                                }
                            });
                            ((Banner) findViewById(R.id.banner)).setImages(urls).setImageLoader(new GlideImageLoader(R.drawable.women)).start();
                        } else {
                            Glide.with(ShowerInfoActivity.this).load(portrait_img).asBitmap().placeholder(R.drawable.man).into(new SimpleTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                    zhuboAvatar.setBackgroundDrawable(new BitmapDrawable(resource));
                                }

                                @Override
                                public void onLoadStarted(Drawable placeholder) {
                                    super.onLoadStarted(placeholder);
                                }
                            });
                            ((Banner) findViewById(R.id.banner)).setImages(urls).setImageLoader(new GlideImageLoader(R.drawable.man)).start();

                        }

                        loadNetView.setVisibility(View.GONE);
                    }

                } catch (JSONException e) {

                } catch (Exception e) {

                }

            } else {
                loadNetView.setVisibility(View.VISIBLE);
                loadNetView.setlayoutVisily(Constants.RELOAD);
            }
        }
    };

}
