package com.act.quzhibo.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.act.quzhibo.R;
import com.act.quzhibo.util.CommonUtil;
import com.act.quzhibo.custom.TitleBarView;

public class TermOfUseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.term_of_use);
        TitleBarView titlebar = (TitleBarView) findViewById(R.id.titlebar);
        TextView textView = (TextView) findViewById(R.id.termofusetext);
        titlebar.setBarTitle("用 户 协 议");
        if (CommonUtil.getToggle(this, "termofuse").getIsOpen().equals("true")) {
            textView.setText(Html.fromHtml(""+CommonUtil.getToggle(this, "termofuse").getToggleObject()+""));
        }
        titlebar.setBackButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TermOfUseActivity.this.finish();
            }
        });
    }

}
