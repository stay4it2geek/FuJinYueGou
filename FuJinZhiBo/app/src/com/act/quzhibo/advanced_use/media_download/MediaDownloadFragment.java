package com.act.quzhibo.advanced_use.media_download;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.act.quzhibo.R;
import com.act.quzhibo.advanced_use.data_access.GetMediaDownloads;
import com.act.quzhibo.advanced_use.model.MediaInfo;
import com.act.quzhibo.ui.fragment.BackHandledFragment;
import com.act.quzhibo.util.ToastUtil;

import org.wlf.filedownloader.DownloadFileInfo;
import org.wlf.filedownloader.FileDownloader;
import org.wlf.filedownloader.base.Status;
import org.wlf.filedownloader.listener.OnDeleteDownloadFilesListener;
import org.wlf.filedownloader.listener.OnDownloadFileChangeListener;
import org.wlf.filedownloader.util.CollectionUtil;

import java.util.ArrayList;
import java.util.List;

public class MediaDownloadFragment extends BackHandledFragment implements MediaDownloadAdapter.OnItemSelectListener, OnDownloadFileChangeListener {

    private RecyclerView mRvmediaDownload;
    private MediaDownloadAdapter mMediaDownloadAdapter;

    private LinearLayout mLnlyOperation;
    private Button mBtnPause;
    private Button mBtnStartOrContinue;
    private Button mBtnDelete;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = getView();

        if (rootView == null) {

            rootView = inflater.inflate(R.layout.fragment_download, null);

            mRvmediaDownload = (RecyclerView) rootView.findViewById(R.id.rvMediaDownload);

            // create LinearLayoutManager
            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
            // vertical layout
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            // set layoutManager
            mRvmediaDownload.setLayoutManager(layoutManager);

            if (mMediaDownloadAdapter != null) {
                mMediaDownloadAdapter.release();
            }
            mMediaDownloadAdapter = new MediaDownloadAdapter(getActivity(), null);
            mRvmediaDownload.setAdapter(mMediaDownloadAdapter);

            mRvmediaDownload.setItemAnimator(null);
            mMediaDownloadAdapter.setOnItemSelectListener(this);

            mLnlyOperation = (LinearLayout) rootView.findViewById(R.id.lnlyOperation);
            mBtnPause = (Button) rootView.findViewById(R.id.btnPause);
            mBtnStartOrContinue = (Button) rootView.findViewById(R.id.btnStartOrContinue);
            mBtnDelete = (Button) rootView.findViewById(R.id.btnDelete);

            initmediaDownloadData(true);

            FileDownloader.registerDownloadStatusListener(mMediaDownloadAdapter);
            FileDownloader.registerDownloadFileChangeListener(this);
        }

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        initmediaDownloadData(true);
    }

    private void initmediaDownloadData(final boolean clearSelects) {
        GetMediaDownloads getmediaDownloads = new GetMediaDownloads();
        getmediaDownloads.getMediaDownloads(getActivity(), new GetMediaDownloads.OnGetmediaDownloadsListener() {
            @Override
            public void onGetmediaDownloadsSucceed(List<MediaInfo> mediaPreviewInfos) {
                mMediaDownloadAdapter.update(mediaPreviewInfos, clearSelects);
            }

            @Override
            public void onGetmediaDownloadsFailed() {
                ToastUtil.showToast(getActivity(), getActivity().getString(R.string.common__get_data_failed));
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        FileDownloader.unregisterDownloadStatusListener(mMediaDownloadAdapter);
        FileDownloader.unregisterDownloadFileChangeListener(this);
        if (mMediaDownloadAdapter != null) {
            mMediaDownloadAdapter.release();
        }
    }

    @Override
    public void onSelected(final List<MediaInfo> selectMediaPreviewInfos) {

        mLnlyOperation.setVisibility(View.VISIBLE);

        mBtnStartOrContinue.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectMediaPreviewInfos != null && !selectMediaPreviewInfos.isEmpty()) {
                    List<String> stoppedUrls = new ArrayList<String>();
                    for (MediaInfo info : selectMediaPreviewInfos) {
                        if (info == null || info.getDownloadFileInfo() == null) {
                            continue;
                        }
                        switch (info.getDownloadFileInfo().getStatus()) {
                            case Status.DOWNLOAD_STATUS_PAUSED:
                            case Status.DOWNLOAD_STATUS_ERROR:
                                stoppedUrls.add(info.getDownloadFileInfo().getUrl());
                                break;
                        }
                    }

                    if (!CollectionUtil.isEmpty(stoppedUrls)) {
                        // start/continue download
                        FileDownloader.start(stoppedUrls);
                    } else {
                        // do nothing
                    }
                }
            }
        });

        mBtnPause.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectMediaPreviewInfos != null && !selectMediaPreviewInfos.isEmpty()) {
                    List<String> downloadingUrls = new ArrayList<String>();
                    for (MediaInfo info : selectMediaPreviewInfos) {
                        if (info == null || info.getDownloadFileInfo() == null) {
                            continue;
                        }
                        switch (info.getDownloadFileInfo().getStatus()) {
                            case Status.DOWNLOAD_STATUS_WAITING:
                            case Status.DOWNLOAD_STATUS_DOWNLOADING:
                            case Status.DOWNLOAD_STATUS_PREPARING:
                            case Status.DOWNLOAD_STATUS_PREPARED:
                                downloadingUrls.add(info.getDownloadFileInfo().getUrl());
                                break;
                        }
                    }

                    if (!CollectionUtil.isEmpty(downloadingUrls)) {
                        FileDownloader.pause(downloadingUrls);
                    } else {
                        // do nothing
                    }
                }
            }
        });

        mBtnDelete.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!CollectionUtil.isEmpty(selectMediaPreviewInfos)) {
                    final List<String> needDeleteUrls = new ArrayList<String>();
                    for (MediaInfo info : selectMediaPreviewInfos) {
                        if (info == null) {
                            continue;
                        }
                        if (info.getDownloadFileInfo() == null) {
                            if (info.getMediaUrl() != null) {
                                needDeleteUrls.add(info.getMediaUrl());
                            }
                        } else {
                            needDeleteUrls.add(info.getDownloadFileInfo().getUrl());
                        }
                    }


                    if (!CollectionUtil.isEmpty(needDeleteUrls)) {

                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        String note = getString(R.string.media_center__media_cache_delete_confirm);
                        note = String.format(note, needDeleteUrls.size());
                        builder.setTitle(note);
                        builder.setNegativeButton("cancel", null);
                        builder.setPositiveButton("confirm", new
                                DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        FileDownloader.delete(needDeleteUrls, true, new OnDeleteDownloadFilesListener() {
                                            @Override
                                            public void onDeletingDownloadFiles(List<DownloadFileInfo>
                                                                                        downloadFilesNeedDelete,
                                                                                List<DownloadFileInfo> downloadFilesDeleted,
                                                                                List<DownloadFileInfo> downloadFilesSkip,
                                                                                DownloadFileInfo downloadFileDeleting) {
                                                Log.e("wlf", "批量删除中，downloadFilesNeedDelete：" + downloadFilesNeedDelete.size
                                                        () + ",downloadFilesDeleted:" + downloadFilesDeleted.size());
                                                if (downloadFileDeleting != null && downloadFilesSkip != null) {
                                                    showToast(getString(R.string.deleting) +
                                                            downloadFileDeleting.getFileName() +
                                                            getString(R.string.progress) +
                                                            (downloadFilesDeleted.size() + downloadFilesSkip.size()) +
                                                            getString(R.string.failed2) +
                                                            downloadFilesSkip.size() + getString(R.string
                                                            .skip_and_total_delete_division) +

                                                            downloadFilesNeedDelete.size());
                                                }
                                            }

                                            @Override
                                            public void onDeleteDownloadFilesPrepared(List<DownloadFileInfo>
                                                                                              downloadFilesNeedDelete) {
                                                Log.e("wlf", "开始批量删除，downloadFilesNeedDelete：" + downloadFilesNeedDelete.size
                                                        ());
                                                showToast(getString(R.string.need_delete) +
                                                        downloadFilesNeedDelete.size());
                                            }

                                            @Override
                                            public void onDeleteDownloadFilesCompleted(List<DownloadFileInfo>
                                                                                               downloadFilesNeedDelete,
                                                                                       List<DownloadFileInfo>
                                                                                               downloadFilesDeleted) {
                                                Log.e("wlf", "批量删除完成，downloadFilesNeedDelete：" + downloadFilesNeedDelete.size
                                                        () + ",downloadFilesDeleted:" + downloadFilesDeleted.size());
                                                showToast(getString(R.string.delete_finish) +
                                                        downloadFilesDeleted.size() +
                                                        getString(R.string.failed3) + (downloadFilesNeedDelete
                                                        .size() - downloadFilesDeleted.size()));
                                            }
                                        });
                                        //
                                        dialog.dismiss();
                                    }
                                });
                        builder.show();
                    } else {
                        showToast(getString(R.string.delete_failed));
                        // can not do it
                    }
                }
            }
        });
    }

    private void showToast(String msg) {
        ToastUtil.showToast(getActivity(), msg);
    }

    @Override
    public void onNoneSelect() {
        mLnlyOperation.setVisibility(View.GONE);
    }

    @Override
    public void onDownloadFileCreated(DownloadFileInfo downloadFileInfo) {
        Log.e("wlf", "onDownloadFileCreated---" + downloadFileInfo.getUrl() + ",thread:" + Thread.currentThread());
        initmediaDownloadData(false);
    }

    @Override
    public void onDownloadFileUpdated(DownloadFileInfo downloadFileInfo, Type type) {
    }

    @Override
    public void onDownloadFileDeleted(DownloadFileInfo downloadFileInfo) {
        initmediaDownloadData(true);
    }


    @Override
    public boolean onBackPressed() {
        return false;
    }
}
