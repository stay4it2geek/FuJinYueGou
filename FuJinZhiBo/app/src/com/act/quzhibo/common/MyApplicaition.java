package com.act.quzhibo.common;

import android.app.Application;

import com.act.quzhibo.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;

import c.b.BP;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobConfig;

public class MyApplicaition extends Application {

    public static HashMap<String, Long> map;
    public static final LinkedHashMap<Integer, String> emotionsKeySrc = new LinkedHashMap();
    public void onCreate() {
        super.onCreate();
        BmobConfig config = new BmobConfig.Builder(this)
                .setApplicationId("227399ddef86ccfa859443473306c43a")
                .setConnectTimeout(20)
                .setUploadBlockSize(1024 * 1024)
                .setFileExpiration(2500)
                .build();
        Bmob.initialize(config);
        BP.init("e37264d2646046d9158d3800afd548f3");
    }
    static {
        emotionsKeySrc.put(R.drawable.joy, ":joy:");
        emotionsKeySrc.put(R.drawable.kissing_heart, ":kissing_heart:");
        emotionsKeySrc.put(R.drawable.kissing_smiling_eyes, ":kissing_smiling_eyes:");
        emotionsKeySrc.put(R.drawable.laughing, ":laughing:");
        emotionsKeySrc.put(R.drawable.logo_uc, ":logo_uc:");
        emotionsKeySrc.put(R.drawable.lollipop, ":lollipop:");
        emotionsKeySrc.put(R.drawable.mask, ":mask:");
        emotionsKeySrc.put(R.drawable.muscle, ":muscle:");
        emotionsKeySrc.put(R.drawable.ok_hand, ":ok_hand:");
        emotionsKeySrc.put(R.drawable.pensive, ":pensive:");
        emotionsKeySrc.put(R.drawable.persevere, ":persevere:");
        emotionsKeySrc.put(R.drawable.pig, ":pig:");
        emotionsKeySrc.put(R.drawable.point_up, ":point_up:");
        emotionsKeySrc.put(R.drawable.police_car, ":police_car:");
        emotionsKeySrc.put(R.drawable.pray, ":pray:");
        emotionsKeySrc.put(R.drawable.punch, ":punch:");
        emotionsKeySrc.put(R.drawable.racehorse, ":racehorse:");
        emotionsKeySrc.put(R.drawable.rage, ":rage:");
        emotionsKeySrc.put(R.drawable.ramen, ":ramen:");
        emotionsKeySrc.put(R.drawable.ring, ":ring:");
        emotionsKeySrc.put(R.drawable.rose, ":rose:");
        emotionsKeySrc.put(R.drawable.say_hello, ":say_hello:");
        emotionsKeySrc.put(R.drawable.scream, ":scream:");
        emotionsKeySrc.put(R.drawable.sleeping, ":sleeping:");
        emotionsKeySrc.put(R.drawable.sleepy, ":sleepy:");
        emotionsKeySrc.put(R.drawable.smile, ":smile:");
        emotionsKeySrc.put(R.drawable.smiley, ":smiley:");
        emotionsKeySrc.put(R.drawable.smiling_imp, ":smiling_imp:");
        emotionsKeySrc.put(R.drawable.smirk, ":smirk:");
        emotionsKeySrc.put(R.drawable.snake, ":snake:");
        emotionsKeySrc.put(R.drawable.sob, ":sob:");
        emotionsKeySrc.put(R.drawable.snake, ":snake:");
        emotionsKeySrc.put(R.drawable.strawberry, ":strawberry:");
        emotionsKeySrc.put(R.drawable.stuck_out_tongue, ":stuck_out_tongue:");
        emotionsKeySrc.put(R.drawable.stuck_out_tongue_winking_eye, ":stuck_out_tongue_winking_eye:");
        emotionsKeySrc.put(R.drawable.stuck_out_tongue_closed_eyes, ":stuck_out_tongue_closed_eyes:");
        emotionsKeySrc.put(R.drawable.sunglasses, ":sunglasses:");
        emotionsKeySrc.put(R.drawable.sunny, ":sunny:");
        emotionsKeySrc.put(R.drawable.sweat, ":sweat:");
        emotionsKeySrc.put(R.drawable.sweat_smile, ":sweat_smile:");
        emotionsKeySrc.put(R.drawable.thumbsdown, ":thumbsdown:");
        emotionsKeySrc.put(R.drawable.thumbsup, ":thumbsup:");
        emotionsKeySrc.put(R.drawable.triumph, ":triumph:");
        emotionsKeySrc.put(R.drawable.trophy, ":trophy:");
        emotionsKeySrc.put(R.drawable.triumph, ":triumph:");
        emotionsKeySrc.put(R.drawable.umbrella, ":umbrella:");
        emotionsKeySrc.put(R.drawable.unamused, ":unamused:");
        emotionsKeySrc.put(R.drawable.v, ":v:");
        emotionsKeySrc.put(R.drawable.watermelon, ":watermelon:");
        emotionsKeySrc.put(R.drawable.weary, ":weary:");
        emotionsKeySrc.put(R.drawable.wink, ":wink:");
        emotionsKeySrc.put(R.drawable.worried, ":worried:");
        emotionsKeySrc.put(R.drawable.yum, ":yum:");
    }
}