package com.act.quzhibo.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import com.act.quzhibo.R;
import com.act.quzhibo.ui.fragment.FunnyPersonFragment;
import com.act.quzhibo.ui.fragment.InterestPlatesFragment;
import com.act.quzhibo.ui.fragment.PersonalFragment;
import com.flyco.tablayout.CommonTabLayout;

import java.util.ArrayList;

/**
 * Created by asus-pc on 2017/5/30.
 * 我的个人中心
 */

public class MineActivity extends TabSlideBaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
    public boolean getActivityType() {
        return true;
    }

    @Override
    protected boolean isNeedShowBackDialog() {
        return true;
    }
}
