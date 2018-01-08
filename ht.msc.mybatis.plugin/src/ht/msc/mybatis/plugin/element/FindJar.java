package ht.msc.mybatis.plugin.element;

import ht.msc.mybatis.plugin.Tools;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.core.JarPackageFragmentRoot;
import org.eclipse.jdt.internal.core.PackageFragment;


public class FindJar
{
  private static final String DOT = ".";

  public static List<IJavaProject> findProject(IWorkspace workspace)
  {
    IProject[] projects = workspace.getRoot().getProjects();
    if (projects != null) {
      List jProjects = new ArrayList(projects.length);
      for (IProject p : projects)
      {
        IJavaProject jProject = JavaCore.create(p);

        if ((jProject != null) && (jProject.exists()))
        {
          jProjects.add(jProject);
        }
      }
      return jProjects;
    }
    return null;
  }

  public static List<JarPackageFragmentRoot> findAllJar(IJavaProject jProject)
  {
    try
    {
      IPackageFragmentRoot[] jars = jProject.getAllPackageFragmentRoots();
      if (jars != null) {
        List jarList = new ArrayList();
        for (IPackageFragmentRoot ele : jars)
        {
          if ((ele instanceof JarPackageFragmentRoot))
          {
            JarPackageFragmentRoot jarFile = (JarPackageFragmentRoot)ele;

            IClasspathEntry rawClasspathEntry = jarFile.getRawClasspathEntry();
            IClasspathContainer classpathContainer = JavaCore.getClasspathContainer(
              rawClasspathEntry.getPath(), jProject);

            if (classpathContainer.getKind() != 3)
            {
              jarList.add(jarFile);
            }
          }
        }
        return jarList;
      }
    } catch (JavaModelException e) {
      e.printStackTrace();
    }
    return null;
  }

  public static JarPackageFragmentRoot findDrvierJar(IWorkspace workspace, String driverClassName)
  {
    List<IJavaProject> projects = findProject(workspace);
    if (projects != null) {
      List<JarPackageFragmentRoot> jarList = new ArrayList();
      for (IJavaProject iJavaProject : projects) {
        jarList.addAll(findAllJar(iJavaProject));
      }
      if (jarList.size() > 0)
      {
        boolean flag = false;
        for (JarPackageFragmentRoot jar : jarList) {
          flag = existsClassFile(jar, driverClassName);
          if (flag) {
            String msg = MessageFormat.format("已自动找到驱动,驱动包路径:{0}", new Object[] { jar.getPath() });
            Tools.writeLine(msg);
            return jar;
          }
        }
      }
    }
    return null;
  }

  private static boolean existsClassFile(JarPackageFragmentRoot jarFile, String driverClassName) {
    if (("".equals(driverClassName)) || (driverClassName == null))
      return false;
    try {
      IJavaElement[] children = jarFile.getChildren();
      if (children != null) {
        String packageName = driverClassName.substring(0, driverClassName.lastIndexOf("."));

        for (IJavaElement ele : children) {
          if ((ele instanceof PackageFragment))
          {
            if (ele.getElementName().equals(packageName)) {
              IJavaElement[] classes = ((PackageFragment)ele).getChildren();

              for (IJavaElement cls : classes)
                if ((cls instanceof IClassFile))
                {
                  String cName = cls.getElementName();
                  String className = ele.getElementName() + "." + cName.substring(0, cName.indexOf("."));
                  if (className.equals(driverClassName))
                    return true;
                }
            }
          }
        }
      }
    }
    catch (JavaModelException e)
    {
      e.printStackTrace();
    }
    return false;
  }
}