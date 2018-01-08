package ht.msc.mybatis.plugin;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class IntrospectedTableIbatis2Java2Impl extends IntrospectedTable
{
  protected List<AbstractJavaGenerator> javaModelGenerators;
  protected List<AbstractJavaGenerator> daoGenerators;
  protected AbstractXmlGenerator sqlMapGenerator;

  public IntrospectedTableIbatis2Java2Impl()
  {
    super(IntrospectedTable.TargetRuntime.IBATIS2);
    this.javaModelGenerators = new ArrayList();
    this.daoGenerators = new ArrayList();
  }

  public void calculateGenerators(List<String> warnings, ProgressCallback progressCallback)
  {
    calculateJavaModelGenerators(warnings, progressCallback);
    calculateDAOGenerators(warnings, progressCallback);
    calculateSqlMapGenerator(warnings, progressCallback);
  }

  protected void calculateSqlMapGenerator(List<String> warnings, ProgressCallback progressCallback)
  {
    this.sqlMapGenerator = new SqlMapGenerator();
    initializeAbstractGenerator(this.sqlMapGenerator, warnings, progressCallback);
  }

  protected void calculateDAOGenerators(List<String> warnings, ProgressCallback progressCallback)
  {
    if (this.context.getJavaClientGeneratorConfiguration() == null) {
      return;
    }

    String type = this.context.getJavaClientGeneratorConfiguration()
      .getConfigurationType();
    AbstractJavaGenerator javaGenerator;
    if ("IBATIS".equalsIgnoreCase(type)) {
      javaGenerator = new DAOGenerator(new IbatisDAOTemplate(), 
        isJava5Targeted());
    }
    else
    {
      if ("SPRING".equalsIgnoreCase(type)) {
        javaGenerator = new DAOGenerator(new SpringDAOTemplate(), 
          isJava5Targeted());
      }
      else
      {
        if ("GENERIC-CI".equalsIgnoreCase(type)) {
          javaGenerator = new DAOGenerator(new GenericCIDAOTemplate(), 
            isJava5Targeted());
        }
        else
        {
          if ("GENERIC-SI".equalsIgnoreCase(type))
            javaGenerator = new DAOGenerator(new GenericSIDAOTemplate(), 
              isJava5Targeted());
          else
            javaGenerator = (AbstractJavaGenerator)
              ObjectFactory.createInternalObject(type); 
        }
      }
    }
    initializeAbstractGenerator(javaGenerator, warnings, progressCallback);
    this.daoGenerators.add(javaGenerator);
  }

  protected void calculateJavaModelGenerators(List<String> warnings, ProgressCallback progressCallback)
  {
    if (getRules().generateExampleClass()) {
      AbstractJavaGenerator javaGenerator = new ExampleGenerator(
        isJava5Targeted());
      initializeAbstractGenerator(javaGenerator, warnings, 
        progressCallback);
      this.javaModelGenerators.add(javaGenerator);
    }

    if (getRules().generatePrimaryKeyClass()) {
      AbstractJavaGenerator javaGenerator = new PrimaryKeyGenerator();
      initializeAbstractGenerator(javaGenerator, warnings, 
        progressCallback);
      this.javaModelGenerators.add(javaGenerator);
    }

    if (getRules().generateBaseRecordClass()) {
      AbstractJavaGenerator javaGenerator = new BaseRecordGenerator();
      initializeAbstractGenerator(javaGenerator, warnings, 
        progressCallback);
      this.javaModelGenerators.add(javaGenerator);
    }

    if (getRules().generateRecordWithBLOBsClass()) {
      AbstractJavaGenerator javaGenerator = new RecordWithBLOBsGenerator();
      initializeAbstractGenerator(javaGenerator, warnings, 
        progressCallback);
      this.javaModelGenerators.add(javaGenerator);
    }
  }

  protected void initializeAbstractGenerator(AbstractGenerator abstractGenerator, List<String> warnings, ProgressCallback progressCallback)
  {
    abstractGenerator.setContext(this.context);
    abstractGenerator.setIntrospectedTable(this);
    abstractGenerator.setProgressCallback(progressCallback);
    abstractGenerator.setWarnings(warnings);
  }

  public List<GeneratedJavaFile> getGeneratedJavaFiles()
  {
    List answer = new ArrayList();
    Iterator localIterator2;
    for (Iterator localIterator1 = this.javaModelGenerators.iterator(); localIterator1.hasNext(); 
      localIterator2.hasNext())
    {
      AbstractJavaGenerator javaGenerator = (AbstractJavaGenerator)localIterator1.next();
      List compilationUnits = javaGenerator
        .getCompilationUnits();
      localIterator2 = compilationUnits.iterator(); 
      //continue; 
      CompilationUnit compilationUnit = (CompilationUnit)localIterator2.next();
      GeneratedJavaFile gjf = new GeneratedJavaFile(compilationUnit, 
        this.context.getJavaModelGeneratorConfiguration()
        .getTargetProject(), 
        this.context.getProperty("javaFileEncoding"), 
        this.context.getJavaFormatter());
      answer.add(gjf);
    }

    for (Iterator localIterator1 = this.daoGenerators.iterator(); localIterator1.hasNext(); 
      localIterator2.hasNext())
    {
      AbstractJavaGenerator javaGenerator = (AbstractJavaGenerator)localIterator1.next();
      List compilationUnits = javaGenerator
        .getCompilationUnits();
      localIterator2 = compilationUnits.iterator(); 
      //continue; 
      CompilationUnit compilationUnit = (CompilationUnit)localIterator2.next();
      GeneratedJavaFile gjf = new GeneratedJavaFile(compilationUnit, 
        this.context.getJavaClientGeneratorConfiguration()
        .getTargetProject(), 
        this.context.getProperty("javaFileEncoding"), 
        this.context.getJavaFormatter());
      answer.add(gjf);
    }

    return answer;
  }

  public List<GeneratedXmlFile> getGeneratedXmlFiles()
  {
    List answer = new ArrayList();

    Document document = this.sqlMapGenerator.getDocument();
    GeneratedXmlFile gxf = new GeneratedXmlFile(document, 
      getIbatis2SqlMapFileName(), getIbatis2SqlMapPackage(), this.context
      .getSqlMapGeneratorConfiguration().getTargetProject(), 
      true, this.context.getXmlFormatter());
    if (this.context.getPlugins().sqlMapGenerated(gxf, this)) {
      answer.add(gxf);
    }

    return answer;
  }

  public boolean isJava5Targeted()
  {
    return false;
  }

  public int getGenerationSteps()
  {
    return this.javaModelGenerators.size() + this.daoGenerators.size() + 1;
  }

  public boolean requiresXMLGenerator()
  {
    return true;
  }

  public List<GeneratedJSFile> getGeneratedJSFiles()
  {
    return null;
  }
}