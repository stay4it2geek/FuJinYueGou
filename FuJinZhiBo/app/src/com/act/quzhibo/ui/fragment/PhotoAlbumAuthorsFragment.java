package com.act.quzhibo.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.act.quzhibo.R;
import com.act.quzhibo.util.CommonUtil;


public class PhotoAlbumAuthorsFragment extends android.support.v4.app.Fragment {
    View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.media_authors_fragment, null);

        view.findViewById(R.id.change).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonUtil.switchFragment(new CoursePreviewFragment(), R.id.layoutContainer, getActivity());
            }
        });
        return view;
    }
}
