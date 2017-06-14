package com.act.quzhibo.entity;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by asus-pc on 2017/6/4.
 * 帖子列表数据
 */

public class InterstPostListInfoResult implements Serializable {
   public String totalPages;
   public String totalNums;
   public  ArrayList<InterestPost> posts;
}
