package ht.msc.mybatis.plugin;


public class SpringDAOTemplate extends AbstractDAOTemplate
{
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
    setDeleteMethodTemplate("getSqlMapClientTemplate().delete(\"{0}.{1}\", {2});");
  }

  protected void configureInsertMethodTemplate()
  {
    setInsertMethodTemplate("getSqlMapClientTemplate().insert(\"{0}.{1}\", {2});");
  }

  protected void configureQueryForListMethodTemplate()
  {
    setQueryForListMethodTemplate("getSqlMapClientTemplate().queryForList(\"{0}.{1}\", {2});");
  }

  protected void configureQueryForObjectMethodTemplate()
  {
    setQueryForObjectMethodTemplate("getSqlMapClientTemplate().queryForObject(\"{0}.{1}\", {2});");
  }

  protected void configureSuperClass()
  {
    setSuperClass(new FullyQualifiedJavaType(
      "org.springframework.orm.ibatis.support.SqlMapClientDaoSupport"));
  }

  protected void configureUpdateMethodTemplate()
  {
    setUpdateMethodTemplate("getSqlMapClientTemplate().update(\"{0}.{1}\", {2});");
  }
}