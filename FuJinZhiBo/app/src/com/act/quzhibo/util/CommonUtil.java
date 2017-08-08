package com.act.quzhibo.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Base64;
import android.view.View;

import com.act.quzhibo.entity.LocationData;
import com.act.quzhibo.R;
import com.act.quzhibo.common.Constants;
import com.act.quzhibo.entity.TabEntity;
import com.act.quzhibo.entity.Toggle;
import com.flyco.tablayout.CommonTabLayout;
import com.flyco.tablayout.listener.CustomTabEntity;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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
    public static <T> ArrayList<T> jsonToArrayList(String json, Class<T> clazz)
    {
        Type type = new TypeToken<ArrayList<JsonObject>>()
        {}.getType();
        ArrayList<JsonObject> jsonObjects = new Gson().fromJson(json, type);

        ArrayList<T> arrayList = new ArrayList<>();
        for (JsonObject jsonObject : jsonObjects)
        {
            arrayList.add(new Gson().fromJson(jsonObject, clazz));
        }
        return arrayList;
    }
    public static void initView(String[] mTitles,View decorView, final ViewPager viewPager, FragmentPagerAdapter mAdapter, boolean activityType) {
        final CommonTabLayout commonTabLayout;
        viewPager.setAdapter(mAdapter);
        if (activityType) {
            commonTabLayout = ViewFindUtils.find(decorView, R.id.layout_mine);
        } else {
            commonTabLayout = ViewFindUtils.find(decorView, R.id.layout);

        }
        commonTabLayout.setVisibility(View.VISIBLE);
        ArrayList<CustomTabEntity> mTabEntities = new ArrayList<>();
        for (int i = 0; i < mTitles.length; i++) {
            mTabEntities.add(new TabEntity(mTitles[i], 0, 0));
        }
        commonTabLayout.setTabData(mTabEntities);

        commonTabLayout.setOnTabSelectListener(new OnTabSelectListener() {
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
                commonTabLayout.setCurrentTab(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        viewPager.setOffscreenPageLimit(mTabEntities.size());
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

    public static String getDateTransforTamp(Long timeTamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
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

    public static String getJson(String fileName, Context context) {
        //将json数据变成字符串
        StringBuilder stringBuilder = new StringBuilder();
        try {
            //获取assets资源管理器
            AssetManager assetManager = context.getAssets();
            //通过管理器打开文件并读取
            BufferedReader bf = new BufferedReader(new InputStreamReader(
                    assetManager.open(fileName)));
            String line;
            while ((line = bf.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    public static LocationData parseLocation(Context context) {
        LocationData data = parseJsonWithGson(getJson("json.txt", context), LocationData.class);
        return data;
    }

    public static void saveData(Context context,int value,String key){
        SharedPreferences sp = context.getSharedPreferences("config",Context.MODE_PRIVATE );
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public static int loadData(Context context,String key) {
        SharedPreferences sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        return sp.getInt(key,0);
    }

}
