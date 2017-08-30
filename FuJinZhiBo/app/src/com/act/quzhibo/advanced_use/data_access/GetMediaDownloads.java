package com.act.quzhibo.advanced_use.data_access;

import android.content.Context;

import com.act.quzhibo.advanced_use.db.MediaDbHelper;
import com.act.quzhibo.advanced_use.model.MediaModel;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.List;

public class GetMediaDownloads {

    public void getMediaDownloads(Context context, OnGetmediaDownloadsListener onGetmediaDownloadsListener) {

        List<MediaModel> mediaPreviewInfos = null;

        try {
            Dao<MediaModel, Integer> dao = MediaDbHelper.getInstance(context).getDao(MediaModel.class);
            mediaPreviewInfos =  dao.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (mediaPreviewInfos != null) {


                for (MediaModel mediaPreviewInfo : mediaPreviewInfos) {
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

        void onGetmediaDownloadsSucceed(List<MediaModel> mediaPreviewInfos);

        void onGetmediaDownloadsFailed();
    }
}
