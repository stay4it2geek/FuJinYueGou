package com.act.quzhibo.bean;

import cn.bmob.v3.BmobObject;


public class ShoppingCart extends BmobObject {

    public RootUser user;

    public CommonCourse course;

    public boolean isChoosed;

    public Double price;

    public boolean isChoosed() {
        return isChoosed;
    }

    public void setChoosed(boolean choosed) {
        isChoosed = choosed;
    }
}
