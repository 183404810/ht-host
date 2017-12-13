package ht.msc.mybatis.plugin.element;

import ht.msc.mybatis.plugin.FullyQualifiedJavaType;
import ht.msc.mybatis.plugin.IntrospectedColumn;
import ht.msc.mybatis.plugin.JavaVisibility;
import ht.msc.mybatis.plugin.Method;
import ht.msc.mybatis.plugin.MyBatis3FormattingUtilities;
import ht.msc.mybatis.plugin.Parameter;
import ht.msc.mybatis.plugin.StringUtility;
import ht.msc.mybatis.plugin.TopLevelClass;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;


public class ProviderUpdateByExampleWithoutBLOBsMethodGenerator extends AbstractJavaProviderMethodGenerator
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

    Method method = new Method(getMethodName());
    method.setReturnType(FullyQualifiedJavaType.getStringInstance());
    method.setVisibility(JavaVisibility.PUBLIC);
    method.addParameter(new Parameter(new FullyQualifiedJavaType("java.util.Map<java.lang.String, java.lang.Object>"), 
      "parameter"));

    this.context.getCommentGenerator().addGeneralMethodComment(method, 
      this.introspectedTable);

    method.addBodyLine("BEGIN();");

    method.addBodyLine(String.format("UPDATE(\"%s\");", new Object[] { 
      StringUtility.escapeStringForJava(this.introspectedTable.getAliasedFullyQualifiedTableNameAtRuntime()) }));
    method.addBodyLine("");

    for (IntrospectedColumn introspectedColumn : getColumns()) {
      StringBuilder sb = new StringBuilder();
      sb.append(MyBatis3FormattingUtilities.getParameterClause(introspectedColumn));
      sb.insert(2, "record.");

      method.addBodyLine(String.format("SET(\"%s = %s\");", new Object[] { 
        StringUtility.escapeStringForJava(MyBatis3FormattingUtilities.getAliasedEscapedColumnName(introspectedColumn)), 
        sb.toString() }));
    }

    method.addBodyLine("");

    FullyQualifiedJavaType example = 
      new FullyQualifiedJavaType(this.introspectedTable.getExampleType());
    importedTypes.add(example);
    method.addBodyLine(String.format("%s example = (%s) parameter.get(\"example\");", new Object[] { 
      example.getShortName(), example.getShortName() }));

    method.addBodyLine("applyWhere(example, true);");
    method.addBodyLine("return SQL();");

    if (callPlugins(method, topLevelClass)) {
      topLevelClass.addStaticImports(staticImports);
      topLevelClass.addImportedTypes(importedTypes);
      topLevelClass.addMethod(method);
    }
  }

  public String getMethodName() {
    return this.introspectedTable.getUpdateByExampleStatementId();
  }

  public List<IntrospectedColumn> getColumns() {
    return this.introspectedTable.getNonBLOBColumns();
  }

  public boolean callPlugins(Method method, TopLevelClass topLevelClass) {
    return this.context.getPlugins().providerUpdateByExampleWithoutBLOBsMethodGenerated(method, topLevelClass, 
      this.introspectedTable);
  }
}