package ht.msc.mybatis.plugin;

import java.util.List;


public class IntrospectedTableMyBatis3SimpleImpl extends IntrospectedTableMyBatis3Impl
{
  protected void calculateXmlMapperGenerator(AbstractJavaClientGenerator javaClientGenerator, List<String> warnings, ProgressCallback progressCallback)
  {
    if (javaClientGenerator == null) {
      if (this.context.getSqlMapGeneratorConfiguration() != null)
        this.xmlMapperGenerator = new SimpleXMLMapperGenerator();
    }
    else {
      this.xmlMapperGenerator = javaClientGenerator.getMatchedXMLGenerator();
    }

    initializeAbstractGenerator(this.xmlMapperGenerator, warnings, 
      progressCallback);
  }

  protected AbstractJavaClientGenerator createJavaClientGenerator()
  {
    if (this.context.getJavaClientGeneratorConfiguration() == null) {
      return null;
    }

    String type = this.context.getJavaClientGeneratorConfiguration()
      .getConfigurationType();
    AbstractJavaClientGenerator javaGenerator;
    if ("XMLMAPPER".equalsIgnoreCase(type)) {
      javaGenerator = new SimpleJavaClientGenerator();
    }
    else
    {
      if ("ANNOTATEDMAPPER".equalsIgnoreCase(type)) {
        javaGenerator = new SimpleAnnotatedClientGenerator();
      }
      else
      {
        if ("MAPPER".equalsIgnoreCase(type))
          javaGenerator = new SimpleJavaClientGenerator();
        else
          javaGenerator = (AbstractJavaClientGenerator)
            ObjectFactory.createInternalObject(type);
      }
    }
    return javaGenerator;
  }

  protected void calculateJavaModelGenerators(List<String> warnings, ProgressCallback progressCallback)
  {
    AbstractJavaGenerator javaGenerator = new SimpleModelGenerator();
    initializeAbstractGenerator(javaGenerator, warnings, 
      progressCallback);
    this.javaModelGenerators.add(javaGenerator);
  }
}