package ht.msc.mybatis.plugin;


public class MixedClientGenerator extends JavaMapperGenerator
{
  public MixedClientGenerator()
  {
    super(true);
  }

  protected void addDeleteByPrimaryKeyMethod(Interface interfaze)
  {
    if (this.introspectedTable.getRules().generateDeleteByPrimaryKey()) {
      AbstractJavaMapperMethodGenerator methodGenerator = new AnnotatedDeleteByPrimaryKeyMethodGenerator(false);
      initializeAndExecuteGenerator(methodGenerator, interfaze);
    }
  }

  protected void addInsertMethod(Interface interfaze)
  {
    if (this.introspectedTable.getRules().generateInsert()) {
      AbstractJavaMapperMethodGenerator methodGenerator = new AnnotatedInsertMethodGenerator(false);
      initializeAndExecuteGenerator(methodGenerator, interfaze);
    }
  }

  protected void addSelectByPrimaryKeyMethod(Interface interfaze)
  {
    if (this.introspectedTable.getRules().generateSelectByPrimaryKey()) {
      AbstractJavaMapperMethodGenerator methodGenerator = new AnnotatedSelectByPrimaryKeyMethodGenerator(true, false);
      initializeAndExecuteGenerator(methodGenerator, interfaze);
    }
  }

  protected void addUpdateByPrimaryKeyWithBLOBsMethod(Interface interfaze)
  {
    if (this.introspectedTable.getRules().generateUpdateByPrimaryKeyWithBLOBs()) {
      AbstractJavaMapperMethodGenerator methodGenerator = new AnnotatedUpdateByPrimaryKeyWithBLOBsMethodGenerator();
      initializeAndExecuteGenerator(methodGenerator, interfaze);
    }
  }

  protected void addUpdateByPrimaryKeyWithoutBLOBsMethod(Interface interfaze)
  {
    if (this.introspectedTable.getRules()
      .generateUpdateByPrimaryKeyWithoutBLOBs()) {
      AbstractJavaMapperMethodGenerator methodGenerator = new AnnotatedUpdateByPrimaryKeyWithoutBLOBsMethodGenerator(false);
      initializeAndExecuteGenerator(methodGenerator, interfaze);
    }
  }

  public AbstractXmlGenerator getMatchedXMLGenerator()
  {
    return new MixedMapperGenerator();
  }
}