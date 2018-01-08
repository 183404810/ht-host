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


public class ProviderUpdateByExampleSelectiveMethodGenerator extends AbstractJavaProviderMethodGenerator
{
  public void addClassElements(TopLevelClass topLevelClass)
  {
    Set staticImports = new TreeSet();
    Set importedTypes = new TreeSet();

    staticImports.add("org.apache.ibatis.jdbc.SqlBuilder.BEGIN");
    staticImports.add("org.apache.ibatis.jdbc.SqlBuilder.UPDATE");
    staticImports.add("org.apache.ibatis.jdbc.SqlBuilder.SET");
    staticImports.add("org.apache.ibatis.jdbc.SqlBuilder.SQL");

    importedTypes.add(new FullyQualifiedJavaType("java.util.Map"));

    Method method = new Method(this.introspectedTable.getUpdateByExampleSelectiveStatementId());
    method.setReturnType(FullyQualifiedJavaType.getStringInstance());
    method.setVisibility(JavaVisibility.PUBLIC);
    method.addParameter(new Parameter(new FullyQualifiedJavaType("java.util.Map<java.lang.String, java.lang.Object>"), 
      "parameter"));

    FullyQualifiedJavaType record = 
      this.introspectedTable.getRules().calculateAllFieldsClass();
    importedTypes.add(record);
    method.addBodyLine(String.format("%s record = (%s) parameter.get(\"record\");", new Object[] { 
      record.getShortName(), record.getShortName() }));

    FullyQualifiedJavaType example = 
      new FullyQualifiedJavaType(this.introspectedTable.getExampleType());
    importedTypes.add(example);
    method.addBodyLine(String.format("%s example = (%s) parameter.get(\"example\");", new Object[] { 
      example.getShortName(), example.getShortName() }));

    this.context.getCommentGenerator().addGeneralMethodComment(method, 
      this.introspectedTable);

    method.addBodyLine("");
    method.addBodyLine("BEGIN();");

    method.addBodyLine(String.format("UPDATE(\"%s\");", new Object[] { 
      StringUtility.escapeStringForJava(this.introspectedTable.getAliasedFullyQualifiedTableNameAtRuntime()) }));
    method.addBodyLine("");

    for (IntrospectedColumn introspectedColumn : this.introspectedTable.getAllColumns()) {
      if (!introspectedColumn.getFullyQualifiedJavaType().isPrimitive()) {
        method.addBodyLine(String.format("if (record.%s() != null) {", new Object[] { 
          JavaBeansUtil.getGetterMethodName(introspectedColumn.getJavaProperty(), 
          introspectedColumn.getFullyQualifiedJavaType()) }));
      }

      StringBuilder sb = new StringBuilder();
      sb.append(MyBatis3FormattingUtilities.getParameterClause(introspectedColumn));
      sb.insert(2, "record.");

      method.addBodyLine(String.format("SET(\"%s = %s\");", new Object[] { 
        StringUtility.escapeStringForJava(MyBatis3FormattingUtilities.getAliasedEscapedColumnName(introspectedColumn)), 
        sb.toString() }));

      if (!introspectedColumn.getFullyQualifiedJavaType().isPrimitive()) {
        method.addBodyLine("}");
      }

      method.addBodyLine("");
    }

    method.addBodyLine("applyWhere(example, true);");
    method.addBodyLine("return SQL();");

    if (this.context.getPlugins().providerUpdateByExampleSelectiveMethodGenerated(method, topLevelClass, 
      this.introspectedTable)) {
      topLevelClass.addStaticImports(staticImports);
      topLevelClass.addImportedTypes(importedTypes);
      topLevelClass.addMethod(method);
    }
  }
}