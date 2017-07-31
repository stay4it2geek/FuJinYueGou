package com.act.quzhibo.ui.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.act.quzhibo.R;
import com.act.quzhibo.adapter.MemberAdapter;
import com.act.quzhibo.adapter.PostImageAdapter;
import com.act.quzhibo.common.Constants;
import com.act.quzhibo.common.GlideImageLoader;
import com.act.quzhibo.entity.CommonPerson;
import com.act.quzhibo.entity.InterestPost;
import com.act.quzhibo.entity.InterestPostListInfoParentData;
import com.act.quzhibo.entity.InterestPostListInfoPersonParentData;
import com.act.quzhibo.okhttp.OkHttpUtils;
import com.act.quzhibo.okhttp.callback.StringCallback;
import com.act.quzhibo.util.CommonUtil;
import com.act.quzhibo.view.CircleImageView;
import com.act.quzhibo.view.HorizontialListView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.youth.banner.Banner;

import java.util.ArrayList;
import java.util.Random;

import okhttp3.Call;

/**
 * Created by asus-pc on 2017/5/30.
 * 普通用户档案界面
 */

public class InfoCommonActivity extends AppCompatActivity {

    private InterestPost post;
    GridView gridView;
    private Banner banner;
    private int second;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_common_layout);
        initView();
    }

    private void initView() {

        Display display = this.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        banner = (Banner) findViewById(R.id.banner);
        banner.setLayoutParams(new FrameLayout.LayoutParams(size.x - 10, size.x - 10));
        if (getIntent() != null) {
            int count = getIntent().getIntExtra("count", 0);
            Log.e("cccc", count + "");

            post = (InterestPost) getIntent().getSerializableExtra(Constants.POST);
            if (Integer.parseInt(post.user.userId) != CommonUtil.loadData(this, "userId")) {
                int max = 400;
                int min = 50;
                Random random = new Random();
                second = random.nextInt(max) % (max - min + 4) + min;
                CommonUtil.saveData(this, second, "time");
                CommonUtil.saveData(this, Integer.parseInt(post.user.userId), "userId");
            } else {
                second = CommonUtil.loadData(this, "time");
            }
            if (second != 0) {
                if (second % 60 == 0) {
                    ((TextView) findViewById(R.id.online_time)).setText(second / 60 + "分前在线");
                } else {
                    ((TextView) findViewById(R.id.online_time)).setText((second - (second % 60)) / 60 + "分" + second % 60 + "秒前在线");
                }
            }
            ArrayList<String> urls = new ArrayList<>();

            if (post.images != null && post.images.size() > 0) {
                urls.addAll(post.images);
            } else {
                urls.add(post.user.photoUrl);
            }
            if (post.user.sex.equals("2")) {
                banner.setImages(urls).setImageLoader(new GlideImageLoader(R.drawable.women)).start();
                Glide.with(this).load(post.user.photoUrl).asBitmap().placeholder(R.drawable.women).into((CircleImageView) findViewById(R.id.userImage));
            } else {
                banner.setImages(urls).setImageLoader(new GlideImageLoader(R.drawable.man)).start();
                Glide.with(this).load(post.user.photoUrl).asBitmap().placeholder(R.drawable.man).into((CircleImageView) findViewById(R.id.userImage));

            }
        }
        if (post.user.vipLevel.equals("1")) {
            ((TextView) findViewById(R.id.level)).setText("VIP");
        } else {
            ((TextView) findViewById(R.id.level)).setText("普通");
        }
        ((TextView) findViewById(R.id.disPurpose)).setText(post.user.disPurpose);
        ((TextView) findViewById(R.id.disMariState)).setText(post.user.disMariState);
        ((TextView) findViewById(R.id.nickName)).setText(post.user.nick);
        getTextAndImageData();
    }


    private void getTextAndImageData() {
        String url = CommonUtil.getToggle(this, Constants.TEXT_IMG_POST).getToggleObject().replace("USERID", post.user.userId).replace("CTIME", "0");
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
            final InterestPostListInfoPersonParentData data =
                    CommonUtil.parseJsonWithGson((String) msg.obj, InterestPostListInfoPersonParentData.class);
            if (data.result != null) {
                if (!TextUtils.isEmpty(data.result.totalNums)) {
                    ((TextView) findViewById(R.id.textpost)).setText("图文动态(" + data.result.totalNums + ")");
                }
                if (data.result.posts != null && data.result.posts.size() > 0) {
                    gridView = (GridView) findViewById(R.id.txt_img_gridview);
                    ArrayList<String> imgs = new ArrayList<>();
                    for (InterestPost post : data.result.posts) {
                        if (post.images != null && post.images.size() > 0) {
                            imgs.addAll(post.images);
                        }
                    }
                    if (data.result.posts.size() > 0 && imgs.size() > 0) {
                        gridView.setAdapter(new PostImageAdapter(InfoCommonActivity.this, imgs, 2));
                        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                Intent intent = new Intent();
                                intent.putExtra(Constants.COMMON_USER_ID, post.user.userId);
                                intent.setClass(InfoCommonActivity.this, CommonPersonPostActivity.class);
                                startActivity(intent);
                            }
                        });
                    }


                }

                //todo error

            }
        }
    };


}
