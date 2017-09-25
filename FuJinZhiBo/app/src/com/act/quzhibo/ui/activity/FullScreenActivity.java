package com.act.quzhibo.ui.activity;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
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
import com.act.quzhibo.download.activity.DownloadManagerActivity;
import com.act.quzhibo.download.callback.OnVideoControllerListner;
import com.act.quzhibo.download.db.DBController;
import com.act.quzhibo.download.domain.MediaInfo;
import com.act.quzhibo.download.domain.MediaInfoLocal;
import com.act.quzhibo.util.ToastUtil;
import com.act.quzhibo.view.FragmentDialog;
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
import permission.auron.com.marshmallowpermissionhelper.ActivityManagePermission;
import permission.auron.com.marshmallowpermissionhelper.PermissionResult;
import permission.auron.com.marshmallowpermissionhelper.PermissionUtils;

import static cn.woblog.android.downloader.domain.DownloadInfo.STATUS_WAIT;
import static com.act.quzhibo.common.Constants.REQUEST_SETTING;


public class FullScreenActivity extends ActivityManagePermission {

    private IjkVideoView ijkVideoView;
    boolean isSdCardExist;
    private DownloadManager downloadManager;
    DBController dbController;
    MediaInfo mediaInfo;
    private DownloadInfo downloadInfo;

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


        mediaInfo = getIntent().getParcelableExtra("videoBean");
        MediaInfoLocal myBusinessInfLocal = new MediaInfoLocal(
                mediaInfo.getUrl().hashCode(), mediaInfo.getName(), mediaInfo.getIcon(), mediaInfo.getUrl(), mediaInfo.getType(), mediaInfo.getTitle(), mediaInfo.getLocalPath());
        if (TextUtils.isEmpty(myBusinessInfLocal.getUrl())) {
            ToastUtil.showToast(this, "视频地址未找到，无法播放");
            return;
        }
        MyFullScreenController myFullScreenController = new MyFullScreenController(this);
        myFullScreenController.setIslocal(getIntent().getBooleanExtra("isLocal", false));
        myFullScreenController.setOnVideoControllerListner(new OnVideoControllerListner() {
            @Override
            public void onMyVideoController(String controllerFlg) {
                if (downloadManager.findAllDownloading().size() > 10) {
                    ToastUtil.showToast(FullScreenActivity.this, "下载任务最多10个,请稍后下载");
                    if (downloadManager.findAllDownloaded().size() > 20) {
                        FragmentDialog.newInstance(false, "已下载任务最多20个，请清除掉一些吧", "", "确定", "取消", -1, false, new FragmentDialog.OnClickBottomListener() {
                            @Override
                            public void onPositiveClick(Dialog dialog, boolean needDelete) {
                                Intent videoIntent = new Intent();
                                videoIntent.putExtra(Constants.DOWN_LOAD_TYPE, Constants.VIDEO_ALBUM);
                                videoIntent.setClass(FullScreenActivity.this, DownloadManagerActivity.class);
                                startActivity(videoIntent);
                                dialog.dismiss();
                            }

                            @Override
                            public void onNegtiveClick(Dialog dialog) {
                                dialog.dismiss();
                            }
                        }).show(getSupportFragmentManager(), "");
                    }
                } else {
                    downLoadVideo();
                }
            }
        });
        ijkVideoView
                .autoRotate()
                .alwaysFullScreen()
                .enableCache()
                .setTitle(myBusinessInfLocal.getTitle())
                .setUrl(mediaInfo.getUrl())
                .setVideoController(myFullScreenController)
                .setScreenScale(IjkVideoView.SCREEN_SCALE_16_9)
                .start();
    }

    private void downLoadVideo() {
        downloadInfo = downloadManager.getDownloadById((mediaInfo.getUrl() + "").hashCode());
        if (isSdCardExist) {
            File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), Constants.VIDEO_DOWNLOAD);
            if (!file.exists()) {
                file.mkdirs();
            }
            File localFile = new File(file.getAbsolutePath().concat("/").concat(mediaInfo.getName()));
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
                FragmentDialog.newInstance(false, "您是否要下载到本地？", "提示:缓冲完成后可离线观看无需下载", "继续观看", "确认下载", -1, false, new FragmentDialog.OnClickBottomListener() {
                    @Override
                    public void onPositiveClick(Dialog dialog, boolean needDelete) {
                        dialog.dismiss();
                    }

                    @Override
                    public void onNegtiveClick(Dialog dialog) {
                        createDownload(mediaInfo);
                        dialog.dismiss();
                    }
                }).show(getSupportFragmentManager(), "");
            }
        }
    }


    class MyDownloadListener implements DownloadListener {

        public MyDownloadListener() {
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

    private DownloadInfo createDownload(MediaInfo mediaInfo) {
        if (isSdCardExist) {
            File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), Constants.VIDEO_DOWNLOAD);
            if (!file.exists()) {
                file.mkdirs();
            }
            String path = file.getAbsolutePath().concat("/").concat(mediaInfo.getName());
            File localFile = new File(path);
            if (localFile.isFile() && localFile.exists()) {
                file.delete();
            }
            downloadInfo = new DownloadInfo.Builder().setUrl(mediaInfo.getUrl()).setPath(path).build();
            downloadInfo.setDownloadListener(new MyDownloadListener());
            downloadManager.download(downloadInfo);
            MediaInfoLocal myBusinessInfLocal = new MediaInfoLocal(
                    mediaInfo.getUrl().hashCode(), mediaInfo.getName(), mediaInfo.getIcon(), mediaInfo.getUrl(), mediaInfo.getType(), mediaInfo.getTitle(), path);
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