package com.act.quzhibo.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
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
import com.act.quzhibo.download.domain.MediaInfoLocal;
import com.act.quzhibo.view.FragmentDialog;
import com.bumptech.glide.Glide;

import java.sql.SQLException;
import java.util.List;

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
        View view = LayoutInflater.from(activity).inflate(R.layout.item_com_download_img, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final DownloadInfo downloadInfo = getData(position);
        try {
            if (downloadInfo != null && downloadInfo.getStatus() == DownloadInfo.STATUS_COMPLETED) {
                holder.delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FragmentDialog.newInstance("", "确定删除?", "确定", "取消", -1, false, new FragmentDialog.OnClickBottomListener() {
                            @Override
                            public void onPositiveClick(Dialog dialog) {
                                deleteListner.onDelete(downloadInfo,position);
                                dialog.dismiss();
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        downloadManager.remove(downloadInfo);

                                    }
                                },1000);
                            }

                            @Override
                            public void onNegtiveClick(Dialog dialog) {
                                dialog.dismiss();
                            }
                        }).show(activity.getSupportFragmentManager(),"");
                    }
                });

                holder.downloadItemLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                });
                MediaInfoLocal myDownloadInfoById = dbController.findMyDownloadInfoById(downloadInfo.getUri().hashCode());
                if (myDownloadInfoById.getType().equals(Constants.PHOTO_DOWNLOAD)) {
                    holder.photoImg.setVisibility(View.VISIBLE);
                    holder.videoImg.setVisibility(View.GONE);
                    holder.video.setVisibility(View.GONE);
                    Glide.with(activity).load(myDownloadInfoById.getUrl()).thumbnail(0.1f).placeholder(R.drawable.xiangjiao).into(holder.photoImg);//加载网络图片
                } else {
                    holder.video.setVisibility(View.VISIBLE);
                    holder.photoImg.setVisibility(View.GONE);
                    holder.videoImg.setVisibility(View.VISIBLE);
                    Glide.with(activity).load(myDownloadInfoById.getIcon()).into(holder.videoImg);//加载网络图片

                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }



    class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView delete;
        private ImageView video;
        private ImageView videoImg;
        private ImageView photoImg;
        private FrameLayout downloadItemLayout;

        public MyViewHolder(View view) {
            super(view);
            photoImg = (ImageView) view.findViewById(R.id.thumbImg);
            videoImg = (ImageView) view.findViewById(R.id.videoImg);
            video = (ImageView) view.findViewById(R.id.video);
            delete = (TextView) view.findViewById(R.id.delete);
            downloadItemLayout = (FrameLayout) view.findViewById(R.id.download_item_layout);

        }
    }
}
