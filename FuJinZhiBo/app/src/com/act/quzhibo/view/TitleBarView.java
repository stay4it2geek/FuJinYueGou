package com.act.quzhibo.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.act.quzhibo.R;
import com.act.quzhibo.common.Constants;

/**
 * Created by weiminglin on 17/7/30.
 */

public class TitleBarView extends FrameLayout {

    private TextView barTitle;
    private Button backButton;


    public TitleBarView(Context context, AttributeSet attrs) {
        super(context, attrs);

        // 加载布局
        LayoutInflater.from(context).inflate(R.layout.title_bar_view, this);

        // 获取控件
        backButton = (Button) findViewById(R.id.backbutton);

        barTitle = (TextView) findViewById(R.id.barTitle);


    }

    public void setBarTitle(String title) {
        barTitle.setText(title);
    }

    // 为左侧返回按钮添加自定义点击事件
    public void setBackButtonListener(OnClickListener listener) {
        backButton.setOnClickListener(listener);
    }


}
