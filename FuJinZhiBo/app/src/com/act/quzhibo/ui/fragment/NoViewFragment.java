package com.act.quzhibo.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.act.quzhibo.R;
import com.act.quzhibo.common.Constants;
import com.act.quzhibo.bean.Room;
import com.act.quzhibo.ui.activity.VideoPlayerActivityLanscape;

public class NoViewFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_nochat, null, false);
        if (getArguments() != null && getArguments().getSerializable("room") != null) {
            final Room room = (Room) getArguments().getSerializable("room");
            if (room.screenType.equals(Constants.LANSPACE)) {
                view.findViewById(R.id.fullScreenLayout).setVisibility(View.VISIBLE);
                view.findViewById(R.id.fullScreenLayout).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getContext(), VideoPlayerActivityLanscape.class);
                        intent.putExtra("room", room);
                        startActivity(intent);
                    }
                });
            }
        }
        return view;
    }
}
