package ht.msc.mybatis.plugin;

import java.util.List;

public abstract class AbstractGenerator
{
  protected Context context;
  protected IntrospectedTable introspectedTable;
  protected List<String> warnings;
  protected ProgressCallback progressCallback;

  public Context getContext()
  {
    return this.context;
  }

  public void setContext(Context context) {
    this.context = context;
  }

  public IntrospectedTable getIntrospectedTable() {
    return this.introspectedTable;
  }

  public void setIntrospectedTable(IntrospectedTable introspectedTable) {
    this.introspectedTable = introspectedTable;
  }

  public List<String> getWarnings() {
    return this.warnings;
  }

  public void setWarnings(List<String> warnings) {
    this.warnings = warnings;
  }

  public ProgressCallback getProgressCallback() {
    return this.progressCallback;
  }

  public void setProgressCallback(ProgressCallback progressCallback) {
    this.progressCallback = progressCallback;
  }
}