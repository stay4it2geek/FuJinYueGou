package com.act.quzhibo.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.act.quzhibo.common.Constants;
import com.act.quzhibo.entity.CourseCategoryInfo;
import com.act.quzhibo.entity.PuaCourses;
import com.act.quzhibo.ui.fragment.CoursesCenterFragment;
import com.act.quzhibo.util.CommonUtil;

import java.util.ArrayList;


public class CoursesActivity extends TabSlideSameBaseActivity implements CoursesCenterFragment.OnCallCourseDetailListner{
    ArrayList<CourseCategoryInfo> courseCategoryInfos;

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String info = CommonUtil.getToggle(this, Constants.COURSE_CATOGERY_INFO).getToggleObject().toString();
        courseCategoryInfos = CommonUtil.jsonToArrayList(info, CourseCategoryInfo.class);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCallDetail(PuaCourses puaCourse) {
        Intent intent = new Intent(this, CourseDetailActivity.class);
        intent.putExtra(Constants.COURSE, puaCourse);
        startActivity(intent);
    }


    @Override
    public ArrayList<Fragment> getFragments() {
        ArrayList<Fragment> mFragments = new ArrayList<>();
        for (CourseCategoryInfo categoryInfo : courseCategoryInfos) {
            CoursesCenterFragment fragment = new CoursesCenterFragment();
            Bundle bundle = new Bundle();
            bundle.putString(Constants.COURSE_CATOGERY_ID, categoryInfo.courseCategoryId);
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
        for (CourseCategoryInfo categoryInfo : courseCategoryInfos) {
            tabTitles.add(categoryInfo.coursesCategoryName);
        }
        return tabTitles;
    }


}
