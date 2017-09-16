package com.act.quzhibo.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.act.quzhibo.R;
import com.act.quzhibo.common.Constants;

/**
 * Created by weiminglin on 17/7/30.
 */

public class LoadNetView extends LinearLayout {

    private final LinearLayout photoalbum_layout;
    private  LinearLayout vipNulllayout;
    private TextView noDownloadedDataText;
    private TextView noDownloadingDataText;
    private Button reloadbutton;
    private Button buybutton;
    private LinearLayout loadlayout;
    private TextView  noDataText;
    private LinearLayout reloadlayout;
    private LinearLayout video_album_layout;
    public LoadNetView(Context context, AttributeSet attrs) {
        super(context, attrs);

        // 加载布局
        LayoutInflater.from(context).inflate(R.layout.load_net_view, this);
        noDownloadedDataText = (TextView) findViewById(R.id.noDownloadedDataText);
        reloadlayout = (LinearLayout) findViewById(R.id.reloadlayout);
        video_album_layout = (LinearLayout) findViewById(R.id.video_album_layout);
        reloadbutton = (Button) findViewById(R.id.relaodbutton);
        buybutton = (Button) findViewById(R.id.buybutton);
        photoalbum_layout = (LinearLayout) findViewById(R.id.photoalbum_layout);
        noDownloadingDataText= (TextView) findViewById(R.id.noDownloadingDataText);
        noDataText= (TextView) findViewById(R.id.noDataText);
        loadlayout = (LinearLayout) findViewById(R.id.loadlayout);
        vipNulllayout = (LinearLayout) findViewById(R.id.VipNulllayout);
        video_album_layout = (LinearLayout) findViewById(R.id.video_album_layout);

    }

    // 为左侧返回按钮添加自定义点击事件
    public void setReloadButtonListener(OnClickListener listener) {
        reloadbutton.setOnClickListener(listener);
    }

    public void setlayoutVisily(int loadType) {
        if (loadType == Constants.LOAD) {
            noDataText.setVisibility(GONE);
            reloadlayout.setVisibility(View.GONE);
            loadlayout.setVisibility(View.VISIBLE);
        } else if(loadType == Constants.RELOAD){
            reloadlayout.setVisibility(View.VISIBLE);
            loadlayout.setVisibility(View.GONE);
        }else if(loadType == Constants.BUY_VIP){
            loadlayout.setVisibility(View.GONE);
            vipNulllayout.setVisibility(View.VISIBLE);
        }else if(loadType ==Integer.parseInt(Constants.PHOTO_ALBUM)){
            loadlayout.setVisibility(View.GONE);
            photoalbum_layout.setVisibility(View.VISIBLE);
        }else if(loadType == Integer.parseInt(Constants.VIDEO_ALBUM)){
            loadlayout.setVisibility(View.GONE);
            video_album_layout.setVisibility(View.VISIBLE);
        }else if(loadType == Constants.NO_DOWN_DATA){
            loadlayout.setVisibility(View.GONE);
            noDownloadedDataText.setVisibility(View.VISIBLE);
        }else if(loadType == Constants.NO_DOWNING_DATA){
            loadlayout.setVisibility(View.GONE);
            noDownloadingDataText.setVisibility(View.VISIBLE);
        }else if(loadType == Constants.NO_DATA){
            loadlayout.setVisibility(View.GONE);
            noDataText.setVisibility(View.VISIBLE);
        }
    }

}
