package com.act.quzhibo;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.PopupMenu;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.act.quzhibo.common.Constants;
import com.act.quzhibo.download.callback.OnVideoControllerListner;
import com.act.quzhibo.util.ToastUtil;
import com.devlin_n.videoplayer.controller.StandardVideoController;

/**
 * Created by weiminglin on 17/9/2.
 */

public class MyStandardVideoController extends StandardVideoController implements View.OnClickListener {
    public MyStandardVideoController(@NonNull Context context) {
        super(context);
    }

    public MyStandardVideoController(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MyStandardVideoController(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }

    protected PopupMenu popupMenu;

    private void doLockUnlock() {
        if (this.isLocked) {
            this.isLocked = false;
            this.mShowing = false;
            this.gestureEnabled = true;
            this.show();
            this.lock.setSelected(false);
            Toast.makeText(this.getContext(), com.devlin_n.videoplayer.R.string.unlocked, Toast.LENGTH_LONG).show();
        } else {
            this.hide();
            this.isLocked = true;
            this.gestureEnabled = false;
            this.lock.setSelected(true);
            Toast.makeText(this.getContext(), com.devlin_n.videoplayer.R.string.locked, Toast.LENGTH_LONG).show();
        }

        this.mediaPlayer.setLock(this.isLocked);
    }



    public OnVideoControllerListner onVideoControllerListner;

    public void setOnVideoControllerListner(OnVideoControllerListner onVideoControllerListner) {
        this.onVideoControllerListner = onVideoControllerListner;

    }


    public void onClick(View v) {
        int i = v.getId();
        if(i != com.devlin_n.videoplayer.R.id.fullscreen && i != com.devlin_n.videoplayer.R.id.back) {
            if(i == com.devlin_n.videoplayer.R.id.lock) {
                this.doLockUnlock();
            } else if(i != com.devlin_n.videoplayer.R.id.iv_play && i != com.devlin_n.videoplayer.R.id.thumb && i != com.devlin_n.videoplayer.R.id.iv_replay) {
                if(i == com.devlin_n.videoplayer.R.id.more_menu) {
                    this.popupMenu.show();
                    this.show();
                }
            }else {
                this.doPauseResume();
            }
        }else if (i == R.id.fullscreen) {
            onVideoControllerListner.onMyVideoController(Constants.FULL_SCREEN);
        }

    }

    @Override
    protected void initView() {
        super.initView();
        findViewById(R.id.more_menu).setVisibility(VISIBLE);
        this.popupMenu = new PopupMenu(this.getContext(), this.moreMenu, Gravity.RIGHT);
        this.popupMenu.getMenuInflater().inflate(R.menu.controller_menu_list, this.popupMenu.getMenu());
        this.popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                int itemId = item.getItemId();
                if(itemId == R.id.fullscreen) {
                    onVideoControllerListner.onMyVideoController(Constants.FULL_SCREEN);
                } else if(itemId == R.id.download) {
                    onVideoControllerListner.onMyVideoController(Constants.DOWNLAOD_VIDEO);
                }
                MyStandardVideoController.this.popupMenu.dismiss();
                return false;
            }
        });
    }
}
