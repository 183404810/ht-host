package ht.msc.mybatis.plugin;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class AnnotatedInsertMethodGenerator extends InsertMethodGenerator
{
  public AnnotatedInsertMethodGenerator(boolean isSimple)
  {
    super(isSimple);
  }

  public void addMapperAnnotations(Interface interfaze, Method method)
  {
    interfaze.addImportedType(new FullyQualifiedJavaType("org.apache.ibatis.annotations.Insert"));

    GeneratedKey gk = this.introspectedTable.getGeneratedKey();

    method.addAnnotation("@Insert({");
    StringBuilder insertClause = new StringBuilder();
    StringBuilder valuesClause = new StringBuilder();

    OutputUtilities.javaIndent(insertClause, 1);
    OutputUtilities.javaIndent(valuesClause, 1);

    insertClause.append("\"insert into ");
    insertClause.append(StringUtility.escapeStringForJava(this.introspectedTable
      .getFullyQualifiedTableNameAtRuntime()));
    insertClause.append(" (");

    valuesClause.append("\"values (");

    List<String> valuesClauses = new ArrayList();
    Iterator iter = this.introspectedTable.getAllColumns()
      .iterator();
    boolean hasFields = false;
    while (iter.hasNext()) {
      IntrospectedColumn introspectedColumn = (IntrospectedColumn)iter.next();
      if (!introspectedColumn.isIdentity())
      {
        insertClause.append(StringUtility.escapeStringForJava(MyBatis3FormattingUtilities.getEscapedColumnName(introspectedColumn)));
        valuesClause.append(MyBatis3FormattingUtilities.getParameterClause(introspectedColumn));
        hasFields = true;
        if (iter.hasNext()) {
          insertClause.append(", ");
          valuesClause.append(", ");
        }

        if (valuesClause.length() > 60) {
          if (!iter.hasNext()) {
            insertClause.append(')');
            valuesClause.append(')');
          }
          insertClause.append("\",");
          valuesClause.append('"');
          if (iter.hasNext()) {
            valuesClause.append(',');
          }

          method.addAnnotation(insertClause.toString());
          insertClause.setLength(0);
          OutputUtilities.javaIndent(insertClause, 1);
          insertClause.append('"');

          valuesClauses.add(valuesClause.toString());
          valuesClause.setLength(0);
          OutputUtilities.javaIndent(valuesClause, 1);
          valuesClause.append('"');
          hasFields = false;
        }
      }
    }
    if (hasFields) {
      insertClause.append(")\",");
      method.addAnnotation(insertClause.toString());

      valuesClause.append(")\"");
      valuesClauses.add(valuesClause.toString());
    }

    for (String clause : valuesClauses) {
      method.addAnnotation(clause);
    }

    method.addAnnotation("})");

    if (gk != null)
      addGeneratedKeyAnnotation(interfaze, method, gk);
  }
}