package com.act.quzhibo.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.act.quzhibo.advanced_use.media_download.MediaDownloadFragment;
import com.act.quzhibo.advanced_use.media_preview.MediaPreviewFragment;
import com.act.quzhibo.common.Constants;
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
        return new String[]{getIntent().getStringExtra("title"), "下载"};
    }

    @Override
    protected ArrayList<Fragment> getFragments() {
        ArrayList<Fragment> mFragments = new ArrayList<>();
        MediaPreviewFragment mediaPreviewFragment=new MediaPreviewFragment();
//        Bundle bundle=new Bundle();
//        bundle.putSerializable("author",getIntent().getSerializableExtra(Constants.MEDIA_AUTHOR));
//        mediaPreviewFragment.setArguments(bundle);
        mFragments.add(mediaPreviewFragment);
        mFragments.add(new MediaDownloadFragment());
        return mFragments;
    }
    @Override
    public void onNear() {
        setPage(1);
    }
}
