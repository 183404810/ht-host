package ht.msc.mybatis.plugin.element;

import ht.msc.mybatis.plugin.IntrospectedColumn;
import ht.msc.mybatis.plugin.Method;
import ht.msc.mybatis.plugin.TopLevelClass;

import java.util.List;


public class ProviderUpdateByExampleWithBLOBsMethodGenerator extends ProviderUpdateByExampleWithoutBLOBsMethodGenerator
{
  public String getMethodName()
  {
    return this.introspectedTable.getUpdateByExampleWithBLOBsStatementId();
  }

  public List<IntrospectedColumn> getColumns()
  {
    return this.introspectedTable.getAllColumns();
  }

  public boolean callPlugins(Method method, TopLevelClass topLevelClass)
  {
    return this.context.getPlugins().providerUpdateByExampleWithBLOBsMethodGenerated(method, topLevelClass, 
      this.introspectedTable);
  }
}