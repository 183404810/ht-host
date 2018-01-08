package ht.msc.mybatis.plugin;

public enum CodeLayoutEnum
{
  DAL_LAYOUT("dal"), SERVICE_LAYOUT("service"), MANAGER_LAYOUT("manager"), DAO_LAYOUT(
    "dao"),  CONTROLLER_LAYOUT("controller"), JSMVC_LAYOUT("jsmvc");

  public String name;

  private CodeLayoutEnum(String name) {
    this.name = name;
  }
}