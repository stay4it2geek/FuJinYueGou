package com.act.quzhibo.adapter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.act.quzhibo.common.MyApplicaition;
import com.act.quzhibo.entity.ProvinceAndCityEntity;
import com.act.quzhibo.R;
import com.act.quzhibo.entity.InterestPostPageCommentDetail;
import com.act.quzhibo.util.CommonUtil;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by weiminglin on 17/6/4.
 */

public class PostCommentAdapter extends BaseAdapter {

    private Activity activity;
    private ArrayList<InterestPostPageCommentDetail> commentDetails;

    public PostCommentAdapter(Activity activity, ArrayList<InterestPostPageCommentDetail> commentDetails) {
        this.activity = activity;
        this.commentDetails = commentDetails;

    }

    public int getCount() {
        return commentDetails.size();
    }

    public Object getItem(int item) {
        return item;
    }

    public long getItemId(int id) {
        return id;
    }


    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (view == null) {
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(activity).inflate(R.layout.item_comment_layout, parent, false);
            viewHolder.nickName = (TextView) view.findViewById(R.id.nickName);
            viewHolder.createTime = (TextView) view.findViewById(R.id.createTime);
            viewHolder.arealocation = (TextView) view.findViewById(R.id.location);
            viewHolder.userImage = (ImageView) view.findViewById(R.id.userImage);
            viewHolder.sexAndAge = (TextView) view.findViewById(R.id.sexAndAge);
            viewHolder.content = (io.github.rockerhieu.emojicon.EmojiconTextView) view.findViewById(R.id.re_content);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        if (viewHolder.userImage.getTag() == null || (viewHolder.userImage.getTag()).equals(commentDetails.get(position).user.photoUrl)) {

            if (commentDetails.get(position).user.sex.equals("2")) {
                viewHolder.userImage.setTag(commentDetails.get(position).user.photoUrl);
                Glide.with(activity).load(commentDetails.get(position).user.photoUrl).asBitmap().placeholder(R.drawable.women).into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        viewHolder.userImage.setBackground(new BitmapDrawable(resource));
                        viewHolder.userImage.setTag(commentDetails.get(position).user.photoUrl);
                    }

                    @Override
                    public void onLoadStarted(Drawable placeholder) {
                        super.onLoadStarted(placeholder);
                        viewHolder.userImage.setBackgroundDrawable(placeholder);

                    }
                });
            } else {
                Glide.with(activity).load(commentDetails.get(position).user.photoUrl).asBitmap().placeholder(R.drawable.man).into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        viewHolder.userImage.setBackground(new BitmapDrawable(resource));
                        viewHolder.userImage.setTag(commentDetails.get(position).user.photoUrl);

                    }

                    @Override
                    public void onLoadStarted(Drawable placeholder) {
                        super.onLoadStarted(placeholder);
                        viewHolder.userImage.setBackgroundDrawable(placeholder);

                    }
                });
            }
        }
        long l = System.currentTimeMillis() - commentDetails.get(position).ctime;
        long day = l / (24 * 60 * 60 * 1000);
        long hour = (l / (60 * 60 * 1000) - day * 24);
        long min = ((l / (60 * 1000)) - day * 24 * 60 - hour * 60);
        if (commentDetails.get(position).user.sex != null)
            viewHolder.sexAndAge.setText(commentDetails.get(position).user.sex.equals("2") ? "女" : "男");
        viewHolder.createTime.setText(hour + "小时" + min + "分钟前");
        viewHolder.nickName.setText(commentDetails.get(position).user.nick);
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String newString = commentDetails.get(position).message;
                Pattern pattern = Pattern.compile("[a-z_]{1,}", Pattern.CASE_INSENSITIVE);
                final Matcher matcher = pattern.matcher(newString);
                while (matcher.find()) {
                    newString = newString.replaceAll(":" + matcher.group().trim() + ":", "<img src='" + MyApplicaition.emotionsKeySrc.get(":" + matcher.group().trim() + ":") + "'>");
                }
                if (newString.contains("null")) {
                    newString= newString.replaceAll("null",R.drawable.smile+"");
                }
                return newString;
            }

            @Override
            protected void onPostExecute(String newString) {
                super.onPostExecute(newString);

                viewHolder.content.setText(Html.fromHtml(newString, new Html.ImageGetter() {
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

        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String text = MyApplicaition.proKeySrc.get(commentDetails.get(position).user.proCode) + MyApplicaition.cityKeySrc.get(commentDetails.get(position).user.cityCode);
                return text;
            }

            @Override
            protected void onPostExecute(String text) {
                super.onPostExecute(text);
                viewHolder.arealocation.setText(text);
            }
        }.execute();
        return view;
    }

    public class ViewHolder {
        private TextView arealocation;
        private TextView createTime;
        private ImageView userImage;
        private TextView sexAndAge;
        private TextView nickName;
        private io.github.rockerhieu.emojicon.EmojiconTextView content;
    }

}
