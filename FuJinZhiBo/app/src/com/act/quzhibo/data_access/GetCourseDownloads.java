package com.act.quzhibo.data_access;

import android.content.Context;

import com.act.quzhibo.db.CourseDbHelper;
import com.act.quzhibo.entity.CoursePreviewInfo;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.List;

public class GetCourseDownloads {

    public void getCourseDownloads(Context context, OnGetCourseDownloadsListener onGetCourseDownloadsListener,String authorId,String type) {

        List<CoursePreviewInfo> coursePreviewInfos = null;

        try {
            Dao<CoursePreviewInfo, Integer> dao = CourseDbHelper.getInstance(context).getDao(CoursePreviewInfo.class);
            coursePreviewInfos = dao.queryBuilder().where().eq("author_id", authorId).and().eq("course_type", type).query();
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
