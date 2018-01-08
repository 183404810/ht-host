package ht.msc.mybatis.plugin;

public class ConditionalModelRules extends BaseRules
{
  public ConditionalModelRules(IntrospectedTable introspectedTable)
  {
    super(introspectedTable);
  }

  public boolean generatePrimaryKeyClass()
  {
    return this.introspectedTable.getPrimaryKeyColumns().size() > 1;
  }

  public boolean generateBaseRecordClass()
  {
    return (this.introspectedTable.getBaseColumns().size() > 0) || 
      (this.introspectedTable.getPrimaryKeyColumns().size() == 1) || (
      (this.introspectedTable.getBLOBColumns().size() > 0) && (!generateRecordWithBLOBsClass()));
  }

  public boolean generateRecordWithBLOBsClass()
  {
    int otherColumnCount = this.introspectedTable.getPrimaryKeyColumns().size() + 
      this.introspectedTable.getBaseColumns().size();

    return (otherColumnCount > 1) && 
      (this.introspectedTable.getBLOBColumns().size() > 1);
  }
}