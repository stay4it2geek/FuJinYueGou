package com.act.quzhibo.entity;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by weiminglin on 17/6/1.
 * 帖子列表父数据
 */

public class InterestPostListInfoParentData implements Serializable {
    public boolean ok;
    public boolean relogin;
    public boolean needRegister;
    public boolean applePayRetry;
     public ArrayList<InterestPost>  result;
}
