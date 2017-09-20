package com.act.quzhibo.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.act.quzhibo.MyStandardVideoController;
import com.act.quzhibo.common.MyApplicaition;
import com.act.quzhibo.entity.ProvinceAndCityEntity;
import com.act.quzhibo.R;
import com.act.quzhibo.common.Constants;
import com.act.quzhibo.entity.InterestPost;
import com.act.quzhibo.entity.InterestPostPageDetailAndComments;
import com.act.quzhibo.entity.PostContentAndImageDesc;
import com.act.quzhibo.ui.activity.InfoInterestPersonActivity;
import com.act.quzhibo.util.CommonUtil;
import com.act.quzhibo.util.ToastUtil;
import com.act.quzhibo.view.MyListView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.devlin_n.videoplayer.player.IjkVideoView;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.github.rockerhieu.emojicon.EmojiconEditText;

public class InteretstPostPageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final LayoutInflater mLayoutInflater;
    private final InterestPost post;
    private InterestPostPageDetailAndComments data;//数据
    private Activity activity;

    public enum ITEM_TYPE {
        ITEM1,
        ITEM2,
        ITEM3,
    }

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
            return new Item1ViewHolder(mLayoutInflater.inflate(R.layout.post_detail_header_layout, parent, false));
        } else if (viewType == ITEM_TYPE.ITEM2.ordinal()) {
            return new Item2ViewHolder(mLayoutInflater.inflate(R.layout.post_detail_imgs_layout, parent, false));
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
                    intent.setClass(activity, InfoInterestPersonActivity.class);
                    activity.startActivity(intent);
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
            if (day < 50) {
                ((Item1ViewHolder) holder).createTime.setText(day + "天" + hour + "小时" + min + "分钟前");
            } else {
                ((Item1ViewHolder) holder).createTime.setText("N天" + hour + "小时" + min + "分钟前");
            }
            String nick = data.detail.user.nick.replaceAll("\r|\n", "");
            ((Item1ViewHolder) holder).nickName.setText(nick);
            ((Item1ViewHolder) holder).title.setText(data.detail.title);

            StringBuffer sb = new StringBuffer();
            for (String s : contentList) {
                sb.append(s);
            }
            String newString = sb.toString();
            Pattern pattern = Pattern.compile("[a-z_]{1,}", Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(newString);
            while (matcher.find()) {
                newString = newString.replaceAll(":"+matcher.group().trim()+":", "<img src='" + MyApplicaition.emotionsKeySrc.get(":"+matcher.group().trim()+":") + "'>");
            }
            new Handler().post(new Runnable() {
                @Override
                public void run() {

                }
            });
            ((Item1ViewHolder) holder).content.setText(Html.fromHtml(newString, new Html.ImageGetter() {
                @Override
                public Drawable getDrawable(String source) {
                    Drawable drawable = null;
                    if (source != null) {
                        int id = Integer.parseInt(source);
                        drawable = activity.getResources().getDrawable(id);
                        if(drawable!=null){
                            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
                                    drawable.getIntrinsicHeight());}
                    }
                    return drawable;
                }
            }, null));
            if (!TextUtils.isEmpty(post.vedioUrl)) {
                new AsyncTask<Void, Void, Bitmap>() {
                    @Override
                    protected Bitmap doInBackground(Void... params) {
                        Bitmap bitmap = CommonUtil.createBitmapFromVideoPath(post.vedioUrl);
                        return bitmap;
                    }

                    @Override
                    protected void onPostExecute(Bitmap bitmap) {
                        super.onPostExecute(bitmap);
                        ((Item1ViewHolder) holder).controller.getThumb().setImageBitmap(bitmap);
                        ((Item1ViewHolder) holder).ijkVideoView
                                .enableCache()
                                .addToPlayerManager()
                                .setUrl(post.vedioUrl)
                                .setTitle(post.title)
                                .setVideoController(((Item1ViewHolder) holder).controller);

                    }
                }.execute();
            } else {
                ((Item1ViewHolder) holder).ijkVideoView.setVisibility(View.GONE);
            }

            new AsyncTask<Void, Void, String>() {
                @Override
                protected String doInBackground(Void... params) {
                    ArrayList<ProvinceAndCityEntity> datas = CommonUtil.parseLocation(activity).data;
                    if (null != datas) {
                        for (ProvinceAndCityEntity entify : datas) {
                            if (TextUtils.equals(data.detail.user.proCode, entify.proId + "")) {
                                for (ProvinceAndCityEntity.CitySub citySub : entify.citySub) {
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
            ((Item2ViewHolder) holder).listView.setAdapter(new PostImageAdapter(activity, pageImgeList, Constants.ITEM_POST_DETAIL_IMG));
        } else {
            PostCommentAdapter adapter = new PostCommentAdapter(activity, data.comments);
            ((Item3ViewHolder) holder).commentsList.setAdapter(adapter);
            ((Item3ViewHolder) holder).pinglunlayout.setVisibility(View.VISIBLE);
            ((Item3ViewHolder) holder).pinglun.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (((Item3ViewHolder) holder).talk.getText().equals("点击这里评论她/他") || ((Item3ViewHolder) holder).talk.getText().length() == 0) {
                        ToastUtil.showToast(activity, "您是否忘记了评论内容?");
                    } else {
                        ToastUtil.showToast(activity, "正在评论...");
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtil.showToast(activity, "评论已提交审核");
                                ((Item3ViewHolder) holder).talk.setText("");
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

        private TextView arealocation;
        private TextView createTime;
        private ImageView userImage;
        private TextView sexAndAge;
        private TextView nickName;
        private TextView title;
        private io.github.rockerhieu.emojicon.EmojiconTextView content;
        private IjkVideoView ijkVideoView;
        private MyStandardVideoController controller;

        public Item1ViewHolder(View view) {
            super(view);
            nickName = (TextView) view.findViewById(R.id.nickName);
            createTime = (TextView) view.findViewById(R.id.createTime);
            arealocation = (TextView) view.findViewById(R.id.location);
            userImage = (ImageView) view.findViewById(R.id.userImage);
            sexAndAge = (TextView) view.findViewById(R.id.sexAndAge);
            nickName = (TextView) view.findViewById(R.id.nickName);
            ijkVideoView = (IjkVideoView) itemView.findViewById(R.id.video_player);
            int widthPixels = activity.getResources().getDisplayMetrics().widthPixels;
            ijkVideoView.setLayoutParams(new RelativeLayout.LayoutParams(widthPixels, widthPixels / 14 * 9));
            controller = new MyStandardVideoController(activity);
            controller.setInitData(false, true);
            ijkVideoView.setVideoController(controller);
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
        private EmojiconEditText talk;
        private TextView pinglun;

        public Item3ViewHolder(View view) {
            super(view);
            pinglunlayout = (LinearLayout) view.findViewById(R.id.pinglunlayout);
            talk = (EmojiconEditText) view.findViewById(R.id.talk);
            pinglun = (TextView) view.findViewById(R.id.pinglun);
            commentsList = (MyListView) view.findViewById(R.id.comments_lv);
        }


    }


}