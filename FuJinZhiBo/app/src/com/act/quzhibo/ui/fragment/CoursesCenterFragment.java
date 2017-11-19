package com.act.quzhibo.ui.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.act.quzhibo.R;
import com.act.quzhibo.adapter.CommonCoursesAdapter;
import com.act.quzhibo.common.Constants;
import com.act.quzhibo.bean.CommonCourse;
import com.act.quzhibo.event.CourseEvent;
import com.act.quzhibo.i.OnQueryDataListner;
import com.act.quzhibo.util.CommonUtil;
import com.act.quzhibo.util.ViewDataUtil;
import com.act.quzhibo.widget.LoadNetView;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import org.greenrobot.eventbus.EventBus;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class CoursesCenterFragment extends Fragment {
    XRecyclerView recyclerView;
    CommonCoursesAdapter courseCenterAdapter;
    LoadNetView loadNetView;
    String lastTime = "";
    ArrayList<CommonCourse> commonCourseList = new ArrayList<>();
    int handlerCourseSize;
    View view;
    String courseCategoryId = "";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_layout, null, false);
        if (getArguments() != null) {
            courseCategoryId = getArguments().getString(Constants.COURSE_CATOGERY_ID);
        }
        recyclerView = (XRecyclerView) view.findViewById(R.id.recyclerview);
        ViewDataUtil.setLayManager(handlerCourseSize, new OnQueryDataListner() {
            @Override
            public void onRefresh() {
                queryCourseData(courseCategoryId, Constants.REFRESH);
            }

            @Override
            public void onLoadMore() {
                queryCourseData(courseCategoryId, Constants.LOADMORE);
            }
        },getActivity(), recyclerView, 1, true, true);

        loadNetView = (LoadNetView) view.findViewById(R.id.loadview);
        loadNetView.setReloadButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadNetView.setlayoutVisily(Constants.LOAD);
                queryCourseData(courseCategoryId, Constants.REFRESH);
            }
        });
        loadNetView.setLoadButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadNetView.setlayoutVisily(Constants.LOAD);
                queryCourseData(courseCategoryId, Constants.REFRESH);
            }
        });

        queryCourseData(courseCategoryId, Constants.REFRESH);
        return view;
    }

    void queryCourseData(String courseCategoryId, final int actionType) {
        List<BmobQuery<CommonCourse>> querise = new ArrayList<>();
        BmobQuery<CommonCourse> query = new BmobQuery<>();
        BmobQuery<CommonCourse> query3 = new BmobQuery<>();
        if (actionType == Constants.LOADMORE) {
            Date date;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                date = sdf.parse(lastTime);
                query.addWhereLessThanOrEqualTo("updatedAt", new BmobDate(date));
                querise.add(query);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        BmobQuery<CommonCourse> query2 = new BmobQuery<>();
        query2.addWhereEqualTo("courseCategoryId", courseCategoryId);
        querise.add(query2);

        BmobQuery<CommonCourse> query4 = new BmobQuery<>();
        query4.addWhereEqualTo("courseUiType", getArguments().getString("courseUiType"));
        querise.add(query4);

        query3.and(querise);
        query3.setLimit(10);
        query3.order("courseAppPrice");

        query3.findObjects(new FindListener<CommonCourse>() {
            @Override
            public void done(List<CommonCourse> list, BmobException e) {
                if (e == null) {
                    if (actionType == Constants.REFRESH) {
                        commonCourseList.clear();
                        if (courseCenterAdapter != null) {
                            courseCenterAdapter.notifyDataSetChanged();
                        }
                    }
                    if (list.size() > 0) {
                        lastTime = list.get(list.size() - 1).getUpdatedAt();
                    }
                    Message message = new Message();
                    message.obj = list;
                    message.what = actionType;
                    handler.sendMessage(message);
                } else {
                    handler.sendEmptyMessage(Constants.NetWorkError);
                }
            }
        });
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            ArrayList<CommonCourse> commonCourses = (ArrayList<CommonCourse>) msg.obj;
            if (msg.what != Constants.NetWorkError) {
                if (commonCourses != null) {
                    commonCourseList.addAll(commonCourses);
                    handlerCourseSize = commonCourses.size();
                } else {
                    handlerCourseSize = 0;
                    if (msg.what == Constants.LOADMORE) {
                        recyclerView.setNoMore(true);
                    }
                }

                if (courseCenterAdapter == null) {
                    courseCenterAdapter = new CommonCoursesAdapter(getActivity(), commonCourseList, getArguments().getString("courseUiType"));
                    recyclerView.setAdapter(courseCenterAdapter);
                    courseCenterAdapter.setOnItemClickListener(new CommonCoursesAdapter.OnRecyclerViewItemClickListener() {
                        @Override
                        public void onItemClick(CommonCourse course) {
                            EventBus.getDefault().post(new CourseEvent(course));
                        }
                    });
                } else {
                    courseCenterAdapter.notifyDataSetChanged();

                }

                loadNetView.setVisibility(View.GONE);
                if (commonCourseList.size() == 0) {
                    loadNetView.setVisibility(View.VISIBLE);
                    loadNetView.setlayoutVisily(Constants.NO_DATA);
                    return;
                }
            } else {
                loadNetView.setVisibility(View.VISIBLE);
                loadNetView.setlayoutVisily(Constants.RELOAD);
            }
        }
    };


}
