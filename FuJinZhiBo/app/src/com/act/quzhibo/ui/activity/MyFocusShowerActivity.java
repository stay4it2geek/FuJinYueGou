package com.act.quzhibo.ui.activity;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Display;
import android.view.View;

import com.act.quzhibo.R;
import com.act.quzhibo.adapter.MyFocusShowerListAdapter;
import com.act.quzhibo.common.Constants;
import com.act.quzhibo.entity.MyFocusShowers;
import com.act.quzhibo.entity.Room;
import com.act.quzhibo.okhttp.OkHttpUtils;
import com.act.quzhibo.okhttp.callback.StringCallback;
import com.act.quzhibo.util.CommonUtil;
import com.act.quzhibo.util.ToastUtil;
import com.act.quzhibo.view.LoadNetView;
import com.act.quzhibo.view.TitleBarView;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

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
import okhttp3.Call;


public class MyFocusShowerActivity extends AppCompatActivity {

    private XRecyclerView recyclerView;
    private MyFocusShowerListAdapter myFocusShowerListAdapter;
    private LoadNetView loadNetView;
    private String lastTime = "";
    private ArrayList<MyFocusShowers> myFocusShowerses = new ArrayList<>();
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
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    queryData(Constants.LOADMORE);
                                    recyclerView.loadMoreComplete();
                                }
                            }, 1000);
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
        queryData(Constants.REFRESH);
    }

    private void queryData(final int actionType) {
        final BmobQuery<MyFocusShowers> query = new BmobQuery<>();
        query.setLimit(10);
        if (actionType == Constants.LOADMORE) {
            // 只查询小于最后一个item发表时间的数据
            Date date = null;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                date = sdf.parse(lastTime);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            query.addWhereLessThanOrEqualTo("updatedAt", new BmobDate(date));
        }
        query.order("-updatedAt");
        query.findObjects(new FindListener<MyFocusShowers>() {
            @Override
            public void done(List<MyFocusShowers> list, BmobException e) {
                if (e == null) {
                    if (list.size() > 0) {
                        if (actionType == Constants.REFRESH) {
                            // 当是下拉刷新操作时，将当前页的编号重置为0，并清空，重新添加
                            myFocusShowerses.clear();
                        }
                        lastTime = list.get(list.size() - 1).getUpdatedAt();
                        Message message = new Message();
                        message.obj = list;
                        message.what = actionType;
                        handler.sendMessage(message);
                    } else {
                        handler.sendEmptyMessage(Constants.NO_MORE);
                    }
                }
            }
        });
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            ArrayList<MyFocusShowers> showerses = (ArrayList<MyFocusShowers>) msg.obj;
            if (msg.what != Constants.NetWorkError) {

                if (showerses != null) {
                    myFocusShowerses.addAll(showerses);
                    myfocusSize = showerses.size();
                } else {
                    myfocusSize = 0;
                }

                Display display = MyFocusShowerActivity.this.getWindowManager().getDefaultDisplay();
                Point size = new Point();
                display.getSize(size);
                int screenWidth = size.x;

                if (myFocusShowerListAdapter == null) {
                    myFocusShowerListAdapter = new MyFocusShowerListAdapter(MyFocusShowerActivity.this, myFocusShowerses, screenWidth, "");
                    recyclerView.setAdapter(myFocusShowerListAdapter);
                    myFocusShowerListAdapter.setOnItemClickListener(new MyFocusShowerListAdapter.OnRecyclerViewItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position, final MyFocusShowers myFocusShowers) {
                            String url = CommonUtil.getToggle(MyFocusShowerActivity.this, Constants.SHOWER_INFO).getToggleObject().replace("USERID", myFocusShowers.userId);
                            OkHttpUtils.get().url(url).build().execute(new StringCallback() {
                                @Override
                                public void onError(Call call, Exception e, int id) {
                                    handler.sendEmptyMessage(Constants.NetWorkError);
                                }

                                @Override
                                public void onResponse(String response, int id) {
                                    try {
                                        JSONObject jsonObject = new JSONObject(response);
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
                            });


                        }
                    });
                    if (myFocusShowerListAdapter != null) {
                        myFocusShowerListAdapter.setDelteListener(new MyFocusShowerListAdapter.OnDeleteListener() {
                            @Override
                            public void onDelete(final int position) {
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
                            }
                        });
                    }
                } else {
                    myFocusShowerListAdapter.notifyDataSetChanged();
                }
                loadNetView.setVisibility(View.GONE);

            } else {
                loadNetView.setVisibility(View.VISIBLE);
                loadNetView.setlayoutVisily(Constants.NO_DATA);
            }
        }
    };

}
