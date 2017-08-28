package com.act.quzhibo.advanced_use.data_access;

import android.content.Context;

import com.act.quzhibo.advanced_use.db.CourseDbHelper;
import com.act.quzhibo.advanced_use.model.CoursePreviewInfo;
import com.act.quzhibo.common.Constants;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.List;

public class GetCourseDownloads {

    public void getCourseDownloads(Context context, OnGetCourseDownloadsListener onGetCourseDownloadsListener) {

        List<CoursePreviewInfo> coursePreviewInfos = null;

        try {
            Dao<CoursePreviewInfo, Integer> dao = CourseDbHelper.getInstance(context).getDao(CoursePreviewInfo.class);
            coursePreviewInfos =  dao.queryForAll();;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (coursePreviewInfos != null) {

                // init DownloadFiles
                for (CoursePreviewInfo coursePreviewInfo : coursePreviewInfos) {
                    if (coursePreviewInfo == null) {
                        continue;
                    }
                    coursePreviewInfo.init();
                }

                onGetCourseDownloadsListener.onGetCourseDownloadsSucceed(coursePreviewInfos);
            } else {
                onGetCourseDownloadsListener.onGetCourseDownloadsFailed();
            }
        }

    }

    public interface OnGetCourseDownloadsListener {

        void onGetCourseDownloadsSucceed(List<CoursePreviewInfo> coursePreviewInfos);

        void onGetCourseDownloadsFailed();
    }
}
