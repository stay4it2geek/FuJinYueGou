package com.act.quzhibo.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.act.quzhibo.R;
import com.act.quzhibo.entity.NearPerson;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.util.List;
import java.util.Random;


/**
 * Created by asus-pc on 2017/5/31.
 */

public class NearAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Activity activity;
    private List<NearPerson> datas;//数据

    //适配器初始化
    public NearAdapter(Activity context, List<NearPerson> datas) {
        activity = context;
        this.datas = datas;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.near_list_item, parent, false);//这个布局就是一个imageview用来显示图片
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof MyViewHolder) {
            final NearPerson user = datas.get(position);
            Display display = activity.getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            int screenWidth = size.x;
            ((MyViewHolder) holder).photoImg.setLayoutParams(new FrameLayout.LayoutParams(size.x-20, size.x-20));
            ((MyViewHolder) holder).nickName.setText(user.username);
            ((MyViewHolder) holder).introduce.setText(user.introduce);
            ((MyViewHolder) holder).near_layout.setOnClickListener(new View.OnClickListener() {
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
            //TODO 定位时使用随机数
             ((MyViewHolder) holder).arealocation.setText("距离你" + user.distance+ "千米");
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
        private TextView introduce;
        private FrameLayout near_layout;

        public MyViewHolder(View view) {
            super(view);
            photoImg = (ImageView) view.findViewById(R.id.photoImg);
            nickName = (TextView) view.findViewById(R.id.nickName);
            arealocation = (TextView) view.findViewById(R.id.arealocation);
            near_layout = (FrameLayout) view.findViewById(R.id.near_layout);
            introduce = (TextView) view.findViewById(R.id.introduce);

        }
    }

}