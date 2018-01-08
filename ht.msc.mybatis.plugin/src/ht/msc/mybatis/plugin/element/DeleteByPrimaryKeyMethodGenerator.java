package ht.msc.mybatis.plugin.element;

import ht.msc.mybatis.plugin.AbstractDAOElementGenerator;
import ht.msc.mybatis.plugin.FullyQualifiedJavaType;
import ht.msc.mybatis.plugin.Interface;
import ht.msc.mybatis.plugin.IntrospectedColumn;
import ht.msc.mybatis.plugin.JavaBeansUtil;
import ht.msc.mybatis.plugin.JavaVisibility;
import ht.msc.mybatis.plugin.Method;
import ht.msc.mybatis.plugin.Parameter;
import ht.msc.mybatis.plugin.TopLevelClass;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;


public class DeleteByPrimaryKeyMethodGenerator extends AbstractDAOElementGenerator
{
  public void addImplementationElements(TopLevelClass topLevelClass)
  {
    Set importedTypes = new TreeSet();
    Method method = getMethodShell(importedTypes);

    StringBuilder sb = new StringBuilder();

    if (!this.introspectedTable.getRules().generatePrimaryKeyClass())
    {
      FullyQualifiedJavaType keyType = new FullyQualifiedJavaType(
        this.introspectedTable.getBaseRecordType());
      topLevelClass.addImportedType(keyType);

      sb.setLength(0);
      sb.append(keyType.getShortName());
      sb.append(" _key = new ");
      sb.append(keyType.getShortName());
      sb.append("();");
      method.addBodyLine(sb.toString());

      Iterator localIterator = this.introspectedTable
        .getPrimaryKeyColumns().iterator();

      while (localIterator.hasNext()) {
        IntrospectedColumn introspectedColumn = (IntrospectedColumn)localIterator.next();
        sb.setLength(0);
        sb.append("_key.");
        sb.append(JavaBeansUtil.getSetterMethodName(introspectedColumn
          .getJavaProperty()));
        sb.append('(');
        sb.append(introspectedColumn.getJavaProperty());
        sb.append(");");
        method.addBodyLine(sb.toString());
      }
    }

    sb.setLength(0);
    sb.append("int rows = ");
    sb.append(this.daoTemplate.getDeleteMethod(this.introspectedTable
      .getIbatis2SqlMapNamespace(), this.introspectedTable
      .getDeleteByPrimaryKeyStatementId(), "_key"));
    method.addBodyLine(sb.toString());
    method.addBodyLine("return rows;");

    if (this.context.getPlugins().clientDeleteByPrimaryKeyMethodGenerated(
      method, topLevelClass, this.introspectedTable)) {
      topLevelClass.addImportedTypes(importedTypes);
      topLevelClass.addMethod(method);
    }
  }

  public void addInterfaceElements(Interface interfaze)
  {
    Set importedTypes = new TreeSet();
    Method method = getMethodShell(importedTypes);

    if (this.context.getPlugins().clientDeleteByPrimaryKeyMethodGenerated(
      method, interfaze, this.introspectedTable)) {
      interfaze.addImportedTypes(importedTypes);
      interfaze.addMethod(method);
    }
  }

  private Method getMethodShell(Set<FullyQualifiedJavaType> importedTypes) {
    Method method = new Method();
    method.setVisibility(JavaVisibility.PUBLIC);
    method.setReturnType(FullyQualifiedJavaType.getIntInstance());
    method.setName(getDAOMethodNameCalculator()
      .getDeleteByPrimaryKeyMethodName(this.introspectedTable));
    Iterator localIterator;
    if (this.introspectedTable.getRules().generatePrimaryKeyClass()) {
      FullyQualifiedJavaType type = new FullyQualifiedJavaType(
        this.introspectedTable.getPrimaryKeyType());
      importedTypes.add(type);
      method.addParameter(new Parameter(type, "_key"));
    }
    else {
      localIterator = this.introspectedTable
        .getPrimaryKeyColumns().iterator();

      while (localIterator.hasNext()) {
        IntrospectedColumn introspectedColumn = (IntrospectedColumn)localIterator.next();
        FullyQualifiedJavaType type = introspectedColumn
          .getFullyQualifiedJavaType();
        importedTypes.add(type);
        method.addParameter(new Parameter(type, introspectedColumn
          .getJavaProperty()));
      }
    }

    for (FullyQualifiedJavaType fqjt : this.daoTemplate.getCheckedExceptions()) {
      method.addException(fqjt);
      importedTypes.add(fqjt);
    }

    this.context.getCommentGenerator().addGeneralMethodComment(method, 
      this.introspectedTable);

    return method;
  }
}