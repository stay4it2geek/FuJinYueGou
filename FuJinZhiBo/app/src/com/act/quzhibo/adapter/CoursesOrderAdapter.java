package com.act.quzhibo.adapter;


import android.app.Activity;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.act.quzhibo.R;
import com.act.quzhibo.bean.CommonCourse;
import com.act.quzhibo.bean.Orders;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;

public class CoursesOrderAdapter extends RecyclerView.Adapter<CoursesOrderAdapter.MyViewHolder> {
    Activity activity;
    ArrayList<Orders> list;
    boolean isTeamType;
    private OnCourseActionListener mListener = null;

    public void setOrderDatas(ArrayList<Orders> list) {
        this.list = list;
    }

    public interface OnCourseActionListener {
        void onDelete(CommonCourse course);

        void onCopyUrl(CommonCourse course);

        void onCopyPsw(CommonCourse course);

        void onSave2File(CommonCourse course);
    }

    public void setListener(OnCourseActionListener listener) {
        mListener = listener;
    }

    public CoursesOrderAdapter(boolean isTeamType, Activity context) {
        activity = context;
        this.isTeamType = isTeamType;
        this.isTeamType = isTeamType;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.course_order_item, parent, false);
        MyViewHolder holder = new CoursesOrderAdapter.MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        BmobQuery<CommonCourse> query = new BmobQuery<>();
        query.getObject(list.get(position).course.getObjectId(), new QueryListener<CommonCourse>() {
            @Override
            public void done(final CommonCourse course, BmobException e) {
                if(e==null){
                    if (course == null) {
                        return;
                    }
                    holder.courseName.setText(course.courseName);
                    holder.courseAppPrice.setText("¥" + course.courseAppPrice);
                    holder.courseMarketPrice.setText("¥" + course.courseMarketPrice);
                    holder.courseMarketPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);  // 设置中划线并加清晰
                    Glide.with(activity).load(course.courseImage.getFileUrl()).placeholder(R.drawable.placehoder_img).into(holder.courseImage);
                    holder.deleteOrder.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mListener.onDelete(course);
                        }
                    });
                    holder.save2File.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mListener.onSave2File(course);
                        }
                    });
                    holder.copypsw.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mListener.onCopyPsw(course);
                        }
                    });
                    holder.copyurl.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mListener.onCopyUrl(course);
                        }
                    });
                }
            }
        });

        if (isTeamType) {
            holder.rl_bottom.setVisibility(View.GONE);
            holder.downloadUrl.setVisibility(View.GONE);
            holder.downloadPsw.setVisibility(View.GONE);
        } else {
            holder.rl_bottom.setVisibility(View.VISIBLE);
            holder.promotionPercent.setVisibility(View.GONE);
            holder.promotionMoney.setVisibility(View.GONE);
        }

    }


    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView courseName;
        TextView downloadUrl;
        TextView downloadPsw;
        TextView courseMarketPrice;
        TextView promotionPercent;
        TextView courseAppPrice;
        ImageView courseImage;
        RelativeLayout rl_bottom;
        LinearLayout courseLayout;
        TextView deleteOrder;
        TextView copyurl;
        TextView copypsw;
        TextView save2File;
        TextView promotionMoney;

        public MyViewHolder(View view) {
            super(view);
            downloadUrl = (TextView) view.findViewById(R.id.downloadUrl);
            downloadPsw = (TextView) view.findViewById(R.id.downloadPsw);
            courseName = (TextView) view.findViewById(R.id.courseName);
            promotionPercent = (TextView) view.findViewById(R.id.promotionPercent);
            promotionMoney = (TextView) view.findViewById(R.id.promotionMoney);
            courseAppPrice = (TextView) view.findViewById(R.id.courseAppPrice);
            courseMarketPrice = (TextView) view.findViewById(R.id.courseMarketPrice);
            courseImage = (ImageView) view.findViewById(R.id.courseImage);
            courseLayout = (LinearLayout) view.findViewById(R.id.courseLayout);
            copyurl = (TextView) view.findViewById(R.id.copyurl);
            copypsw = (TextView) view.findViewById(R.id.copypsw);
            deleteOrder = (TextView) view.findViewById(R.id.deleteOrder);
            rl_bottom = (RelativeLayout) view.findViewById(R.id.rl_bottom);
            save2File = (TextView) view.findViewById(R.id.save2File);

        }
    }
}