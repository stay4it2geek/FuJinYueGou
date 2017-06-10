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
import com.act.quzhibo.entity.InterestPlates;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;

/**
 * Created by asus-pc on 2017/5/31.
 */

public class InterestPlatesListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private ArrayList<InterestPlates> datas;//数据

    //自定义监听事件
    public interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, int position,String plateId);
    }

    private OnRecyclerViewItemClickListener mOnItemClickListener = null;

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    //适配器初始化
    public InterestPlatesListAdapter(Context context, ArrayList<InterestPlates> datas) {
        mContext = context;
        this.datas = datas;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_interest_plates, parent, false);//这个布局就是一个imageview用来显示图片
        MyViewHolder holder = new MyViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        if (holder instanceof MyViewHolder) {
            Glide.with(mContext).load(datas.get(position).imgUrl).placeholder(R.drawable.ic_launcher).diskCacheStrategy(DiskCacheStrategy.RESULT).into(((MyViewHolder) holder).plateImg);//加载网络图片
            ((MyViewHolder) holder).pAbstract.setText(datas.get(position).pAbstract);
            ((MyViewHolder) holder).pName.setText(datas.get(position).pName);
            ((MyViewHolder) holder).plateLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onItemClick(v, position,datas.get(position).pid);
                }
            });
        }else {

        }


    }

    @Override
    public int getItemCount() {
        return datas.size();
    }


    //自定义ViewHolder
    class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView plateImg;
        private TextView pAbstract;
        private TextView pName;
        private RelativeLayout plateLayout;

        public MyViewHolder(View view) {
            super(view);
            plateImg = (ImageView) view.findViewById(R.id.plateImg);
            pAbstract = (TextView) view.findViewById(R.id.pAbstract);
            plateLayout = (RelativeLayout) view.findViewById(R.id.plateLayout);
            pName = (TextView) view.findViewById(R.id.pName);

        }
    }

}
