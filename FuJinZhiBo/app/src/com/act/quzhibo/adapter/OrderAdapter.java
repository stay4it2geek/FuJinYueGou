package com.act.quzhibo.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.act.quzhibo.R;
import com.act.quzhibo.bean.VipOrders;

import java.util.ArrayList;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.MyViewHolder> {
    ArrayList<VipOrders> vipOrderses;
    Activity mContext;
    private OnDeleteListener mListener = null;

    public interface OnDeleteListener {
        void onDelete(int position);
    }

    public void setDeleteListener(OnDeleteListener listener) {
        mListener = listener;
    }

    public OrderAdapter(Activity context, ArrayList<VipOrders> vipOrderses) {
        mContext = context;
        this.vipOrderses = vipOrderses;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_order, parent, false);//这个布局就是一个imageview用来显示图片
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        if (holder instanceof MyViewHolder) {
            holder.orderStatus.setText(vipOrderses.get(position).orderStatus ? "支付已完成" : "交易关闭");
            holder.orderDescription.setText("订单描述: " + vipOrderses.get(position).goodsDescription);
            holder.updatedTime.setText("下单时间:" + vipOrderses.get(position).getUpdatedAt());
            holder.orderPrice.setText("订单价格: ￥" + vipOrderses.get(position).orderPrice);
            holder.order_layout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    mListener.onDelete(position);
                    return false;
                }
            });
            holder.deleteOrder.setOnClickListener(new View.OnClickListener() {
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
