package com.act.quzhibo.ui.activity;

import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.act.quzhibo.R;
import com.act.quzhibo.adapter.DownLoadHistoryListAdapter;
import com.act.quzhibo.common.Constants;
import com.act.quzhibo.view.LoadNetView;
import com.act.quzhibo.view.TitleBarView;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CommonDownLoadHistoryActivty extends AppCompatActivity {

    LoadNetView loadNetView;
    XRecyclerView recyclerView;
    DownLoadHistoryListAdapter adapter;
    int size;
    List<File> files;
    private int pagesize = 10;
    int pagecount = 0;
    int pageHasIndex = 0;
    int totalcount;
    private int loadIndex = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_common);
        loadNetView = (LoadNetView) findViewById(R.id.loadview);
        size = getSourcePathFromSD().size();
        recyclerView = (XRecyclerView) findViewById(R.id.recycler_view);
        TitleBarView titlebar = (TitleBarView) findViewById(R.id.titlebar);
        if (getIntent().getStringExtra("downLoadType").equals(Constants.VIDEO_DOWNLOAD)) {
            titlebar.setBarTitle("我下载的视频");
        } else {
            titlebar.setBarTitle("我下载的照片");
        }
        titlebar.setVisibility(View.VISIBLE);
        titlebar.setBackButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonDownLoadHistoryActivty.this.finish();
            }
        });
        files = getSourcePathFromSD();
        totalcount = files.size();
        pageHasIndex = totalcount % pagesize;

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
        if (totalcount > 0) {
            handler.sendEmptyMessage(Constants.REFRESH);
        } else {
            loadNetView.setlayoutVisily(Constants.NO_DOWN_DATA);
        }

        if (pageHasIndex > 0) {
            pagecount = totalcount / pagesize + 1;
        } else {
            pagecount = totalcount / pagesize;
        }
    }

    List<File> fileList = new ArrayList<>();

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if (msg.what != Constants.NO_DOWN_DATA) {
                adapter = new DownLoadHistoryListAdapter(CommonDownLoadHistoryActivty.this);
                recyclerView.setAdapter(adapter);
                if (totalcount <= 0) {
                    return;

                }
                if (msg.what == Constants.REFRESH) {
                    fileList.clear();
                    List<File> subList = files.subList((loadIndex - 1) * pagesize, pagesize * (loadIndex));
                    fileList.addAll(subList);
                    adapter.updateDatas(fileList);

                } else if (msg.what == Constants.LOADMORE) {
                    if (loadIndex == pagecount) {
                        List<File> subList = files.subList((loadIndex - 1) * pagesize, totalcount);
                        fileList.addAll(subList);
                    } else {
                        List<File> subList = files.subList((loadIndex - 1) * pagesize, pagesize * (loadIndex));
                        fileList.addAll(subList);
                    }
                    adapter.updateDatas(fileList);
                } else {
                    recyclerView.setNoMore(true);
                }

                loadNetView.setVisibility(View.GONE);

            }
        }


    };

    /**
     * 从sd卡获取资源
     *
     * @return
     */
    private List<File> getSourcePathFromSD() {
        // 图片列表
        List<File> sourceFlieList = new ArrayList<>();
        // 得到sd卡内image文件夹的路径   File.separator(/)
        String filePath = Environment.getExternalStorageDirectory().toString() + File.separator
                + getIntent().getStringExtra("downLoadType");
        // 得到该路径文件夹下所有的文件
        File fileAll = new File(filePath);
        File[] files = fileAll.listFiles();
        if (files != null && files.length > 0) {
            for (int i = 0; i < files.length; i++) {
                File file = files[i];
//            if (checkIsVideoFile(file.getPath())) {
                sourceFlieList.add(file);
//            }
            }
            Collections.sort(sourceFlieList, new FileComparator());
        }
        // 返回得到的图片列表
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

    /**
     * 检查扩展名，得到图片格式的文件
     *
     * @param fName 文件名
     * @return
     */
    private boolean checkIsVideoFile(String fName) {
        boolean isVideoFile = false;
        // 获取扩展名
        String FileEnd = fName.substring(fName.lastIndexOf(".") + 1,
                fName.length()).toLowerCase();
        if (FileEnd.equals("mp4") || FileEnd.equals("MP4") || FileEnd.equals("FLV")
                || FileEnd.equals("flv")) {
            isVideoFile = true;
        } else {
            isVideoFile = false;
        }
        return isVideoFile;
    }
}
