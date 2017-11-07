package com.act.quzhibo;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;


@DatabaseTable(tableName = UserAge.TABLE_NAME)
public class UserAge {

    public static final String TABLE_NAME = "table_age";

    @DatabaseField(columnName = "userId")
    private String userId;

    @DatabaseField(columnName = "userAge")
    private String userAge;

    //空的构造方法一定要有，否则数据库会创建失败
    public UserAge() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserAge() {
        return userAge;
    }

    public void setUserAge(String userAge) {
        this.userAge = userAge;
    }
}
