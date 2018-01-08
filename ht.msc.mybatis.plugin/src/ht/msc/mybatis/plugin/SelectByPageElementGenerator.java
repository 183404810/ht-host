package ht.msc.mybatis.plugin;

public class SelectByPageElementGenerator extends AbstractXmlElementGenerator
{
  public void addElements(XmlElement parentElement)
  {
    String dbmsTypeStr = this.context.getDbmsType();
    DbmsTypeEnum dbmsTypeEnums = DbmsTypeEnum.valueOf(dbmsTypeStr.toUpperCase());
    switch (dbmsTypeEnums) {
    case ORACLE:
      getOraclePageElement(parentElement);
      break;
    case MYSQL:
      getMysqlPageElement(parentElement);
      break;
    case POSTGRESQL:
      getPGPageElement(parentElement);
    }
  }

  private void getOraclePageElement(XmlElement parentElement)
  {
    XmlElement answer = new XmlElement("select");

    answer.addAttribute(new Attribute(
      "id", "selectByPage"));
    answer.addAttribute(new Attribute("resultMap", 
      this.introspectedTable.getBaseResultMapId()));

    answer.addAttribute(new Attribute("parameterType", 
      "map"));

    this.context.getCommentGenerator().addComment(answer);

    StringBuilder sb = new StringBuilder();
    sb.append("SELECT B.* from (SELECT A.*,ROWNUM rn FROM( ");

    sb.append("SELECT ");

    answer.addElement(new TextElement(sb.toString()));
    answer.addElement(getBaseColumnListElement());
    if (this.introspectedTable.hasBLOBColumns()) {
      answer.addElement(new TextElement(","));
      answer.addElement(getBlobColumnListElement());
    }

    sb.setLength(0);
    sb.append(" FROM ");
    sb.append(this.introspectedTable
      .getAliasedFullyQualifiedTableNameAtRuntime());
    sb.append(" WHERE 1=1 ");
    answer.addElement(new TextElement(sb.toString()));

    XmlElement sqlElement = new XmlElement("include");
    sqlElement.addAttribute(new Attribute("refid", "condition"));
    answer.addElement(sqlElement);

    XmlElement orderByElement = new XmlElement("if");
    sb.setLength(0);
    sb.append("orderByField");
    sb.append(" != null and ");
    sb.append("''!=orderByField");
    orderByElement.addAttribute(new Attribute(
      "test", sb.toString()));
    sb.setLength(0);
    sb.append("ORDER BY ${orderByField}");
    orderByElement.addElement(new TextElement(sb.toString()));

    XmlElement orderByConditionElement = new XmlElement("if");
    sb.setLength(0);
    sb.append("orderByField");
    orderByConditionElement.addAttribute(new Attribute("test", sb.toString()));
    sb.setLength(0);
    sb.append("${orderBy}");
    orderByConditionElement.addElement(new TextElement(sb.toString()));
    orderByElement.addElement(orderByConditionElement);
    answer.addElement(orderByElement);

    sb.setLength(0);
    sb.append(") A WHERE ROWNUM &lt; #{page.endRowNum}");
    sb.append(") B WHERE rn &gt; #{page.startRowNum}");
    answer.addElement(new TextElement(sb.toString()));

    parentElement.addElement(answer);
  }

  private void getMysqlPageElement(XmlElement parentElement)
  {
    XmlElement answer = new XmlElement("select");

    answer.addAttribute(new Attribute(
      "id", "selectByPage"));
    answer.addAttribute(new Attribute("resultMap", 
      this.introspectedTable.getBaseResultMapId()));

    answer.addAttribute(new Attribute("parameterType", 
      "map"));

    this.context.getCommentGenerator().addComment(answer);

    StringBuilder sb = new StringBuilder();

    sb.append("SELECT ");

    answer.addElement(new TextElement(sb.toString()));
    answer.addElement(getBaseColumnListElement());
    if (this.introspectedTable.hasBLOBColumns()) {
      answer.addElement(new TextElement(","));
      answer.addElement(getBlobColumnListElement());
    }

    sb.setLength(0);
    sb.append(" FROM ");
    sb.append(this.introspectedTable
      .getAliasedFullyQualifiedTableNameAtRuntime());
    sb.append(" WHERE 1=1 ");
    answer.addElement(new TextElement(sb.toString()));

    XmlElement sqlElement = new XmlElement("include");
    sqlElement.addAttribute(new Attribute("refid", "condition"));
    answer.addElement(sqlElement);

    XmlElement orderByElement = new XmlElement("if");
    sb.setLength(0);
    sb.append("orderByField");
    sb.append(" != null and ");
    sb.append("''!=orderByField");
    orderByElement.addAttribute(new Attribute(
      "test", sb.toString()));
    sb.setLength(0);
    sb.append("ORDER BY ${orderByField}");
    orderByElement.addElement(new TextElement(sb.toString()));

    XmlElement orderByConditionElement = new XmlElement("if");
    sb.setLength(0);
    sb.append("orderByField");
    orderByConditionElement.addAttribute(new Attribute("test", sb.toString()));
    sb.setLength(0);
    sb.append("${orderBy}");
    orderByConditionElement.addElement(new TextElement(sb.toString()));
    orderByElement.addElement(orderByConditionElement);
    answer.addElement(orderByElement);

    parentElement.addElement(answer);
  }

  private void getPGPageElement(XmlElement parentElement)
  {
    XmlElement answer = new XmlElement("select");

    answer.addAttribute(new Attribute(
      "id", "selectByPage"));
    answer.addAttribute(new Attribute("resultMap", 
      this.introspectedTable.getBaseResultMapId()));

    answer.addAttribute(new Attribute("parameterType", 
      "map"));

    this.context.getCommentGenerator().addComment(answer);

    StringBuilder sb = new StringBuilder();

    sb.append("SELECT ");

    answer.addElement(new TextElement(sb.toString()));
    answer.addElement(getBaseColumnListElement());
    if (this.introspectedTable.hasBLOBColumns()) {
      answer.addElement(new TextElement(","));
      answer.addElement(getBlobColumnListElement());
    }

    sb.setLength(0);
    sb.append(" FROM ");
    sb.append(this.introspectedTable
      .getAliasedFullyQualifiedTableNameAtRuntime());

    answer.addElement(new TextElement(sb.toString()));

    XmlElement whereElement = new XmlElement("where");

    XmlElement sqlElement = new XmlElement("include");
    sqlElement.addAttribute(new Attribute("refid", "condition"));
    whereElement.addElement(sqlElement);
    answer.addElement(whereElement);

    XmlElement orderByElement = new XmlElement("if");
    sb.setLength(0);
    sb.append("orderByField");
    sb.append(" != null and ");
    sb.append("''!=orderByField");
    orderByElement.addAttribute(new Attribute(
      "test", sb.toString()));
    sb.setLength(0);
    sb.append("ORDER BY ${orderByField}");
    orderByElement.addElement(new TextElement(sb.toString()));

    XmlElement orderByConditionElement = new XmlElement("if");
    sb.setLength(0);
    sb.append("orderByField");
    orderByConditionElement.addAttribute(new Attribute("test", sb.toString()));
    sb.setLength(0);
    sb.append("${orderBy}");
    orderByConditionElement.addElement(new TextElement(sb.toString()));
    orderByElement.addElement(orderByConditionElement);
    answer.addElement(orderByElement);

    sb.setLength(0);
    sb.append(" LIMIT #{page.pageSize} OFFSET #{page.startRowNum} ");

    answer.addElement(new TextElement(sb.toString()));
    parentElement.addElement(answer);
  }
}