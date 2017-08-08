package com.act.quzhibo.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import com.act.quzhibo.ui.fragment.NoFragment;

import java.util.ArrayList;

public class MoneyActivity extends TabSlideBaseActivity {

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
        return true;
    }

    @Override
    protected String[] getTitles() {
        return new String[]{"赚钱项目", "赚钱秘笈", "赚钱防骗"};
    }

    @Override
    protected ArrayList<Fragment> getFragments() {
        ArrayList<Fragment> fragments=new ArrayList<>();
        fragments.add(new NoFragment());
        fragments.add(new NoFragment());
        fragments.add(new NoFragment());
        return fragments;
    }

}
