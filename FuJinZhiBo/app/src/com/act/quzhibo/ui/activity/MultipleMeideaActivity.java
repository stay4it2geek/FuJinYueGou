package com.act.quzhibo.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Window;

import com.act.quzhibo.ui.fragment.NoFragment;
import com.act.quzhibo.ui.fragment.ReadFragment;

import java.util.ArrayList;



public class MultipleMeideaActivity extends TabSlideBaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
    }

    @Override
    public boolean getActivityType() {
        return false;
    }

    @Override
    protected boolean isNeedShowBackDialog() {
        return true;
    }

    @Override
    protected String[] getTitles() {
        return new String[]{"图文课程", "影音课程"};
    }

    @Override
    protected ArrayList<Fragment> getFragments() {

        ArrayList<Fragment> fragments = new ArrayList<>();
        fragments.add(new ReadFragment());
        fragments.add(new NoFragment());
        return fragments;
    }

}
