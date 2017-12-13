package ht.msc.mybatis.plugin.element;

import ht.msc.mybatis.plugin.Configuration;
import ht.msc.mybatis.plugin.Context;
import ht.msc.mybatis.plugin.GeneratedJSFile;
import ht.msc.mybatis.plugin.GeneratedJavaFile;
import ht.msc.mybatis.plugin.GeneratedXmlFile;
import ht.msc.mybatis.plugin.InvalidConfigurationException;
import ht.msc.mybatis.plugin.Messages;
import ht.msc.mybatis.plugin.ObjectFactory;
import ht.msc.mybatis.plugin.ProgressCallback;
import ht.msc.mybatis.plugin.Tools;
import ht.msc.mybatis.plugin.YouGouSqlMapConfigConfiguration;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;


public class MyBatisGenerator
{
  private Configuration configuration;
  private ShellCallback shellCallback;
  private List<GeneratedJavaFile> generatedJavaFiles;
  private List<GeneratedXmlFile> generatedXmlFiles;
  private List<GeneratedJSFile> generatedJSFiles;
  private List<String> warnings;
  private Set<String> projects;

  public MyBatisGenerator(Configuration configuration, ShellCallback shellCallback, List<String> warnings)
    throws InvalidConfigurationException
  {
    if (configuration == null) {
      throw new IllegalArgumentException(Messages.getString("RuntimeError.2"));
    }
    this.configuration = configuration;

    if (shellCallback == null)
      this.shellCallback = new DefaultShellCallback(true);
    else {
      this.shellCallback = shellCallback;
    }

    if (warnings == null)
      this.warnings = new ArrayList();
    else {
      this.warnings = warnings;
    }
    this.generatedJavaFiles = new ArrayList();
    this.generatedXmlFiles = new ArrayList();
    this.generatedJSFiles = new ArrayList();
    this.projects = new HashSet();

    this.configuration.validate();
  }

  public void generate(ProgressCallback callback)
    throws SQLException, IOException, InterruptedException
  {
    generate(callback, null, null);
  }

  public void generate(ProgressCallback callback, Set<String> contextIds)
    throws SQLException, IOException, InterruptedException
  {
    generate(callback, contextIds, null);
  }

  public void generate(ProgressCallback callback, Set<String> contextIds, Set<String> fullyQualifiedTableNames)
    throws SQLException, IOException, InterruptedException
  {
    if (callback == null) {
      callback = new NullProgressCallback();
    }

    this.generatedJavaFiles.clear();
    this.generatedXmlFiles.clear();
    this.generatedJSFiles.clear();
    List<Context> contextsToRun;
    if ((contextIds == null) || (contextIds.size() == 0)) {
      contextsToRun = this.configuration.getContexts();
    } else {
      contextsToRun = new ArrayList();
      for (Context context : this.configuration.getContexts()) {
        if (contextIds.contains(context.getId())) {
          contextsToRun.add(context);
        }
      }

    }

    if (this.configuration.getClassPathEntries().size() > 0) {
      ClassLoader classLoader = ClassloaderUtility.getCustomClassloader(this.configuration.getClassPathEntries());
      ObjectFactory.addExternalClassLoader(classLoader);
    }

    int totalSteps = 0;
    for (Context context : contextsToRun) {
      totalSteps += context.getIntrospectionSteps();
    }
    callback.introspectionStarted(totalSteps);

    for (Context context : contextsToRun) {
      context.introspectTables(callback, this.warnings, 
        fullyQualifiedTableNames);
    }

    totalSteps = 0;
    for (Context context : contextsToRun) {
      totalSteps += context.getGenerationSteps();
    }
    callback.generationStarted(totalSteps);

    for (Context context : contextsToRun) {
      context.generateFiles(callback, this.generatedJavaFiles, 
        this.generatedXmlFiles, this.generatedJSFiles, this.warnings);
    }

    callback.saveStarted(this.generatedXmlFiles.size() + 
      this.generatedJavaFiles.size() + this.generatedJSFiles.size());

    Shell shell = new Shell(new Display(), 16793600);
    for (GeneratedXmlFile gxf : this.generatedXmlFiles) {
      this.projects.add(gxf.getTargetProject());
      try
      {
        File directory = this.shellCallback.getDirectory(gxf
          .getTargetProject(), gxf.getTargetPackage());
        File targetFile = new File(directory, gxf.getFileName());
        String source;
        if (targetFile.exists())
        {
          boolean flag = MessageDialog.openConfirm(shell, "确认", "是否覆盖" + targetFile.getName() + "文件");
          if (!flag)
            continue;
          if (gxf.isMergeable()) {
            source = XmlFileMergerJaxp.getMergedSource(gxf, 
              targetFile);
          }
          else
          {
            if (this.shellCallback.isOverwriteEnabled()) {
              source = gxf.getFormattedContent();
            }
            else
            {
              source = gxf.getFormattedContent();
              targetFile = getUniqueFileName(directory, gxf
                .getFileName());
            }
          }
        }
        else {
          source = gxf.getFormattedContent();
        }

        try
        {
          YouGouSqlMapConfigConfiguration sqlconfig = Context.getYouGouSqlMapConfigConfiguration();
          if (sqlconfig != null)
          {
            File configTargetFile;
            try {
              File configDirectory = this.shellCallback.getDirectory(sqlconfig.getTargetProject(), sqlconfig.getTargetPackage());
              configTargetFile = new File(configDirectory, sqlconfig.getConfileFileName());
            }
            catch (Exception localException1)
            {
              File configDirectory = this.shellCallback.getDirectory(sqlconfig.getTargetProject(), "");
              configTargetFile = new File(configDirectory, sqlconfig.getTargetPackage() + "/" + sqlconfig.getConfileFileName());
            }

            if (configTargetFile.exists())
              MybatisConfigAddElement.appendGeneratorXml(configTargetFile.getAbsolutePath(), sqlconfig.getConfileFilePackagePath(), gxf.getFileName());
          }
        }
        catch (Exception e) {
          this.warnings.add(e.getMessage());
        }
      } catch (ShellException e) {
        this.warnings.add(e.getMessage());
      }
      String source = null;
      File targetFile = null;
      callback.checkCancel();
      callback.startTask(Messages.getString(
        "Progress.15", targetFile.getName()));
      writeFile(targetFile, source, "UTF-8");
    }

    for (GeneratedJavaFile gjf : this.generatedJavaFiles) {
      this.projects.add(gjf.getTargetProject());
      try
      {
        File directory = this.shellCallback.getDirectory(gjf
          .getTargetProject(), gjf.getTargetPackage());
        File targetFile = new File(directory, gjf.getFileName());
        String source;
        if (targetFile.exists())
        {
          boolean flag = MessageDialog.openConfirm(shell, "确认", "是否覆盖" + targetFile.getName() + "文件");
          if (!flag)
            continue;
          if (this.shellCallback.isMergeSupported()) {
            source = this.shellCallback.mergeJavaFile(gjf
              .getFormattedContent(), targetFile
              .getAbsolutePath(), 
              MergeConstants.OLD_ELEMENT_TAGS, 
              gjf.getFileEncoding());
          }
          else
          {
            if (this.shellCallback.isOverwriteEnabled()) {
              source = gjf.getFormattedContent();
            }
            else
            {
              source = gjf.getFormattedContent();
              targetFile = getUniqueFileName(directory, gjf
                .getFileName());
            }
          }
        }
        else {
          source = gjf.getFormattedContent();
        }

        callback.checkCancel();
        callback.startTask(Messages.getString(
          "Progress.15", targetFile.getName()));
        writeFile(targetFile, source, gjf.getFileEncoding());
      } catch (ShellException e) {
        this.warnings.add(e.getMessage());
      }

    }

    for (GeneratedJSFile gjf : this.generatedJSFiles)
    {
      this.projects.add(gjf.getTargetProject());
      try
      {
        String sourceFileName = gjf.getFileName();
        String modulePath = sourceFileName.replace("Model", "").replace(".js", "").toLowerCase();
        String fileCommentLines = ((String)gjf.getCompilationUnit().getFileCommentLines().get(0)).toLowerCase();

        File directory = this.shellCallback.getDirectory(gjf.getTargetProject(), gjf.getTargetPackage());
        File targetFile;
        if (fileCommentLines.equals("view_default")) {
          directory = getModuleDirectory(directory, modulePath);
          targetFile = new File(directory.toString() + "\\" + sourceFileName.replace("Model", ""));
        }
        else
        {
          if (fileCommentLines.equals("view_model")) {
            directory = getModuleDirectory(directory, modulePath);
            targetFile = new File(directory.toString() + "\\" + sourceFileName);
          }
          else
          {
            if (fileCommentLines.equals("view_controller")) {
              directory = getModuleDirectory(directory, modulePath);
              targetFile = new File(directory.toString() + "\\" + sourceFileName.replace("Model", "Controller"));
            }
            else
            {
              directory = getModuleDirectory(directory, "model");
              targetFile = new File(directory.toString() + "\\" + sourceFileName.replace("Model", ""));
            }
          }
        }
        Tools.writeLine("directory:" + directory + " :targetFile" + targetFile);
        String source;
        if (targetFile.exists()) {
          boolean flag = MessageDialog.openConfirm(shell, "确认", "是否覆盖" + targetFile.getName() + "文件");
          if (!flag)
            continue;
          source = ((String)gjf.getCompilationUnit().getJSFileContentLines().get(0)).toString();
        } else {
          source = ((String)gjf.getCompilationUnit().getJSFileContentLines().get(0)).toString();
        }

        callback.checkCancel();
        callback.startTask(Messages.getString("Progress.15", targetFile.getName()));

        writeFile(targetFile, source, gjf.getFileEncoding());
      } catch (ShellException e) {
        this.warnings.add(e.getMessage());
      }
    }

    for (String project : this.projects) {
      this.shellCallback.refreshProject(project);
    }

    callback.done();
  }

  private File getModuleDirectory(File directory, String modulePath)
  {
    File moduleDirectory = null;
    String jsMVCPath = directory.toString();
    jsMVCPath = jsMVCPath.subSequence(0, jsMVCPath.indexOf("src")) + "src\\main\\webapp\\resources\\app";
    try {
      if ("model".equals(modulePath))
      {
        moduleDirectory = new File(jsMVCPath + "\\model\\");
      }
      else {
        moduleDirectory = new File(jsMVCPath + "\\view\\" + modulePath);
        if ((!moduleDirectory.exists()) && (!moduleDirectory.isDirectory()))
          moduleDirectory.mkdir();
      }
    }
    catch (Exception e)
    {
      Tools.writeLine("getModuleDirectory error:" + e.getStackTrace());
    }

    return moduleDirectory;
  }

  private void writeFile(File file, String content, String fileEncoding)
    throws IOException
  {
    FileOutputStream fos = new FileOutputStream(file, false);
    OutputStreamWriter osw;
    if (fileEncoding == null)
      osw = new OutputStreamWriter(fos);
    else {
      osw = new OutputStreamWriter(fos, fileEncoding);
    }

    BufferedWriter bw = new BufferedWriter(osw);
    bw.write(content);
    bw.close();
  }

  private File getUniqueFileName(File directory, String fileName) {
    File answer = null;

    StringBuilder sb = new StringBuilder();
    for (int i = 1; i < 1000; i++) {
      sb.setLength(0);
      sb.append(fileName);
      sb.append('.');
      sb.append(i);

      File testFile = new File(directory, sb.toString());
      if (!testFile.exists()) {
        answer = testFile;
        break;
      }
    }

    if (answer == null) {
      throw new RuntimeException(Messages.getString(
        "RuntimeError.3", directory.getAbsolutePath()));
    }

    return answer;
  }
}