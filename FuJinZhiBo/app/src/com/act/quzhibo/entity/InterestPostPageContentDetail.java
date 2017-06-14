package com.act.quzhibo.entity;

import java.util.ArrayList;

/**
 * Created by asus-pc on 2017/6/11.
 */

public class InterestPostPageContentDetail {
    public String postId;
    public String title;
    public ArrayList<PostContentAndImageDesc> desc;
    public InterstUser user;
    public long ctime;
    public boolean elite;// false,
    public boolean top;//false,
    public long pageView;// 331,
    public long rewards;//0,
    public long type;//0,
    public long heat;//0,
    public boolean hasbuy;//false
}
