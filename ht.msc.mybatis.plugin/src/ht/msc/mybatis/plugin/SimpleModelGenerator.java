package ht.msc.mybatis.plugin;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class SimpleModelGenerator extends AbstractJavaGenerator
{
  public List<CompilationUnit> getCompilationUnits()
  {
    FullyQualifiedTable table = this.introspectedTable.getFullyQualifiedTable();
    this.progressCallback.startTask(Messages.getString("Progress.8", table.toString()));
    Plugin plugins = this.context.getPlugins();
    CommentGenerator commentGenerator = this.context.getCommentGenerator();

    FullyQualifiedJavaType type = new FullyQualifiedJavaType(
      this.introspectedTable.getBaseRecordType());
    TopLevelClass topLevelClass = new TopLevelClass(type);
    topLevelClass.setVisibility(JavaVisibility.PUBLIC);

    commentGenerator.addClassComment(topLevelClass);

    FullyQualifiedJavaType superClass = getSuperClass();
    if (superClass != null) {
      topLevelClass.setSuperClass(superClass);
      topLevelClass.addImportedType(superClass);
    }

    String rootInterface = this.introspectedTable
      .getTableConfigurationProperty("rootInterface");
    if (!StringUtility.stringHasValue(rootInterface)) {
      rootInterface = this.context.getJavaModelGeneratorConfiguration()
        .getProperty("rootInterface");
    }

    if (StringUtility.stringHasValue(rootInterface)) {
      FullyQualifiedJavaType fqjt = new FullyQualifiedJavaType(
        rootInterface);
      topLevelClass.addSuperInterface(fqjt);
      topLevelClass.addImportedType(fqjt);
    }

    List<IntrospectedColumn> introspectedColumns = this.introspectedTable.getAllColumns();

    if (this.introspectedTable.isConstructorBased()) {
      addParameterizedConstructor(topLevelClass);

      if (!this.introspectedTable.isImmutable()) {
        addDefaultConstructor(topLevelClass);
      }
    }

    String rootClass = getRootClass();
    for (IntrospectedColumn introspectedColumn : introspectedColumns)
    {
      if (!RootClassInfo.getInstance(rootClass, this.warnings)
        .containsProperty(introspectedColumn))
      {
        Field field = getJavaBeansField(introspectedColumn);
        if (plugins.modelFieldGenerated(field, topLevelClass, 
          introspectedColumn, this.introspectedTable, 
          Plugin.ModelClassType.BASE_RECORD)) {
          topLevelClass.addField(field);
          topLevelClass.addImportedType(field.getType());
        }

        Method method = getJavaBeansGetter(introspectedColumn);
        if (plugins.modelGetterMethodGenerated(method, topLevelClass, 
          introspectedColumn, this.introspectedTable, 
          Plugin.ModelClassType.BASE_RECORD)) {
          topLevelClass.addMethod(method);
        }

        if (!this.introspectedTable.isImmutable()) {
          method = getJavaBeansSetter(introspectedColumn);
          if (plugins.modelSetterMethodGenerated(method, topLevelClass, 
            introspectedColumn, this.introspectedTable, 
            Plugin.ModelClassType.BASE_RECORD)) {
            topLevelClass.addMethod(method);
          }
        }
      }
    }
    List answer = new ArrayList();
    if (this.context.getPlugins().modelBaseRecordClassGenerated(topLevelClass, 
      this.introspectedTable)) {
      answer.add(topLevelClass);
    }
    return answer;
  }

  private FullyQualifiedJavaType getSuperClass()
  {
    String rootClass = getRootClass();
    FullyQualifiedJavaType superClass;
    if (rootClass != null)
      superClass = new FullyQualifiedJavaType(rootClass);
    else {
      superClass = null;
    }

    return superClass;
  }

  private void addParameterizedConstructor(TopLevelClass topLevelClass) {
    Method method = new Method();
    method.setVisibility(JavaVisibility.PUBLIC);
    method.setConstructor(true);
    method.setName(topLevelClass.getType().getShortName());
    this.context.getCommentGenerator().addGeneralMethodComment(method, 
      this.introspectedTable);

    List<IntrospectedColumn> constructorColumns = this.introspectedTable
      .getAllColumns();

    for (IntrospectedColumn introspectedColumn : constructorColumns) {
      method.addParameter(new Parameter(introspectedColumn
        .getFullyQualifiedJavaType(), introspectedColumn
        .getJavaProperty()));
    }

    StringBuilder sb = new StringBuilder();
    Iterator localIterator2;
    if (this.introspectedTable.getRules().generatePrimaryKeyClass()) {
      boolean comma = false;
      sb.append("super(");

      localIterator2 = this.introspectedTable
        .getPrimaryKeyColumns().iterator();

      while (localIterator2.hasNext()) {
        IntrospectedColumn introspectedColumn = (IntrospectedColumn)localIterator2.next();
        if (comma)
          sb.append(", ");
        else {
          comma = true;
        }
        sb.append(introspectedColumn.getJavaProperty());
      }
      sb.append(");");
      method.addBodyLine(sb.toString());
    }

    List<IntrospectedColumn> introspectedColumns = this.introspectedTable.getAllColumns();

    for (IntrospectedColumn introspectedColumn : introspectedColumns) {
      sb.setLength(0);
      sb.append("this.");
      sb.append(introspectedColumn.getJavaProperty());
      sb.append(" = ");
      sb.append(introspectedColumn.getJavaProperty());
      sb.append(';');
      method.addBodyLine(sb.toString());
    }

    topLevelClass.addMethod(method);
  }
}