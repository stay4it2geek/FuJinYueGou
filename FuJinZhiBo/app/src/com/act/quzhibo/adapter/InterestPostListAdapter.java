package com.act.quzhibo.adapter;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.act.quzhibo.ProvinceAndCityEntify;
import com.act.quzhibo.R;
import com.act.quzhibo.common.Constants;
import com.act.quzhibo.entity.InterestPost;
import com.act.quzhibo.entity.InterstUser;
import com.act.quzhibo.ui.activity.UserInfoActivity;
import com.act.quzhibo.util.CommonUtil;
import com.act.quzhibo.view.CircleImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;


/**
 * Created by asus-pc on 2017/5/31.
 */

public class InterestPostListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Activity activity;
    private ArrayList<InterestPost> datas;//数据

    //自定义监听事件
    public interface OnInterestPostRecyclerViewItemClickListener {
        void onItemClick(InterestPost post);
    }

    private OnInterestPostRecyclerViewItemClickListener mOnItemClickListener = null;

    public void setOnItemClickListener(OnInterestPostRecyclerViewItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    //适配器初始化
        public InterestPostListAdapter(Activity context, ArrayList<InterestPost> datas) {
        activity = context;
        this.datas = datas;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.interest_post_list_item, parent, false);//这个布局就是一个imageview用来显示图片
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof MyViewHolder) {
            final InterstUser user = datas.get(position).user;
            final InterestPost post = datas.get(position);
            ((MyViewHolder) holder).nickName.setText(user.nick);

            long l = System.currentTimeMillis() - Long.parseLong(datas.get(position).ctime);
            long day = l / (24 * 60 * 60 * 1000);
            long hour = (l / (60 * 60 * 1000) - day * 24);
            long min = ((l / (60 * 1000)) - day * 24 * 60 - hour * 60);
            if (!datas.get(position).user.sex.equals("2")) {
                ((MyViewHolder) holder).sexAndAge.setBackgroundColor(activity.getResources().getColor(R.color.blue));
            }
            ((MyViewHolder) holder).sexAndAge.setText(datas.get(position).user.sex.equals("2") ? "女" : "男");
            ((MyViewHolder) holder).createTime.setText(hour + "小时" + min + "分钟前");
            ((MyViewHolder) holder).title.setText(datas.get(position).title);
            ((MyViewHolder) holder).absText.setText(datas.get(position).absText);
            ((MyViewHolder) holder).viewNum.setText(datas.get(position).pageView);
            ((MyViewHolder) holder).pinglunNum.setText(datas.get(position).totalComments);
            ((MyViewHolder) holder).dashangNum.setText(datas.get(position).rewards);

            if (datas.get(position).totalImages != null && Integer.parseInt(datas.get(position).totalImages) > 0) {
                ((MyViewHolder) holder).imgGridview.setVisibility(View.VISIBLE);
                ((MyViewHolder) holder).imgVideo.setVisibility(View.GONE);
                ((MyViewHolder) holder).imgtotal.setVisibility(View.VISIBLE);
                ((MyViewHolder) holder).imgGridview.setAdapter(new PostImageAdapter(activity, datas.get(position).images, datas.get(position).images.size()));

                ((MyViewHolder) holder).imgtotal.setText("共" + datas.get(position).totalImages + "张");
            } else {
                ((MyViewHolder) holder).imgtotal.setVisibility(View.GONE);
                ((MyViewHolder) holder).imgGridview.setVisibility(View.GONE);
                if (!TextUtils.isEmpty(post.vedioUrl)) {
                    ((MyViewHolder) holder).imgVideo.setVisibility(View.VISIBLE);
                    ((MyViewHolder) holder).imgVideo.setImageResource(R.drawable.video);
                }
            }
            ((MyViewHolder) holder).imgGridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int gridPosition, long id) {
                    mOnItemClickListener.onItemClick(post);
                }
            });
            ((MyViewHolder) holder).postlayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onItemClick(post);
                }
            });

            ((MyViewHolder) holder).photoImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent();
                    intent.putExtra(Constants.POST_USER,post.user);
                    intent.setClass(activity, UserInfoActivity.class);
                    activity.startActivity(intent);
                }
            });

            if(post.user.sex.equals("2")){
                Glide.with(activity).load(user.photoUrl).placeholder(R.drawable.women).diskCacheStrategy(DiskCacheStrategy.RESULT).into(((MyViewHolder) holder).photoImg);//加载网络图片

            }else{
                Glide.with(activity).load(user.photoUrl).placeholder(R.drawable.man).diskCacheStrategy(DiskCacheStrategy.RESULT).into(((MyViewHolder) holder).photoImg);//加载网络图片

            }
            new AsyncTask<Void, Void, String>() {
                @Override
                protected String doInBackground(Void... params) {
                    ArrayList<ProvinceAndCityEntify> data_ = CommonUtil.parseLocation(activity).data;
                    if (null != datas) {
                        for (ProvinceAndCityEntify entify : data_) {
                            if (TextUtils.equals(datas.get(position).user.proCode + "", entify.proId + "")) {
                                for (ProvinceAndCityEntify.CitySub citySub : entify.citySub) {
                                    if (TextUtils.equals(datas.get(position).user.cityCode, citySub.cityId + "")) {
                                        return !TextUtils.equals("",entify.name + citySub.name + "")?entify.name + citySub.name + "":"----";
                                    }
                                }
                            }
                        }
                    }
                    return "";
                }

                @Override
                protected void onPostExecute(String text) {
                    super.onPostExecute(text);
                    ((MyViewHolder) holder).arealocation.setText(text);
                }
            }.execute();

        }
    }


    @Override
    public int getItemCount() {
        return datas.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private GridView imgGridview;
        private TextView viewNum;
        private TextView pinglunNum;
        private TextView dashangNum;
        private TextView nickName;
        private TextView title;
        private ImageView photoImg;
        private RelativeLayout postlayout;
        private TextView absText;
        private TextView imgtotal;
        private ImageView imgVideo;
        private TextView arealocation;
        private TextView createTime;
        private TextView sexAndAge;

        public MyViewHolder(View view) {
            super(view);
            photoImg = (ImageView) view.findViewById(R.id.photoImg);
            nickName = (TextView) view.findViewById(R.id.nick);
            title = (TextView) view.findViewById(R.id.title);
            absText = (io.github.rockerhieu.emojicon.EmojiconTextView) view.findViewById(R.id.absText);
            createTime = (TextView) view.findViewById(R.id.createTime);
            arealocation = (TextView) view.findViewById(R.id.arealocation);
            sexAndAge = (TextView) view.findViewById(R.id.sexAndAge);
            viewNum = (TextView) view.findViewById(R.id.viewNum);
            pinglunNum = (TextView) view.findViewById(R.id.pinglunNum);
            dashangNum = (TextView) view.findViewById(R.id.dashangNum);
            imgtotal = (TextView) view.findViewById(R.id.imgtotal);
            postlayout = (RelativeLayout) view.findViewById(R.id.postlayout);
            imgGridview = (GridView) view.findViewById(R.id.imgGridview);
            imgVideo = (ImageView) view.findViewById(R.id.imgVideo);
        }
    }

}