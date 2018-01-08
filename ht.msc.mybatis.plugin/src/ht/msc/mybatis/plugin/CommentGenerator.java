package ht.msc.mybatis.plugin;

import java.util.Properties;


public abstract interface CommentGenerator
{
  public abstract void addConfigurationProperties(Properties paramProperties);

  public abstract void addFieldComment(Field paramField, IntrospectedTable paramIntrospectedTable, IntrospectedColumn paramIntrospectedColumn);

  public abstract void addFieldComment(Field paramField, IntrospectedTable paramIntrospectedTable);

  public abstract void addClassComment(JavaElement paramJavaElement);

  public abstract void addClassComment(InnerClass paramInnerClass, IntrospectedTable paramIntrospectedTable);

  public abstract void addClassComment(InnerClass paramInnerClass, IntrospectedTable paramIntrospectedTable, boolean paramBoolean);

  public abstract void addEnumComment(InnerEnum paramInnerEnum, IntrospectedTable paramIntrospectedTable);

  public abstract void addGetterComment(Method paramMethod, IntrospectedTable paramIntrospectedTable, IntrospectedColumn paramIntrospectedColumn);

  public abstract void addSetterComment(Method paramMethod, IntrospectedTable paramIntrospectedTable, IntrospectedColumn paramIntrospectedColumn);

  public abstract void addGeneralMethodComment(Method paramMethod, IntrospectedTable paramIntrospectedTable);

  public abstract void addJavaFileComment(CompilationUnit paramCompilationUnit);

  public abstract void addComment(XmlElement paramXmlElement);

  public abstract void addComment(XmlElement paramXmlElement, boolean paramBoolean);

  public abstract void addRootComment(XmlElement paramXmlElement);
}