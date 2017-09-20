package com.act.quzhibo.ui.activity;


import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;

import com.act.quzhibo.R;
import com.act.quzhibo.view.ExpanbleCommonView;
import com.act.quzhibo.view.TitleBarView;

public class MakeMoneyActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.money_introduce);
        TitleBarView titlebar = (TitleBarView) findViewById(R.id.titlebar);
        titlebar.setVisibility(View.VISIBLE);
        titlebar.setBarTitle("财富通道");
        titlebar.setBackButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MakeMoneyActivity.this.finish();
            }
        });
        final Button moneywayBtn = (Button) findViewById(R.id.moneyway_btn);
        moneywayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        final ExpanbleCommonView expanbaleView = (ExpanbleCommonView) findViewById(R.id.expand_commonview);

        expanbaleView.setOnMoneyButtonTextListner(new ExpanbleCommonView.OnMoneyWayButtonTextListner() {
            @Override
            public void setText(String text) {
                moneywayBtn.setText(text);
            }
        });
    }

}
