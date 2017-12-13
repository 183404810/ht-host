package ht.msc.mybatis.plugin;

import java.util.Iterator;


public class AnnotatedSelectByPrimaryKeyMethodGenerator extends SelectByPrimaryKeyMethodGenerator
{
  private boolean useResultMapIfAvailable;

  public AnnotatedSelectByPrimaryKeyMethodGenerator(boolean useResultMapIfAvailable, boolean isSimple)
  {
    super(isSimple);
    this.useResultMapIfAvailable = useResultMapIfAvailable;
  }

  public void addMapperAnnotations(Interface interfaze, Method method)
  {
    interfaze.addImportedType(new FullyQualifiedJavaType("org.apache.ibatis.annotations.Select"));

    StringBuilder sb = new StringBuilder();
    method.addAnnotation("@Select({");
    OutputUtilities.javaIndent(sb, 1);
    sb.append("\"select\",");
    method.addAnnotation(sb.toString());

    Iterator iter = this.introspectedTable
      .getAllColumns().iterator();
    sb.setLength(0);
    OutputUtilities.javaIndent(sb, 1);
    sb.append('"');
    boolean hasColumns = false;
    while (iter.hasNext()) {
      sb.append(StringUtility.escapeStringForJava(MyBatis3FormattingUtilities.getSelectListPhrase((IntrospectedColumn)iter.next())));
      hasColumns = true;

      if (iter.hasNext()) {
        sb.append(", ");
      }

      if (sb.length() > 80) {
        sb.append("\",");
        method.addAnnotation(sb.toString());

        sb.setLength(0);
        OutputUtilities.javaIndent(sb, 1);
        sb.append('"');
        hasColumns = false;
      }
    }

    if (hasColumns) {
      sb.append("\",");
      method.addAnnotation(sb.toString());
    }

    sb.setLength(0);
    OutputUtilities.javaIndent(sb, 1);
    sb.append("\"from ");
    sb.append(StringUtility.escapeStringForJava(this.introspectedTable
      .getAliasedFullyQualifiedTableNameAtRuntime()));
    sb.append("\",");
    method.addAnnotation(sb.toString());

    boolean and = false;
    iter = this.introspectedTable.getPrimaryKeyColumns().iterator();
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

      sb.append(StringUtility.escapeStringForJava(MyBatis3FormattingUtilities.getAliasedEscapedColumnName(introspectedColumn)));
      sb.append(" = ");
      sb.append(MyBatis3FormattingUtilities.getParameterClause(introspectedColumn));
      sb.append('"');
      if (iter.hasNext()) {
        sb.append(',');
      }
      method.addAnnotation(sb.toString());
    }

    method.addAnnotation("})");

    if (this.useResultMapIfAvailable) {
      if ((this.introspectedTable.getRules().generateBaseResultMap()) || 
        (this.introspectedTable.getRules().generateResultMapWithBLOBs()))
        addResultMapAnnotation(interfaze, method);
      else
        addAnnotatedResults(interfaze, method);
    }
    else
      addAnnotatedResults(interfaze, method);
  }

  private void addResultMapAnnotation(Interface interfaze, Method method)
  {
    interfaze.addImportedType(new FullyQualifiedJavaType("org.apache.ibatis.annotations.ResultMap"));

    String annotation = String.format("@ResultMap(\"%s\")", new Object[] { 
      this.introspectedTable.getRules().generateResultMapWithBLOBs() ? 
      this.introspectedTable.getResultMapWithBLOBsId() : this.introspectedTable.getBaseResultMapId() });
    method.addAnnotation(annotation);
  }

  private void addAnnotatedResults(Interface interfaze, Method method) {
    interfaze.addImportedType(new FullyQualifiedJavaType("org.apache.ibatis.type.JdbcType"));

    if (this.introspectedTable.isConstructorBased()) {
      interfaze.addImportedType(new FullyQualifiedJavaType("org.apache.ibatis.annotations.Arg"));
      interfaze.addImportedType(new FullyQualifiedJavaType("org.apache.ibatis.annotations.ConstructorArgs"));
      method.addAnnotation("@ConstructorArgs({");
    } else {
      interfaze.addImportedType(new FullyQualifiedJavaType("org.apache.ibatis.annotations.Result"));
      interfaze.addImportedType(new FullyQualifiedJavaType("org.apache.ibatis.annotations.Results"));
      method.addAnnotation("@Results({");
    }

    StringBuilder sb = new StringBuilder();

    Iterator iterPk = this.introspectedTable.getPrimaryKeyColumns().iterator();
    Iterator iterNonPk = this.introspectedTable.getNonPrimaryKeyColumns().iterator();
    while (iterPk.hasNext()) {
      IntrospectedColumn introspectedColumn = (IntrospectedColumn)iterPk.next();
      sb.setLength(0);
      OutputUtilities.javaIndent(sb, 1);
      sb.append(getResultAnnotation(interfaze, introspectedColumn, true, 
        this.introspectedTable.isConstructorBased()));

      if ((iterPk.hasNext()) || (iterNonPk.hasNext())) {
        sb.append(',');
      }

      method.addAnnotation(sb.toString());
    }

    while (iterNonPk.hasNext()) {
      IntrospectedColumn introspectedColumn = (IntrospectedColumn)iterNonPk.next();
      sb.setLength(0);
      OutputUtilities.javaIndent(sb, 1);
      sb.append(getResultAnnotation(interfaze, introspectedColumn, false, 
        this.introspectedTable.isConstructorBased()));

      if (iterNonPk.hasNext()) {
        sb.append(',');
      }

      method.addAnnotation(sb.toString());
    }

    method.addAnnotation("})");
  }
}