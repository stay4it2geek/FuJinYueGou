package com.act.quzhibo.common;

import android.app.Application;
import android.os.Environment;
import android.util.Log;

import org.wlf.filedownloader.FileDownloadConfiguration;
import org.wlf.filedownloader.FileDownloader;

import java.io.File;
import java.util.HashMap;

import c.b.BP;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobConfig;

public class MyApplicaition extends Application {

    public static HashMap<String, Long> map;

    public void onCreate() {
        super.onCreate();

        // init FileDownloader
        initFileDownloader();



        BmobConfig config = new BmobConfig.Builder(this)
                //设置appkey
                .setApplicationId("227399ddef86ccfa859443473306c43a")
                //请求超时时间（单位为秒）：默认15s
                .setConnectTimeout(20)
                //文件分片上传时每片的大小（单位字节），默认512*1024
                .setUploadBlockSize(1024 * 1024)
                //文件的过期时间(单位为秒)：默认1800s
                .setFileExpiration(2500)
                .build();
        Bmob.initialize(config);
        BP.init("e37264d2646046d9158d3800afd548f3");
    }

    @Override
    public void onTerminate() {
        super.onTerminate();

        // release FileDownloader
        releaseFileDownloader();
    }

    // init FileDownloader
    private void initFileDownloader() {

        // 1.create FileDownloadConfiguration.Builder
        FileDownloadConfiguration.Builder builder = new FileDownloadConfiguration.Builder(this);

        // 2.config FileDownloadConfiguration.Builder
        // config the download path
        builder.configFileDownloadDir(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator +
                "FileDownloader");
        // builder.configFileDownloadDir("/storage/sdcard1/FileDownloader2");

        Log.e("path",Environment.getExternalStorageDirectory().getAbsolutePath());
        // allow 3 download task at the same time
        builder.configDownloadTaskSize(3);

        // config retry download times when failed
        builder.configRetryDownloadTimes(5);

        // enable debug mode
        //builder.configDebugMode(true);

        // config connect timeout
        builder.configConnectTimeout(25000); // 25s

        // 3.init FileDownloader with the configuration
        // build FileDownloadConfiguration with the builder
        FileDownloadConfiguration configuration = builder.build();
        FileDownloader.init(configuration);
    }

    // release FileDownloader
    private void releaseFileDownloader() {
        FileDownloader.release();
    }

}