package com.act.quzhibo.bean;

import java.io.Serializable;
import cn.bmob.v3.BmobObject;

public class VipOrders extends BmobObject implements Serializable {
    public RootUser user;
    public boolean orderStatus;
    public String orderPrice;
    public String goodsDescription;
    public String orderId;
}
