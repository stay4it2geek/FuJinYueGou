package com.act.quzhibo.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.act.quzhibo.MyFullScreenController;
import com.act.quzhibo.MyStandardVideoController;
import com.act.quzhibo.R;
import com.act.quzhibo.common.Constants;
import com.act.quzhibo.entity.InterestSubPerson;
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
        setContentView(ijkVideoView);
        if (TextUtils.isEmpty(getIntent().getStringExtra("videoUrl"))) {
            ToastUtil.showToast(this, "视频地址未找到，无法播放");
            return;
        }
        MyStandardVideoController controller = new MyStandardVideoController(this);
        controller.setInitData(true, true);
        Glide.with(this)
                .load(videoEntity.videoPic)
                .crossFade()
                .placeholder(android.R.color.darker_gray)
                .error(R.drawable.error_img).into(controller.getThumb());
        ijkVideoView
                .autoRotate()
                .alwaysFullScreen()
                .enableCache()
                .setTitle("")
                .setUrl(videoEntity.url)
                .setVideoController(controller)
                .setScreenScale(IjkVideoView.SCREEN_SCALE_16_9)
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
