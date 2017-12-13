package ht.msc.mybatis.plugin;

public class SqlConditionElementGenerator extends AbstractXmlElementGenerator
{
  public void addElements(XmlElement parentElement)
  {
    XmlElement answer = new XmlElement("sql");

    answer.addAttribute(new Attribute("id", "condition"));

    StringBuilder sb = new StringBuilder();

    XmlElement conditionElement = new XmlElement("if");
    sb.append("null!=params");
    conditionElement.addAttribute(new Attribute(
      "test", sb.toString()));

    XmlElement conditionElement1 = new XmlElement("if");
    sb.setLength(0);
    sb.append("null!=params.queryCondition and ''!=params.queryCondition");
    conditionElement1.addAttribute(new Attribute(
      "test", sb.toString()));
    sb.setLength(0);
    sb.append("${params.queryCondition}");
    conditionElement1.addElement(new TextElement(sb.toString()));

    conditionElement.addElement(conditionElement1);

    answer.addElement(conditionElement);

    parentElement.addElement(answer);
    this.context.getCommentGenerator().addComment(answer);
  }
}