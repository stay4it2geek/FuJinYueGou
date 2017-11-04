package com.act.quzhibo.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.act.quzhibo.R;
import com.act.quzhibo.common.Constants;
import com.act.quzhibo.bean.CategoryInfo;
import com.act.quzhibo.bean.CommonCourse;
import com.act.quzhibo.event.ChangeEvent;
import com.act.quzhibo.ui.fragment.CoursesCenterFragment;
import com.act.quzhibo.util.CommonUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

public class MoneyCourseActivity extends TabSlideSameBaseActivity {

    ArrayList<CategoryInfo> categoryInfos = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String info = CommonUtil.getToggle(this, Constants.MONEY_CATOGERY_INFO).getToggleObject().toString();
        categoryInfos.addAll(CommonUtil.jsonToArrayList(info, CategoryInfo.class));
        super.onCreate(savedInstanceState);
        findViewById(R.id.course_tab).setVisibility(View.VISIBLE);
        findViewById(R.id.course_tab).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                EventBus.getDefault().post(new ChangeEvent("money"));
                return true;
            }
        });

    }


    @Override
    public ArrayList<Fragment> getFragments() {
        ArrayList<Fragment> mFragments = new ArrayList<>();
        for (CategoryInfo categoryInfo : categoryInfos) {
            CoursesCenterFragment fragment = new CoursesCenterFragment();
            Bundle bundle = new Bundle();
            bundle.putString("courseUiType", "money");
            bundle.putString(Constants.COURSE_CATOGERY_ID, categoryInfo.categoryId);
            fragment.setArguments(bundle);
            mFragments.add(fragment);
        }
        return mFragments;
    }

    @Override
    public String getDialogTitle() {
        return "撸管不如来赚钱";
    }

    @Override
    public String getChangeText() {
        return "切换情感秘籍";
    }

    @Override
    public ArrayList<String> getTabTitles() {
        ArrayList<String> tabTitles = new ArrayList<>();
        for (CategoryInfo categoryInfo : categoryInfos) {
            tabTitles.add(categoryInfo.categoryName);
        }
        return tabTitles;
    }

}
