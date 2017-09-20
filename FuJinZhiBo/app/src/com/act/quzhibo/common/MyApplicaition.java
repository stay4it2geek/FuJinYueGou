package com.act.quzhibo.common;

import android.app.Application;

import com.act.quzhibo.R;
import com.act.quzhibo.entity.ProvinceAndCityEntity;
import com.act.quzhibo.util.CommonUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;

import c.b.BP;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobConfig;

public class MyApplicaition extends Application {

    public static HashMap<String, Long> map;
    public static final LinkedHashMap<String, Integer> emotionsKeySrc = new LinkedHashMap();
    public static final LinkedHashMap<String, LinkedHashMap<String, ProvinceAndCityEntity>> locationKeySrc = new LinkedHashMap();
    public static final LinkedHashMap<String, String> cityKeySrc = new LinkedHashMap();


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
        emotionsKeySrc.put(":joy:", R.drawable.joy);
        emotionsKeySrc.put(":kissing_heart:", R.drawable.kissing_heart);
        emotionsKeySrc.put(":kissing_smiling_eyes:", R.drawable.kissing_smiling_eyes);
        emotionsKeySrc.put(":laughing:", R.drawable.laughing);
        emotionsKeySrc.put(":logo_uc:", R.drawable.logo_uc);
        emotionsKeySrc.put(":lollipop:", R.drawable.lollipop);
        emotionsKeySrc.put(":mask:", R.drawable.mask);
        emotionsKeySrc.put(":muscle:", R.drawable.muscle);
        emotionsKeySrc.put(":ok_hand:", R.drawable.ok_hand);
        emotionsKeySrc.put(":pensive:", R.drawable.pensive);
        emotionsKeySrc.put(":persevere:", R.drawable.persevere);
        emotionsKeySrc.put(":pig:", R.drawable.pig);
        emotionsKeySrc.put(":point_up:", R.drawable.point_up);
        emotionsKeySrc.put(":police_car:", R.drawable.police_car);
        emotionsKeySrc.put(":pray:", R.drawable.pray);
        emotionsKeySrc.put(":punch:", R.drawable.punch);
        emotionsKeySrc.put(":racehorse:", R.drawable.racehorse);
        emotionsKeySrc.put(":rage:", R.drawable.rage);
        emotionsKeySrc.put(":ramen:", R.drawable.ramen);
        emotionsKeySrc.put(":ring:", R.drawable.ring);
        emotionsKeySrc.put(":rose:", R.drawable.rose);
        emotionsKeySrc.put(":say_hello:", R.drawable.say_hello);
        emotionsKeySrc.put(":scream:", R.drawable.scream);
        emotionsKeySrc.put(":sleeping:", R.drawable.sleeping);
        emotionsKeySrc.put(":sleepy:", R.drawable.sleepy);
        emotionsKeySrc.put(":smile:", R.drawable.smile);
        emotionsKeySrc.put(":smiley:", R.drawable.smiley);
        emotionsKeySrc.put(":smiling_imp:", R.drawable.smiling_imp);
        emotionsKeySrc.put(":smirk:", R.drawable.smirk);
        emotionsKeySrc.put(":snake:", R.drawable.snake);
        emotionsKeySrc.put(":sob:", R.drawable.sob);
        emotionsKeySrc.put(":snake:", R.drawable.snake);
        emotionsKeySrc.put(":strawberry:", R.drawable.strawberry);
        emotionsKeySrc.put(":stuck_out_tongue:", R.drawable.stuck_out_tongue);
        emotionsKeySrc.put(":stuck_out_tongue_winking_eye:", R.drawable.stuck_out_tongue_winking_eye);
        emotionsKeySrc.put(":stuck_out_tongue_closed_eyes:", R.drawable.stuck_out_tongue_closed_eyes);
        emotionsKeySrc.put(":sunglasses:", R.drawable.sunglasses);
        emotionsKeySrc.put(":sunny:", R.drawable.sunny);
        emotionsKeySrc.put(":sweat:", R.drawable.sweat);
        emotionsKeySrc.put(":sweat_smile:", R.drawable.sweat_smile);
        emotionsKeySrc.put(":thumbsdown:", R.drawable.thumbsdown);
        emotionsKeySrc.put(":thumbsup:", R.drawable.thumbsup);
        emotionsKeySrc.put(":triumph:", R.drawable.triumph);
        emotionsKeySrc.put(":trophy:", R.drawable.trophy);
        emotionsKeySrc.put(":triumph:", R.drawable.triumph);
        emotionsKeySrc.put(":umbrella:", R.drawable.umbrella);
        emotionsKeySrc.put(":unamused:", R.drawable.unamused);
        emotionsKeySrc.put(":v:", R.drawable.v);
        emotionsKeySrc.put(":watermelon:", R.drawable.watermelon);
        emotionsKeySrc.put(":weary:", R.drawable.weary);
        emotionsKeySrc.put(":wink:", R.drawable.wink);
        emotionsKeySrc.put(":worried:", R.drawable.worried);
        emotionsKeySrc.put(":yum:", R.drawable.yum);
    }

    static {
        cityKeySrc.put("187", "保定市");
        cityKeySrc.put("188", "沧州市");
        cityKeySrc.put("189", "承德市");
        cityKeySrc.put("190", "邯郸市");
        cityKeySrc.put("191", "衡水市");
        cityKeySrc.put("192", "廊坊市");
        cityKeySrc.put("193", "邯郸市");
        cityKeySrc.put("194", "秦皇岛");
        cityKeySrc.put("195", "唐山市");
        cityKeySrc.put("196", "邢台市");
        cityKeySrc.put("197", "张家口市");


        cityKeySrc.put("344", "长治市");
        cityKeySrc.put("345", "晋城市");
        cityKeySrc.put("346", "大同市");
        cityKeySrc.put("348", "晋中市");
        cityKeySrc.put("349", "临汾市");
        cityKeySrc.put("350", "吕梁市");
        cityKeySrc.put("351", "朔州市");
        cityKeySrc.put("353", "忻州市");
        cityKeySrc.put("354", "阳泉市");
        cityKeySrc.put("355", "运城市");


        cityKeySrc.put("309", "呼伦贝尔市");
        cityKeySrc.put("308", "呼和浩特市");
        cityKeySrc.put("305", "包头市");
        cityKeySrc.put("311", "乌海市");
        cityKeySrc.put("312", "乌兰察布市");
        cityKeySrc.put("310", "通辽市");
        cityKeySrc.put("306", "赤峰市");
        cityKeySrc.put("307", "鄂尔多斯市");
        cityKeySrc.put("304", "巴彦淖尔市");
        cityKeySrc.put("313", "锡林郭勒盟");
        cityKeySrc.put("314", "兴安盟");
        cityKeySrc.put("303", "阿拉善盟");


        cityKeySrc.put("300", "沈阳市");
        cityKeySrc.put("301", "铁岭市");
        cityKeySrc.put("292", "大连市");
        cityKeySrc.put("289", "鞍山市");
        cityKeySrc.put("294", "抚顺市");
        cityKeySrc.put("290", "本溪市");
        cityKeySrc.put("293", "丹东市");
        cityKeySrc.put("297", "锦州市");
        cityKeySrc.put("303", "阿拉善盟");
        cityKeySrc.put("303", "阿拉善盟");

//        {
//            "cityId":302,
//                "name":"营口市",
//                "CitySort":35
//        },
//        {
//            "cityId":295,
//                "name":"阜新市",
//                "CitySort":36
//        },
//        {
//            "cityId":298,
//                "name":"辽阳市",
//                "CitySort":37
//        },
//        {
//            "cityId":291,
//                "name":"朝阳市",
//                "CitySort":38
//        },
//        {
//            "cityId":299,
//                "name":"盘锦市",
//                "CitySort":39
//        },
//        {
//            "cityId":296,
//                "name":"葫芦岛市",
//                "CitySort":40
//        }
//

    }

}