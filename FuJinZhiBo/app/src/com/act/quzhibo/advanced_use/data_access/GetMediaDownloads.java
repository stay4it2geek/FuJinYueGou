package com.act.quzhibo.advanced_use.data_access;

import android.content.Context;

import com.act.quzhibo.advanced_use.db.MediaDbHelper;
import com.act.quzhibo.advanced_use.model.MediaInfo;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.List;

public class GetMediaDownloads {

    public void getMediaDownloads(Context context, OnGetmediaDownloadsListener onGetmediaDownloadsListener) {

        List<MediaInfo> mediaPreviewInfos = null;

        try {
            Dao<MediaInfo, Integer> dao = MediaDbHelper.getInstance(context).getDao(MediaInfo.class);
            mediaPreviewInfos =  dao.queryForAll();;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (mediaPreviewInfos != null) {

                // init DownloadFiles
                for (MediaInfo mediaPreviewInfo : mediaPreviewInfos) {
                    if (mediaPreviewInfo == null) {
                        continue;
                    }
                    mediaPreviewInfo.init();
                }

                onGetmediaDownloadsListener.onGetmediaDownloadsSucceed(mediaPreviewInfos);
            } else {
                onGetmediaDownloadsListener.onGetmediaDownloadsFailed();
            }
        }

    }

    public interface OnGetmediaDownloadsListener {

        void onGetmediaDownloadsSucceed(List<MediaInfo> mediaPreviewInfos);

        void onGetmediaDownloadsFailed();
    }
}
