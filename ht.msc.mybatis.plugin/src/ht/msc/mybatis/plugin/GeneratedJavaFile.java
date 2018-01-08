package ht.msc.mybatis.plugin;

public class GeneratedJavaFile extends GeneratedFile
{
  private CompilationUnit compilationUnit;
  private String fileEncoding;
  private JavaFormatter javaFormatter;

  public GeneratedJavaFile(CompilationUnit compilationUnit, String targetProject, String fileEncoding, JavaFormatter javaFormatter)
  {
    super(targetProject);
    this.compilationUnit = compilationUnit;
    this.fileEncoding = fileEncoding;
    this.javaFormatter = javaFormatter;
  }

  public GeneratedJavaFile(CompilationUnit compilationUnit, String targetProject, JavaFormatter javaFormatter)
  {
    this(compilationUnit, targetProject, null, javaFormatter);
  }

  public String getFormattedContent()
  {
    return this.javaFormatter.getFormattedContent(this.compilationUnit);
  }

  public String getFileName()
  {
    return this.compilationUnit.getType().getShortName() + ".java";
  }

  public String getTargetPackage() {
    return this.compilationUnit.getType().getPackageName();
  }

  public CompilationUnit getCompilationUnit()
  {
    return this.compilationUnit;
  }

  public boolean isMergeable()
  {
    return true;
  }

  public String getFileEncoding() {
    return this.fileEncoding;
  }
}