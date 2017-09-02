package com.act.quzhibo.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.act.quzhibo.MyStandardVideoController;
import com.act.quzhibo.R;
import com.act.quzhibo.entity.VideoBean;
import com.act.quzhibo.ui.activity.FullScreenActivity;
import com.bumptech.glide.Glide;
import com.devlin_n.videoplayer.controller.StandardVideoController;
import com.devlin_n.videoplayer.player.IjkVideoView;


import org.greenrobot.eventbus.EventBus;

import java.util.List;

public class VideoRecyclerViewAdapter extends RecyclerView.Adapter<VideoRecyclerViewAdapter.VideoHolder> {


    private List<VideoBean> videos;
    private Context context;

    public VideoRecyclerViewAdapter(List<VideoBean> videos, Context context) {
        this.videos = videos;
        this.context = context;
    }

    @Override
    public VideoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_video, parent, false);
        return new VideoHolder(itemView);

    }

    @Override
    public void onBindViewHolder(final VideoHolder holder, int position) {

        final VideoBean videoBean = videos.get(position);
        Glide.with(context)
                .load(videoBean.getThumb())
                .crossFade()
                .placeholder(android.R.color.darker_gray)
                .into(holder.controller.getThumb());
        holder.ijkVideoView
                .enableCache()
                .addToPlayerManager()
                .setUrl(videoBean.getUrl())
                .setTitle(videoBean.getTitle())
                .setVideoController(holder.controller);
        holder.ijkVideoView.setTag(position);
        holder.controller.setOnStartFullScreenListner(new MyStandardVideoController.OnStartFullScreenListner() {
            @Override
            public void onStartFullScreen() {
                Intent intent = new Intent();
                intent.putExtra("videoUrl", videoBean.getUrl());
                intent.putExtra("videoTitle", videoBean.getTitle());
                intent.setClass(context, FullScreenActivity.class);
                context.startActivity(intent);
            }
        });

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
            ijkVideoView.setVideoController(controller);
        }
    }
}