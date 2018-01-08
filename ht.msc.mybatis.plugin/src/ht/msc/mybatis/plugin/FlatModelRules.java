package ht.msc.mybatis.plugin;

public class FlatModelRules extends BaseRules
{
  public FlatModelRules(IntrospectedTable introspectedTable)
  {
    super(introspectedTable);
  }

  public boolean generatePrimaryKeyClass()
  {
    return false;
  }

  public boolean generateBaseRecordClass()
  {
    return true;
  }

  public boolean generateRecordWithBLOBsClass()
  {
    return false;
  }
}