package ht.msc.mybatis.plugin;

import java.util.Iterator;


public class SimpleSelectByPrimaryKeyElementGenerator extends AbstractXmlElementGenerator
{
  public void addElements(XmlElement parentElement)
  {
    XmlElement answer = new XmlElement("select");

    answer.addAttribute(new Attribute(
      "id", this.introspectedTable.getSelectByPrimaryKeyStatementId()));
    answer.addAttribute(new Attribute("resultMap", 
      this.introspectedTable.getBaseResultMapId()));
    String parameterType;
    if (this.introspectedTable.getPrimaryKeyColumns().size() > 1)
      parameterType = "map";
    else {
      parameterType = ((IntrospectedColumn)this.introspectedTable.getPrimaryKeyColumns().get(0))
        .getFullyQualifiedJavaType().toString();
    }

    answer.addAttribute(new Attribute("parameterType", 
      parameterType));

    this.context.getCommentGenerator().addComment(answer);

    StringBuilder sb = new StringBuilder();
    sb.append("SELECT ");

    if (StringUtility.stringHasValue(this.introspectedTable.getSelectByPrimaryKeyQueryId())) {
      sb.append('\'');
      sb.append(this.introspectedTable.getSelectByPrimaryKeyQueryId());
      sb.append("' as QUERYID,");
    }

    Iterator iter = this.introspectedTable.getAllColumns()
      .iterator();
    while (iter.hasNext()) {
      sb.append(MyBatis3FormattingUtilities.getSelectListPhrase(
        (IntrospectedColumn)iter
        .next()));

      if (iter.hasNext()) {
        sb.append(", ");
      }

      if (sb.length() > 80) {
        answer.addElement(new TextElement(sb.toString()));
        sb.setLength(0);
      }
    }

    if (sb.length() > 0) {
      answer.addElement(new TextElement(sb.toString()));
    }

    sb.setLength(0);
    sb.append("FROM ");
    sb.append(this.introspectedTable
      .getAliasedFullyQualifiedTableNameAtRuntime());
    answer.addElement(new TextElement(sb.toString()));

    boolean and = false;

    Iterator localIterator1 = this.introspectedTable
      .getPrimaryKeyColumns().iterator();

    while (localIterator1.hasNext()) {
      IntrospectedColumn introspectedColumn = (IntrospectedColumn)localIterator1.next();
      sb.setLength(0);
      if (and) {
        sb.append("  AND ");
      } else {
        sb.append("WHERE ");
        and = true;
      }

      sb.append(
        MyBatis3FormattingUtilities.getAliasedEscapedColumnName(introspectedColumn));
      sb.append(" = ");
      sb.append(
        MyBatis3FormattingUtilities.getParameterClause(introspectedColumn));
      answer.addElement(new TextElement(sb.toString()));
    }

    if (this.context.getPlugins().sqlMapSelectByPrimaryKeyElementGenerated(
      answer, this.introspectedTable))
      parentElement.addElement(answer);
  }
}