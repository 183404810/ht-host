package ht.msc.mybatis.plugin;

public abstract interface XmlFormatter
{
  public abstract void setContext(Context paramContext);

  public abstract String getFormattedContent(Document paramDocument);
}