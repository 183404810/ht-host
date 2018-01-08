package ht.msc.mybatis.plugin;

import java.util.List;
import java.util.Properties;

public abstract interface JavaTypeResolver
{
  public abstract void addConfigurationProperties(Properties paramProperties);

  public abstract void setContext(Context paramContext);

  public abstract void setWarnings(List<String> paramList);

  public abstract FullyQualifiedJavaType calculateJavaType(IntrospectedColumn paramIntrospectedColumn);

  public abstract String calculateJdbcTypeName(IntrospectedColumn paramIntrospectedColumn);
}