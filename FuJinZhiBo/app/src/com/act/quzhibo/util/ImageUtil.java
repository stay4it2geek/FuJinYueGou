package com.act.quzhibo.util;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

/**
 * Created by ps_an on 2016/3/11.
 */
public class ImageUtil {


    public static void load(Context mContext, String uri, ImageView view, int placeholder) {
        Glide.with(mContext)
                .load(uri)
                .placeholder(placeholder)
                .crossFade()
                .into(view);
    }

    public static void loadBitmapImg(Context mContext, String uri, ImageView view, int placeholder) {
        Glide.with(mContext)
                .load(uri)
                .placeholder(placeholder)
                .crossFade()
                .into(view);
    }

}