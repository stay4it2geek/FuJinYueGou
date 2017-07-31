package com.act.quzhibo.ui.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ExpandableListView;

import com.act.quzhibo.R;
import com.act.quzhibo.adapter.BuyPowerExpandableListAdapter;

/**
 * Created by weiminglin on 17/6/15.
 */

public class BuyerPowerActivity extends AppCompatActivity {

    private ExpandableListView expand_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buypower);

    }


}
