package com.act.quzhibo.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.act.quzhibo.R;
import com.act.quzhibo.bean.RootUser;
import com.act.quzhibo.util.CommonUtil;
import com.act.quzhibo.widget.TitleBarView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.LogInListener;
import me.leefeng.promptlibrary.PromptDialog;

import static android.text.InputType.TYPE_CLASS_TEXT;
import static android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD;
import static android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD;

public class LoginActivity extends AppCompatActivity {
    EditText et_userPhonenumber;
    EditText et_password;
    PromptDialog promptDialog;

    @Override
    protected void onResume() {
        super.onResume();
        et_userPhonenumber.setText(CommonUtil.loadLoginData(this, "account"));
        et_password.setText(CommonUtil.loadLoginData(this, "passWord"));
        ((CheckBox) findViewById(R.id.isSetPswVisi)).setChecked(true);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        et_userPhonenumber = (EditText) findViewById(R.id.et_userPhonenumber);
        et_password = (EditText) findViewById(R.id.et_password);
        promptDialog=new PromptDialog(this);
        findViewById(R.id.btn_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });

        findViewById(R.id.tv_frgetPsw).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, ResetPasswordActivity.class));
                LoginActivity.this.finish();

            }
        });

        TitleBarView titlebar = (TitleBarView) findViewById(R.id.titlebar);
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
        et_password.setInputType(TYPE_CLASS_TEXT | TYPE_TEXT_VARIATION_PASSWORD);
        ((CheckBox) findViewById(R.id.isSetPswVisi)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    et_password.setInputType(TYPE_CLASS_TEXT | TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                } else {
                    et_password.setInputType(TYPE_CLASS_TEXT | TYPE_TEXT_VARIATION_PASSWORD);
                }
                et_password.setSelection(et_password.getText().length());
            }
        });
    }

    private void login() {
        if (et_userPhonenumber.getText().toString().equals("手机号") || et_userPhonenumber.getText().toString().equals("") || et_userPhonenumber.getText().length() > 11) {
            promptDialog.showWarn("请输入正确位数的手机号码",true);
            return;
        } else {
            String regex = "^((13[0-9])|(14[5|7])|(15([0-3]|[5-9]))||(17[4|7])|(18[0,5-9]))\\d{8}$";
            Pattern p = Pattern.compile(regex);
            Matcher m = p.matcher(et_userPhonenumber.getText().toString());
            if (!m.find()) {
                promptDialog.showWarn("请输入国内通用的手机号码",true);
                return;
            }
        }
        if (et_password.getText().toString().equals("密码") || et_password.getText().toString().equals("")) {
            promptDialog.showWarn("请输入密码",true);
            return;
        }
        promptDialog.showLoading("正在登录");
        CommonUtil.saveLoginData(this, et_userPhonenumber.getText().toString(), et_password.getText().toString());
        BmobUser.loginByAccount(et_userPhonenumber.getText().toString(), et_password.getText().toString(), new LogInListener<RootUser>() {
            @Override
            public void done(RootUser user, BmobException e) {
                if (user != null) {
                    promptDialog.showSuccess("登录成功",true);;
                    LoginActivity.this.finish();
                } else {
                    promptDialog.showError("登录失败",true);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if(promptDialog.onBackPressed()){
            super.onBackPressed();
        }
    }
}
