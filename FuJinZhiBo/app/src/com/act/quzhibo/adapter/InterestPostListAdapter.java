package com.act.quzhibo.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.act.quzhibo.common.MyApplicaition;
import com.act.quzhibo.entity.ProvinceAndCityEntity;
import com.act.quzhibo.R;
import com.act.quzhibo.common.Constants;
import com.act.quzhibo.entity.InterestParentPerson;
import com.act.quzhibo.entity.InterestPost;
import com.act.quzhibo.ui.activity.InfoInterestPersonActivity;
import com.act.quzhibo.util.CommonUtil;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InterestPostListAdapter extends RecyclerView.Adapter<InterestPostListAdapter.MyViewHolder> {
    private ArrayList<InterestPost> datas;
    private Activity activity;

    public interface OnInterestPostRecyclerViewItemClickListener {
        void onItemClick(InterestPost post);
    }

    private OnInterestPostRecyclerViewItemClickListener mOnItemClickListener = null;

    public void setOnItemClickListener(OnInterestPostRecyclerViewItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    public InterestPostListAdapter(Activity context, ArrayList<InterestPost> datas) {
        activity = context;
        this.datas = datas;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.interest_post_list_item, parent, false);//这个布局就是一个imageview用来显示图片
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final InterestParentPerson user = datas.get(position).user;
        final InterestPost post = datas.get(position);
        String nick = user.nick.replaceAll("\r|\n", "");
        holder.nickName.setText(nick);

        long l = System.currentTimeMillis() - Long.parseLong(datas.get(position).ctime);
        long day = l / (24 * 60 * 60 * 1000);
        long hour = (l / (60 * 60 * 1000) - day * 24);
        long min = ((l / (60 * 1000)) - day * 24 * 60 - hour * 60);
        holder.sexAndAge.setText(datas.get(position).user.sex.equals("2") ? "女" : "男");
        if (day < 50) {
            holder.createTime.setText(day + "天" + hour + "时" + min + "分钟前");
        } else {
            holder.createTime.setText("N天" + hour + "时" + min + "分钟前");
        }
        holder.title.setText(datas.get(position).title + "");
        String newString = datas.get(position).absText;
        Pattern pattern = Pattern.compile("[a-z_]{1,}", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(newString);
        while (matcher.find()) {
            newString = newString.replaceAll(":" + matcher.group().trim() + ":", "<img src='" + MyApplicaition.emotionsKeySrc.get(":" + matcher.group().trim() + ":") + "'>");
        }

            holder.absText.setText(Html.fromHtml(newString, new Html.ImageGetter() {
                @Override
                public Drawable getDrawable(String source) {
                    Drawable drawable = null;
                    if (!TextUtils.isEmpty(source)&&!source.equals("null")) {
                        int id = Integer.parseInt(source);
                        drawable = activity.getResources().getDrawable(id);
                        if(drawable!=null){
                            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
                                    drawable.getIntrinsicHeight());}
                    }
                    return drawable;
                }
            }, null));


        holder.viewNum.setText(datas.get(position).pageView + "");
        holder.pinglunNum.setText(datas.get(position).totalComments + "");

        if (datas.get(position).totalImages != null && Integer.parseInt(datas.get(position).totalImages) > 0) {
            holder.imgGridview.setVisibility(View.VISIBLE);
            holder.imgVideolayout.setVisibility(View.GONE);
            holder.imgtotal.setVisibility(View.VISIBLE);
            holder.imgGridview.setAdapter(new PostImageAdapter(activity, datas.get(position).images, Constants.ITEM_POST_LIST_IMG));
            holder.imgtotal.setText("共" + datas.get(position).totalImages + "张");
        } else {
            holder.imgVideolayout.setVisibility(View.VISIBLE);
            holder.imgtotal.setVisibility(View.GONE);
            holder.imgGridview.setVisibility(View.GONE);
            if (!TextUtils.isEmpty(post.vedioUrl)) {
                if (!TextUtils.isEmpty(post.vedioUrl)) {
                    new AsyncTask<Void, Void, Bitmap>() {
                        @Override
                        protected Bitmap doInBackground(Void... params) {
                            Bitmap bitmap = CommonUtil.createBitmapFromVideoPath(post.vedioUrl);
                            return bitmap;
                        }

                        @Override
                        protected void onPostExecute(Bitmap bitmap) {
                            super.onPostExecute(bitmap);
                            holder.imgVideo.setImageBitmap(bitmap);
                        }
                    }.execute();
                }
            }
        }
        holder.postlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemClickListener.onItemClick(post);
            }
        });
        holder.imgGridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int gridPosition, long id) {
                mOnItemClickListener.onItemClick(post);
            }
        });

        holder.photoImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra(Constants.POST, post);
                intent.setClass(activity, InfoInterestPersonActivity.class);
                activity.startActivity(intent);
            }
        });
        if (post.user.sex.equals("2")) {
            Glide.with(activity).load(user.photoUrl).asBitmap().placeholder(R.drawable.women).into(new SimpleTarget<Bitmap>() {
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
            Glide.with(activity).load(user.photoUrl).asBitmap().placeholder(R.drawable.man).into(new SimpleTarget<Bitmap>() {
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
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                ArrayList<ProvinceAndCityEntity> data_ = CommonUtil.parseLocation(activity).data;
                if (null != datas) {
                    for (ProvinceAndCityEntity entify : data_) {
                        if (TextUtils.equals(datas.get(position).user.proCode + "", entify.proId + "")) {
                            for (ProvinceAndCityEntity.CitySub citySub : entify.citySub) {
                                if (TextUtils.equals(datas.get(position).user.cityCode, citySub.cityId + "")) {
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
                holder.arealocation.setText(text);
            }
        }.execute();
    }


    @Override
    public int getItemCount() {
        return datas.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private GridView imgGridview;
        private TextView viewNum;
        private TextView pinglunNum;
        private TextView nickName;
        private TextView title;
        private ImageView photoImg;
        private RelativeLayout postlayout;
        private TextView absText;
        private TextView imgtotal;
        private ImageView imgVideo;
        private TextView arealocation;
        private TextView createTime;
        private TextView sexAndAge;
        private FrameLayout imgVideolayout;

        public MyViewHolder(View view) {
            super(view);
            photoImg = (ImageView) view.findViewById(R.id.photoImg);
            nickName = (TextView) view.findViewById(R.id.nick);
            title = (TextView) view.findViewById(R.id.title);
            absText = (io.github.rockerhieu.emojicon.EmojiconTextView) view.findViewById(R.id.absText);
            createTime = (TextView) view.findViewById(R.id.createTime);
            arealocation = (TextView) view.findViewById(R.id.location);
            sexAndAge = (TextView) view.findViewById(R.id.sexAndAge);
            viewNum = (TextView) view.findViewById(R.id.viewNum);
            pinglunNum = (TextView) view.findViewById(R.id.pinglunNum);
            imgtotal = (TextView) view.findViewById(R.id.imgtotal);
            postlayout = (RelativeLayout) view.findViewById(R.id.postlayout);
            imgGridview = (GridView) view.findViewById(R.id.imgGridview);
            imgVideo = (ImageView) view.findViewById(R.id.imgVideo);
            imgVideolayout = (FrameLayout) view.findViewById(R.id.imgVideolayout);


        }
    }
}