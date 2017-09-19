package com.act.quzhibo.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.act.quzhibo.common.Constants;
import com.act.quzhibo.entity.CategoryInfo;
import com.act.quzhibo.entity.MoneyCourse;
import com.act.quzhibo.entity.PuaCourse;
import com.act.quzhibo.ui.fragment.MoneyCoursesCenterFragment;
import com.act.quzhibo.ui.fragment.PuaCoursesCenterFragment;
import com.act.quzhibo.util.CommonUtil;

import java.util.ArrayList;

public class MoneyActivity  extends TabSlideSameBaseActivity implements MoneyCoursesCenterFragment.OnCallCourseDetailListner{
    ArrayList<CategoryInfo> categoryInfos;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String info = CommonUtil.getToggle(this, Constants.MONEY_CATOGERY_INFO).getToggleObject().toString();
        categoryInfos = CommonUtil.jsonToArrayList(info, CategoryInfo.class);
        super.onCreate(savedInstanceState);
    }



    @Override
    public ArrayList<Fragment> getFragments() {
        ArrayList<Fragment> mFragments = new ArrayList<>();
        for (CategoryInfo categoryInfo : categoryInfos) {
            PuaCoursesCenterFragment fragment = new PuaCoursesCenterFragment();
            Bundle bundle = new Bundle();
            bundle.putString(Constants.COURSE_CATOGERY_ID, categoryInfo.courseCategoryId);
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
            tabTitles.add(categoryInfo.coursesCategoryName);
        }
        return tabTitles;
    }

    @Override
    public void onCallDetail(MoneyCourse moneyCourse) {
        Intent intent = new Intent(this, CourseDetailActivity.class);
        intent.putExtra(Constants.COURSE, moneyCourse);
        startActivity(intent);
    }
}
