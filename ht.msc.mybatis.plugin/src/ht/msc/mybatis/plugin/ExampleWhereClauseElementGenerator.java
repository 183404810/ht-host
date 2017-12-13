package ht.msc.mybatis.plugin;

import java.util.Iterator;

public class ExampleWhereClauseElementGenerator extends AbstractXmlElementGenerator
{
  private boolean isForUpdateByExample;
  public ExampleWhereClauseElementGenerator(){}

  public ExampleWhereClauseElementGenerator(boolean isForUpdateByExample)
  {
    this.isForUpdateByExample = isForUpdateByExample;
  }

  public void addElements(XmlElement parentElement)
  {
    XmlElement answer = new XmlElement("sql");

    if (this.isForUpdateByExample)
      answer
        .addAttribute(new Attribute(
        "id", this.introspectedTable.getMyBatis3UpdateByExampleWhereClauseId()));
    else {
      answer.addAttribute(new Attribute(
        "id", this.introspectedTable.getExampleWhereClauseId()));
    }

    this.context.getCommentGenerator().addComment(answer);

    XmlElement whereElement = new XmlElement("where");
    answer.addElement(whereElement);

    XmlElement outerForEachElement = new XmlElement("foreach");
    if (this.isForUpdateByExample)
      outerForEachElement.addAttribute(new Attribute(
        "collection", "example.oredCriteria"));
    else {
      outerForEachElement.addAttribute(new Attribute(
        "collection", "oredCriteria"));
    }
    outerForEachElement.addAttribute(new Attribute("item", "criteria"));
    outerForEachElement.addAttribute(new Attribute("separator", "or"));
    whereElement.addElement(outerForEachElement);

    XmlElement ifElement = new XmlElement("if");
    ifElement.addAttribute(new Attribute("test", "criteria.valid"));
    outerForEachElement.addElement(ifElement);

    XmlElement trimElement = new XmlElement("trim");
    trimElement.addAttribute(new Attribute("prefix", "("));
    trimElement.addAttribute(new Attribute("suffix", ")"));
    trimElement.addAttribute(new Attribute("prefixOverrides", "and"));

    ifElement.addElement(trimElement);

    trimElement.addElement(getMiddleForEachElement(null));

    Iterator localIterator = this.introspectedTable
      .getNonBLOBColumns().iterator();

    while (localIterator.hasNext()) {
      IntrospectedColumn introspectedColumn = (IntrospectedColumn)localIterator.next();
      if (StringUtility.stringHasValue(introspectedColumn
        .getTypeHandler())) {
        trimElement
          .addElement(getMiddleForEachElement(introspectedColumn));
      }

    }

    if (this.context.getPlugins()
      .sqlMapExampleWhereClauseElementGenerated(answer, 
      this.introspectedTable))
      parentElement.addElement(answer);
  }

  private XmlElement getMiddleForEachElement(IntrospectedColumn introspectedColumn)
  {
    StringBuilder sb = new StringBuilder();
    String typeHandlerString;
    String criteriaAttribute;
    boolean typeHandled;
    if (introspectedColumn == null) {
      criteriaAttribute = "criteria.criteria";
      typeHandled = false;
      typeHandlerString = null;
    } else {
      sb.setLength(0);
      sb.append("criteria.");
      sb.append(introspectedColumn.getJavaProperty());
      sb.append("Criteria");
      criteriaAttribute = sb.toString();

      typeHandled = true;

      sb.setLength(0);
      sb.append(",typeHandler=");
      sb.append(introspectedColumn.getTypeHandler());
      typeHandlerString = sb.toString();
    }

    XmlElement middleForEachElement = new XmlElement("foreach");
    middleForEachElement.addAttribute(new Attribute(
      "collection", criteriaAttribute));
    middleForEachElement.addAttribute(new Attribute("item", "criterion"));

    XmlElement chooseElement = new XmlElement("choose");
    middleForEachElement.addElement(chooseElement);

    XmlElement when = new XmlElement("when");
    when.addAttribute(new Attribute("test", "criterion.noValue"));
    when.addElement(new TextElement("and ${criterion.condition}"));
    chooseElement.addElement(when);

    when = new XmlElement("when");
    when.addAttribute(new Attribute("test", "criterion.singleValue"));
    sb.setLength(0);
    sb.append("AND ${criterion.condition} #{criterion.value");
    if (typeHandled) {
      sb.append(typeHandlerString);
    }
    sb.append('}');
    when.addElement(new TextElement(sb.toString()));
    chooseElement.addElement(when);

    when = new XmlElement("when");
    when.addAttribute(new Attribute("test", "criterion.betweenValue"));
    sb.setLength(0);
    sb.append("AND ${criterion.condition} #{criterion.value");
    if (typeHandled) {
      sb.append(typeHandlerString);
    }
    sb.append("} AND #{criterion.secondValue");
    if (typeHandled) {
      sb.append(typeHandlerString);
    }
    sb.append('}');
    when.addElement(new TextElement(sb.toString()));
    chooseElement.addElement(when);

    when = new XmlElement("when");
    when.addAttribute(new Attribute("test", "criterion.listValue"));
    when.addElement(new TextElement("and ${criterion.condition}"));
    XmlElement innerForEach = new XmlElement("foreach");
    innerForEach
      .addAttribute(new Attribute("collection", "criterion.value"));
    innerForEach.addAttribute(new Attribute("item", "listItem"));
    innerForEach.addAttribute(new Attribute("open", "("));
    innerForEach.addAttribute(new Attribute("close", ")"));
    innerForEach.addAttribute(new Attribute("separator", ","));
    sb.setLength(0);
    sb.append("#{listItem");
    if (typeHandled) {
      sb.append(typeHandlerString);
    }
    sb.append('}');
    innerForEach.addElement(new TextElement(sb.toString()));
    when.addElement(innerForEach);
    chooseElement.addElement(when);

    return middleForEachElement;
  }
}