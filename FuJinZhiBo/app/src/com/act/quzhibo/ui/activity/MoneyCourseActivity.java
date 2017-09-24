package com.act.quzhibo.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.act.quzhibo.common.Constants;
import com.act.quzhibo.entity.CategoryInfo;
import com.act.quzhibo.entity.CommonCourse;
import com.act.quzhibo.ui.fragment.CoursesCenterFragment;
import com.act.quzhibo.util.CommonUtil;

import java.util.ArrayList;

public class MoneyCourseActivity extends TabSlideSameBaseActivity implements CoursesCenterFragment.OnCallCourseDetailListner {

    ArrayList<CategoryInfo> categoryInfos = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String info = CommonUtil.getToggle(this, Constants.MONEY_CATOGERY_INFO).getToggleObject().toString();
        categoryInfos.addAll(CommonUtil.jsonToArrayList(info, CategoryInfo.class));
        super.onCreate(savedInstanceState);
    }

    @Override
    public ArrayList<Fragment> getFragments() {
        ArrayList<Fragment> mFragments = new ArrayList<>();
        for (CategoryInfo categoryInfo : categoryInfos) {
            CoursesCenterFragment fragment = new CoursesCenterFragment();
            Bundle bundle = new Bundle();
            bundle.putString("courseUiType","money");
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
    public ArrayList<String> getTabTitles() {
        ArrayList<String> tabTitles = new ArrayList<>();
        for (CategoryInfo categoryInfo : categoryInfos) {
            tabTitles.add(categoryInfo.categoryName);
        }
        return tabTitles;
    }

    @Override
    public void onCallDetail(CommonCourse moneyCourse) {
        Intent intent = new Intent(this, CourseDetailActivity.class);
        intent.putExtra("courseUiType","money");
        intent.putExtra(Constants.COURSE, moneyCourse);
        startActivity(intent);
    }

}