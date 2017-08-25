package com.act.quzhibo.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.act.quzhibo.entity.ProvinceAndCityEntify;
import com.act.quzhibo.R;
import com.act.quzhibo.common.Constants;
import com.act.quzhibo.entity.InterestPost;
import com.act.quzhibo.entity.InterestPostPageDetailAndComments;
import com.act.quzhibo.entity.PostContentAndImageDesc;
import com.act.quzhibo.ui.activity.InfoCommonActivity;
import com.act.quzhibo.util.CommonUtil;
import com.act.quzhibo.util.ToastUtil;
import com.act.quzhibo.view.MyListView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by asus-pc on 2017/6/11.
 */
public class InteretstPostPageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final LayoutInflater mLayoutInflater;
    private final InterestPost post;
    private InterestPostPageDetailAndComments data;//数据
    private Activity activity;
    private int count;

    public enum ITEM_TYPE {
        ITEM1,
        ITEM2,
        ITEM3,
    }

    //适配器初始化
    public InteretstPostPageAdapter(InterestPost post, Activity context, InterestPostPageDetailAndComments data) {
        this.data = data;
        this.activity = context;
        this.post = post;
        mLayoutInflater = LayoutInflater.from(context);

    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return ITEM_TYPE.ITEM1.ordinal();
        } else if (position == 1) {
            return ITEM_TYPE.ITEM2.ordinal();
        } else if (position == 2) {
            return ITEM_TYPE.ITEM3.ordinal();
        } else {
            return super.getItemViewType(position);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE.ITEM1.ordinal()) {
            return new Item1ViewHolder(mLayoutInflater.inflate(R.layout.post_header_layout, parent, false));
        } else if (viewType == ITEM_TYPE.ITEM2.ordinal()) {
            return new Item2ViewHolder(mLayoutInflater.inflate(R.layout.post_imglist_layout, parent, false));
        } else {
            return new Item3ViewHolder(mLayoutInflater.inflate(R.layout.comments_layout, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int positon) {
        ArrayList<String> pageImgeList = new ArrayList<>();
        ArrayList<String> contentList = new ArrayList<>();
        for (PostContentAndImageDesc des : data.detail.desc) {
            if (des.type != 1) {
                pageImgeList.add(des.value);
            } else {
                contentList.add(des.value);
            }
        }
        if (holder instanceof Item1ViewHolder) {
            if (post.user.sex.equals("2")) {
                Glide.with(activity).load(data.detail.user.photoUrl).asBitmap().placeholder(R.drawable.women).into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        ((Item1ViewHolder) holder).userImage.setBackgroundDrawable(new BitmapDrawable(resource));
                    }

                    @Override
                    public void onLoadStarted(Drawable placeholder) {
                        super.onLoadStarted(placeholder);
                    }
                });
            } else {
                Glide.with(activity).load(data.detail.user.photoUrl).asBitmap().placeholder(R.drawable.man).into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        ((Item1ViewHolder) holder).userImage.setBackgroundDrawable(new BitmapDrawable(resource));
                    }

                    @Override
                    public void onLoadStarted(Drawable placeholder) {
                        super.onLoadStarted(placeholder);
                    }
                });

            }

            ((Item1ViewHolder) holder).userImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.putExtra(Constants.POST, post);
                    intent.putExtra("count", count);
                    intent.setClass(activity, InfoCommonActivity.class);
                    activity.startActivity(intent);
                    count++;
                }
            });

            ((Item1ViewHolder) holder).sexAndAge.setText(data.detail.user.sex);
            long l = System.currentTimeMillis() - data.detail.ctime;
            long day = l / (24 * 60 * 60 * 1000);
            long hour = (l / (60 * 60 * 1000) - day * 24);
            long min = ((l / (60 * 1000)) - day * 24 * 60 - hour * 60);
            if (!data.detail.user.sex.equals("2")) {
                ((Item1ViewHolder) holder).sexAndAge.setBackgroundColor(activity.getResources().getColor(R.color.blue));
            }
            ((Item1ViewHolder) holder).sexAndAge.setText(data.detail.user.sex.equals("2") ? "女" : "男");
            if (day < 365) {
                ((Item1ViewHolder) holder).createTime.setText(day + "天" + hour + "小时" + min + "分钟前");
            }
            ((Item1ViewHolder) holder).nickName.setText(data.detail.user.nick);
            ((Item1ViewHolder) holder).title.setText(data.detail.title);

            StringBuffer sb = new StringBuffer();
            for (String s : contentList) {
                sb.append(s);
            }
            ((Item1ViewHolder) holder).content.setText(sb.toString());
            if (!TextUtils.isEmpty(post.vedioUrl)) {
                ((Item1ViewHolder) holder).videoframlayout.setVisibility(View.VISIBLE);
                new AsyncTask<Void, Void, Bitmap>() {
                    @Override
                    protected Bitmap doInBackground(Void... params) {
                        Bitmap bitmap = createBitmapFromVideoPath(post.vedioUrl);
                        return bitmap;
                    }

                    @Override
                    protected void onPostExecute(Bitmap bitmap) {
                        super.onPostExecute(bitmap);
                        ((Item1ViewHolder) holder).coverUser.setImageBitmap(bitmap);
                        ((Item1ViewHolder) holder).coverplay.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ((Item1ViewHolder) holder).coverUser.setVisibility(View.GONE);
                                ((Item1ViewHolder) holder).coverplay.setVisibility(View.GONE);
                                ((Item1ViewHolder) holder).bar.setVisibility(View.VISIBLE);
                                Uri uri = Uri.parse(post.vedioUrl);
                                ((Item1ViewHolder) holder).videoView.setVideoURI(uri);
                                ((Item1ViewHolder) holder).videoView.start();
                                ((Item1ViewHolder) holder).videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                    @Override
                                    public void onPrepared(MediaPlayer mp) {
                                        ((Item1ViewHolder) holder).bar.setVisibility(View.GONE);
                                    }
                                });
                            }
                        });
                    }
                }.execute();
            }

            new AsyncTask<Void, Void, String>() {
                @Override
                protected String doInBackground(Void... params) {
                    ArrayList<ProvinceAndCityEntify> datas = CommonUtil.parseLocation(activity).data;
                    if (null != datas) {
                        for (ProvinceAndCityEntify entify : datas) {
                            if (TextUtils.equals(data.detail.user.proCode, entify.proId + "")) {
                                for (ProvinceAndCityEntify.CitySub citySub : entify.citySub) {
                                    if (TextUtils.equals(data.detail.user.cityCode, citySub.cityId + "")) {
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
                    ((Item1ViewHolder) holder).arealocation.setText(text);
                }
            }.execute();
        } else if (holder instanceof Item2ViewHolder) {
            ((Item2ViewHolder) holder).listView.setAdapter(new PostImageAdapter(activity, pageImgeList, 1, 1));
        } else {
            PostCommentAdapter adapter = new PostCommentAdapter(activity, data.comments);
            ((Item3ViewHolder) holder).commentsList.setAdapter(adapter);
            ((Item3ViewHolder) holder).pinglunlayout.setVisibility(View.VISIBLE);
            ((Item3ViewHolder) holder).pinglun.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (((Item3ViewHolder) holder).talk.getText().equals("点击这里评论她/他") || ((Item3ViewHolder) holder).talk.getText().length()== 0) {
                        ToastUtil.showToast(activity, "您是否忘记了评论内容?");
                    } else {
                        ToastUtil.showToast(activity, "正在评论...");
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtil.showToast(activity, "评论异常，请稍后重试！");
                                InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                                imm.hideSoftInputFromWindow(((Item3ViewHolder) holder).pinglun.getWindowToken(), 0);
                            }
                        }, 1000);
                    }
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return 3;
    }

    class Item1ViewHolder extends RecyclerView.ViewHolder {

        private ProgressBar bar;
        private ImageView coverplay;
        private ImageView coverUser;
        private TextView arealocation;
        private TextView createTime;
        private ImageView userImage;
        private TextView sexAndAge;
        private TextView nickName;
        private TextView title;
        private io.github.rockerhieu.emojicon.EmojiconTextView content;
        private VideoView videoView;
        private FrameLayout videoframlayout;

        public Item1ViewHolder(View view) {
            super(view);
            videoframlayout = (FrameLayout) view.findViewById(R.id.videoFr);
            bar = (ProgressBar) view.findViewById(R.id.bar);
            videoView = (VideoView) view.findViewById(R.id.videoView);
            coverUser = (ImageView) view.findViewById(R.id.coverUser);
            coverplay = (ImageView) view.findViewById(R.id.coverplay);
            videoView = (VideoView) view.findViewById(R.id.videoView);
            nickName = (TextView) view.findViewById(R.id.nickName);
            createTime = (TextView) view.findViewById(R.id.createTime);
            arealocation = (TextView) view.findViewById(R.id.arealocation);
            userImage = (ImageView) view.findViewById(R.id.userImage);
            sexAndAge = (TextView) view.findViewById(R.id.sexAndAge);
            nickName = (TextView) view.findViewById(R.id.nickName);


            title = (TextView) view.findViewById(R.id.title);
            content = (io.github.rockerhieu.emojicon.EmojiconTextView) view.findViewById(R.id.content);
        }


    }

    class Item2ViewHolder extends RecyclerView.ViewHolder {
        private MyListView listView;

        public Item2ViewHolder(View view) {
            super(view);
            listView = (MyListView) view.findViewById(R.id.imglistview);
        }

    }

    class Item3ViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout pinglunlayout;
        private MyListView commentsList;
        private EditText talk;
        private TextView pinglun;

        public Item3ViewHolder(View view) {
            super(view);
            pinglunlayout = (LinearLayout) view.findViewById(R.id.pinglunlayout);
            talk = (EditText) view.findViewById(R.id.talk);
            pinglun = (TextView) view.findViewById(R.id.pinglun);
            commentsList = (MyListView) view.findViewById(R.id.comments_lv);
        }


    }


    public Bitmap createBitmapFromVideoPath(String url) {
        Bitmap bitmap = null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        int kind = MediaStore.Video.Thumbnails.MINI_KIND;
        try {
            if (Build.VERSION.SDK_INT >= 12) {
                retriever.setDataSource(url, new HashMap<String, String>());
            } else {
                retriever.setDataSource(url);
            }
            bitmap = retriever.getFrameAtTime();
        } catch (IllegalArgumentException ex) {
            // Assume this is a corrupt video file
        } catch (RuntimeException ex) {
            // Assume this is a corrupt video file.
        } finally {
            try {
                retriever.release();
            } catch (RuntimeException ex) {
                // Ignore failures while cleaning up.
            }
        }
        if (kind == MediaStore.Images.Thumbnails.MICRO_KIND && bitmap != null) {
            bitmap = ThumbnailUtils.extractThumbnail(bitmap, 160, 160,
                    ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        }
        return bitmap;
    }
}