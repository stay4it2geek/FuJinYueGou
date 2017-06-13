package com.act.quzhibo.ui.activity;

import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.act.quzhibo.R;
import com.act.quzhibo.adapter.InterestPostListAdapter;
import com.act.quzhibo.common.Constants;
import com.act.quzhibo.entity.InterestPost;
import com.act.quzhibo.entity.InterstPostResult;
import com.act.quzhibo.entity.InterstUser;
import com.act.quzhibo.okhttp.OkHttpUtils;
import com.act.quzhibo.okhttp.callback.StringCallback;
import com.act.quzhibo.ui.fragment.InterestPostFragment;
import com.act.quzhibo.ui.fragment.PostDetailFragment;
import com.act.quzhibo.util.CommonUtil;
import com.bumptech.glide.Glide;

import io.github.rockerhieu.emojicon.util.Utils;
import okhttp3.Call;

/**
 * Created by asus-pc on 2017/5/30.
 * 用户界面
 */

public class UserInfoActivity extends AppCompatActivity {
    InterstUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        if (getIntent() != null) {
            user= (InterstUser) getIntent().getSerializableExtra(Constants.POST_USER);
        }
        Display display = this.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int height= size.x;
        findViewById(R.id.userImage).setLayoutParams(
                new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, height));
        Glide.with(this).load(user.photoUrl).into((ImageView) findViewById(R.id.userImage));

    }

    private void getTextAndImageData(final int what) {
        String url = CommonUtil.getToggle(this, Constants.TEXT_IMG_POST).getToggleObject().replace("USEID", user.userId);
        OkHttpUtils.get().url(url).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                handler.sendEmptyMessage(Constants.NetWorkError);
            }

            @Override
            public void onResponse(String response, int id) {
                Message message = handler.obtainMessage();
                message.obj = response;
                message.what = what;
                handler.sendMessage(message);
            }
        });
    }

    private void getVideoData(final int what) {
        String url = CommonUtil.getToggle(this, Constants.VIDEO_POST).getToggleObject().replace("USEID", user.userId);
        OkHttpUtils.get().url(url).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                handler.sendEmptyMessage(Constants.NetWorkError);
            }

            @Override
            public void onResponse(String response, int id) {
                Message message = handler.obtainMessage();
                message.obj = response;
                message.what = what;
                handler.sendMessage(message);
            }
        });
    }


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            final InterstPostResult interstPostResult = CommonUtil.parseJsonWithGson((String) msg.obj, InterstPostResult.class);
            if (msg.what != Constants.NetWorkError) {




            } else {
                //todo error
            }
        }
    };

}
