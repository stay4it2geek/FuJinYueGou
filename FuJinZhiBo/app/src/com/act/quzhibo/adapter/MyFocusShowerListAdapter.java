package com.act.quzhibo.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.act.quzhibo.R;
import com.act.quzhibo.common.Constants;
import com.act.quzhibo.entity.MyFocusShowers;
import com.act.quzhibo.entity.MyFocusShowers;
import com.act.quzhibo.okhttp.OkHttpUtils;
import com.act.quzhibo.okhttp.callback.StringCallback;
import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.Call;

public class MyFocusShowerListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final String cataTitle;
    private int screenWidth;
    private Context mContext;
    private String pathPrefix;
    private ArrayList<MyFocusShowers> datas;//数据


    private OnDeleteListener mListener = null;

    public interface OnDeleteListener {
        void onDelete(int position);
    }

    public void setDelteListener(OnDeleteListener listener) {
        mListener = listener;
    }

    //自定义监听事件
    public interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, int position, MyFocusShowers myFocusShowers);
    }

    private OnRecyclerViewItemClickListener mOnItemClickListener = null;

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    //适配器初始化
    public MyFocusShowerListAdapter(Context context, ArrayList<MyFocusShowers> datas,int screenWidth, String cataTitle) {
        mContext = context;
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
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof MyViewHolder) {
            ((MyViewHolder) holder).showerAvtar.setAdjustViewBounds(true);
            ((MyViewHolder) holder).showerAvtar.setScaleType(ImageView.ScaleType.FIT_XY);
            if (datas.get(position).gender.equals("0")) {
                Glide.with(mContext).load(datas.get(position).portrait_path_1280).placeholder(R.drawable.women).into(((MyViewHolder) holder).showerAvtar);//加载网络图片
            } else {
                Glide.with(mContext).load(datas.get(position).portrait_path_1280).placeholder(R.drawable.man).into(((MyViewHolder) holder).showerAvtar);//加载网络图片

            }
        }
        ((MyViewHolder) holder).nickName.setText(datas.get(position).nickname);
        ((MyViewHolder) holder).showerLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemClickListener.onItemClick(v, position, datas.get(position));
            }
        });
        ((MyViewHolder) holder).showerLayout.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                mListener.onDelete(position);
                return false;
            }
        });


    }

    @Override
    public int getItemCount() {
        return datas.size();//获取数据的个数
    }


    //自定义ViewHolder，用于加载图片
    class MyViewHolder extends RecyclerView.ViewHolder {
        private RelativeLayout showerLayout;
        private TextView isRelax;
        private ImageView showerAvtar;
        private TextView nickName;
        private TextView onlineCount;

        public MyViewHolder(View view) {
            super(view);
            showerLayout = (RelativeLayout) view.findViewById(R.id.showerLayout);
            showerAvtar = (ImageView) view.findViewById(R.id.showerAvtar);
            nickName = (TextView) view.findViewById(R.id.nickName);
            onlineCount = (TextView) view.findViewById(R.id.onlineCount);
            isRelax = (TextView) view.findViewById(R.id.isRelax);
        }
    }

}
