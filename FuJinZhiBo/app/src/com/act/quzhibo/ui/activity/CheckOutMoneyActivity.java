package com.act.quzhibo.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.act.quzhibo.R;
import com.act.quzhibo.widget.TitleBarView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;

public class CheckOutMoneyActivity extends FragmentActivity {
    @Bind(R.id.payAccountET)
    EditText payAccountET;
    @Bind(R.id.lockPayAccount)
    Button lockPayAccount;
    @Bind(R.id.payAccountNameET)
    EditText payAccountNameET;
    @Bind(R.id.lockPayNameAccount)
    Button lockPayNameAccount;
    @Bind(R.id.monthdate5)
    TextView monthdate5;
    @Bind(R.id.checkoutMonthDate5)
    Button checkoutMonthDate5;
    @Bind(R.id.monthdate6)
    TextView monthdate6;
    @Bind(R.id.checkoutMonthDate6)
    Button checkoutMonthDate6;
    @Bind(R.id.monthdate18)
    TextView monthdate18;
    @Bind(R.id.checkoutMonthDate18)
    Button checkoutMonthDate18;
    @Bind(R.id.monthdate19)
    TextView monthdate19;
    @Bind(R.id.checkoutMonthDate19)
    Button checkoutMonthDate19;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.checkoutmoney);
        ButterKnife.bind(this);
        TitleBarView titlebar = (TitleBarView) findViewById(R.id.titlebar);
        titlebar.setBarTitle("申请提现");
        initCheckOutView();
    }
    private void initCheckOutView(){
        Bmob.getServerTime(new QueryListener<Long>() {

            @Override
            public void done(Long time, BmobException e) {
                if(e==null){
                    Calendar now = Calendar.getInstance();
                    now.setTime(new Date(time * 1000L));
                    setView(now);
                }else{
                    Calendar now = Calendar.getInstance();
                    setView(now);
                }
            }
        });
    }

    private void setView(Calendar now) {
        monthdate5.setText((now.get(Calendar.MONTH) + 1)+"月5日");
        monthdate6.setText((now.get(Calendar.MONTH) + 1)+"月6日");
        monthdate18.setText((now.get(Calendar.MONTH) + 1)+"月18日");
        monthdate19.setText((now.get(Calendar.MONTH) + 1)+"月19日");
        if ((now.get(Calendar.DATE)+"").equals("5")){
            checkoutMonthDate5.setEnabled(true);
        }else if((now.get(Calendar.DATE)+"").equals("6")){
            checkoutMonthDate6.setEnabled(true);
        }
        else if ((now.get(Calendar.DATE)+"").equals("18")){
            checkoutMonthDate18.setEnabled(true);
        }
        else if ((now.get(Calendar.DATE)+"").equals("19")){
            checkoutMonthDate19.setEnabled(true);
        }
    }
}
