package com.act.quzhibo.entity;

import java.util.ArrayList;

/**
 * Created by asus-pc on 2017/6/11.
 */

public class InterestPostPageDetailAndComments {
    public InterestPostPageContentDetail detail;
    public long totalComments;
    public ArrayList<HotComments> hotComments;
    public ArrayList<InterestPostPageCommentDetail> comments;
    public InterestItemModel item;
    public RewardUsers rewardUsers;
}