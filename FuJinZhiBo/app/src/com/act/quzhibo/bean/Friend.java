package com.act.quzhibo.bean;

import com.act.quzhibo.entity.RootUser;

import cn.bmob.v3.BmobObject;

/**好友表
 */
public class Friend extends BmobObject {

    private RootUser user;
    private RootUser friendUser;
    private String friendUserId;

    public void setFriendUserId(String friendUserId) {
        this.friendUserId = friendUserId;
    }

    private transient String pinyin;

    public String getPinyin() {
        return pinyin;
    }

    public void setPinyin(String pinyin) {
        this.pinyin = pinyin;
    }

    public RootUser getUser() {
        return user;
    }

    public void setUser(RootUser user) {
        this.user = user;
    }

    public RootUser getFriendUser() {
        return friendUser;
    }

    public void setFriendUser(RootUser friendUser) {
        this.friendUser = friendUser;
    }
}
