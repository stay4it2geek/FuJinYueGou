package com.act.quzhibo.ui.activity;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.act.quzhibo.R;
import com.act.quzhibo.view.TitleBarView;

public class VipPolicyActivity extends AppCompatActivity implements View.OnClickListener{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buypower);
        TitleBarView titlebar = (TitleBarView) findViewById(R.id.titlebar);
        titlebar.setBarTitle("趣 会 员 特 权");
        titlebar.setBackButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VipPolicyActivity.this.finish();
            }
        });
        findViewById(R.id.tv_getAccese).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_getAccese:
                startActivity(new Intent(this, GetVipPayActivity.class));
                break;
        }
    }


}
