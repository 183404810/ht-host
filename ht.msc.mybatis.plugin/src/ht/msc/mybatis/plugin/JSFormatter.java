package ht.msc.mybatis.plugin;

public abstract interface JSFormatter
{
  public abstract void setContext(Context paramContext);

  public abstract String getFormattedContent(CompilationUnit paramCompilationUnit);
}