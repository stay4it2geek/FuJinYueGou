package com.act.quzhibo.advanced_use.media_preview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.act.quzhibo.R;
import com.act.quzhibo.adapter.MediaAuthorListAdapter;
import com.act.quzhibo.advanced_use.model.MediaModel;
import com.act.quzhibo.entity.MediaAuthor;
import com.act.quzhibo.util.ToastUtil;
import com.bumptech.glide.Glide;

import org.wlf.filedownloader.FileDownloader;

import java.util.ArrayList;
import java.util.List;


public class MediaPreviewAdapter extends RecyclerView.Adapter<MediaPreviewAdapter.mediaPreviewViewHolder> {

    private List<MediaModel> mMediaPreviewInfos = new ArrayList<>();
    private Context context;

    public MediaPreviewAdapter(Context context, List<MediaModel> mediaPreviewInfos) {

        update(context,mediaPreviewInfos);
    }
    public interface OnMediaModelRecyclerViewItemClickListener {
        void onItemClick(MediaModel MediaModel);
    }

    private OnMediaModelRecyclerViewItemClickListener mOnItemClickListener = null;

    public void setOnItemClickListener(OnMediaModelRecyclerViewItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    public void update(Context context,List<MediaModel> mediaPreviewInfos) {
        if (mediaPreviewInfos == null) {
            return;
        }
        this.context=context;
        mMediaPreviewInfos = mediaPreviewInfos;
        notifyDataSetChanged();
    }

    @Override
    public mediaPreviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (parent == null) {
            return null;
        }

        View itemView = View.inflate(parent.getContext(), R.layout.item_media_preview, null);

        mediaPreviewViewHolder holder = new mediaPreviewViewHolder(itemView);

        return holder;
    }

    @Override
    public void onBindViewHolder(mediaPreviewViewHolder holder, int position) {

        if (holder == null) {
            return;
        }

        if (position >= mMediaPreviewInfos.size()) {
            return;
        }

        final MediaModel mediaPreviewInfo = mMediaPreviewInfos.get(position);
        if (mediaPreviewInfo == null) {
            return;
        }

        // media cover
        if (TextUtils.isEmpty(mediaPreviewInfo.getMediaCoverUrl())) {
            holder.mIvMediaCover.setImageResource(R.drawable.ic_launcher);
        } else {
            Glide.with(context).load(mediaPreviewInfo.getMediaCoverUrl()).placeholder(R.drawable.xiangjiao).into(holder.mIvMediaCover);//加载网络图片

        }
        // media name
        holder.mTvmediaName.setText(mediaPreviewInfo.getMediaName());

        holder.mIvDownloadmedia.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // download media
                if (TextUtils.isEmpty(mediaPreviewInfo.getMediaUrl())) {
                    ToastUtil.showToast(v.getContext(), v.getContext().getString(R.string
                            .media_preview_url_is_empty_note));
                    return;
                }

                ToastUtil.showToast(v.getContext(), v.getContext().getString(R.string
                        .media_preview_add_download) + mediaPreviewInfo.getMediaName());
                // use FileDownloader to download
                FileDownloader.start(mediaPreviewInfo.getMediaUrl());
            }
        });

        holder.itemView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // go to play media
                ToastUtil.showToast(v.getContext(), "watch " + mediaPreviewInfo.getMediaName());
            }
        });
    }

    @Override
    public int getItemCount() {
        return mMediaPreviewInfos.size();
    }

    public static class mediaPreviewViewHolder extends RecyclerView.ViewHolder {

        private ImageView mIvDownloadmedia;
        private ImageView mIvMediaCover;
        private TextView mTvmediaName;

        public mediaPreviewViewHolder(View itemView) {
            super(itemView);

            mIvDownloadmedia = (ImageView) itemView.findViewById(R.id.ivMediaDownload);
            mIvMediaCover = (ImageView) itemView.findViewById(R.id.ivMediaCover);
            mTvmediaName = (TextView) itemView.findViewById(R.id.tvMediaName);
        }
    }

    public void release() {
        for (MediaModel mediaPreviewInfo : mMediaPreviewInfos) {
            if (mediaPreviewInfo == null) {
                continue;
            }
            mediaPreviewInfo.release();
        }
    }
}
