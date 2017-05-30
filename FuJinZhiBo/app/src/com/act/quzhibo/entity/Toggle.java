package com.act.quzhibo.entity;

import cn.bmob.v3.BmobObject;

/**
 * Created by asus-pc on 2017/5/30.
 */
public class Toggle extends BmobObject{
    private String ToggleObject;
    private String objectKey;
    private String isOpen;
    private String explain;

    public String getToggleObject() {
        return ToggleObject;
    }

    public void setToggleObject(String toggleObject) {
        ToggleObject = toggleObject;
    }

    public String getObjectKey() {
        return objectKey;
    }

    public void setObjectKey(String objectKey) {
        this.objectKey = objectKey;
    }

    public String getIsOpen() {
        return isOpen;
    }

    public void setIsOpen(String isOpen) {
        this.isOpen = isOpen;
    }

    public String getExplain() {
        return explain;
    }

    public void setExplain(String explain) {
        this.explain = explain;
    }
}
