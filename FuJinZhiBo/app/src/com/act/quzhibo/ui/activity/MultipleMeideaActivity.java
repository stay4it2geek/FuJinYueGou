package com.act.quzhibo.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;

import com.act.quzhibo.R;
import com.act.quzhibo.ui.fragment.FunnyPersonFragment;
import com.act.quzhibo.ui.fragment.InterestPlatesFragment;
import com.act.quzhibo.ui.fragment.NoFragment;
import com.act.quzhibo.ui.fragment.ReadFragment;
import com.act.quzhibo.ui.fragment.ShowerListFragment;
import com.act.quzhibo.util.CommonUtil;
import com.act.quzhibo.util.ViewFindUtils;

import java.util.ArrayList;

/**
 * Created by asus-pc on 2017/5/30.
 * 多媒体
 */

public class MultipleMeideaActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
    }
    @Override
    protected String[] getTitles() {
        return new String[]{"阅读", "视听"};
    }

    @Override
    protected ArrayList<Fragment> getFragments() {

        ArrayList<Fragment> fragments=new ArrayList<>();
        fragments.add(new ReadFragment());
        fragments.add(new NoFragment());
        return fragments;
    }

}
