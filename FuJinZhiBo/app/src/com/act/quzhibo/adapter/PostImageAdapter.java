package com.act.quzhibo.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.act.quzhibo.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.util.ArrayList;



/**
 * Created by weiminglin on 17/6/4.
 */

public class PostImageAdapter extends BaseAdapter {

    private int type;
    private Context context;
    private ArrayList<String> imgs;

    public PostImageAdapter(Context context, ArrayList<String> imgs, int type) {
        this.context = context;
        this.imgs = imgs;
        this.type = type;

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
            if (type == 0) {
                convertView = LayoutInflater.from(context).inflate(R.layout.item_post_img, parent, false);
            } else if (type == 1) {
                convertView = LayoutInflater.from(context).inflate(R.layout.item_post_page_img, parent, false);
            } else {
                convertView = LayoutInflater.from(context).inflate(R.layout.item_info_post_img, parent, false);
            }
            viewHolder.avatar = (ImageView) convertView.findViewById(R.id.postimg);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if (imgs != null && imgs.size() > 0) {
            Glide.with(context).load(imgs.get(position)).placeholder(R.drawable.xiangjiao).into(viewHolder.avatar);//加载网络图片
        }
        return convertView;
    }

    public class ViewHolder {
        ImageView avatar;
    }

}
