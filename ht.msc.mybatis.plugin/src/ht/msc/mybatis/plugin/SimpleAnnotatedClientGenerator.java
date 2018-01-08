package ht.msc.mybatis.plugin;


public class SimpleAnnotatedClientGenerator extends SimpleJavaClientGenerator
{
  public SimpleAnnotatedClientGenerator()
  {
    super(false);
  }

  protected void addDeleteByPrimaryKeyMethod(Interface interfaze)
  {
    if (this.introspectedTable.getRules().generateDeleteByPrimaryKey()) {
      AbstractJavaMapperMethodGenerator methodGenerator = new AnnotatedDeleteByPrimaryKeyMethodGenerator(true);
      initializeAndExecuteGenerator(methodGenerator, interfaze);
    }
  }

  protected void addInsertMethod(Interface interfaze)
  {
    if (this.introspectedTable.getRules().generateInsert()) {
      AbstractJavaMapperMethodGenerator methodGenerator = new AnnotatedInsertMethodGenerator(true);
      initializeAndExecuteGenerator(methodGenerator, interfaze);
    }
  }

  protected void addSelectByPrimaryKeyMethod(Interface interfaze)
  {
    if (this.introspectedTable.getRules().generateSelectByPrimaryKey()) {
      AbstractJavaMapperMethodGenerator methodGenerator = new AnnotatedSelectByPrimaryKeyMethodGenerator(false, true);
      initializeAndExecuteGenerator(methodGenerator, interfaze);
    }
  }

  protected void addSelectAllMethod(Interface interfaze)
  {
    AbstractJavaMapperMethodGenerator methodGenerator = new AnnotatedSelectAllMethodGenerator();
    initializeAndExecuteGenerator(methodGenerator, interfaze);
  }

  protected void addUpdateByPrimaryKeyMethod(Interface interfaze)
  {
    if (this.introspectedTable.getRules().generateUpdateByPrimaryKeySelective()) {
      AbstractJavaMapperMethodGenerator methodGenerator = new AnnotatedUpdateByPrimaryKeyWithoutBLOBsMethodGenerator(true);
      initializeAndExecuteGenerator(methodGenerator, interfaze);
    }
  }

  public AbstractXmlGenerator getMatchedXMLGenerator()
  {
    return null;
  }
}