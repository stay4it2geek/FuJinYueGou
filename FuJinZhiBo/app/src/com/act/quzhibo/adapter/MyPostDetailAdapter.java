package com.act.quzhibo.adapter;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.act.quzhibo.R;
import com.act.quzhibo.entity.MyPost;
import com.act.quzhibo.entity.RootUser;
import com.act.quzhibo.util.CommonUtil;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.io.File;
import java.util.List;

import cn.bingoogolapple.photopicker.activity.BGAPhotoPreviewActivity;
import cn.bingoogolapple.photopicker.widget.BGANinePhotoLayout;
import cn.bmob.v3.BmobUser;

public class MyPostDetailAdapter extends RecyclerView.Adapter<MyPostDetailAdapter.Item1ViewHolder> implements BGANinePhotoLayout.Delegate {
    private LayoutInflater mLayoutInflater;
    private MyPost post;
    private FragmentActivity activity;
    private File downloadDir;

    @Override
    public void onClickNinePhotoItem(BGANinePhotoLayout ninePhotoLayout, View view, int position, String model, List<String> models) {
        if (ninePhotoLayout.getItemCount() == 1) {
            activity.startActivity(BGAPhotoPreviewActivity.newIntent(activity, downloadDir, ninePhotoLayout.getCurrentClickItem()));
        } else if (ninePhotoLayout.getItemCount() > 1) {
            activity.startActivity(BGAPhotoPreviewActivity.newIntent(activity, downloadDir, ninePhotoLayout.getData(), ninePhotoLayout.getCurrentClickItemPosition()));
        }
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    public MyPostDetailAdapter(MyPost post, FragmentActivity context) {
        this.activity = context;
        this.post = post;
        mLayoutInflater = LayoutInflater.from(context);
        downloadDir = new File(Environment.getExternalStorageDirectory(), "PhotoPickerDownload");
        if (!downloadDir.exists()) {
            downloadDir.mkdirs();
        }
    }

    @Override
    public Item1ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Item1ViewHolder(mLayoutInflater.inflate(R.layout.post_detail_header_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(final Item1ViewHolder holder, final int positon) {

        if (BmobUser.getCurrentUser(RootUser.class).sex.equals("女")) {
            Glide.with(activity).load(BmobUser.getCurrentUser(RootUser.class).photoFileUrl + "").asBitmap().placeholder(R.drawable.women).into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                    holder.userImage.setBackgroundDrawable(new BitmapDrawable(resource));
                }

                @Override
                public void onLoadFailed(Exception e, Drawable errorDrawable) {
                    super.onLoadFailed(e, errorDrawable);
                    holder.userImage.setBackgroundDrawable(errorDrawable);

                }
            });
        } else {
            Glide.with(activity).load(BmobUser.getCurrentUser(RootUser.class).photoFileUrl + "").asBitmap().placeholder(R.drawable.man).into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                    holder.userImage.setBackgroundDrawable(new BitmapDrawable(resource));
                }

                @Override
                public void onLoadFailed(Exception e, Drawable errorDrawable) {
                    super.onLoadFailed(e, errorDrawable);
                    holder.userImage.setBackgroundDrawable(errorDrawable);
                }

            });
        }

        holder.sexAndAge.setText(BmobUser.getCurrentUser(RootUser.class).sex);
        long l = System.currentTimeMillis() - Long.parseLong(CommonUtil.dateToStamp(post.getCreatedAt()));
        long day = l / (24 * 60 * 60 * 1000);
        long hour = (l / (60 * 60 * 1000) - day * 24);
        long min = ((l / (60 * 1000)) - day * 24 * 60 - hour * 60);
        if (day < 50) {
            holder.createTime.setText(day + "天" + hour + "小时" + min + "分钟前");
        } else {
            holder.createTime.setText("N天" + hour + "小时" + min + "分钟前");
        }
        holder.nickName.setText(BmobUser.getCurrentUser(RootUser.class).getUsername());
        holder.title.setText(post.title + "");
        holder.content.setText(post.absText + "");
        holder.areaLocation.setText(BmobUser.getCurrentUser(RootUser.class).provinceAndcity + "");

        holder.ninePhotoLayout.setDelegate(this);
        holder.ninePhotoLayout.setData(post.images);

    }

    @Override
    public int getItemCount() {
        return 1;
    }

    class Item1ViewHolder extends RecyclerView.ViewHolder {

        private TextView areaLocation;
        private TextView createTime;
        private ImageView userImage;
        private TextView sexAndAge;
        private TextView nickName;
        private TextView title;
        private io.github.rockerhieu.emojicon.EmojiconTextView content;
        BGANinePhotoLayout ninePhotoLayout;

        public Item1ViewHolder(View view) {
            super(view);
            nickName = (TextView) view.findViewById(R.id.nickName);
            createTime = (TextView) view.findViewById(R.id.createTime);
            areaLocation = (TextView) view.findViewById(R.id.location);
            userImage = (ImageView) view.findViewById(R.id.userImage);
            sexAndAge = (TextView) view.findViewById(R.id.sexAndAge);
            nickName = (TextView) view.findViewById(R.id.nickName);
            title = (TextView) view.findViewById(R.id.title);
            content = (io.github.rockerhieu.emojicon.EmojiconTextView) view.findViewById(R.id.content);
            ninePhotoLayout = (BGANinePhotoLayout) view.findViewById(R.id.imglistview);
        }

    }

}

