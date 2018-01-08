package ht.msc.mybatis.plugin;

public abstract class AbstractJavaClientGenerator extends AbstractJavaGenerator
{
  private boolean requiresXMLGenerator;

  public AbstractJavaClientGenerator(boolean requiresXMLGenerator)
  {
    this.requiresXMLGenerator = requiresXMLGenerator;
  }

  public boolean requiresXMLGenerator()
  {
    return this.requiresXMLGenerator;
  }

  public abstract AbstractXmlGenerator getMatchedXMLGenerator();
}