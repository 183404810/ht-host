package ht.msc.mybatis.plugin;

import java.io.Serializable;


public class TableColumModel
implements Serializable
{
private static final long serialVersionUID = 1L;
private String name;
private String type;
private String comment;
private String isNullAble;
private Integer maxLength;
private String columnKey;

public String getName()
{
  return this.name;
}
public void setName(String name) {
  this.name = name;
}
public String getType() {
  return this.type;
}
public void setType(String type) {
  this.type = type;
}
public String getComment() {
  return this.comment;
}
public void setComment(String comment) {
  this.comment = comment;
}
public String getIsNullAble() {
  return this.isNullAble;
}
public void setIsNullAble(String isNullAble) {
  this.isNullAble = isNullAble;
}
public Integer getMaxLength() {
  return this.maxLength;
}
public void setMaxLength(Integer maxLength) {
  this.maxLength = maxLength;
}
public String getColumnKey() {
  return this.columnKey;
}
public void setColumnKey(String columnKey) {
  this.columnKey = columnKey;
}
}