package com.act.quzhibo.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;

import com.act.quzhibo.R;
import com.act.quzhibo.ui.fragment.FunnyPersonFragment;
import com.act.quzhibo.ui.fragment.InterestPlatesFragment;
import com.act.quzhibo.util.CommonUtil;
import com.act.quzhibo.util.ViewFindUtils;

import java.util.ArrayList;

/**
 * 赚钱
 */
public class MoneyActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    protected String[] getTitles() {
        return new String[]{"资源", "随笔", "项目"};
    }

    @Override
    protected ArrayList<Fragment> getFragments() {
        ArrayList<Fragment> fragments=new ArrayList<>();
        fragments.add(new InterestPlatesFragment());
        fragments.add(new FunnyPersonFragment());
        fragments.add(new FunnyPersonFragment());
        return fragments;
    }

}
