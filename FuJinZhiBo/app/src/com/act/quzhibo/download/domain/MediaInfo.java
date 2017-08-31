package com.act.quzhibo.download.domain;

import java.io.Serializable;

import cn.bmob.v3.BmobObject;


public class MediaInfo extends BmobObject implements Serializable {

  private String name;
  private String icon;
  private String url;

  public MediaInfo(String name, String icon, String url) {
    this.name = name;
    this.icon = icon;
    this.url = url;
  }

  public String getIcon() {
    return icon;
  }

  public void setIcon(String icon) {
    this.icon = icon;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }
}
