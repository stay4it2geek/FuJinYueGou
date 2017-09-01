package com.act.quzhibo.download.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.act.quzhibo.R;
import com.act.quzhibo.common.fragment.BaseFragment;
import com.act.quzhibo.download.adapter.DownloadAdapter;
import com.act.quzhibo.download.event.DownloadStatusChanged;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import cn.woblog.android.downloader.DownloadService;
import cn.woblog.android.downloader.callback.DownloadManager;


/**
 * Created by renpingqing on 17/3/1.
 */

public class DownloadingFragment extends BaseFragment {

  private RecyclerView rv;
  private DownloadAdapter downloadAdapter;
  private DownloadManager downloadManager;

  public static DownloadingFragment newInstance() {

    Bundle args = new Bundle();

    DownloadingFragment fragment = new DownloadingFragment();
    fragment.setArguments(args);
    return fragment;
  }

  @Override
  protected View getLayoutView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_download, null);
  }

  @Override
  protected void initView() {
    super.initView();
    rv = (RecyclerView) getView().findViewById(R.id.rv);
    rv.setLayoutManager(new LinearLayoutManager(getActivity()));


  }

  @Override
  protected void initData() {
    super.initData();
    EventBus.getDefault().register(this);
    downloadManager = DownloadService
        .getDownloadManager(getActivity().getApplicationContext());

    downloadAdapter = new DownloadAdapter(getActivity());
    rv.setAdapter(downloadAdapter);

    setData();
  }

  @Subscribe
  public void onEventMainThread(DownloadStatusChanged event) {
    setData();
  }

  private void setData() {
    downloadAdapter.setData(downloadManager.findAllDownloading());
  }

  @Override
  public void onDestroy() {
    EventBus.getDefault().unregister(this);
    super.onDestroy();
  }
}