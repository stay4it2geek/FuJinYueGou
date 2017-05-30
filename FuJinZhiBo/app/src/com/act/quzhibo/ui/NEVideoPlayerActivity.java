package com.act.quzhibo.ui;

import android.annotation.TargetApi;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.act.quzhibo.R;
import com.act.quzhibo.adapter.MainFragmentPageAdapter;
import com.act.quzhibo.view.MyVideoView;

import java.util.ArrayList;


public class NEVideoPlayerActivity extends FragmentActivity implements ChatFragment.OnFinishVideoCallbak {
    private ViewPager vp;
    private ArrayList<Fragment> fragments;
    private ChatFragment chatFragment;
    private NoFragment noFragment;
    private MyVideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewplay);
        initView();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void initView() {

        vp = (ViewPager) findViewById(R.id.view_pager);
        // 设置viewPager的适配器
        fragments = new ArrayList<>();
        chatFragment = new ChatFragment();
        noFragment = new NoFragment();
        fragments.add(noFragment);
        fragments.add(chatFragment);
        final ProgressBar bar = (ProgressBar) findViewById(R.id.bar);
        bar.requestFocus();
        MainFragmentPageAdapter myFragmentPagerAdapter = new MainFragmentPageAdapter(getSupportFragmentManager(), fragments);
        vp.setAdapter(myFragmentPagerAdapter);
        vp.setCurrentItem(1);
        vp.addOnPageChangeListener(new MyOnPageChangeListener());
        videoView = (MyVideoView) findViewById(R.id.video);
        final String uri = ("http://pull.kktv8.com/livekktv/102950202.flv");
        videoView.setVideoURI(Uri.parse(uri));
        videoView.start();
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                bar.setVisibility(View.GONE);
                chatFragment.setViewVisily(true);
            }
        });
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                videoView.setVideoURI(Uri.parse(uri));
                videoView.start();
            }
        });
        videoView.setOnInfoListener(new MediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(MediaPlayer mp, int what, int extra) {
                Log.e("fdsfsd",what+""+"----"+extra);
                return false;
            }
        });
        videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {

            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                bar.setVisibility(View.GONE);
                Log.e("onError",what+""+"----"+extra);
                videoView.setVisibility(View.GONE);
                findViewById(R.id.show).setVisibility(View.VISIBLE);
                chatFragment.setViewVisily(false);
                return true;
            }
        });

    }

    @Override
    public void finishVideo() {
        videoView.stopPlayback();
        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
        videoView.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        videoView.start();
    }

    class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageScrollStateChanged(int arg0) {
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageSelected(int arg0) {
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        videoView.stopPlayback();
        finish();
    }
}
