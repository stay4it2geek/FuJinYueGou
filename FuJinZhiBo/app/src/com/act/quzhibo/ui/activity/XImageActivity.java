package com.act.quzhibo.ui.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.act.quzhibo.R;
import com.act.quzhibo.common.Constants;
import com.act.quzhibo.download.activity.DownloadManagerActivity;
import com.act.quzhibo.download.db.DBController;
import com.act.quzhibo.download.domain.MediaInfo;
import com.act.quzhibo.download.domain.MediaInfoLocal;
import com.act.quzhibo.util.ToastUtil;
import com.act.quzhibo.view.FragmentDialog;
import com.act.quzhibo.view.LoadNetView;
import com.act.quzhibo.view.TitleBarView;
import com.act.quzhibo.view.xImageView.IXImageView;
import com.act.quzhibo.view.xImageView.XImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;

import cn.woblog.android.downloader.DownloadService;
import cn.woblog.android.downloader.callback.DownloadListener;
import cn.woblog.android.downloader.callback.DownloadManager;
import cn.woblog.android.downloader.domain.DownloadInfo;
import cn.woblog.android.downloader.exception.DownloadException;

import static cn.woblog.android.downloader.domain.DownloadInfo.STATUS_WAIT;


public class XImageActivity extends AppCompatActivity {

    private ViewPager mPager;
    private boolean isSdCardExist;
    private DownloadManager downloadManager;
    private PagerAdapter adapter;
    private DBController dbController;
    private LoadNetView loadNetView;
    private ArrayList<MediaInfo> mediaInfos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pager);
        TitleBarView titlebar = (TitleBarView) findViewById(R.id.titlebar);
        titlebar.setBarTitle("高 清 私 照");
        titlebar.setBackButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                XImageActivity.this.finish();
            }
        });
        loadNetView = (LoadNetView) findViewById(R.id.loadview);
        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setPageMargin((int) (getResources().getDisplayMetrics().density * 15));
        final Snackbar snackbar = Snackbar.make(findViewById(R.id.container), "长按保存,双击或双指按住拉伸放大", Snackbar.LENGTH_LONG);
        snackbar.setAction("知道了", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar.dismiss();
            }
        }).setDuration(7000).show();

        loadNetView.setReloadButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadNetView.setlayoutVisily(Constants.LOAD);
                onResume();
            }
        });
    }



    @Override
    protected void onResume() {
        super.onResume();

        adapter = null;
        mediaInfos = getIntent().getParcelableArrayListExtra("mediaList");
        if (mediaInfos.size() > 0) {
            downloadManager = DownloadService.getDownloadManager(getApplicationContext());
            isSdCardExist = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);// 判断sdcard是否存在
            try {
                dbController = DBController.getInstance(this.getApplicationContext());
            } catch (SQLException e) {
                e.printStackTrace();
            }
            mPager.setAdapter(adapter = new PagerAdapter() {
                @Override
                public int getCount() {
                    return mediaInfos.size();
                }

                @Override
                public boolean isViewFromObject(View view, Object object) {
                    return view == object;
                }

                @Override
                public Object instantiateItem(ViewGroup container, final int position) {
                    String url = mediaInfos.get(position).getUrl();
                    DownloadInfo downloadInfo = downloadManager.getDownloadById(url.hashCode());
                    View view = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.imglayout, null);
                    final XImageView xImageView = (XImageView) view.findViewById(R.id.ximageview);
                    xImageView.setDoubleTapScaleType(IXImageView.DoubleType.FIT_VIEW_MIN_VIEW_MAX);
                    xImageView.setInitType(IXImageView.InitType.FIT_VIEW_MIN_IMAGE_MIN);
                    xImageView.setActionListener(new MyActionListner(mediaInfos.get(position), downloadInfo, url));

                    Glide.with(XImageActivity.this).load(url).asBitmap().skipMemoryCache(false).placeholder(R.drawable.placehoder_img).into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                            xImageView.setImage(resource);
                        }

                        @Override
                        public void onLoadStarted(Drawable placeholder) {
                            super.onLoadStarted(placeholder);
                        }

                        @Override
                        public void onLoadFailed(Exception e, Drawable errorDrawable) {
                            super.onLoadFailed(e, errorDrawable);
                            loadNetView.setlayoutVisily(Constants.RELOAD);
                        }
                    });

                    container.addView(view);
                    return view;
                }

                @Override
                public void destroyItem(ViewGroup container, int position, Object object) {
                    container.removeView((View) object);
                }
            });
        }
        mPager.setCurrentItem(getIntent().getIntExtra("position", 0));
        loadNetView.setVisibility(View.GONE);
    }

    class MyActionListner implements XImageView.OnActionListener {
        DownloadInfo downloadInfo;
        MediaInfo mediaInfo;
        String url;

        public MyActionListner(MediaInfo mediaInfo, DownloadInfo downloadInfo, String url) {
            this.downloadInfo = downloadInfo;
            this.mediaInfo = mediaInfo;
            this.url = url;
        }

        @Override
        public void onSingleTapped(XImageView view, MotionEvent event, boolean onImage) {
        }

        @Override
        public boolean onDoubleTapped(XImageView view, MotionEvent event) {
            return false;
        }

        @Override
        public void onLongPressed(XImageView view, MotionEvent event) {
            if (isSdCardExist) {
                File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), Constants.PHOTO_DOWNLOAD);
                if (!file.exists()) {
                    file.mkdirs();
                }
                String path = file.getAbsolutePath().concat("/").concat(mediaInfo.getName());
                File localFile = new File(path);
                if (downloadInfo != null) {
                    ToastUtil.showToast(XImageActivity.this, downloadInfo.getStatus() + "");
                }

                if (localFile.exists() && localFile.isFile() && downloadInfo != null) {
                    switch (downloadInfo.getStatus()) {
                        case DownloadInfo.STATUS_NONE:
                        case DownloadInfo.STATUS_PAUSED:
                        case DownloadInfo.STATUS_ERROR:
                            downloadManager.resume(downloadInfo);
                            break;
                        case DownloadInfo.STATUS_DOWNLOADING:
                        case DownloadInfo.STATUS_PREPARE_DOWNLOAD:
                        case STATUS_WAIT:
                            downloadManager.pause(downloadInfo);
                            break;
                        case DownloadInfo.STATUS_COMPLETED:
                            ToastUtil.showToast(XImageActivity.this, "您已经保存过该照片");
                            break;
                    }
                } else {
                    if (downloadManager.findAllDownloading().size() > 10) {
                        ToastUtil.showToast(XImageActivity.this, "下载任务最多10个,请稍后下载");
                        if (downloadManager.findAllDownloaded().size() >20) {
                            FragmentDialog.newInstance(false,"",  "已下载任务最多20个，请清除掉一些吧", "确定", "取消", -1, false, new FragmentDialog.OnClickBottomListener() {
                                @Override
                                public void onPositiveClick(Dialog dialog,boolean needDelete) {
                                    Intent photoIntent = new Intent();
                                    photoIntent.putExtra(Constants.DOWN_LOAD_TYPE, Constants.PHOTO_ALBUM);
                                    photoIntent.setClass(XImageActivity.this, DownloadManagerActivity.class);
                                    startActivity(photoIntent);
                                    dialog.dismiss();
                                }
                                @Override
                                public void onNegtiveClick(Dialog dialog) {
                                    dialog.dismiss();
                                }
                            }).show(getSupportFragmentManager(), "");
                        }
                    } else {
                        createDownload(mediaInfo, url);
                    }
                }
            }
        }

        @Override
        public void onSetImageFinished(XImageView view, boolean success, Rect image) {
        }
    }

    class MyDownloadListener implements DownloadListener {
        DownloadInfo downloadInfo;

        public MyDownloadListener(DownloadInfo downloadInfo) {
            this.downloadInfo = downloadInfo;
        }

        @Override
        public void onStart() {
            ToastUtil.showToast(XImageActivity.this, "开始保存");
        }

        @Override
        public void onWaited() {
            ToastUtil.showToast(XImageActivity.this, "等待保存");
        }

        @Override
        public void onPaused() {
            ToastUtil.showToast(XImageActivity.this, "暂停保存");
        }

        @Override
        public void onDownloading(long progress, long size) {
            ToastUtil.showToast(XImageActivity.this, "正在保存");
        }

        @Override
        public void onRemoved() {
            ToastUtil.showToast(XImageActivity.this, "已删除保存任务");
            downloadInfo = null;
        }

        @Override
        public void onDownloadSuccess() {
            adapter.notifyDataSetChanged();
            ToastUtil.showToast(XImageActivity.this, "保存成功");
        }

        @Override
        public void onDownloadFailed(DownloadException e) {
            ToastUtil.showToast(XImageActivity.this, "保存失败，原因是：" + e.getMessage());
        }
    }

    private DownloadInfo createDownload(MediaInfo mediaInfo, String url) {
        DownloadInfo downloadInfo = null;
        if (isSdCardExist) {
            File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), Constants.PHOTO_DOWNLOAD);
            if (!file.exists()) {
                file.mkdirs();
            }
            String path = file.getAbsolutePath().concat("/").concat(mediaInfo.getName());
            File localFile = new File(path);

            if (localFile.isFile() && localFile.exists()) {
                file.delete();
            }
            downloadInfo = new DownloadInfo.Builder().setUrl(url).setPath(path).build();
            downloadInfo.setDownloadListener(new MyDownloadListener(downloadInfo));
            downloadManager.download(downloadInfo);
            //save extra info to my database.
            MediaInfoLocal myBusinessInfLocal = new MediaInfoLocal(
                    mediaInfo.getUrl().hashCode(), mediaInfo.getName(),
                    mediaInfo.getIcon(), mediaInfo.getUrl(), mediaInfo.getType(), mediaInfo.getTitle(),mediaInfo.getLocalPath());
            try {
                dbController.createOrUpdateMyDownloadInfo(myBusinessInfLocal);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return downloadInfo;
    }
}

