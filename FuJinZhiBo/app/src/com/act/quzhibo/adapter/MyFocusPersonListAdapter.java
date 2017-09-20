package com.act.quzhibo.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.act.quzhibo.R;
import com.act.quzhibo.common.Constants;
import com.act.quzhibo.entity.MyFocusCommonPerson;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class MyFocusPersonListAdapter extends RecyclerView.Adapter<MyFocusPersonListAdapter.MyViewHolder> {

    private int screenWidth;
    private Context mContext;
    private ArrayList<MyFocusCommonPerson> datas;//数据
    private OnDeleteListener mListener = null;

    public interface OnDeleteListener {
        void onDelete(int position);
    }

    public void setDeleteListener(OnDeleteListener listener) {
        mListener = listener;
    }

    public interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, int position, MyFocusCommonPerson myFocusCommonPerson);
    }

    private OnRecyclerViewItemClickListener mOnItemClickListener = null;

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    public MyFocusPersonListAdapter(Context context, ArrayList<MyFocusCommonPerson> datas,int screenWidth) {
        mContext = context;
        this.screenWidth = screenWidth;
        this.datas = datas;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.common_list_item, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
            holder.photoImg.setLayoutParams(new FrameLayout.LayoutParams((screenWidth / 2 - 20), (screenWidth / 2) - 5));
            holder.photoImg.setScaleType(ImageView.ScaleType.FIT_XY);
            if (datas.get(position).sex.equals("2")) {
                holder.sex.setText("女");
                Glide.with(mContext).load(datas.get(position).photoUrl).placeholder(R.drawable.women).into(holder.photoImg);
            } else {
                holder.sex.setText("男");
                Glide.with(mContext).load(datas.get(position).photoUrl).placeholder(R.drawable.man).into(holder.photoImg);

            }
        if(datas.get(position).userType.equals(Constants.INTEREST)){
            holder.locaitonDistance.setText("距离你太远了");
        }else{
            holder.locaitonDistance.setText("距离你"+datas.get(position).distance+"千米");
        }
        holder.introduce.setText(datas.get(position).introduce+"");
        holder.nickName.setText(datas.get(position).username);
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
        return datas.size();
    }
    class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView locaitonDistance;
        private RelativeLayout commonLayout;
        private TextView sex;
        private TextView introduce;
        private ImageView photoImg;
        private TextView nickName;
        private TextView delete;

        public MyViewHolder(View view) {
            super(view);
            commonLayout = (RelativeLayout) view.findViewById(R.id.commonLayout);
            delete = (TextView) view.findViewById(R.id.delete);
            photoImg = (ImageView) view.findViewById(R.id.photoImg);
            introduce = (TextView) view.findViewById(R.id.introduce);
            locaitonDistance = (TextView) view.findViewById(R.id.location);
            nickName = (TextView) view.findViewById(R.id.nickName);
            sex = (TextView) view.findViewById(R.id.sex);
            delete.setVisibility(View.VISIBLE);

        }
    }

}
