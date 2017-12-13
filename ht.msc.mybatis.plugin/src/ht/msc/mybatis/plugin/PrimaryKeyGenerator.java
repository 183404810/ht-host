package ht.msc.mybatis.plugin;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class PrimaryKeyGenerator extends AbstractJavaGenerator
{
  public List<CompilationUnit> getCompilationUnits()
  {
    FullyQualifiedTable table = this.introspectedTable.getFullyQualifiedTable();
    this.progressCallback.startTask(Messages.getString(
      "Progress.7", table.toString()));
    Plugin plugins = this.context.getPlugins();
    CommentGenerator commentGenerator = this.context.getCommentGenerator();

    TopLevelClass topLevelClass = new TopLevelClass(this.introspectedTable
      .getPrimaryKeyType());
    topLevelClass.setVisibility(JavaVisibility.PUBLIC);
    commentGenerator.addJavaFileComment(topLevelClass);

    String rootClass = getRootClass();
    if (rootClass != null) {
      topLevelClass.setSuperClass(new FullyQualifiedJavaType(rootClass));
      topLevelClass.addImportedType(topLevelClass.getSuperClass());
    }

    Iterator localIterator = this.introspectedTable
      .getPrimaryKeyColumns().iterator();

    while (localIterator.hasNext()) {
      IntrospectedColumn introspectedColumn = (IntrospectedColumn)localIterator.next();

      if (!RootClassInfo.getInstance(rootClass, this.warnings)
        .containsProperty(introspectedColumn))
      {
        Field field = getJavaBeansField(introspectedColumn);
        if (plugins.modelFieldGenerated(field, topLevelClass, 
          introspectedColumn, this.introspectedTable, 
          Plugin.ModelClassType.PRIMARY_KEY)) {
          topLevelClass.addField(field);
          topLevelClass.addImportedType(field.getType());
        }

        Method method = getJavaBeansGetter(introspectedColumn);
        if (plugins.modelGetterMethodGenerated(method, topLevelClass, 
          introspectedColumn, this.introspectedTable, 
          Plugin.ModelClassType.PRIMARY_KEY)) {
          topLevelClass.addMethod(method);
        }

        method = getJavaBeansSetter(introspectedColumn);
        if (plugins.modelSetterMethodGenerated(method, topLevelClass, 
          introspectedColumn, this.introspectedTable, 
          Plugin.ModelClassType.PRIMARY_KEY)) {
          topLevelClass.addMethod(method);
        }
      }
    }
    List answer = new ArrayList();
    if (this.context.getPlugins().modelPrimaryKeyClassGenerated(
      topLevelClass, this.introspectedTable)) {
      answer.add(topLevelClass);
    }
    return answer;
  }
}