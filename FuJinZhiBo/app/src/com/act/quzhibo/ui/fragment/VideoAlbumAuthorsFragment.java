package com.act.quzhibo.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.act.quzhibo.R;
import com.act.quzhibo.ui.activity.MediaActivity;

/**
 * Created by asus-pc on 2017/8/28.
 */
public class VideoAlbumAuthorsFragment extends BackHandledFragment {
    View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.media_authors_fragment, null);

        view.findViewById(R.id.change).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), MediaActivity.class));
            }
        });
        return view;
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }
}