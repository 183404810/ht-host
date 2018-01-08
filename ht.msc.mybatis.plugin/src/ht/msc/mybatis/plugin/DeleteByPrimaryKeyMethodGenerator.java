package ht.msc.mybatis.plugin;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;


public class DeleteByPrimaryKeyMethodGenerator extends AbstractJavaMapperMethodGenerator
{
  private boolean isSimple;
  
  public DeleteByPrimaryKeyMethodGenerator(){}
  
  public DeleteByPrimaryKeyMethodGenerator(boolean isSimple)
  {
    this.isSimple = isSimple;
  }

  public void addInterfaceElements(Interface interfaze)
  {
    Set importedTypes = new TreeSet();
    Method method = new Method();
    method.setVisibility(JavaVisibility.PUBLIC);
    method.setReturnType(FullyQualifiedJavaType.getIntInstance());
    method.setName(this.introspectedTable.getDeleteByPrimaryKeyStatementId());

    if ((!this.isSimple) && (this.introspectedTable.getRules().generatePrimaryKeyClass())) {
      FullyQualifiedJavaType type = new FullyQualifiedJavaType(
        this.introspectedTable.getPrimaryKeyType());
      importedTypes.add(type);
      method.addParameter(new Parameter(type, "key"));
    }
    else
    {
      List<IntrospectedColumn> introspectedColumns = this.introspectedTable
        .getPrimaryKeyColumns();
      boolean annotate = introspectedColumns.size() > 1;
      if (annotate) {
        importedTypes.add(new FullyQualifiedJavaType(
          "org.apache.ibatis.annotations.Param"));
      }
      StringBuilder sb = new StringBuilder();
      for (IntrospectedColumn introspectedColumn : introspectedColumns) {
        FullyQualifiedJavaType type = introspectedColumn
          .getFullyQualifiedJavaType();
        importedTypes.add(type);
        Parameter parameter = new Parameter(type, introspectedColumn
          .getJavaProperty());
        if (annotate) {
          sb.setLength(0);
          sb.append("@Param(\"");
          sb.append(introspectedColumn.getJavaProperty());
          sb.append("\")");
          parameter.addAnnotation(sb.toString());
        }
        method.addParameter(parameter);
      }
    }

    this.context.getCommentGenerator().addGeneralMethodComment(method, 
      this.introspectedTable);

    addMapperAnnotations(interfaze, method);

    if (this.context.getPlugins().clientDeleteByPrimaryKeyMethodGenerated(
      method, interfaze, this.introspectedTable)) {
      interfaze.addImportedTypes(importedTypes);
      interfaze.addMethod(method);
    }
  }

  public void addMapperAnnotations(Interface interfaze, Method method)
  {
  }
}