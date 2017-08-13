package com.act.quzhibo.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.act.quzhibo.R;
import com.act.quzhibo.entity.RootUser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.LogInListener;
import cn.bmob.v3.listener.SaveListener;


public class RegisterActivity extends AppCompatActivity {
	EditText et_userPhonenumber;
	EditText et_password;
	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		et_userPhonenumber = (EditText) findViewById(R.id.et_userPhonenumber);
		et_password = (EditText) findViewById(R.id.et_password);
		findViewById(R.id.btn_register).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				btnRegister();
			}
		});
		findViewById(R.id.tv_loginWithPhone).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				startActivity(new Intent(RegisterActivity.this,LoginWithCodeActivity.class));
			}
		});
	}

	private void btnRegister() {
		RootUser rootUser=new RootUser();
		if (et_userPhonenumber.getText().toString().equals("手机号") || et_userPhonenumber.getText().toString().equals("") || et_userPhonenumber.getText().length() > 11) {
			Toast.makeText(this, "请输入正确的手机号码", Toast.LENGTH_SHORT).show();
			return;
		} else {
			String regex = "^((13[0-9])|(14[5|7])|(15([0-3]|[5-9]))||(17[4|7])|(18[0,5-9]))\\d{8}$";
			Pattern p = Pattern.compile(regex);
			Matcher m = p.matcher(et_userPhonenumber.getText().toString());
			if(m.find()) {
				rootUser.setUsername(et_userPhonenumber.getText().toString());
				rootUser.setMobilePhoneNumber(et_userPhonenumber.getText().toString());
			}
		}
		if (et_password.getText().toString().equals("密码") || et_password.getText().toString().equals("")) {
			Toast.makeText(this, "请输入密码", Toast.LENGTH_SHORT).show();
			return;
		}else{
			rootUser.setPassword(et_password.getText().toString());

		}

		rootUser.signUp(new SaveListener<RootUser>() {
			@Override
			public void done(RootUser rootUser, BmobException e) {
				if (e == null) {
					Toast.makeText(RegisterActivity.this, "用户注册成功", Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(RegisterActivity.this, "用户注册失败", Toast.LENGTH_LONG).show();
				}
			}
		});
	}
}
