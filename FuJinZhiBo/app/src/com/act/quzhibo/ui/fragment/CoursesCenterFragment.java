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
import android.widget.AdapterView;

import com.act.quzhibo.R;
import com.act.quzhibo.adapter.CourseCategoryAdapter;
import com.act.quzhibo.adapter.CoursesAdapter;
import com.act.quzhibo.adapter.MemberAdapter;
import com.act.quzhibo.common.Constants;
import com.act.quzhibo.entity.CourseCategoryInfo;
import com.act.quzhibo.entity.PuaCourses;
import com.act.quzhibo.entity.MyPost;
import com.act.quzhibo.util.CommonUtil;
import com.act.quzhibo.view.HorizontialListView;
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
    private CoursesAdapter courseCenterAdapter;
    private LoadNetView loadNetView;
    private String lastTime = "";
    ArrayList<CourseCategoryInfo> courseCategoryInfos;
    private ArrayList<PuaCourses> puaCoursesList = new ArrayList<>();
    private int puaCourseSize;
    private View view;
    HorizontialListView courseHorizontialListView;
    String courseCategoryId = "1";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        view = LayoutInflater.from(getActivity()).inflate(R.layout.layout_course, null, false);
        courseHorizontialListView = (HorizontialListView) view.findViewById(R.id.course_tab_lv);

        String catogeryInfos = CommonUtil.getToggle(getActivity(), Constants.COURSE_CATOGERY_INFO).getToggleObject().toString();
        courseCategoryInfos = CommonUtil.jsonToArrayList(catogeryInfos, CourseCategoryInfo.class);
        CourseCategoryAdapter courseCategoryAdapter = new CourseCategoryAdapter(courseCategoryInfos, getActivity());

        courseHorizontialListView.setAdapter(courseCategoryAdapter);
        courseHorizontialListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                courseCategoryId=courseCategoryInfos.get(position).courseCategoryId;
                queryCourseData(courseCategoryId, Constants.REFRESH);
            }
        });

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
                        if (puaCourseSize > 0) {
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
        return view;
    }


    private void queryCourseData(String courseCategoryId, final int actionType) {
        loadNetView.setVisibility(View.VISIBLE);
        loadNetView.setlayoutVisily(Constants.LOAD);
        List<BmobQuery<PuaCourses>> querise = new ArrayList<>();
        BmobQuery<PuaCourses> query = new BmobQuery<>();
        BmobQuery<PuaCourses> query3 = new BmobQuery<>();

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
        BmobQuery<PuaCourses> query2 = new BmobQuery<>();
        query2.addWhereEqualTo("courseCategoryId", courseCategoryId);
        querise.add(query2);

        query3.and(querise);
        query3.setLimit(10);
        query3.order("courseAppPrice");

        query3.findObjects(new FindListener<PuaCourses>() {
            @Override
            public void done(List<PuaCourses> list, BmobException e) {
                if (e == null) {
                    if (actionType == Constants.REFRESH) {
                        puaCoursesList.clear();
                        if(courseCenterAdapter!=null){
                            courseCenterAdapter.notifyDataSetChanged();
                        }
                    }
                    if(list.size()>0){
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
            ArrayList<PuaCourses> puaCourses = (ArrayList<PuaCourses>) msg.obj;
            if (msg.what != Constants.NetWorkError) {
                if (puaCourses != null) {
                    puaCoursesList.addAll(puaCourses);
                    puaCourseSize = puaCourses.size();
                } else {
                    puaCourseSize = 0;
                }

                if (courseCenterAdapter == null) {
                    courseCenterAdapter = new CoursesAdapter(getActivity(), puaCoursesList);
                    recyclerView.setAdapter(courseCenterAdapter);
                } else {
                    courseCenterAdapter.notifyDataSetChanged();
                }

                loadNetView.setVisibility(View.GONE);
                if (puaCoursesList.size() == 0) {
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
