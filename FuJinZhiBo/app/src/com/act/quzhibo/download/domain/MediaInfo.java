package com.act.quzhibo.download.domain;

import android.os.Parcel;
import android.os.Parcelable;



import cn.bmob.v3.BmobObject;


public class MediaInfo extends BmobObject implements  Parcelable {

  private String name;
  private String icon;
  private String url;
  private String type;

  public MediaInfo(String name, String icon, String url, String type) {
    this.name = name;
    this.icon = icon;
    this.url = url;
    this.type = type;
  }

  protected MediaInfo(Parcel in) {
    name = in.readString();
    icon = in.readString();
    url = in.readString();
    type = in.readString();
  }

  public static final Creator<MediaInfo> CREATOR = new Creator<MediaInfo>() {
    @Override
    public MediaInfo createFromParcel(Parcel source) {
      return new MediaInfo(source);
    }

    @Override
    public MediaInfo[] newArray(int size) {
      return new MediaInfo[size];
    }
  };

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

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(name);
    dest.writeString(icon);
    dest.writeString(url);
    dest.writeString(type);
  }


}
