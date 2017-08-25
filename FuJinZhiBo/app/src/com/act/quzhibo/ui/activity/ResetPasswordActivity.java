package com.act.quzhibo.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.act.quzhibo.R;
import com.act.quzhibo.entity.RootUser;
import com.act.quzhibo.util.CommonUtil;
import com.act.quzhibo.util.ToastUtil;
import com.act.quzhibo.view.TitleBarView;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FetchUserInfoListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.UpdateListener;

import static android.text.InputType.TYPE_CLASS_TEXT;
import static android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD;
import static android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD;


public class ResetPasswordActivity extends AppCompatActivity {
    EditText et_newpsw;
    EditText et_c_newpsw;
    EditText et_sms_code;
    private EditText et_userPhonenumber;
    TitleBarView titlebar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resetpassword);
        CommonUtil.fecth(ResetPasswordActivity.this);
        ;

        et_userPhonenumber = (EditText) findViewById(R.id.et_userPhonenumber);

        et_newpsw = (EditText) findViewById(R.id.et_newpsw);
        et_c_newpsw = (EditText) findViewById(R.id.et_c_newpsw);
        et_sms_code = (EditText) findViewById(R.id.et_sms_code);

        titlebar = (TitleBarView) findViewById(R.id.titlebar);
        titlebar.setBarTitle(" 重 置 密 码");
        titlebar.setBackButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ResetPasswordActivity.this.finish();
            }
        });

        setEditext(R.id.isSetPswVisiNew, et_newpsw);
        setEditext(R.id.isSetPswVisiConfirm, et_c_newpsw);


        findViewById(R.id.getCode_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getCode();

            }
        });
        findViewById(R.id.btn_verify_resetPsw).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyAndResetPsw();
            }
        });
    }

    private void setEditext(int viewId, final EditText editText) {
        editText.setSingleLine(true);
        editText.setInputType(TYPE_CLASS_TEXT | TYPE_TEXT_VARIATION_PASSWORD);
        ((CheckBox) findViewById(viewId)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    editText.setInputType(TYPE_CLASS_TEXT | TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                } else {
                    editText.setInputType(TYPE_CLASS_TEXT | TYPE_TEXT_VARIATION_PASSWORD);
                }
                editText.setSelection(editText.getText().length());

            }
        });
    }

    private void resetPasswordBySMSCodeBtn() {

        BmobUser.getCurrentUser(RootUser.class).resetPasswordBySMSCode(et_sms_code.getText().toString().trim(), et_c_newpsw.getText().toString(), new UpdateListener() {

            @Override
            public void done(BmobException ex) {
                if (ex == null) {
                    CommonUtil.fecth(ResetPasswordActivity.this);
                    ToastUtil.showToast(ResetPasswordActivity.this, "密码重置成功");
                    ResetPasswordActivity.this.finish();
                } else {
                    ToastUtil.showToast(ResetPasswordActivity.this, "密码重置失败：" + "原因是：" + ex.getLocalizedMessage() + ex.getErrorCode());

                }

            }
        });

    }


    private void getCode() {
        if (TextUtils.isEmpty(et_newpsw.getText()) || et_newpsw.getText().toString().equals("新密码")) {
            ToastUtil.showToast(this, "请输入新密码");
            return;
        }

        if (TextUtils.isEmpty(et_c_newpsw.getText()) || et_c_newpsw.getText().toString().equals("确认密码")) {
            ToastUtil.showToast(this, "请确认新密码");
            return;
        }

        if (et_userPhonenumber.getText().toString().equals("手机号码") || et_userPhonenumber.getText().toString().equals("") || et_userPhonenumber.getText().length() > 11) {
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
        if (!et_userPhonenumber.getText().toString().equals(BmobUser.getCurrentUser(RootUser.class).getMobilePhoneNumber())) {
            ToastUtil.showToast(this, "旧手机号不匹配");
            return;
        }
        BmobSMS.requestSMSCode(et_userPhonenumber.getText().toString(),
                "您的验证码是`%smscode%`，有效期为`%ttl%`分钟。您正在使用`%appname%`的验证码。【比目科技】", new QueryListener<Integer>() {

                    @Override
                    public void done(Integer o, BmobException e) {
                        if (e == null) {
                            ToastUtil.showToast(ResetPasswordActivity.this, "短信验证码已经发送,序列号是：" + o);
                        } else {
                            ToastUtil.showToast(ResetPasswordActivity.this, "短信验证码发送失败，原因是" + e.getLocalizedMessage());

                        }
                    }
                });

    }

    private void verifyAndResetPsw() {

        if (TextUtils.isEmpty(et_sms_code.getText()) || et_sms_code.getText().toString().equals("请输入短信验证码")) {
            ToastUtil.showToast(this, "请输入短信验证码");
            return;
        }

        BmobSMS.verifySmsCode(et_userPhonenumber.getText().toString(), et_sms_code.getText().toString(), new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    ToastUtil.showToast(ResetPasswordActivity.this, "验证成功，正在重置密码");
                    resetPasswordBySMSCodeBtn();
                } else {
                    ToastUtil.showToast(ResetPasswordActivity.this, "验证失败，原因是：" + e.getLocalizedMessage());

                }
            }
        });


    }


}
