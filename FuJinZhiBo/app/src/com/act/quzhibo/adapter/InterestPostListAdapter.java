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
import com.act.quzhibo.R;
import com.act.quzhibo.common.Constants;
import com.act.quzhibo.bean.InterestParentPerson;
import com.act.quzhibo.bean.InterestPost;
import com.act.quzhibo.ui.activity.InfoInterestPersonActivity;
import com.act.quzhibo.util.CommonUtil;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.util.ArrayList;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InterestPostListAdapter extends RecyclerView.Adapter<InterestPostListAdapter.MyViewHolder> {
    private ArrayList<InterestPost> datas;
    private Activity activity;
    private boolean isNeedBlur;

    public interface OnInterestPostRecyclerViewItemClickListener {
        void onItemClick(InterestPost post);
    }

    private OnInterestPostRecyclerViewItemClickListener mOnItemClickListener = null;

    public void setOnItemClickListener(OnInterestPostRecyclerViewItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    public InterestPostListAdapter(Activity activity, ArrayList<InterestPost> datas, boolean isNeedBlur) {
        this.activity = activity;
        this.datas = datas;
        this.isNeedBlur = isNeedBlur;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.interest_post_list_item, parent, false);
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
         int randomAge;
        if (CommonUtil.loadData(activity, "randomAge") > 0) {
            if (Integer.parseInt(post.user.userId) != CommonUtil.loadData(activity, "userId")) {
                int age = 20;
                Random random = new Random();
                randomAge = random.nextInt(age) + 15;
                CommonUtil.saveData(activity, randomAge, "randomAge");
                CommonUtil.saveData(activity, Integer.parseInt(post.user.userId), "userId");
            } else {
                randomAge = CommonUtil.loadData(activity, "randomAge");
            }
        } else {
            int age = 20;
            Random random = new Random();
            randomAge = random.nextInt(age) + 15;
            CommonUtil.saveData(activity, randomAge, "randomAge");
            CommonUtil.saveData(activity, Integer.parseInt(post.user.userId), "userId");
        }
        holder.sexAndAge.setText(datas.get(position).user.sex.equals("2") ? "女 "+randomAge : "男 "+randomAge);
        if (day <= 1) {
            holder.createTime.setText(hour + "小时" + min + "分钟前");
        } else if (day < 30) {
            holder.createTime.setText(day + "天" + hour + "小时前");
        } else if (day > 30 && day < 60) {
            holder.createTime.setText("2个月前");
        } else if (day > 90) {
            holder.createTime.setText("3个月前");
        }
        holder.title.setText(datas.get(position).title + "");
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String newString = datas.get(position).absText;
                Pattern pattern = Pattern.compile("[a-z_]{1,}", Pattern.CASE_INSENSITIVE);
                final Matcher matcher = pattern.matcher(newString);
                while (matcher.find()) {
                    newString = newString.replaceAll(":" + matcher.group().trim() + ":", "<img src='" + MyApplicaition.emotionsKeySrc.get(":" + matcher.group().trim() + ":") + "'>");
                }
                if (newString.contains("null")) {
                    newString = newString.replaceAll("null", R.drawable.kissing_heart + "");
                }
                return newString;
            }

            @Override
            protected void onPostExecute(String newString) {
                super.onPostExecute(newString);
                holder.absText.setText(Html.fromHtml(newString, new Html.ImageGetter() {
                    @Override
                    public Drawable getDrawable(String source) {
                        Drawable drawable = null;
                        if (!TextUtils.isEmpty(source) && !source.equals("null")) {
                            int id = Integer.parseInt(source);
                            drawable = activity.getResources().getDrawable(id);
                            if (drawable != null) {
                                drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
                                        drawable.getIntrinsicHeight());
                            }
                        }
                        return drawable;
                    }
                }, null));
            }
        }.execute();


        holder.viewNum.setText(datas.get(position).pageView + "");
        holder.pinglunNum.setText(datas.get(position).totalComments + "");

        if (isNeedBlur) {
            holder.pName.setVisibility(View.VISIBLE);
            holder.pName.setText(post.sName);
        }
        if (datas.get(position).totalImages != null && Integer.parseInt(datas.get(position).totalImages) > 0) {
            holder.imgGridview.setVisibility(View.VISIBLE);
            holder.imgVideolayout.setVisibility(View.GONE);
            holder.imgtotal.setVisibility(View.VISIBLE);
            holder.imgGridview.setAdapter(new PostImageAdapter(activity, datas.get(position).images, Constants.ITEM_POST_LIST_IMG, isNeedBlur, false));
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
            } else {
                holder.imgVideolayout.setVisibility(View.GONE);

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
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
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
            Glide.with(activity).load(user.photoUrl).asBitmap().placeholder(R.drawable.women).error(R.drawable.error_img).into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                    holder.photoImg.setBackgroundDrawable(new BitmapDrawable(resource));
                }

                @Override
                public void onLoadStarted(Drawable placeholder) {
                    super.onLoadStarted(placeholder);
                    holder.photoImg.setBackgroundDrawable(placeholder);
                }

                @Override
                public void onLoadFailed(Exception e, Drawable errorDrawable) {
                    super.onLoadFailed(e, errorDrawable);
                    holder.photoImg.setBackgroundDrawable(errorDrawable);

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
                }

                @Override
                public void onLoadFailed(Exception e, Drawable errorDrawable) {
                    super.onLoadFailed(e, errorDrawable);
                    holder.photoImg.setBackgroundDrawable(errorDrawable);

                }
            });
        }
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String text = MyApplicaition.proKeySrc.get(datas.get(position).user.proCode) + MyApplicaition.cityKeySrc.get(datas.get(position).user.cityCode);
                return text;
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
        private TextView pName;
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
            pName = (TextView) view.findViewById(R.id.pName);
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