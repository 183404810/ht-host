package ht.msc.mybatis.plugin;

public abstract class GeneratedFile
{
  protected String targetProject;

  public GeneratedFile(String targetProject)
  {
    this.targetProject = targetProject;
  }

  public abstract String getFormattedContent();

  public abstract String getFileName();

  public String getTargetProject()
  {
    return this.targetProject;
  }

  public abstract String getTargetPackage();

  public String toString()
  {
    return getFormattedContent();
  }

  public abstract boolean isMergeable();
}