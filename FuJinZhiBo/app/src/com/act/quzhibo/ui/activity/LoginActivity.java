package com.act.quzhibo.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.act.quzhibo.R;
import com.act.quzhibo.entity.RootUser;
import com.act.quzhibo.view.TitleBarView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.LogInListener;

import static android.text.InputType.TYPE_CLASS_TEXT;
import static android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD;
import static android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD;

public class LoginActivity extends AppCompatActivity {
    EditText et_userPhonenumber;
    EditText et_password;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        et_userPhonenumber = (EditText) findViewById(R.id.et_userPhonenumber);
        et_password = (EditText) findViewById(R.id.et_password);

        findViewById(R.id.btn_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });

        findViewById(R.id.tv_loginWithPhone).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, LoginWithSmsCodeActivity.class));
                LoginActivity.this.finish();

            }
        });

        TitleBarView  titlebar = (TitleBarView) findViewById(R.id.titlebar);
        titlebar.setBarTitle("登  录");
        titlebar.setBackButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginActivity.this.finish();
            }
        });

        findViewById(R.id.tv_register).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                LoginActivity.this.finish();
            }
        });
        et_password.setSingleLine(true);
        et_password.setInputType(TYPE_CLASS_TEXT|TYPE_TEXT_VARIATION_PASSWORD);
        ((CheckBox) findViewById(R.id.isSetPswVisi)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    et_password.setInputType(TYPE_CLASS_TEXT|TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                } else {
                    et_password.setInputType(TYPE_CLASS_TEXT|TYPE_TEXT_VARIATION_PASSWORD);
                }
            }
        });
    }

    private void login() {

        if (et_userPhonenumber.getText().toString().equals("手机号") || et_userPhonenumber.getText().toString().equals("") || et_userPhonenumber.getText().length() > 11) {
            Toast.makeText(this, "请输入正确位数的手机号码", Toast.LENGTH_SHORT).show();
            return;
        } else {
            String regex = "^((13[0-9])|(14[5|7])|(15([0-3]|[5-9]))||(17[4|7])|(18[0,5-9]))\\d{8}$";
            Pattern p = Pattern.compile(regex);
            Matcher m = p.matcher(et_userPhonenumber.getText().toString());
            if (!m.find()) {
                Toast.makeText(this, "请输入国内通用的手机号码", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        if (et_password.getText().toString().equals("密码") || et_password.getText().toString().equals("")) {
            Toast.makeText(this, "请输入密码", Toast.LENGTH_SHORT).show();
            return;
        }
        BmobUser.loginByAccount(et_userPhonenumber.getText().toString(), et_password.getText().toString(), new LogInListener<RootUser>() {
            @Override
            public void done(RootUser user, BmobException e) {
                if (user != null) {
                    Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_LONG).show();
                    LoginActivity.this.finish();
                } else {
                    Toast.makeText(LoginActivity.this, "登录失败,原因是：" + e.getMessage(), Toast.LENGTH_LONG).show();

                }

            }
        });
    }
}
