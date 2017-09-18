package com.act.quzhibo.adapter;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.act.quzhibo.R;
import com.act.quzhibo.common.Constants;
import com.act.quzhibo.entity.MyPost;
import com.act.quzhibo.entity.RootUser;
import com.act.quzhibo.view.MyListView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import cn.bmob.v3.BmobUser;

public class MyPostPageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private LayoutInflater mLayoutInflater;
    private MyPost post;
    private FragmentActivity activity;

    public enum ITEM_TYPE {
        ITEM1,
        ITEM2,
        ITEM3
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return ITEM_TYPE.ITEM1.ordinal();
        } else if (position == 1) {
            return ITEM_TYPE.ITEM2.ordinal();
        } else if (position == 2) {
            return ITEM_TYPE.ITEM3.ordinal();
        } else {
            return super.getItemViewType(position);
        }
    }

    public MyPostPageAdapter(MyPost post, FragmentActivity context) {
        this.activity = context;
        this.post = post;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE.ITEM1.ordinal()) {
            return new Item1ViewHolder(mLayoutInflater.inflate(R.layout.post_detail_header_layout, parent, false));
        } else if (viewType == ITEM_TYPE.ITEM2.ordinal()) {
            return new Item2ViewHolder(mLayoutInflater.inflate(R.layout.post_detail_imgs_layout, parent, false));
        } else {
            return new Item3ViewHolder(mLayoutInflater.inflate(R.layout.comments_layout, parent, false));
        }
    }
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int positon) {
        if (holder instanceof Item1ViewHolder) {
            if (BmobUser.getCurrentUser(RootUser.class).sex.equals("女")) {
                Glide.with(activity).load(BmobUser.getCurrentUser(RootUser.class).photoUrlFile.getUrl() + "").asBitmap().placeholder(R.drawable.women).into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        ((Item1ViewHolder) holder).userImage.setBackgroundDrawable(new BitmapDrawable(resource));
                    }
                    @Override
                    public void onLoadStarted(Drawable placeholder) {
                        super.onLoadStarted(placeholder);
                    }
                });
            } else {
                Glide.with(activity).load(BmobUser.getCurrentUser(RootUser.class).photoUrlFile.getUrl() + "").asBitmap().placeholder(R.drawable.man).into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        ((Item1ViewHolder) holder).userImage.setBackgroundDrawable(new BitmapDrawable(resource));
                    }
                    @Override
                    public void onLoadStarted(Drawable placeholder) {
                        super.onLoadStarted(placeholder);
                    }
                });
            }

            ((Item1ViewHolder) holder).sexAndAge.setText(BmobUser.getCurrentUser(RootUser.class).sex);
            long l = System.currentTimeMillis() - Long.parseLong(post.ctime);
            long day = l / (24 * 60 * 60 * 1000);
            long hour = (l / (60 * 60 * 1000) - day * 24);
            long min = ((l / (60 * 1000)) - day * 24 * 60 - hour * 60);
            if (day <50) {
                ((Item1ViewHolder) holder).createTime.setText(day + "天" + hour + "小时" + min + "分钟前");
            } else {
                ((Item1ViewHolder) holder).createTime.setText("N天" + hour + "小时" + min + "分钟前");
            }
            ((Item1ViewHolder) holder).nickName.setText(BmobUser.getCurrentUser(RootUser.class).getUsername());
            ((Item1ViewHolder) holder).title.setText(post.title + "");
            ((Item1ViewHolder) holder).content.setText(post.absText + "");
            ((Item1ViewHolder) holder).arealocation.setText(BmobUser.getCurrentUser(RootUser.class).provinceAndcity + "");
        } else if (holder instanceof Item2ViewHolder) {
            ((Item2ViewHolder) holder).listView.setAdapter(new PostImageAdapter(activity, post.images, Constants.ITEM_POST_DETAIL_IMG));
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }

    class Item1ViewHolder extends RecyclerView.ViewHolder {

        private TextView arealocation;
        private TextView createTime;
        private ImageView userImage;
        private TextView sexAndAge;
        private TextView nickName;
        private TextView title;
        private io.github.rockerhieu.emojicon.EmojiconTextView content;

        public Item1ViewHolder(View view) {
            super(view);
            nickName = (TextView) view.findViewById(R.id.nickName);
            createTime = (TextView) view.findViewById(R.id.createTime);
            arealocation = (TextView) view.findViewById(R.id.locaiton);
            userImage = (ImageView) view.findViewById(R.id.userImage);
            sexAndAge = (TextView) view.findViewById(R.id.sexAndAge);
            nickName = (TextView) view.findViewById(R.id.nickName);
            title = (TextView) view.findViewById(R.id.title);
            content = (io.github.rockerhieu.emojicon.EmojiconTextView) view.findViewById(R.id.content);
        }


    }

    class Item2ViewHolder extends RecyclerView.ViewHolder {
        private MyListView listView;

        public Item2ViewHolder(View view) {
            super(view);
            listView = (MyListView) view.findViewById(R.id.imglistview);
        }
    }

    class Item3ViewHolder extends RecyclerView.ViewHolder {

        public Item3ViewHolder(View view) {
            super(view);
            view.findViewById(R.id.pinglunlayout).setVisibility(View.GONE);
        }
    }
}