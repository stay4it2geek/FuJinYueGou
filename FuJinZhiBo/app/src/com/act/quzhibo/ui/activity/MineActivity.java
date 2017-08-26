package com.act.quzhibo.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.Button;

import com.act.quzhibo.R;
import com.act.quzhibo.entity.RootUser;
import com.act.quzhibo.ui.fragment.PersonalFragment;
import com.act.quzhibo.util.CommonUtil;

import java.util.ArrayList;

import cn.bmob.v3.BmobUser;


public class MineActivity extends TabSlideBaseActivity {
    Button isLogin;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isLogin = (Button) findViewById(R.id.isLogin);
        isLogin.setVisibility(View.VISIBLE);

        isLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RootUser rootUser = BmobUser.getCurrentUser(RootUser.class);
                if (rootUser == null) {
                    startActivity(new Intent(MineActivity.this, LoginActivity.class));
                }
                return;

            }
        });
    }

    @Override
    public boolean getActivityType() {
        return true;
    }

    @Override
    protected boolean isNeedShowBackDialog() {
        return true;
    }

    @Override
    protected String[] getTitles() {
        return new String[]{"个人中心"};
    }

    @Override
    protected ArrayList<Fragment> getFragments() {
        ArrayList<Fragment> fragments = new ArrayList<>();
        fragments.add(new PersonalFragment());
        return fragments;
    }

    @Override
    protected void onResume() {
        super.onResume();
        RootUser rootUser = BmobUser.getCurrentUser(RootUser.class);
        if (rootUser != null) {
            CommonUtil.fecth(this);
            isLogin.setText("已登录");
        } else {
            isLogin.setText("去登录");
        }
    }
}
