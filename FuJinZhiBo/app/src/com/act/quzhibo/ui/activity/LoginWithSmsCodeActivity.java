package com.act.quzhibo.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.act.quzhibo.R;
import com.act.quzhibo.entity.RootUser;
import com.act.quzhibo.view.TitleBarView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.LogInListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.UpdateListener;


public class LoginWithSmsCodeActivity extends AppCompatActivity {

    private EditText et_userPhonenumber;
    private EditText et_sms_code;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_with_smscode);

        et_userPhonenumber = (EditText) findViewById(R.id.et_userPhonenumber);
        et_sms_code = (EditText) findViewById(R.id.et_sms_code);

        TitleBarView titlebar = (TitleBarView) findViewById(R.id.titlebar);
        titlebar.setBarTitle("手 机 注 册/登  录");
        titlebar.setBackButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginWithSmsCodeActivity.this.finish();
            }
        });
        findViewById(R.id.getCode_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getCode();
            }
        });
        findViewById(R.id.btn_verify_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyAndLogin();
            }
        });
    }

    private void getCode() {
        if (et_userPhonenumber.getText().toString().equals("请输入手机号码") || et_userPhonenumber.getText().toString().equals("") || et_userPhonenumber.getText().length() > 11) {
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

        BmobSMS.requestSMSCode(et_userPhonenumber.getText().toString(),
                "您的验证码是`%smscode%`，有效期为`%ttl%`分钟。您正在使用`%appname%`的验证码。【比目科技】", new QueryListener<Integer>() {

                    @Override
                    public void done(Integer o, BmobException e) {
                        if (e == null) {
                            Toast.makeText(LoginWithSmsCodeActivity.this, "短信验证码已经发送,序列号是：" + o, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(LoginWithSmsCodeActivity.this, "短信验证码发送失败，原因是" + e.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    }
                });


    }

    private void verifyAndLogin() {

        if (et_sms_code.getText().toString().equals("请输入短信验证码") || et_sms_code.getText().toString().equals("")) {
            Toast.makeText(this, "请输入短信验证码", Toast.LENGTH_SHORT).show();
            return;
        }
        if (et_userPhonenumber.getText().toString().equals("请输入手机号码") || et_userPhonenumber.getText().toString().equals("") || et_userPhonenumber.getText().length() > 11) {
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


        BmobSMS.verifySmsCode(et_userPhonenumber.getText().toString(), et_sms_code.getText().toString(), new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    RootUser rootUser = new RootUser();
                    rootUser.setMobilePhoneNumber(et_userPhonenumber.getText().toString());
                    rootUser.setMobilePhoneNumberVerified(true);
                    Toast.makeText(LoginWithSmsCodeActivity.this, "验证成功，自动登录中......", Toast.LENGTH_SHORT).show();

                    rootUser.signOrLoginByMobilePhone(et_userPhonenumber.getText().toString(), et_sms_code.getText().toString(), new LogInListener<RootUser>() {

                        @Override
                        public void done(RootUser rootUser, BmobException e) {
                            if (rootUser != null) {
                                LoginWithSmsCodeActivity.this.finish();
                                Toast.makeText(LoginWithSmsCodeActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(LoginWithSmsCodeActivity.this, "登录失败，原因是：" + e.getMessage(), Toast.LENGTH_SHORT).show();

                            }
                        }

                    });
                } else {
                    Toast.makeText(LoginWithSmsCodeActivity.this, "验证失败，原因是：" + e.getMessage(), Toast.LENGTH_SHORT).show();

                }
            }
        });


    }


}
