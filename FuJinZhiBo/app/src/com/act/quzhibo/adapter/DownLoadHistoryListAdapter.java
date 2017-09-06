package com.act.quzhibo.adapter;

import android.app.Activity;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.act.quzhibo.R;
import com.act.quzhibo.util.ToastUtil;
import com.bumptech.glide.Glide;

import java.io.File;
import java.util.List;

public class DownLoadHistoryListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<File> files;
    private Activity activity;

    public interface OnMediaRecyclerViewItemClickListener {
        void onItemClick(String path);
    }

    private OnMediaRecyclerViewItemClickListener mOnItemClickListener = null;

    public void setOnItemClickListener(OnMediaRecyclerViewItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    //适配器初始化
    public DownLoadHistoryListAdapter(Activity context, List<File> files) {
        activity = context;
        this.files = files;
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
                    mOnItemClickListener.onItemClick(files.get(position).getAbsolutePath());
                }
            });
            ToastUtil.showToast(activity, files.get(position).getAbsolutePath());
            if (! checkIsVideoFile(files.get(position).getName())) {
                Glide.with(activity).load(files.get(position).getAbsolutePath() + "").thumbnail(0.1f).placeholder(R.drawable.women).into(((MyViewHolder) holder).photoImg);//加载网络图片
            } else {
                Glide.with(activity).load(Uri.fromFile(new File(files.get(position).getAbsolutePath()))).into(((MyViewHolder) holder).photoImg);//加载网络图片
            }
        }
    }

    @Override
    public int getItemCount() {
        return files.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        private ImageView photoImg;

        public MyViewHolder(View view) {
            super(view);
            photoImg = (ImageView) view.findViewById(R.id.postimg);
        }
    }

    private boolean checkIsVideoFile(String fName) {
        boolean isVideoFile ;
        // 获取扩展名
        String FileEnd = fName.substring(fName.lastIndexOf(".") + 1,
                fName.length()).toLowerCase();
        if (FileEnd.equals("mp4") || FileEnd.equals("MP4") || FileEnd.equals("FLV")
                || FileEnd.equals("flv")) {
            isVideoFile = true;
        } else {
            isVideoFile = false;
        }
        return isVideoFile;
    }
}
