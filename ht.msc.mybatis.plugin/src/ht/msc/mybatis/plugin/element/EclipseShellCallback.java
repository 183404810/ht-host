package ht.msc.mybatis.plugin.element;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;


public class EclipseShellCallback
  implements ShellCallback
{
  private Map<String, IJavaProject> projects;
  private Map<String, IFolder> folders;
  private Map<String, IPackageFragmentRoot> sourceFolders;

  public EclipseShellCallback()
  {
    this.projects = new HashMap();
    this.folders = new HashMap();
    this.sourceFolders = new HashMap();
  }

  public File getDirectory(String targetProject, String targetPackage)
    throws ShellException
  {
    if ((targetProject.startsWith("/")) || (targetProject.startsWith("\\"))) {
      StringBuffer sb = new StringBuffer();
      sb.append("targetProject ");
      sb.append(targetProject);
      sb.append(" is invalid - it cannot start with / or \\");

      throw new ShellException(sb.toString());
    }

    IFolder folder = getFolder(targetProject, targetPackage);

    return folder.getRawLocation().toFile();
  }

  public void refreshProject(String project)
  {
    try
    {
      IPackageFragmentRoot root = getSourceFolder(project);
      root.getCorrespondingResource().refreshLocal(
        2, null);
    }
    catch (Exception localException)
    {
    }
  }

  private IJavaProject getJavaProject(String javaProjectName) throws ShellException
  {
    IJavaProject javaProject = (IJavaProject)this.projects.get(javaProjectName);
    if (javaProject == null) {
      IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
      IProject project = root.getProject(javaProjectName);
      boolean isJavaProject;
      if (project.exists())
      {
        try {
          isJavaProject = project.hasNature("org.eclipse.jdt.core.javanature");
        }
        catch (CoreException e)
        {
          throw new ShellException(e.getStatus().getMessage(), e);
        }
        if (isJavaProject) {
          javaProject = JavaCore.create(project);
        } else {
          StringBuffer sb = new StringBuffer();
          sb.append("Project ");
          sb.append(javaProjectName);
          sb.append(" is not a Java project");

          throw new ShellException(sb.toString());
        }
      } else {
        StringBuffer sb = new StringBuffer();
        sb.append("Project ");
        sb.append(javaProjectName);
        sb.append(" does not exist");

        throw new ShellException(sb.toString());
      }

      this.projects.put(javaProjectName, javaProject);
    }

    return javaProject;
  }

  private IFolder getFolder(String targetProject, String targetPackage) throws ShellException
  {
    String key = targetProject + targetPackage;
    IFolder folder = (IFolder)this.folders.get(key);
    if (folder == null) {
      IPackageFragmentRoot root = getSourceFolder(targetProject);
      IPackageFragment packageFragment = getPackage(root, targetPackage);
      try
      {
        folder = (IFolder)packageFragment.getCorrespondingResource();

        this.folders.put(key, folder);
      } catch (CoreException e) {
        throw new ShellException(e.getStatus().getMessage(), e);
      }
    }

    return folder;
  }

  private IPackageFragmentRoot getFirstSourceFolder(IJavaProject javaProject)
    throws ShellException
  {
	  IPackageFragmentRoot[] roots;
    try
    {
      roots = javaProject.getPackageFragmentRoots();
    }
    catch (CoreException e)
    {
      
      throw new ShellException(e.getStatus().getMessage(), e);
    }
    IPackageFragmentRoot srcFolder = null;
    for (int i = 0; i < roots.length; i++) {
      if ((!roots[i].isArchive()) && (!roots[i].isReadOnly()) && 
        (!roots[i].isExternal()))
      {
        srcFolder = roots[i];
        break;
      }
    }

    if (srcFolder == null) {
      StringBuffer sb = new StringBuffer();
      sb.append("Cannot find source folder for project ");
      sb.append(javaProject.getElementName());

      throw new ShellException(sb.toString());
    }

    return srcFolder;
  }

  private IPackageFragmentRoot getSpecificSourceFolder(IJavaProject javaProject, String targetProject)
    throws ShellException
  {
    try
    {
      Path path = new Path("/" + targetProject);
      IPackageFragmentRoot pfr = javaProject
        .findPackageFragmentRoot(path);
      if (pfr == null) {
        StringBuffer sb = new StringBuffer();
        sb.append("Cannot find source folder ");
        sb.append(targetProject);

        throw new ShellException(sb.toString());
      }

      return pfr;
    } catch (CoreException e) {
      throw new ShellException(e.getStatus().getMessage(), e);
    }
  }

  private IPackageFragment getPackage(IPackageFragmentRoot srcFolder, String packageName)
    throws ShellException
  {
    IPackageFragment fragment = srcFolder.getPackageFragment(packageName);
    try
    {
      if (!fragment.exists()) {
        fragment = srcFolder.createPackageFragment(packageName, true, 
          null);
      }

      fragment.getCorrespondingResource().refreshLocal(
        1, null);
    } catch (CoreException e) {
      throw new ShellException(e.getStatus().getMessage(), e);
    }

    return fragment;
  }

  public boolean isMergeSupported()
  {
    return false;
  }

  private IPackageFragmentRoot getSourceFolder(String targetProject) throws ShellException
  {
    IPackageFragmentRoot answer = (IPackageFragmentRoot)this.sourceFolders.get(targetProject);
    if (answer == null)
    {
      int index = targetProject.indexOf('/');
      if (index == -1) {
        index = targetProject.indexOf('\\');
      }

      if (index == -1)
      {
        IJavaProject javaProject = getJavaProject(targetProject);
        answer = getFirstSourceFolder(javaProject);
      } else {
        IJavaProject javaProject = getJavaProject(targetProject
          .substring(0, index));
        answer = getSpecificSourceFolder(javaProject, targetProject);
      }

      this.sourceFolders.put(targetProject, answer);
    }

    return answer;
  }

  public boolean isOverwriteEnabled() {
    return true;
  }

  public String mergeJavaFile(String newFileSource, String existingFileFullPath, String[] javadocTags, String fileEncoding)
    throws ShellException
  {
    JavaFileMerger merger = new JavaFileMerger(newFileSource, existingFileFullPath, javadocTags, fileEncoding);
    return merger.getMergedSource();
  }
}