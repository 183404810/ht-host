package ht.msc.mybatis.plugin.element;

import ht.msc.mybatis.plugin.AbstractDAOElementGenerator;
import ht.msc.mybatis.plugin.FullyQualifiedJavaType;
import ht.msc.mybatis.plugin.Interface;
import ht.msc.mybatis.plugin.JavaVisibility;
import ht.msc.mybatis.plugin.Messages;
import ht.msc.mybatis.plugin.Method;
import ht.msc.mybatis.plugin.Parameter;
import ht.msc.mybatis.plugin.TopLevelClass;

import java.util.Set;
import java.util.TreeSet;


public class SelectByExampleWithoutBLOBsMethodGenerator extends AbstractDAOElementGenerator
{
  private boolean generateForJava5;

  public SelectByExampleWithoutBLOBsMethodGenerator(boolean generateForJava5)
  {
    this.generateForJava5 = generateForJava5;
  }

  public void addImplementationElements(TopLevelClass topLevelClass)
  {
    Set importedTypes = new TreeSet();
    Method method = getMethodShell(importedTypes);

    if (this.generateForJava5) {
      method.addSuppressTypeWarningsAnnotation();
    }

    StringBuilder sb = new StringBuilder();
    sb.append(method.getReturnType().getShortName());
    sb.append(" list = ");
    sb.append(this.daoTemplate.getQueryForListMethod(this.introspectedTable
      .getIbatis2SqlMapNamespace(), this.introspectedTable
      .getSelectByExampleStatementId(), "example"));
    method.addBodyLine(sb.toString());
    method.addBodyLine("return list;");

    if (this.context.getPlugins()
      .clientSelectByExampleWithoutBLOBsMethodGenerated(method, 
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
        .clientSelectByExampleWithoutBLOBsMethodGenerated(method, 
        interfaze, this.introspectedTable)) {
        interfaze.addImportedTypes(importedTypes);
        interfaze.addMethod(method);
      }
    }
  }

  private Method getMethodShell(Set<FullyQualifiedJavaType> importedTypes) {
    FullyQualifiedJavaType type = new FullyQualifiedJavaType(
      this.introspectedTable.getExampleType());
    importedTypes.add(type);
    importedTypes.add(FullyQualifiedJavaType.getNewListInstance());

    Method method = new Method();
    method.setVisibility(getExampleMethodVisibility());

    FullyQualifiedJavaType returnType = 
      FullyQualifiedJavaType.getNewListInstance();

    if (this.generateForJava5)
    {
      FullyQualifiedJavaType fqjt;
      if (this.introspectedTable.getRules().generateBaseRecordClass()) {
        fqjt = new FullyQualifiedJavaType(this.introspectedTable
          .getBaseRecordType());
      }
      else
      {
        if (this.introspectedTable.getRules().generatePrimaryKeyClass())
          fqjt = new FullyQualifiedJavaType(this.introspectedTable
            .getPrimaryKeyType());
        else
          throw new RuntimeException(Messages.getString("RuntimeError.12"));
      }
      importedTypes.add(fqjt);
      returnType.addTypeArgument(fqjt);
    }

    method.setReturnType(returnType);

    method.setName(getDAOMethodNameCalculator()
      .getSelectByExampleWithoutBLOBsMethodName(this.introspectedTable));
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