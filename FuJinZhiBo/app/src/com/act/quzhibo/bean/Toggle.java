package com.act.quzhibo.bean;

import cn.bmob.v3.BmobObject;

public class Toggle extends BmobObject {
    private String ToggleObject;
    private String objectKey;
    private String isOpen;
    private String explain;

    public String getToggleObject() {
        return ToggleObject;
    }
    public String getObjectKey() {
        return objectKey;
    }
    public String getIsOpen() {
        return isOpen;
    }

}
