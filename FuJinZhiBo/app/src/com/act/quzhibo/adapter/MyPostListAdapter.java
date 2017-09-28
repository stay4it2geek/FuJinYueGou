package com.act.quzhibo.adapter;

import android.app.Activity;
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

import com.act.quzhibo.MyStandardVideoController;
import com.act.quzhibo.R;
import com.act.quzhibo.common.Constants;
import com.act.quzhibo.entity.MyPost;
import com.act.quzhibo.entity.RootUser;
import com.act.quzhibo.util.CommonUtil;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.devlin_n.videoplayer.player.IjkVideoView;

import java.util.ArrayList;

import cn.bmob.v3.BmobUser;

public class MyPostListAdapter extends RecyclerView.Adapter<MyPostListAdapter.MyViewHolder> {
    private ArrayList<MyPost> posts;
    private Activity activity;
    RootUser rootUser = BmobUser.getCurrentUser(RootUser.class);

    public interface OnMyPostRecyclerViewItemClickListener {
        void onItemClick(MyPost post);
    }

    private OnMyPostRecyclerViewItemClickListener mOnItemClickListener = null;

    public void setOnItemClickListener(OnMyPostRecyclerViewItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    //适配器初始化
    public MyPostListAdapter(Activity context, ArrayList<MyPost> posts) {
        activity = context;
        this.posts = posts;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.interest_post_list_item, parent, false);//这个布局就是一个imageview用来显示图片
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
            String nick = rootUser.getUsername().replaceAll("\r|\n", "");
            final MyPost post=posts.get(position);
            holder.nickName.setText(nick);
            long l = System.currentTimeMillis() - Long.parseLong(CommonUtil.dateToStamp(post.getCreatedAt()));
            long day = l / (24 * 60 * 60 * 1000);
            long hour = (l / (60 * 60 * 1000) - day * 24);
            long min = ((l / (60 * 1000)) - day * 24 * 60 - hour * 60);
            holder.sexAndAge.setText(BmobUser.getCurrentUser(RootUser.class).sex + "");
            if (day < 365) {
                holder.createTime.setText(day + "天" + hour + "时" + min + "分钟前");
            } else {
                holder.createTime.setText("N天" + hour + "时" + min + "分钟前");
            }
            holder.title.setText(post.title + "");
            holder.absText.setText(post.absText + "");
            holder.viewNum.setText(post.pageView + "");
            holder.commentNum.setText(post.totalComments + "");

            if (post.images != null && post.images.size()> 0) {
                holder.imgGridview.setVisibility(View.VISIBLE);
                holder.imgVideo.setVisibility(View.GONE);
                holder.imgtotal.setVisibility(View.VISIBLE);
                holder.imgGridview.setAdapter(new PostImageAdapter(activity, post.images, Constants.ITEM_POST_DETAIL_IMG));
                holder.imgtotal.setText("共" + post.totalImages + "张");
            } else {
                holder.imgtotal.setVisibility(View.GONE);
                holder.imgGridview.setVisibility(View.GONE);
            }
            holder.postlayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onItemClick(post);
                }
            });
            holder.imgGridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int gridPosition, long id) {
                    mOnItemClickListener.onItemClick(post);
                }
            });

            if (rootUser.photoFileUrl != null) {
                if (rootUser.sex.equals("女")) {
                    Glide.with(activity).load(rootUser.photoFileUrl).asBitmap().placeholder(R.drawable.women).into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                            holder.photoImg.setBackgroundDrawable(new BitmapDrawable(resource));
                        }

                        @Override
                        public void onLoadFailed(Exception e, Drawable errorDrawable) {
                            super.onLoadFailed(e, errorDrawable);
                            holder.photoImg.setBackgroundDrawable(errorDrawable);

                        }
                    });
                } else {
                    Glide.with(activity).load(rootUser.photoFileUrl).asBitmap().placeholder(R.drawable.man).into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                            holder.photoImg.setBackgroundDrawable(new BitmapDrawable(resource));
                        }
                        @Override
                        public void onLoadFailed(Exception e, Drawable errorDrawable) {
                            super.onLoadFailed(e, errorDrawable);
                            holder.photoImg.setBackgroundDrawable(errorDrawable);

                        }
                    });
                }
            }

            holder.areaLocation.setText(rootUser.provinceAndcity + "");

        if (!TextUtils.isEmpty(post.vedioUrl)) {
            holder.ijkVideoView.setVisibility(View.VISIBLE);
            new AsyncTask<Void, Void, Bitmap>() {
                @Override
                protected Bitmap doInBackground(Void... params) {
                    Bitmap bitmap = CommonUtil.createBitmapFromVideoPath(post.vedioUrl);
                    return bitmap;
                }

                @Override
                protected void onPostExecute(Bitmap bitmap) {
                    super.onPostExecute(bitmap);
                    holder.controller.getThumb().setImageBitmap(bitmap);
                    holder.ijkVideoView
                            .enableCache()
                            .addToPlayerManager()
                            .setUrl(post.vedioUrl)
                            .setTitle(post.title)
                            .setVideoController(holder.controller);

                }
            }.execute();
        } else {
            holder.ijkVideoView.setVisibility(View.GONE);
        }

       
    }


    @Override
    public int getItemCount() {
        return posts.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private GridView imgGridview;
        private TextView viewNum;
        private TextView commentNum;
        private TextView nickName;
        private TextView title;
        private ImageView photoImg;
        private RelativeLayout postlayout;
        private TextView absText;
        private TextView imgtotal;
        private ImageView imgVideo;
        private TextView areaLocation;
        private TextView createTime;
        private TextView sexAndAge;
        private IjkVideoView ijkVideoView;
        private MyStandardVideoController controller;

        public MyViewHolder(View view) {
            super(view);
            photoImg = (ImageView) view.findViewById(R.id.photoImg);
            nickName = (TextView) view.findViewById(R.id.nick);
            title = (TextView) view.findViewById(R.id.title);
            absText = (io.github.rockerhieu.emojicon.EmojiconTextView) view.findViewById(R.id.absText);
            createTime = (TextView) view.findViewById(R.id.createTime);
            areaLocation = (TextView) view.findViewById(R.id.location);
            sexAndAge = (TextView) view.findViewById(R.id.sexAndAge);
            viewNum = (TextView) view.findViewById(R.id.viewNum);
            commentNum = (TextView) view.findViewById(R.id.pinglunNum);
            imgtotal = (TextView) view.findViewById(R.id.imgtotal);
            postlayout = (RelativeLayout) view.findViewById(R.id.postlayout);
            ijkVideoView = (IjkVideoView) itemView.findViewById(R.id.video_player);
            int widthPixels = activity.getResources().getDisplayMetrics().widthPixels;
            ijkVideoView.setLayoutParams(new RelativeLayout.LayoutParams(widthPixels, widthPixels / 14 * 9));
            controller = new MyStandardVideoController(activity);
            controller.setInitData(true, true);
            ijkVideoView.setVideoController(controller);
            imgGridview = (GridView) view.findViewById(R.id.imgGridview);
            imgVideo = (ImageView) view.findViewById(R.id.imgVideo);
        }
    }
}