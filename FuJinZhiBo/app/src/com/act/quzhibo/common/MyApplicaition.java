package com.act.quzhibo.common;

import android.app.Application;

import c.b.BP;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobConfig;

public class MyApplicaition extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
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

}
