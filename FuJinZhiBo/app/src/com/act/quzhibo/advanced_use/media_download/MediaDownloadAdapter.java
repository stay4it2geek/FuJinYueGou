package com.act.quzhibo.advanced_use.media_download;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.act.quzhibo.R;
import com.act.quzhibo.advanced_use.model.MediaModel;
import com.act.quzhibo.util.TimeUtil;
import com.act.quzhibo.util.ToastUtil;

import org.wlf.filedownloader.DownloadFileInfo;
import org.wlf.filedownloader.FileDownloader;
import org.wlf.filedownloader.base.Status;
import org.wlf.filedownloader.listener.OnRetryableFileDownloadStatusListener;
import org.wlf.filedownloader.util.FileUtil;
import org.wlf.filedownloader.util.MathUtil;

import java.util.ArrayList;
import java.util.List;


public class MediaDownloadAdapter extends RecyclerView.Adapter<MediaDownloadAdapter.mediaDownloadViewHolder> implements OnRetryableFileDownloadStatusListener {

    private List<MediaModel> mMediaPreviewInfos = new ArrayList<>();
    private List<MediaModel> mSelectMediaPreviewInfos = new ArrayList<>();

    private OnItemSelectListener mOnItemSelectListener;
    private Context mContext;

    public MediaDownloadAdapter(Context context, List<MediaModel> mediaPreviewInfos) {
        this.mContext = context;
        update(mediaPreviewInfos, true);
    }

    public void update(List<MediaModel> mediaPreviewInfos, boolean clearSelects) {
        if (mediaPreviewInfos == null) {
            return;
        }
        mMediaPreviewInfos = mediaPreviewInfos;

        if (clearSelects) {
            mSelectMediaPreviewInfos.clear();
            if (mOnItemSelectListener != null) {
                mOnItemSelectListener.onNoneSelect();
            }
        }

        notifyDataSetChanged();
    }

    public void setOnItemSelectListener(OnItemSelectListener onItemSelectListener) {
        mOnItemSelectListener = onItemSelectListener;
    }

    @Override
    public int getItemViewType(int position) {
        return position;// make viewType == position
    }

    @Override
    public mediaDownloadViewHolder onCreateViewHolder(ViewGroup parent, final int viewType) {

        Log.e("wlf", "onCreateViewHolder,viewType(==position):" + viewType);

        if (parent == null) {
            return null;
        }

        View itemView = View.inflate(parent.getContext(), R.layout.item_media_download, null);

        // set item layout param
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup
                .LayoutParams.WRAP_CONTENT);
        itemView.setLayoutParams(lp);

        mediaDownloadViewHolder holder = new mediaDownloadViewHolder(itemView);

        return holder;
    }

    @Override
    public void onBindViewHolder(final mediaDownloadViewHolder holder, final int position, List<Object> payloads) {

        Log.e("wlf", "position:" + position + ",payloads:" + payloads.size() + ",payloads.toString:" + payloads
                .toString());

        if (holder == null) {
            return;
        }

        if (position >= mMediaPreviewInfos.size()) {
            return;
        }

        Payload payload = null;
        for (int i = payloads.size() - 1; i >= 0; i--) {
            try {
                payload = (Payload) payloads.get(i);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (payload != null) {
                break;
            }
        }

        final MediaModel mediaPreviewInfo = mMediaPreviewInfos.get(position);
        if (mediaPreviewInfo == null) {
            return;
        }

        final Context context = holder.itemView.getContext();

        // media name
        holder.mTvFileName.setText(mediaPreviewInfo.getMediaName());

        DownloadFileInfo downloadFileInfo = mediaPreviewInfo.getDownloadFileInfo();

        Log.e("wlf", "onBindViewHolder,position:" + position + ",downloadFileInfo:" + downloadFileInfo);

        if (downloadFileInfo == null) {
            holder.mIvIcon.setImageDrawable(null);
            holder.mPbProgress.setMax(100);
            holder.mPbProgress.setProgress(0);
            holder.mTvDownloadSize.setText("00.00M/");
            holder.mTvTotalSize.setText("00.00M");
            holder.mTvPercent.setText("00.00%");
            holder.mTvText.setText(context.getString(R.string.media_download_not_start));
        } else {
            // media icon
            if ("mp4".equalsIgnoreCase(FileUtil.getFileSuffix(downloadFileInfo.getFileName()))) {//mp4
                holder.mIvIcon.setImageResource(R.drawable.ic_launcher);
            } else if ("flv".equalsIgnoreCase(FileUtil.getFileSuffix(downloadFileInfo.getFileName()))) {//flv
                holder.mIvIcon.setImageResource(R.drawable.ic_launcher);
            } else {//other
                //more
            }

            // download progress
            int totalSize = (int) downloadFileInfo.getFileSizeLong();
            int downloaded = (int) downloadFileInfo.getDownloadedSizeLong();
            double rate = (double) totalSize / Integer.MAX_VALUE;
            if (rate > 1.0) {
                totalSize = Integer.MAX_VALUE;
                downloaded = (int) (downloaded / rate);
            }

            holder.mPbProgress.setMax(totalSize);
            holder.mPbProgress.setProgress(downloaded);

            // downloaded size & file size
            double downloadSize = downloadFileInfo.getDownloadedSizeLong() / 1024f / 1024;
            double fileSize = downloadFileInfo.getFileSizeLong() / 1024f / 1024;
            holder.mTvDownloadSize.setText(MathUtil.formatNumber(downloadSize) + "M/");
            holder.mTvTotalSize.setText(MathUtil.formatNumber(fileSize) + "M");

            // downloaded percent
            double percent = downloadSize / fileSize * 100;
            holder.mTvPercent.setText(MathUtil.formatNumber(percent) + "%");

            final TextView tvText = holder.mTvText;
            // status
            switch (downloadFileInfo.getStatus()) {
                // download file status:unknown
                case Status.DOWNLOAD_STATUS_UNKNOWN:
                    tvText.setText(context.getString(R.string.can_not_download));
                    break;
                // download file status:waiting
                case Status.DOWNLOAD_STATUS_WAITING:
                    tvText.setText(context.getString(R.string.waiting));
                    break;
                // download file status:waiting
                case Status.DOWNLOAD_STATUS_RETRYING:
                    String retryTimesStr = "";
                    if (payload != null) {
                        retryTimesStr = "(" + payload.mRetryTimes + ")";
                    }
                    tvText.setText(context.getString(R.string.retrying_connect_resource) + retryTimesStr);
                    break;
                // download file status:preparing
                case Status.DOWNLOAD_STATUS_PREPARING:
                    tvText.setText(context.getString(R.string.getting_resource));
                    break;
                // download file status:prepared
                case Status.DOWNLOAD_STATUS_PREPARED:
                    tvText.setText(context.getString(R.string.connected_resource));
                    break;
                // download file status:downloading
                case Status.DOWNLOAD_STATUS_DOWNLOADING:
                    if (payload != null && payload.mDownloadSpeed > 0 && payload.mRemainingTime > 0) {
                        tvText.setText(MathUtil.formatNumber(payload.mDownloadSpeed) + "KB/s   " + TimeUtil
                                .seconds2HH_mm_ss(payload.mRemainingTime));
                    } else {
                        tvText.setText(context.getString(R.string.downloading));
                    }
                    break;
                // download file status:paused
                case Status.DOWNLOAD_STATUS_PAUSED:
                    tvText.setText(context.getString(R.string.paused));
                    break;
                // download file status:error
                case Status.DOWNLOAD_STATUS_ERROR:

                    String msg = context.getString(R.string.download_error);

                    if (payload != null && payload.mFailReason != null) {
                        FileDownloadStatusFailReason failReason = payload.mFailReason;
                        if (FileDownloadStatusFailReason.TYPE_NETWORK_DENIED.equals(failReason.getType())) {
                            msg += context.getString(R.string.check_network);
                        } else if (FileDownloadStatusFailReason.TYPE_URL_ILLEGAL.equals(failReason.getType())) {
                            msg += context.getString(R.string.url_illegal);
                        } else if (FileDownloadStatusFailReason.TYPE_NETWORK_TIMEOUT.equals(failReason.getType())) {
                            msg += context.getString(R.string.network_timeout);
                        }
                    }

                    tvText.setText(msg);

                    tvText.setText(context.getString(R.string.download_error));
                    break;
                // download file status:completed
                case Status.DOWNLOAD_STATUS_COMPLETED:
                    holder.mTvDownloadSize.setText("");
                    //mp4
                    if ("mp4".equalsIgnoreCase(FileUtil.getFileSuffix(downloadFileInfo.getFileName()))) {
                        tvText.setText(context.getString(R.string.media_download_play_mp4));
                    }
                    //flv
                    else if ("flv".equalsIgnoreCase(FileUtil.getFileSuffix(downloadFileInfo.getFileName()))) {
                        tvText.setText(context.getString(R.string.media_download_play_flv));
                    }
                    //other
                    else {
                        //more
                        tvText.setText(context.getString(R.string.download_completed));
                    }
                    break;
                // download file status:file not exist
                case Status.DOWNLOAD_STATUS_FILE_NOT_EXIST:
                    holder.mTvDownloadSize.setText("");
                    tvText.setText(context.getString(R.string.file_not_exist));
                    break;
            }
        }

        // check box
        holder.mCbSelect.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mSelectMediaPreviewInfos.add(mediaPreviewInfo);

                    Log.e("wlf", "isChecked=true mSelectMediaPreviewInfos.size:" + mSelectMediaPreviewInfos.size()
                            + ",position:" + position);

                    if (mOnItemSelectListener != null) {
                        // select a download file
                        mOnItemSelectListener.onSelected(mSelectMediaPreviewInfos);
                    }
                } else {
                    mSelectMediaPreviewInfos.remove(mediaPreviewInfo);

                    Log.e("wlf", "isChecked=false mSelectMediaPreviewInfos.size:" + mSelectMediaPreviewInfos.size()
                            + ",position:" + position);

                    if (mSelectMediaPreviewInfos.isEmpty()) {
                        if (mOnItemSelectListener != null) {
                            // select none
                            mOnItemSelectListener.onNoneSelect();
                        }
                    } else {
                        if (mOnItemSelectListener != null) {
                            // select a download file
                            mOnItemSelectListener.onSelected(mSelectMediaPreviewInfos);
                        }
                    }
                }
            }
        });

        // set check status
        boolean isChecked = false;
        for (MediaModel selectMediaPreviewInfo : mSelectMediaPreviewInfos) {
            if (selectMediaPreviewInfo == mediaPreviewInfo) {
                isChecked = true;
                break;
            }
        }
        holder.mCbSelect.setChecked(isChecked);

        // set background on click listener
        setBackgroundOnClickListener(holder, mediaPreviewInfo);

    }

    // set background on click listener
    private void setBackgroundOnClickListener(final mediaDownloadViewHolder holder, final MediaModel
            mediaPreviewInfo) {

        if (holder == null || holder.itemView == null) {
            return;
        }

        holder.itemView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                if (mediaPreviewInfo == null) {
                    return;
                }

                final DownloadFileInfo downloadFileInfo = mediaPreviewInfo.getDownloadFileInfo();

                final String mediaName = mediaPreviewInfo.getMediaName();// or downloadFileInfo.getFileName()
                final String url = mediaPreviewInfo.getMediaUrl();// or downloadFileInfo.getUrl()

                final Context context = v.getContext();
                if (downloadFileInfo != null) {
                    switch (downloadFileInfo.getStatus()) {
                        // download file status:unknown
                        case Status.DOWNLOAD_STATUS_UNKNOWN:

                            showToast(context, context.getString(R.string.can_not_download2) +
                                    downloadFileInfo.getFilePath() + context.getString(R.string
                                    .re_download));

                            break;
                        // download file status:error & paused
                        case Status.DOWNLOAD_STATUS_ERROR:
                        case Status.DOWNLOAD_STATUS_PAUSED:

                            // start
                            FileDownloader.start(url);

                            showToast(context, context.getString(R.string.start_download) + mediaName);
                            break;
                        // download file status:file not exist
                        case Status.DOWNLOAD_STATUS_FILE_NOT_EXIST:

                            // show dialog
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setTitle(context.getString(R.string.whether_re_download))
                                    .setNegativeButton(context.getString(R.string.dialog_btn_cancel), 
                                            null);
                            builder.setPositiveButton(context.getString(R.string.dialog_btn_confirm), 
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {

                                            // re-download
                                            FileDownloader.reStart(url);

                                            showToast(context, context.getString(R.string.re_download2)
                                                    + mediaName);
                                        }
                                    });
                            builder.show();
                            break;
                        // download file status:waiting & preparing & prepared & downloading
                        case Status.DOWNLOAD_STATUS_WAITING:
                        case Status.DOWNLOAD_STATUS_PREPARING:
                        case Status.DOWNLOAD_STATUS_PREPARED:
                        case Status.DOWNLOAD_STATUS_DOWNLOADING:

                            // pause
                            FileDownloader.pause(url);

                            showToast(context, context.getString(R.string.paused_download) + mediaName);

                            if (holder.mTvText != null) {
                                holder.mTvText.setText(context.getString(R.string.paused));
                            }
                            break;
                        // download file status:completed
                        case Status.DOWNLOAD_STATUS_COMPLETED:

                            if (holder.mTvDownloadSize != null) {
                                holder.mTvDownloadSize.setText("");
                            }

                            TextView tvText = holder.mTvText;
                            if (tvText != null) {
                                //mp4
                                if ("mp4".equalsIgnoreCase(FileUtil.getFileSuffix(downloadFileInfo.getFileName()))) {
                                    tvText.setText(context.getString(R.string.media_download_play_mp4));
                                }
                                //flv
                                else if ("flv".equalsIgnoreCase(FileUtil.getFileSuffix(downloadFileInfo.getFileName()
                                ))) {
                                    tvText.setText(context.getString(R.string.media_download_play_flv));
                                }
                                //other
                                else {
                                    //more
                                    tvText.setText(context.getString(R.string.download_completed));
                                }
                            }
                            break;
                    }
                } else {
                    // start
                    FileDownloader.start(url);
                }
            }
        });
    }

    @Override
    public void onBindViewHolder(mediaDownloadViewHolder holder, int position) {
    }

    // show toast
    private void showToast(Context context, String text) {
        ToastUtil.showToast(context, text);
    }

    @Override
    public int getItemCount() {
        return mMediaPreviewInfos.size();
    }

    @Override
    public void onFileDownloadStatusWaiting(DownloadFileInfo downloadFileInfo) {
        int position = findPosition(downloadFileInfo);
        if (position >= 0 && position < getItemCount()) {
            notifyItemChanged(position, new Payload(downloadFileInfo.getStatus(), downloadFileInfo.getUrl(), -1, -1, -1, null));
        }
    }

    @Override
    public void onFileDownloadStatusRetrying(DownloadFileInfo downloadFileInfo, int retryTimes) {
        int position = findPosition(downloadFileInfo);
        if (position >= 0 && position < getItemCount()) {
            notifyItemChanged(position, new Payload(downloadFileInfo.getStatus(), downloadFileInfo.getUrl(), -1, -1, 
                    retryTimes, null));
        }
    }

    @Override
    public void onFileDownloadStatusPreparing(DownloadFileInfo downloadFileInfo) {
        int position = findPosition(downloadFileInfo);
        if (position >= 0 && position < getItemCount()) {
            notifyItemChanged(position, new Payload(downloadFileInfo.getStatus(), downloadFileInfo.getUrl(), -1, -1, -1, null));
        }
    }

    @Override
    public void onFileDownloadStatusPrepared(DownloadFileInfo downloadFileInfo) {
        int position = findPosition(downloadFileInfo);
        if (position >= 0 && position < getItemCount()) {
            notifyItemChanged(position, new Payload(downloadFileInfo.getStatus(), downloadFileInfo.getUrl(), -1, -1, -1, null));
        }
    }

    @Override
    public void onFileDownloadStatusDownloading(DownloadFileInfo downloadFileInfo, float downloadSpeed, long
            remainingTime) {
        int position = findPosition(downloadFileInfo);
        if (position >= 0 && position < getItemCount()) {
            notifyItemChanged(position, new Payload(downloadFileInfo.getStatus(), downloadFileInfo.getUrl(), downloadSpeed, remainingTime, -1, null));
        }
    }

    @Override
    public void onFileDownloadStatusPaused(DownloadFileInfo downloadFileInfo) {
        int position = findPosition(downloadFileInfo);
        if (position >= 0 && position < getItemCount()) {
            notifyItemChanged(position, new Payload(downloadFileInfo.getStatus(), downloadFileInfo.getUrl(), -1, -1, -1, null));
        }
    }

    @Override
    public void onFileDownloadStatusCompleted(DownloadFileInfo downloadFileInfo) {
        int position = findPosition(downloadFileInfo);
        if (position >= 0 && position < getItemCount()) {
            notifyItemChanged(position, new Payload(downloadFileInfo.getStatus(), downloadFileInfo.getUrl(), -1, -1, -1, null));
        }
    }

    @Override
    public void onFileDownloadStatusFailed(String url, DownloadFileInfo downloadFileInfo, FileDownloadStatusFailReason failReason) {
        int position = findPosition(downloadFileInfo);
        if (position >= 0 && position < getItemCount()) {
            notifyItemChanged(position, new Payload(downloadFileInfo.getStatus(), downloadFileInfo.getUrl(), -1, -1, -1, failReason));
        }


        if (mContext != null) {
            String msg = mContext.getString(R.string.download_error);

            if (failReason != null) {
                if (FileDownloadStatusFailReason.TYPE_NETWORK_DENIED.equals(failReason.getType())) {
                    msg += mContext.getString(R.string.check_network);
                } else if (FileDownloadStatusFailReason.TYPE_URL_ILLEGAL.equals(failReason.getType())) {
                    msg += mContext.getString(R.string.url_illegal);
                } else if (FileDownloadStatusFailReason.TYPE_NETWORK_TIMEOUT.equals(failReason.getType())) {
                    msg += mContext.getString(R.string.network_timeout);
                } else if (FileDownloadStatusFailReason.TYPE_STORAGE_SPACE_IS_FULL.equals(failReason.getType())) {
                    msg += mContext.getString(R.string.storage_space_is_full);
                } else if (FileDownloadStatusFailReason.TYPE_STORAGE_SPACE_CAN_NOT_WRITE.equals(failReason.getType())) {
                    msg += mContext.getString(R.string.storage_space_can_not_write);
                } else if (FileDownloadStatusFailReason.TYPE_FILE_NOT_DETECT.equals(failReason.getType())) {
                    msg += mContext.getString(R.string.file_not_detect);
                } else if (FileDownloadStatusFailReason.TYPE_BAD_HTTP_RESPONSE_CODE.equals(failReason.getType())) {
                    msg += mContext.getString(R.string.http_bad_response_code);
                } else if (FileDownloadStatusFailReason.TYPE_HTTP_FILE_NOT_EXIST.equals(failReason.getType())) {
                    msg += mContext.getString(R.string.http_file_not_exist);
                } else if (FileDownloadStatusFailReason.TYPE_SAVE_FILE_NOT_EXIST.equals(failReason.getType())) {
                    msg += mContext.getString(R.string.save_file_not_exist);
                }
            }


            showToast(mContext, msg + "，url：" + url);
        }
    }

    public static final class mediaDownloadViewHolder extends RecyclerView.ViewHolder {

        private ImageView mIvIcon;
        private TextView mTvFileName;
        private ProgressBar mPbProgress;
        private TextView mTvDownloadSize;
        private TextView mTvTotalSize;
        private TextView mTvPercent;
        private TextView mTvText;
        private CheckBox mCbSelect;


        public mediaDownloadViewHolder(View itemView) {
            super(itemView);

            mIvIcon = (ImageView) itemView.findViewById(R.id.ivIcon);
            mTvFileName = (TextView) itemView.findViewById(R.id.tvFileName);
            mPbProgress = (ProgressBar) itemView.findViewById(R.id.pbProgress);
            mTvDownloadSize = (TextView) itemView.findViewById(R.id.tvDownloadSize);
            mTvTotalSize = (TextView) itemView.findViewById(R.id.tvTotalSize);
            mTvPercent = (TextView) itemView.findViewById(R.id.tvPercent);
            mTvText = (TextView) itemView.findViewById(R.id.tvText);
            mCbSelect = (CheckBox) itemView.findViewById(R.id.cbSelect);
        }
    }

    private int findPosition(DownloadFileInfo downloadFileInfo) {
        if (downloadFileInfo == null) {
            return -1;
        }
        for (int i = 0; i < mMediaPreviewInfos.size(); i++) {
            MediaModel mediaPreviewInfo = mMediaPreviewInfos.get(i);
            if (mediaPreviewInfo == null || TextUtils.isEmpty(mediaPreviewInfo.getMediaUrl())) {
                continue;
            }
            if (mediaPreviewInfo.getMediaUrl().equals(downloadFileInfo.getUrl())) {
                // find
                return i;
            }
        }
        return -1;
    }

    private static class Payload {

        private int mStatus = Status.DOWNLOAD_STATUS_UNKNOWN;

        private String mUrl;
        private float mDownloadSpeed;
        private long mRemainingTime;
        private int mRetryTimes;
        private FileDownloadStatusFailReason mFailReason;

        public Payload(int status, String url, float downloadSpeed, long remainingTime, int retryTimes, FileDownloadStatusFailReason failReason) {
            this.mStatus = status;
            this.mUrl = url;
            this.mDownloadSpeed = downloadSpeed;
            this.mRemainingTime = remainingTime;
            this.mRetryTimes = retryTimes;
            this.mFailReason = failReason;
        }

        @Override
        public String toString() {
            return "Payload{" +
                    "mStatus=" + mStatus +
                    ", mUrl='" + mUrl + '\'' +
                    ", mDownloadSpeed=" + mDownloadSpeed +
                    ", mRemainingTime=" + mRemainingTime +
                    ", mRetryTimes=" + mRetryTimes +
                    ", mFailReason=" + mFailReason +
                    '}';
        }
    }

    /**
     * OnItemSelectListener
     */
    public interface OnItemSelectListener {

        void onSelected(List<MediaModel> selectmediaPreviewInfos);

        void onNoneSelect();
    }

    public void release() {
        for (MediaModel mediaPreviewInfo : mMediaPreviewInfos) {
            if (mediaPreviewInfo == null) {
                continue;
            }
            mediaPreviewInfo.release();
        }
    }

}
