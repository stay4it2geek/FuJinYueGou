package com.act.quzhibo.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.act.quzhibo.R;
import com.act.quzhibo.bean.Promotion;
import com.act.quzhibo.bean.RootUser;
import com.act.quzhibo.widget.CircleImageView;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class MyTeamListAdapter extends RecyclerView.Adapter<MyTeamListAdapter.MyViewHolder> {
    ArrayList<Promotion> promotions;
    Activity mContext;

    public MyTeamListAdapter(Activity context, ArrayList<Promotion> promotions) {
        mContext = context;
        this.promotions = promotions;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_referee_user, parent, false);//这个布局就是一个imageview用来显示图片
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        RootUser refreeUser = promotions.get(position).refereeUser;
        if (refreeUser == null) {
            return;
        }
        holder.createTime.setText(TextUtils.isEmpty(refreeUser.getCreatedAt())?"":"加入时间:"+refreeUser.getCreatedAt());
        holder.userNickName.setText(TextUtils.isEmpty(refreeUser.getUsername())?"":refreeUser.getUsername());
        Glide.with(mContext).load(refreeUser.photoFileUrl).into(holder.avater);
        if (refreeUser.vipConis != null && refreeUser.vipConis > 0) {
            if (0 < refreeUser.vipConis && refreeUser.vipConis < 3000) {
                holder.vipLevel.setText("初级趣会员");
            } else if (3000 < refreeUser.vipConis && refreeUser.vipConis < 5000) {
                holder.vipLevel.setText("中级趣会员");
            } else if (5000 < refreeUser.vipConis && refreeUser.vipConis < 8000) {
                holder.vipLevel.setText("特级趣会员");
            } else if (refreeUser.vipConis > 8000) {
                holder.vipLevel.setText("超级趣会员");
            }else{
                holder.vipLevel.setText("非趣会员");
            }
        }

        holder.orderLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });

        holder.refereeOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return promotions.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout orderLayout;
        TextView createTime;
        TextView userNickName;
        CircleImageView avater;
        TextView vipLevel;
        TextView refereeOrder;

        public MyViewHolder(View view) {
            super(view);
            createTime = (TextView) view.findViewById(R.id.createTime);
            userNickName = (TextView) view.findViewById(R.id.userNickName);
            vipLevel = (TextView) view.findViewById(R.id.vipLevel);
            avater = (CircleImageView) view.findViewById(R.id.avater);
            refereeOrder = (TextView) view.findViewById(R.id.refereeOrder);
            orderLayout = (RelativeLayout) view.findViewById(R.id.order_layout);

        }
    }
}
