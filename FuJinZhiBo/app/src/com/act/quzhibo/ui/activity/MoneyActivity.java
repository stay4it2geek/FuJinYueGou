package com.act.quzhibo.ui.activity;

import android.os.Bundle;
import android.support.v4.view.ViewPager;

import com.act.quzhibo.R;
import com.act.quzhibo.util.CommonUtil;
import com.act.quzhibo.util.ViewFindUtils;

/**
 * 赚钱
 */
public class MoneyActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    protected String[] getTitles() {
        return new String[]{"资源", "随笔", "项目"};
    }

}
