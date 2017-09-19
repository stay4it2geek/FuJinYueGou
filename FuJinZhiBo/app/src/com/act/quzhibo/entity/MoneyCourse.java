package com.act.quzhibo.entity;

import java.io.Serializable;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;


public class MoneyCourse extends BmobObject implements Serializable {

    public String courseCategoryId;
    public String courseTag;
    public String courseName;
    public String courseAppPrice;
    public String courseMarketPrice;
    public String courseDetail;
    public BmobFile courseImage;
}
