package com.act.quzhibo.ui.activity;

import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;

import com.act.quzhibo.R;
import com.act.quzhibo.adapter.DownLoadHistoryListAdapter;
import com.act.quzhibo.common.Constants;
import com.act.quzhibo.util.ToastUtil;
import com.act.quzhibo.view.LoadNetView;
import com.act.quzhibo.view.TitleBarView;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cn.woblog.android.downloader.domain.DownloadInfo;

public class CommonDownLoadHistoryActivty extends AppCompatActivity {
    List<File> fileList = new ArrayList<>();
    LoadNetView loadNetView;
    XRecyclerView recyclerView;
    DownLoadHistoryListAdapter adapter;
    List<File> files=new ArrayList<>();
    private int pagesize = 8;
    int pagecount = 0;
    int pageHasIndex = 0;
    private int loadIndex = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_common);
        loadNetView = (LoadNetView) findViewById(R.id.loadview);
        recyclerView = (XRecyclerView) findViewById(R.id.recycler_view);
        TitleBarView titlebar = (TitleBarView) findViewById(R.id.titlebar);
        if (getIntent().getStringExtra("downLoadType").equals(Constants.VIDEO_DOWNLOAD)) {
            titlebar.setBarTitle("我保存好的视频");
        } else {
            titlebar.setBarTitle("我保存好的照片");
        }
        titlebar.setVisibility(View.VISIBLE);
        titlebar.setBackButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonDownLoadHistoryActivty.this.finish();
            }
        });
        files.addAll(getSourcePathFromSD());
        pageHasIndex = files.size() % pagesize;
        if (pageHasIndex > 0) {
            pagecount = files.size() / pagesize + 1;
        } else {
            pagecount = files.size() / pagesize;
        }
        if (files.size() > pagesize) {
            recyclerView.setNoMore(false);
            recyclerView.setLoadingMoreEnabled(true);
        }
        recyclerView.setPullRefreshEnabled(true);
        recyclerView.setLoadingMoreEnabled(true);
        recyclerView.setLoadingMoreProgressStyle(R.style.Small);
        recyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loadIndex = 1;
                        recyclerView.setNoMore(false);
                        recyclerView.setLoadingMoreEnabled(true);
                        handler.sendEmptyMessage(Constants.REFRESH);
                        recyclerView.refreshComplete();
                    }
                }, 1000);
            }

            @Override
            public void onLoadMore() {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ++loadIndex;
                        if (loadIndex > pagecount) {
                            handler.sendEmptyMessage(Constants.NO_MORE);
                        } else {
                            handler.sendEmptyMessage(Constants.LOADMORE);
                        }
                        recyclerView.loadMoreComplete();
                    }
                }, 1000);
            }
        });
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 1);
        gridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(gridLayoutManager);
        if (files.size() > 0) {
            handler.sendEmptyMessage(Constants.REFRESH);
        } else {
            loadNetView.setlayoutVisily(Constants.NO_DOWN_DATA);
        }
    }


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what != Constants.NO_DOWN_DATA) {
                if (files.size() > 0) {
                    if (files.size() > pagesize) {
                        recyclerView.setNoMore(false);
                        recyclerView.setLoadingMoreEnabled(true);
                    }
                    if (files.size() > pagesize) {
                        if (msg.what == Constants.REFRESH) {
                            List<File> subList = files.subList((loadIndex - 1) * pagesize, pagesize * (loadIndex));
                            fileList.clear();
                            fileList.addAll(subList);
                            adapter = new DownLoadHistoryListAdapter(CommonDownLoadHistoryActivty.this, fileList);
                            recyclerView.setAdapter(adapter);

                        } else if (msg.what == Constants.LOADMORE) {
                            if (loadIndex == pagecount) {
                                List<File> subList = files.subList((loadIndex - 1) * pagesize, files.size());
                                fileList.addAll(subList);
                            } else {
                                List<File> subList = files.subList((loadIndex - 1) * pagesize, pagesize * (loadIndex));
                                fileList.addAll(subList);
                            }
                            adapter.notifyDataSetChanged();
                        } else {
                            recyclerView.setNoMore(true);
                        }
                    } else {
                        adapter = new DownLoadHistoryListAdapter(CommonDownLoadHistoryActivty.this, files);
                        recyclerView.setAdapter(adapter);
                        recyclerView.setNoMore(true);

                    }
                    loadNetView.setVisibility(View.GONE);
                }
            }else{
                loadNetView.setlayoutVisily(Constants.NO_DOWN_DATA);
            }
        }


    };

    private List<File> getSourcePathFromSD() {
        List<File> sourceFlieList = new ArrayList<>();
        String filePath = Environment.getExternalStorageDirectory().toString() + File.separator
                + getIntent().getStringExtra("downLoadType");
        File fileAll = new File(filePath);
        File[] files = fileAll.listFiles();
        if (files != null && files.length > 0) {
            for (int i = 0; i < files.length; i++) {
                File file = files[i];
                Log.e("file",file.getAbsolutePath()+"pppp");
                {
                    if(file.getAbsolutePath().contains("@#@#@#@#")){
                        String space[]=file.getAbsolutePath().split("@#@#@#@#");
                        if (space[1].equals("")) {
                            sourceFlieList.add(file);
                        }
                    }else{
                        sourceFlieList.add(file);
                    }

                }
            }
            Collections.sort(sourceFlieList, new FileComparator());
        }
        return sourceFlieList;
    }

    private class FileComparator implements Comparator<File> {

        @Override
        public int compare(File lhs, File rhs) {
            if (lhs.lastModified() < rhs.lastModified()) {
                return 1;
            } else {
                return -1;
            }
        }

    }

}
