package ht.msc.mybatis.plugin.element;

import ht.msc.mybatis.plugin.Messages;

import java.io.File;
import java.util.StringTokenizer;


public class DefaultShellCallback
  implements ShellCallback
{
  private boolean overwrite;

  public DefaultShellCallback(boolean overwrite)
  {
    this.overwrite = overwrite;
  }

  public File getDirectory(String targetProject, String targetPackage)
    throws ShellException
  {
    File project = new File(targetProject);
    if (!project.isDirectory()) {
      throw new ShellException(Messages.getString("Warning.9", 
        targetProject));
    }

    StringBuilder sb = new StringBuilder();
    StringTokenizer st = new StringTokenizer(targetPackage, ".");
    while (st.hasMoreTokens()) {
      sb.append(st.nextToken());
      sb.append(File.separatorChar);
    }

    File directory = new File(project, sb.toString());
    if (!directory.isDirectory()) {
      boolean rc = directory.mkdirs();
      if (!rc) {
        throw new ShellException(Messages.getString("Warning.10", 
          directory.getAbsolutePath()));
      }
    }

    return directory;
  }

  public void refreshProject(String project)
  {
  }

  public boolean isMergeSupported() {
    return false;
  }

  public boolean isOverwriteEnabled() {
    return this.overwrite;
  }

  public String mergeJavaFile(String newFileSource, String existingFileFullPath, String[] javadocTags, String fileEncoding)
    throws ShellException
  {
    throw new UnsupportedOperationException();
  }
}