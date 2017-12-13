package ht.msc.mybatis.plugin.element;

import ht.msc.mybatis.plugin.AbstractDAOElementGenerator;
import ht.msc.mybatis.plugin.FullyQualifiedJavaType;
import ht.msc.mybatis.plugin.Interface;
import ht.msc.mybatis.plugin.IntrospectedColumn;
import ht.msc.mybatis.plugin.JavaVisibility;
import ht.msc.mybatis.plugin.Method;
import ht.msc.mybatis.plugin.Parameter;
import ht.msc.mybatis.plugin.PrimitiveTypeWrapper;
import ht.msc.mybatis.plugin.TopLevelClass;

import java.util.Set;
import java.util.TreeSet;


public class InsertSelectiveMethodGenerator extends AbstractDAOElementGenerator
{
  public void addImplementationElements(TopLevelClass topLevelClass)
  {
    Set importedTypes = new TreeSet();
    Method method = getMethodShell(importedTypes);

    FullyQualifiedJavaType returnType = method.getReturnType();
    StringBuilder sb = new StringBuilder();

    if (returnType != null) {
      sb.append("Object newKey = ");
    }

    sb.append(this.daoTemplate.getInsertMethod(this.introspectedTable
      .getIbatis2SqlMapNamespace(), this.introspectedTable
      .getInsertSelectiveStatementId(), "record"));
    method.addBodyLine(sb.toString());

    if (returnType != null) {
      if ("Object".equals(returnType.getShortName()))
      {
        method.addBodyLine("return newKey;");
      } else {
        sb.setLength(0);

        if (returnType.isPrimitive()) {
          PrimitiveTypeWrapper ptw = returnType
            .getPrimitiveTypeWrapper();
          sb.append("return ((");
          sb.append(ptw.getShortName());
          sb.append(") newKey");
          sb.append(").");
          sb.append(ptw.getToPrimitiveMethod());
          sb.append(';');
        } else {
          sb.append("return (");
          sb.append(returnType.getShortName());
          sb.append(") newKey;");
        }

        method.addBodyLine(sb.toString());
      }
    }

    if (this.context.getPlugins().clientInsertSelectiveMethodGenerated(
      method, topLevelClass, this.introspectedTable)) {
      topLevelClass.addImportedTypes(importedTypes);
      topLevelClass.addMethod(method);
    }
  }

  public void addInterfaceElements(Interface interfaze)
  {
    Set importedTypes = new TreeSet();
    Method method = getMethodShell(importedTypes);

    if (this.context.getPlugins().clientInsertSelectiveMethodGenerated(
      method, interfaze, this.introspectedTable)) {
      interfaze.addImportedTypes(importedTypes);
      interfaze.addMethod(method);
    }
  }

  private Method getMethodShell(Set<FullyQualifiedJavaType> importedTypes) {
    Method method = new Method();
    FullyQualifiedJavaType returnType;
    if (this.introspectedTable.getGeneratedKey() != null) {
      IntrospectedColumn introspectedColumn = this.introspectedTable
        .getColumn(this.introspectedTable.getGeneratedKey().getColumn());
      if (introspectedColumn == null)
      {
        returnType = null;
      } else {
        returnType = introspectedColumn.getFullyQualifiedJavaType();
        importedTypes.add(returnType);
      }
    } else {
      returnType = null;
    }
    method.setReturnType(returnType);
    method.setVisibility(JavaVisibility.PUBLIC);
    method.setName(getDAOMethodNameCalculator()
      .getInsertSelectiveMethodName(this.introspectedTable));

    FullyQualifiedJavaType parameterType = this.introspectedTable.getRules()
      .calculateAllFieldsClass();

    importedTypes.add(parameterType);
    method.addParameter(new Parameter(parameterType, "record"));

    for (FullyQualifiedJavaType fqjt : this.daoTemplate.getCheckedExceptions()) {
      method.addException(fqjt);
      importedTypes.add(fqjt);
    }

    this.context.getCommentGenerator().addGeneralMethodComment(method, 
      this.introspectedTable);

    return method;
  }
}