package com.act.quzhibo.ui.activity;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.act.quzhibo.R;
import com.act.quzhibo.adapter.InterestPostListAdapter;
import com.act.quzhibo.adapter.InteretstPostPageAdapter;
import com.act.quzhibo.common.Constants;
import com.act.quzhibo.entity.InterestPostListInfoParentData;
import com.act.quzhibo.entity.InterstPostListInfoResult;
import com.act.quzhibo.entity.InterstUser;
import com.act.quzhibo.okhttp.OkHttpUtils;
import com.act.quzhibo.okhttp.callback.StringCallback;
import com.act.quzhibo.util.CommonUtil;
import com.act.quzhibo.view.FullyLinearLayoutManager;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import okhttp3.Call;

/**
 * Created by asus-pc on 2017/5/30.
 * 用户界面
 */

public class UserInfoActivity extends AppCompatActivity {
    private InterstUser user;
    private RecyclerView textlist;
    private RecyclerView videolist;
    private InterestPostListAdapter adapterText;
    private InterestPostListAdapter adapterVideo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        if (getIntent() != null) {
            user = (InterstUser) getIntent().getSerializableExtra(Constants.POST_USER);
        }
        Display display = this.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int height = size.x;
        textlist = (RecyclerView) findViewById(R.id.textlist);
        videolist = (RecyclerView) findViewById(R.id.videolist);
        FullyLinearLayoutManager linearLayoutManager2 = new FullyLinearLayoutManager(this);
        FullyLinearLayoutManager linearLayoutManager = new FullyLinearLayoutManager(this);
        textlist.setNestedScrollingEnabled(false);
        textlist.setLayoutManager(linearLayoutManager);
        videolist.setNestedScrollingEnabled(false);
        videolist.setLayoutManager(linearLayoutManager2);
        if (user.sex.equals("2")) {
            Glide.with(this).load(user.photoUrl).asBitmap().placeholder(R.drawable.women).into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                    (findViewById(R.id.userImage)).setBackgroundDrawable(new BitmapDrawable(resource));
                }

                @Override
                public void onLoadStarted(Drawable placeholder) {
                    super.onLoadStarted(placeholder);
                }
            });
        } else {
            Glide.with(this).load(user.photoUrl).asBitmap().placeholder(R.drawable.man).into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                    (findViewById(R.id.userImage)).setBackgroundDrawable(new BitmapDrawable(resource));
                }

                @Override
                public void onLoadStarted(Drawable placeholder) {
                    super.onLoadStarted(placeholder);
                }
            });

        }
        ((TextView) findViewById(R.id.sex)).setText(user.sex.equals("2") ? "女" : "男");
        ((TextView) findViewById(R.id.purpose)).setText(user.disPurpose);
        ((TextView) findViewById(R.id.disMariState)).setText(user.disMariState);
        getTextAndImageData(0);
        getVideoData(1);
        ((RadioGroup) findViewById(R.id.radiogroup)).setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.textpost:
                        findViewById(R.id.textlistlayout).setVisibility(View.VISIBLE);
                        findViewById(R.id.videolistlayout).setVisibility(View.GONE);
                        break;
                    case R.id.videopost:
                        findViewById(R.id.textlistlayout).setVisibility(View.GONE);
                        findViewById(R.id.videolistlayout).setVisibility(View.VISIBLE);
                        break;
                }
            }
        });
        ((RadioButton) findViewById(R.id.textpost)).setChecked(true);

    }

    private void getTextAndImageData(final int what) {
        String url = CommonUtil.getToggle(this, Constants.TEXT_IMG_POST).getToggleObject().replace("USERID", user.userId);

        OkHttpUtils.get().url(url).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                handler.sendEmptyMessage(Constants.NetWorkError);
            }

            @Override
            public void onResponse(String response, int id) {
                Message message = handler.obtainMessage();
                message.obj = response;
                Log.e("fdsafds", response + "");
                message.what = what;
                handler.sendMessage(message);
            }
        });
    }

    private void getVideoData(final int what) {
        String url = CommonUtil.getToggle(this, Constants.VIDEO_POST).getToggleObject().replace("USERID", user.userId);
        Log.e("fdsfdsf323afds", url + "");

        OkHttpUtils.get().url(url).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                handler.sendEmptyMessage(Constants.NetWorkError);
            }

            @Override
            public void onResponse(String response, int id) {
                Message message = handler.obtainMessage();
                message.obj = response;
                message.what = what;
                Log.e("fdsfdsf323afds", response + "");

                handler.sendMessage(message);
            }
        });
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if (msg.what != Constants.NetWorkError) {
                switch (msg.what) {
                    case 0:
                        InterestPostListInfoParentData data =
                                CommonUtil.parseJsonWithGson((String) msg.obj, InterestPostListInfoParentData.class);
                        if (data.result != null) {
                            if (!TextUtils.isEmpty(data.result.totalNums)) {
                                ((RadioButton) findViewById(R.id.textpost)).setText("图文动态(" + data.result.totalNums + ")");
                            }
                            if (data.result.posts != null && data.result.posts.size() > 0) {
                                if (adapterText == null) {
                                    adapterText = new InterestPostListAdapter(UserInfoActivity.this, data.result.posts);
                                    textlist.setAdapter(adapterText);
                                } else {
                                    adapterText.notifyDataSetChanged();
                                }
                            }
                            break;
                        }
                    case 1:
                        InterestPostListInfoParentData data2 =
                                CommonUtil.parseJsonWithGson((String) msg.obj, InterestPostListInfoParentData.class);
                        if (data2.result != null) {
                            if (!TextUtils.isEmpty(data2.result.totalNums)) {
                                ((RadioButton) findViewById(R.id.videopost)).setText("视频动态(" + data2.result.totalNums + ")");
                            }
                            if (data2.result.posts != null && data2.result.posts.size() > 0) {
                                if (adapterVideo == null) {
                                    adapterVideo = new InterestPostListAdapter(UserInfoActivity.this, data2.result.posts);
                                    videolist.setAdapter(adapterVideo);
                                } else {
                                    adapterVideo.notifyDataSetChanged();
                                }
                            }
                            break;
                        }
                        break;
                }
            } else {
                //todo error
                Log.e("fdsafds", "error");

            }
        }
    };

}
