package com.act.quzhibo.ui.activity;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.act.quzhibo.R;
import com.act.quzhibo.common.Constants;
import com.act.quzhibo.common.GlideImageLoader;
import com.act.quzhibo.entity.NearPerson;
import com.act.quzhibo.util.CommonUtil;
import com.act.quzhibo.view.CircleImageView;
import com.bumptech.glide.Glide;
import com.youth.banner.Banner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;


public class NearInfoCommonActivity extends AppCompatActivity {

    private Banner banner;
    private NearPerson user;
    int second;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_common_layout);
        initView();
    }


    private void initView() {

        user = (NearPerson) getIntent().getSerializableExtra(Constants.NEAR_USER);
        findViewById(R.id.audio_layout).setVisibility(View.VISIBLE);
        findViewById(R.id.rl_zipai_img).setVisibility(View.VISIBLE);
        findViewById(R.id.rl_zipai_video).setVisibility(View.VISIBLE);
        findViewById(R.id.last_see_20_rl).setVisibility(View.VISIBLE);
        Glide.with(this).load(user.absCoverPic).asBitmap().placeholder(R.drawable.women).into((CircleImageView) findViewById(R.id.userImage));
        findViewById(R.id.rl_zipai_img).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        findViewById(R.id.rl_zipai_img).setOnClickListener(new View.OnClickListener() {
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
        if (user.vipType.equals("1")) {
            ((TextView) findViewById(R.id.level)).setText("VIP");
        } else {
            ((TextView) findViewById(R.id.level)).setText("普通");
        }
        int count = getIntent().getIntExtra("count", 0);
        if (count == 0&&Integer.parseInt(user.userId)!=CommonUtil.loadData(this,"userId")) {
            int max = 400;
            int min = 50;
            Random random = new Random();
            second = random.nextInt(max) % (max - min + 4) + min;
            CommonUtil.saveData(this,second,"time");
            CommonUtil.saveData(this,Integer.parseInt(user.userId),"userId");
        }else {
            second = CommonUtil.loadData(this,"time");
        }
        if(second!=0){
        if (second % 60 == 0) {
            ((TextView) findViewById(R.id.online_time)).setText(second / 60 + "分前在线");
        } else {
            ((TextView) findViewById(R.id.online_time)).setText((second - (second % 60)) / 60 + "分" + second % 60 + "秒前在线");
        }
        }
        ((TextView) findViewById(R.id.disPurpose)).setText(user.disPurpose == null ? "" : user.disPurpose);
        ((TextView) findViewById(R.id.disMariState)).setText(user.disMariState == null ? "" : user.disMariState);
        ((TextView) findViewById(R.id.nickName)).setText(user.username == null ? "" : user.username);

        if (getIntent() != null) {
            user = (NearPerson) getIntent().getSerializableExtra(Constants.NEAR_USER);
            findViewById(R.id.brocast).setVisibility(View.VISIBLE);
            findViewById(R.id.brocast).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    String uri = "http://file.nidong.com/" + user.soundUrl;
                    intent.setClass(NearInfoCommonActivity.this, AudioPlayActivity.class);
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
                    banner.setImages(Arrays.asList(new String[]{"http://file.nidong.com//" + user.pics})).setImageLoader(new GlideImageLoader(R.drawable.man)).start();
                }
            } else {
                if (user.sex.equals("2")) {
                    banner.setImages(Arrays.asList(new String[]{user.pics})).setImageLoader(new GlideImageLoader(R.drawable.women)).start();
                } else {
                    banner.setImages(Arrays.asList(new String[]{user.pics})).setImageLoader(new GlideImageLoader(R.drawable.man)).start();
                }

            }
        }
    }
}


