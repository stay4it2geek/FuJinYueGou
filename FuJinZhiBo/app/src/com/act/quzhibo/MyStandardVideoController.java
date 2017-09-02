package com.act.quzhibo;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Toast;

import com.act.quzhibo.util.ToastUtil;
import com.devlin_n.videoplayer.controller.StandardVideoController;

/**
 * Created by weiminglin on 17/9/2.
 */

public class MyStandardVideoController extends StandardVideoController implements View.OnClickListener {
    public MyStandardVideoController(@NonNull Context context) {
        super(context);
        moreMenu.setVisibility(GONE);
    }

    public MyStandardVideoController(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        moreMenu.setVisibility(GONE);
    }

    public MyStandardVideoController(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        moreMenu.setVisibility(GONE);
    }

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

    public interface OnStartFullScreenListner {
        void onStartFullScreen();
    }

    public OnStartFullScreenListner onStartFullScreenListner;

    public void setOnStartFullScreenListner(OnStartFullScreenListner onStartFullScreenListner) {
        this.onStartFullScreenListner = onStartFullScreenListner;

    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i != com.devlin_n.videoplayer.R.id.fullscreen && i != com.devlin_n.videoplayer.R.id.back) {
            if (i == com.devlin_n.videoplayer.R.id.lock) {
                this.doLockUnlock();
            } else {
                this.doPauseResume();
            }
        } else if (i == R.id.fullscreen) {
            onStartFullScreenListner.onStartFullScreen();
        }


    }
}
