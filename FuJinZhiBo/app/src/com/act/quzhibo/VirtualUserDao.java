package com.act.quzhibo;

import android.content.Context;
import android.database.SQLException;
import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;

import java.util.List;

public class VirtualUserDao {

    Context context;
    Dao<VirtualDataUser, String> dao;

    private static VirtualUserDao instance;

    private VirtualUserDao(Context context) {
        super();
        this.context = context;
        VirtualBaseHelper mHelper = VirtualBaseHelper.getInstance(context);
        try {
            dao = mHelper.getDao(VirtualDataUser.class);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
    }


    public static VirtualUserDao getInstance(Context context) {
        if (instance == null) {
            synchronized (VirtualBaseHelper.class) {
                if (instance == null) {
                    instance = new VirtualUserDao(context.getApplicationContext());
                }
            }
        }
        return instance;
    }


    public int add(VirtualDataUser user) {
        try {
            return dao.create(user);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public VirtualDataUser query(String userId) {
        try {
            return dao.queryBuilder().where().eq("userId", userId).queryForFirst();
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int update(VirtualDataUser user) {
        try {
            return dao.update(user);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public void delete() {
        try {
            List<VirtualDataUser> users = dao.queryForAll();
            if (users.size() > 0) {
                DeleteBuilder<VirtualDataUser, String> deleteBuilder = dao.deleteBuilder();
                for (VirtualDataUser useAge : users) {
                    deleteBuilder.where().eq("userId", useAge.userId);
                    deleteBuilder.delete();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }

    }

    public void updateOnlineTime2Space() {
        try {
            List<VirtualDataUser> users = dao.queryForAll();
            if (users.size() > 0) {
                for (VirtualDataUser user : users) {
                    user.onlineTime = "";
                    dao.update(user);
                    Log.e("time",user.onlineTime+"pp");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }

    }

}