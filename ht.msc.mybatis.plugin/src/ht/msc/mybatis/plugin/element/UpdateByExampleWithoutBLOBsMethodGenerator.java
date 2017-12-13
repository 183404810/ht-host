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


public class UpdateByExampleWithoutBLOBsMethodGenerator extends AbstractDAOElementGenerator
{
  public void addImplementationElements(TopLevelClass topLevelClass)
  {
    Set importedTypes = new TreeSet();
    Method method = getMethodShell(importedTypes);

    method
      .addBodyLine("UpdateByExampleParms parms = new UpdateByExampleParms(record, example);");

    StringBuilder sb = new StringBuilder();
    sb.append("int rows = ");
    sb.append(this.daoTemplate.getUpdateMethod(this.introspectedTable
      .getIbatis2SqlMapNamespace(), this.introspectedTable
      .getUpdateByExampleStatementId(), "parms"));
    method.addBodyLine(sb.toString());

    method.addBodyLine("return rows;");

    if (this.context.getPlugins()
      .clientUpdateByExampleWithoutBLOBsMethodGenerated(method, 
      topLevelClass, this.introspectedTable)) {
      topLevelClass.addImportedTypes(importedTypes);
      topLevelClass.addMethod(method);
    }
  }

  public void addInterfaceElements(Interface interfaze)
  {
    if (getExampleMethodVisibility() == JavaVisibility.PUBLIC) {
      Set importedTypes = new TreeSet();
      Method method = getMethodShell(importedTypes);

      if (this.context.getPlugins()
        .clientUpdateByExampleWithoutBLOBsMethodGenerated(method, 
        interfaze, this.introspectedTable)) {
        interfaze.addImportedTypes(importedTypes);
        interfaze.addMethod(method);
      }
    }
  }

  private Method getMethodShell(Set<FullyQualifiedJavaType> importedTypes)
  {
    FullyQualifiedJavaType parameterType;
    if (this.introspectedTable.getRules().generateBaseRecordClass())
      parameterType = new FullyQualifiedJavaType(this.introspectedTable
        .getBaseRecordType());
    else {
      parameterType = new FullyQualifiedJavaType(this.introspectedTable
        .getPrimaryKeyType());
    }

    importedTypes.add(parameterType);

    Method method = new Method();
    method.setVisibility(getExampleMethodVisibility());
    method.setReturnType(FullyQualifiedJavaType.getIntInstance());
    method.setName(getDAOMethodNameCalculator()
      .getUpdateByExampleWithoutBLOBsMethodName(this.introspectedTable));
    method.addParameter(new Parameter(parameterType, "record"));
    method.addParameter(new Parameter(new FullyQualifiedJavaType(
      this.introspectedTable.getExampleType()), "example"));

    for (FullyQualifiedJavaType fqjt : this.daoTemplate.getCheckedExceptions()) {
      method.addException(fqjt);
      importedTypes.add(fqjt);
    }

    this.context.getCommentGenerator().addGeneralMethodComment(method, 
      this.introspectedTable);

    return method;
  }
}