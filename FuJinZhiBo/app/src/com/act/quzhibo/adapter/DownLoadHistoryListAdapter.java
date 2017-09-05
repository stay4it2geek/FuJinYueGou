package com.act.quzhibo.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.act.quzhibo.R;
import com.act.quzhibo.entity.MediaAuthor;
import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DownLoadHistoryListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<File> paths;
    private Activity activity;

    public interface OnMediaRecyclerViewItemClickListener {
        void onItemClick(String path);
    }

    private OnMediaRecyclerViewItemClickListener mOnItemClickListener = null;

    public void setOnItemClickListener(OnMediaRecyclerViewItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    //适配器初始化
    public DownLoadHistoryListAdapter(Activity context) {
        activity = context;
    }


    public void updateDatas(List<File> paths) {
        this.paths = paths;
        this.notifyDataSetChanged();

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.item_post_page_img, parent, false);//这个布局就是一个imageview用来显示图片
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof MyViewHolder) {
            ((MyViewHolder) holder).photoImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onItemClick(paths.get(position).getAbsolutePath());
                }
            });
            Glide.with(activity).load(paths.get(position) + "").placeholder(R.drawable.women).into(((MyViewHolder) holder).photoImg);//加载网络图片

        }
    }


    @Override
    public int getItemCount() {
        return paths.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        private ImageView photoImg;

        public MyViewHolder(View view) {
            super(view);
            photoImg = (ImageView) view.findViewById(R.id.postimg);

        }
    }


}
