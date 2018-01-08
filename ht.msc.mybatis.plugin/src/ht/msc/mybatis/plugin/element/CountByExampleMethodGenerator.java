package ht.msc.mybatis.plugin.element;

import ht.msc.mybatis.plugin.AbstractJavaMapperMethodGenerator;
import ht.msc.mybatis.plugin.FullyQualifiedJavaType;
import ht.msc.mybatis.plugin.Interface;
import ht.msc.mybatis.plugin.JavaVisibility;
import ht.msc.mybatis.plugin.Method;
import ht.msc.mybatis.plugin.Parameter;

import java.util.Set;
import java.util.TreeSet;

public class CountByExampleMethodGenerator extends AbstractJavaMapperMethodGenerator
{
  public void addInterfaceElements(Interface interfaze)
  {
    FullyQualifiedJavaType fqjt = new FullyQualifiedJavaType(
      this.introspectedTable.getExampleType());

    Set importedTypes = new TreeSet();
    importedTypes.add(fqjt);

    Method method = new Method();
    method.setVisibility(JavaVisibility.PUBLIC);
    method.setReturnType(FullyQualifiedJavaType.getIntInstance());
    method.setName(this.introspectedTable.getCountByExampleStatementId());
    method.addParameter(new Parameter(fqjt, "example"));
    this.context.getCommentGenerator().addGeneralMethodComment(method, 
      this.introspectedTable);

    addMapperAnnotations(interfaze, method);

    if (this.context.getPlugins().clientCountByExampleMethodGenerated(method, 
      interfaze, this.introspectedTable)) {
      interfaze.addImportedTypes(importedTypes);
      interfaze.addMethod(method);
    }
  }

  public void addMapperAnnotations(Interface interfaze, Method method)
  {
  }
}