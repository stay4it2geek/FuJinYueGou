package com.act.quzhibo.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.act.quzhibo.advanced_use.course_download.CourseDownloadFragment;
import com.act.quzhibo.advanced_use.course_preview.CoursePreviewFragment;
import com.act.quzhibo.ui.fragment.InterestPlatesFragment;

import java.util.ArrayList;


public class MediaActivity extends TabSlideBaseActivity implements InterestPlatesFragment.OnNearByListner {

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
        return new String[]{"一览", "下载"};
    }

    @Override
    protected ArrayList<Fragment> getFragments() {
        ArrayList<Fragment> mFragments = new ArrayList<>();
        mFragments.add(new CoursePreviewFragment());
        mFragments.add(new CourseDownloadFragment());
        return mFragments;
    }
    @Override
    public void onNear() {
        setPage(1);
    }
}
