package ht.msc.mybatis.plugin;


public class IbatisDAOTemplate extends AbstractDAOTemplate
{
  private FullyQualifiedJavaType fqjt = new FullyQualifiedJavaType(
    "com.ibatis.dao.client.DaoManager");

  protected void configureConstructorTemplate()
  {
    Method method = new Method();
    method.setConstructor(true);
    method.setVisibility(JavaVisibility.PUBLIC);
    method.addParameter(new Parameter(this.fqjt, "daoManager"));
    method.addBodyLine("super(daoManager);");
    setConstructorTemplate(method);
  }

  protected void configureDeleteMethodTemplate()
  {
    setDeleteMethodTemplate("delete(\"{0}.{1}\", {2});");
  }

  protected void configureImplementationImports()
  {
    addImplementationImport(this.fqjt);
  }

  protected void configureInsertMethodTemplate()
  {
    setInsertMethodTemplate("insert(\"{0}.{1}\", {2});");
  }

  protected void configureQueryForListMethodTemplate()
  {
    setQueryForListMethodTemplate("queryForList(\"{0}.{1}\", {2});");
  }

  protected void configureQueryForObjectMethodTemplate()
  {
    setQueryForObjectMethodTemplate("queryForObject(\"{0}.{1}\", {2});");
  }

  protected void configureSuperClass()
  {
    setSuperClass(new FullyQualifiedJavaType(
      "com.ibatis.dao.client.template.SqlMapDaoTemplate"));
  }

  protected void configureUpdateMethodTemplate()
  {
    setUpdateMethodTemplate("update(\"{0}.{1}\", {2});");
  }
}