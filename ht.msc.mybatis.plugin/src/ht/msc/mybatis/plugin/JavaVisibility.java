package ht.msc.mybatis.plugin;

public enum JavaVisibility
{
  PUBLIC("public "), 
  PRIVATE("private "), 
  PROTECTED("protected "), 
  DEFAULT("");

  private String value;

  private JavaVisibility(String value) {
    this.value = value;
  }

  public String getValue() {
    return this.value;
  }
}