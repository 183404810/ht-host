package ht.msc.mybatis.plugin;

import java.util.Set;
import java.util.TreeSet;


public class SelectByExampleWithoutBLOBsMethodGenerator extends AbstractJavaMapperMethodGenerator
{
  public void addInterfaceElements(Interface interfaze)
  {
    Set importedTypes = new TreeSet();
    FullyQualifiedJavaType type = new FullyQualifiedJavaType(
      this.introspectedTable.getExampleType());
    importedTypes.add(type);
    importedTypes.add(FullyQualifiedJavaType.getNewListInstance());

    Method method = new Method();
    method.setVisibility(JavaVisibility.PUBLIC);

    FullyQualifiedJavaType returnType = 
      FullyQualifiedJavaType.getNewListInstance();
    FullyQualifiedJavaType listType;
    if (this.introspectedTable.getRules().generateBaseRecordClass()) {
      listType = new FullyQualifiedJavaType(this.introspectedTable
        .getBaseRecordType());
    }
    else
    {
      if (this.introspectedTable.getRules().generatePrimaryKeyClass())
        listType = new FullyQualifiedJavaType(this.introspectedTable
          .getPrimaryKeyType());
      else
        throw new RuntimeException(Messages.getString("RuntimeError.12"));
    }
    importedTypes.add(listType);
    returnType.addTypeArgument(listType);
    method.setReturnType(returnType);

    method.setName(this.introspectedTable.getSelectByExampleStatementId());
    method.addParameter(new Parameter(type, "example"));

    this.context.getCommentGenerator().addGeneralMethodComment(method, 
      this.introspectedTable);

    addMapperAnnotations(interfaze, method);

    if (this.context.getPlugins()
      .clientSelectByExampleWithoutBLOBsMethodGenerated(method, 
      interfaze, this.introspectedTable)) {
      interfaze.addImportedTypes(importedTypes);
      interfaze.addMethod(method);
    }
  }

  public void addMapperAnnotations(Interface interfaze, Method method)
  {
  }
}