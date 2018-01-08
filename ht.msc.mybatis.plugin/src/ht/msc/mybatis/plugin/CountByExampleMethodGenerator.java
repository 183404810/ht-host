package ht.msc.mybatis.plugin;

import java.util.Set;
import java.util.TreeSet;

public class CountByExampleMethodGenerator extends AbstractDAOElementGenerator
{
  private boolean generateForJava5;

  public CountByExampleMethodGenerator(boolean generateForJava5)
  {
    this.generateForJava5 = generateForJava5;
  }

  public void addImplementationElements(TopLevelClass topLevelClass)
  {
    Set importedTypes = new TreeSet();
    Method method = getMethodShell(importedTypes);

    StringBuilder sb = new StringBuilder();

    sb.append("Integer count = (Integer)  ");
    sb.append(this.daoTemplate.getQueryForObjectMethod(this.introspectedTable
      .getIbatis2SqlMapNamespace(), this.introspectedTable
      .getCountByExampleStatementId(), "example"));
    method.addBodyLine(sb.toString());

    if (this.generateForJava5)
      method.addBodyLine("return count;");
    else {
      method.addBodyLine("return count.intValue();");
    }

    if (this.context.getPlugins().clientCountByExampleMethodGenerated(method, 
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

      if (this.context.getPlugins().clientCountByExampleMethodGenerated(
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
      .getCountByExampleMethodName(this.introspectedTable));
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