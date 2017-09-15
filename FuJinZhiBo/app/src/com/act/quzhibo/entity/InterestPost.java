package com.act.quzhibo.entity;

import java.io.Serializable;
import java.util.ArrayList;

public class InterestPost implements Serializable{

    public String postId;
    public String title;
    public String absText;
    public ArrayList<String> images;
    public String totalImages;
    public InterestPostPerson user;
    public String elite;//false,
    public String top;//false,
    public String ctime;
    public String htime;
    public String sName;
    public String totalComments;
    public InterestItemModel itemModel;
    public String plateId;
    public String pageView;
    public String rewards;
    public String type;
    public String heat;
    public String hot;
    public String hasbuy;
    public String vedioUrl;

}
