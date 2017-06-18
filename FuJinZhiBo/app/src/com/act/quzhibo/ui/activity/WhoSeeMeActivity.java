package com.act.quzhibo.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.act.quzhibo.ui.fragment.CommonSeeFragment;
import com.act.quzhibo.ui.fragment.NearSeeFragment;

import java.util.ArrayList;

/**
 * Created by weiminglin on 17/6/15.
 */

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
        return new String[]{"外市", "附近"};
    }

    @Override
    protected ArrayList<Fragment> getFragments() {
        ArrayList<Fragment> fragments = new ArrayList<>();
        fragments.add(new CommonSeeFragment());
        fragments.add(new NearSeeFragment());
        return fragments;
    }


}
