package com.act.quzhibo;


import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

public class AgeBaseHelper extends OrmLiteSqliteOpenHelper {
    private static final String DB_NAME = "age_data.db";
    private static final int DB_VERSION = 1;

    private static AgeBaseHelper instance;

    private AgeBaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }
    public static AgeBaseHelper getInstance(Context context){
        if(instance == null){
            synchronized (AgeBaseHelper.class){
                if(instance == null){
                    instance = new AgeBaseHelper(context.getApplicationContext());
                }
            }
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, UserAge.class);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource, int i, int i1) {
        try {
            TableUtils.dropTable(connectionSource,UserAge.class,true);
            onCreate(sqLiteDatabase,connectionSource);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
    }


}
