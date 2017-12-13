package ht.msc.mybatis.plugin.element;

import org.eclipse.core.resources.IFile;

public class ConfigurationFileAdapter
{
  private IFile baseFile;

  public ConfigurationFileAdapter(IFile baseFile)
  {
    this.baseFile = baseFile;
  }

  public IFile getBaseFile() {
    return this.baseFile;
  }
}