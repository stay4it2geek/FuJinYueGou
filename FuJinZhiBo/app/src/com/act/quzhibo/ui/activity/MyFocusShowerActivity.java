package com.act.quzhibo.ui.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Display;
import android.view.View;

import com.act.quzhibo.R;
import com.act.quzhibo.adapter.MyFocusShowerListAdapter;
import com.act.quzhibo.bean.MyFocusCommonPerson;
import com.act.quzhibo.bean.RootUser;
import com.act.quzhibo.common.Constants;
import com.act.quzhibo.common.OkHttpClientManager;
import com.act.quzhibo.bean.MyFocusShower;
import com.act.quzhibo.bean.Room;
import com.act.quzhibo.util.CommonUtil;
import com.act.quzhibo.util.ToastUtil;
import com.act.quzhibo.widget.FragmentDialog;
import com.act.quzhibo.widget.LoadNetView;
import com.act.quzhibo.widget.TitleBarView;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;


public class MyFocusShowerActivity extends FragmentActivity {

    private XRecyclerView recyclerView;
    private MyFocusShowerListAdapter myFocusShowerListAdapter;
    private LoadNetView loadNetView;
    private String lastTime = "";
    private ArrayList<MyFocusShower> myFocusShowerses = new ArrayList<>();
    private int myfocusSize;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_common);
        TitleBarView titlebar = (TitleBarView) findViewById(R.id.titlebar);
        titlebar.setVisibility(View.VISIBLE);
        titlebar.setBarTitle("我关注的主播");
        titlebar.setBackButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyFocusShowerActivity.this.finish();
            }
        });
        loadNetView = (LoadNetView) findViewById(R.id.loadview);
        recyclerView = (XRecyclerView) findViewById(R.id.recycler_view);
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
                        queryData(Constants.REFRESH);
                        recyclerView.refreshComplete();
                    }
                }, 1000);
            }

            @Override
            public void onLoadMore() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (myfocusSize > 0) {
                            queryData(Constants.LOADMORE);
                            recyclerView.loadMoreComplete();
                        } else {
                            recyclerView.setNoMore(true);
                        }
                    }
                }, 1000);
            }
        });
        GridLayoutManager gridLayoutManager = new GridLayoutManager(MyFocusShowerActivity.this, 2);
        gridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(gridLayoutManager);
        loadNetView.setLoadButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadNetView.setlayoutVisily(Constants.LOAD);
                queryData(Constants.REFRESH);
            }
        });
        loadNetView.setLoadButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadNetView.setlayoutVisily(Constants.LOAD);
                queryData(Constants.REFRESH);
            }
        });
    }

    private void queryData(final int actionType) {
        BmobQuery<MyFocusShower> query = new BmobQuery<>();

        List<BmobQuery<MyFocusShower>> queries = new ArrayList<>();


        if (actionType == Constants.LOADMORE) {
            Date date = null;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                date = sdf.parse(lastTime);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            query.addWhereLessThanOrEqualTo("updatedAt", new BmobDate(date));
        }

        BmobQuery<MyFocusShower> query2 = new BmobQuery<>();

        query2.addWhereEqualTo("rootUser", BmobUser.getCurrentUser(RootUser.class));
        queries.add(query2);
        query.and(queries);
        query.order("-updatedAt");
        query.findObjects(new FindListener<MyFocusShower>() {
            @Override
            public void done(List<MyFocusShower> list, BmobException e) {
                if (e == null) {

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
            ArrayList<MyFocusShower> showerses = (ArrayList<MyFocusShower>) msg.obj;
            if (msg.what != Constants.NetWorkError) {
                if (msg.what == Constants.REFRESH) {
                    myFocusShowerses.clear();
                }
                if (showerses != null) {
                    myFocusShowerses.addAll(showerses);
                    myfocusSize = showerses.size();
                } else {
                    myfocusSize = 0;
                }

                if (myFocusShowerListAdapter == null) {
                    Display display = MyFocusShowerActivity.this.getWindowManager().getDefaultDisplay();
                    Point size = new Point();
                    display.getSize(size);
                    int screenWidth = size.x;
                    myFocusShowerListAdapter = new MyFocusShowerListAdapter(MyFocusShowerActivity.this, myFocusShowerses, screenWidth);
                    recyclerView.setAdapter(myFocusShowerListAdapter);
                    myFocusShowerListAdapter.setOnItemClickListener(new MyFocusShowerListAdapter.OnRecyclerViewItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position, final MyFocusShower myFocusShower) {
                            requestInfo(myFocusShower.userId);
                        }
                    });
                    if (myFocusShowerListAdapter != null) {
                        myFocusShowerListAdapter.setDeleteListener(new MyFocusShowerListAdapter.OnDeleteListener() {
                            @Override
                            public void onDelete(final int position) {
                                FragmentDialog.newInstance(false, "是否取消关注", "真的要取消关注人家吗", "取消", "确定","","",false, new FragmentDialog.OnClickBottomListener() {
                                    @Override
                                    public void onPositiveClick(Dialog dialog, boolean deleteFileSource) {
                                        dialog.dismiss();
                                    }

                                    @Override
                                    public void onNegtiveClick(Dialog dialog) {
                                        myFocusShowerses.get(position).delete(myFocusShowerses.get(position).getObjectId(), new UpdateListener() {
                                            @Override
                                            public void done(BmobException e) {
                                                if (e == null) {
                                                    myFocusShowerses.remove(position);
                                                    myFocusShowerListAdapter.notifyDataSetChanged();
                                                    if (myFocusShowerses.size() == 0) {
                                                        loadNetView.setVisibility(View.VISIBLE);
                                                        loadNetView.setlayoutVisily(Constants.NO_DATA);
                                                        return;
                                                    }
                                                }
                                            }
                                        });
                                        dialog.dismiss();
                                    }
                                }).show(getSupportFragmentManager(), "");
                            }
                        });
                    }
                } else {
                    myFocusShowerListAdapter.notifyDataSetChanged();
                }

                if (msg.what == Constants.LOADMORE) {
                    recyclerView.setNoMore(true);
                }
                loadNetView.setVisibility(View.GONE);
                if (myFocusShowerses.size() == 0) {
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
    protected void onResume() {
        super.onResume();
        queryData(Constants.REFRESH);
    }

    Handler infoHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            try {
                JSONObject jsonObject = new JSONObject((String) msg.obj);
                Room room = new Room();
                room.screenType = jsonObject.getString("screenType");
                room.roomId = jsonObject.getString("roomId");
                room.nickname = jsonObject.getString("nickname");
                room.userId = jsonObject.getString("userId");
                room.gender = jsonObject.getString("gender");
                room.liveStream = "http://pull.kktv8.com/livekktv/" + jsonObject.getString("roomId") + ".flv";
                room.city = jsonObject.getString("city");
                room.liveType = jsonObject.getString("liveType");
                room.nickname = jsonObject.getString("nickname");
                room.gender = jsonObject.getString("gender");
                room.onlineCount = jsonObject.getString("onlineCount");
                room.portrait_path_1280 = jsonObject.getString("portrait_path_1280");
                Intent intent;
                if (room.screenType.equals(Constants.LANSPACE)) {
                    if (room.liveType.equals(Constants.LANSPACE_IS_LIVE)) {
                        intent = new Intent(MyFocusShowerActivity.this, VideoPlayerActivity.class);
                    } else {
                        intent = new Intent(MyFocusShowerActivity.this, ShowerInfoActivity.class);
                        ToastUtil.showToast(MyFocusShowerActivity.this, "该主播未直播哦");
                    }
                } else {
                    if (room.liveType.equals(Constants.PORTAIL_IS_LIVE)) {
                        intent = new Intent(MyFocusShowerActivity.this, VideoPlayerActivity.class);
                    } else {
                        intent = new Intent(MyFocusShowerActivity.this, ShowerInfoActivity.class);
                        ToastUtil.showToast(MyFocusShowerActivity.this, "该主播未直播哦");
                    }
                }
                intent.putExtra("room", room);
                startActivity(intent);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };


    private void requestInfo(String showerId) {
        String url = CommonUtil.getToggle(MyFocusShowerActivity.this, Constants.SHOWER_INFO).getToggleObject().replace("USERID", showerId);
        OkHttpClientManager.parseRequest(this, url, infoHandler, Constants.REFRESH);
    }


}
