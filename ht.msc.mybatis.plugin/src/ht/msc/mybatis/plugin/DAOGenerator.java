package ht.msc.mybatis.plugin;

import ht.msc.mybatis.plugin.element.DeleteByPrimaryKeyMethodGenerator;
import ht.msc.mybatis.plugin.element.InsertMethodGenerator;
import ht.msc.mybatis.plugin.element.InsertSelectiveMethodGenerator;
import ht.msc.mybatis.plugin.element.SelectByExampleWithBLOBsMethodGenerator;
import ht.msc.mybatis.plugin.element.SelectByExampleWithoutBLOBsMethodGenerator;
import ht.msc.mybatis.plugin.element.SelectByPrimaryKeyMethodGenerator;
import ht.msc.mybatis.plugin.element.UpdateByExampleParmsInnerclassGenerator;
import ht.msc.mybatis.plugin.element.UpdateByExampleSelectiveMethodGenerator;
import ht.msc.mybatis.plugin.element.UpdateByExampleWithBLOBsMethodGenerator;
import ht.msc.mybatis.plugin.element.UpdateByExampleWithoutBLOBsMethodGenerator;
import ht.msc.mybatis.plugin.element.UpdateByPrimaryKeySelectiveMethodGenerator;
import ht.msc.mybatis.plugin.element.UpdateByPrimaryKeyWithBLOBsMethodGenerator;
import ht.msc.mybatis.plugin.element.UpdateByPrimaryKeyWithoutBLOBsMethodGenerator;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DAOGenerator extends AbstractJavaClientGenerator
{
  private AbstractDAOTemplate daoTemplate;
  private boolean generateForJava5;

  public DAOGenerator(AbstractDAOTemplate daoTemplate, boolean generateForJava5)
  {
    super(true);
    this.daoTemplate = daoTemplate;
    this.generateForJava5 = generateForJava5;
  }

  public List<CompilationUnit> getCompilationUnits()
  {
    FullyQualifiedTable table = this.introspectedTable.getFullyQualifiedTable();
    this.progressCallback.startTask(Messages.getString(
      "Progress.14", table.toString()));
    TopLevelClass topLevelClass = getTopLevelClassShell();
    Interface interfaze = getInterfaceShell();

    addCountByExampleMethod(topLevelClass, interfaze);
    addDeleteByExampleMethod(topLevelClass, interfaze);
    addDeleteByPrimaryKeyMethod(topLevelClass, interfaze);
    addInsertMethod(topLevelClass, interfaze);
    addInsertSelectiveMethod(topLevelClass, interfaze);
    addSelectByExampleWithBLOBsMethod(topLevelClass, interfaze);
    addSelectByExampleWithoutBLOBsMethod(topLevelClass, interfaze);
    addSelectByPrimaryKeyMethod(topLevelClass, interfaze);
    addUpdateByExampleParmsInnerclass(topLevelClass, interfaze);
    addUpdateByExampleSelectiveMethod(topLevelClass, interfaze);
    addUpdateByExampleWithBLOBsMethod(topLevelClass, interfaze);
    addUpdateByExampleWithoutBLOBsMethod(topLevelClass, interfaze);
    addUpdateByPrimaryKeySelectiveMethod(topLevelClass, interfaze);
    addUpdateByPrimaryKeyWithBLOBsMethod(topLevelClass, interfaze);
    addUpdateByPrimaryKeyWithoutBLOBsMethod(topLevelClass, interfaze);

    List answer = new ArrayList();
    if (this.context.getPlugins().clientGenerated(interfaze, 
      topLevelClass, this.introspectedTable)) {
      answer.add(topLevelClass);
      answer.add(interfaze);
    }

    return answer;
  }

  protected TopLevelClass getTopLevelClassShell() {
    FullyQualifiedJavaType interfaceType = new FullyQualifiedJavaType(
      this.introspectedTable.getDAOInterfaceType());
    FullyQualifiedJavaType implementationType = new FullyQualifiedJavaType(
      this.introspectedTable.getDAOImplementationType());

    CommentGenerator commentGenerator = this.context.getCommentGenerator();

    TopLevelClass answer = new TopLevelClass(implementationType);
    answer.setVisibility(JavaVisibility.PUBLIC);
    answer.setSuperClass(this.daoTemplate.getSuperClass());
    answer.addImportedType(this.daoTemplate.getSuperClass());
    answer.addSuperInterface(interfaceType);
    answer.addImportedType(interfaceType);

    Iterator localIterator = this.daoTemplate
      .getImplementationImports().iterator();

    while (localIterator.hasNext()) {
      FullyQualifiedJavaType fqjt = (FullyQualifiedJavaType)localIterator.next();
      answer.addImportedType(fqjt);
    }

    commentGenerator.addJavaFileComment(answer);

    answer.addMethod(this.daoTemplate.getConstructorClone(commentGenerator, 
      implementationType, this.introspectedTable));

    for (Field field : this.daoTemplate.getFieldClones(commentGenerator, 
      this.introspectedTable))
    {
      answer.addField(field);
    }

    for (Method method : this.daoTemplate.getMethodClones(commentGenerator, 
      this.introspectedTable))
    {
      answer.addMethod(method);
    }

    return answer;
  }

  protected Interface getInterfaceShell() {
    Interface answer = new Interface(new FullyQualifiedJavaType(
      this.introspectedTable.getDAOInterfaceType()));
    answer.setVisibility(JavaVisibility.PUBLIC);

    String rootInterface = this.introspectedTable
      .getTableConfigurationProperty("rootInterface");
    if (!StringUtility.stringHasValue(rootInterface)) {
      rootInterface = this.context.getJavaClientGeneratorConfiguration()
        .getProperty("rootInterface");
    }

    if (StringUtility.stringHasValue(rootInterface)) {
      FullyQualifiedJavaType fqjt = new FullyQualifiedJavaType(
        rootInterface);
      answer.addSuperInterface(fqjt);
      answer.addImportedType(fqjt);
    }

    for (FullyQualifiedJavaType fqjt : this.daoTemplate.getInterfaceImports()) {
      answer.addImportedType(fqjt);
    }

    this.context.getCommentGenerator().addJavaFileComment(answer);

    return answer;
  }

  protected void addCountByExampleMethod(TopLevelClass topLevelClass, Interface interfaze)
  {
    if (this.introspectedTable.getRules().generateCountByExample()) {
      AbstractDAOElementGenerator methodGenerator = new CountByExampleMethodGenerator(
        this.generateForJava5);
      initializeAndExecuteGenerator(methodGenerator, topLevelClass, 
        interfaze);
    }
  }

  protected void addDeleteByExampleMethod(TopLevelClass topLevelClass, Interface interfaze)
  {
    if (this.introspectedTable.getRules().generateDeleteByExample()) {
      AbstractDAOElementGenerator methodGenerator = new DeleteByExampleMethodGenerator();
      initializeAndExecuteGenerator(methodGenerator, topLevelClass, 
        interfaze);
    }
  }

  protected void addDeleteByPrimaryKeyMethod(TopLevelClass topLevelClass, Interface interfaze)
  {
    if (this.introspectedTable.getRules().generateDeleteByPrimaryKey()) {
      AbstractDAOElementGenerator methodGenerator = new DeleteByPrimaryKeyMethodGenerator();
      initializeAndExecuteGenerator(methodGenerator, topLevelClass, 
        interfaze);
    }
  }

  protected void addInsertMethod(TopLevelClass topLevelClass, Interface interfaze)
  {
    if (this.introspectedTable.getRules().generateInsert()) {
      AbstractDAOElementGenerator methodGenerator = new InsertMethodGenerator();
      initializeAndExecuteGenerator(methodGenerator, topLevelClass, 
        interfaze);
    }
  }

  protected void addInsertSelectiveMethod(TopLevelClass topLevelClass, Interface interfaze)
  {
    if (this.introspectedTable.getRules().generateInsertSelective()) {
      AbstractDAOElementGenerator methodGenerator = new InsertSelectiveMethodGenerator();
      initializeAndExecuteGenerator(methodGenerator, topLevelClass, 
        interfaze);
    }
  }

  protected void addSelectByExampleWithBLOBsMethod(TopLevelClass topLevelClass, Interface interfaze)
  {
    if (this.introspectedTable.getRules().generateSelectByExampleWithBLOBs()) {
      AbstractDAOElementGenerator methodGenerator = new SelectByExampleWithBLOBsMethodGenerator(
        this.generateForJava5);
      initializeAndExecuteGenerator(methodGenerator, topLevelClass, 
        interfaze);
    }
  }

  protected void addSelectByExampleWithoutBLOBsMethod(TopLevelClass topLevelClass, Interface interfaze)
  {
    if (this.introspectedTable.getRules().generateSelectByExampleWithoutBLOBs()) {
      AbstractDAOElementGenerator methodGenerator = new SelectByExampleWithoutBLOBsMethodGenerator(
        this.generateForJava5);
      initializeAndExecuteGenerator(methodGenerator, topLevelClass, 
        interfaze);
    }
  }

  protected void addSelectByPrimaryKeyMethod(TopLevelClass topLevelClass, Interface interfaze)
  {
    if (this.introspectedTable.getRules().generateSelectByPrimaryKey()) {
      AbstractDAOElementGenerator methodGenerator = new SelectByPrimaryKeyMethodGenerator();
      initializeAndExecuteGenerator(methodGenerator, topLevelClass, 
        interfaze);
    }
  }

  protected void addUpdateByExampleParmsInnerclass(TopLevelClass topLevelClass, Interface interfaze)
  {
    Rules rules = this.introspectedTable.getRules();
    if ((rules.generateUpdateByExampleSelective()) || 
      (rules.generateUpdateByExampleWithBLOBs()) || 
      (rules.generateUpdateByExampleWithoutBLOBs())) {
      AbstractDAOElementGenerator methodGenerator = new UpdateByExampleParmsInnerclassGenerator();
      initializeAndExecuteGenerator(methodGenerator, topLevelClass, 
        interfaze);
    }
  }

  protected void addUpdateByExampleSelectiveMethod(TopLevelClass topLevelClass, Interface interfaze)
  {
    if (this.introspectedTable.getRules().generateUpdateByExampleSelective()) {
      AbstractDAOElementGenerator methodGenerator = new UpdateByExampleSelectiveMethodGenerator();
      initializeAndExecuteGenerator(methodGenerator, topLevelClass, 
        interfaze);
    }
  }

  protected void addUpdateByExampleWithBLOBsMethod(TopLevelClass topLevelClass, Interface interfaze)
  {
    if (this.introspectedTable.getRules().generateUpdateByExampleWithBLOBs()) {
      AbstractDAOElementGenerator methodGenerator = new UpdateByExampleWithBLOBsMethodGenerator();
      initializeAndExecuteGenerator(methodGenerator, topLevelClass, 
        interfaze);
    }
  }

  protected void addUpdateByExampleWithoutBLOBsMethod(TopLevelClass topLevelClass, Interface interfaze)
  {
    if (this.introspectedTable.getRules().generateUpdateByExampleWithoutBLOBs()) {
      AbstractDAOElementGenerator methodGenerator = new UpdateByExampleWithoutBLOBsMethodGenerator();
      initializeAndExecuteGenerator(methodGenerator, topLevelClass, 
        interfaze);
    }
  }

  protected void addUpdateByPrimaryKeySelectiveMethod(TopLevelClass topLevelClass, Interface interfaze)
  {
    if (this.introspectedTable.getRules().generateUpdateByPrimaryKeySelective()) {
      AbstractDAOElementGenerator methodGenerator = new UpdateByPrimaryKeySelectiveMethodGenerator();
      initializeAndExecuteGenerator(methodGenerator, topLevelClass, 
        interfaze);
    }
  }

  protected void addUpdateByPrimaryKeyWithBLOBsMethod(TopLevelClass topLevelClass, Interface interfaze)
  {
    if (this.introspectedTable.getRules().generateUpdateByPrimaryKeyWithBLOBs()) {
      AbstractDAOElementGenerator methodGenerator = new UpdateByPrimaryKeyWithBLOBsMethodGenerator();
      initializeAndExecuteGenerator(methodGenerator, topLevelClass, 
        interfaze);
    }
  }

  protected void addUpdateByPrimaryKeyWithoutBLOBsMethod(TopLevelClass topLevelClass, Interface interfaze)
  {
    if (this.introspectedTable.getRules()
      .generateUpdateByPrimaryKeyWithoutBLOBs()) {
      AbstractDAOElementGenerator methodGenerator = new UpdateByPrimaryKeyWithoutBLOBsMethodGenerator();
      initializeAndExecuteGenerator(methodGenerator, topLevelClass, 
        interfaze);
    }
  }

  protected void initializeAndExecuteGenerator(AbstractDAOElementGenerator methodGenerator, TopLevelClass topLevelClass, Interface interfaze)
  {
    methodGenerator.setDAOTemplate(this.daoTemplate);
    methodGenerator.setContext(this.context);
    methodGenerator.setIntrospectedTable(this.introspectedTable);
    methodGenerator.setProgressCallback(this.progressCallback);
    methodGenerator.setWarnings(this.warnings);
    methodGenerator.addImplementationElements(topLevelClass);
    methodGenerator.addInterfaceElements(interfaze);
  }

  public AbstractXmlGenerator getMatchedXMLGenerator()
  {
    return null;
  }
}