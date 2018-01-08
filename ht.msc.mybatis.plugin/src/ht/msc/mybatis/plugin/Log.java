package ht.msc.mybatis.plugin;

public abstract interface Log
{
  public abstract boolean isDebugEnabled();

  public abstract void error(String paramString, Throwable paramThrowable);

  public abstract void error(String paramString);

  public abstract void debug(String paramString);

  public abstract void warn(String paramString);
}