package ht.msc.mybatis.plugin;

import java.util.Set;
import java.util.TreeSet;


public class UpdateByExampleSelectiveMethodGenerator extends AbstractJavaMapperMethodGenerator
{
  public void addInterfaceElements(Interface interfaze)
  {
    Set importedTypes = new TreeSet();
    Method method = new Method();
    method.setVisibility(JavaVisibility.PUBLIC);
    method.setReturnType(FullyQualifiedJavaType.getIntInstance());
    method.setName(this.introspectedTable
      .getUpdateByExampleSelectiveStatementId());

    FullyQualifiedJavaType parameterType = 
      this.introspectedTable.getRules().calculateAllFieldsClass();
    method.addParameter(new Parameter(parameterType, 
      "record", "@Param(\"record\")"));
    importedTypes.add(parameterType);

    FullyQualifiedJavaType exampleType = new FullyQualifiedJavaType(
      this.introspectedTable.getExampleType());
    method.addParameter(new Parameter(exampleType, 
      "example", "@Param(\"example\")"));
    importedTypes.add(exampleType);

    importedTypes.add(new FullyQualifiedJavaType(
      "org.apache.ibatis.annotations.Param"));

    this.context.getCommentGenerator().addGeneralMethodComment(method, 
      this.introspectedTable);

    addMapperAnnotations(interfaze, method);

    if (this.context.getPlugins()
      .clientUpdateByExampleSelectiveMethodGenerated(method, interfaze, 
      this.introspectedTable)) {
      interfaze.addImportedTypes(importedTypes);
      interfaze.addMethod(method);
    }
  }

  public void addMapperAnnotations(Interface interfaze, Method method)
  {
  }
}