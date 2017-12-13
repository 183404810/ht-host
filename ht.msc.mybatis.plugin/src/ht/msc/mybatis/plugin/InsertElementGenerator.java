package ht.msc.mybatis.plugin;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class InsertElementGenerator extends AbstractXmlElementGenerator
{
  private boolean isSimple;
  
  public InsertElementGenerator(){}

  public InsertElementGenerator(boolean isSimple)
  {
    this.isSimple = isSimple;
  }

  public void addElements(XmlElement parentElement)
  {
    XmlElement answer = new XmlElement("insert");

    answer.addAttribute(new Attribute(
      "id", this.introspectedTable.getInsertStatementId()));
    FullyQualifiedJavaType parameterType;
    if (this.isSimple)
      parameterType = new FullyQualifiedJavaType(
        this.introspectedTable.getBaseRecordType());
    else {
      parameterType = this.introspectedTable.getRules()
        .calculateAllFieldsClass();
    }

    answer.addAttribute(new Attribute("parameterType", 
      parameterType.getFullyQualifiedName()));

    this.context.getCommentGenerator().addComment(answer);

    GeneratedKey gk = this.introspectedTable.getGeneratedKey();
    if (gk != null) {
      IntrospectedColumn introspectedColumn = this.introspectedTable
        .getColumn(gk.getColumn());

      if (introspectedColumn != null) {
        if (gk.isJdbcStandard()) {
          answer.addAttribute(new Attribute(
            "useGeneratedKeys", "true"));
          answer.addAttribute(new Attribute(
            "keyProperty", introspectedColumn.getJavaProperty()));
        } else {
          answer.addElement(getSelectKey(introspectedColumn, gk));
        }
      }
    }

    StringBuilder insertClause = new StringBuilder();
    StringBuilder valuesClause = new StringBuilder();

    insertClause.append("INSERT INTO ");
    insertClause.append(this.introspectedTable
      .getFullyQualifiedTableNameAtRuntime());
    insertClause.append(" (");

    valuesClause.append("VALUES (");

    List<String> valuesClauses = new ArrayList();
    Iterator iter = this.introspectedTable.getAllColumns()
      .iterator();
    while (iter.hasNext()) {
      IntrospectedColumn introspectedColumn = (IntrospectedColumn)iter.next();
      if (!introspectedColumn.isIdentity())
      {
        insertClause.append(
          MyBatis3FormattingUtilities.getEscapedColumnName(introspectedColumn));
        valuesClause.append(
          MyBatis3FormattingUtilities.getParameterClause(introspectedColumn));
        if (iter.hasNext()) {
          insertClause.append(", ");
          valuesClause.append(", ");
        }

        if (valuesClause.length() > 80) {
          answer.addElement(new TextElement(insertClause.toString()));
          insertClause.setLength(0);
          OutputUtilities.xmlIndent(insertClause, 1);

          valuesClauses.add(valuesClause.toString());
          valuesClause.setLength(0);
          OutputUtilities.xmlIndent(valuesClause, 1);
        }
      }
    }
    insertClause.append(')');
    answer.addElement(new TextElement(insertClause.toString()));

    valuesClause.append(')');
    valuesClauses.add(valuesClause.toString());

    for (String clause : valuesClauses) {
      answer.addElement(new TextElement(clause));
    }

    if (this.context.getPlugins().sqlMapInsertElementGenerated(answer, 
      this.introspectedTable))
      parentElement.addElement(answer);
  }
}