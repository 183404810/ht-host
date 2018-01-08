package ht.msc.mybatis.plugin;


public class DefaultDAOMethodNameCalculator
  implements DAOMethodNameCalculator
{
  public String getInsertMethodName(IntrospectedTable introspectedTable)
  {
    return "insert";
  }

  public String getUpdateByPrimaryKeyWithoutBLOBsMethodName(IntrospectedTable introspectedTable)
  {
    Rules rules = introspectedTable.getRules();

    if (!rules.generateUpdateByPrimaryKeyWithBLOBs())
      return "updateByPrimaryKey";
    if (rules.generateRecordWithBLOBsClass()) {
      return "updateByPrimaryKey";
    }
    return "updateByPrimaryKeyWithoutBLOBs";
  }

  public String getUpdateByPrimaryKeyWithBLOBsMethodName(IntrospectedTable introspectedTable)
  {
    Rules rules = introspectedTable.getRules();

    if (!rules.generateUpdateByPrimaryKeyWithoutBLOBs())
      return "updateByPrimaryKey";
    if (rules.generateRecordWithBLOBsClass()) {
      return "updateByPrimaryKey";
    }
    return "updateByPrimaryKeyWithBLOBs";
  }

  public String getDeleteByExampleMethodName(IntrospectedTable introspectedTable)
  {
    return "deleteByExample";
  }

  public String getDeleteByPrimaryKeyMethodName(IntrospectedTable introspectedTable)
  {
    return "deleteByPrimaryKey";
  }

  public String getSelectByExampleWithoutBLOBsMethodName(IntrospectedTable introspectedTable)
  {
    Rules rules = introspectedTable.getRules();

    if (!rules.generateSelectByExampleWithBLOBs()) {
      return "selectByExample";
    }
    return "selectByExampleWithoutBLOBs";
  }

  public String getSelectByExampleWithBLOBsMethodName(IntrospectedTable introspectedTable)
  {
    Rules rules = introspectedTable.getRules();

    if (!rules.generateSelectByExampleWithoutBLOBs()) {
      return "selectByExample";
    }
    return "selectByExampleWithBLOBs";
  }

  public String getSelectByPrimaryKeyMethodName(IntrospectedTable introspectedTable)
  {
    return "selectByPrimaryKey";
  }

  public String getUpdateByPrimaryKeySelectiveMethodName(IntrospectedTable introspectedTable)
  {
    return "updateByPrimaryKeySelective";
  }

  public String getCountByExampleMethodName(IntrospectedTable introspectedTable)
  {
    return "countByExample";
  }

  public String getUpdateByExampleSelectiveMethodName(IntrospectedTable introspectedTable)
  {
    return "updateByExampleSelective";
  }

  public String getUpdateByExampleWithBLOBsMethodName(IntrospectedTable introspectedTable)
  {
    Rules rules = introspectedTable.getRules();

    if (!rules.generateUpdateByExampleWithoutBLOBs())
      return "updateByExample";
    if (rules.generateRecordWithBLOBsClass()) {
      return "updateByExample";
    }
    return "updateByExampleWithBLOBs";
  }

  public String getUpdateByExampleWithoutBLOBsMethodName(IntrospectedTable introspectedTable)
  {
    Rules rules = introspectedTable.getRules();

    if (!rules.generateUpdateByExampleWithBLOBs())
      return "updateByExample";
    if (rules.generateRecordWithBLOBsClass()) {
      return "updateByExample";
    }
    return "updateByExampleWithoutBLOBs";
  }

  public String getInsertSelectiveMethodName(IntrospectedTable introspectedTable)
  {
    return "insertSelective";
  }
}