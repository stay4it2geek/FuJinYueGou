package com.act.quzhibo.ui.activity;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.view.View;

import com.act.quzhibo.R;
import com.act.quzhibo.adapter.CoursesOrderAdapter;
import com.act.quzhibo.bean.CommonCourse;
import com.act.quzhibo.bean.Orders;
import com.act.quzhibo.bean.RootUser;
import com.act.quzhibo.common.Constants;
import com.act.quzhibo.event.ChangeEvent;
import com.act.quzhibo.i.OnQueryDataListner;
import com.act.quzhibo.util.ToastUtil;
import com.act.quzhibo.util.ViewDataUtil;
import com.act.quzhibo.widget.FragmentDialog;
import com.act.quzhibo.widget.LoadNetView;
import com.act.quzhibo.widget.TitleBarView;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

public class CourseOrdersActivity extends BaseActivity {

    XRecyclerView recyclerView;
    CoursesOrderAdapter orderAdapter;
    LoadNetView loadNetView;
    ArrayList<Orders> orderList = new ArrayList<>();

    String lastTime = "";
    int handlerOrderSize;
    RootUser user;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_common);
        loadNetView = (LoadNetView) findViewById(R.id.loadview);
        TitleBarView titlebar = (TitleBarView) findViewById(R.id.titlebar);
        titlebar.setBarTitle("订单列表");
        titlebar.setBackButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CourseOrdersActivity.this.finish();
            }
        });
        titlebar.setVisibility(View.VISIBLE);
        recyclerView = (XRecyclerView) findViewById(R.id.recyclerview);
        ViewDataUtil.setLayManager(handlerOrderSize, new OnQueryDataListner() {
            @Override
            public void onRefresh() {
                queryRootUserData(Constants.REFRESH);
            }

            @Override
            public void onLoadMore() {
                queryRootUserData(Constants.LOADMORE);
            }
        }, this, recyclerView, 1, true, true);

        loadNetView.setReloadButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadNetView.setlayoutVisily(Constants.LOAD);
                queryRootUserData(Constants.REFRESH);
            }
        });
        loadNetView.setBuyButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CourseOrdersActivity.this, TabMainActivity.class);
                startActivity(intent);
                EventBus.getDefault().post(new ChangeEvent("buy"));
            }
        });
        user = (RootUser) getIntent().getSerializableExtra("user");
        queryRootUserData(Constants.REFRESH);
    }

    void queryRootUserData(final int type) {
        BmobQuery<Orders> query = new BmobQuery<>();
        query.addWhereEqualTo("user", user);
        query.order("-updatedAt");
        query.findObjects(new FindListener<Orders>() {
            @Override
            public void done(List<Orders> list, BmobException e) {
                if (e == null) {
                    if (list.size() > 0) {
                        queryDatas(type);
                    } else {
                        loadNetView.setlayoutVisily(Constants.BUY_VIP);
                    }
                } else {
                    setAdapterView(Constants.NetWorkError, null);
                }
            }
        });
    }

    void queryDatas(final int actionType) {

        BmobQuery<Orders> query = new BmobQuery<>();
        BmobQuery<Orders> query2 = new BmobQuery<>();
        List<BmobQuery<Orders>> queries = new ArrayList<>();
        query2.setLimit(10);
        if (actionType == Constants.LOADMORE) {
            Date date;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                date = sdf.parse(lastTime);
                query2.addWhereLessThanOrEqualTo("updatedAt", new BmobDate(date));
                queries.add(query2);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        BmobQuery<Orders> query3 = new BmobQuery<>();
        query3.addWhereEqualTo("user", user);
        queries.add(query3);
        query.and(queries);
        query.order("-updatedAt");

        query.findObjects(new FindListener<Orders>() {
            @Override
            public void done(final List<Orders> list, BmobException e) {
                if (e == null) {
                    if (actionType == Constants.REFRESH) {
                        orderList.clear();
                        if (orderAdapter != null) {
                            orderAdapter.notifyDataSetChanged();
                        }
                    }
                    if (list.size() > 0) {
                        lastTime = list.get(list.size() - 1).getUpdatedAt();
                    }
                    setAdapterView(actionType, list);
                } else {
                    setAdapterView(Constants.NetWorkError, null);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (orderAdapter != null) {
            queryRootUserData(Constants.REFRESH);
        }
    }


    void setAdapterView(int what, List<Orders> orderses) {
        if (what != Constants.NetWorkError) {
            if (orderses != null) {
                orderList.addAll(orderses);
                handlerOrderSize = orderses.size();
            } else {
                handlerOrderSize = 0;
            }
            if (orderAdapter == null) {
                orderAdapter = new CoursesOrderAdapter(getIntent().getBooleanExtra("isTeamType", false), CourseOrdersActivity.this);
                orderAdapter.setOrderDatas(orderList);
                recyclerView.setAdapter(orderAdapter);
                if (!getIntent().getBooleanExtra("isTeamType", false)) {
                    orderAdapter.setListener(new CoursesOrderAdapter.OnCourseActionListener() {
                        @Override
                        public void onDelete(final CommonCourse course) {
                            FragmentDialog.newInstance(false, getResources().getString(R.string.isCancelFocus), getResources().getString(R.string.reallyCancelFocus), getResources().getString(R.string.keepFocus), getResources().getString(R.string.cancelFocus), "", "", false, new FragmentDialog.OnClickBottomListener() {
                                @Override
                                public void onPositiveClick(Dialog dialog, boolean deleteFileSource) {
                                    dialog.dismiss();
                                }

                                @Override
                                public void onNegtiveClick(Dialog dialog) {
                                    course.delete(course.getObjectId(), new UpdateListener() {
                                        @Override
                                        public void done(BmobException e) {
                                            if (e == null) {
                                                orderList.remove(course);
                                                orderAdapter.setOrderDatas(orderList);
                                                orderAdapter.notifyDataSetChanged();
                                                if (orderList.size() == 0) {
                                                    loadNetView.setVisibility(View.VISIBLE);
                                                    loadNetView.setlayoutVisily(Constants.BUY_VIP);
                                                    return;
                                                }

                                            } else {
                                                ToastUtil.showToast(CourseOrdersActivity.this, "删除失败");
                                            }
                                        }
                                    });
                                    dialog.dismiss();
                                }
                            }).show(getSupportFragmentManager(), "");
                        }

                        @Override
                        public void onCopyUrl(CommonCourse course) {
                            ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                            ClipData clipData = ClipData.newPlainText("", course.downloadUrl);
                            clipboardManager.setPrimaryClip(clipData);
                            ToastUtil.showToast(CourseOrdersActivity.this, "复制下载网址成功");

                        }

                        @Override
                        public void onCopyPsw(CommonCourse course) {
                            ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                            ClipData clipData = ClipData.newPlainText("", course.downloadPsw);
                            clipboardManager.setPrimaryClip(clipData);
                            ToastUtil.showToast(CourseOrdersActivity.this, "复制提取密码成功");

                        }

                        @Override
                        public void onSave2File(CommonCourse course) {
                            try {
                                File file = new File(Environment.getExternalStorageDirectory(), "课程提取信息.txt");
                                FileOutputStream outStream = new FileOutputStream(file);
                                outStream.write(("课程下载网址：" + course.downloadUrl + "  课程提取密码：" + course.downloadPsw).getBytes());
                                outStream.close();
                                ToastUtil.showToast(CourseOrdersActivity.this, "已保存到" + file.getAbsolutePath());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            } else {
                orderAdapter.notifyDataSetChanged();
            }
            if (what == Constants.LOADMORE) {
                recyclerView.setNoMore(true);
            }
            loadNetView.setVisibility(View.GONE);
            if (orderList.size() == 0) {
                loadNetView.setVisibility(View.VISIBLE);
                loadNetView.setlayoutVisily(Constants.NO_DATA);
                return;
            }
        } else {
            loadNetView.setVisibility(View.VISIBLE);
            loadNetView.setlayoutVisily(Constants.RELOAD);
        }
    }
}
