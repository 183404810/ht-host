package ht.msc.mybatis.plugin.element;

import ht.msc.mybatis.plugin.Activator;
import ht.msc.mybatis.plugin.CodeLayoutEnum;
import ht.msc.mybatis.plugin.Configuration;
import ht.msc.mybatis.plugin.ConfigurationParser;
import ht.msc.mybatis.plugin.InvalidConfigurationException;
import ht.msc.mybatis.plugin.PluginAction;
import ht.msc.mybatis.plugin.XMLParserException;

import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.xml.sax.InputSource;


public class RunGeneratorThread
  implements IWorkspaceRunnable
{
  private IFile inputFile;
  private List<String> warnings;
  private ClassLoader oldClassLoader;
  private List<String> tableList;
  private Map<CodeLayoutEnum, Boolean> codeLayout;
  private String codeVersion;

  public RunGeneratorThread(IFile inputFile, List<String> warnings, List<String> tableList, Map<CodeLayoutEnum, Boolean> codeLayout, String codeVersion)
  {
    this.inputFile = inputFile;
    this.warnings = warnings;
    this.tableList = tableList;
    this.codeLayout = codeLayout;
    this.codeVersion = codeVersion;
  }

  public void run(IProgressMonitor monitor)
    throws CoreException
  {
    SubMonitor subMonitor = SubMonitor.convert(monitor, 1000);
    subMonitor.beginTask("Generating MyBatis/iBATIS Artifacts:", 1000);

    setClassLoader();
    try
    {
      subMonitor.subTask("Parsing Configuration");

      ConfigurationParser cp = new ConfigurationParser(
        this.warnings);

      Configuration config = cp.parseConfiguration(new InputSource(new FileReader(this.inputFile.getLocation().toFile())), this.tableList, this.codeLayout, this.codeVersion);

      if ((config.getClassPathEntries().size() == 0) && (PluginAction.cacheDriverPathMap != null) && (PluginAction.cacheDriverPathMap.size() > 0)) {
        for (IPath path : PluginAction.cacheDriverPathMap.values()) {
          config.getClassPathEntries().add(path.toString());
        }
      }

      subMonitor.worked(50);

      MyBatisGenerator mybatisGenerator = new MyBatisGenerator(config, new EclipseShellCallback(), this.warnings);
      monitor.subTask("Generating Files from Database Tables");
      SubMonitor spm = subMonitor.newChild(950);
      mybatisGenerator.generate(new EclipseProgressCallback(spm));
    }
    catch (InterruptedException localInterruptedException) {
      throw new OperationCanceledException();
    } catch (SQLException e) {
      Status status = new Status(4, "org.mybatis.generator.eclipse.ui", 
        4, e.getMessage(), e);
      Activator.getDefault().getLog().log(status);
      throw new CoreException(status);
    } catch (IOException e) {
      Status status = new Status(4, "org.mybatis.generator.eclipse.ui", 4, e.getMessage(), e);
      Activator.getDefault().getLog().log(status);
      throw new CoreException(status);
    } catch (XMLParserException e) {
      List errors = e.getErrors();
      MultiStatus multiStatus = new MultiStatus("org.mybatis.generator.eclipse.ui", 4, 
        "XML Parser Errors\n  See Details for more Information", 
        null);

      Iterator iter = errors.iterator();
      while (iter.hasNext()) {
        Status message = new Status(4, "org.mybatis.generator.eclipse.ui", 4, (String)iter.next(), 
          null);

        multiStatus.add(message);
      }
      throw new CoreException(multiStatus);
    } catch (InvalidConfigurationException e) {
      List errors = e.getErrors();

      MultiStatus multiStatus = new MultiStatus(
        "org.mybatis.generator.eclipse.ui", 
        4, 
        "Invalid Configuration\n  See Details for more Information", 
        null);

      Iterator iter = errors.iterator();
      while (iter.hasNext()) {
        Status message = new Status(4, "org.mybatis.generator.eclipse.ui", 4, (String)iter.next(), 
          null);

        multiStatus.add(message);
      }
      throw new CoreException(multiStatus);
    } finally {
      monitor.done();
      restoreClassLoader();
    }
  }

  private void setClassLoader()
  {
    IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
    IJavaProject javaProject = getJavaProject();
    try
    {
      if (javaProject != null) {
        List entries = new ArrayList();
        IPath path = javaProject.getOutputLocation();
        IResource iResource = root.findMember(path);
        path = iResource.getLocation();
        path = path.addTrailingSeparator();
        entries.add(path.toFile().toURL());

        IClasspathEntry[] cpEntries = javaProject.getRawClasspath();
        for (IClasspathEntry cpEntry : cpEntries) {
          switch (cpEntry.getEntryKind()) {
          case 3:
            path = cpEntry.getOutputLocation();
            if (path != null) {
              iResource = root.findMember(path);
              path = iResource.getLocation();
              path = path.addTrailingSeparator();
              entries.add(path.toFile().toURL());
            }
            break;
          case 1:
            iResource = root.findMember(cpEntry.getPath());
            if (iResource == null)
            {
              path = cpEntry.getPath();
            }
            else path = iResource.getLocation();

            entries.add(path.toFile().toURL());
          case 2:
          }
        }

        ClassLoader oldCl = Thread.currentThread().getContextClassLoader();
        URL[] entryArray = new URL[entries.size()];
        entries.toArray(entryArray);
        Object newCl = new URLClassLoader(entryArray, oldCl);
        Thread.currentThread().setContextClassLoader((ClassLoader)newCl);
        this.oldClassLoader = oldCl;
      }
    }
    catch (Exception localException)
    {
    }
  }

  private void restoreClassLoader()
  {
    if (this.oldClassLoader != null)
      Thread.currentThread().setContextClassLoader(this.oldClassLoader);
  }

  private IJavaProject getJavaProject()
  {
    IJavaProject answer = null;
    IProject project = this.inputFile.getProject();
    try {
      if (project.hasNature("org.eclipse.jdt.core.javanature")) {
        answer = JavaCore.create(project);
      }
    }
    catch (CoreException localCoreException)
    {
    }

    return answer;
  }
}