package com.act.quzhibo.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.act.quzhibo.ui.fragment.CourseDownloadFragment;
import com.act.quzhibo.ui.fragment.CoursePreviewFragment;
import com.act.quzhibo.ui.fragment.FuliPicsFragement;
import com.act.quzhibo.ui.fragment.InterestPlatesFragment;

import java.util.ArrayList;

/**
 * 广场
 */
public class SquareActivity extends TabSlideBaseActivity implements InterestPlatesFragment.OnNearByListner {

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
        return new String[]{"专辑", "视频", "下载"};
    }

    @Override
    protected ArrayList<Fragment> getFragments() {
        ArrayList<Fragment> fragments = new ArrayList<>();
//        fragments.add(new InterestPlatesFragment());
        fragments.add(new FuliPicsFragement());
        fragments.add(new CoursePreviewFragment());
//        fragments.add(new NearFragment());
        fragments.add(new CourseDownloadFragment());
        return fragments;
    }


    public String pid;

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    @Override
    public void onNear() {
        setPage(3);
    }
}
