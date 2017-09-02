package com.act.quzhibo.adapter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.act.quzhibo.R;
import com.act.quzhibo.download.domain.MediaInfo;
import com.act.quzhibo.ui.activity.XImageActivity;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class MediaListAdapter extends RecyclerView.Adapter<MediaListAdapter.MediaListViewHolder> {

    private Activity activity;
    private ArrayList<MediaInfo> mMediaInfos = new ArrayList<>();

    public MediaListAdapter(Activity activity, ArrayList<MediaInfo> coursePreviewInfos) {
        this.activity = activity;
        update(coursePreviewInfos);

    }

    public void update(ArrayList<MediaInfo> coursePreviewInfos) {
        if (coursePreviewInfos == null) {
            return;
        }
        mMediaInfos = coursePreviewInfos;
        notifyDataSetChanged();
    }

    @Override
    public MediaListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (parent == null) {
            return null;
        }

        View itemView = View.inflate(parent.getContext(), R.layout.item_media, null);

        MediaListViewHolder holder = new MediaListViewHolder(itemView);

        return holder;
    }

    @Override
    public void onBindViewHolder(MediaListViewHolder holder, final int position) {

        if (holder == null) {
            return;
        }

        if (position >= mMediaInfos.size()) {
            return;
        }

        final MediaInfo mediaInfo = mMediaInfos.get(position);
        if (mediaInfo == null) {
            return;
        }

        if (TextUtils.isEmpty(mediaInfo.getIcon())) {
            holder.mIvMediaCover.setImageResource(R.drawable.ic_launcher);
        } else {
            Glide.with(activity).load(mediaInfo.getIcon()).placeholder(R.drawable.xiangjiao).into(holder.mIvMediaCover);//加载网络图片
        }
        holder.mTvMediaName.setText(mediaInfo.getName());


        holder.itemView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("position", position);
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("mediaList", mMediaInfos);//
                intent.putExtras(bundle);
                intent.setClass(activity, XImageActivity.class);
                activity.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mMediaInfos.size();
    }

    public void setData(ArrayList<MediaInfo> mediaInfos) {
        this.mMediaInfos = mediaInfos;
    }

    public static class MediaListViewHolder extends RecyclerView.ViewHolder {

        private ImageView mIvMediaCover;
        private TextView mTvMediaName;

        public MediaListViewHolder(View itemView) {
            super(itemView);
            mIvMediaCover = (ImageView) itemView.findViewById(R.id.ivMediaCover);
            mTvMediaName = (TextView) itemView.findViewById(R.id.tvMediaName);
        }
    }
}
  