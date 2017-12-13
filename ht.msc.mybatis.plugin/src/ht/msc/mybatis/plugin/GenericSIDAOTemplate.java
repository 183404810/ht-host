package ht.msc.mybatis.plugin;


public class GenericSIDAOTemplate extends AbstractDAOTemplate
{
  private FullyQualifiedJavaType sqlMapClientType = new FullyQualifiedJavaType(
    "com.ibatis.sqlmap.client.SqlMapClient");

  protected void configureCheckedExceptions()
  {
    addCheckedException(new FullyQualifiedJavaType("java.sql.SQLException"));
  }

  protected void configureConstructorTemplate()
  {
    Method method = new Method();
    method.setConstructor(true);
    method.setVisibility(JavaVisibility.PUBLIC);
    method.addBodyLine("super();");
    setConstructorTemplate(method);
  }

  protected void configureDeleteMethodTemplate()
  {
    setDeleteMethodTemplate("sqlMapClient.delete(\"{0}.{1}\", {2});");
  }

  protected void configureFields()
  {
    Field field = new Field();
    field.setVisibility(JavaVisibility.PRIVATE);
    field.setType(this.sqlMapClientType);
    field.setName("sqlMapClient");
    addField(field);
  }

  protected void configureImplementationImports()
  {
    addImplementationImport(this.sqlMapClientType);
  }

  protected void configureInsertMethodTemplate()
  {
    setInsertMethodTemplate("sqlMapClient.insert(\"{0}.{1}\", {2});");
  }

  protected void configureMethods()
  {
    Method method = new Method();
    method.setVisibility(JavaVisibility.PUBLIC);
    method.setName("setSqlMapClient");
    method.addParameter(new Parameter(this.sqlMapClientType, "sqlMapClient"));
    method.addBodyLine("this.sqlMapClient = sqlMapClient;");
    addMethod(method);

    method = new Method();
    method.setVisibility(JavaVisibility.PUBLIC);
    method.setName("getSqlMapClient");
    method.setReturnType(this.sqlMapClientType);
    method.addBodyLine("return sqlMapClient;");
    addMethod(method);
  }

  protected void configureQueryForListMethodTemplate()
  {
    setQueryForListMethodTemplate("sqlMapClient.queryForList(\"{0}.{1}\", {2});");
  }

  protected void configureQueryForObjectMethodTemplate()
  {
    setQueryForObjectMethodTemplate("sqlMapClient.queryForObject(\"{0}.{1}\", {2});");
  }

  protected void configureUpdateMethodTemplate()
  {
    setUpdateMethodTemplate("sqlMapClient.update(\"{0}.{1}\", {2});");
  }
}