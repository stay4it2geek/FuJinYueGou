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
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.util.ArrayList;

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
        long l = System.currentTimeMillis() - (position * 100 + 100);
        long day = l / (24 * 60 * 60 * 1000);
        long hour = (l / (60 * 60 * 1000) - day * 24);
        long min = ((l / (60 * 1000)) - day * 24 * 60 - hour * 60);
        if (!datas.get(position).sex.equals("2")) {
            holder.sexAndAge.setBackgroundColor(activity.getResources().getColor(R.color.blue));
        }
        holder.sexAndAge.setText(datas.get(position).sex.equals("2") ? "女" : "男");
        holder.createTime.setText(hour + "时" + min + "分钟前看过你");
        holder.arealocation.setText("  距离你" + datas.get(position).distance + "公里");

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

            Glide.with(activity).load(photoUrl).asBitmap().placeholder(R.drawable.women).into(new SimpleTarget<Bitmap>() {
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
            Glide.with(activity).load(photoUrl).asBitmap().placeholder(R.drawable.man).into(new SimpleTarget<Bitmap>() {
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