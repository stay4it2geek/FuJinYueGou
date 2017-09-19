package com.act.quzhibo.adapter;


import android.app.Activity;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.act.quzhibo.R;
import com.act.quzhibo.common.Constants;
import com.act.quzhibo.entity.MoneyCourse;
import com.bumptech.glide.Glide;

import java.util.ArrayList;


public class MoneyCourseAdapter extends RecyclerView.Adapter<MoneyCourseAdapter.MyViewHolder> {
    private Activity activity;
    private ArrayList<MoneyCourse> courses;

    public MoneyCourseAdapter(Activity context, ArrayList<MoneyCourse> courses) {
        activity = context;
        this.courses = courses;
    }
    public interface OnRecyclerViewItemClickListener {
        void onItemClick(MoneyCourse course);
    }

    private OnRecyclerViewItemClickListener mOnItemClickListener = null;

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.course_item, parent, false);//这个布局就是一个imageview用来显示图片
        MyViewHolder holder = new MoneyCourseAdapter.MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final MoneyCourse course = courses.get(position);
        holder.courseName.setText(course.courseName);
        holder.courseDetail.setText(Html.fromHtml(course.courseDetail));
        Glide.with(activity).load(course.courseImage.getUrl()).into(holder.courseImage);
        holder.courseLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemClickListener.onItemClick(course);
            }
        });
        if (course.courseTag.equals(Constants.VIDEO_COURSE)) {
            holder.courseTag.setText("付费");
            holder.courseAppPrice.setVisibility(View.VISIBLE);
            holder.courseMarketPrice.setVisibility(View.VISIBLE);
            holder.courseAppPrice.setText("¥" + course.courseAppPrice);
            holder.courseMarketPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);  //
        } else {
            holder.courseTag.setText("免费");
        }
    }

    @Override
    public int getItemCount() {
        return courses.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView courseName;
        private TextView courseAppPrice;
        private TextView courseMarketPrice;
        private ImageView courseImage;
        private LinearLayout courseLayout;
        private TextView courseDetail;
        private TextView courseTag;

        public MyViewHolder(View view) {
            super(view);
            courseName = (TextView) view.findViewById(R.id.courseName);
            courseDetail = (TextView) view.findViewById(R.id.courseDetail);
            courseTag = (TextView) view.findViewById(R.id.courseTag);
            courseName = (TextView) view.findViewById(R.id.courseName);
            courseAppPrice = (TextView) view.findViewById(R.id.courseAppPrice);
            courseMarketPrice = (TextView) view.findViewById(R.id.courseMarketPrice);
            courseImage = (ImageView) view.findViewById(R.id.courseImage);
            courseLayout = (LinearLayout) view.findViewById(R.id.courseLayout);
        }
    }
}