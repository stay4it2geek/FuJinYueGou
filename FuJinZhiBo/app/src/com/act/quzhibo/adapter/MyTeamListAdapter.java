package com.act.quzhibo.adapter;

import android.app.Activity;
import android.content.Intent;
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
import com.act.quzhibo.bean.ShoppingCart;
import com.act.quzhibo.i.OnReturnTotalListner;
import com.act.quzhibo.ui.activity.CourseOrdersActivity;
import com.act.quzhibo.widget.CircleImageView;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;

public class MyTeamListAdapter extends RecyclerView.Adapter<MyTeamListAdapter.MyViewHolder> {
    ArrayList<Promotion> promotions;
    Activity mContext;

    public MyTeamListAdapter(Activity  activity, ArrayList<Promotion> promotions) {
        mContext = activity;
        this.promotions = promotions;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_referee_user, parent, false);//这个布局就是一个imageview用来显示图片
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final RootUser refereeUser = promotions.get(position).refereeUser;
        BmobQuery<RootUser> user = new BmobQuery<>();
        user.getObject(refereeUser.getObjectId(), new QueryListener<RootUser>() {
            @Override
            public void done(final RootUser refereeUser, BmobException e) {
                if (e == null) {
                    if (refereeUser == null) {
                        return;
                    }
                    holder.createTime.setText(TextUtils.isEmpty(refereeUser.getCreatedAt())?"":"加入时间:"+refereeUser.getCreatedAt());
                    holder.userNickName.setText(TextUtils.isEmpty(refereeUser.getUsername())?"":refereeUser.getUsername());
                    Glide.with(mContext).load(refereeUser.photoFileUrl).into(holder.avater);
                    if (refereeUser.vipConis != null && refereeUser.vipConis > 0) {
                        if (0 < refereeUser.vipConis && refereeUser.vipConis < 3000) {
                            holder.vipLevel.setText("白银会员");
                        } else if (3000 < refereeUser.vipConis && refereeUser.vipConis < 5000) {
                            holder.vipLevel.setText("铂金会员");
                        } else if (5000 < refereeUser.vipConis && refereeUser.vipConis < 8000) {
                            holder.vipLevel.setText("黄金会员");
                        } else if (refereeUser.vipConis > 8000) {
                            holder.vipLevel.setText("钻石会员");
                        }else{
                            holder.vipLevel.setText("非会员");
                        }
                    }

                    holder.refereeOrder.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent=new Intent(mContext,CourseOrdersActivity.class);
                            intent.putExtra("user",refereeUser);
                            intent.putExtra("isTeamType",true);
                            mContext.startActivity(intent);
                        }
                    });

                }
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
