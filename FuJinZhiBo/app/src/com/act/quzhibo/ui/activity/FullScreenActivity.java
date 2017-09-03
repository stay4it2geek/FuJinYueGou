package com.act.quzhibo.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.act.quzhibo.MyFullScreenController;
import com.act.quzhibo.MyStandardVideoController;
import com.act.quzhibo.R;
import com.act.quzhibo.adapter.VideoRecyclerViewAdapter;
import com.act.quzhibo.common.Constants;
import com.act.quzhibo.download.callback.OnVideoControllerListner;
import com.act.quzhibo.download.db.DBController;
import com.act.quzhibo.download.domain.MediaInfo;
import com.act.quzhibo.download.domain.MediaInfoLocal;
import com.act.quzhibo.util.ToastUtil;
import com.devlin_n.floatWindowPermission.FloatWindowManager;
import com.devlin_n.videoplayer.controller.FullScreenController;
import com.devlin_n.videoplayer.player.IjkVideoView;

import java.io.File;
import java.sql.SQLException;

import cn.woblog.android.downloader.DownloadService;
import cn.woblog.android.downloader.callback.DownloadListener;
import cn.woblog.android.downloader.callback.DownloadManager;
import cn.woblog.android.downloader.domain.DownloadInfo;
import cn.woblog.android.downloader.exception.DownloadException;

import static cn.woblog.android.downloader.domain.DownloadInfo.STATUS_WAIT;

/**
 * 全屏播放
 * Created by Devlin_n on 2017/4/21.
 */

public class FullScreenActivity extends AppCompatActivity {

    private IjkVideoView ijkVideoView;
    boolean isSdCardExist;
    private DownloadManager downloadManager;
    DBController dbController;
    MediaInfo videoBean;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ijkVideoView = new IjkVideoView(this);
        setContentView(ijkVideoView);
        downloadManager = DownloadService.getDownloadManager(this.getApplicationContext());
        isSdCardExist = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);// 判断sdcard是否存在
        try {
            dbController = DBController.getInstance(this.getApplicationContext());
        } catch (SQLException e) {
            e.printStackTrace();
        }


        videoBean = getIntent().getParcelableExtra("videoBean");
        if (TextUtils.isEmpty(videoBean.getUrl())) {
            ToastUtil.showToast(this, "视频地址未找到，无法播放");
            return;
        }
        MyFullScreenController myFullScreenController = new MyFullScreenController(this);
        myFullScreenController.setOnVideoControllerListner(new OnVideoControllerListner() {
            @Override
            public void onMyVideoController(String controllerFlg) {
                if (Constants.DOWNLAOD_VIDEO.equals(controllerFlg)) {
                    downLoadVideo();
                }
            }
        });
        ijkVideoView
                .autoRotate()
                .alwaysFullScreen()
                .setTitle(videoBean.getTitle())
                .setUrl(videoBean.getUrl())
                .setVideoController(myFullScreenController)
                .setScreenScale(IjkVideoView.SCREEN_SCALE_16_9)
                .start();
    }

    private void downLoadVideo() {
        String url = videoBean.getUrl() + "";
        DownloadInfo downloadInfo = downloadManager.getDownloadById(url.hashCode());
        if (isSdCardExist) {
            File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "videoDownload");
            if (!file.exists()) {
                file.mkdirs();
            }
            File localFile = new File(file.getAbsolutePath().concat("/").concat(url.substring(url.length() - 10, url.length())));

            if (localFile.exists() && localFile.isFile()) {
                if (downloadInfo != null) {
                    switch (downloadInfo.getStatus()) {
                        case DownloadInfo.STATUS_NONE:
                        case DownloadInfo.STATUS_PAUSED:
                        case DownloadInfo.STATUS_ERROR:
                            downloadManager.resume(downloadInfo);
                            break;
                        case DownloadInfo.STATUS_DOWNLOADING:
                        case DownloadInfo.STATUS_PREPARE_DOWNLOAD:
                        case STATUS_WAIT:
                            //pause DownloadInfo
                            downloadManager.pause(downloadInfo);
                            break;
                        case DownloadInfo.STATUS_COMPLETED:
                            ToastUtil.showToast(FullScreenActivity.this, "您已经保存过该视频");
                            break;
                    }
                } else {
                    ToastUtil.showToast(FullScreenActivity.this, "您已经保存过该视频");
                }
            } else {
                createDownload(videoBean, url);
            }
        }

    }


    class MyDownloadListener implements DownloadListener {
        DownloadInfo downloadInfo;

        public MyDownloadListener(DownloadInfo downloadInfo) {
            this.downloadInfo = downloadInfo;
        }

        @Override
        public void onStart() {
            ToastUtil.showToast(FullScreenActivity.this, "开始保存");

        }

        @Override
        public void onWaited() {
            ToastUtil.showToast(FullScreenActivity.this, "等待保存");

        }

        @Override
        public void onPaused() {
            ToastUtil.showToast(FullScreenActivity.this, "暂停保存");

        }

        @Override
        public void onDownloading(long progress, long size) {
            ToastUtil.showToast(FullScreenActivity.this, "正在保存");

        }

        @Override
        public void onRemoved() {
            ToastUtil.showToast(FullScreenActivity.this, "已删除保存任务");
            downloadInfo = null;
        }

        @Override
        public void onDownloadSuccess() {
            ToastUtil.showToast(FullScreenActivity.this, "保存成功");
        }

        @Override
        public void onDownloadFailed(DownloadException e) {
            ToastUtil.showToast(FullScreenActivity.this, "保存失败，原因是：" + e.getMessage());

        }

    }

    private DownloadInfo createDownload(MediaInfo mediaInfo, String url) {
        DownloadInfo downloadInfo = null;
        if (isSdCardExist) {
            File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "videoDownload");
            if (!file.exists()) {
                file.mkdirs();
            }
            String path = file.getAbsolutePath().concat("/").concat(url.substring(url.length() - 10, url.length()));

            File localFile = new File(file.getAbsolutePath().concat("/").concat(url.substring(url.length() - 10, url.length())));
            if (localFile.isFile() && localFile.exists()) {
                file.delete();
            }

            downloadInfo = new DownloadInfo.Builder().setUrl(url).setPath(path).build();
            downloadInfo.setDownloadListener(new MyDownloadListener(downloadInfo));
            downloadManager.download(downloadInfo);


            //save extra info to my database.
            MediaInfoLocal myBusinessInfLocal = new MediaInfoLocal(
                    mediaInfo.getUrl().hashCode(), mediaInfo.getName(), mediaInfo.getIcon(), mediaInfo.getUrl(), mediaInfo.getType(), mediaInfo.getTitle());
            try {
                dbController.createOrUpdateMyDownloadInfo(myBusinessInfLocal);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return downloadInfo;
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