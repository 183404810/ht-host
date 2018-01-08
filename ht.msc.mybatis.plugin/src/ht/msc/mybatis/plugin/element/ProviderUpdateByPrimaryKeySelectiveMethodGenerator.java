package ht.msc.mybatis.plugin.element;

import ht.msc.mybatis.plugin.FullyQualifiedJavaType;
import ht.msc.mybatis.plugin.IntrospectedColumn;
import ht.msc.mybatis.plugin.JavaBeansUtil;
import ht.msc.mybatis.plugin.JavaVisibility;
import ht.msc.mybatis.plugin.Method;
import ht.msc.mybatis.plugin.MyBatis3FormattingUtilities;
import ht.msc.mybatis.plugin.Parameter;
import ht.msc.mybatis.plugin.StringUtility;
import ht.msc.mybatis.plugin.TopLevelClass;

import java.util.Set;
import java.util.TreeSet;


public class ProviderUpdateByPrimaryKeySelectiveMethodGenerator extends AbstractJavaProviderMethodGenerator
{
  public void addClassElements(TopLevelClass topLevelClass)
  {
    Set staticImports = new TreeSet();
    Set importedTypes = new TreeSet();

    staticImports.add("org.apache.ibatis.jdbc.SqlBuilder.BEGIN");
    staticImports.add("org.apache.ibatis.jdbc.SqlBuilder.UPDATE");
    staticImports.add("org.apache.ibatis.jdbc.SqlBuilder.SET");
    staticImports.add("org.apache.ibatis.jdbc.SqlBuilder.SQL");
    staticImports.add("org.apache.ibatis.jdbc.SqlBuilder.WHERE");

    FullyQualifiedJavaType fqjt = this.introspectedTable.getRules().calculateAllFieldsClass();
    importedTypes.add(fqjt);

    Method method = new Method(this.introspectedTable.getUpdateByPrimaryKeySelectiveStatementId());
    method.setReturnType(FullyQualifiedJavaType.getStringInstance());
    method.setVisibility(JavaVisibility.PUBLIC);
    method.addParameter(new Parameter(fqjt, "record"));

    this.context.getCommentGenerator().addGeneralMethodComment(method, 
      this.introspectedTable);

    method.addBodyLine("BEGIN();");

    method.addBodyLine(String.format("UPDATE(\"%s\");", new Object[] { 
      StringUtility.escapeStringForJava(this.introspectedTable.getFullyQualifiedTableNameAtRuntime()) }));
    method.addBodyLine("");

    for (IntrospectedColumn introspectedColumn : this.introspectedTable.getNonPrimaryKeyColumns()) {
      if (!introspectedColumn.getFullyQualifiedJavaType().isPrimitive()) {
        method.addBodyLine(String.format("if (record.%s() != null) {", new Object[] { 
          JavaBeansUtil.getGetterMethodName(introspectedColumn.getJavaProperty(), 
          introspectedColumn.getFullyQualifiedJavaType()) }));
      }

      method.addBodyLine(String.format("SET(\"%s = %s\");", new Object[] { 
        StringUtility.escapeStringForJava(MyBatis3FormattingUtilities.getEscapedColumnName(introspectedColumn)), 
        MyBatis3FormattingUtilities.getParameterClause(introspectedColumn) }));

      if (!introspectedColumn.getFullyQualifiedJavaType().isPrimitive()) {
        method.addBodyLine("}");
      }

      method.addBodyLine("");
    }

    for (IntrospectedColumn introspectedColumn : this.introspectedTable.getPrimaryKeyColumns()) {
      method.addBodyLine(String.format("WHERE(\"%s = %s\");", new Object[] { 
        StringUtility.escapeStringForJava(MyBatis3FormattingUtilities.getEscapedColumnName(introspectedColumn)), 
        MyBatis3FormattingUtilities.getParameterClause(introspectedColumn) }));
    }

    method.addBodyLine("");
    method.addBodyLine("return SQL();");

    if (this.context.getPlugins().providerUpdateByPrimaryKeySelectiveMethodGenerated(method, topLevelClass, 
      this.introspectedTable)) {
      topLevelClass.addStaticImports(staticImports);
      topLevelClass.addImportedTypes(importedTypes);
      topLevelClass.addMethod(method);
    }
  }
}