package ht.msc.mybatis.plugin;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;


public abstract class AbstractDAOTemplate
{
  private List<FullyQualifiedJavaType> interfaceImports;
  private List<FullyQualifiedJavaType> implementationImports;
  private FullyQualifiedJavaType superClass;
  private List<FullyQualifiedJavaType> checkedExceptions;
  private List<Field> fields;
  private List<Method> methods;
  private Method constructorTemplate;
  private String deleteMethodTemplate;
  private String insertMethodTemplate;
  private String updateMethodTemplate;
  private String queryForObjectMethodTemplate;
  private String queryForListMethodTemplate;
  private boolean configured;

  public AbstractDAOTemplate()
  {
    this.interfaceImports = new ArrayList();
    this.implementationImports = new ArrayList();
    this.fields = new ArrayList();
    this.methods = new ArrayList();
    this.checkedExceptions = new ArrayList();
    this.configured = false;
  }

  public final Method getConstructorClone(CommentGenerator commentGenerator, FullyQualifiedJavaType type, IntrospectedTable introspectedTable)
  {
    configure();
    Method answer = new Method();
    answer.setConstructor(true);
    answer.setName(type.getShortName());
    answer.setVisibility(this.constructorTemplate.getVisibility());
    for (Parameter parm : this.constructorTemplate.getParameters()) {
      answer.addParameter(parm);
    }

    for (String bodyLine : this.constructorTemplate.getBodyLines()) {
      answer.addBodyLine(bodyLine);
    }

    for (FullyQualifiedJavaType fqjt : this.constructorTemplate.getExceptions()) {
      answer.addException(fqjt);
    }

    commentGenerator.addGeneralMethodComment(answer, introspectedTable);

    return answer;
  }

  public final String getDeleteMethod(String sqlMapNamespace, String statementId, String parameter)
  {
    configure();
    String answer = MessageFormat.format(this.deleteMethodTemplate, 
      new Object[] { sqlMapNamespace, statementId, parameter });

    return answer;
  }

  public final List<FullyQualifiedJavaType> getInterfaceImports() {
    configure();
    return this.interfaceImports;
  }

  public final List<FullyQualifiedJavaType> getImplementationImports() {
    configure();
    return this.implementationImports;
  }

  public final String getInsertMethod(String sqlMapNamespace, String statementId, String parameter)
  {
    configure();
    String answer = MessageFormat.format(this.insertMethodTemplate, 
      new Object[] { sqlMapNamespace, statementId, parameter });

    return answer;
  }

  public final String getQueryForListMethod(String sqlMapNamespace, String statementId, String parameter)
  {
    configure();
    String answer = MessageFormat.format(this.queryForListMethodTemplate, 
      new Object[] { sqlMapNamespace, statementId, parameter });

    return answer;
  }

  public final String getQueryForObjectMethod(String sqlMapNamespace, String statementId, String parameter)
  {
    configure();
    String answer = MessageFormat.format(this.queryForObjectMethodTemplate, 
      new Object[] { sqlMapNamespace, statementId, parameter });

    return answer;
  }

  public final FullyQualifiedJavaType getSuperClass() {
    configure();
    return this.superClass;
  }

  public final String getUpdateMethod(String sqlMapNamespace, String statementId, String parameter)
  {
    configure();
    String answer = MessageFormat.format(this.updateMethodTemplate, 
      new Object[] { sqlMapNamespace, statementId, parameter });

    return answer;
  }

  public final List<FullyQualifiedJavaType> getCheckedExceptions() {
    configure();
    return this.checkedExceptions;
  }

  public final List<Field> getFieldClones(CommentGenerator commentGenerator, IntrospectedTable introspectedTable)
  {
    configure();
    List answer = new ArrayList();
    for (Field oldField : this.fields) {
      Field field = new Field();

      field.setInitializationString(oldField.getInitializationString());
      field.setFinal(oldField.isFinal());
      field.setStatic(oldField.isStatic());
      field.setName(oldField.getName());
      field.setType(oldField.getType());
      field.setVisibility(oldField.getVisibility());
      commentGenerator.addFieldComment(field, introspectedTable);
      answer.add(field);
    }

    return answer;
  }

  public final List<Method> getMethodClones(CommentGenerator commentGenerator, IntrospectedTable introspectedTable)
  {
    configure();
    List answer = new ArrayList();
    for (Method oldMethod : this.methods) {
      Method method = new Method();

      for (String bodyLine : oldMethod.getBodyLines()) {
        method.addBodyLine(bodyLine);
      }

      for (FullyQualifiedJavaType fqjt : oldMethod.getExceptions()) {
        method.addException(fqjt);
      }

      for (Parameter parm : oldMethod.getParameters()) {
        method.addParameter(parm);
      }

      method.setConstructor(oldMethod.isConstructor());
      method.setFinal(oldMethod.isFinal());
      method.setStatic(oldMethod.isStatic());
      method.setName(oldMethod.getName());
      method.setReturnType(oldMethod.getReturnType());
      method.setVisibility(oldMethod.getVisibility());

      commentGenerator.addGeneralMethodComment(method, introspectedTable);

      answer.add(method);
    }

    return answer;
  }

  protected void setConstructorTemplate(Method constructorTemplate) {
    this.constructorTemplate = constructorTemplate;
  }

  protected void setDeleteMethodTemplate(String deleteMethodTemplate) {
    this.deleteMethodTemplate = deleteMethodTemplate;
  }

  protected void addField(Field field) {
    this.fields.add(field);
  }

  protected void setInsertMethodTemplate(String insertMethodTemplate) {
    this.insertMethodTemplate = insertMethodTemplate;
  }

  protected void addMethod(Method method) {
    this.methods.add(method);
  }

  protected void setQueryForListMethodTemplate(String queryForListMethodTemplate)
  {
    this.queryForListMethodTemplate = queryForListMethodTemplate;
  }

  protected void setQueryForObjectMethodTemplate(String queryForObjectMethodTemplate)
  {
    this.queryForObjectMethodTemplate = queryForObjectMethodTemplate;
  }

  protected void setSuperClass(FullyQualifiedJavaType superClass) {
    this.superClass = superClass;
  }

  protected void setUpdateMethodTemplate(String updateMethodTemplate) {
    this.updateMethodTemplate = updateMethodTemplate;
  }

  protected void addInterfaceImport(FullyQualifiedJavaType type) {
    this.interfaceImports.add(type);
  }

  protected void addImplementationImport(FullyQualifiedJavaType type) {
    this.implementationImports.add(type);
  }

  protected void addCheckedException(FullyQualifiedJavaType type) {
    this.checkedExceptions.add(type);
  }

  private void configure()
  {
    if (!this.configured) {
      configureCheckedExceptions();
      configureConstructorTemplate();
      configureDeleteMethodTemplate();
      configureFields();
      configureImplementationImports();
      configureInsertMethodTemplate();
      configureInterfaceImports();
      configureMethods();
      configureQueryForListMethodTemplate();
      configureQueryForObjectMethodTemplate();
      configureSuperClass();
      configureUpdateMethodTemplate();
      this.configured = true;
    }
  }

  protected void configureCheckedExceptions()
  {
  }

  protected void configureFields()
  {
  }

  protected void configureImplementationImports()
  {
  }

  protected void configureInterfaceImports()
  {
  }

  protected void configureMethods()
  {
  }

  protected void configureSuperClass()
  {
  }

  protected abstract void configureConstructorTemplate();

  protected abstract void configureInsertMethodTemplate();

  protected abstract void configureQueryForListMethodTemplate();

  protected abstract void configureQueryForObjectMethodTemplate();

  protected abstract void configureUpdateMethodTemplate();

  protected abstract void configureDeleteMethodTemplate();
}