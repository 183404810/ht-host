package ht.msc.mybatis.plugin;

public class XMLMapperGenerator extends AbstractXmlGenerator
{
  protected XmlElement getSqlMapElement()
  {
    FullyQualifiedTable table = this.introspectedTable.getFullyQualifiedTable();
    this.progressCallback.startTask(Messages.getString(
      "Progress.12", table.toString()));
    XmlElement answer = new XmlElement("mapper");
    String namespace = this.introspectedTable.getMyBatis3SqlMapNamespace();
    answer.addAttribute(new Attribute("namespace", 
      namespace));

    this.context.getCommentGenerator().addRootComment(answer);

    addResultMapWithoutBLOBsElement(answer);
    addResultMapWithBLOBsElement(answer);
    addExampleWhereClauseElement(answer);
    addMyBatis3UpdateByExampleWhereClauseElement(answer);
    addBaseColumnListElement(answer);

    addSqlConditionElement(answer);

    addBlobColumnListElement(answer);
    addSelectByExampleWithBLOBsElement(answer);
    addSelectByExampleWithoutBLOBsElement(answer);
    addSelectByPrimaryKeyElement(answer);

    addSelectCountElement(answer);
    addSelectByPageElement(answer);
    addSelectByParamsElement(answer);

    addDeleteByPrimaryKeyElement(answer);

    addDeleteByPrimarayKeyForModelElement(answer);

    addDeleteByExampleElement(answer);
    addInsertElement(answer);
    addInsertSelectiveElement(answer);
    addCountByExampleElement(answer);
    addUpdateByExampleSelectiveElement(answer);
    addUpdateByExampleWithBLOBsElement(answer);
    addUpdateByExampleWithoutBLOBsElement(answer);
    addUpdateByPrimaryKeySelectiveElement(answer);
    addUpdateByPrimaryKeyWithBLOBsElement(answer);
    addUpdateByPrimaryKeyWithoutBLOBsElement(answer);

    return answer;
  }

  protected void addResultMapWithoutBLOBsElement(XmlElement parentElement) {
    if (this.introspectedTable.getRules().generateBaseResultMap()) {
      AbstractXmlElementGenerator elementGenerator = new ResultMapWithoutBLOBsElementGenerator(false);
      initializeAndExecuteGenerator(elementGenerator, parentElement);
    }
  }

  protected void addResultMapWithBLOBsElement(XmlElement parentElement) {
    if (this.introspectedTable.getRules().generateResultMapWithBLOBs()) {
      AbstractXmlElementGenerator elementGenerator = new ResultMapWithBLOBsElementGenerator();
      initializeAndExecuteGenerator(elementGenerator, parentElement);
    }
  }

  protected void addExampleWhereClauseElement(XmlElement parentElement) {
    if (this.introspectedTable.getRules().generateSQLExampleWhereClause()) {
      AbstractXmlElementGenerator elementGenerator = new ExampleWhereClauseElementGenerator(
        false);
      initializeAndExecuteGenerator(elementGenerator, parentElement);
    }
  }

  protected void addMyBatis3UpdateByExampleWhereClauseElement(XmlElement parentElement)
  {
    if (this.introspectedTable.getRules()
      .generateMyBatis3UpdateByExampleWhereClause()) {
      AbstractXmlElementGenerator elementGenerator = new ExampleWhereClauseElementGenerator(
        true);
      initializeAndExecuteGenerator(elementGenerator, parentElement);
    }
  }

  protected void addBaseColumnListElement(XmlElement parentElement) {
    if (this.introspectedTable.getRules().generateBaseColumnList()) {
      AbstractXmlElementGenerator elementGenerator = new BaseColumnListElementGenerator();
      initializeAndExecuteGenerator(elementGenerator, parentElement);
    }
  }

  protected void addBlobColumnListElement(XmlElement parentElement) {
    if (this.introspectedTable.getRules().generateBlobColumnList()) {
      AbstractXmlElementGenerator elementGenerator = new BlobColumnListElementGenerator();
      initializeAndExecuteGenerator(elementGenerator, parentElement);
    }
  }

  protected void addSelectByExampleWithoutBLOBsElement(XmlElement parentElement)
  {
    if (this.introspectedTable.getRules().generateSelectByExampleWithoutBLOBs()) {
      AbstractXmlElementGenerator elementGenerator = new SelectByExampleWithoutBLOBsElementGenerator();
      initializeAndExecuteGenerator(elementGenerator, parentElement);
    }
  }

  protected void addSelectByExampleWithBLOBsElement(XmlElement parentElement) {
    if (this.introspectedTable.getRules().generateSelectByExampleWithBLOBs()) {
      AbstractXmlElementGenerator elementGenerator = new SelectByExampleWithBLOBsElementGenerator();
      initializeAndExecuteGenerator(elementGenerator, parentElement);
    }
  }

  protected void addSelectByPrimaryKeyElement(XmlElement parentElement) {
    if (this.introspectedTable.getRules().generateSelectByPrimaryKey()) {
      AbstractXmlElementGenerator elementGenerator = new SelectByPrimaryKeyElementGenerator();
      initializeAndExecuteGenerator(elementGenerator, parentElement);
    }
  }

  protected void addSelectCountElement(XmlElement parentElement)
  {
    if (this.introspectedTable.getRules().generateSelectByPrimaryKey()) {
      AbstractXmlElementGenerator elementGenerator = new SelectCountElementGenerator();
      initializeAndExecuteGenerator(elementGenerator, parentElement);
    }
  }

  protected void addSelectByPageElement(XmlElement parentElement)
  {
    if (this.introspectedTable.getRules().generateSelectByPrimaryKey()) {
      AbstractXmlElementGenerator elementGenerator = new SelectByPageElementGenerator();
      initializeAndExecuteGenerator(elementGenerator, parentElement);
    }
  }

  protected void addSelectByParamsElement(XmlElement parentElement)
  {
    AbstractXmlElementGenerator elementGenerator = new SelectByParamsElementGenerator();
    initializeAndExecuteGenerator(elementGenerator, parentElement);
  }

  protected void addSelectByDataAuthorityElement(XmlElement parentElement)
  {
    SelectByDataAuthorityElementGenerator elementGenerator = new SelectByDataAuthorityElementGenerator();
    initializeAndExecuteGenerator(elementGenerator, parentElement);
  }

  protected void addSqlConditionElement(XmlElement parentElement)
  {
    AbstractXmlElementGenerator elementGenerator = new SqlConditionElementGenerator();
    initializeAndExecuteGenerator(elementGenerator, parentElement);
  }

  protected void addDeleteByExampleElement(XmlElement parentElement) {
    if (this.introspectedTable.getRules().generateDeleteByExample()) {
      AbstractXmlElementGenerator elementGenerator = new DeleteByExampleElementGenerator();
      initializeAndExecuteGenerator(elementGenerator, parentElement);
    }
  }

  protected void addDeleteByPrimaryKeyElement(XmlElement parentElement) {
    if (this.introspectedTable.getRules().generateDeleteByPrimaryKey()) {
      AbstractXmlElementGenerator elementGenerator = new DeleteByPrimaryKeyElementGenerator(false);
      initializeAndExecuteGenerator(elementGenerator, parentElement);
    }
  }

  protected void addDeleteByPrimarayKeyForModelElement(XmlElement parentElement)
  {
    if (this.introspectedTable.getRules().generateDeleteByPrimaryKey()) {
      AbstractXmlElementGenerator elementGenerator = new DeleteByPrimarayKeyForModelElementGenerator();
      initializeAndExecuteGenerator(elementGenerator, parentElement);
    }
  }

  protected void addInsertElement(XmlElement parentElement) {
    if (this.introspectedTable.getRules().generateInsert()) {
      AbstractXmlElementGenerator elementGenerator = new InsertElementGenerator(false);
      initializeAndExecuteGenerator(elementGenerator, parentElement);
    }
  }

  protected void addInsertSelectiveElement(XmlElement parentElement) {
    if (this.introspectedTable.getRules().generateInsertSelective()) {
      AbstractXmlElementGenerator elementGenerator = new InsertSelectiveElementGenerator();
      initializeAndExecuteGenerator(elementGenerator, parentElement);
    }
  }

  protected void addCountByExampleElement(XmlElement parentElement) {
    if (this.introspectedTable.getRules().generateCountByExample()) {
      AbstractXmlElementGenerator elementGenerator = new CountByExampleElementGenerator();
      initializeAndExecuteGenerator(elementGenerator, parentElement);
    }
  }

  protected void addUpdateByExampleSelectiveElement(XmlElement parentElement) {
    if (this.introspectedTable.getRules().generateUpdateByExampleSelective()) {
      AbstractXmlElementGenerator elementGenerator = new UpdateByExampleSelectiveElementGenerator();
      initializeAndExecuteGenerator(elementGenerator, parentElement);
    }
  }

  protected void addUpdateByExampleWithBLOBsElement(XmlElement parentElement) {
    if (this.introspectedTable.getRules().generateUpdateByExampleWithBLOBs()) {
      AbstractXmlElementGenerator elementGenerator = new UpdateByExampleWithBLOBsElementGenerator();
      initializeAndExecuteGenerator(elementGenerator, parentElement);
    }
  }

  protected void addUpdateByExampleWithoutBLOBsElement(XmlElement parentElement)
  {
    if (this.introspectedTable.getRules().generateUpdateByExampleWithoutBLOBs()) {
      AbstractXmlElementGenerator elementGenerator = new UpdateByExampleWithoutBLOBsElementGenerator();
      initializeAndExecuteGenerator(elementGenerator, parentElement);
    }
  }

  protected void addUpdateByPrimaryKeySelectiveElement(XmlElement parentElement)
  {
    if (this.introspectedTable.getRules().generateUpdateByPrimaryKeySelective()) {
      AbstractXmlElementGenerator elementGenerator = new UpdateByPrimaryKeySelectiveElementGenerator();
      initializeAndExecuteGenerator(elementGenerator, parentElement);
    }
  }

  protected void addUpdateByPrimaryKeyWithBLOBsElement(XmlElement parentElement)
  {
    if (this.introspectedTable.getRules().generateUpdateByPrimaryKeyWithBLOBs()) {
      AbstractXmlElementGenerator elementGenerator = new UpdateByPrimaryKeyWithBLOBsElementGenerator();
      initializeAndExecuteGenerator(elementGenerator, parentElement);
    }
  }

  protected void addUpdateByPrimaryKeyWithoutBLOBsElement(XmlElement parentElement)
  {
    if (this.introspectedTable.getRules()
      .generateUpdateByPrimaryKeyWithoutBLOBs()) {
      AbstractXmlElementGenerator elementGenerator = new UpdateByPrimaryKeyWithoutBLOBsElementGenerator(false);
      initializeAndExecuteGenerator(elementGenerator, parentElement);
    }
  }

  protected void initializeAndExecuteGenerator(AbstractXmlElementGenerator elementGenerator, XmlElement parentElement)
  {
    elementGenerator.setContext(this.context);
    elementGenerator.setIntrospectedTable(this.introspectedTable);
    elementGenerator.setProgressCallback(this.progressCallback);
    elementGenerator.setWarnings(this.warnings);
    elementGenerator.addElements(parentElement);
  }

  public Document getDocument()
  {
    Document document = new Document(
      "-//mybatis.org//DTD Mapper 3.0//EN", 
      "http://mybatis.org/dtd/mybatis-3-mapper.dtd");
    document.setRootElement(getSqlMapElement());

    if (!this.context.getPlugins().sqlMapDocumentGenerated(document, 
      this.introspectedTable)) {
      document = null;
    }

    return document;
  }
}