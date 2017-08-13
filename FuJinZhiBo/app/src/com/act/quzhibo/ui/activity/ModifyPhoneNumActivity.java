package com.act.quzhibo.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.act.quzhibo.R;
import com.act.quzhibo.view.TitleBarView;

public class ModifyPhoneNumActivity extends AppCompatActivity{


	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_modifyphonenumber);
		TitleBarView titlebar = (TitleBarView) findViewById(R.id.titlebar);
		titlebar.setBarTitle("修 改 手 机 号");
		titlebar.setBackButtonListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ModifyPhoneNumActivity.this.finish();
			}
		});
	}
}
