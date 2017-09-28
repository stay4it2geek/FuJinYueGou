package com.act.quzhibo.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.act.quzhibo.R;
import com.act.quzhibo.entity.RecordVideoEvent;
import com.mabeijianxi.smallvideorecord2.MediaRecorderActivity;

import org.greenrobot.eventbus.EventBus;


public class RecordConfirmActivity extends FragmentActivity {

    private Button yes;
    private Button no;
    private TextView titleTv;
    private TextView messageTv;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.common_dialog_layout);
        initView();

    }


    private void initView() {
        yes = (Button) findViewById(R.id.positive);
        no = (Button) findViewById(R.id.negtive);
        titleTv = (TextView) findViewById(R.id.title);
        messageTv = (TextView) findViewById(R.id.message);
        titleTv.setText("录制已完成，是否上传？");
        messageTv.setText("");
        yes.setText("上传");
        no.setText("取消");
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = getIntent();
                String videoUri = intent.getStringExtra(MediaRecorderActivity.VIDEO_URI);
                String videoScreenshot = intent.getStringExtra(MediaRecorderActivity.VIDEO_SCREENSHOT);
                EventBus.getDefault().post(new RecordVideoEvent(videoUri, videoScreenshot));
                finish();
            }
        });
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RecordConfirmActivity.this.finish();
            }
        });
    }


}
