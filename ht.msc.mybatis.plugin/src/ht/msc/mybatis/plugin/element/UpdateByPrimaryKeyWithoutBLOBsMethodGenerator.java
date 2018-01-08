package ht.msc.mybatis.plugin.element;

import ht.msc.mybatis.plugin.AbstractDAOElementGenerator;
import ht.msc.mybatis.plugin.FullyQualifiedJavaType;
import ht.msc.mybatis.plugin.Interface;
import ht.msc.mybatis.plugin.JavaVisibility;
import ht.msc.mybatis.plugin.Method;
import ht.msc.mybatis.plugin.Parameter;
import ht.msc.mybatis.plugin.TopLevelClass;

import java.util.Set;
import java.util.TreeSet;


public class UpdateByPrimaryKeyWithoutBLOBsMethodGenerator extends AbstractDAOElementGenerator
{
  public void addImplementationElements(TopLevelClass topLevelClass)
  {
    Set importedTypes = new TreeSet();
    Method method = getMethodShell(importedTypes);

    StringBuilder sb = new StringBuilder();
    sb.append("int rows = ");
    sb.append(this.daoTemplate.getUpdateMethod(this.introspectedTable
      .getIbatis2SqlMapNamespace(), this.introspectedTable
      .getUpdateByPrimaryKeyStatementId(), "record"));
    method.addBodyLine(sb.toString());

    method.addBodyLine("return rows;");

    if (this.context.getPlugins()
      .clientUpdateByPrimaryKeyWithoutBLOBsMethodGenerated(method, 
      topLevelClass, this.introspectedTable)) {
      topLevelClass.addImportedTypes(importedTypes);
      topLevelClass.addMethod(method);
    }
  }

  public void addInterfaceElements(Interface interfaze)
  {
    Set importedTypes = new TreeSet();
    Method method = getMethodShell(importedTypes);

    if (this.context.getPlugins()
      .clientUpdateByPrimaryKeyWithoutBLOBsMethodGenerated(method, 
      interfaze, this.introspectedTable)) {
      interfaze.addImportedTypes(importedTypes);
      interfaze.addMethod(method);
    }
  }

  private Method getMethodShell(Set<FullyQualifiedJavaType> importedTypes) {
    FullyQualifiedJavaType parameterType = new FullyQualifiedJavaType(
      this.introspectedTable.getBaseRecordType());
    importedTypes.add(parameterType);

    Method method = new Method();
    method.setVisibility(JavaVisibility.PUBLIC);
    method.setReturnType(FullyQualifiedJavaType.getIntInstance());
    method
      .setName(getDAOMethodNameCalculator()
      .getUpdateByPrimaryKeyWithoutBLOBsMethodName(
      this.introspectedTable));
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