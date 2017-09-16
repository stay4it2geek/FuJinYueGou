package com.act.quzhibo.adapter;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.act.quzhibo.R;
import com.act.quzhibo.common.Constants;
import com.act.quzhibo.entity.PuaCourses;
import com.act.quzhibo.ui.activity.InfoInterestPersonActivity;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class CoursesAdapter extends RecyclerView.Adapter<CoursesAdapter.MyViewHolder> {
    private Activity activity;
    private ArrayList<PuaCourses> courses;

    public CoursesAdapter(Activity context, ArrayList<PuaCourses> courses) {
        activity = context;
        this.courses = courses;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.course_item, parent, false);//这个布局就是一个imageview用来显示图片
        MyViewHolder holder = new CoursesAdapter.MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final PuaCourses course = courses.get(position);
        holder.courseName.setText(course.courseName);
        holder.leanerCount.setText(course.leanerCount + "个付费学员");
        holder.selectionNum.setText("总共"+course.selectionNum + "章节");
        holder.courseAppPrice.setText("¥" + course.courseAppPrice);
        holder.courseMarketPrice.setText("¥" + course.courseMarketPrice);
        holder.courseMarketPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);  // 设置中划线并加清晰
        Glide.with(activity).load(course.courseImage.getUrl()).into(holder.courseImage);
        holder.courseLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra(Constants.COURSE, courses.get(position));
//                intent.setClass(activity, InfoInterestPersonActivity.class);
//                activity.startActivity(intent);
            }
        });
        if (course.courseTag.equals(Constants.VIDEO_COURSE)) {
            holder.courseTag.setText("视频");
        } else {
            holder.courseTag.setText("课程");

        }

    }


    @Override
    public int getItemCount() {
        return courses.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView courseName;
        private TextView leanerCount;
        private TextView selectionNum;
        private TextView courseMarketPrice;
        private TextView courseTag;
        private TextView courseAppPrice;
        private ImageView courseImage;
        private LinearLayout courseLayout;

        public MyViewHolder(View view) {
            super(view);
            courseTag = (TextView) view.findViewById(R.id.courseTag);
            courseName = (TextView) view.findViewById(R.id.courseName);
            leanerCount = (TextView) view.findViewById(R.id.leanerCount);
            selectionNum = (TextView) view.findViewById(R.id.selectionNum);
            courseAppPrice = (TextView) view.findViewById(R.id.courseAppPrice);
            courseMarketPrice = (TextView) view.findViewById(R.id.courseMarketPrice);
            courseImage = (ImageView) view.findViewById(R.id.courseImage);
            courseLayout = (LinearLayout) view.findViewById(R.id.courseLayout);
        }
    }
}