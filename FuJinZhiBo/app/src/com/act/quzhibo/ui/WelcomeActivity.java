package com.act.quzhibo.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.Toast;

import com.act.quzhibo.R;
import com.act.quzhibo.entity.Toggle;

import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobConfig;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

//设置启动欢迎页
public class WelcomeActivity extends Activity {
    private String isOpen;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_welcome);
        ImageView mWelcomeImage = (ImageView) findViewById(R.id.welcome_image);
        BmobQuery<Toggle> query = new BmobQuery<Toggle>();
        query.addWhereEqualTo("objectKey", "squareAndmoney");
        query.findObjects(new FindListener<Toggle>() {
            @Override
            public void done(List<Toggle> Toggles, BmobException e) {
                if(e==null){
                    if(Toggles.size()==1){
                        isOpen= Toggles.get(0).getIsOpen();
                    }
                    new Handler().postDelayed(r, 2000); //设置2秒钟后切换到下个Activity
                }else{
                    Toast.makeText(WelcomeActivity.this,e.getMessage()+"",Toast.LENGTH_LONG).show();
                    return;
                }
            }
        });

    }


    Runnable r = new Runnable() {
        public void run() {
            Intent intent = new Intent();
            intent.setClass(WelcomeActivity.this, TabMainActivity.class);
            intent.putExtra("isOpen",isOpen);
            startActivity(intent);
            finish();
        }
    };



}