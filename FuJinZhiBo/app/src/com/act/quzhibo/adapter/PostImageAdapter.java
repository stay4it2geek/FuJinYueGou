package com.act.quzhibo.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.act.quzhibo.R;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

/**
 * Created by weiminglin on 17/6/4.
 */

public class PostImageAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<String> imgs;

    public PostImageAdapter(Context context, ArrayList<String> imgs) {
        this.context = context;
        this.imgs = imgs;
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

    //创建View方法
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_post_img, parent, false);
            viewHolder.avatar = (ImageView) convertView.findViewById(R.id.postimg);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (imgs.size() > 0) {
            Glide.with(context).load(imgs.get(position)).placeholder(R.drawable.ic_launcher).into(viewHolder.avatar);//加载网络图片
        } else {
            viewHolder.avatar.setImageResource(R.drawable.default_head);
        }
        return convertView;
    }

    public class ViewHolder {
        ImageView avatar;
    }


}
