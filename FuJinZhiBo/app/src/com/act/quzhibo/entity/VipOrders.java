package com.act.quzhibo.entity;

import java.io.Serializable;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobUser;


public class VipOrders extends BmobObject implements Serializable {
    public RootUser user;
    public boolean orderStatus;
    public String orderPrice;
    public String goodsDescription;
    public String orderId;
    public String orderType;
}
