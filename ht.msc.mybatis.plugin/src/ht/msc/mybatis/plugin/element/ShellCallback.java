package ht.msc.mybatis.plugin.element;

import java.io.File;

public abstract interface ShellCallback
{
  public abstract File getDirectory(String paramString1, String paramString2)
    throws ShellException;

  public abstract String mergeJavaFile(String paramString1, String paramString2, String[] paramArrayOfString, String paramString3)
    throws ShellException;

  public abstract void refreshProject(String paramString);

  public abstract boolean isMergeSupported();

  public abstract boolean isOverwriteEnabled();
}