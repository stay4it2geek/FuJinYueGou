package com.act.quzhibo.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
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

public class CommonSeeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Activity activity;
    private List<CommonSeePerson> datas;//数据

    //适配器初始化
    public CommonSeeAdapter(Activity context, List<CommonSeePerson> datas) {
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
            final CommonSeePerson user = datas.get(position);
            ((MyViewHolder) holder).nickName.setText(user.nick);
            ((MyViewHolder) holder).disMariState.setText(user.disMariState);
            long l = System.currentTimeMillis() - (position*100+100);
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
                        intent.putExtra(Constants.POST_USER_WHO_SEE_ME, datas.get(position));
                        intent.putExtra(Constants.LIST_FLAG,Constants.PAGE_FLAG);
                        intent.setClass(activity, UserInfoActivity.class);
                        activity.startActivity(intent);
                    }
                });
                if (user.sex.equals("2")) {
                    Glide.with(activity).load(user.photoUrl).asBitmap().placeholder(R.drawable.women).into(new SimpleTarget<Bitmap>() {
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
                    Glide.with(activity).load(user.photoUrl).asBitmap().placeholder(R.drawable.man).into(new SimpleTarget<Bitmap>() {
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
                new AsyncTask<Void, Void, String>() {
                    @Override
                    protected String doInBackground(Void... params) {
                        ArrayList<ProvinceAndCityEntify> data_ = CommonUtil.parseLocation(activity).data;
                        if (null != datas) {
                            for (ProvinceAndCityEntify entify : data_) {
                                if (TextUtils.equals(datas.get(position).proCode + "", entify.proId + "")) {
                                    for (ProvinceAndCityEntify.CitySub citySub : entify.citySub) {
                                        if (TextUtils.equals(datas.get(position).cityCode, citySub.cityId + "")) {
                                            return !TextUtils.equals("", entify.name + citySub.name + "") ? entify.name + citySub.name + "" : "----";
                                        }
                                    }
                                }
                            }
                        }
                        return "";
                    }

                    @Override
                    protected void onPostExecute(String text) {
                        super.onPostExecute(text);
                        ((MyViewHolder) holder).arealocation.setText(text);
                    }
                }.execute();
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
            arealocation = (TextView) view.findViewById(R.id.arealocation);
            sexAndAge = (TextView) view.findViewById(R.id.sexAndAge);
            disMariState = (TextView) view.findViewById(R.id.disMariState);
            who_see_me_layout = (RelativeLayout) view.findViewById(R.id.who_see_me_layout);

        }
    }

}