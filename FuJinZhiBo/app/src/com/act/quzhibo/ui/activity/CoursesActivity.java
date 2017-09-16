package com.act.quzhibo.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.Window;

import com.act.quzhibo.R;
import com.act.quzhibo.ui.fragment.CoursesCenterFragment;

import java.util.ArrayList;



public class CoursesActivity extends TabSlideBaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        findViewById(R.id.isLogin).setVisibility(View.GONE);
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
        return new String[]{"课程中心"};
    }

    @Override
    protected ArrayList<Fragment> getFragments() {

        ArrayList<Fragment> fragments = new ArrayList<>();
        fragments.add(new CoursesCenterFragment());
        return fragments;
    }


}
