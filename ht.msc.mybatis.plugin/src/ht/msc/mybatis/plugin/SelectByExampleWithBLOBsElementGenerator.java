package ht.msc.mybatis.plugin;


public class SelectByExampleWithBLOBsElementGenerator extends AbstractXmlElementGenerator
{
  public void addElements(XmlElement parentElement)
  {
    String fqjt = this.introspectedTable.getExampleType();

    XmlElement answer = new XmlElement("select");
    answer
      .addAttribute(new Attribute(
      "id", this.introspectedTable.getSelectByExampleWithBLOBsStatementId()));
    answer.addAttribute(new Attribute(
      "resultMap", this.introspectedTable.getResultMapWithBLOBsId()));
    answer.addAttribute(new Attribute("parameterType", fqjt));

    this.context.getCommentGenerator().addComment(answer);

    answer.addElement(new TextElement("SELECT"));
    XmlElement ifElement = new XmlElement("if");
    ifElement.addAttribute(new Attribute("test", "distinct"));
    ifElement.addElement(new TextElement("DISTINCT"));
    answer.addElement(ifElement);

    StringBuilder sb = new StringBuilder();
    if (StringUtility.stringHasValue(this.introspectedTable
      .getSelectByExampleQueryId())) {
      sb.append('\'');
      sb.append(this.introspectedTable.getSelectByExampleQueryId());
      sb.append("' as QUERYID,");
      answer.addElement(new TextElement(sb.toString()));
    }

    answer.addElement(getBaseColumnListElement());
    answer.addElement(new TextElement(","));
    answer.addElement(getBlobColumnListElement());

    sb.setLength(0);
    sb.append("FROM ");
    sb.append(this.introspectedTable
      .getAliasedFullyQualifiedTableNameAtRuntime());
    answer.addElement(new TextElement(sb.toString()));
    answer.addElement(getExampleIncludeElement());

    ifElement = new XmlElement("if");
    ifElement.addAttribute(new Attribute("test", "orderByClause != null"));
    ifElement.addElement(new TextElement("ORDER BY ${orderByClause}"));
    answer.addElement(ifElement);

    if (this.context.getPlugins()
      .sqlMapSelectByExampleWithBLOBsElementGenerated(answer, 
      this.introspectedTable))
      parentElement.addElement(answer);
  }
}