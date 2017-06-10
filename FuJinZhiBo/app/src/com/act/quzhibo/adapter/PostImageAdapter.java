package com.act.quzhibo.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.act.quzhibo.R;
import com.act.quzhibo.common.MyApplicaition;
import com.bumptech.glide.Glide;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

/**
 * Created by weiminglin on 17/6/4.
 */

public class PostImageAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<String> imgs;
    private int size;

    public PostImageAdapter(Context context, ArrayList<String> imgs, int size) {
        this.context = context;
        this.size = size;
        this.imgs = imgs;
    }

    public int getCount() {
        return size;
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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_post_img, parent, false);
            viewHolder.avatar = (ImageView) convertView.findViewById(R.id.postimg);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if (imgs != null && imgs.size() > 0) {
            Glide.with(context).load(imgs.get(position)).placeholder(R.drawable.ic_launcher).into(viewHolder.avatar);//加载网络图片
        }
        return convertView;
    }

    public class ViewHolder {
        ImageView avatar;
    }

}
