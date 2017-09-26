package com.act.quzhibo.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
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

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.bingoogolapple.photopicker.activity.BGAPhotoPreviewActivity;
import cn.bingoogolapple.photopicker.widget.BGANinePhotoLayout;
import io.github.rockerhieu.emojicon.EmojiconEditText;

public class InteretstPostDetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements BGANinePhotoLayout.Delegate {
    private final LayoutInflater mLayoutInflater;
    private final InterestPost post;
    private InterestPostPageDetailAndComments data;
    private Activity activity;
    File downloadDir;

    @Override
    public void onClickNinePhotoItem(BGANinePhotoLayout ninePhotoLayout, View view, int position, String model, List<String> models) {

        if (ninePhotoLayout.getItemCount() == 1) {
            // 预览单张图片
            activity.startActivity(BGAPhotoPreviewActivity.newIntent(activity, downloadDir, ninePhotoLayout.getCurrentClickItem()));
        } else if (ninePhotoLayout.getItemCount() > 1) {
            // 预览多张图片
            activity.startActivity(BGAPhotoPreviewActivity.newIntent(activity, downloadDir, ninePhotoLayout.getData(), ninePhotoLayout.getCurrentClickItemPosition()));
        }
    }

    public enum ITEM_TYPE {
        ITEM1,
        ITEM2,
        ITEM3,
    }

    public InteretstPostDetailAdapter(InterestPost post, Activity context, InterestPostPageDetailAndComments data) {
        this.data = data;
        this.activity = context;
        this.post = post;
        mLayoutInflater = LayoutInflater.from(context);
        downloadDir = new File(Environment.getExternalStorageDirectory(), "PhotoPickerDownload");
        if (!downloadDir.exists()) {
            downloadDir.mkdirs();
        }
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
                Glide.with(activity).load(data.detail.user.photoUrl).asBitmap().placeholder(R.drawable.women).error(R.drawable.error_img).into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        ((Item1ViewHolder) holder).userImage.setBackgroundDrawable(new BitmapDrawable(resource));
                    }

                    @Override
                    public void onLoadStarted(Drawable placeholder) {
                        super.onLoadStarted(placeholder);
                    }

                    @Override
                    public void onLoadFailed(Exception e, Drawable errorDrawable) {
                        super.onLoadFailed(e, errorDrawable);
                        ((Item1ViewHolder) holder).userImage.setBackgroundDrawable(errorDrawable);

                    }
                });
            } else {
                Glide.with(activity).load(data.detail.user.photoUrl).asBitmap().error(R.drawable.error_img).placeholder(R.drawable.man).into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        ((Item1ViewHolder) holder).userImage.setBackgroundDrawable(new BitmapDrawable(resource));
                    }

                    @Override
                    public void onLoadStarted(Drawable placeholder) {
                        super.onLoadStarted(placeholder);
                    }

                    @Override
                    public void onLoadFailed(Exception e, Drawable errorDrawable) {
                        super.onLoadFailed(e, errorDrawable);
                        ((Item1ViewHolder) holder).userImage.setBackgroundDrawable(errorDrawable);

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
                newString = newString.replaceAll(":" + matcher.group().trim() + ":", "<img src='" + MyApplicaition.emotionsKeySrc.get(":" + matcher.group().trim() + ":") + "'>");
            }
            if (newString.contains("null")) {
                newString = newString.replaceAll("null", R.drawable.kissing_heart + "");
            }
            ((Item1ViewHolder) holder).content.setText(Html.fromHtml(newString, new Html.ImageGetter() {
                @Override
                public Drawable getDrawable(String source) {
                    Drawable drawable = null;
                    if (!TextUtils.isEmpty(source) && !source.equals("null")) {
                        int id = Integer.parseInt(source);
                        drawable = activity.getResources().getDrawable(id);
                        if (drawable != null) {
                            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
                                    drawable.getIntrinsicHeight());
                        }
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
                    String text = MyApplicaition.proKeySrc.get(data.detail.user.proCode) + MyApplicaition.cityKeySrc.get(data.detail.user.cityCode);
                    return text;
                }

                @Override
                protected void onPostExecute(String text) {
                    super.onPostExecute(text);
                    ((Item1ViewHolder) holder).arealocation.setText(text);
                }
            }.execute();

        } else if (holder instanceof Item2ViewHolder) {
            ((Item2ViewHolder) holder).ninePhotoLayout.setDelegate(this);
            ((Item2ViewHolder) holder).ninePhotoLayout.setData(pageImgeList);
        } else {
            PostCommentAdapter adapter = new PostCommentAdapter(activity, data.comments);
            ((Item3ViewHolder) holder).commentsList.setAdapter(adapter);
            ((Item3ViewHolder) holder).commentBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (((Item3ViewHolder) holder).commentET.getText().equals("点击这里评论她/他") || ((Item3ViewHolder) holder).commentET.getText().length() == 0) {
                        ToastUtil.showToast(activity, "您是否忘记了评论内容?");
                    } else {
                        ToastUtil.showToast(activity, "正在评论...");
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtil.showToast(activity, "评论已提交审核");
                                ((Item3ViewHolder) holder).commentET.setText("");
                                InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                                imm.hideSoftInputFromWindow(((Item3ViewHolder) holder).commentBtn.getWindowToken(), 0);
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
        private BGANinePhotoLayout ninePhotoLayout;

        public Item2ViewHolder(View view) {
            super(view);
            ninePhotoLayout = (BGANinePhotoLayout) view.findViewById(R.id.imglistview);
        }

    }

    class Item3ViewHolder extends RecyclerView.ViewHolder {
        private MyListView commentsList;
        private EmojiconEditText commentET;
        private TextView commentBtn;

        public Item3ViewHolder(View view) {
            super(view);
            commentET = (EmojiconEditText) view.findViewById(R.id.comment_et);
            commentBtn = (TextView) view.findViewById(R.id.commentBtn);
            commentsList = (MyListView) view.findViewById(R.id.comments_lv);
        }


    }

}