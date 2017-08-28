package com.act.quzhibo.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.act.quzhibo.R;
import com.act.quzhibo.common.Constants;

/**
 * Created by weiminglin on 17/7/30.
 */

public class LoadNetView extends LinearLayout {

    private  LinearLayout vipNulllayout;
    private Button reloadbutton;
    private Button buybutton;
    private LinearLayout loadlayout;
    private LinearLayout reloadlayout;

    public LoadNetView(Context context, AttributeSet attrs) {
        super(context, attrs);

        // 加载布局
        LayoutInflater.from(context).inflate(R.layout.load_net_view, this);

        // 获取控件
        reloadlayout = (LinearLayout) findViewById(R.id.reloadlayout);
        reloadlayout = (LinearLayout) findViewById(R.id.reloadlayout);
        reloadbutton = (Button) findViewById(R.id.relaodbutton);
        buybutton = (Button) findViewById(R.id.buybutton);

        loadlayout = (LinearLayout) findViewById(R.id.loadlayout);
        vipNulllayout = (LinearLayout) findViewById(R.id.VipNulllayout);

    }

    // 为左侧返回按钮添加自定义点击事件
    public void setReloadButtonListener(OnClickListener listener) {
        reloadbutton.setOnClickListener(listener);
    }
    // 为左侧返回按钮添加自定义点击事件
    public void setBuyButtonListener(OnClickListener listener) {
        buybutton.setOnClickListener(listener);
    }

    public void setlayoutVisily(int loadType) {
        if (loadType == Constants.LOAD) {
            reloadlayout.setVisibility(View.GONE);
            loadlayout.setVisibility(View.VISIBLE);
        } else if(loadType == Constants.RELOAD){
            reloadlayout.setVisibility(View.VISIBLE);
            loadlayout.setVisibility(View.GONE);
        }else if(loadType == Constants.BUY_VIP){
            loadlayout.setVisibility(View.GONE);
            vipNulllayout.setVisibility(View.VISIBLE);
        }else if(loadType == Constants.PHOTO_ALBUM){
            loadlayout.setVisibility(View.GONE);
            vipNulllayout.setVisibility(View.VISIBLE);
        }else if(loadType == Constants.VIDEO_ALBUM){
            loadlayout.setVisibility(View.GONE);
            vipNulllayout.setVisibility(View.VISIBLE);
        }
    }

}
