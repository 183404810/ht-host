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


public class ProviderInsertSelectiveMethodGenerator extends AbstractJavaProviderMethodGenerator
{
  public void addClassElements(TopLevelClass topLevelClass)
  {
    Set staticImports = new TreeSet();
    Set importedTypes = new TreeSet();

    staticImports.add("org.apache.ibatis.jdbc.SqlBuilder.BEGIN");
    staticImports.add("org.apache.ibatis.jdbc.SqlBuilder.INSERT_INTO");
    staticImports.add("org.apache.ibatis.jdbc.SqlBuilder.SQL");
    staticImports.add("org.apache.ibatis.jdbc.SqlBuilder.VALUES");

    FullyQualifiedJavaType fqjt = this.introspectedTable.getRules()
      .calculateAllFieldsClass();
    importedTypes.add(fqjt);

    Method method = new Method(
      this.introspectedTable.getInsertSelectiveStatementId());
    method.setVisibility(JavaVisibility.PUBLIC);
    method.setReturnType(FullyQualifiedJavaType.getStringInstance());
    method.addParameter(new Parameter(fqjt, "record"));

    this.context.getCommentGenerator().addGeneralMethodComment(method, 
      this.introspectedTable);

    method.addBodyLine("BEGIN();");
    method.addBodyLine(String.format("INSERT_INTO(\"%s\");", new Object[] { 
      StringUtility.escapeStringForJava(this.introspectedTable.getFullyQualifiedTableNameAtRuntime()) }));

    for (IntrospectedColumn introspectedColumn : this.introspectedTable.getAllColumns()) {
      if (!introspectedColumn.isIdentity())
      {
        method.addBodyLine("");
        if ((!introspectedColumn.getFullyQualifiedJavaType().isPrimitive()) && 
          (!introspectedColumn.isSequenceColumn())) {
          method.addBodyLine(String.format("if (record.%s() != null) {", new Object[] { 
            JavaBeansUtil.getGetterMethodName(introspectedColumn.getJavaProperty(), 
            introspectedColumn.getFullyQualifiedJavaType()) }));
        }
        method.addBodyLine(String.format("VALUES(\"%s\", \"%s\");", new Object[] { 
          StringUtility.escapeStringForJava(MyBatis3FormattingUtilities.getEscapedColumnName(introspectedColumn)), 
          MyBatis3FormattingUtilities.getParameterClause(introspectedColumn) }));
        if ((!introspectedColumn.getFullyQualifiedJavaType().isPrimitive()) && 
          (!introspectedColumn.isSequenceColumn())) {
          method.addBodyLine("}");
        }
      }
    }
    method.addBodyLine("");
    method.addBodyLine("return SQL();");

    if (this.context.getPlugins().providerInsertSelectiveMethodGenerated(method, topLevelClass, 
      this.introspectedTable)) {
      topLevelClass.addStaticImports(staticImports);
      topLevelClass.addImportedTypes(importedTypes);
      topLevelClass.addMethod(method);
    }
  }
}