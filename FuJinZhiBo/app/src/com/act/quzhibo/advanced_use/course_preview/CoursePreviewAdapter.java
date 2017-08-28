package com.act.quzhibo.advanced_use.course_preview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.act.quzhibo.R;
import com.act.quzhibo.advanced_use.model.CoursePreviewInfo;
import com.act.quzhibo.util.ToastUtil;
import com.bumptech.glide.Glide;

import org.wlf.filedownloader.FileDownloader;

import java.util.ArrayList;
import java.util.List;


public class CoursePreviewAdapter extends RecyclerView.Adapter<CoursePreviewAdapter.CoursePreviewViewHolder> {

    private List<CoursePreviewInfo> mCoursePreviewInfos = new ArrayList<CoursePreviewInfo>();
    private Context context;

    public CoursePreviewAdapter(Context context, List<CoursePreviewInfo> coursePreviewInfos) {

        update(context,coursePreviewInfos);
    }

    public void update(Context context,List<CoursePreviewInfo> coursePreviewInfos) {
        if (coursePreviewInfos == null) {
            return;
        }
        this.context=context;
        mCoursePreviewInfos = coursePreviewInfos;
        notifyDataSetChanged();
    }

    @Override
    public CoursePreviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (parent == null) {
            return null;
        }

        View itemView = View.inflate(parent.getContext(), R.layout.item_course_preview, null);

        CoursePreviewViewHolder holder = new CoursePreviewViewHolder(itemView);

        return holder;
    }

    @Override
    public void onBindViewHolder(CoursePreviewViewHolder holder, int position) {

        if (holder == null) {
            return;
        }

        if (position >= mCoursePreviewInfos.size()) {
            return;
        }

        final CoursePreviewInfo coursePreviewInfo = mCoursePreviewInfos.get(position);
        if (coursePreviewInfo == null) {
            return;
        }

        // course cover
        if (TextUtils.isEmpty(coursePreviewInfo.getCourseCoverUrl())) {
            holder.mIvCourseCover.setImageResource(R.drawable.ic_launcher);
        } else {
            Glide.with(context).load(coursePreviewInfo.getCourseCoverUrl()).placeholder(R.drawable.xiangjiao).into(holder.mIvCourseCover);//加载网络图片

        }
        // course name
        holder.mTvCourseName.setText(coursePreviewInfo.getCourseName());

        holder.mIvDownloadCourse.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // download course
                if (TextUtils.isEmpty(coursePreviewInfo.getCourseUrl())) {
                    ToastUtil.showToast(v.getContext(), v.getContext().getString(R.string
                            .course_preview_url_is_empty_note));
                    return;
                }

                ToastUtil.showToast(v.getContext(), v.getContext().getString(R.string
                        .course_preview_add_download) + coursePreviewInfo.getCourseName());
                // use FileDownloader to download
                FileDownloader.start(coursePreviewInfo.getCourseUrl());
            }
        });

        holder.itemView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // go to play course
                ToastUtil.showToast(v.getContext(), "watch " + coursePreviewInfo.getCourseName());
            }
        });
    }

    @Override
    public int getItemCount() {
        return mCoursePreviewInfos.size();
    }

    public static class CoursePreviewViewHolder extends RecyclerView.ViewHolder {

        private ImageView mIvDownloadCourse;
        private ImageView mIvCourseCover;
        private TextView mTvCourseName;

        public CoursePreviewViewHolder(View itemView) {
            super(itemView);

            mIvDownloadCourse = (ImageView) itemView.findViewById(R.id.ivDownloadCourse);
            mIvCourseCover = (ImageView) itemView.findViewById(R.id.ivCourseCover);
            mTvCourseName = (TextView) itemView.findViewById(R.id.tvCourseName);
        }
    }

    public void release() {
        for (CoursePreviewInfo coursePreviewInfo : mCoursePreviewInfos) {
            if (coursePreviewInfo == null) {
                continue;
            }
            coursePreviewInfo.release();
        }
    }
}
