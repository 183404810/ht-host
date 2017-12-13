package ht.msc.mybatis.plugin;

import java.util.List;
import java.util.Properties;

public abstract class AbstractJavaGenerator extends AbstractGenerator
{
  public abstract List<CompilationUnit> getCompilationUnits();

  public static Method getGetter(Field field)
  {
    Method method = new Method();
    method.setName(JavaBeansUtil.getGetterMethodName(field.getName(), field
      .getType()));
    method.setReturnType(field.getType());
    method.setVisibility(JavaVisibility.PUBLIC);
    StringBuilder sb = new StringBuilder();
    sb.append("return ");
    sb.append(field.getName());
    sb.append(';');
    method.addBodyLine(sb.toString());
    return method;
  }

  public Method getJavaBeansGetter(IntrospectedColumn introspectedColumn) {
    FullyQualifiedJavaType fqjt = introspectedColumn
      .getFullyQualifiedJavaType();
    String property = introspectedColumn.getJavaProperty();

    Method method = new Method();
    method.setVisibility(JavaVisibility.PUBLIC);
    method.setReturnType(fqjt);
    method.setName(JavaBeansUtil.getGetterMethodName(property, fqjt));
    this.context.getCommentGenerator().addGetterComment(method, 
      this.introspectedTable, introspectedColumn);

    StringBuilder sb = new StringBuilder();
    sb.append("return ");
    sb.append(property);
    sb.append(';');
    method.addBodyLine(sb.toString());

    return method;
  }

  public Field getJavaBeansField(IntrospectedColumn introspectedColumn) {
    FullyQualifiedJavaType fqjt = introspectedColumn
      .getFullyQualifiedJavaType();
    String property = introspectedColumn.getJavaProperty();

    Field field = new Field();
    field.setVisibility(JavaVisibility.PRIVATE);
    field.setType(fqjt);
    field.setName(property);
    this.context.getCommentGenerator().addFieldComment(field, 
      this.introspectedTable, introspectedColumn);

    return field;
  }

  public Method getJavaBeansSetter(IntrospectedColumn introspectedColumn) {
    FullyQualifiedJavaType fqjt = introspectedColumn
      .getFullyQualifiedJavaType();
    String property = introspectedColumn.getJavaProperty();

    Method method = new Method();
    method.setVisibility(JavaVisibility.PUBLIC);
    method.setName(JavaBeansUtil.getSetterMethodName(property));
    method.addParameter(new Parameter(fqjt, property));
    this.context.getCommentGenerator().addSetterComment(method, 
      this.introspectedTable, introspectedColumn);

    StringBuilder sb = new StringBuilder();
    if ((isTrimStringsEnabled()) && (introspectedColumn.isStringColumn())) {
      sb.append("this.");
      sb.append(property);
      sb.append(" = ");
      sb.append(property);
      sb.append(" == null ? null : ");
      sb.append(property);
      sb.append(".trim();");
      method.addBodyLine(sb.toString());
    } else {
      sb.append("this.");
      sb.append(property);
      sb.append(" = ");
      sb.append(property);
      sb.append(';');
      method.addBodyLine(sb.toString());
    }

    return method;
  }

  public boolean isTrimStringsEnabled() {
    Properties properties = this.context
      .getJavaModelGeneratorConfiguration().getProperties();
    boolean rc = StringUtility.isTrue(properties
      .getProperty("trimStrings"));
    return rc;
  }

  public String getRootClass() {
    String rootClass = this.introspectedTable
      .getTableConfigurationProperty("rootClass");
    if (rootClass == null) {
      Properties properties = this.context
        .getJavaModelGeneratorConfiguration().getProperties();
      rootClass = properties.getProperty("rootClass");
    }

    return rootClass;
  }

  protected void addDefaultConstructor(TopLevelClass topLevelClass) {
    Method method = new Method();
    method.setVisibility(JavaVisibility.PUBLIC);
    method.setConstructor(true);
    method.setName(topLevelClass.getType().getShortName());
    method.addBodyLine("super();");
    this.context.getCommentGenerator().addGeneralMethodComment(method, this.introspectedTable);
    topLevelClass.addMethod(method);
  }
}