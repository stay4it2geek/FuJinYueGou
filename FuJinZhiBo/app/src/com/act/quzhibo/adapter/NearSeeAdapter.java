package com.act.quzhibo.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.act.quzhibo.ProvinceAndCityEntify;
import com.act.quzhibo.R;
import com.act.quzhibo.common.Constants;
import com.act.quzhibo.entity.CommonSeePerson;
import com.act.quzhibo.entity.NearSeePerson;
import com.act.quzhibo.ui.activity.UserInfoActivity;
import com.act.quzhibo.util.CommonUtil;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


/**
 * Created by asus-pc on 2017/5/31.
 */

public class NearSeeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Activity activity;
    private List<NearSeePerson> datas;//数据

    //适配器初始化
    public NearSeeAdapter(Activity context, List<NearSeePerson> datas) {
        activity = context;
        this.datas = datas;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.who_see_me_list_item, parent, false);//这个布局就是一个imageview用来显示图片
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof MyViewHolder) {
            final NearSeePerson user = datas.get(position);
            ((MyViewHolder) holder).nickName.setText(user.username);
            ((MyViewHolder) holder).disMariState.setText(user.disMariState);
            ((MyViewHolder) holder).introduce.setText(user.introduce);
            ((MyViewHolder) holder).introduce.setVisibility(View.VISIBLE);
            ((MyViewHolder) holder).line1.setVisibility(View.VISIBLE);
            ((MyViewHolder) holder).line2.setVisibility(View.GONE);

            long l = System.currentTimeMillis() - (position * 100 + 100);
            long day = l / (24 * 60 * 60 * 1000);
            long hour = (l / (60 * 60 * 1000) - day * 24);
            long min = ((l / (60 * 1000)) - day * 24 * 60 - hour * 60);
            if (!datas.get(position).sex.equals("2")) {
                ((MyViewHolder) holder).sexAndAge.setBackgroundColor(activity.getResources().getColor(R.color.blue));
            }
            ((MyViewHolder) holder).sexAndAge.setText(datas.get(position).sex.equals("2") ? "女" : "男");
            ((MyViewHolder) holder).createTime.setText(hour + "小时" + min + "分钟前");

            ((MyViewHolder) holder).who_see_me_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();

                }
            });
            if (user.sex.equals("2")) {
                Glide.with(activity).load(user.absCoverPic).asBitmap().placeholder(R.drawable.women).into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        ((MyViewHolder) holder).photoImg.setBackgroundDrawable(new BitmapDrawable(resource));
                    }

                    @Override
                    public void onLoadStarted(Drawable placeholder) {
                        super.onLoadStarted(placeholder);
                    }
                });

            } else {
                Glide.with(activity).load(user.absCoverPic).asBitmap().placeholder(R.drawable.man).into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        ((MyViewHolder) holder).photoImg.setBackgroundDrawable(new BitmapDrawable(resource));
                    }

                    @Override
                    public void onLoadStarted(Drawable placeholder) {
                        super.onLoadStarted(placeholder);
                    }
                });
            }
            ((MyViewHolder) holder).arealocation.setText("距离你" + user.distance + "千米");

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
        private TextView introduce;
        private RelativeLayout who_see_me_layout;
        private View line2;
        private View line1;
        public MyViewHolder(View view) {
            super(view);
            photoImg = (ImageView) view.findViewById(R.id.photoImg);
            nickName = (TextView) view.findViewById(R.id.nick);
            createTime = (TextView) view.findViewById(R.id.createTime);
            arealocation = (TextView) view.findViewById(R.id.arealocation);
            sexAndAge = (TextView) view.findViewById(R.id.sexAndAge);
            disMariState = (TextView) view.findViewById(R.id.disMariState);
            who_see_me_layout = (RelativeLayout) view.findViewById(R.id.who_see_me_layout);
            line2 = (View) view.findViewById(R.id.line2);
            line1 = (View) view.findViewById(R.id.line1);
            introduce = (TextView) view.findViewById(R.id.introduce);

        }
    }

}