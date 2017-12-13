package ht.msc.mybatis.plugin;

import java.util.Iterator;
import java.util.List;

public class ResultMapWithoutBLOBsElementGenerator extends AbstractXmlElementGenerator
{
  private boolean isSimple;
  
  public ResultMapWithoutBLOBsElementGenerator(){}
  public ResultMapWithoutBLOBsElementGenerator(boolean isSimple)
  {
    this.isSimple = isSimple;
  }

  public void addElements(XmlElement parentElement)
  {
    XmlElement answer = new XmlElement("resultMap");
    answer.addAttribute(new Attribute("id", 
      this.introspectedTable.getBaseResultMapId()));
    String returnType;
    if (this.isSimple) {
      returnType = this.introspectedTable.getBaseRecordType();
    }
    else
    {
      if (this.introspectedTable.getRules().generateBaseRecordClass())
        returnType = this.introspectedTable.getBaseRecordType();
      else {
        returnType = this.introspectedTable.getPrimaryKeyType();
      }
    }

    answer.addAttribute(new Attribute("type", 
      returnType));

    this.context.getCommentGenerator().addComment(answer);

    if (this.introspectedTable.isConstructorBased())
      addResultMapConstructorElements(answer);
    else {
      addResultMapElements(answer);
    }

    if (this.context.getPlugins().sqlMapResultMapWithoutBLOBsElementGenerated(
      answer, this.introspectedTable))
      parentElement.addElement(answer);
  }

  private void addResultMapElements(XmlElement answer)
  {
    Iterator localIterator = this.introspectedTable
      .getPrimaryKeyColumns().iterator();
    XmlElement resultElement;
    while (localIterator.hasNext()) {
      IntrospectedColumn introspectedColumn = (IntrospectedColumn)localIterator.next();
      resultElement = new XmlElement("id");

      resultElement
        .addAttribute(new Attribute(
        "column", MyBatis3FormattingUtilities.getRenamedColumnNameForResultMap(introspectedColumn)));
      resultElement.addAttribute(new Attribute(
        "property", introspectedColumn.getJavaProperty()));
      resultElement.addAttribute(new Attribute("jdbcType", 
        introspectedColumn.getJdbcTypeName()));

      if (StringUtility.stringHasValue(introspectedColumn.getTypeHandler())) {
        resultElement.addAttribute(new Attribute(
          "typeHandler", introspectedColumn.getTypeHandler()));
      }

      answer.addElement(resultElement);
    }
    List<IntrospectedColumn> columns;
    if (this.isSimple)
      columns = this.introspectedTable.getNonPrimaryKeyColumns();
    else {
      columns = this.introspectedTable.getBaseColumns();
    }
    for (IntrospectedColumn introspectedColumn : columns) {
       resultElement = new XmlElement("result");

      resultElement
        .addAttribute(new Attribute(
        "column", MyBatis3FormattingUtilities.getRenamedColumnNameForResultMap(introspectedColumn)));
      resultElement.addAttribute(new Attribute(
        "property", introspectedColumn.getJavaProperty()));
      resultElement.addAttribute(new Attribute("jdbcType", 
        introspectedColumn.getJdbcTypeName()));

      if (StringUtility.stringHasValue(introspectedColumn.getTypeHandler())) {
        resultElement.addAttribute(new Attribute(
          "typeHandler", introspectedColumn.getTypeHandler()));
      }

      answer.addElement(resultElement);
    }
  }

  private void addResultMapConstructorElements(XmlElement answer) {
    XmlElement constructor = new XmlElement("constructor");

    Iterator localIterator = this.introspectedTable
      .getPrimaryKeyColumns().iterator();
    XmlElement resultElement;
    while (localIterator.hasNext()) {
      IntrospectedColumn introspectedColumn = (IntrospectedColumn)localIterator.next();
      resultElement = new XmlElement("idArg");

      resultElement
        .addAttribute(new Attribute(
        "column", MyBatis3FormattingUtilities.getRenamedColumnNameForResultMap(introspectedColumn)));
      resultElement.addAttribute(new Attribute("jdbcType", 
        introspectedColumn.getJdbcTypeName()));
      resultElement.addAttribute(new Attribute("javaType", 
        introspectedColumn.getFullyQualifiedJavaType()
        .getFullyQualifiedName()));

      if (StringUtility.stringHasValue(introspectedColumn.getTypeHandler())) {
        resultElement.addAttribute(new Attribute(
          "typeHandler", introspectedColumn.getTypeHandler()));
      }

      constructor.addElement(resultElement);
    }
    List<IntrospectedColumn> columns;
    if (this.isSimple)
      columns = this.introspectedTable.getNonPrimaryKeyColumns();
    else {
      columns = this.introspectedTable.getBaseColumns();
    }
    for (IntrospectedColumn introspectedColumn : columns) {
      resultElement = new XmlElement("arg");

      resultElement
        .addAttribute(new Attribute(
        "column", MyBatis3FormattingUtilities.getRenamedColumnNameForResultMap(introspectedColumn)));
      resultElement.addAttribute(new Attribute("jdbcType", 
        introspectedColumn.getJdbcTypeName()));
      resultElement.addAttribute(new Attribute("javaType", 
        introspectedColumn.getFullyQualifiedJavaType()
        .getFullyQualifiedName()));

      if (StringUtility.stringHasValue(introspectedColumn.getTypeHandler())) {
        resultElement.addAttribute(new Attribute(
          "typeHandler", introspectedColumn.getTypeHandler()));
      }

      constructor.addElement(resultElement);
    }

    answer.addElement(constructor);
  }
}