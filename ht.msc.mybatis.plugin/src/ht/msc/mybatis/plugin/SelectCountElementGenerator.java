package ht.msc.mybatis.plugin;


public class SelectCountElementGenerator extends AbstractXmlElementGenerator
{
  public void addElements(XmlElement parentElement)
  {
    XmlElement answer = new XmlElement("select");
    answer.addAttribute(new Attribute(
      "id", "selectCount"));
    answer.addAttribute(new Attribute(
      "resultType", "java.lang.Integer"));

    StringBuilder sb = new StringBuilder();
    sb.append("SELECT ");
    sb.append("COUNT(1) as s");
    sb.append(" FROM ");
    sb.append(this.introspectedTable
      .getAliasedFullyQualifiedTableNameAtRuntime());
    sb.append(" WHERE 1=1 ");
    answer.addElement(new TextElement(sb.toString()));

    XmlElement sqlElement = new XmlElement("include");
    sqlElement.addAttribute(new Attribute("refid", "condition"));
    answer.addElement(sqlElement);

    parentElement.addElement(answer);
  }
}