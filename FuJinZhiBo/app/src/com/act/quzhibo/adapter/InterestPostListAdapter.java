package com.act.quzhibo.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.act.quzhibo.R;
import com.act.quzhibo.entity.InterestPost;
import com.act.quzhibo.entity.InterstUser;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;

/**
 * Created by asus-pc on 2017/5/31.
 */

public class InterestPostListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private ArrayList<InterestPost> datas;//数据

    //自定义监听事件
    public interface OnInterestPostRecyclerViewItemClickListener {
        void onItemClick(View view, int position, InterstUser user);
    }

    private OnInterestPostRecyclerViewItemClickListener mOnItemClickListener = null;

    public void setOnItemClickListener(OnInterestPostRecyclerViewItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    //适配器初始化
    public InterestPostListAdapter(Context context, ArrayList<InterestPost> datas) {
        mContext = context;
        this.datas = datas;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.interest_post_list_item, parent, false);//这个布局就是一个imageview用来显示图片
        MyViewHolder holder = new MyViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        if (holder instanceof MyViewHolder) {
            final InterstUser user = datas.get(position).user;
            Glide.with(mContext).load(user.photoUrl).placeholder(R.drawable.ic_launcher).diskCacheStrategy(DiskCacheStrategy.RESULT).into(((MyViewHolder) holder).photoImg);//加载网络图片
            ((MyViewHolder) holder).nickName.setText(user.nick);
            ((MyViewHolder) holder).disMariState.setText(user.disMariState);
            ((MyViewHolder) holder).sex.setText(user.sex);
            ((MyViewHolder) holder).vipLevel.setText(user.vipLevel);
            ((MyViewHolder) holder).title.setText(datas.get(position).title);
            ((MyViewHolder) holder).absText.setText(datas.get(position).absText);
            ((MyViewHolder) holder).vipLevel.setText(user.vipLevel);
            if (datas.get(position).totalImages > 0) {
                ((MyViewHolder) holder).imgGridView.setNumColumns(datas.get(position).totalImages);
                ((MyViewHolder) holder).imgGridView.setAdapter(new PostImageAdapter(mContext, datas.get(position).images));
            }
            ((MyViewHolder) holder).postlayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onItemClick(v, position, user);
                }
            });
        }


    }

    @Override
    public int getItemCount() {
        return datas.size();
    }


    //自定义ViewHolder
    class MyViewHolder extends RecyclerView.ViewHolder {
        private GridView imgGridView;
        private TextView sex;
        private TextView vipLevel;
        private TextView disMariState;
        private TextView nickName;
        private TextView title;
        private ImageView photoImg;
        private RelativeLayout postlayout;
        private TextView absText;

        public MyViewHolder(View view) {
            super(view);
            photoImg = (ImageView) view.findViewById(R.id.photoImg);
            nickName = (TextView) view.findViewById(R.id.nick);
            title = (TextView) view.findViewById(R.id.title);
            absText = (io.github.rockerhieu.emojicon.EmojiconTextView) view.findViewById(R.id.absText);
            disMariState = (TextView) view.findViewById(R.id.disMariState);
            vipLevel = (TextView) view.findViewById(R.id.vipLevel);
            sex = (TextView) view.findViewById(R.id.sex);
            postlayout = (RelativeLayout) view.findViewById(R.id.postlayout);
            imgGridView = (GridView) view.findViewById(R.id.imgGridView);

        }
    }

}
