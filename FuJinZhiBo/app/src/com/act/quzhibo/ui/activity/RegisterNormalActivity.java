package com.act.quzhibo.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.act.quzhibo.R;
import com.act.quzhibo.entity.InterestSubPerson;
import com.act.quzhibo.entity.RootUser;
import com.act.quzhibo.util.CommonUtil;
import com.act.quzhibo.util.ToastUtil;
import com.act.quzhibo.view.TitleBarView;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FetchUserInfoListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.LogInListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

import static android.text.InputType.TYPE_CLASS_TEXT;
import static android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD;
import static android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD;


public class RegisterNormalActivity extends AppCompatActivity {

    private EditText et_userPhonenumber;
    private EditText et_password;
    private CheckBox check_agree;
    private EditText et_userNick;
    private EditText invite_code;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        findViewById(R.id.rl_sms_code).setVisibility(View.GONE);
        findViewById(R.id.verify_fail).setVisibility(View.GONE);
        ((TextView) findViewById(R.id.btn_verify_login)).setText("确 认 注 册");
        et_userPhonenumber = (EditText) findViewById(R.id.et_userPhonenumber);
        check_agree = (CheckBox) findViewById(R.id.check_agree);
        et_userNick = (EditText) findViewById(R.id.et_userNick);
        invite_code = (EditText) findViewById(R.id.invite_code);
        et_password = (EditText) findViewById(R.id.et_password);
        TitleBarView titlebar = (TitleBarView) findViewById(R.id.titlebar);
        titlebar.setBarTitle("普 通 注 册");
        titlebar.setBackButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RegisterNormalActivity.this.finish();
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
                startActivity(new Intent(RegisterNormalActivity.this, TermOfUseActivity.class));
            }
        });
        et_password = (EditText) findViewById(R.id.et_password);
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


    private void verifyAndLogin() {
        if (!check_agree.isChecked()) {
            ToastUtil.showToast(this, "请先同意用户协议");
            return;
        }

        if (et_userNick.getText().toString().equals("用户名") || et_userNick.getText().toString().equals("") || et_userNick.getText().length() > 20) {
            ToastUtil.showToast(this, "用户名必须是少于20个文字或者数字的组合");
            return;
        }

        if (et_password.getText().toString().equals("密码") || et_password.getText().toString().equals("")) {
            ToastUtil.showToast(this, "请输入密码");
            return;
        }
        if (invite_code.getText().toString().equals("邀请码,务必要填") || et_password.getText().toString().equals("")) {
            ToastUtil.showToast(this, "请输入邀请码");
            return;
        }
        if (et_userPhonenumber.getText().toString().equals("请输入手机号码") || et_userPhonenumber.getText().toString().equals("") || et_userPhonenumber.getText().length() > 11) {
            ToastUtil.showToast(this, "请输入正确位数的手机号码");
            return;
        } else {
            String regex = "^((13[0-9])|(14[5|7])|(15([0-3]|[5-9]))||(17[4|7])|(18[0,5-9]))\\d{8}$";
            Pattern p = Pattern.compile(regex);
            Matcher m = p.matcher(et_userPhonenumber.getText().toString());
            if (!m.find()) {
                ToastUtil.showToast(this, "请输入国内通用的手机号码");
                return;
            }
        }

        BmobQuery<RootUser> query = new BmobQuery<>();
        query.getObject(invite_code.getText().toString().toString(), new QueryListener<RootUser>() {
            @Override
            public void done(RootUser user, BmobException e) {
                if(user!=null){
                    RootUser rootUser = new RootUser();
                    rootUser.setUsername(et_userPhonenumber.getText().toString());
                    rootUser.setPassword(et_password.getText().toString());
                    rootUser.setMobilePhoneNumber(et_userPhonenumber.getText().toString());
                    rootUser.setUsername(et_userNick.getText().toString());
                    rootUser.signUp(new SaveListener<RootUser>() {
                        @Override
                        public void done(RootUser user, BmobException e) {
                            if (e == null) {
                                ToastUtil.showToast(RegisterNormalActivity.this, "注册成功");
                                if (user != null) {
                                    ToastUtil.showToast(RegisterNormalActivity.this, "登录成功");
                                    CommonUtil.fecth(RegisterNormalActivity.this);
                                    RegisterNormalActivity.this.finish();
                                }
                            } else {
                                ToastUtil.showToast(RegisterNormalActivity.this, "登录失败，原因是：" + e.getLocalizedMessage());

                            }
                        }
                    });
                } else {
                    ToastUtil.showToast(RegisterNormalActivity.this, "邀请码错误");
                }
            }
        });


    }

}
