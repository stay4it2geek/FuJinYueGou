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
import com.act.quzhibo.bean.CommonCourse;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

import cn.bmob.v3.b.V;

public class CommonCoursesAdapter extends RecyclerView.Adapter<CommonCoursesAdapter.MyViewHolder> {
    private String courseUiType;
    private Activity activity;
    private ArrayList<CommonCourse> courses;

    public CommonCoursesAdapter(Activity context, ArrayList<CommonCourse> courses,String courseUiType) {
        activity = context;
        this.courses = courses;
        this.courseUiType = courseUiType;

    }
    public interface OnRecyclerViewItemClickListener {
        void onItemClick(CommonCourse course);
    }

    private OnRecyclerViewItemClickListener mOnItemClickListener = null;

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        mOnItemClickListener = listener;
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.course_item, parent, false);
        MyViewHolder holder = new CommonCoursesAdapter.MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final CommonCourse course = courses.get(position);
        holder.courseName.setText(course.courseName);
        if( courseUiType.equals("money")){
            holder.courseDetail.setVisibility(View.VISIBLE);
            holder.leanerCount.setVisibility(View.GONE);
            holder.selectionNum.setVisibility(View.GONE);
            holder.courseDetail.setText(Html.fromHtml(course.courseDetail));
        }else {//pua
            holder.courseTag.setVisibility(View.VISIBLE);
            if (course.courseTag.equals(Constants.VIDEO_COURSE)) {
                holder.courseTag.setText("视频");
            } else {
                holder.courseTag.setText("课程");
            }
            holder.leanerCount.setText(course.leanerCount + "个付费学员");
            holder.selectionNum.setText("总共"+course.selectionNum + "章节");
        }

        holder.courseAppPrice.setText("¥" + course.courseAppPrice);
        holder.courseMarketPrice.setText("¥" + course.courseMarketPrice);
        holder.courseMarketPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);  // 设置中划线并加清晰
        Glide.with(activity).load(course.courseImage.getUrl()).placeholder(R.drawable.placehoder_img).into(holder.courseImage);
        holder.courseLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemClickListener.onItemClick(course);
            }
        });
        holder.needPay.setVisibility(View.VISIBLE);
        if(course.needPay){
            holder.needPay.setText("付费");
        } else {
            holder.needPay.setText(course.freePromotion);
            holder.courseAppPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);  // 设置中划线并加清晰
        }


    }


    @Override
    public int getItemCount() {
        return courses == null ? 0 : courses.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView needPay;
        private TextView courseName;
        private TextView leanerCount;
        private TextView selectionNum;
        private TextView courseMarketPrice;
        private TextView courseTag;
        private TextView courseAppPrice;
        private ImageView courseImage;
        private LinearLayout courseLayout;
        private TextView courseDetail;

        public MyViewHolder(View view) {
            super(view);
            needPay = (TextView) view.findViewById(R.id.needPay);
            courseTag = (TextView) view.findViewById(R.id.courseTag);
            courseName = (TextView) view.findViewById(R.id.courseName);
            leanerCount = (TextView) view.findViewById(R.id.leanerCount);
            selectionNum = (TextView) view.findViewById(R.id.selectionNum);
            courseAppPrice = (TextView) view.findViewById(R.id.courseAppPrice);
            courseMarketPrice = (TextView) view.findViewById(R.id.courseMarketPrice);
            courseImage = (ImageView) view.findViewById(R.id.courseImage);
            courseLayout = (LinearLayout) view.findViewById(R.id.courseLayout);
            courseDetail = (TextView) view.findViewById(R.id.courseDetail);
        }
    }
}