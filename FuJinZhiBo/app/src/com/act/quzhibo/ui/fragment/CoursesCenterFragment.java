package com.act.quzhibo.ui.fragment;

import android.content.Context;
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
import com.act.quzhibo.entity.CommonCourse;
import com.act.quzhibo.ui.activity.MoneyCourseActivity;
import com.act.quzhibo.ui.activity.PuaCoursesActivity;
import com.act.quzhibo.view.LoadNetView;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

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
    private XRecyclerView recyclerView;
    private CommonCoursesAdapter courseCenterAdapter;
    private LoadNetView loadNetView;
    private String lastTime = "";
    private ArrayList<CommonCourse> commonCourseList = new ArrayList<>();
    private int handlerCourseSize;
    private View view;
    String courseCategoryId = "";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        view = LayoutInflater.from(getActivity()).inflate(R.layout.layout_course, null, false);
        if(getArguments()!=null){
            courseCategoryId=getArguments().getString(Constants.COURSE_CATOGERY_ID);
        }
        recyclerView = (XRecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setPullRefreshEnabled(true);
        recyclerView.setLoadingMoreEnabled(true);
        recyclerView.setLoadingMoreProgressStyle(R.style.Small);
        recyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        recyclerView.setNoMore(false);
                        recyclerView.setLoadingMoreEnabled(true);
                        queryCourseData(courseCategoryId, Constants.REFRESH);
                        recyclerView.refreshComplete();
                    }
                }, 1000);
            }

            @Override
            public void onLoadMore() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (handlerCourseSize > 0) {
                            queryCourseData(courseCategoryId, Constants.LOADMORE);
                            recyclerView.loadMoreComplete();
                        } else {
                            recyclerView.setNoMore(true);
                        }
                    }
                }, 1000);
            }
        });
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 1);
        gridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(gridLayoutManager);
        loadNetView = (LoadNetView) view.findViewById(R.id.loadview);
        loadNetView.setReloadButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadNetView.setlayoutVisily(Constants.LOAD);
                queryCourseData(courseCategoryId, Constants.REFRESH);
            }
        });
        queryCourseData(courseCategoryId, Constants.REFRESH);


        loadNetView.setLoadButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadNetView.setlayoutVisily(Constants.LOAD);
                queryCourseData(courseCategoryId, Constants.REFRESH);
            }
        });
        return view;
    }

    private void queryCourseData(String courseCategoryId, final int actionType) {
        loadNetView.setVisibility(View.VISIBLE);
        loadNetView.setlayoutVisily(Constants.LOAD);
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
                    if(msg.what==Constants.LOADMORE){
                        recyclerView.setNoMore(true);
                    }
                }

                if (courseCenterAdapter == null) {
                        courseCenterAdapter = new CommonCoursesAdapter(getActivity(), commonCourseList,getArguments().getString("courseUiType"));
                        recyclerView.setAdapter(courseCenterAdapter);
                        courseCenterAdapter.setOnItemClickListener(new CommonCoursesAdapter.OnRecyclerViewItemClickListener() {
                            @Override
                            public void onItemClick(CommonCourse course) {
                                listner.onCallDetail(course);
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

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof PuaCoursesActivity||context instanceof MoneyCourseActivity) {
            listner = (OnCallCourseDetailListner) context;
        }
    }

    OnCallCourseDetailListner listner;

    public interface OnCallCourseDetailListner {
        void onCallDetail(CommonCourse commonCourse);
    }
}
