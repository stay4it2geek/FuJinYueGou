package com.act.quzhibo.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.act.quzhibo.R;
import com.act.quzhibo.common.Constants;
import com.act.quzhibo.entity.RootUser;
import com.act.quzhibo.stackblur.StackBlurManager;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.util.ArrayList;

import cn.bmob.v3.BmobUser;

public class PostImageAdapter extends BaseAdapter {

    private int viewHodlerType;
    private Context context;
    private ArrayList<String> imgs;

    public PostImageAdapter(Context context, ArrayList<String> imgs, int viewHodlerType) {
        this.context = context;
        this.imgs = imgs;
        this.viewHodlerType = viewHodlerType;
    }

    public int getCount() {
        return imgs.size();
    }

    public Object getItem(int item) {
        return item;
    }

    public long getItemId(int id) {
        return id;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            if (viewHodlerType == Constants.ITEM_POST_LIST_IMG) {
                convertView = LayoutInflater.from(context).inflate(R.layout.item_post_list_img, parent, false);
            } else if (viewHodlerType ==  Constants.ITEM_POST_DETAIL_IMG) {
                convertView = LayoutInflater.from(context).inflate(R.layout.item_post_detail_img, parent, false);
            } else {
                convertView = LayoutInflater.from(context).inflate(R.layout.item_info_common_user_img, parent, false);
            }
            viewHolder.avatar = (ImageView) convertView.findViewById(R.id.postimg);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        RootUser rootUser = BmobUser.getCurrentUser(RootUser.class);
        if (imgs != null && imgs.size() > 0) {
            if (BmobUser.getCurrentUser(RootUser.class) == null) {
                blurImage(position, viewHolder);
            } else {
                if (rootUser.vipConis < 14000) {
                    blurImage(position, viewHolder);
                } else {
                    Glide.with(context).load(imgs.get(position)).diskCacheStrategy(DiskCacheStrategy.SOURCE).placeholder(R.drawable.xiangjiao).into(viewHolder.avatar);//加载网络图片
                }
            }
        }


        return convertView;
    }

    private void blurImage(int position, final ViewHolder viewHolder) {
        Glide.with(context).load(imgs.get(position)).asBitmap().placeholder(R.drawable.xiangjiao).into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(final Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                new AsyncTask<Void, Void, StackBlurManager>() {
                    @Override
                    protected StackBlurManager doInBackground(Void... params) {
                        StackBlurManager stackBlurManager = new StackBlurManager(context, resource);
                        return stackBlurManager;
                    }

                    @Override
                    protected void onPostExecute(StackBlurManager stackBlurManager) {
                        super.onPostExecute(stackBlurManager);
                        viewHolder.avatar.setImageBitmap(stackBlurManager.process(15));
                    }
                }.execute();
            }

            @Override
            public void onLoadStarted(Drawable placeholder) {
                super.onLoadStarted(placeholder);
                viewHolder.avatar.setBackgroundDrawable(placeholder);
            }
        });
    }

    public class ViewHolder {
        ImageView avatar;
    }

}
