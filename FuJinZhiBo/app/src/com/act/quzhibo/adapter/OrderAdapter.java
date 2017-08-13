package com.act.quzhibo.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.act.quzhibo.R;
import com.act.quzhibo.entity.InterestPlates;
import com.act.quzhibo.entity.Member;
import com.act.quzhibo.entity.VipOrders;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;

public class OrderAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    ArrayList<VipOrders> vipOrderses;
    Activity mContext;
    private OnRecyclerViewListener mListener = null;

    public interface OnRecyclerViewListener {
        void onDelete(int position);
    }

    public void setListener(OnRecyclerViewListener listener) {
        mListener = listener;
    }

    public OrderAdapter(Activity context, ArrayList<VipOrders> vipOrderses) {
        mContext = context;
        this.vipOrderses = vipOrderses;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_order, parent, false);//这个布局就是一个imageview用来显示图片
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {

        if (viewHolder instanceof MyViewHolder) {
            ((MyViewHolder) viewHolder).orderStatus.setText(vipOrderses.get(position).orderStatus ? "支付已完成" : "交易关闭");
            ((MyViewHolder) viewHolder).orderDescription.setText("订单描述: " + vipOrderses.get(position).goodsDescription);
            ((MyViewHolder) viewHolder).updatedTime.setText("下单时间:" + vipOrderses.get(position).getUpdatedAt());
            ((MyViewHolder) viewHolder).orderPrice.setText("订单价格: ￥" + vipOrderses.get(position).orderPrice);
            ((MyViewHolder) viewHolder).order_layout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    mListener.onDelete(position);
                    return false;
                }
            });
            ((MyViewHolder) viewHolder).deleteOrder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.onDelete(position);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return vipOrderses.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView updatedTime;
        TextView orderDescription;
        TextView orderStatus;
        TextView orderPrice;
        RelativeLayout order_layout;
        TextView deleteOrder;

        public MyViewHolder(View view) {
            super(view);
            updatedTime = (TextView) view.findViewById(R.id.updatedTime);
            orderDescription = (TextView) view.findViewById(R.id.orderDescription);
            orderStatus = (TextView) view.findViewById(R.id.orderStatus);
            orderPrice = (TextView) view.findViewById(R.id.orderPrice);
            order_layout = (RelativeLayout) view.findViewById(R.id.order_layout);
            deleteOrder = (TextView) view.findViewById(R.id.deleteOrder);
        }
    }
}
