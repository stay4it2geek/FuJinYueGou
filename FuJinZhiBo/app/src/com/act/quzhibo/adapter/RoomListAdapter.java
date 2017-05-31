package com.act.quzhibo.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.act.quzhibo.R;
import com.act.quzhibo.entity.Room;
import com.act.quzhibo.okhttp.OkHttpUtils;
import com.bumptech.glide.Glide;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by asus-pc on 2017/5/31.
 */

public class RoomListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private String pathPrefix;
    private ArrayList<Room> datas;//数据

    //自定义监听事件
    public interface OnRecyclerViewItemClickListener {
        void onItemClick(View view,int position);
    }

    private OnRecyclerViewItemClickListener mOnItemClickListener = null;

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    //适配器初始化
    public RoomListAdapter(Context context, ArrayList<Room> datas,String pathPrefix) {
        mContext = context;
        this.pathPrefix = pathPrefix;
        this.datas = datas;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //根据item类别加载不同ViewHolder
        View view = LayoutInflater.from(mContext).inflate(R.layout.grid_showe_room_item, parent, false);//这个布局就是一个imageview用来显示图片
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof MyViewHolder) {
            Glide.with(mContext).load(pathPrefix+datas.get(position).portrait_path_original).into(((MyViewHolder) holder).showerAvtar);//加载网络图片
        }
        ((MyViewHolder) holder).nickName.setText(datas.get(position).nickname);
        ((MyViewHolder) holder).onlineCount.setText(datas.get(position).onlineCount+"人在线");
        ((MyViewHolder) holder).showerAvtar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemClickListener.onItemClick(v,position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return datas.size();//获取数据的个数
    }


    //自定义ViewHolder，用于加载图片
    class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView showerAvtar;
        private TextView nickName;
        private TextView onlineCount;

        public MyViewHolder(View view) {
            super(view);
            showerAvtar = (ImageView) view.findViewById(R.id.showerAvtar);
            nickName = (TextView) view.findViewById(R.id.nickName);
            onlineCount = (TextView) view.findViewById(R.id.onlineCount);

        }
    }

    public void addItem(Room room, int position) {
        datas.add(position, room);
        notifyItemInserted(position);
    }
}
