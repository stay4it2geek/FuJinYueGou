package com.act.quzhibo.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.act.quzhibo.R;

import java.util.List;


public class MiBoFragement extends BackHandledFragment {

    View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_common, null, false);

        return view;
    }




    @Override
    public boolean onBackPressed() {
        return false;
    }

}
