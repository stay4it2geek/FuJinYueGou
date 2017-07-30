package com.act.quzhibo.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.act.quzhibo.R;
import com.act.quzhibo.common.Constants;
import com.act.quzhibo.common.GlideImageLoader;
import com.act.quzhibo.entity.Room;
import com.act.quzhibo.okhttp.OkHttpUtils;
import com.act.quzhibo.okhttp.callback.StringCallback;
import com.act.quzhibo.util.CommonUtil;
import com.act.quzhibo.view.CircleImageView;
import com.bumptech.glide.Glide;
import com.youth.banner.Banner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

/**
 * Created by asus-pc on 2017/5/30.
 */

/**
 * 主播个人档案
 */
public class ShowerInfoActivityLandscape extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shower_info);
        Room room = (Room) getIntent().getSerializableExtra(Constants.ROOM_BUNDLE);
        if (room != null) {
            getData(room.userId);
        }
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
                handler.sendMessage(message);
            }
        });
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
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
                    ((TextView) findViewById(R.id.introduce)).setText(introduce != null ? introduce : "");
                    ((TextView) findViewById(R.id.nickName)).setText(nickname != null ? nickname : "");
                    if (liveType.equals("1")) {
                        findViewById(R.id.isShowing).setVisibility(View.VISIBLE);
                        ((TextView) findViewById(R.id.isShowing)).setText("直播中");
                    }
                    if (introduce != null&&!introduce.equals("")) {
                        findViewById(R.id.introduce).setVisibility(View.VISIBLE);
                        ((TextView) findViewById(R.id.introduce)).setText(introduce != null ? introduce : "");
                    }
                    JSONArray photos = jsonObject1.getJSONArray("photoList");

                    List<String> urls = new ArrayList<>();
                    if (photos != null && photos.length() > 0) {
                        for (int i = 0; i < photos.length(); i++) {
                            JSONObject jsonObject2=photos.getJSONObject(i);
                            String url= (String) jsonObject2.get("photo_path_original");
                            urls.add(url);
                        }
                    } else {
                        urls.clear();
                        urls.add(portrait_img);
                    }
                    if (gender.equals("0")) {
                        ((Banner) findViewById(R.id.banner)).setImages(urls).setImageLoader(new GlideImageLoader(R.drawable.women)).start();
                        Glide.with(ShowerInfoActivityLandscape.this).load(portrait_img).placeholder(R.drawable.women).into((CircleImageView) findViewById(R.id.userImage));
                    } else {
                        ((Banner) findViewById(R.id.banner)).setImages(urls).setImageLoader(new GlideImageLoader(R.drawable.man)).start();
                        Glide.with(ShowerInfoActivityLandscape.this).load(portrait_img).placeholder(R.drawable.man).into((CircleImageView) findViewById(R.id.userImage));

                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

}
