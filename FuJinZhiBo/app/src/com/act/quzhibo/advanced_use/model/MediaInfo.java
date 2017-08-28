package com.act.quzhibo.advanced_use.model;

import android.text.TextUtils;
import android.util.Log;

import com.act.quzhibo.advanced_use.db.MediaDbHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.Dao.CreateOrUpdateStatus;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.j256.ormlite.table.DatabaseTable;

import org.wlf.filedownloader.DownloadFileInfo;
import org.wlf.filedownloader.FileDownloader;
import org.wlf.filedownloader.listener.OnDownloadFileChangeListener;

import java.sql.SQLException;

import cn.bmob.v3.BmobObject;

@DatabaseTable(tableName = "tb_media")
public class MediaInfo extends BmobObject implements OnDownloadFileChangeListener {

    public static final String COLUMN_NAME_OF_FIELD_MEDIA_URL = "media_url";

    @DatabaseField(generatedId = true, columnName = "_id")
    private Integer mId;//the id of the table

    @DatabaseField(columnName = "media_id", unique = true, canBeNull = false)
    private String mMediaId;//the id of the media

    @DatabaseField(columnName = COLUMN_NAME_OF_FIELD_MEDIA_URL, unique = true, canBeNull = false)
    private String mMediaUrl;//the url of the media

    @DatabaseField(columnName = "media_cover_url")
    private String mMediaCoverUrl;//the cover_url of the media

    @DatabaseField(columnName = "media_name")
    private String mMediaName;//the name of the media

    private DownloadFileInfo mDownloadFileInfo;//DownloadFileInfo
    private MediaDbHelper mMediaDbHelper;//the DbOpenHelper

    private MediaInfo() {
        init();
    }

    public MediaInfo(String mediaId, String mediaUrl, String mediaCoverUrl, String mediaName,
                     MediaDbHelper mediaDbHelper) {
        mMediaId = mediaId;
        mMediaUrl = mediaUrl;
        mMediaCoverUrl = mediaCoverUrl;
        mMediaName = mediaName;
        mMediaDbHelper = mediaDbHelper;

        init();
    }

    /**
     * init resources
     */
    public void init() {
        // register DownloadFileChangeListener
        FileDownloader.registerDownloadFileChangeListener(this);
        // init DownloadFileInfo if has been downloaded
        if (!TextUtils.isEmpty(mMediaUrl)) {
            mDownloadFileInfo = FileDownloader.getDownloadFile(mMediaUrl);
        }
    }

    /**
     * release resources
     */
    public void release() {
        // unregister
        FileDownloader.unregisterDownloadFileChangeListener(this);
    }

    // getters
    public Integer getId() {
        return mId;
    }

    public String getMediaId() {
        return mMediaId;
    }

    public String getMediaUrl() {
        return mMediaUrl;
    }

    public String getMediaCoverUrl() {
        return mMediaCoverUrl;
    }

    public String getMediaName() {
        return mMediaName;
    }

    public DownloadFileInfo getDownloadFileInfo() {
        return mDownloadFileInfo;
    }

    @Override
    public void onDownloadFileCreated(DownloadFileInfo downloadFileInfo) {

        if (downloadFileInfo != null && downloadFileInfo.getUrl() != null && downloadFileInfo.getUrl().equals
                (mMediaUrl)) {


            try {
                if (mMediaDbHelper == null) {
                    return;
                }
                Dao<MediaInfo, Integer> dao = mMediaDbHelper.getDao(MediaInfo.class);
                CreateOrUpdateStatus status = dao.createOrUpdate(this);
                if (status.isCreated() || status.isUpdated()) {
                    this.mDownloadFileInfo = downloadFileInfo;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void onDownloadFileUpdated(DownloadFileInfo downloadFileInfo, Type type) {

        if (downloadFileInfo != null && downloadFileInfo.getUrl() != null && downloadFileInfo.getUrl().equals
                (mMediaUrl)) {
            if (this.mDownloadFileInfo == null) {
                try {
                    if (mMediaDbHelper == null) {
                        return;
                    }
                    UpdateBuilder builder = mMediaDbHelper.getDao(MediaInfo.class).updateBuilder();
                    builder.where().eq(MediaInfo.COLUMN_NAME_OF_FIELD_MEDIA_URL, downloadFileInfo.getUrl());
                    int result = builder.update();
                    if (result == 1) {
                        this.mDownloadFileInfo = downloadFileInfo;
                    } else {
                        Dao<MediaInfo, Integer> dao = mMediaDbHelper.getDao(MediaInfo.class);
                        CreateOrUpdateStatus status = dao.createOrUpdate(this);
                        if (status.isCreated() || status.isUpdated()) {
                            this.mDownloadFileInfo = downloadFileInfo;
                        }
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    @Override
    public void onDownloadFileDeleted(DownloadFileInfo downloadFileInfo) {

        if (downloadFileInfo != null) {
            Log.e("wlf", "onDownloadFileDeleted,downloadFileInfo:" + downloadFileInfo.getUrl());
        } else {
            Log.e("wlf", "onDownloadFileDeleted,downloadFileInfo:" + downloadFileInfo);
        }

        if (downloadFileInfo != null && downloadFileInfo.getUrl() != null && downloadFileInfo.getUrl().equals
                (mMediaUrl)) {
            // delete this media preview in database download record
            try {
                if (mMediaDbHelper == null) {
                    return;
                }
                DeleteBuilder builder = mMediaDbHelper.getDao(MediaInfo.class).deleteBuilder();
                builder.where().eq(MediaInfo.COLUMN_NAME_OF_FIELD_MEDIA_URL, mMediaUrl);
                int result = builder.delete();
                if (result == 1) {
                    this.mDownloadFileInfo = null;
                    Log.w("wlf", "onDownloadFileDeleted,downloadFileInfo,delete succeed:" + downloadFileInfo.getUrl());
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }
    }
}
