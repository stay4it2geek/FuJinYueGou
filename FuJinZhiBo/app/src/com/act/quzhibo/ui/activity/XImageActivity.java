package com.act.quzhibo.ui.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
    private int photoIndex;

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
        final Snackbar snackbar = Snackbar.make(findViewById(R.id.container), "双击或双指按住拉伸可放大", Snackbar.LENGTH_LONG);
        snackbar.setAction("知道了", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar.dismiss();
            }
        }).setDuration(3000).show();

        loadNetView.setReloadButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadNetView.setlayoutVisily(Constants.LOAD);
                onResume();
            }
        });
        photoIndex = getIntent().getIntExtra("position", 0);
        mPager.setCurrentItem(photoIndex);
        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                photoIndex = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        findViewById(R.id.download_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MediaInfo mediaInfo = mediaInfos.get(photoIndex);
                String url = mediaInfos.get(photoIndex).getUrl();
                DownloadInfo downloadInfo = downloadManager.getDownloadById(url.hashCode());

                if (isSdCardExist) {
                    File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), Constants.PHOTO_DOWNLOAD);
                    if (!file.exists()) {
                        file.mkdirs();
                    }
                    String path = file.getAbsolutePath().concat("/").concat(mediaInfo.getName());
                    File localFile = new File(path);

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
                            if (downloadManager.findAllDownloaded().size() > 20) {
                                FragmentDialog.newInstance(false, "", "已下载任务最多20个，请清除掉一些吧", "确定", "取消", -1, false, new FragmentDialog.OnClickBottomListener() {
                                    @Override
                                    public void onPositiveClick(Dialog dialog, boolean needDelete) {
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
                    View view = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.imglayout, null);
                    final XImageView xImageView = (XImageView) view.findViewById(R.id.ximageview);
                    xImageView.setDoubleTapScaleType(IXImageView.DoubleType.FIT_VIEW_MIN_VIEW_MAX);
                    xImageView.setInitType(IXImageView.InitType.FIT_VIEW_MIN_IMAGE_MIN);

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
                            xImageView.setImage(BitmapFactory.decodeResource(XImageActivity.this.getResources(),R.drawable.error_img));

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
        mPager.setCurrentItem(photoIndex);
        loadNetView.setVisibility(View.GONE);
    }


    class MyDownloadListener implements DownloadListener {
        DownloadInfo downloadInfo;

        public MyDownloadListener(DownloadInfo downloadInfo) {
            this.downloadInfo = downloadInfo;
        }

        @Override
        public void onStart() {

        }

        @Override
        public void onWaited() {

        }

        @Override
        public void onPaused() {

        }

        @Override
        public void onDownloading(long progress, long size) {

        }

        @Override
        public void onRemoved() {
            downloadInfo = null;
        }

        @Override
        public void onDownloadSuccess() {
            adapter.notifyDataSetChanged();
            ToastUtil.showToast(XImageActivity.this, "图片已保存在" + downloadInfo.getPath());
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
                    mediaInfo.getIcon(), mediaInfo.getUrl(), mediaInfo.getType(), mediaInfo.getTitle(), mediaInfo.getLocalPath());
            try {
                dbController.createOrUpdateMyDownloadInfo(myBusinessInfLocal);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return downloadInfo;
    }
}

