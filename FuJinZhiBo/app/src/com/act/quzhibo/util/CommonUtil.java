package com.act.quzhibo.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Base64;
import android.util.Log;
import android.view.View;

import com.act.quzhibo.R;
import com.act.quzhibo.common.Constants;
import com.act.quzhibo.entity.TabEntity;
import com.act.quzhibo.entity.Toggle;
import com.act.quzhibo.ui.fragment.CommonFragment;
import com.flyco.tablayout.CommonTabLayout;
import com.flyco.tablayout.listener.CustomTabEntity;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.google.gson.Gson;
import com.netease.neliveplayer.util.string.StringUtil;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.text.DateFormat;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by weiminglin on 17/5/30.
 */

public class CommonUtil {

    public static void switchFragment(Fragment fragment, int layoutId, FragmentActivity activity) {
        FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
        transaction.add(layoutId, fragment);
        transaction.addToBackStack(null);
        transaction.commitAllowingStateLoss();
    }


    public static String SceneList2String(List SceneList) {
        String SceneListString = "";
        try { // 实例化一个ByteArrayOutputStream对象，用来装载压缩后的字节文件。
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            // 然后将得到的字符数据装载到ObjectOutputStream
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(
                    byteArrayOutputStream);
            // writeObject 方法负责写入特定类的对象的状态，以便相应的 readObject 方法可以还原它
            objectOutputStream.writeObject(SceneList);
            // 最后，用Base64.encode将字节文件转换成Base64编码保存在String中
            SceneListString = new String(Base64.encode(
                    byteArrayOutputStream.toByteArray(), Base64.DEFAULT));
            // 关闭objectOutputStream
            objectOutputStream.close();
        } catch (StreamCorruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return SceneListString;

    }

    @SuppressWarnings("unchecked")
    public static List String2SceneList(String SceneListString) {
        byte[] mobileBytes = Base64.decode(SceneListString.getBytes(),
                Base64.DEFAULT);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(
                mobileBytes);
        ObjectInputStream objectInputStream;
        List<Toggle> SceneList = null;
        try {
            objectInputStream = new ObjectInputStream(byteArrayInputStream);
            SceneList = null;
            SceneList = (List) objectInputStream.readObject();
            objectInputStream.close();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (StreamCorruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return SceneList;
    }


    public static Toggle getToggle(Activity activity, String ToggleKey) {
        SharedPreferences sharedPreferences = activity.getSharedPreferences(Constants.SAVE, Context.MODE_PRIVATE);
        String liststr = sharedPreferences.getString(Constants.TOGGLES, "defValue");
        List<Toggle> Toggles = CommonUtil.String2SceneList(liststr);
        for (Toggle toggle : Toggles) {
            if (toggle.getObjectKey().equals(ToggleKey)) {
                return toggle;
            }
        }
        return null;
    }

    public static <T> T parseJsonWithGson(String jsonData, Class<T> type) {
        Gson gson = new Gson();
        T result = gson.fromJson(jsonData, type);
        return result;
    }

    public static void initView(String[] mTitles, ArrayList<Fragment> mFragments, View decorView, final ViewPager viewPager, FragmentPagerAdapter mAdapter) {
        for (String title : mTitles) {
            mFragments.add(CommonFragment.getInstance(title));
        }

        viewPager.setAdapter(mAdapter);
        final CommonTabLayout tabLayout = ViewFindUtils.find(decorView, R.id.layout);
        ArrayList<CustomTabEntity> mTabEntities = new ArrayList<>();
        for (int i = 0; i < mTitles.length; i++) {
            mTabEntities.add(new TabEntity(mTitles[i], 0, 0));
        }
        tabLayout.setTabData(mTabEntities);

        tabLayout.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                viewPager.setCurrentItem(position);
            }

            @Override
            public void onTabReselect(int position) {
            }
        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                tabLayout.setCurrentTab(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        viewPager.setCurrentItem(0);
    }


    /*
       * 将时间转换为时间戳
       */
    public static String dateToStamp(String s) {
        String res = null;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = simpleDateFormat.parse(s);
            long ts = date.getTime();
            res = String.valueOf(ts);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return res;
    }

    public static  String getDateTransforTamp(Long timeTamp){
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String sd = sdf.format(new Date(timeTamp));
        return sd;
    }
    /**
     * 获取第某前天的时间
     */

    public static String getDataString(int num) {

        Date dNow = new Date();   //当前时间
        Date dBefore;

        Calendar calendar = Calendar.getInstance(); //得到日历
        calendar.setTime(dNow);//把当前时间赋给日历
        calendar.add(Calendar.DAY_OF_MONTH, -num);  //设置为前n天
        dBefore = calendar.getTime();   //得到前第n天的时间
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); //设置时间格式
        String defaultStartDate = sdf.format(dBefore);    //格式化前n天
        String defaultEndDate = sdf.format(dNow); //格式化当前时间

        System.out.println("前n天的时间是：" + defaultStartDate);
        System.out.println("生成的时间是：" + defaultEndDate);

        return defaultStartDate;
    }


}
