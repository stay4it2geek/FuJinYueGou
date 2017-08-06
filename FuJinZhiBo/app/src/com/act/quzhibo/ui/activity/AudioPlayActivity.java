package com.act.quzhibo.ui.activity;

import android.app.Activity;

import java.io.IOException;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SeekBar;

import com.act.quzhibo.R;
import com.act.quzhibo.common.Constants;


public class AudioPlayActivity extends Activity implements OnClickListener, SeekBar.OnSeekBarChangeListener {
    private MediaPlayer mMediaPlayer = null;//媒体播放器
    private AudioManager mAudioManager = null;//声音管理器
    private Button mPlayButton = null;
    private Button mPauseButton = null;
    private Button mStopButton = null;
    private SeekBar mSoundSeekBar = null;
    private int maxStreamVolume;//最大音量
    private int currentStreamVolume;//当前音量

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.audioplayer);
        findViewById(R.id.audio_layout).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        String uri=getIntent().getStringExtra(Constants.AUDIO);
        mMediaPlayer = MediaPlayer.create(this, Uri.parse(uri));//加载res/raw的happyis.mp3文件
        mAudioManager = (AudioManager) this.getSystemService(AUDIO_SERVICE);
        mPlayButton = (Button) findViewById(R.id.Play);
        mPauseButton = (Button) findViewById(R.id.Pause);
        mStopButton = (Button) findViewById(R.id.Stop);
        mSoundSeekBar = (SeekBar) findViewById(R.id.SoundSeekBar);
        mPlayButton.setOnClickListener(this);
        mPauseButton.setOnClickListener(this);
        mStopButton.setOnClickListener(this);
        maxStreamVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        currentStreamVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        mSoundSeekBar.setMax(maxStreamVolume);
        mSoundSeekBar.setProgress(currentStreamVolume);
        mSoundSeekBar.setOnSeekBarChangeListener(this);
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.Play:
                mMediaPlayer.start();
                break;
            case R.id.Pause:
                mMediaPlayer.pause();
                break;
            case R.id.Stop:
                System.out.println("Stop");
                mMediaPlayer.stop();
                try {
                    mMediaPlayer.prepare();
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mMediaPlayer.seekTo(0);
                break;
            default:
                break;
        }
    }

    //进度条变化
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress,
                                  boolean fromUser) {
        // TODO Auto-generated method stub
        System.out.println("progress:" + String.valueOf(progress));
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, AudioManager.FLAG_PLAY_SOUND);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        // TODO Auto-generated method stub

    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        if(mMediaPlayer != null) {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.stop();
            }
            mMediaPlayer.release();
            mMediaPlayer = null;
        }

        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        if(mMediaPlayer != null) {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.stop();
            }
            mMediaPlayer.release();
            mMediaPlayer = null;
        }

        super.onBackPressed();
    }
}