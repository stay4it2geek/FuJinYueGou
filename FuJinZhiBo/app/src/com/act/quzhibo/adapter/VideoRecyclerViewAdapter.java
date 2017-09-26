package com.act.quzhibo.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.act.quzhibo.MyStandardVideoController;
import com.act.quzhibo.R;
import com.act.quzhibo.common.Constants;
import com.act.quzhibo.download.activity.DownloadManagerActivity;
import com.act.quzhibo.download.callback.OnVideoControllerListner;
import com.act.quzhibo.download.db.DBController;
import com.act.quzhibo.download.domain.MediaInfo;
import com.act.quzhibo.download.domain.MediaInfoLocal;
import com.act.quzhibo.ui.activity.FullScreenActivity;
import com.act.quzhibo.util.ToastUtil;
import com.act.quzhibo.view.FragmentDialog;
import com.bumptech.glide.Glide;
import com.devlin_n.videoplayer.player.IjkVideoView;

import java.io.File;
import java.sql.SQLException;
import java.util.List;

import cn.woblog.android.downloader.DownloadService;
import cn.woblog.android.downloader.callback.DownloadListener;
import cn.woblog.android.downloader.callback.DownloadManager;
import cn.woblog.android.downloader.domain.DownloadInfo;
import cn.woblog.android.downloader.exception.DownloadException;

import static cn.woblog.android.downloader.DownloadService.downloadManager;
import static cn.woblog.android.downloader.domain.DownloadInfo.STATUS_WAIT;

public class VideoRecyclerViewAdapter extends RecyclerView.Adapter<VideoRecyclerViewAdapter.VideoHolder> {

    boolean isSdCardExist;
    private DownloadManager downloadManager;
    DBController dbController;
    private List<MediaInfo> videos;
    private FragmentActivity context;

    public VideoRecyclerViewAdapter(List<MediaInfo> videos, FragmentActivity context) {
        this.videos = videos;
        this.context = context;

        downloadManager = DownloadService.getDownloadManager(context.getApplicationContext());
        isSdCardExist = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);// 判断sdcard是否存在
        try {
            dbController = DBController.getInstance(context.getApplicationContext());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public VideoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_video, parent, false);
        return new VideoHolder(itemView);

    }

    @Override
    public void onBindViewHolder(final VideoHolder holder, int position) {

        final MediaInfo videoBean = videos.get(position);
        Glide.with(context)
                .load(videoBean.getIcon())
                .crossFade()
                .placeholder(android.R.color.darker_gray)
                .error(R.drawable.error_img).into(holder.controller.getThumb());
        holder.ijkVideoView
                .enableCache()
                .addToPlayerManager()
                .setUrl(videoBean.getUrl())
                .setTitle(videoBean.getTitle())
                .setVideoController(holder.controller);
        holder.ijkVideoView.setTag(position);
        holder.controller.setOnVideoControllerListner(new OnVideoControllerListner() {
            @Override
            public void onMyVideoController(String controllerFlg) {
                if (Constants.DOWNLAOD_VIDEO.equals(controllerFlg)) {
                    if (downloadManager.findAllDownloading().size() > 10) {
                        ToastUtil.showToast(context, "下载任务最多10个,请稍后下载");
                        if (downloadManager.findAllDownloaded().size() > 20) {
                            FragmentDialog.newInstance(false, "已下载任务最多20个，请清除掉一些吧", "", "确定", "取消", -1, false, new FragmentDialog.OnClickBottomListener() {
                                @Override
                                public void onPositiveClick(Dialog dialog, boolean needDelete) {
                                    Intent videoIntent = new Intent();
                                    videoIntent.putExtra(Constants.DOWN_LOAD_TYPE, Constants.VIDEO_ALBUM);
                                    videoIntent.setClass(context, DownloadManagerActivity.class);
                                    context.startActivity(videoIntent);
                                    dialog.dismiss();
                                }

                                @Override
                                public void onNegtiveClick(Dialog dialog) {
                                    dialog.dismiss();
                                }
                            }).show(context.getSupportFragmentManager(), "");
                            ;
                        }
                    } else {
                        FragmentDialog.newInstance(false, "您是否要下载到本地？", "提示:缓冲完成后可离线观看无需下载", "继续观看", "确认下载", -1, false, new FragmentDialog.OnClickBottomListener() {
                            @Override
                            public void onPositiveClick(Dialog dialog, boolean needDelete) {

                                dialog.dismiss();
                            }

                            @Override
                            public void onNegtiveClick(Dialog dialog) {
                                downLoadVideo(videoBean);
                                dialog.dismiss();
                            }
                        }).show(context.getSupportFragmentManager(), "");

                    }
                } else {
                    Intent intent = new Intent();
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("videoBean", videoBean);
                    intent.putExtras(bundle);
                    intent.putExtra("isLocal", false);
                    intent.setClass(context, FullScreenActivity.class);
                    context.startActivity(intent);
                }

            }
        });

    }

    private void downLoadVideo(MediaInfo videoBean) {
        String url = videoBean.getUrl() + "";
        DownloadInfo downloadInfo = downloadManager.getDownloadById(url.hashCode());
        if (isSdCardExist) {
            File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), Constants.VIDEO_DOWNLOAD);
            if (!file.exists()) {
                file.mkdirs();
            }
            String path = file.getAbsolutePath().concat("/").concat(videoBean.getName());
            File localFile = new File(path);
            if (localFile.exists() && localFile.isFile() && downloadInfo != null) {
                switch (downloadInfo.getStatus()) {
                    case DownloadInfo.STATUS_NONE:
                    case DownloadInfo.STATUS_PAUSED:
                    case DownloadInfo.STATUS_ERROR:
                        downloadManager.resume(downloadInfo);
                        break;
                    case DownloadInfo.STATUS_DOWNLOADING:
                    case DownloadInfo.STATUS_PREPARE_DOWNLOAD:
                    case STATUS_WAIT:
                        downloadManager.pause(downloadInfo);
                        break;
                    case DownloadInfo.STATUS_COMPLETED:
                        ToastUtil.showToast(context, "您已经保存过视频");
                        break;
                }
            } else {
                createDownload(videoBean, url);
            }
        }

    }

    @Override
    public int getItemCount() {
        return videos.size();
    }

    class VideoHolder extends RecyclerView.ViewHolder {

        private IjkVideoView ijkVideoView;
        private MyStandardVideoController controller;

        VideoHolder(View itemView) {
            super(itemView);
            ijkVideoView = (IjkVideoView) itemView.findViewById(R.id.video_player);
            int widthPixels = context.getResources().getDisplayMetrics().widthPixels;
            ijkVideoView.setLayoutParams(new LinearLayout.LayoutParams(widthPixels, widthPixels / 16 * 9));
            controller = new MyStandardVideoController(context);
            controller.setInitData(true, false);
            ijkVideoView.setVideoController(controller);
        }
    }


    class MyDownloadListener implements DownloadListener {
        DownloadInfo downloadInfo;

        public MyDownloadListener(DownloadInfo downloadInfo) {
            this.downloadInfo = downloadInfo;
        }

        @Override
        public void onStart() {
            ToastUtil.showToast(context, "开始保存");
        }

        @Override
        public void onWaited() {
            ToastUtil.showToast(context, "等待保存");

        }

        @Override
        public void onPaused() {
            ToastUtil.showToast(context, "暂停保存");
        }

        @Override
        public void onDownloading(long progress, long size) {
            ToastUtil.showToast(context, "正在保存");


        }

        @Override
        public void onRemoved() {
            ToastUtil.showToast(context, "已删除保存任务");
            downloadInfo = null;


        }

        @Override
        public void onDownloadSuccess() {
            ToastUtil.showToast(context, "保存成功");
        }

        @Override
        public void onDownloadFailed(DownloadException e) {
            ToastUtil.showToast(context, "保存失败，原因是：" + e.getMessage());
        }

    }

    private DownloadInfo createDownload(MediaInfo mediaInfo, String url) {
        DownloadInfo downloadInfo = null;
        if (isSdCardExist) {
            File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), Constants.VIDEO_DOWNLOAD);
            if (!file.exists()) {
                file.mkdirs();
            }
            String path = file.getAbsolutePath().concat("/").concat(mediaInfo.getName());

            File localFile = new File(path);
            if (localFile.isFile() && localFile.exists()) {
                localFile.delete();
            }
            downloadInfo = new DownloadInfo.Builder().setUrl(url).setPath(path).build();
            downloadInfo.setDownloadListener(new MyDownloadListener(downloadInfo));
            downloadManager.download(downloadInfo);
            //save extra info to my database.
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

}