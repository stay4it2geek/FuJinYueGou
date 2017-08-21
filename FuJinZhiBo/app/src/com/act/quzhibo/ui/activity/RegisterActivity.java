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
import com.act.quzhibo.util.CommonUtil;
import com.act.quzhibo.view.TitleBarView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.LogInListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.UpdateListener;

import static android.text.InputType.TYPE_CLASS_TEXT;
import static android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD;
import static android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD;


public class RegisterActivity extends AppCompatActivity {

    private EditText et_userPhonenumber;
    private EditText et_sms_code;
    private EditText et_password;
    private CheckBox check_agree;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        et_userPhonenumber = (EditText) findViewById(R.id.et_userPhonenumber);
        et_sms_code = (EditText) findViewById(R.id.et_sms_code);
        check_agree = (CheckBox) findViewById(R.id.check_agree);

        et_password = (EditText) findViewById(R.id.et_password);
        TitleBarView titlebar = (TitleBarView) findViewById(R.id.titlebar);
        titlebar.setBarTitle("手 机 注 册");
        titlebar.setBackButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RegisterActivity.this.finish();
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

        findViewById(R.id.termofuse).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               startActivity(new Intent(RegisterActivity.this,TermOfUseActivity.class));
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
                et_password.setSelection(et_password.getText().length());

            }
        });

        findViewById(R.id.verify_fail).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               startActivity(new Intent(RegisterActivity.this,RegisterNormalActivity.class));
                RegisterActivity.this.finish();
            }
        });

    }

    private void getCode() {
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
        BmobSMS.requestSMSCode(et_userPhonenumber.getText().toString(),
                "您的验证码是`%smscode%`，有效期为`%ttl%`分钟。您正在使用`%appname%`的验证码。【比目科技】", new QueryListener<Integer>() {

                    @Override
                    public void done(Integer o, BmobException e) {
                        if (e == null) {
                            Toast.makeText(RegisterActivity.this, "短信验证码已经发送,序列号是：" + o, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(RegisterActivity.this, "短信验证码发送失败，原因是" + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();

                        }
                    }
                });


    }

    private void verifyAndLogin() {
        if (!check_agree.isChecked()) {
            Toast.makeText(this, "请先同意用户协议", Toast.LENGTH_SHORT).show();
            return;
        }
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


        BmobSMS.verifySmsCode(et_userPhonenumber.getText().toString(), et_sms_code.getText().toString().trim(), new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    RootUser rootUser = new RootUser();
                    rootUser.setMobilePhoneNumber(et_userPhonenumber.getText().toString());
                    rootUser.setMobilePhoneNumberVerified(true);
                    Toast.makeText(RegisterActivity.this, "验证成功，自动注册登录中......", Toast.LENGTH_SHORT).show();
                    rootUser.signOrLoginByMobilePhone(et_userPhonenumber.getText().toString(), et_sms_code.getText().toString().trim(), new LogInListener<RootUser>() {

                        @Override
                        public void done(RootUser rootUser, BmobException e) {
                            if (rootUser != null) {
                                CommonUtil.fecth(RegisterActivity.this);
                                RegisterActivity.this.finish();
                                Toast.makeText(RegisterActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(RegisterActivity.this, "登录失败，原因是：" + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();

                            }
                        }

                    });
                } else {
                    Toast.makeText(RegisterActivity.this, "验证失败，原因是：" + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();

                }
            }
        });


    }


}
