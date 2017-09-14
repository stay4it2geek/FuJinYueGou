package com.act.quzhibo.entity;

import java.io.Serializable;
import java.util.ArrayList;
public class InterestPostPageContentDetail implements Serializable {
    public String postId;
    public String title;
    public ArrayList<PostContentAndImageDesc> desc;
    public InterestPostPerson user;
    public long ctime;
    public boolean elite;// false,
    public boolean top;//false,
    public long pageView;// 331,
    public long rewards;//0,
    public long type;//0,
    public long heat;//0,
    public boolean hasbuy;//false
}
