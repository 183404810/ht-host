package ht.msc.mybatis.plugin.element;

import ht.msc.mybatis.plugin.IntrospectedColumn;
import ht.msc.mybatis.plugin.Method;
import ht.msc.mybatis.plugin.TopLevelClass;

import java.util.List;


public class ProviderSelectByExampleWithBLOBsMethodGenerator extends ProviderSelectByExampleWithoutBLOBsMethodGenerator
{
  public List<IntrospectedColumn> getColumns()
  {
    return this.introspectedTable.getAllColumns();
  }

  public String getMethodName()
  {
    return this.introspectedTable.getSelectByExampleWithBLOBsStatementId();
  }

  public boolean callPlugins(Method method, TopLevelClass topLevelClass)
  {
    return this.context.getPlugins().providerSelectByExampleWithBLOBsMethodGenerated(method, topLevelClass, 
      this.introspectedTable);
  }
}