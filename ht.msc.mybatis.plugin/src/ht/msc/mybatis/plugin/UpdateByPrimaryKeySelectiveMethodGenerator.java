package ht.msc.mybatis.plugin;

import java.util.Set;
import java.util.TreeSet;


public class UpdateByPrimaryKeySelectiveMethodGenerator extends AbstractJavaMapperMethodGenerator
{
  public void addInterfaceElements(Interface interfaze)
  {
    Set importedTypes = new TreeSet();
    FullyQualifiedJavaType parameterType;
    if (this.introspectedTable.getRules().generateRecordWithBLOBsClass())
      parameterType = new FullyQualifiedJavaType(this.introspectedTable
        .getRecordWithBLOBsType());
    else {
      parameterType = new FullyQualifiedJavaType(this.introspectedTable
        .getBaseRecordType());
    }

    importedTypes.add(parameterType);

    Method method = new Method();
    method.setVisibility(JavaVisibility.PUBLIC);
    method.setReturnType(FullyQualifiedJavaType.getIntInstance());
    method.setName(this.introspectedTable
      .getUpdateByPrimaryKeySelectiveStatementId());
    method.addParameter(new Parameter(parameterType, "record"));

    this.context.getCommentGenerator().addGeneralMethodComment(method, 
      this.introspectedTable);

    addMapperAnnotations(interfaze, method);

    if (this.context.getPlugins()
      .clientUpdateByPrimaryKeySelectiveMethodGenerated(method, 
      interfaze, this.introspectedTable)) {
      interfaze.addImportedTypes(importedTypes);
      interfaze.addMethod(method);
    }
  }

  public void addMapperAnnotations(Interface interfaze, Method method)
  {
  }
}