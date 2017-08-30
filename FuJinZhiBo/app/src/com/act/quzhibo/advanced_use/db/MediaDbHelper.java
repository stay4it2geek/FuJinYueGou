package com.act.quzhibo.advanced_use.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;


import com.act.quzhibo.advanced_use.model.MediaModel;
import com.j256.ormlite.support.ConnectionSource;

import java.util.List;

public class MediaDbHelper extends BaseOrmLiteSQLiteHelper {

    private static final String DB_NAME = "media.db";
    private static final int DB_VERSION = 1;

    private static MediaDbHelper sInstance;

    public MediaDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }



    public static MediaDbHelper getInstance(Context context) {
        if (sInstance == null) {
            synchronized (MediaDbHelper.class) {
                if (sInstance == null || !sInstance.isOpen()) {
                    sInstance = new MediaDbHelper(context.getApplicationContext());
                }
            }
        }
        return sInstance;
    }

    @Override
    protected void onConfigTables(List<Class<?>> supportTables) {
        if (supportTables == null) {
            return;
        }
        // add table
        supportTables.add(MediaModel.class);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        // nothing to do 
    }

    @Override
    public void close() {
        super.close();
        sInstance = null;
    }
}
