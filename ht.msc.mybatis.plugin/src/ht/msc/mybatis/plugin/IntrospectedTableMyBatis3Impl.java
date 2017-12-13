package ht.msc.mybatis.plugin;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import ht.msc.mybatis.plugin.ExampleGenerator;

public class IntrospectedTableMyBatis3Impl extends IntrospectedTable
{
  protected List<AbstractJavaGenerator> javaModelGenerators;
  protected List<AbstractJavaGenerator> serviceGenerators;
  protected List<AbstractJavaGenerator> daoGenerators;
  protected List<AbstractJSGenerator> jsMVCGenerators;
  protected List<AbstractJavaGenerator> controllerGenerators;
  protected List<AbstractJavaGenerator> clientGenerators;
  protected AbstractXmlGenerator xmlMapperGenerator;

  public IntrospectedTableMyBatis3Impl()
  {
    super(IntrospectedTable.TargetRuntime.MYBATIS3);
    this.javaModelGenerators = new ArrayList();
    this.clientGenerators = new ArrayList();
    this.serviceGenerators = new ArrayList();
    this.daoGenerators = new ArrayList();
    this.jsMVCGenerators = new ArrayList();
    this.controllerGenerators = new ArrayList();
  }

  public void calculateGenerators(List<String> warnings, ProgressCallback progressCallback)
  {
    calculateJavaModelGenerators(warnings, progressCallback);

    AbstractJavaClientGenerator javaClientGenerator = 
      calculateClientGenerators(warnings, progressCallback);

    if (this.context.getYouGouServiceGeneratorConfiguration() != null) {
      calculateServiceGenerators(warnings, progressCallback);
    }
    if (this.context.getYouGouDaoGeneratorConfiguration() != null) {
      calculateDaoGenerators(warnings, progressCallback);
    }
    if (this.context.getYouGouJSMVCGeneratorConfiguration() != null) {
      calculateJSMVCGenerators(warnings, progressCallback);
    }
    if (this.context.getYouGouControllerGeneratorConfiguration() != null) {
      calculateControllerGenerators(warnings, progressCallback);
    }

    calculateXmlMapperGenerator(javaClientGenerator, warnings, progressCallback);
  }

  protected void calculateXmlMapperGenerator(AbstractJavaClientGenerator javaClientGenerator, List<String> warnings, ProgressCallback progressCallback)
  {
    if (javaClientGenerator == null) {
      if (this.context.getSqlMapGeneratorConfiguration() != null)
        this.xmlMapperGenerator = new XMLMapperGenerator();
    }
    else {
      this.xmlMapperGenerator = javaClientGenerator.getMatchedXMLGenerator();
    }

    initializeAbstractGenerator(this.xmlMapperGenerator, warnings, 
      progressCallback);
  }

  protected AbstractJavaClientGenerator calculateClientGenerators(List<String> warnings, ProgressCallback progressCallback)
  {
    if (!this.rules.generateJavaClient()) {
      return null;
    }

    AbstractJavaClientGenerator javaGenerator = createJavaClientGenerator();
    if (javaGenerator == null) {
      return null;
    }

    initializeAbstractGenerator(javaGenerator, warnings, progressCallback);
    this.clientGenerators.add(javaGenerator);

    return javaGenerator;
  }

  protected void calculateServiceGenerators(List<String> warnings, ProgressCallback progressCallback)
  {
    AbstractJavaGenerator javaGenerator = new YouGouServiceGenerator();
    initializeAbstractGenerator(javaGenerator, warnings, 
      progressCallback);
    this.serviceGenerators.add(javaGenerator);
  }

  protected void calculateDaoGenerators(List<String> warnings, ProgressCallback progressCallback)
  {
    AbstractJavaGenerator javaGenerator = new YouGouDaoGenerator();
    initializeAbstractGenerator(javaGenerator, warnings, 
      progressCallback);
    this.daoGenerators.add(javaGenerator);
  }

  protected void calculateJSMVCGenerators(List<String> warnings, ProgressCallback progressCallback)
  {
    AbstractJSGenerator jsGenerator = new YouGouJSMVCGenerator();
    initializeAbstractGenerator(jsGenerator, warnings, 
      progressCallback);
    this.jsMVCGenerators.add(jsGenerator);
  }

  protected void calculateControllerGenerators(List<String> warnings, ProgressCallback progressCallback)
  {
    AbstractJavaGenerator javaGenerator = new YouGouControllerGenerator();
    initializeAbstractGenerator(javaGenerator, warnings, 
      progressCallback);
    this.controllerGenerators.add(javaGenerator);
  }
  protected AbstractJavaClientGenerator createJavaClientGenerator() {
    if (this.context.getJavaClientGeneratorConfiguration() == null) {
      return null;
    }

    String type = this.context.getJavaClientGeneratorConfiguration()
      .getConfigurationType();
    AbstractJavaClientGenerator javaGenerator;
    if ("XMLMAPPER".equalsIgnoreCase(type)) {
      javaGenerator = new JavaMapperGenerator();
    }
    else
    {
      if ("MIXEDMAPPER".equalsIgnoreCase(type)) {
        javaGenerator = new MixedClientGenerator();
      }
      else
      {
        if ("ANNOTATEDMAPPER".equalsIgnoreCase(type)) {
          javaGenerator = new AnnotatedClientGenerator();
        }
        else
        {
          if ("MAPPER".equalsIgnoreCase(type))
            javaGenerator = new JavaMapperGenerator();
          else
            javaGenerator = (AbstractJavaClientGenerator)
              ObjectFactory.createInternalObject(type); 
        }
      }
    }
    return javaGenerator;
  }

  protected void calculateJavaModelGenerators(List<String> warnings, ProgressCallback progressCallback)
  {
    if (getRules().generateExampleClass()) {
      AbstractJavaGenerator javaGenerator = new ExampleGenerator();
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
    if (abstractGenerator == null) {
      return;
    }

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

    for (Iterator localIterator1 = this.clientGenerators.iterator(); localIterator1.hasNext(); 
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

    for (Iterator localIterator1 = this.serviceGenerators.iterator(); localIterator1.hasNext(); 
      localIterator2.hasNext())
    {
      AbstractJavaGenerator javaGenerator = (AbstractJavaGenerator)localIterator1.next();
      List compilationUnits = javaGenerator
        .getCompilationUnits();
      localIterator2 = compilationUnits.iterator(); 
      //continue;
      CompilationUnit compilationUnit = (CompilationUnit)localIterator2.next();
      GeneratedJavaFile gjf = new GeneratedJavaFile(compilationUnit, 
        this.context.getYouGouServiceGeneratorConfiguration()
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
        this.context.getYouGouDaoGeneratorConfiguration()
        .getTargetProject(), 
        this.context.getProperty("javaFileEncoding"), 
        this.context.getJavaFormatter());
      answer.add(gjf);
    }

    for (Iterator localIterator1 = this.controllerGenerators.iterator(); localIterator1.hasNext(); 
      localIterator2.hasNext())
    {
      AbstractJavaGenerator javaGenerator = (AbstractJavaGenerator)localIterator1.next();
      List compilationUnits = javaGenerator
        .getCompilationUnits();
      localIterator2 = compilationUnits.iterator(); 
      //continue; 
      CompilationUnit compilationUnit = (CompilationUnit)localIterator2.next();
      GeneratedJavaFile gjf = new GeneratedJavaFile(compilationUnit, 
        this.context.getYouGouControllerGeneratorConfiguration()
        .getTargetProject(), 
        this.context.getProperty("javaFileEncoding"), 
        this.context.getJavaFormatter());
      answer.add(gjf);
    }

    return answer;
  }

  public List<GeneratedJSFile> getGeneratedJSFiles()
  {
    List answer = new ArrayList();
    Iterator localIterator2;
    for (Iterator localIterator1 = this.jsMVCGenerators.iterator(); localIterator1.hasNext(); 
      localIterator2.hasNext())
    {
      AbstractJSGenerator jsGenerator = (AbstractJSGenerator)localIterator1.next();
      List compilationUnits = jsGenerator.getCompilationUnits();
      localIterator2 = compilationUnits.iterator(); 
      //continue; 
      CompilationUnit compilationUnit = (CompilationUnit)localIterator2.next();
      GeneratedJSFile gjf = new GeneratedJSFile(compilationUnit, 
        this.context.getYouGouJSMVCGeneratorConfiguration()
        .getTargetProject(), 
        this.context.getProperty("jsFileEncoding"), 
        this.context.getJavaFormatter());
      answer.add(gjf);
    }

    return answer;
  }

  public List<GeneratedXmlFile> getGeneratedXmlFiles()
  {
    List answer = new ArrayList();

    if (this.xmlMapperGenerator != null) {
      Document document = this.xmlMapperGenerator.getDocument();

      GeneratedXmlFile gxf = new GeneratedXmlFile(document, 
        getMyBatis3XmlMapperFileName(), getMyBatis3XmlMapperPackage(), 
        this.context.getSqlMapGeneratorConfiguration().getTargetProject(), 
        false, this.context.getXmlFormatter());
      if (this.context.getPlugins().sqlMapGenerated(gxf, this)) {
        answer.add(gxf);
      }
    }

    return answer;
  }

  public int getGenerationSteps()
  {
    return this.javaModelGenerators.size() + this.clientGenerators.size() + (
      this.xmlMapperGenerator == null ? 0 : 1);
  }

  public boolean isJava5Targeted()
  {
    return true;
  }

  public boolean requiresXMLGenerator()
  {
    AbstractJavaClientGenerator javaClientGenerator = 
      createJavaClientGenerator();

    if (javaClientGenerator == null) {
      return false;
    }
    return javaClientGenerator.requiresXMLGenerator();
  }
}