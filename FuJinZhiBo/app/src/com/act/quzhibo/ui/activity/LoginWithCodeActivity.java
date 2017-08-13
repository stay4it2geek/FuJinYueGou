package com.act.quzhibo.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.act.quzhibo.R;
import com.act.quzhibo.entity.RootUser;

import cn.bmob.sms.BmobSMS;
import cn.bmob.sms.exception.BmobException;
import cn.bmob.sms.listener.RequestSMSCodeListener;
import cn.bmob.sms.listener.VerifySMSCodeListener;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.LogInListener;


public class LoginWithCodeActivity extends AppCompatActivity{

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login_forget_password);
		findViewById(R.id.getCode_btn).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				BmobSMS.requestSMSCode(LoginWithCodeActivity.this, "18950060293", "", new RequestSMSCodeListener() {
					@Override
					public void done(Integer integer, BmobException e) {
						Toast.makeText(LoginWithCodeActivity.this, "短信验证码已经发送，短信编码是"+integer, Toast.LENGTH_SHORT).show();
					}
				});
			}
		});
		EditText editText= (EditText) findViewById(R.id.vertify_code);
		RootUser rootUser=new RootUser();
		rootUser.signOrLoginByMobilePhone("11位手机号码", editText.getText().toString(), new LogInListener<RootUser>() {

			@Override
			public void done(RootUser rootUser, cn.bmob.v3.exception.BmobException e) {
				if(rootUser!=null){
					LoginWithCodeActivity.this.finish();
				}
			}

		});
//				BmobSMS.verifySmsCode(LoginWithCodeActivity.this, editText.getText().toString(), "", new VerifySMSCodeListener() {
//					@Override
//					public void done(BmobException e) {
//						if(e==null){
//
//						}
//					}
//				});
	}
}
