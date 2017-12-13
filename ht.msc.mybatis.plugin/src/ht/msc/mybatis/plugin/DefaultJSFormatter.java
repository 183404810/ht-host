package ht.msc.mybatis.plugin;


public class DefaultJSFormatter
  implements JavaFormatter
{
  protected Context context;

  public String getFormattedContent(CompilationUnit compilationUnit)
  {
    return compilationUnit.getFormattedContent();
  }

  public void setContext(Context context) {
    this.context = context;
  }
}