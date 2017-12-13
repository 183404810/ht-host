package ht.msc.mybatis.plugin;


public abstract interface AbstractLogFactory
{
  public abstract Log getLog(Class<?> paramClass);
}