package com.act.quzhibo.entity;

import java.io.Serializable;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;


public class MediaAuthor extends BmobObject implements Serializable{
    public String nickName;
    public String introduce;
    public BmobFile authorFile;
    public String age;
}
