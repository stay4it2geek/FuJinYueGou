package com.act.quzhibo.ui.activity;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.act.quzhibo.R;
import com.act.quzhibo.widget.TitleBarView;

public class CheckoutExplainActivity extends AppCompatActivity{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.checkout_explain_activity);
        TitleBarView titlebar = (TitleBarView) findViewById(R.id.titlebar);
        titlebar.setBarTitle("提现说明");
    }
}
