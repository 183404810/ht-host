package ht.msc.mybatis.plugin;

import java.util.ArrayList;
import java.util.List;


public class BaseRecordGenerator extends AbstractJavaGenerator
{
  public List<CompilationUnit> getCompilationUnits()
  {
    FullyQualifiedTable table = this.introspectedTable.getFullyQualifiedTable();
    this.progressCallback.startTask(Messages.getString(
      "Progress.8", table.toString()));
    Plugin plugins = this.context.getPlugins();
    CommentGenerator commentGenerator = this.context.getCommentGenerator();

    TopLevelClass topLevelClass = new TopLevelClass(this.introspectedTable
      .getBaseRecordType());
    topLevelClass.setVisibility(JavaVisibility.PUBLIC);
    commentGenerator.addJavaFileComment(topLevelClass);

    FullyQualifiedJavaType superClass = getSuperClass();
    if (superClass != null) {
      topLevelClass.setSuperClass(superClass);
      topLevelClass.addImportedType(superClass);
    }
    List<IntrospectedColumn> introspectedColumns;
    if (includePrimaryKeyColumns())
    {
      if (includeBLOBColumns())
        introspectedColumns = this.introspectedTable.getAllColumns();
      else
        introspectedColumns = this.introspectedTable.getNonBLOBColumns();
    }
    else
    {
      if (includeBLOBColumns())
        introspectedColumns = this.introspectedTable
          .getNonPrimaryKeyColumns();
      else {
        introspectedColumns = this.introspectedTable.getBaseColumns();
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

        method = getJavaBeansSetter(introspectedColumn);
        if (plugins.modelSetterMethodGenerated(method, topLevelClass, 
          introspectedColumn, this.introspectedTable, 
          Plugin.ModelClassType.BASE_RECORD)) {
          topLevelClass.addMethod(method);
        }
      }
    }
    List answer = new ArrayList();
    if (this.context.getPlugins().modelBaseRecordClassGenerated(
      topLevelClass, this.introspectedTable)) {
      answer.add(topLevelClass);
    }
    return answer;
  }

  private FullyQualifiedJavaType getSuperClass()
  {
    FullyQualifiedJavaType superClass;
    if (this.introspectedTable.getRules().generatePrimaryKeyClass()) {
      superClass = new FullyQualifiedJavaType(this.introspectedTable
        .getPrimaryKeyType());
    } else {
      String rootClass = getRootClass();
      if (rootClass != null)
        superClass = new FullyQualifiedJavaType(rootClass);
      else {
        superClass = null;
      }
    }

    return superClass;
  }

  private boolean includePrimaryKeyColumns()
  {
    return (!this.introspectedTable.getRules().generatePrimaryKeyClass()) && 
      (this.introspectedTable.hasPrimaryKeyColumns());
  }

  private boolean includeBLOBColumns()
  {
    return (!this.introspectedTable.getRules().generateRecordWithBLOBsClass()) && 
      (this.introspectedTable.hasBLOBColumns());
  }
}