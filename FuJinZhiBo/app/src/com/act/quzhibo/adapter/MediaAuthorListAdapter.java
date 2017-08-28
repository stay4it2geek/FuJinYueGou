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

import java.util.ArrayList;

public class MediaAuthorListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<MediaAuthor> medias;
    private Activity activity;

    public interface OnMediaRecyclerViewItemClickListener {
        void onItemClick(MediaAuthor meidaList);
    }

    private OnMediaRecyclerViewItemClickListener mOnItemClickListener = null;

    public void setOnItemClickListener(OnMediaRecyclerViewItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    //适配器初始化
    public MediaAuthorListAdapter(Activity context, ArrayList<MediaAuthor> medias) {
        activity = context;
        this.medias = medias;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.media_list_item, parent, false);//这个布局就是一个imageview用来显示图片
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof MyViewHolder) {
            ((MyViewHolder) holder).nickName.setText(medias.get(position).nickName + "");
            ((MyViewHolder) holder).introduce.setText(medias.get(position).introduce + "");
            ((MyViewHolder) holder).media_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onItemClick(medias.get(position));
                }
            });
            Glide.with(activity).load(medias.get(position).authorFile.getUrl()+"").placeholder(R.drawable.women).into(((MyViewHolder) holder).photoImg);//加载网络图片

        }
    }


    @Override
    public int getItemCount() {
        return medias.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView nickName;
        private TextView introduce;
        private ImageView photoImg;
        private RelativeLayout media_layout;

        public MyViewHolder(View view) {
            super(view);
            photoImg = (ImageView) view.findViewById(R.id.photoImg);
            nickName = (TextView) view.findViewById(R.id.nickName);
            introduce = (TextView) view.findViewById(R.id.introduce);
            media_layout = (RelativeLayout) view.findViewById(R.id.media_layout);
        }
    }


}
