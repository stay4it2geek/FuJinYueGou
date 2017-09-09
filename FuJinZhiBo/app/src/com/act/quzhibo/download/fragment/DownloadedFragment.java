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
import com.act.quzhibo.adapter.DownLoadedListAdapter;
import com.act.quzhibo.common.Constants;
import com.act.quzhibo.common.fragment.BaseFragment;
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


public class DownloadedFragment extends BaseFragment {
    private DownLoadedListAdapter downloadAdapter;
    private DownloadManager downloadManager;
    LoadNetView loadNetView;
    XRecyclerView recyclerView;
    List<DownloadInfo> downInfos = new ArrayList<>();
    private int pagesize = 8;
    int pagecount = 0;
    int pageHasIndex = 0;
    private int loadIndex = 1;

    public static DownloadedFragment newInstance() {
        Bundle args = new Bundle();
        DownloadedFragment fragment = new DownloadedFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected View getLayoutView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
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
        initdownInfosize();

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

    private void initdownInfosize() {
        downInfos.clear();
        downInfos.addAll(downloadManager.findAllDownloaded());
        if (downInfos.size() > pagesize) {
            recyclerView.setNoMore(false);
            recyclerView.setLoadingMoreEnabled(true);
        }
        pageHasIndex = downInfos.size() % pagesize;
        if (pageHasIndex > 0) {
            pagecount = downInfos.size() / pagesize + 1;
        } else {
            pagecount = downInfos.size() / pagesize;
        }
        if (downInfos.size() > 0) {
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
                if (downInfos.size() > pagesize) {
                    recyclerView.setNoMore(false);
                    recyclerView.setLoadingMoreEnabled(true);
                }
                if (downInfos.size() > 0) {
                    if (downInfos.size() > pagesize) {
                        if (msg.what == Constants.REFRESH) {
                            List<DownloadInfo> subList = downloadManager.findAllDownloaded().subList((loadIndex - 1) * pagesize, pagesize * (loadIndex));
                            downInfos.clear();
                            downInfos.addAll(subList);
                            downloadAdapter = new DownLoadedListAdapter(getActivity() );
                            recyclerView.setAdapter(downloadAdapter);
                            setUiData(downInfos);
                        } else if (msg.what == Constants.LOADMORE) {
                            if (loadIndex == pagecount) {
                                List<DownloadInfo> subList = downloadManager.findAllDownloaded().subList((loadIndex - 1) * pagesize, downInfos.size());
                                downInfos.addAll(subList);
                            } else {
                                List<DownloadInfo> subList = downloadManager.findAllDownloaded().subList((loadIndex - 1) * pagesize, pagesize * (loadIndex));
                                downInfos.addAll(subList);
                            }
                            notifyChangeUiData(downInfos);
                        } else {
                            recyclerView.setNoMore(true);
                        }
                    } else {
                        downloadAdapter = new DownLoadedListAdapter(getActivity());
                        recyclerView.setAdapter(downloadAdapter);
                        setUiData(downInfos);
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
                                downInfos.remove(position);
                                downloadAdapter.setData(downInfos);
                                if (downInfos.size() == 0) {
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
        initdownInfosize();
    }

    private void setUiData(List<DownloadInfo> downLoadedList) {
        downloadAdapter.setData(downLoadedList);
    }


    private void notifyChangeUiData(List<DownloadInfo> downLoadedList) {
        downloadAdapter.appendData(downLoadedList);
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

}
