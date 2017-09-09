package com.act.quzhibo.download.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.act.quzhibo.R;
import com.act.quzhibo.common.Constants;
import com.act.quzhibo.common.fragment.BaseFragment;
import com.act.quzhibo.download.adapter.DownloadAdapter;
import com.act.quzhibo.download.callback.OnDeleteListner;
import com.act.quzhibo.download.event.DownloadStatusChanged;
import com.act.quzhibo.util.ToastUtil;
import com.act.quzhibo.view.LoadNetView;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.woblog.android.downloader.DownloadService;
import cn.woblog.android.downloader.callback.DownloadManager;
import cn.woblog.android.downloader.domain.DownloadInfo;

public class DownloadingFragment extends BaseFragment {

    private DownloadAdapter downloadAdapter;
    private DownloadManager downloadManager;
    LoadNetView loadNetView;
    XRecyclerView recyclerView;
    List<DownloadInfo> files = new ArrayList<>();
    private int pagesize = 8;
    int pagecount = 0;
    int pageHasIndex = 0;
    private int loadIndex = 1;

    public static DownloadingFragment newInstance() {

        Bundle args = new Bundle();

        DownloadingFragment fragment = new DownloadingFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    protected View getLayoutView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_common, null);
    }

    @Override
    protected void initView() {
        super.initView();
        recyclerView = (XRecyclerView) getView().findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        loadNetView = (LoadNetView) getView().findViewById(R.id.loadview);
    }

    @Override
    protected void initData() {
        super.initData();
        EventBus.getDefault().register(this);
        downloadManager = DownloadService.getDownloadManager(getActivity().getApplicationContext());

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 1);
        gridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setPullRefreshEnabled(true);
        recyclerView.setLoadingMoreEnabled(true);
        recyclerView.setLoadingMoreProgressStyle(R.style.Small);
        initFilesize();

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

    }

    private void initFilesize() {
        files.clear();
        files.addAll(downloadManager.findAllDownloading());

        pageHasIndex = files.size() % pagesize;
        if (pageHasIndex > 0) {
            pagecount = files.size() / pagesize + 1;
        } else {
            pagecount = files.size() / pagesize;
        }

        if (files.size() > 0) {
            handler.sendEmptyMessage(Constants.REFRESH);
        } else {
            loadNetView.setlayoutVisily(Constants.NO_DOWN_DATA);
        }
        if (files.size() > pagesize) {
            recyclerView.setNoMore(false);
            recyclerView.setLoadingMoreEnabled(true);
        }
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what != Constants.NO_DOWN_DATA) {
                if (files.size() > pagesize) {
                    recyclerView.setNoMore(false);
                    recyclerView.setLoadingMoreEnabled(true);
                }
                if (files.size() > 0) {
                    if (files.size() > pagesize) {
                        if (msg.what == Constants.REFRESH) {
                            List<DownloadInfo> subList = downloadManager.findAllDownloading().subList((loadIndex - 1) * pagesize, pagesize * (loadIndex));
                            files.clear();
                            files.addAll(subList);
                            downloadAdapter = new DownloadAdapter(getActivity());
                            recyclerView.setAdapter(downloadAdapter);
                            setUiData(files);
                        } else if (msg.what == Constants.LOADMORE) {
                            if (loadIndex == pagecount) {
                                List<DownloadInfo> subList = downloadManager.findAllDownloading().subList((loadIndex - 1) * pagesize, files.size());
                                files.addAll(subList);
                            } else {
                                List<DownloadInfo> subList = downloadManager.findAllDownloading().subList((loadIndex - 1) * pagesize, pagesize * (loadIndex));
                                files.addAll(subList);
                            }
                            notifyChangeUiData(files);
                        } else {
                            recyclerView.setNoMore(true);
                        }
                    } else{
                        downloadAdapter = new DownloadAdapter(getActivity());
                        recyclerView.setAdapter(downloadAdapter);
                        setUiData(files);
                        if (loadIndex > pagecount) {
                            recyclerView.setNoMore(true);
                        }
                    }


                    downloadAdapter.setOnDeleteListner(new OnDeleteListner() {
                        @Override
                        public void onDelete(DownloadInfo downloadInfo, int position) {
                            File localFile = new File(downloadInfo.getPath());
                            if (localFile.isFile() && localFile.exists()) {
                                localFile.delete();
                                files.remove(position);
                                downloadAdapter.setData(files);
                                if (files.size() == 0) {
                                    loadNetView.setlayoutVisily(Constants.NO_DOWN_DATA);
                                }
                                ToastUtil.showToast(getActivity(), "删除成功" + downloadInfo == null ? "null" : "nonull");
                            }
                        }
                    });

                    loadNetView.setVisibility(View.GONE);
                }
            }
        }


    };


    @Subscribe
    public void onEventMainThread(DownloadStatusChanged event) {
        initFilesize();

    }

    private void setUiData(List<DownloadInfo> fileList) {
        downloadAdapter.setData(fileList);
    }


    private void notifyChangeUiData(List<DownloadInfo> fileList) {
        downloadAdapter.appendData(fileList);
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

}
