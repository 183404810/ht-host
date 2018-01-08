package ht.msc.mybatis.plugin;

import java.util.Set;
import java.util.TreeSet;


public class DeleteByExampleMethodGenerator extends AbstractDAOElementGenerator
{
  public void addImplementationElements(TopLevelClass topLevelClass)
  {
    Set importedTypes = new TreeSet();
    Method method = getMethodShell(importedTypes);

    StringBuilder sb = new StringBuilder();
    sb.append("int rows = ");
    sb.append(this.daoTemplate.getDeleteMethod(this.introspectedTable
      .getIbatis2SqlMapNamespace(), this.introspectedTable
      .getDeleteByExampleStatementId(), "example"));
    method.addBodyLine(sb.toString());
    method.addBodyLine("return rows;");

    if (this.context.getPlugins().clientDeleteByExampleMethodGenerated(
      method, topLevelClass, this.introspectedTable)) {
      topLevelClass.addImportedTypes(importedTypes);
      topLevelClass.addMethod(method);
    }
  }

  public void addInterfaceElements(Interface interfaze)
  {
    if (getExampleMethodVisibility() == JavaVisibility.PUBLIC) {
      Set importedTypes = new TreeSet();
      Method method = getMethodShell(importedTypes);

      if (this.context.getPlugins().clientDeleteByExampleMethodGenerated(
        method, interfaze, this.introspectedTable)) {
        interfaze.addImportedTypes(importedTypes);
        interfaze.addMethod(method);
      }
    }
  }

  private Method getMethodShell(Set<FullyQualifiedJavaType> importedTypes) {
    FullyQualifiedJavaType type = new FullyQualifiedJavaType(
      this.introspectedTable.getExampleType());
    importedTypes.add(type);

    Method method = new Method();
    method.setVisibility(getExampleMethodVisibility());
    method.setReturnType(FullyQualifiedJavaType.getIntInstance());
    method.setName(getDAOMethodNameCalculator()
      .getDeleteByExampleMethodName(this.introspectedTable));
    method.addParameter(new Parameter(type, "example"));

    for (FullyQualifiedJavaType fqjt : this.daoTemplate.getCheckedExceptions()) {
      method.addException(fqjt);
      importedTypes.add(fqjt);
    }

    this.context.getCommentGenerator().addGeneralMethodComment(method, 
      this.introspectedTable);

    return method;
  }
}