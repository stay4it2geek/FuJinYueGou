package com.act.quzhibo.bean;

import com.act.quzhibo.entity.RootUser;

import cn.bmob.v3.BmobObject;

/**好友表
 * @author smile
 * @project Friend
 * @date 2016-04-26
 */
//TODO 好友管理：9.1、创建好友表
public class Friend extends BmobObject {

    private RootUser user;
    private RootUser friendUser;

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
