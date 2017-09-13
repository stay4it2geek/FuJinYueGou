package com.act.quzhibo.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.act.quzhibo.R;
import com.act.quzhibo.common.Constants;
import com.act.quzhibo.entity.Room;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class RoomListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final String cataTitle;
    private int screenWidth;
    private Context mContext;
    private String pathPrefix;
    private ArrayList<Room> datas;

    public interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, int position);
    }

    private OnRecyclerViewItemClickListener mOnItemClickListener = null;

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    public RoomListAdapter(Context context, ArrayList<Room> datas, String pathPrefix, int screenWidth, String cataTitle) {
        mContext = context;
        this.pathPrefix = pathPrefix;
        this.cataTitle = cataTitle;
        this.datas = datas;
        this.screenWidth = screenWidth;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.grid_shower_room_item, parent, false);//这个布局就是一个imageview用来显示图片
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof MyViewHolder) {
            if (!TextUtils.equals("手机达人", cataTitle)) {
                ((MyViewHolder) holder).showerAvtar.setLayoutParams(new RelativeLayout.LayoutParams((screenWidth / 2 - 20), RelativeLayout.LayoutParams.WRAP_CONTENT));
            } else {
                ((MyViewHolder) holder).showerAvtar.setLayoutParams(new RelativeLayout.LayoutParams((screenWidth), RelativeLayout.LayoutParams.WRAP_CONTENT));
            }

            if (Integer.parseInt(datas.get(position).onlineCount) > 1 && datas.get(position).liveType != null && (datas.get(position).liveType.equals(Constants.LANSPACE_IS_LIVE)
                    || datas.get(position).liveType.equals(Constants.PORTAIL_IS_LIVE))) {
                ((MyViewHolder) holder).onlineCount.setVisibility(View.VISIBLE);
                ((MyViewHolder) holder).isRelax.setVisibility(View.GONE);
                ((MyViewHolder) holder).onlineCount.setText(datas.get(position).onlineCount + "人");
            } else {
                ((MyViewHolder) holder).isRelax.setVisibility(View.VISIBLE);
                ((MyViewHolder) holder).isRelax.setText("休息中");
                ((MyViewHolder) holder).onlineCount.setVisibility(View.GONE);
            }

            ((MyViewHolder) holder).showerAvtar.setAdjustViewBounds(true);
            ((MyViewHolder) holder).showerAvtar.setScaleType(ImageView.ScaleType.FIT_XY);
            if (datas.get(position).gender.equals("0")) {
                Glide.with(mContext).load(pathPrefix + datas.get(position).poster_path_400).placeholder(R.drawable.women).into(((MyViewHolder) holder).showerAvtar);//加载网络图片
            } else {
                Glide.with(mContext).load(pathPrefix + datas.get(position).poster_path_400).placeholder(R.drawable.man).into(((MyViewHolder) holder).showerAvtar);//加载网络图片

            }
        }

        ((MyViewHolder) holder).nickName.setText(datas.get(position).nickname);
        ((MyViewHolder) holder).showerAvtar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemClickListener.onItemClick(v, position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView isRelax;
        private ImageView showerAvtar;
        private TextView nickName;
        private TextView onlineCount;

        public MyViewHolder(View view) {
            super(view);
            showerAvtar = (ImageView) view.findViewById(R.id.showerAvtar);
            nickName = (TextView) view.findViewById(R.id.nickName);
            onlineCount = (TextView) view.findViewById(R.id.onlineCount);
            isRelax = (TextView) view.findViewById(R.id.isRelax);
        }
    }

}
