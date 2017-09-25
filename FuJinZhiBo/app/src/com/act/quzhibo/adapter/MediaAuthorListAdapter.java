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

public class MediaAuthorListAdapter extends RecyclerView.Adapter<MediaAuthorListAdapter.MyViewHolder> {
    private ArrayList<MediaAuthor> medias;
    private Activity activity;

    public interface OnMediaRecyclerViewItemClickListener {
        void onItemClick(MediaAuthor meidaList);
    }

    private OnMediaRecyclerViewItemClickListener mOnItemClickListener = null;

    public void setOnItemClickListener(OnMediaRecyclerViewItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    public MediaAuthorListAdapter(Activity context, ArrayList<MediaAuthor> medias) {
        activity = context;
        this.medias = medias;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.media_list_item, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        if (holder instanceof MyViewHolder) {
            holder.nickName.setText(medias.get(position).nickName + "");
            holder.introduce.setText(medias.get(position).introduce + "");
            holder.mediaLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onItemClick(medias.get(position));
                }
            });
            Glide.with(activity).load(medias.get(position).authorFile.getUrl()+"").placeholder(R.drawable.women).into(holder.photoImg);
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
        private RelativeLayout mediaLayout;

        public MyViewHolder(View view) {
            super(view);
            photoImg = (ImageView) view.findViewById(R.id.photoImg);
            nickName = (TextView) view.findViewById(R.id.nickName);
            introduce = (TextView) view.findViewById(R.id.introduce);
            mediaLayout = (RelativeLayout) view.findViewById(R.id.media_layout);
        }
    }

}
