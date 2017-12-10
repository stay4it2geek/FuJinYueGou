package com.act.quzhibo.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.TextView;

import com.act.quzhibo.R;
import com.act.quzhibo.bean.RootUser;
import com.act.quzhibo.widget.CircleImageView;
import com.act.quzhibo.widget.TitleBarView;
import com.bumptech.glide.Glide;
import com.mabeijianxi.smallvideorecord2.StringUtils;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobUser;

public class CommisionActivity extends FragmentActivity {

    @Bind(R.id.iv_avtar)
    public CircleImageView circleImageView;
    @Bind(R.id.coinsCount)
    public TextView coinsCount;
    @Bind(R.id.commission_total)
    public TextView commission_total;
    @Bind(R.id.titlebar)
    public TitleBarView titlebar;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.commission);
        ButterKnife.bind(this);
        titlebar.setBarTitle("我 的 收 益");
        titlebar.setBackButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        RootUser user = BmobUser.getCurrentUser(RootUser.class);
        Glide.with(this).load(user.photoFileUrl).error(R.drawable.error_img).into(circleImageView);
        commission_total.setText(getIntent().getDoubleExtra("proMoneyTotal",0.0)+"");
        coinsCount.setText((user.vipConis==0 || user.vipConis == null)?"0":user.vipConis+"");
    }

    @OnClick({R.id.checkout, R.id.change})
    public void btnClicks(View view) {

    }
}
