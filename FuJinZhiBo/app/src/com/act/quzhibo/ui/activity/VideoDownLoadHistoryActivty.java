package com.act.quzhibo.ui.activity;

import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.act.quzhibo.R;
import com.act.quzhibo.adapter.DownLoadHistoryListAdapter;
import com.act.quzhibo.adapter.PostImageAdapter;
import com.act.quzhibo.common.Constants;
import com.act.quzhibo.view.LoadNetView;
import com.act.quzhibo.view.TitleBarView;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class VideoDownLoadHistoryActivty extends AppCompatActivity {

    LoadNetView loadNetView;
    XRecyclerView recyclerView;
    private boolean canload;
    int loadCount;
    DownLoadHistoryListAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_common);
        loadNetView = (LoadNetView) findViewById(R.id.loadview);
        loadNetView.setlayoutVisily(Constants.NO_DOWN_DATA);
        recyclerView = (XRecyclerView) findViewById(R.id.recycler_view);
        TitleBarView titlebar = (TitleBarView) findViewById(R.id.titlebar);
        titlebar.setBarTitle("我下载的视频");
        titlebar.setVisibility(View.VISIBLE);
        titlebar.setBackButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VideoDownLoadHistoryActivty.this.finish();
            }
        });

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
//                        queryData(Constants.REFRESH);
                        recyclerView.refreshComplete();
                    }
                }, 1000);
            }

            @Override
            public void onLoadMore() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
//                        queryData(Constants.LOADMORE);
                        recyclerView.loadMoreComplete();
                    }
                }, 1000);
            }
        });
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 1);
        gridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(gridLayoutManager);
        int loadnum;
        if (getImagePathFromSD().size() > 0) {
            int size = getImagePathFromSD().size();
            int canLoadNum = size / 10;
            if (canLoadNum == 0) {

                adapter = new DownLoadHistoryListAdapter(this, getImagePathFromSD());
                recyclerView.setAdapter(adapter);

                loadNetView.setVisibility(View.GONE);
            } else if (canLoadNum > 0) {
                canload = true;
                loadnum = (canLoadNum / 10) - (canLoadNum % 10);
//                adapter.setDatas();
            }

        }
    }


    /**
     * 从sd卡获取图片资源
     *
     * @return
     */
    private ArrayList<File> getImagePathFromSD() {
        // 图片列表
        ArrayList<File> videoFlieList = new ArrayList<File>();
        // 得到sd卡内image文件夹的路径   File.separator(/)
        String filePath = Environment.getExternalStorageDirectory().toString() + File.separator
                + "videoDownLoad";
        // 得到该路径文件夹下所有的文件
        File fileAll = new File(filePath);
        File[] files = fileAll.listFiles();
        // 将所有的文件存入ArrayList中,并过滤所有图片格式的文件
        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            if (checkIsVideoFile(file.getPath())) {
                videoFlieList.add(file);
            }
        }
        Collections.sort(videoFlieList, new FileComparator());
        // 返回得到的图片列表
        return videoFlieList;
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
