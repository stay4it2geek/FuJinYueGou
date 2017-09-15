package com.act.quzhibo.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.act.quzhibo.R;
import com.act.quzhibo.entity.MyFocusShower;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class MyFocusShowerListAdapter extends RecyclerView.Adapter<MyFocusShowerListAdapter.MyViewHolder> {

    private int screenWidth;
    private Context mContext;
    private ArrayList<MyFocusShower> datas;
    private OnDeleteListener mListener = null;

    public interface OnDeleteListener {
        void onDelete(int position);
    }

    public void setDelteListener(OnDeleteListener listener) {
        mListener = listener;
    }

    public interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, int position, MyFocusShower myFocusShower);
    }

    private OnRecyclerViewItemClickListener mOnItemClickListener = null;
    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    public MyFocusShowerListAdapter(Context context, ArrayList<MyFocusShower> datas, int screenWidth) {
        mContext = context;
        this.datas = datas;
        this.screenWidth = screenWidth;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.grid_shower_room_item, parent, false);//这个布局就是一个imageview用来显示图片
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        if (holder instanceof MyViewHolder) {
            holder.photoImg.setAdjustViewBounds(true);
            holder.photoImg.setScaleType(ImageView.ScaleType.FIT_XY);
            if (datas.get(position).gender.equals("0")) {
                Glide.with(mContext).load(datas.get(position).portrait_path_1280).placeholder(R.drawable.women).into(holder.photoImg);
            } else {
                Glide.with(mContext).load(datas.get(position).portrait_path_1280).placeholder(R.drawable.man).into(holder.photoImg);
            }
        }
        holder.nickName.setText(datas.get(position).nickname);
        holder.commonLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemClickListener.onItemClick(v, position, datas.get(position));
            }
        });
        holder.delete.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mListener.onDelete(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return datas.size();//获取数据的个数
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private RelativeLayout commonLayout;
        private ImageView photoImg;
        private TextView nickName;
        private TextView delete;

        public MyViewHolder(View view) {
            super(view);
            commonLayout = (RelativeLayout) view.findViewById(R.id.commonLayout);
            photoImg = (ImageView) view.findViewById(R.id.photoImg);
            nickName = (TextView) view.findViewById(R.id.nickName);
            delete = (TextView) view.findViewById(R.id.delete);
            delete.setVisibility(View.VISIBLE);
        }
    }
}
