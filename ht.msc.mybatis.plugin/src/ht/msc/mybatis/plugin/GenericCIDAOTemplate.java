package ht.msc.mybatis.plugin;


public class GenericCIDAOTemplate extends AbstractDAOTemplate
{
  private FullyQualifiedJavaType sqlMapClientType = new FullyQualifiedJavaType(
    "com.ibatis.sqlmap.client.SqlMapClient");

  protected void configureCheckedExceptions()
  {
    addCheckedException(new FullyQualifiedJavaType("java.sql.SQLException"));
  }

  protected void configureConstructorTemplate()
  {
    Method constructor = new Method();
    constructor.setConstructor(true);
    constructor.setVisibility(JavaVisibility.PUBLIC);
    constructor
      .addParameter(new Parameter(this.sqlMapClientType, "sqlMapClient"));
    constructor.addBodyLine("super();");
    constructor.addBodyLine("this.sqlMapClient = sqlMapClient;");
    setConstructorTemplate(constructor);
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