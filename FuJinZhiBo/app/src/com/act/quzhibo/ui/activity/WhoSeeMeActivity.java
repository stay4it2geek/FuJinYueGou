package com.act.quzhibo.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.act.quzhibo.ui.fragment.WhoLikeThenSeeMeFragment;
import com.act.quzhibo.ui.fragment.NearFragment;

import java.util.ArrayList;

public class WhoSeeMeActivity extends TabSlideBaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public boolean getActivityType() {
        return false;
    }

    @Override
    protected boolean isNeedShowBackDialog() {
        return false;
    }
    @Override
    protected String[] getTitles() {
        return new String[]{"市外", "附近"};
    }

    @Override
    protected ArrayList<Fragment> getFragments() {
        ArrayList<Fragment> fragments = new ArrayList<>();
        fragments.add(new WhoLikeThenSeeMeFragment());
        fragments.add(new NearFragment());
        return fragments;
    }


}
