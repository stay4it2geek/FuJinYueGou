package com.act.quzhibo.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.act.quzhibo.advanced_use.course_download.CourseDownloadFragment;
import com.act.quzhibo.advanced_use.course_preview.CoursePreviewFragment;
import com.act.quzhibo.ui.fragment.InterestPlatesFragment;
import com.act.quzhibo.ui.fragment.PhotoAlbumAuthorsFragment;
import com.act.quzhibo.ui.fragment.VideoAlbumAuthorsFragment;

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
        return new String[]{"专辑", "视频"};
    }

    @Override
    protected ArrayList<Fragment> getFragments() {
        ArrayList<Fragment> mFragments = new ArrayList<>();
        //        fragments.add(new InterestPlatesFragment());
        //        fragments.add(new NearFragment());
        mFragments.add(new PhotoAlbumAuthorsFragment());
        mFragments.add(new VideoAlbumAuthorsFragment());

        return mFragments;
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
        setPage(1);
    }
}
