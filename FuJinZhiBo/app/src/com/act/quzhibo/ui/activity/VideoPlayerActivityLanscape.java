package com.act.quzhibo.ui.activity;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.VideoView;

import com.act.quzhibo.R;
import com.act.quzhibo.adapter.ViewPlayerPageAdapter;
import com.act.quzhibo.common.Constants;
import com.act.quzhibo.entity.Room;
import com.act.quzhibo.ui.fragment.ChatFragment;
import com.act.quzhibo.ui.fragment.NoViewFragment;

import java.util.ArrayList;

public class VideoPlayerActivityLanscape extends FragmentActivity implements ChatFragment.OnFinishVideoCallbak {
    private ViewPager viewPager;
    private ArrayList<Fragment> fragments;
    private ChatFragment chatFragment;
    private NoViewFragment noViewFragment;
    private VideoView videoView;
    private Room room;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewplay);
        initView();
    }

    private void initView() {
        videoView = (VideoView) findViewById(R.id.video);
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        fragments = new ArrayList<>();
        room = (Room) getIntent().getSerializableExtra("room");
        Bundle bundle = new Bundle();
        bundle.putSerializable("room", room);
        chatFragment = new ChatFragment();
        chatFragment.setArguments(bundle);
        noViewFragment = new NoViewFragment();
        fragments.add(chatFragment);
        fragments.add(noViewFragment);
        final ProgressBar bar = (ProgressBar) findViewById(R.id.bar);
        bar.requestFocus();
        final Uri uri = Uri.parse(room.liveStream);
        videoView.setVideoURI(uri);
        videoView.start();
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                bar.setVisibility(View.GONE);
            }
        });
        videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                bar.setVisibility(View.GONE);
                findViewById(R.id.hasNotshow).setVisibility(View.VISIBLE);
                return true;
            }
        });
        ViewPlayerPageAdapter myFragmentPagerAdapter = new ViewPlayerPageAdapter(getSupportFragmentManager(), fragments);
        viewPager.setAdapter(myFragmentPagerAdapter);
        viewPager.setCurrentItem(0);
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        videoView.stopPlayback();
        finish();
    }
}
