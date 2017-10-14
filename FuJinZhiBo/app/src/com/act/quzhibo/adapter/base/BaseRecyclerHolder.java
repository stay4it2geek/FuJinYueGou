package com.act.quzhibo.adapter.base;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.act.quzhibo.R;
import com.bumptech.glide.Glide;


/**
 * 与BaseRecyclerAdapter一起使用
 *
 */
public class BaseRecyclerHolder extends RecyclerView.ViewHolder {

    private final SparseArray<View> mViews;
    private final Context context;
    public  int layoutId;

    public BaseRecyclerHolder(Context context, int layoutId, View itemView) {
        super(itemView);
        this.layoutId =layoutId;
        this.context =context;
        this.mViews = new SparseArray<>(8);
    }


    /**
     * @param viewId
     * @return
     */
    protected <T extends View> T getView(int viewId) {
        View view = mViews.get(viewId);
        if (view == null) {
            view = itemView.findViewById(viewId);
            mViews.put(viewId, view);
        }
        return (T) view;
    }

    /**
     * @param viewId
     * @param text
     * @return
     */
    public BaseRecyclerHolder setText(int viewId, String text) {
        TextView view = getView(viewId);
        view.setText(text);
        return this;
    }

    /**
     * 设置Enabled
     * @param viewId
     * @param enable
     * @return
     */
    public BaseRecyclerHolder setEnabled(int viewId,boolean enable){
        View v = getView(viewId);
        v.setEnabled(enable);
        return this;
    }

    /**
     * 点击事件
     * @param viewId
     * @param listener
     * @return
     */
    public BaseRecyclerHolder setOnClickListener(int viewId, View.OnClickListener listener){
        View v = getView(viewId);
        v.setOnClickListener(listener);
        return this;
    }

    /**
     * @param viewId
     * @param visibility
     * @return
     */
    public BaseRecyclerHolder setVisible(int viewId,int visibility) {
        View view = getView(viewId);
        view.setVisibility(visibility);
        return this;
    }

    /**
     * @param viewId
     * @param drawableId
     * @return
     */
    public BaseRecyclerHolder setImageResource(int viewId, int drawableId) {
        ImageView view = getView(viewId);
        view.setImageResource(drawableId);
        return this;
    }

    /**
     * @param viewId
     * @param bm
     * @return
     */
    public BaseRecyclerHolder setImageBitmap(int viewId, Bitmap bm) {
        ImageView view = getView(viewId);
        view.setImageBitmap(bm);
        return this;
    }

    /**
     * @param avatar
     * @param defaultRes
     * @param viewId
     * @return
     */
    public BaseRecyclerHolder setImageView(String avatar, int defaultRes, int viewId) {
        ImageView iv = getView(viewId);
        Glide.with(context).load(avatar).error(R.drawable.error_img).placeholder(R.drawable.women).into(iv);

        return this;
    }
}