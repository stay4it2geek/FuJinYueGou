package com.act.quzhibo;

import android.content.Context;
import android.database.SQLException;

import com.j256.ormlite.dao.Dao;

public class AgeDao {

    private Dao<UserAge, String> dao;

    public AgeDao(Context context) {
        try {
            dao = AgeBaseHelper.getInstance(context).getDao(UserAge.class);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
    }

    public int add(UserAge userAge) {
        try {
            return dao.create(userAge);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public UserAge query(String userId) {
        try {
            return dao.queryBuilder().where().eq("userId", userId).queryForFirst();
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int update(UserAge userAge) {
        try {
            return dao.update(userAge);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public int delete() {
        try {
            return dao.deleteBuilder().delete();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
}