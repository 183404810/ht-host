package ht.msc.mybatis.plugin;

import java.util.Set;
import java.util.TreeSet;


public class InsertMethodGenerator extends AbstractJavaMapperMethodGenerator
{
  boolean isSimple;

  public InsertMethodGenerator(boolean isSimple)
  {
    this.isSimple = isSimple;
  }

  public void addInterfaceElements(Interface interfaze)
  {
    Set importedTypes = new TreeSet();
    Method method = new Method();

    method.setReturnType(FullyQualifiedJavaType.getIntInstance());
    method.setVisibility(JavaVisibility.PUBLIC);
    method.setName(this.introspectedTable.getInsertStatementId());
    FullyQualifiedJavaType parameterType;
    if (this.isSimple)
      parameterType = new FullyQualifiedJavaType(
        this.introspectedTable.getBaseRecordType());
    else {
      parameterType = this.introspectedTable.getRules()
        .calculateAllFieldsClass();
    }

    importedTypes.add(parameterType);
    method.addParameter(new Parameter(parameterType, "record"));

    this.context.getCommentGenerator().addGeneralMethodComment(method, 
      this.introspectedTable);

    addMapperAnnotations(interfaze, method);

    if (this.context.getPlugins().clientInsertMethodGenerated(method, interfaze, 
      this.introspectedTable)) {
      interfaze.addImportedTypes(importedTypes);
      interfaze.addMethod(method);
    }
  }

  public void addMapperAnnotations(Interface interfaze, Method method)
  {
  }
}