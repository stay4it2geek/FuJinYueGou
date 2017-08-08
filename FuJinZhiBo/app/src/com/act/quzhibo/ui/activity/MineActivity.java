package com.act.quzhibo.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import com.act.quzhibo.ui.fragment.PersonalFragment;
import java.util.ArrayList;


public class MineActivity extends TabSlideBaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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


}
