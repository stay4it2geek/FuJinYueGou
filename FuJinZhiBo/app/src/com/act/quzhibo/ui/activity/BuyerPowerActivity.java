package com.act.quzhibo.ui.activity;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.act.quzhibo.R;

public class BuyerPowerActivity extends AppCompatActivity implements View.OnClickListener{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buypower);
        intAcceses();
    }

    private void intAcceses() {
        ((TextView) findViewById(R.id.mid_whoseeme)).getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
        ((TextView) findViewById(R.id.mid_shiliaofujin)).getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
        ((TextView) findViewById(R.id.mid_xiazaivip)).getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
        ((TextView) findViewById(R.id.mid_caifuvip)).getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
        ((TextView) findViewById(R.id.chu_chakanfujin)).getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
        ((TextView) findViewById(R.id.chu_liulanmibo)).getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
        ((TextView) findViewById(R.id.chu_whoseeme)).getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
        ((TextView) findViewById(R.id.chu_shiliaofujin)).getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
        ((TextView) findViewById(R.id.chu_shiliaoqingqu)).getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
        ((TextView) findViewById(R.id.chu_xiazaivip)).getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
        ((TextView) findViewById(R.id.chu_caifuvip)).getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
        findViewById(R.id.tv_getAccese).setOnClickListener(this);
        findViewById(R.id.tv_getAccese2).setOnClickListener(this);
        findViewById(R.id.tv_getAccese3).setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_getAccese:
            case R.id.tv_getAccese2:
            case R.id.tv_getAccese3:
               startActivity(new Intent(this, GetVipPayActivity.class));
                break;
        }
    }


}
