package com.act.quzhibo.download.adapter;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.act.quzhibo.R;
import com.act.quzhibo.common.Constants;
import com.act.quzhibo.common.adapter.BaseRecyclerViewAdapter;
import com.act.quzhibo.download.callback.OnDeleteListner;
import com.act.quzhibo.download.db.DBController;
import com.act.quzhibo.download.domain.MediaInfo;
import com.act.quzhibo.download.domain.MediaInfoLocal;
import com.act.quzhibo.ui.activity.BGAPhotoPreviewActivity;
import com.act.quzhibo.ui.activity.InfoNearPersonActivity;
import com.act.quzhibo.ui.activity.XImageActivity;
import com.act.quzhibo.view.FragmentDialog;
import com.bumptech.glide.Glide;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;

import cn.woblog.android.downloader.domain.DownloadInfo;

import static cn.woblog.android.downloader.DownloadService.downloadManager;

public class DownLoadedListAdapter extends BaseRecyclerViewAdapter<DownloadInfo, DownLoadedListAdapter.MyViewHolder> {
    private FragmentActivity activity;
    private DBController dbController;
    private OnDeleteListner deleteListner;

    public DownLoadedListAdapter(FragmentActivity activity) {
        super(activity);
        this.activity = activity;
        try {
            dbController = DBController.getInstance(context.getApplicationContext());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setOnDeleteListner(OnDeleteListner deleteListner) {
        this.deleteListner = deleteListner;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.item_downloaded_thumb, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final DownloadInfo downloadInfo = getData(position);
        try {
            if (downloadInfo != null && downloadInfo.getStatus() == DownloadInfo.STATUS_COMPLETED) {
                holder.delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FragmentDialog.newInstance(true, "确定删除?", "删除后不可恢复!", "确定", "取消", "", "", false, new FragmentDialog.OnClickBottomListener() {
                            @Override
                            public void onPositiveClick(Dialog dialog, boolean needDelete) {
                                deleteListner.onDelete(downloadInfo, position, needDelete);
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        downloadManager.remove(downloadInfo);
                                        try {
                                            dbController.deleteMyDownloadInfo(downloadInfo.getUri().hashCode());
                                        } catch (SQLException e) {
                                            e.printStackTrace();
                                        }

                                    }
                                }, 500);
                                dialog.dismiss();
                            }

                            @Override
                            public void onNegtiveClick(Dialog dialog) {
                                dialog.dismiss();
                            }
                        }).show(activity.getSupportFragmentManager(), "");
                    }
                });
                final MediaInfoLocal myDownloadInfoById = dbController.findMyDownloadInfoById(downloadInfo.getUri().hashCode());
                final MediaInfo mediaInfo = new MediaInfo(myDownloadInfoById.getTitle(),
                        myDownloadInfoById.getName(),
                        myDownloadInfoById.getIcon(),
                        myDownloadInfoById.getUrl(),
                        myDownloadInfoById.getType(),
                        myDownloadInfoById.getLocalPath());
                final ArrayList<MediaInfo> mMediaInfos = new ArrayList<>();
                mMediaInfos.add(mediaInfo);
                holder.download_item_layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (myDownloadInfoById.getType().equals(Constants.PHOTO_ALBUM)) {
                            Intent intent = new Intent();
                            intent.putExtra("position", position);
                            Bundle bundle = new Bundle();
                            bundle.putParcelableArrayList("mediaList", mMediaInfos);//
                            intent.putExtras(bundle);
                            intent.setClass(activity, BGAPhotoPreviewActivity.class);
                            activity.startActivity(intent);
                            if (mMediaInfos.size() > 0) {
                                activity.startActivity(BGAPhotoPreviewActivity.newIntent(activity, mMediaInfos, position));
                            }
                        } else {
                            Intent intent = new Intent();
                            intent.setAction(android.content.Intent.ACTION_VIEW);
                            File file = new File(mediaInfo.getLocalPath());
                            Uri uri;
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                Uri contentUri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".FileProvider", file);
                                intent.setDataAndType(contentUri, "video/*");
                            } else {
                                uri = Uri.fromFile(file);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.setDataAndType(uri, "video/*");
                            }
                            context.startActivity(intent);
                        }
                    }
                });
                if (myDownloadInfoById.getType().equals(Constants.PHOTO_ALBUM)) {
                    Glide.with(activity).load(myDownloadInfoById.getUrl()).thumbnail(0.1f).placeholder(R.drawable.placehoder_img).error(R.drawable.error_img).into(holder.imgThumb);//加载网络图片
                } else {
                    holder.videoLayout.setVisibility(View.VISIBLE);
                    Glide.with(activity).load(myDownloadInfoById.getIcon()).skipMemoryCache(false).placeholder(R.drawable.placehoder_img).error(R.drawable.error_img).into(holder.videoImg);//加载网络图片
                }

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        holder.delete.setVisibility(View.VISIBLE);
                    }
                }, 500);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView delete;
        private ImageView videoImg;
        private ImageView imgThumb;
        private FrameLayout videoLayout;
        private FrameLayout download_item_layout;

        public MyViewHolder(View view) {
            super(view);
            imgThumb = (ImageView) view.findViewById(R.id.imgThumb);
            videoImg = (ImageView) view.findViewById(R.id.videoImg);
            delete = (TextView) view.findViewById(R.id.delete);
            videoLayout = (FrameLayout) view.findViewById(R.id.videoLayout);
            download_item_layout = (FrameLayout) view.findViewById(R.id.download_item_layout);

        }
    }
}
