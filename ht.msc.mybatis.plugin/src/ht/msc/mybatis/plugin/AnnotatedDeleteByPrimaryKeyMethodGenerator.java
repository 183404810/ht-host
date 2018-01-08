package ht.msc.mybatis.plugin;

import java.util.Iterator;


public class AnnotatedDeleteByPrimaryKeyMethodGenerator extends DeleteByPrimaryKeyMethodGenerator
{
  public AnnotatedDeleteByPrimaryKeyMethodGenerator(boolean isSimple)
  {
    super(isSimple);
  }

  public void addMapperAnnotations(Interface interfaze, Method method)
  {
    interfaze.addImportedType(new FullyQualifiedJavaType("org.apache.ibatis.annotations.Delete"));

    method.addAnnotation("@Delete({");

    StringBuilder sb = new StringBuilder();
    OutputUtilities.javaIndent(sb, 1);
    sb.append("\"delete from ");
    sb.append(StringUtility.escapeStringForJava(
      this.introspectedTable.getFullyQualifiedTableNameAtRuntime()));
    sb.append("\",");
    method.addAnnotation(sb.toString());

    boolean and = false;
    Iterator iter = this.introspectedTable.getPrimaryKeyColumns().iterator();
    while (iter.hasNext()) {
      IntrospectedColumn introspectedColumn = (IntrospectedColumn)iter.next();
      sb.setLength(0);
      OutputUtilities.javaIndent(sb, 1);
      if (and) {
        sb.append("  \"and ");
      } else {
        sb.append("\"where ");
        and = true;
      }

      sb.append(StringUtility.escapeStringForJava(
        MyBatis3FormattingUtilities.getEscapedColumnName(introspectedColumn)));
      sb.append(" = ");
      sb.append(MyBatis3FormattingUtilities.getParameterClause(introspectedColumn));
      sb.append('"');
      if (iter.hasNext()) {
        sb.append(',');
      }

      method.addAnnotation(sb.toString());
    }

    method.addAnnotation("})");
  }
}