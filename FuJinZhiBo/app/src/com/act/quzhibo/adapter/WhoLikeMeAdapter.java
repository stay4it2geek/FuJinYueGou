package com.act.quzhibo.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.act.quzhibo.R;
import com.act.quzhibo.common.Constants;
import com.act.quzhibo.entity.InterestSubPerson;
import com.act.quzhibo.ui.activity.InfonNearPersonActivity;
import com.act.quzhibo.ui.activity.IntersetPersonPostListActivity;
import com.act.quzhibo.util.CommonUtil;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.util.ArrayList;
import java.util.Random;

public class WhoLikeMeAdapter extends RecyclerView.Adapter<WhoLikeMeAdapter.MyViewHolder> {
    private Activity activity;
    private ArrayList<InterestSubPerson> datas;

    public WhoLikeMeAdapter(Activity context, ArrayList<InterestSubPerson> datas) {
        activity = context;
        this.datas = datas;
    }

    @Override
    public WhoLikeMeAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.item_who_like_me, parent, false);//这个布局就是一个imageview用来显示图片
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final WhoLikeMeAdapter.MyViewHolder holder, final int position) {
        final InterestSubPerson user = datas.get(position);
        holder.nickName.setText(user.username);
        holder.disMariState.setText(user.disMariState);
        int minute ;
        if (CommonUtil.loadData(activity, "see_time") > 0) {
            if (Integer.parseInt(user.userId) != CommonUtil.loadData(activity, "userId")) {
                int max = 800;
                int min = 30;
                Random random = new Random();
                minute = random.nextInt(max) +  random.nextInt(min) ;
                CommonUtil.saveData(activity, minute, "see_time");
                CommonUtil.saveData(activity, Integer.parseInt(user.userId), "see_userId");
            } else {
                minute = CommonUtil.loadData(activity, "see_time");
            }
        } else {
            int max = 800;
            int min = 30;
            Random random = new Random();
            minute = random.nextInt(max) +  random.nextInt(min) ;
            CommonUtil.saveData(activity, minute, "see_time");
            CommonUtil.saveData(activity, Integer.parseInt(user.userId), "see_userId");
        }
        if (minute != 0) {
            if (minute % 60 == 0) {
                holder.createTime.setText(minute / 60 + "小时前看过你");
            } else {
                holder.createTime.setText((minute - (minute % 60)) / 60 + "小时" + minute % 60 + "分钟前看过你");
            }
        }
        holder.arealocation.setText("  距离你" + datas.get(position).distance + "公里");
        holder.sexAndAge.setText(datas.get(position).sex.equals("2") ? "女" : "男");
        holder.who_see_me_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                if (user.userType.equals(Constants.INTEREST)) {
                    intent.putExtra(Constants.COMMON_USER_ID, datas.get(position).userId);
                    intent.setClass(activity, IntersetPersonPostListActivity.class);
                } else {
                    intent.putExtra(Constants.NEAR_USER, datas.get(position));
                    intent.setClass(activity, InfonNearPersonActivity.class);
                }
                activity.startActivity(intent);
            }
        });
        String photoUrl = "";
        if (user.userType.equals(Constants.INTEREST)) {
            photoUrl = user.photoUrl;
        } else {
            photoUrl = user.headUrl;
        }
        if (user.sex.equals("2")) {

            Glide.with(activity).load(photoUrl).asBitmap().placeholder(R.drawable.women).error(R.drawable.error_img).into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                    holder.photoImg.setBackgroundDrawable(new BitmapDrawable(resource));
                }

                @Override
                public void onLoadStarted(Drawable placeholder) {
                    super.onLoadStarted(placeholder);
                    holder.photoImg.setBackgroundDrawable(placeholder);
                }
            });
        } else {
            Glide.with(activity).load(photoUrl).asBitmap().placeholder(R.drawable.man).error(R.drawable.error_img).into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                    holder.photoImg.setBackgroundDrawable(new BitmapDrawable(resource));
                }

                @Override
                public void onLoadStarted(Drawable placeholder) {
                    super.onLoadStarted(placeholder);
                    holder.photoImg.setBackgroundDrawable(placeholder);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView nickName;
        private ImageView photoImg;
        private TextView arealocation;
        private TextView createTime;
        private TextView sexAndAge;
        private TextView disMariState;
        private RelativeLayout who_see_me_layout;

        public MyViewHolder(View view) {
            super(view);
            photoImg = (ImageView) view.findViewById(R.id.photoImg);
            nickName = (TextView) view.findViewById(R.id.nick);
            createTime = (TextView) view.findViewById(R.id.createTime);
            arealocation = (TextView) view.findViewById(R.id.location);
            sexAndAge = (TextView) view.findViewById(R.id.sexAndAge);
            disMariState = (TextView) view.findViewById(R.id.disMariState);
            who_see_me_layout = (RelativeLayout) view.findViewById(R.id.who_see_me_layout);
            arealocation.setVisibility(View.VISIBLE);
        }
    }

}