package com.act.quzhibo.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
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

import com.act.quzhibo.entity.ProvinceAndCityEntify;
import com.act.quzhibo.R;
import com.act.quzhibo.common.Constants;
import com.act.quzhibo.entity.InterestPostPerson;
import com.act.quzhibo.entity.InterestPost;
import com.act.quzhibo.ui.activity.InfoInterestPersonActivity;
import com.act.quzhibo.util.CommonUtil;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.util.ArrayList;

public class InterestPostListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int isBlurType;
    private ArrayList<InterestPost> datas;
    private Activity activity;

    public interface OnInterestPostRecyclerViewItemClickListener {
        void onItemClick(InterestPost post);
    }

    private OnInterestPostRecyclerViewItemClickListener mOnItemClickListener = null;

    public void setOnItemClickListener(OnInterestPostRecyclerViewItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    //适配器初始化
    public InterestPostListAdapter(Activity context, ArrayList<InterestPost> datas, int isBlurType) {
        activity = context;
        this.datas = datas;
        this.isBlurType = isBlurType;
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
            final InterestPostPerson user = datas.get(position).user;
            final InterestPost post = datas.get(position);
           String nick = user.nick.replaceAll("\r|\n", "");
            ((MyViewHolder) holder).nickName.setText(nick);

            long l = System.currentTimeMillis() - Long.parseLong(datas.get(position).ctime);
            long day = l / (24 * 60 * 60 * 1000);
            long hour = (l / (60 * 60 * 1000) - day * 24);
            long min = ((l / (60 * 1000)) - day * 24 * 60 - hour * 60);
            ((MyViewHolder) holder).sexAndAge.setText(datas.get(position).user.sex.equals("2") ? "女" : "男");
            if (day < 365) {
                ((MyViewHolder) holder).createTime.setText(day + "天" + hour + "时" + min + "分钟前");
            }else{
                ((MyViewHolder) holder).createTime.setText("N天" + hour + "时" + min + "分钟前");
            }
            ((MyViewHolder) holder).title.setText(datas.get(position).title + "");
            ((MyViewHolder) holder).absText.setText(datas.get(position).absText + "");
            ((MyViewHolder) holder).viewNum.setText(datas.get(position).pageView + "");
            ((MyViewHolder) holder).pinglunNum.setText(datas.get(position).totalComments + "");
            ((MyViewHolder) holder).dashangNum.setText(datas.get(position).rewards + "");

            if (datas.get(position).totalImages != null && Integer.parseInt(datas.get(position).totalImages) > 0) {
                ((MyViewHolder) holder).imgGridview.setVisibility(View.VISIBLE);
                ((MyViewHolder) holder).imgVideo.setVisibility(View.GONE);
                ((MyViewHolder) holder).imgtotal.setVisibility(View.VISIBLE);
                ((MyViewHolder) holder).imgGridview.setAdapter(new PostImageAdapter(activity, datas.get(position).images, 0, isBlurType));
                ((MyViewHolder) holder).imgtotal.setText("共" + datas.get(position).totalImages + "张");
            } else {
                ((MyViewHolder) holder).imgtotal.setVisibility(View.GONE);
                ((MyViewHolder) holder).imgGridview.setVisibility(View.GONE);
                if (!TextUtils.isEmpty(post.vedioUrl)) {
                    ((MyViewHolder) holder).imgVideo.setVisibility(View.VISIBLE);
                    ((MyViewHolder) holder).imgVideo.setImageResource(R.drawable.video);
                }
            }
            ((MyViewHolder) holder).postlayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onItemClick(post);
                }
            });
            ((MyViewHolder) holder).imgGridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int gridPosition, long id) {
                    mOnItemClickListener.onItemClick(post);
                }
            });

            ((MyViewHolder) holder).photoImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.putExtra(Constants.POST, post);
                    intent.setClass(activity, InfoInterestPersonActivity.class);
                    activity.startActivity(intent);
                }
            });
            if (post.user.sex.equals("2")) {
                Glide.with(activity).load(user.photoUrl).asBitmap().placeholder(R.drawable.women).into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        ((MyViewHolder) holder).photoImg.setBackgroundDrawable(new BitmapDrawable(resource));
                    }

                    @Override
                    public void onLoadStarted(Drawable placeholder) {
                        super.onLoadStarted(placeholder);
                    }
                });
            } else {
                Glide.with(activity).load(user.photoUrl).asBitmap().placeholder(R.drawable.man).into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        ((MyViewHolder) holder).photoImg.setBackgroundDrawable(new BitmapDrawable(resource));
                    }

                    @Override
                    public void onLoadStarted(Drawable placeholder) {
                        super.onLoadStarted(placeholder);
                    }
                });
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
                                        return !TextUtils.equals("", entify.name + citySub.name + "") ? entify.name + citySub.name + "" : "----";
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