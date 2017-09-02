package com.act.quzhibo.download.domain;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by renpingqing on 17/3/2.
 */
@DatabaseTable(tableName = "MediaInfoLocal")
public class MediaInfoLocal {

  @DatabaseField(id = true)
  private int id;

  @DatabaseField
  private String name;

  @DatabaseField
  private String icon;

  @DatabaseField
  private String url;

  @DatabaseField
  private String type;

  public MediaInfoLocal() {
  }

  public MediaInfoLocal(int id, String name, String icon, String url, String type) {
    this.id = id;
    this.name = name;
    this.icon = icon;
    this.url = url;
    this.type = type;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getIcon() {
    return icon;
  }

  public void setIcon(String icon) {
    this.icon = icon;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }
}
