package ht.msc.mybatis.plugin;

import java.util.Iterator;


public class InsertSelectiveElementGenerator extends AbstractXmlElementGenerator
{
  public void addElements(XmlElement parentElement)
  {
    XmlElement answer = new XmlElement("insert");

    answer.addAttribute(new Attribute(
      "id", this.introspectedTable.getInsertSelectiveStatementId()));

    FullyQualifiedJavaType parameterType = this.introspectedTable.getRules()
      .calculateAllFieldsClass();

    answer.addAttribute(new Attribute("parameterType", 
      parameterType.getFullyQualifiedName()));

    this.context.getCommentGenerator().addComment(answer);

    GeneratedKey gk = this.introspectedTable.getGeneratedKey();
    if (gk != null) {
      IntrospectedColumn introspectedColumn = this.introspectedTable
        .getColumn(gk.getColumn());

      if (introspectedColumn != null) {
        if (gk.isJdbcStandard()) {
          answer.addAttribute(new Attribute("useGeneratedKeys", "true"));
          answer.addAttribute(new Attribute("keyProperty", introspectedColumn.getJavaProperty()));
        } else {
          answer.addElement(getSelectKey(introspectedColumn, gk));
        }
      }
    }

    StringBuilder sb = new StringBuilder();

    sb.append("INSERT INTO ");
    sb.append(this.introspectedTable.getFullyQualifiedTableNameAtRuntime());
    answer.addElement(new TextElement(sb.toString()));

    XmlElement insertTrimElement = new XmlElement("trim");
    insertTrimElement.addAttribute(new Attribute("prefix", "("));
    insertTrimElement.addAttribute(new Attribute("suffix", ")"));
    insertTrimElement.addAttribute(new Attribute("suffixOverrides", ","));
    answer.addElement(insertTrimElement);

    XmlElement valuesTrimElement = new XmlElement("trim");
    valuesTrimElement.addAttribute(new Attribute("prefix", "values ("));
    valuesTrimElement.addAttribute(new Attribute("suffix", ")"));
    valuesTrimElement.addAttribute(new Attribute("suffixOverrides", ","));
    answer.addElement(valuesTrimElement);

    Iterator localIterator = this.introspectedTable
      .getAllColumns().iterator();

    while (localIterator.hasNext()) {
      IntrospectedColumn introspectedColumn = (IntrospectedColumn)localIterator.next();
      if (!introspectedColumn.isIdentity())
      {
        if ((introspectedColumn.isSequenceColumn()) || 
          (introspectedColumn.getFullyQualifiedJavaType().isPrimitive()))
        {
          sb.setLength(0);
          sb.append(
            MyBatis3FormattingUtilities.getEscapedColumnName(introspectedColumn));
          sb.append(',');
          insertTrimElement.addElement(new TextElement(sb.toString()));

          sb.setLength(0);
          sb.append(
            MyBatis3FormattingUtilities.getParameterClause(introspectedColumn));
          sb.append(',');
          valuesTrimElement.addElement(new TextElement(sb.toString()));
        }
        else
        {
          XmlElement insertNotNullElement = new XmlElement("if");
          sb.setLength(0);
          sb.append(introspectedColumn.getJavaProperty());
          sb.append(" != null");
          insertNotNullElement.addAttribute(new Attribute(
            "test", sb.toString()));

          sb.setLength(0);
          sb.append(
            MyBatis3FormattingUtilities.getEscapedColumnName(introspectedColumn));
          sb.append(',');
          insertNotNullElement.addElement(new TextElement(sb.toString()));
          insertTrimElement.addElement(insertNotNullElement);

          XmlElement valuesNotNullElement = new XmlElement("if");
          sb.setLength(0);
          sb.append(introspectedColumn.getJavaProperty());
          sb.append(" != null");
          valuesNotNullElement.addAttribute(new Attribute(
            "test", sb.toString()));

          sb.setLength(0);
          sb.append(
            MyBatis3FormattingUtilities.getParameterClause(introspectedColumn));
          sb.append(',');
          valuesNotNullElement.addElement(new TextElement(sb.toString()));
          valuesTrimElement.addElement(valuesNotNullElement);
        }
      }
    }
    if (this.context.getPlugins().sqlMapInsertSelectiveElementGenerated(
      answer, this.introspectedTable))
      parentElement.addElement(answer);
  }
}