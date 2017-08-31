package com.act.quzhibo.adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.act.quzhibo.R;
import com.act.quzhibo.download.activity.ListActivity;
import com.act.quzhibo.download.domain.MediaInfo;
import com.act.quzhibo.util.ToastUtil;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class MediaListAdapter  extends RecyclerView.Adapter<MediaListAdapter.MediaListViewHolder> {

    private Activity activity;
    private List<MediaInfo> mMediaInfos = new ArrayList<>();

    public MediaListAdapter(Activity activity, List<MediaInfo> coursePreviewInfos) {
        this.activity = activity;
        update(coursePreviewInfos);
    }

    public void update(List<MediaInfo> coursePreviewInfos) {
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
    public void onBindViewHolder(MediaListViewHolder holder, int position) {

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
            holder.mIvCourseCover.setImageResource(R.drawable.ic_launcher);
        } else {
            Glide.with(activity).load(mediaInfo.getIcon()).placeholder(R.drawable.xiangjiao).into(holder.mIvCourseCover);//加载网络图片
        }
        holder.mTvCourseName.setText(mediaInfo.getName());

        holder.itemView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
              activity.startActivity(new Intent(activity, ListActivity.class));
            }
        });
    }

    @Override
    public int getItemCount() {
        return mMediaInfos.size();
    }

    public void setData(ArrayList<MediaInfo> mediaInfos) {
        this.mMediaInfos=mediaInfos;
    }

    public static class MediaListViewHolder extends RecyclerView.ViewHolder {

        private ImageView mIvDownloadCourse;
        private ImageView mIvCourseCover;
        private TextView mTvCourseName;

        public MediaListViewHolder(View itemView) {
            super(itemView);
            mIvCourseCover = (ImageView) itemView.findViewById(R.id.ivCourseCover);
            mTvCourseName = (TextView) itemView.findViewById(R.id.tvCourseName);
        }
    }
}
  