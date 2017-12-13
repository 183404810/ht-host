package ht.msc.mybatis.plugin;

import java.util.Iterator;


public class SimpleSelectAllElementGenerator extends AbstractXmlElementGenerator
{
  public void addElements(XmlElement parentElement)
  {
    XmlElement answer = new XmlElement("select");

    answer.addAttribute(new Attribute(
      "id", this.introspectedTable.getSelectAllStatementId()));
    answer.addAttribute(new Attribute("resultMap", 
      this.introspectedTable.getBaseResultMapId()));

    this.context.getCommentGenerator().addComment(answer);

    StringBuilder sb = new StringBuilder();
    sb.append("SELECT ");
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

    String orderByClause = this.introspectedTable.getTableConfigurationProperty("selectAllOrderByClause");
    boolean hasOrderBy = StringUtility.stringHasValue(orderByClause);
    if (hasOrderBy) {
      sb.setLength(0);
      sb.append("ORDER BY ");
      sb.append(orderByClause);
      answer.addElement(new TextElement(sb.toString()));
    }

    if (this.context.getPlugins().sqlMapSelectAllElementGenerated(
      answer, this.introspectedTable))
      parentElement.addElement(answer);
  }
}