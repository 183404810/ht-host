package ht.msc.mybatis.plugin;

public class DeleteByExampleElementGenerator extends AbstractXmlElementGenerator
{
  public void addElements(XmlElement parentElement)
  {
    XmlElement answer = new XmlElement("delete");

    String fqjt = this.introspectedTable.getExampleType();

    answer.addAttribute(new Attribute(
      "id", this.introspectedTable.getDeleteByExampleStatementId()));
    answer.addAttribute(new Attribute("parameterType", fqjt));

    this.context.getCommentGenerator().addComment(answer);

    StringBuilder sb = new StringBuilder();
    sb.append("DELETE FROM ");
    sb.append(this.introspectedTable
      .getAliasedFullyQualifiedTableNameAtRuntime());
    answer.addElement(new TextElement(sb.toString()));
    answer.addElement(getExampleIncludeElement());

    if (this.context.getPlugins().sqlMapDeleteByExampleElementGenerated(
      answer, this.introspectedTable))
      parentElement.addElement(answer);
  }
}