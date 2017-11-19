package com.act.quzhibo.bean;

import java.io.Serializable;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

public class Orders extends CommonCourse implements Serializable {
    public RootUser user;
    public boolean orderStatus;
    public String orderId;
    public  CommonCourse course;

}
