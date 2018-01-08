package ht.msc.mybatis.plugin;

public abstract interface Rules
{
  public abstract boolean generateInsert();

  public abstract boolean generateInsertSelective();

  public abstract FullyQualifiedJavaType calculateAllFieldsClass();

  public abstract boolean generateUpdateByPrimaryKeyWithoutBLOBs();

  public abstract boolean generateUpdateByPrimaryKeyWithBLOBs();

  public abstract boolean generateUpdateByPrimaryKeySelective();

  public abstract boolean generateDeleteByPrimaryKey();

  public abstract boolean generateDeleteByExample();

  public abstract boolean generateBaseResultMap();

  public abstract boolean generateResultMapWithBLOBs();

  public abstract boolean generateSQLExampleWhereClause();

  public abstract boolean generateMyBatis3UpdateByExampleWhereClause();

  public abstract boolean generateBaseColumnList();

  public abstract boolean generateBlobColumnList();

  public abstract boolean generateSelectByPrimaryKey();

  public abstract boolean generateSelectCount();

  public abstract boolean generateSelectByPage();

  public abstract boolean generateSelectByExampleWithoutBLOBs();

  public abstract boolean generateSelectByExampleWithBLOBs();

  public abstract boolean generateExampleClass();

  public abstract boolean generateCountByExample();

  public abstract boolean generateUpdateByExampleSelective();

  public abstract boolean generateUpdateByExampleWithoutBLOBs();

  public abstract boolean generateUpdateByExampleWithBLOBs();

  public abstract boolean generatePrimaryKeyClass();

  public abstract boolean generateBaseRecordClass();

  public abstract boolean generateRecordWithBLOBsClass();

  public abstract boolean generateJavaClient();

  public abstract IntrospectedTable getIntrospectedTable();
}