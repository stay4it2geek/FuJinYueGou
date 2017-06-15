package com.act.quzhibo.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Window;

import com.act.quzhibo.R;
import com.act.quzhibo.ui.fragment.NoFragment;
import com.act.quzhibo.ui.fragment.ReadFragment;
import com.flyco.tablayout.CommonTabLayout;

import java.util.ArrayList;

/**
 * Created by asus-pc on 2017/5/30.
 * 多媒体
 */

public class MultipleMeideaActivity extends TabSlideBaseActivity {
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

        ArrayList<Fragment> fragments = new ArrayList<>();
        fragments.add(new ReadFragment());
        fragments.add(new NoFragment());
        return fragments;
    }

    @Override
    public boolean getActivityType() {
        return false;
    }
}
