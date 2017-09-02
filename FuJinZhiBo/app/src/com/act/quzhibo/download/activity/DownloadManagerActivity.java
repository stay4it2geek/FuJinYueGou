package com.act.quzhibo.download.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.act.quzhibo.R;
import com.act.quzhibo.download.fragment.DownloadedFragment;
import com.act.quzhibo.download.fragment.DownloadingFragment;
import com.act.quzhibo.ui.activity.TabSlideBaseActivity;

import java.util.ArrayList;

public class DownloadManagerActivity extends TabSlideBaseActivity {

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
        return new String[]{"下载中", " 已下载"};
    }

    @Override
    protected ArrayList<Fragment> getFragments() {
        ArrayList<Fragment> fragments = new ArrayList<>();
        fragments.add(DownloadingFragment.newInstance());
        fragments.add(DownloadedFragment.newInstance());

        return fragments;
    }


}
