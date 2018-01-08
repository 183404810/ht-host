package ht.msc.mybatis.plugin;

import java.util.Iterator;

public class DeleteByPrimarayKeyForModelElementGenerator extends AbstractXmlElementGenerator
{
  public void addElements(XmlElement parentElement)
  {
    XmlElement answer = new XmlElement("delete");

    answer.addAttribute(new Attribute(
      "id", "deleteByPrimarayKeyForModel"));
    answer.addAttribute(new Attribute("parameterType", this.introspectedTable.getBaseRecordType()));

    this.context.getCommentGenerator().addComment(answer);

    StringBuilder sb = new StringBuilder();
    sb.append("DELETE FROM ");
    sb.append(this.introspectedTable.getFullyQualifiedTableNameAtRuntime());
    answer.addElement(new TextElement(sb.toString()));

    boolean and = false;

    Iterator localIterator = this.introspectedTable
      .getPrimaryKeyColumns().iterator();

    while (localIterator.hasNext()) {
      IntrospectedColumn introspectedColumn = (IntrospectedColumn)localIterator.next();
      sb.setLength(0);
      if (and) {
        sb.append("  AND ");
      } else {
        sb.append("WHERE ");
        and = true;
      }

      sb.append(
        MyBatis3FormattingUtilities.getEscapedColumnName(introspectedColumn));
      sb.append(" = ");
      sb.append(
        MyBatis3FormattingUtilities.getParameterClause(introspectedColumn));
      answer.addElement(new TextElement(sb.toString()));
    }

    if (this.context.getPlugins()
      .sqlMapDeleteByPrimaryKeyElementGenerated(answer, 
      this.introspectedTable))
      parentElement.addElement(answer);
  }
}