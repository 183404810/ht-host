package ht.msc.mybatis.plugin;


public abstract class AbstractJavaMapperMethodGenerator extends AbstractGenerator
{
  public abstract void addInterfaceElements(Interface paramInterface);

  protected String getResultAnnotation(Interface interfaze, IntrospectedColumn introspectedColumn, boolean idColumn, boolean constructorBased)
  {
    StringBuilder sb = new StringBuilder();
    if (constructorBased) {
      interfaze.addImportedType(introspectedColumn.getFullyQualifiedJavaType());
      sb.append("@Arg(column=\"");
      sb.append(MyBatis3FormattingUtilities.getRenamedColumnNameForResultMap(introspectedColumn));
      sb.append("\", javaType=");
      sb.append(introspectedColumn.getFullyQualifiedJavaType().getShortName());
      sb.append(".class");
    } else {
      sb.append("@Result(column=\"");
      sb.append(MyBatis3FormattingUtilities.getRenamedColumnNameForResultMap(introspectedColumn));
      sb.append("\", property=\"");
      sb.append(introspectedColumn.getJavaProperty());
      sb.append('"');
    }

    if (StringUtility.stringHasValue(introspectedColumn.getTypeHandler())) {
      FullyQualifiedJavaType fqjt = 
        new FullyQualifiedJavaType(introspectedColumn.getTypeHandler());
      interfaze.addImportedType(fqjt);
      sb.append(", typeHandler=");
      sb.append(fqjt.getShortName());
      sb.append(".class");
    }

    sb.append(", jdbcType=JdbcType.");
    sb.append(introspectedColumn.getJdbcTypeName());
    if (idColumn) {
      sb.append(", id=true");
    }
    sb.append(')');

    return sb.toString();
  }

  protected void addGeneratedKeyAnnotation(Interface interfaze, Method method, GeneratedKey gk)
  {
    StringBuilder sb = new StringBuilder();
    IntrospectedColumn introspectedColumn = this.introspectedTable.getColumn(gk.getColumn());
    if (introspectedColumn != null)
      if (gk.isJdbcStandard()) {
        interfaze.addImportedType(new FullyQualifiedJavaType("org.apache.ibatis.annotations.Options"));
        sb.append("@Options(useGeneratedKeys=true,keyProperty=\"");
        sb.append(introspectedColumn.getJavaProperty());
        sb.append("\")");
        method.addAnnotation(sb.toString());
      } else {
        interfaze.addImportedType(new FullyQualifiedJavaType("org.apache.ibatis.annotations.SelectKey"));
        FullyQualifiedJavaType fqjt = introspectedColumn.getFullyQualifiedJavaType();
        interfaze.addImportedType(fqjt);
        sb.append("@SelectKey(statement=\"");
        sb.append(gk.getRuntimeSqlStatement());
        sb.append("\", keyProperty=\"");
        sb.append(introspectedColumn.getJavaProperty());
        sb.append("\", before=");
        sb.append(gk.isIdentity() ? "false" : "true");
        sb.append(", resultType=");
        sb.append(fqjt.getShortName());
        sb.append(".class)");
        method.addAnnotation(sb.toString());
      }
  }
}