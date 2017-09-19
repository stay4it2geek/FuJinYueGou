package com.act.quzhibo.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.act.quzhibo.common.Constants;
import com.act.quzhibo.entity.CategoryInfo;
import com.act.quzhibo.entity.PuaCourse;
import com.act.quzhibo.ui.fragment.PuaCoursesCenterFragment;
import com.act.quzhibo.util.CommonUtil;

import java.util.ArrayList;

public class CoursesActivity extends TabSlideSameBaseActivity implements PuaCoursesCenterFragment.OnCallCourseDetailListner{
    ArrayList<CategoryInfo> categoryInfos=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String info = CommonUtil.getToggle(this, Constants.COURSE_CATOGERY_INFO).getToggleObject().toString();
        categoryInfos.addAll(CommonUtil.jsonToArrayList(info, CategoryInfo.class));
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCallDetail(PuaCourse puaCourse) {
        Intent intent = new Intent(this, CourseDetailActivity.class);
        intent.putExtra(Constants.COURSE, puaCourse);
        startActivity(intent);
    }

    @Override
    public ArrayList<Fragment> getFragments() {
        ArrayList<Fragment> mFragments = new ArrayList<>();
        for (CategoryInfo categoryInfo : categoryInfos) {
            PuaCoursesCenterFragment fragment = new PuaCoursesCenterFragment();
            Bundle bundle = new Bundle();
            bundle.putString(Constants.COURSE_CATOGERY_ID, categoryInfo.categoryId);
            fragment.setArguments(bundle);
            mFragments.add(fragment);
        }
        return mFragments;
    }

    @Override
    public String getDialogTitle() {
        return "自嗨不如大胆撩";
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
