package com.act.quzhibo.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.Toast;

import com.act.quzhibo.util.ToastUtil;
import com.devlin_n.floatWindowPermission.FloatWindowManager;
import com.devlin_n.videoplayer.controller.FullScreenController;
import com.devlin_n.videoplayer.player.IjkVideoView;

/**
 * 全屏播放
 * Created by Devlin_n on 2017/4/21.
 */

public class FullScreenActivity extends AppCompatActivity {

    private IjkVideoView ijkVideoView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ijkVideoView = new IjkVideoView(this);
        setContentView(ijkVideoView);

        if (TextUtils.isEmpty(getIntent().getStringExtra("videoUrl"))) {
            ToastUtil.showToast(this, "视频地址未找到，无法播放");
            return;
        }

        ijkVideoView
                .autoRotate()
                .alwaysFullScreen()
                .setTitle(getIntent().getStringExtra("videoTitle"))
                .setUrl(getIntent().getStringExtra("videoUrl"))
                .setVideoController(new FullScreenController(this))
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
        ijkVideoView.stopFloatWindow();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == FloatWindowManager.PERMISSION_REQUEST_CODE) {
            if (FloatWindowManager.getInstance().checkPermission(this)) {
                ijkVideoView.startFloatWindow();
            } else {
                Toast.makeText(FullScreenActivity.this, "权限授予失败，无法开启悬浮窗", Toast.LENGTH_SHORT).show();
            }
        }
    }
}