package ht.msc.mybatis.plugin;

public class HierarchicalModelRules extends BaseRules
{
  public HierarchicalModelRules(IntrospectedTable introspectedTable)
  {
    super(introspectedTable);
  }

  public boolean generatePrimaryKeyClass()
  {
    return this.introspectedTable.hasPrimaryKeyColumns();
  }

  public boolean generateBaseRecordClass()
  {
    return this.introspectedTable.hasBaseColumns();
  }

  public boolean generateRecordWithBLOBsClass()
  {
    return this.introspectedTable.hasBLOBColumns();
  }
}