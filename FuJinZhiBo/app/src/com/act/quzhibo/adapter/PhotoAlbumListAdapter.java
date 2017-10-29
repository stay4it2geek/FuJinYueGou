package com.act.quzhibo.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.act.quzhibo.R;
import com.act.quzhibo.download.bean.MediaInfo;
import com.act.quzhibo.ui.activity.BGAPhotoPreviewActivity;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class PhotoAlbumListAdapter extends RecyclerView.Adapter<PhotoAlbumListAdapter.MediaListViewHolder> {

    private Activity activity;
    private ArrayList<MediaInfo> mMediaInfos = new ArrayList<>();

    public PhotoAlbumListAdapter(Activity activity, ArrayList<MediaInfo> coursePreviewInfos) {
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

        Glide.with(activity).load(mediaInfo.getIcon()).placeholder(R.drawable.placehoder_img).error(R.drawable.error_img).into(holder.mIvMediaCover);//加载网络图片

        holder.mTvMediaTtile.setText(mediaInfo.getTitle());

        holder.itemView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMediaInfos.size() > 0) {
                    activity.startActivity(BGAPhotoPreviewActivity.newIntent(activity, mMediaInfos, position,true));
                }
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
        private TextView mTvMediaTtile;

        public MediaListViewHolder(View itemView) {
            super(itemView);
            mIvMediaCover = (ImageView) itemView.findViewById(R.id.ivMediaCover);
            mTvMediaTtile = (TextView) itemView.findViewById(R.id.tvMediaTtile);
        }
    }
}
  