package com.act.quzhibo.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;

import com.act.quzhibo.R;
import com.act.quzhibo.common.Constants;
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
import com.bumptech.glide.Glide;
import com.youth.banner.Banner;

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
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shower_info);
        final Room room = (Room) getIntent().getSerializableExtra(Constants.ROOM_BUNDLE);
        if (room != null) {
            getData(room.userId);
        }
        BmobQuery<MyFocusShowers> query = new BmobQuery<>();
        query.addWhereEqualTo("userId", room.userId);
        query.addWhereEqualTo("rootUser", BmobUser.getCurrentUser(RootUser.class));
        query.findObjects(new FindListener<MyFocusShowers>() {
            @Override
            public void done(List<MyFocusShowers> myFocusShowers, BmobException e) {
                if (e == null) {
                    if (myFocusShowers.size() >= 1) {
                        ((TextView) findViewById(R.id.focus)).setText("已关注");
                    } else {
                        if (!((TextView) findViewById(R.id.focus)).getText().equals("已关注")) {
                            findViewById(R.id.focus).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    MyFocusShowers myFocusShowers = new MyFocusShowers();
                                    myFocusShowers.rootUser = BmobUser.getCurrentUser(RootUser.class);
                                    myFocusShowers.poster_path_400 = getIntent().getStringExtra("pathPrefix");
                                    myFocusShowers.nickname = room.nickname;
                                    myFocusShowers.roomId = room.roomId;
                                    myFocusShowers.userId = room.userId;
                                    myFocusShowers.gender = room.gender;
                                    myFocusShowers.city = room.city;
                                    myFocusShowers.save(new SaveListener<String>() {
                                        @Override
                                        public void done(String objectId, BmobException e) {
                                            if (e == null) {
                                                ((TextView) findViewById(R.id.focus)).setText("已关注");
                                                ToastUtil.showToast(ShowerInfoActivity.this, "关注成功");
                                            } else {
                                                ToastUtil.showToast(ShowerInfoActivity.this, "关注失败");
                                            }
                                        }
                                    });
                                }
                            });
                        }
                    }
                } else {
                    ToastUtil.showToast(ShowerInfoActivity.this, "请求异常");
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
                    String liveType = jsonObject.getString("liveType");
                    String gender = jsonObject.getString("gender");

                    JSONObject jsonObject1 = jsonObject.getJSONObject("getPhotoListResult");
                    if (msg.what != Constants.NetWorkError) {
                        ((TextView) findViewById(R.id.fansCount)).setText(fansCount != null ? "粉丝 " + fansCount : "");
                        if (introduce != null && !introduce.equals("")) {
                            findViewById(R.id.introduce).setVisibility(View.VISIBLE);
                            ((TextView) findViewById(R.id.introduce)).setText(introduce != null ? introduce : "");
                        }
                        ((TextView) findViewById(R.id.nickName)).setText(nickname != null ? nickname : "");
                        if (liveType.equals("0")) {
                            findViewById(R.id.isShowing).setVisibility(View.VISIBLE);
                            ((TextView) findViewById(R.id.isShowing)).setText("直播中");
                        }
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
                        if (gender.equals("0")) {
                            ((Banner) findViewById(R.id.banner)).setImages(urls).setImageLoader(new GlideImageLoader(R.drawable.women)).start();
                            Glide.with(ShowerInfoActivity.this).load(portrait_img).placeholder(R.drawable.women).into((CircleImageView) findViewById(R.id.userImage));
                        } else {
                            ((Banner) findViewById(R.id.banner)).setImages(urls).setImageLoader(new GlideImageLoader(R.drawable.man)).start();
                            Glide.with(ShowerInfoActivity.this).load(portrait_img).placeholder(R.drawable.man).into((CircleImageView) findViewById(R.id.userImage));

                        }
                    }
                } catch (JSONException e) {

                } catch (Exception e) {

                }
            } else {
                ToastUtil.showToast(ShowerInfoActivity.this, "请求失败");
            }
        }
    };

}
