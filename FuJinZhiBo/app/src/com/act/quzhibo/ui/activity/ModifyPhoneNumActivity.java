package com.act.quzhibo.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.act.quzhibo.R;
import com.act.quzhibo.entity.RootUser;
import com.act.quzhibo.view.TitleBarView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.UpdateListener;

public class ModifyPhoneNumActivity extends AppCompatActivity {
    private EditText et_oldPhone;
    private EditText et_c_newPhone;
    private EditText et_newPhone;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modifyphonenumber);
        et_oldPhone = (EditText) findViewById(R.id.et_oldPhone);
        et_newPhone = (EditText) findViewById(R.id.et_newPhone);
        et_c_newPhone = (EditText) findViewById(R.id.et_c_newPhone);

        TitleBarView titlebar = (TitleBarView) findViewById(R.id.titlebar);
        titlebar.setBarTitle("重 置 手 机 号");
        titlebar.setBackButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ModifyPhoneNumActivity.this.finish();
            }
        });

        findViewById(R.id.btn_modifyPhone).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                modifyPhone();
            }
        });
    }

    private boolean matcherPhone(String userPhonenumber) {
        String regex = "^((13[0-9])|(14[5|7])|(15([0-3]|[5-9]))||(17[4|7])|(18[0,5-9]))\\d{8}$";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(userPhonenumber);
        if (m.find()) {
            return false;
        }
        return true;
    }

    private void modifyPhone() {
        if (TextUtils.isEmpty(et_newPhone.getText()) || et_newPhone.getText().toString().equals("新手机号") || et_oldPhone.getText().length() > 11) {
            Toast.makeText(this, "请输入正确的新手机号", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(et_c_newPhone.getText()) || et_c_newPhone.getText().toString().equals("确认新手机号") || et_oldPhone.getText().length() > 11) {
            Toast.makeText(this, "请确认正确的新手机号", Toast.LENGTH_SHORT).show();
            return;
        }

        if (et_oldPhone.getText().toString().equals("旧手机号") || et_oldPhone.getText().toString().equals("") || et_oldPhone.getText().length() > 11) {
            Toast.makeText(this, "请输入正确的旧手机号码", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!et_newPhone.getText().toString().equals(et_oldPhone.getText().toString())) {
            Toast.makeText(this, "2次输入的新手机号不一致", Toast.LENGTH_SHORT).show();
            return;
        }

        if (matcherPhone(et_newPhone.getText().toString())
                || matcherPhone(et_oldPhone.getText().toString())
                || matcherPhone(et_c_newPhone.getText().toString())) {
            Toast.makeText(this, "请输入国内通用的手机号码", Toast.LENGTH_SHORT).show();
            return;
        }
        RootUser rootUser = BmobUser.getCurrentUser(RootUser.class);
        rootUser.setMobilePhoneNumber(et_c_newPhone.getText().toString());
        BmobUser.getCurrentUser(RootUser.class).update(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    ModifyPhoneNumActivity.this.finish();
                    Toast.makeText(ModifyPhoneNumActivity.this, "手机号码重置成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ModifyPhoneNumActivity.this, "手机号码重置失败，原因是" + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
