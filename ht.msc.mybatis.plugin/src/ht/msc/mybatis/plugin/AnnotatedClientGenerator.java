package ht.msc.mybatis.plugin;

import ht.msc.mybatis.plugin.element.AnnotatedDeleteByExampleMethodGenerator;
import ht.msc.mybatis.plugin.element.AnnotatedInsertSelectiveMethodGenerator;
import ht.msc.mybatis.plugin.element.AnnotatedSelectByExampleWithBLOBsMethodGenerator;
import ht.msc.mybatis.plugin.element.AnnotatedSelectByExampleWithoutBLOBsMethodGenerator;
import ht.msc.mybatis.plugin.element.AnnotatedUpdateByExampleSelectiveMethodGenerator;
import ht.msc.mybatis.plugin.element.AnnotatedUpdateByExampleWithBLOBsMethodGenerator;
import ht.msc.mybatis.plugin.element.AnnotatedUpdateByExampleWithoutBLOBsMethodGenerator;
import ht.msc.mybatis.plugin.element.AnnotatedUpdateByPrimaryKeySelectiveMethodGenerator;
import ht.msc.mybatis.plugin.element.SqlProviderGenerator;

import java.util.List;


public class AnnotatedClientGenerator extends JavaMapperGenerator
{
  public AnnotatedClientGenerator()
  {
    super(false);
  }

  protected void addCountByExampleMethod(Interface interfaze)
  {
    if (this.introspectedTable.getRules().generateCountByExample()) {
      AbstractJavaMapperMethodGenerator methodGenerator = new AnnotatedCountByExampleMethodGenerator();
      initializeAndExecuteGenerator(methodGenerator, interfaze);
    }
  }

  protected void addDeleteByExampleMethod(Interface interfaze)
  {
    if (this.introspectedTable.getRules().generateDeleteByExample()) {
      AbstractJavaMapperMethodGenerator methodGenerator = new AnnotatedDeleteByExampleMethodGenerator();
      initializeAndExecuteGenerator(methodGenerator, interfaze);
    }
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

  protected void addInsertSelectiveMethod(Interface interfaze)
  {
    if (this.introspectedTable.getRules().generateInsertSelective()) {
      AbstractJavaMapperMethodGenerator methodGenerator = new AnnotatedInsertSelectiveMethodGenerator();
      initializeAndExecuteGenerator(methodGenerator, interfaze);
    }
  }

  protected void addSelectByExampleWithBLOBsMethod(Interface interfaze)
  {
    if (this.introspectedTable.getRules().generateSelectByExampleWithBLOBs()) {
      AbstractJavaMapperMethodGenerator methodGenerator = new AnnotatedSelectByExampleWithBLOBsMethodGenerator();
      initializeAndExecuteGenerator(methodGenerator, interfaze);
    }
  }

  protected void addSelectByExampleWithoutBLOBsMethod(Interface interfaze)
  {
    if (this.introspectedTable.getRules().generateSelectByExampleWithoutBLOBs()) {
      AbstractJavaMapperMethodGenerator methodGenerator = new AnnotatedSelectByExampleWithoutBLOBsMethodGenerator();
      initializeAndExecuteGenerator(methodGenerator, interfaze);
    }
  }

  protected void addSelectByPrimaryKeyMethod(Interface interfaze)
  {
    if (this.introspectedTable.getRules().generateSelectByPrimaryKey()) {
      AbstractJavaMapperMethodGenerator methodGenerator = new AnnotatedSelectByPrimaryKeyMethodGenerator(false, false);
      initializeAndExecuteGenerator(methodGenerator, interfaze);
    }
  }

  protected void addUpdateByExampleSelectiveMethod(Interface interfaze)
  {
    if (this.introspectedTable.getRules().generateUpdateByExampleSelective()) {
      AbstractJavaMapperMethodGenerator methodGenerator = new AnnotatedUpdateByExampleSelectiveMethodGenerator();
      initializeAndExecuteGenerator(methodGenerator, interfaze);
    }
  }

  protected void addUpdateByExampleWithBLOBsMethod(Interface interfaze)
  {
    if (this.introspectedTable.getRules().generateUpdateByExampleWithBLOBs()) {
      AbstractJavaMapperMethodGenerator methodGenerator = new AnnotatedUpdateByExampleWithBLOBsMethodGenerator();
      initializeAndExecuteGenerator(methodGenerator, interfaze);
    }
  }

  protected void addUpdateByExampleWithoutBLOBsMethod(Interface interfaze)
  {
    if (this.introspectedTable.getRules().generateUpdateByExampleWithoutBLOBs()) {
      AbstractJavaMapperMethodGenerator methodGenerator = new AnnotatedUpdateByExampleWithoutBLOBsMethodGenerator();
      initializeAndExecuteGenerator(methodGenerator, interfaze);
    }
  }

  protected void addUpdateByPrimaryKeySelectiveMethod(Interface interfaze)
  {
    if (this.introspectedTable.getRules().generateUpdateByPrimaryKeySelective()) {
      AbstractJavaMapperMethodGenerator methodGenerator = new AnnotatedUpdateByPrimaryKeySelectiveMethodGenerator();
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

  public List<CompilationUnit> getExtraCompilationUnits()
  {
    SqlProviderGenerator sqlProviderGenerator = new SqlProviderGenerator();
    sqlProviderGenerator.setContext(this.context);
    sqlProviderGenerator.setIntrospectedTable(this.introspectedTable);
    sqlProviderGenerator.setProgressCallback(this.progressCallback);
    sqlProviderGenerator.setWarnings(this.warnings);
    return sqlProviderGenerator.getCompilationUnits();
  }

  public AbstractXmlGenerator getMatchedXMLGenerator()
  {
    return null;
  }
}