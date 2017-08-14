package com.act.quzhibo.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.act.quzhibo.R;
import com.act.quzhibo.entity.CardBean;
import com.bigkoo.pickerview.OptionsPickerView;
import com.bigkoo.pickerview.listener.CustomListener;

import org.angmarch.views.NiceSpinner;

import java.util.ArrayList;

public class SettingMineInfoActivity extends AppCompatActivity {
    private OptionsPickerView pvCustomOptions;
    private ArrayList<CardBean> cardItem = new ArrayList<>();
    NiceSpinner niceSpinner;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_mine_info);
        niceSpinner = (NiceSpinner) findViewById(R.id.sex_Spinner);
        getCardData();
        initCustomOptionPicker();
        niceSpinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pvCustomOptions.show();
            }
        });
    }

    private void initCustomOptionPicker() {
        pvCustomOptions = new OptionsPickerView.Builder(this, new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3, View v) {
                String tx = cardItem.get(options1).getPickerViewText();
                niceSpinner.setText(tx);
            }
        }).setLayoutRes(R.layout.pickerview_custom_options, new CustomListener() {
            @Override
            public void customLayout(View v) {
                final TextView tvSubmit = (TextView) v.findViewById(R.id.tv_finish);
                TextView ivCancel = (TextView) v.findViewById(R.id.tv_cancel);
                tvSubmit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pvCustomOptions.returnData();
                        pvCustomOptions.dismiss();
                    }
                });
                ivCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pvCustomOptions.dismiss();
                    }
                });
            }
        }).isDialog(true).build();
        pvCustomOptions.setPicker(cardItem);//添加数据
    }


    private void getCardData() {
        cardItem.add(new CardBean(0, "男"));
        cardItem.add(new CardBean(1, "女"));
    }
}
