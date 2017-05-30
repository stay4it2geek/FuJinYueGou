package com.act.quzhibo.entity;

import java.io.Serializable;

import cn.bmob.v3.BmobObject;

/**
 * Created by asus-pc on 2017/5/30.
 */

public class PlateCatagory implements Serializable{
    private String position;
    private String titleName;
    private String titleId;
    private String icon;
    private String cdnState;

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getTitleName() {
        return titleName;
    }

    public void setTitleName(String titleName) {
        this.titleName = titleName;
    }

    public String getTitleId() {
        return titleId;
    }

    public void setTitleId(String titleId) {
        this.titleId = titleId;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getCdnState() {
        return cdnState;
    }

    public void setCdnState(String cdnState) {
        this.cdnState = cdnState;
    }
}
