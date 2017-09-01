package com.act.quzhibo.ui.activity;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.annotation.XmlRes;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.act.quzhibo.R;
import com.act.quzhibo.adapter.InterestPostListAdapter;
import com.act.quzhibo.common.Constants;
import com.act.quzhibo.download.adapter.DownloadListAdapter;
import com.act.quzhibo.download.callback.MyDownloadListener;
import com.act.quzhibo.download.domain.MediaInfoLocal;
import com.act.quzhibo.download.util.FileUtil;
import com.act.quzhibo.stackblur.StackBlurManager;
import com.act.quzhibo.util.CommonUtil;
import com.act.quzhibo.util.ToastUtil;
import com.act.quzhibo.view.FragmentDialog;
import com.act.quzhibo.view.xImageView.IXImageView;
import com.act.quzhibo.view.xImageView.XImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.io.File;
import java.lang.ref.SoftReference;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.XMLFormatter;

import cn.bmob.v3.b.V;
import cn.woblog.android.downloader.DownloadService;
import cn.woblog.android.downloader.callback.DownloadListener;
import cn.woblog.android.downloader.callback.DownloadManager;
import cn.woblog.android.downloader.domain.DownloadInfo;
import cn.woblog.android.downloader.exception.DownloadException;

import static cn.woblog.android.downloader.domain.DownloadInfo.STATUS_COMPLETED;
import static cn.woblog.android.downloader.domain.DownloadInfo.STATUS_REMOVED;
import static cn.woblog.android.downloader.domain.DownloadInfo.STATUS_WAIT;

/**
 * Created by weiminglin on 17/9/1.
 */

public class XImageActivity extends FragmentActivity {

    private ViewPager mPager;

    boolean isSdCardExist;
    private DownloadManager downloadManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pager);
        final ArrayList<String> urls = getIntent().getStringArrayListExtra("imgUrls");
        downloadManager = DownloadService.getDownloadManager(getApplicationContext());
        isSdCardExist = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);// 判断sdcard是否存在

        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setPageMargin((int) (getResources().getDisplayMetrics().density * 15));
        mPager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return urls.size();
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            @Override
            public Object instantiateItem(ViewGroup container, final int position) {

                final DownloadInfo downloadInfo = downloadManager.getDownloadById(urls.get(position).hashCode());


                View view = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.imglayout, null);
                final Button bt_action = (Button) view.findViewById(R.id.bt_action);
                final TextView tv_status = (TextView) view.findViewById(R.id.tv_status);
                final ProgressBar pb = (ProgressBar) view.findViewById(R.id.pb);
                refresh(downloadInfo, tv_status, pb, bt_action);
                final XImageView xImageView = (XImageView) view.findViewById(R.id.ximageview);
                xImageView.setDoubleTapScaleType(IXImageView.DoubleType.FIT_VIEW_MIN_VIEW_MAX);
                xImageView.setInitType(IXImageView.InitType.FIT_VIEW_MIN_IMAGE_MIN);

                bt_action.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (downloadInfo != null) {

                            switch (downloadInfo.getStatus()) {
                                case DownloadInfo.STATUS_NONE:
                                case DownloadInfo.STATUS_PAUSED:
                                case DownloadInfo.STATUS_ERROR:

                                    //resume downloadInfo
                                    downloadManager.resume(downloadInfo);
                                    break;

                                case DownloadInfo.STATUS_DOWNLOADING:
                                case DownloadInfo.STATUS_PREPARE_DOWNLOAD:
                                case STATUS_WAIT:
                                    //pause downloadInfo
                                    downloadManager.pause(downloadInfo);
                                    break;
                                case DownloadInfo.STATUS_COMPLETED:
                                    downloadManager.remove(downloadInfo);
                                    break;
                            }
                        } else {
//            createDownload(urls.get(position),)
//
//            //save extra info to my database.
//            MediaInfoLocal mediaInfoLocal = new MediaInfoLocal(
//                    data.getUrl().hashCode(), data.getName(), data.getIcon(), data.getUrl());
//            try {
//                dbController.createOrUpdateMyDownloadInfo(mediaInfoLocal);
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
                        }
                    }
                });
                Glide.with(XImageActivity.this).load(urls.get(position)).asBitmap().skipMemoryCache(false).placeholder(R.drawable.xiangjiao).into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        xImageView.setImage(resource);
                    }

                    @Override
                    public void onLoadStarted(Drawable placeholder) {
                        super.onLoadStarted(placeholder);
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
        mPager.setCurrentItem(getIntent().getIntExtra("position", 0));
    }


    private void refresh(DownloadInfo downloadInfo, TextView tv_status, ProgressBar pb, Button bt_action) {
        if (downloadInfo == null) {
            pb.setProgress(0);
            bt_action.setText("Download");
            tv_status.setText("not downloadInfo");
        } else {
            switch (downloadInfo.getStatus()) {
                case DownloadInfo.STATUS_NONE:
                    bt_action.setText("Download");
                    tv_status.setText("not downloadInfo");
                    break;
                case DownloadInfo.STATUS_PAUSED:
                case DownloadInfo.STATUS_ERROR:
                    bt_action.setText("Continue");
                    tv_status.setText("paused");
                    try {
                        pb.setProgress((int) (downloadInfo.getProgress() * 100.0 / downloadInfo.getSize()));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;

                case DownloadInfo.STATUS_DOWNLOADING:
                case DownloadInfo.STATUS_PREPARE_DOWNLOAD:
                    bt_action.setText("Pause");
                    try {
                        pb.setProgress((int) (downloadInfo.getProgress() * 100.0 / downloadInfo.getSize()));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    tv_status.setText("downloading");
                    break;
                case STATUS_COMPLETED:
                    bt_action.setText("Delete");
                    try {
                        pb.setProgress((int) (downloadInfo.getProgress() * 100.0 / downloadInfo.getSize()));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    tv_status.setText("success");
                    break;
                case STATUS_REMOVED:

                    pb.setProgress(0);
                    bt_action.setText("Download");
                    tv_status.setText("not downloadInfo");
                    break;
                case STATUS_WAIT:
                    pb.setProgress(0);
                    bt_action.setText("Pause");
                    tv_status.setText("Waiting");
                    break;
            }

        }
    }

    DownloadInfo downloadInfo;

    private DownloadInfo createDownload(String url, final TextView tv_status, final ProgressBar pb, final Button bt_action) {
        String filepath = Environment.getExternalStorageDirectory().getAbsolutePath() +
                File.separator +
                "photoDownload" +
                File.separator +
                url.substring(url.length() - 15, url.length());
        File file = new File(filepath);
        if (!file.exists()) {
            file.mkdirs();
        }
        downloadInfo = new DownloadInfo.Builder().setUrl(url).setPath(filepath).build();
        downloadInfo.setDownloadListener(new DownloadListener() {

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
                bt_action.setText(FileUtil.formatFileSize(progress) + "/" + FileUtil
                        .formatFileSize(size));

            }

            @Override
            public void onRemoved() {
                downloadInfo = null;
            }

            @Override
            public void onDownloadSuccess() {
                ToastUtil.showToast(XImageActivity.this, "下载成功");

                refresh(downloadInfo, tv_status, pb, bt_action);
            }

            @Override
            public void onDownloadFailed(DownloadException e) {
            }
        });
        downloadManager.download(downloadInfo);
        return downloadInfo;
    }


}