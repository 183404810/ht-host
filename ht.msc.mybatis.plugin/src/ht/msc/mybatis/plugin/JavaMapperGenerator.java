package ht.msc.mybatis.plugin;

import ht.msc.mybatis.plugin.element.CountByExampleMethodGenerator;
import ht.msc.mybatis.plugin.element.DeleteByExampleMethodGenerator;

import java.util.ArrayList;
import java.util.List;


public class JavaMapperGenerator extends AbstractJavaClientGenerator
{
  public JavaMapperGenerator()
  {
    super(true);
  }

  public JavaMapperGenerator(boolean requiresMatchedXMLGenerator) {
    super(requiresMatchedXMLGenerator);
  }

  public List<CompilationUnit> getCompilationUnits()
  {
    this.progressCallback.startTask(Messages.getString("Progress.17", 
      this.introspectedTable.getFullyQualifiedTable().toString()));
    CommentGenerator commentGenerator = this.context.getCommentGenerator();

    FullyQualifiedJavaType type = new FullyQualifiedJavaType(
      this.introspectedTable.getMyBatis3JavaMapperType());
    Interface interfaze = new Interface(type);
    interfaze.setVisibility(JavaVisibility.PUBLIC);

    commentGenerator.addClassComment(interfaze);

    String rootInterface = this.introspectedTable
      .getTableConfigurationProperty("rootInterface");
    if (!StringUtility.stringHasValue(rootInterface)) {
      rootInterface = this.context.getJavaClientGeneratorConfiguration()
        .getProperty("rootInterface");
    }

    if (StringUtility.stringHasValue(rootInterface)) {
      FullyQualifiedJavaType fqjt = new FullyQualifiedJavaType(
        rootInterface);
      interfaze.addSuperInterface(fqjt);
      interfaze.addImportedType(fqjt);
    }

    JavaClientGeneratorConfiguration clientGenerator = this.context.getJavaClientGeneratorConfiguration();
    if (clientGenerator.exclusionsMethods != null) {
      if (!clientGenerator.exclusionsMethods.contains("countByQuery")) {
        addCountByExampleMethod(interfaze);
      }
      if (!clientGenerator.exclusionsMethods.contains("deleteByQuery")) {
        addDeleteByExampleMethod(interfaze);
      }
      if (!clientGenerator.exclusionsMethods.contains("deleteByPrimaryKey")) {
        addDeleteByPrimaryKeyMethod(interfaze);
      }
      if (!clientGenerator.exclusionsMethods.contains("insert")) {
        addInsertMethod(interfaze);
      }
      if (!clientGenerator.exclusionsMethods.contains("insertSelective")) {
        addInsertSelectiveMethod(interfaze);
      }
      if (!clientGenerator.exclusionsMethods.contains("selectByQuery")) {
        addSelectByExampleWithBLOBsMethod(interfaze);
      }
      if (!clientGenerator.exclusionsMethods.contains("selectByQueryWithBLOBs")) {
        addSelectByExampleWithoutBLOBsMethod(interfaze);
      }
      if (!clientGenerator.exclusionsMethods.contains("selectByPrimaryKey")) {
        addSelectByPrimaryKeyMethod(interfaze);
      }

      if (!clientGenerator.exclusionsMethods.contains("selectCount")) {
        addSelectCountMethod(interfaze);
      }

      if (!clientGenerator.exclusionsMethods.contains("selectByPage")) {
        addSelectByPageMethod(interfaze);
      }

      if (!clientGenerator.exclusionsMethods.contains("updateByQuery")) {
        addUpdateByExampleSelectiveMethod(interfaze);
      }
      if (!clientGenerator.exclusionsMethods.contains("updateByQueryWithBLOBs")) {
        addUpdateByExampleWithBLOBsMethod(interfaze);
      }
      if (!clientGenerator.exclusionsMethods.contains("updateByQueryWithBLOBs")) {
        addUpdateByExampleWithoutBLOBsMethod(interfaze);
      }
      if (!clientGenerator.exclusionsMethods.contains("updateByPrimaryKeySelective")) {
        addUpdateByPrimaryKeySelectiveMethod(interfaze);
      }
      if (!clientGenerator.exclusionsMethods.contains("updateByPrimaryKeyWithBLOBs")) {
        addUpdateByPrimaryKeyWithBLOBsMethod(interfaze);
      }
      if (!clientGenerator.exclusionsMethods.contains("updateByPrimaryKeyWithBLOBs"))
        addUpdateByPrimaryKeyWithoutBLOBsMethod(interfaze);
    }
    else {
      addDeleteByPrimaryKeyMethod(interfaze);
      addInsertMethod(interfaze);
      addInsertSelectiveMethod(interfaze);
      addSelectByPrimaryKeyMethod(interfaze);
      addSelectCountMethod(interfaze);
      addUpdateByPrimaryKeySelectiveMethod(interfaze);
      addUpdateByPrimaryKeyWithBLOBsMethod(interfaze);
      addUpdateByPrimaryKeyWithoutBLOBsMethod(interfaze);
    }

    List answer = new ArrayList();
    if (this.context.getPlugins().clientGenerated(interfaze, null, 
      this.introspectedTable)) {
      answer.add(interfaze);
    }

    JavaClientGeneratorConfiguration config = this.context.getJavaClientGeneratorConfiguration();
    if (config.getInterFaceExtendSupInterfaceDoMain().length() > 0) {
      interfaze.addImportedType(new FullyQualifiedJavaType(config.getInterfaceExtendSupInterface()));
      String basemapper = config.getInterFaceExtendSupInterfaceDoMain();
      String modelName = this.introspectedTable.getFullyQualifiedTable().getDomainObjectName();
      if (new Boolean(config.getEnableInterfaceSupInterfaceGenericity()).booleanValue()) {
        interfaze.addImportedType(new FullyQualifiedJavaType(this.introspectedTable.getBaseRecordType()));
        interfaze.addSuperInterface(new FullyQualifiedJavaType(basemapper + "<" + modelName + ">"));
      } else {
        interfaze.addSuperInterface(new FullyQualifiedJavaType(basemapper));
      }
    }

    List extraCompilationUnits = getExtraCompilationUnits();
    if (extraCompilationUnits != null) {
      answer.addAll(extraCompilationUnits);
    }

    return answer;
  }

  protected void addCountByExampleMethod(Interface interfaze) {
    if (this.introspectedTable.getRules().generateCountByExample()) {
      AbstractJavaMapperMethodGenerator methodGenerator = new CountByExampleMethodGenerator();
      initializeAndExecuteGenerator(methodGenerator, interfaze);
    }
  }

  protected void addDeleteByExampleMethod(Interface interfaze) {
    if (this.introspectedTable.getRules().generateDeleteByExample()) {
      AbstractJavaMapperMethodGenerator methodGenerator = new DeleteByExampleMethodGenerator();
      initializeAndExecuteGenerator(methodGenerator, interfaze);
    }
  }

  protected void addDeleteByPrimaryKeyMethod(Interface interfaze) {
    if (this.introspectedTable.getRules().generateDeleteByPrimaryKey()) {
      AbstractJavaMapperMethodGenerator methodGenerator = new DeleteByPrimaryKeyMethodGenerator(false);
      initializeAndExecuteGenerator(methodGenerator, interfaze);
    }
  }

  protected void addInsertMethod(Interface interfaze) {
    if (this.introspectedTable.getRules().generateInsert()) {
      AbstractJavaMapperMethodGenerator methodGenerator = new InsertMethodGenerator(false);
      initializeAndExecuteGenerator(methodGenerator, interfaze);
    }
  }

  protected void addInsertSelectiveMethod(Interface interfaze) {
    if (this.introspectedTable.getRules().generateInsertSelective()) {
      AbstractJavaMapperMethodGenerator methodGenerator = new InsertSelectiveMethodGenerator();
      initializeAndExecuteGenerator(methodGenerator, interfaze);
    }
  }

  protected void addSelectByExampleWithBLOBsMethod(Interface interfaze) {
    if (this.introspectedTable.getRules().generateSelectByExampleWithBLOBs()) {
      AbstractJavaMapperMethodGenerator methodGenerator = new SelectByExampleWithBLOBsMethodGenerator();
      initializeAndExecuteGenerator(methodGenerator, interfaze);
    }
  }

  protected void addSelectByExampleWithoutBLOBsMethod(Interface interfaze) {
    if (this.introspectedTable.getRules().generateSelectByExampleWithoutBLOBs()) {
      AbstractJavaMapperMethodGenerator methodGenerator = new SelectByExampleWithoutBLOBsMethodGenerator();
      initializeAndExecuteGenerator(methodGenerator, interfaze);
    }
  }

  protected void addSelectByPrimaryKeyMethod(Interface interfaze) {
    if (this.introspectedTable.getRules().generateSelectByPrimaryKey()) {
      AbstractJavaMapperMethodGenerator methodGenerator = new SelectByPrimaryKeyMethodGenerator(false);
      initializeAndExecuteGenerator(methodGenerator, interfaze);
    }
  }

  protected void addSelectCountMethod(Interface interfaze)
  {
    if (this.introspectedTable.getRules().generateSelectCount()) {
      AbstractJavaMapperMethodGenerator methodGenerator = new SelectCountMethodGenerator();
      initializeAndExecuteGenerator(methodGenerator, interfaze);
    }
  }

  protected void addSelectByPageMethod(Interface interfaze)
  {
    if (this.introspectedTable.getRules().generateSelectByPage()) {
      AbstractJavaMapperMethodGenerator methodGenerator = new SelectByPageMethodGenerator(this.context);
      initializeAndExecuteGenerator(methodGenerator, interfaze);
    }
  }

  protected void addUpdateByExampleSelectiveMethod(Interface interfaze) {
    if (this.introspectedTable.getRules().generateUpdateByExampleSelective()) {
      AbstractJavaMapperMethodGenerator methodGenerator = new UpdateByExampleSelectiveMethodGenerator();
      initializeAndExecuteGenerator(methodGenerator, interfaze);
    }
  }

  protected void addUpdateByExampleWithBLOBsMethod(Interface interfaze) {
    if (this.introspectedTable.getRules().generateUpdateByExampleWithBLOBs()) {
      AbstractJavaMapperMethodGenerator methodGenerator = new UpdateByExampleWithBLOBsMethodGenerator();
      initializeAndExecuteGenerator(methodGenerator, interfaze);
    }
  }

  protected void addUpdateByExampleWithoutBLOBsMethod(Interface interfaze) {
    if (this.introspectedTable.getRules().generateUpdateByExampleWithoutBLOBs()) {
      AbstractJavaMapperMethodGenerator methodGenerator = new UpdateByExampleWithoutBLOBsMethodGenerator();
      initializeAndExecuteGenerator(methodGenerator, interfaze);
    }
  }

  protected void addUpdateByPrimaryKeySelectiveMethod(Interface interfaze) {
    if (this.introspectedTable.getRules().generateUpdateByPrimaryKeySelective()) {
      AbstractJavaMapperMethodGenerator methodGenerator = new UpdateByPrimaryKeySelectiveMethodGenerator();
      initializeAndExecuteGenerator(methodGenerator, interfaze);
    }
  }

  protected void addUpdateByPrimaryKeyWithBLOBsMethod(Interface interfaze) {
    if (this.introspectedTable.getRules().generateUpdateByPrimaryKeyWithBLOBs()) {
      AbstractJavaMapperMethodGenerator methodGenerator = new UpdateByPrimaryKeyWithBLOBsMethodGenerator();
      initializeAndExecuteGenerator(methodGenerator, interfaze);
    }
  }

  protected void addUpdateByPrimaryKeyWithoutBLOBsMethod(Interface interfaze)
  {
    if (this.introspectedTable.getRules()
      .generateUpdateByPrimaryKeyWithoutBLOBs()) {
      AbstractJavaMapperMethodGenerator methodGenerator = new UpdateByPrimaryKeyWithoutBLOBsMethodGenerator();
      initializeAndExecuteGenerator(methodGenerator, interfaze);
    }
  }

  protected void initializeAndExecuteGenerator(AbstractJavaMapperMethodGenerator methodGenerator, Interface interfaze)
  {
    methodGenerator.setContext(this.context);
    methodGenerator.setIntrospectedTable(this.introspectedTable);
    methodGenerator.setProgressCallback(this.progressCallback);
    methodGenerator.setWarnings(this.warnings);
    methodGenerator.addInterfaceElements(interfaze);
  }

  public List<CompilationUnit> getExtraCompilationUnits() {
    return null;
  }

  public AbstractXmlGenerator getMatchedXMLGenerator()
  {
    return new XMLMapperGenerator();
  }
}