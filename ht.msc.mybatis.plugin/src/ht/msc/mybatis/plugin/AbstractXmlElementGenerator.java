package ht.msc.mybatis.plugin;


public abstract class AbstractXmlElementGenerator extends AbstractGenerator
{
  public abstract void addElements(XmlElement paramXmlElement);

  protected XmlElement getSelectKey(IntrospectedColumn introspectedColumn, GeneratedKey generatedKey)
  {
    String identityColumnType = introspectedColumn
      .getFullyQualifiedJavaType().getFullyQualifiedName();

    XmlElement answer = new XmlElement("selectKey");
    answer.addAttribute(new Attribute("resultType", identityColumnType));
    answer.addAttribute(new Attribute(
      "keyProperty", introspectedColumn.getJavaProperty()));
    answer.addAttribute(new Attribute("order", 
      generatedKey.getMyBatis3Order()));

    answer.addElement(new TextElement(generatedKey
      .getRuntimeSqlStatement()));

    return answer;
  }

  protected XmlElement getBaseColumnListElement() {
    XmlElement answer = new XmlElement("include");
    answer.addAttribute(new Attribute("refid", 
      this.introspectedTable.getBaseColumnListId()));
    return answer;
  }

  protected XmlElement getBlobColumnListElement() {
    XmlElement answer = new XmlElement("include");
    answer.addAttribute(new Attribute("refid", 
      this.introspectedTable.getBlobColumnListId()));
    return answer;
  }

  protected XmlElement getExampleIncludeElement() {
    XmlElement ifElement = new XmlElement("if");
    ifElement.addAttribute(new Attribute("test", "_parameter != null"));

    XmlElement includeElement = new XmlElement("include");
    includeElement.addAttribute(new Attribute("refid", 
      this.introspectedTable.getExampleWhereClauseId()));
    ifElement.addElement(includeElement);

    return ifElement;
  }

  protected XmlElement getUpdateByExampleIncludeElement() {
    XmlElement ifElement = new XmlElement("if");
    ifElement.addAttribute(new Attribute("test", "_parameter != null"));

    XmlElement includeElement = new XmlElement("include");
    includeElement.addAttribute(new Attribute("refid", 
      this.introspectedTable.getMyBatis3UpdateByExampleWhereClauseId()));
    ifElement.addElement(includeElement);

    return ifElement;
  }
}