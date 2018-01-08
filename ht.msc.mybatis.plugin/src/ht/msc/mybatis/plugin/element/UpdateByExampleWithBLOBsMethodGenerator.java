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


public class UpdateByExampleWithBLOBsMethodGenerator extends AbstractDAOElementGenerator
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
      .getUpdateByExampleWithBLOBsStatementId(), "parms"));
    method.addBodyLine(sb.toString());

    method.addBodyLine("return rows;");

    if (this.context.getPlugins()
      .clientUpdateByExampleWithBLOBsMethodGenerated(method, 
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
        .clientUpdateByExampleWithBLOBsMethodGenerated(method, 
        interfaze, this.introspectedTable)) {
        interfaze.addImportedTypes(importedTypes);
        interfaze.addMethod(method);
      }
    }
  }

  private Method getMethodShell(Set<FullyQualifiedJavaType> importedTypes)
  {
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
    method.setVisibility(getExampleMethodVisibility());
    method.setReturnType(FullyQualifiedJavaType.getIntInstance());
    method.setName(getDAOMethodNameCalculator()
      .getUpdateByExampleWithBLOBsMethodName(this.introspectedTable));
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