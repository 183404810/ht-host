package ht.msc.mybatis.plugin;

import java.util.Set;
import java.util.TreeSet;


public class UpdateByPrimaryKeyWithoutBLOBsMethodGenerator extends AbstractJavaMapperMethodGenerator
{
  public void addInterfaceElements(Interface interfaze)
  {
    Set importedTypes = new TreeSet();
    FullyQualifiedJavaType parameterType = new FullyQualifiedJavaType(
      this.introspectedTable.getBaseRecordType());
    importedTypes.add(parameterType);

    Method method = new Method();
    method.setVisibility(JavaVisibility.PUBLIC);
    method.setReturnType(FullyQualifiedJavaType.getIntInstance());
    method.setName(this.introspectedTable.getUpdateByPrimaryKeyStatementId());
    method.addParameter(new Parameter(parameterType, "record"));

    this.context.getCommentGenerator().addGeneralMethodComment(method, 
      this.introspectedTable);

    addMapperAnnotations(interfaze, method);

    if (this.context.getPlugins()
      .clientUpdateByPrimaryKeyWithoutBLOBsMethodGenerated(method, 
      interfaze, this.introspectedTable)) {
      interfaze.addImportedTypes(importedTypes);
      interfaze.addMethod(method);
    }
  }

  public void addMapperAnnotations(Interface interfaze, Method method)
  {
  }
}