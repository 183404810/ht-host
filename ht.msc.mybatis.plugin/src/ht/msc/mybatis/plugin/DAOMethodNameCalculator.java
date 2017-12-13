package ht.msc.mybatis.plugin;

public abstract interface DAOMethodNameCalculator
{
  public abstract String getInsertMethodName(IntrospectedTable paramIntrospectedTable);

  public abstract String getInsertSelectiveMethodName(IntrospectedTable paramIntrospectedTable);

  public abstract String getUpdateByPrimaryKeyWithoutBLOBsMethodName(IntrospectedTable paramIntrospectedTable);

  public abstract String getUpdateByPrimaryKeyWithBLOBsMethodName(IntrospectedTable paramIntrospectedTable);

  public abstract String getUpdateByPrimaryKeySelectiveMethodName(IntrospectedTable paramIntrospectedTable);

  public abstract String getSelectByPrimaryKeyMethodName(IntrospectedTable paramIntrospectedTable);

  public abstract String getSelectByExampleWithoutBLOBsMethodName(IntrospectedTable paramIntrospectedTable);

  public abstract String getSelectByExampleWithBLOBsMethodName(IntrospectedTable paramIntrospectedTable);

  public abstract String getDeleteByPrimaryKeyMethodName(IntrospectedTable paramIntrospectedTable);

  public abstract String getDeleteByExampleMethodName(IntrospectedTable paramIntrospectedTable);

  public abstract String getCountByExampleMethodName(IntrospectedTable paramIntrospectedTable);

  public abstract String getUpdateByExampleSelectiveMethodName(IntrospectedTable paramIntrospectedTable);

  public abstract String getUpdateByExampleWithBLOBsMethodName(IntrospectedTable paramIntrospectedTable);

  public abstract String getUpdateByExampleWithoutBLOBsMethodName(IntrospectedTable paramIntrospectedTable);
}