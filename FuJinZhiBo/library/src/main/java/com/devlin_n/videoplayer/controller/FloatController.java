package com.devlin_n.videoplayer.controller;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ProgressBar;

import com.devlin_n.videoplayer.R;
import com.devlin_n.videoplayer.player.IjkVideoView;
import com.devlin_n.videoplayer.widget.PlayProgressButton;

/**
 * 悬浮播放控制器
 * Created by Devlin_n on 2017/6/1.
 */

public class FloatController extends BaseVideoController implements View.OnClickListener {

    private PlayProgressButton playProgressButton;
    private String value;
    ProgressBar progressBar;

    public FloatController(@NonNull Context context) {
        super(context);
    }

    public FloatController(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.layout_float_controller;
    }

    @Override
    protected void initView() {
        super.initView();
        this.setOnClickListener(this);
        controllerView.findViewById(R.id.btn_close).setOnClickListener(this);
        playProgressButton = (PlayProgressButton) controllerView.findViewById(R.id.play_progress_btn);
    }

    public void setPlayProgressButtonGone(String value) {
        this.value = value;
        progressBar = (ProgressBar) controllerView.findViewById(R.id.probar);
        playProgressButton = (PlayProgressButton) controllerView.findViewById(R.id.play_progress_btn);
        progressBar.setVisibility(VISIBLE);
        if (!TextUtils.equals(value, "gone")) {
            playProgressButton.setVisibility(VISIBLE);
            playProgressButton.setPlayButtonClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    doPauseResume();
                }
            });
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(GONE);
            }
        }, 2000);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_close) {
            mediaPlayer.stopFloatWindow();
        }
    }

    @Override
    public void setPlayState(int playState) {
        super.setPlayState(playState);
        if (!value.equals("gone")) {
            switch (playState) {
                case IjkVideoView.STATE_IDLE:
                    break;
                case IjkVideoView.STATE_PLAYING:
                    playProgressButton.setState(PlayProgressButton.STATE_PLAYING);
                    hide();
                    break;
                case IjkVideoView.STATE_PAUSED:
                    playProgressButton.setState(PlayProgressButton.STATE_PAUSE);
                    show(0);
                    break;
                case IjkVideoView.STATE_PREPARING:
                    playProgressButton.setState(PlayProgressButton.STATE_LOADING);
                    break;
                case IjkVideoView.STATE_PREPARED:
                    playProgressButton.setVisibility(GONE);
                    break;
                case IjkVideoView.STATE_ERROR:
                    break;
                case IjkVideoView.STATE_BUFFERING:
                    playProgressButton.setState(PlayProgressButton.STATE_LOADING);
                    playProgressButton.setVisibility(VISIBLE);
                    break;
                case IjkVideoView.STATE_BUFFERED:
                    playProgressButton.setState(PlayProgressButton.STATE_LOADING_END);
                    if (!mShowing) playProgressButton.setVisibility(GONE);
                    break;
                case IjkVideoView.STATE_PLAYBACK_COMPLETED:
                    playProgressButton.setState(PlayProgressButton.STATE_PAUSE);
                    show(0);
                    break;
            }
        }
    }


    @Override
    public void show() {
        show(sDefaultTimeout);
    }

    private void show(int timeout) {
        if (!mShowing) {
            playProgressButton.show();
            mShowing = true;
        }
        removeCallbacks(mFadeOut);
        if (timeout != 0) {
            postDelayed(mFadeOut, timeout);
        }
    }


    @Override
    public void hide() {
        if (mShowing) {
            playProgressButton.hide();
            mShowing = false;
        }
    }
}
