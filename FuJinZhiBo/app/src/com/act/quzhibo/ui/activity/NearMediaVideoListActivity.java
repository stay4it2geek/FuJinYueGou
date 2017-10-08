package com.act.quzhibo.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.act.quzhibo.MyStandardVideoController;
import com.act.quzhibo.R;
import com.act.quzhibo.common.Constants;
import com.act.quzhibo.entity.NearVideoEntity;
import com.act.quzhibo.util.ToastUtil;
import com.bumptech.glide.Glide;
import com.devlin_n.videoplayer.player.IjkVideoView;

import permission.auron.com.marshmallowpermissionhelper.ActivityManagePermission;


public class NearMediaVideoListActivity extends ActivityManagePermission {

    private IjkVideoView ijkVideoView;
    NearVideoEntity videoEntity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        videoEntity = (NearVideoEntity) getIntent().getSerializableExtra(Constants.NEAR_USER_VIDEO);
        ijkVideoView = new IjkVideoView(this);
        MyStandardVideoController  controller = new MyStandardVideoController(this);
        controller.setInitData(false, false);
        ijkVideoView.setVideoController(controller);
        setContentView(ijkVideoView);
        if (TextUtils.isEmpty(videoEntity.url)) {
            ToastUtil.showToast(this, "视频地址未找到，无法播放");
            return;
        }
        Glide.with(this)
                .load(videoEntity.videoPic)
                .crossFade()
                .placeholder(android.R.color.darker_gray)
                .error(R.drawable.error_img).into(controller.getThumb());
        ijkVideoView
                .autoRotate()
                .enableCache()
                .setTitle("")
                .setUrl(videoEntity.url)
                .setScreenScale(IjkVideoView.SCREEN_SCALE_DEFAULT)
                .start();
    }


    @Override
    protected void onPause() {
        super.onPause();
        ijkVideoView.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        ijkVideoView.resume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ijkVideoView.release();
    }

    @Override
    public void onBackPressed() {
        if (!ijkVideoView.onBackPressed()) {
            super.onBackPressed();
        }
    }

}
