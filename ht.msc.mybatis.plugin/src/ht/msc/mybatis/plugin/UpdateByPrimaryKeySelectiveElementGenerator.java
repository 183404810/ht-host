package ht.msc.mybatis.plugin;

import java.util.Iterator;


public class UpdateByPrimaryKeySelectiveElementGenerator extends AbstractXmlElementGenerator
{
  public void addElements(XmlElement parentElement)
  {
    XmlElement answer = new XmlElement("update");

    answer
      .addAttribute(new Attribute(
      "id", this.introspectedTable.getUpdateByPrimaryKeySelectiveStatementId()));
    String parameterType;
    if (this.introspectedTable.getRules().generateRecordWithBLOBsClass())
      parameterType = this.introspectedTable.getRecordWithBLOBsType();
    else {
      parameterType = this.introspectedTable.getBaseRecordType();
    }

    answer.addAttribute(new Attribute("parameterType", 
      parameterType));

    this.context.getCommentGenerator().addComment(answer);

    StringBuilder sb = new StringBuilder();

    sb.append("UPDATE ");
    sb.append(this.introspectedTable.getFullyQualifiedTableNameAtRuntime());
    answer.addElement(new TextElement(sb.toString()));

    XmlElement dynamicElement = new XmlElement("set");
    answer.addElement(dynamicElement);

    Iterator localIterator = this.introspectedTable
      .getNonPrimaryKeyColumns().iterator();
    
    XmlElement isNotNullElement;

    while (localIterator.hasNext()) {
      IntrospectedColumn introspectedColumn = (IntrospectedColumn)localIterator.next();
      isNotNullElement = new XmlElement("if");
      sb.setLength(0);
      sb.append(introspectedColumn.getJavaProperty());
      sb.append(" != null");
      isNotNullElement.addAttribute(new Attribute("test", sb.toString()));
      dynamicElement.addElement(isNotNullElement);

      sb.setLength(0);
      sb.append(
        MyBatis3FormattingUtilities.getEscapedColumnName(introspectedColumn));
      sb.append(" = ");
      sb.append(
        MyBatis3FormattingUtilities.getParameterClause(introspectedColumn));
      sb.append(',');

      isNotNullElement.addElement(new TextElement(sb.toString()));
    }
    Iterator notNullElement = this.introspectedTable.getPrimaryKeyColumns().iterator();
    boolean and = false;
    while (notNullElement.hasNext()) {
      IntrospectedColumn introspectedColumn = (IntrospectedColumn)notNullElement.next();
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
      .sqlMapUpdateByPrimaryKeySelectiveElementGenerated(answer, 
      this.introspectedTable))
      parentElement.addElement(answer);
  }
}