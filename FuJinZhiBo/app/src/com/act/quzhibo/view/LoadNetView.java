package com.act.quzhibo.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.act.quzhibo.R;

/**
 * Created by weiminglin on 17/7/30.
 */

public class LoadNetView  extends LinearLayout {


    private  Button relaodbutton;
    private  LinearLayout loadlayout;
    private  LinearLayout reloadlayout;

    public LoadNetView(Context context, AttributeSet attrs) {
            super(context, attrs);

            // 加载布局
            LayoutInflater.from(context).inflate(R.layout.load_net_view, this);

            // 获取控件
            reloadlayout = (LinearLayout) findViewById(R.id.reloadlayout);
        reloadlayout = (LinearLayout) findViewById(R.id.reloadlayout);
        relaodbutton= (Button) findViewById(R.id.relaodbutton);
        loadlayout = (LinearLayout) findViewById(R.id.loadlayout);
        }

        // 为左侧返回按钮添加自定义点击事件
        public void setLeftButtonListener(OnClickListener listener) {
            relaodbutton.setOnClickListener(listener);
        }


        public void setlayoutVisily(int layoutId,int visiType) {
            findViewById(layoutId).setVisibility(visiType);
        }

}
