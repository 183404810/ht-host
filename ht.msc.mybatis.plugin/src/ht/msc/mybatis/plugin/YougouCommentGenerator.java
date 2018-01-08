package ht.msc.mybatis.plugin;

import java.text.SimpleDateFormat;
import java.util.Date;

public class YougouCommentGenerator extends DefaultCommentGenerator
{
  private static SimpleDateFormat sdf = new SimpleDateFormat(
    "yyyy-MM-dd HH:mm:ss");

  public void addClassComment(JavaElement javaElement)
  {
    javaElement.addJavaDocLine("/**");
    javaElement.addJavaDocLine(" * Description: 请写出类的用途");
    javaElement.addJavaDocLine(" * All rights Reserved, Designed By BeLLE");
    javaElement.addJavaDocLine(" * Copyright:   Copyright(C) 2014-2015");
    javaElement.addJavaDocLine(" * Company:     Wonhigh.");
    javaElement.addJavaDocLine(" * @author:     " + System.getProperty("user.name"));
    javaElement.addJavaDocLine(" * @date:  " + sdf.format(new Date()));
    javaElement.addJavaDocLine(" * @version " + Context.getCodeVersion());
    javaElement.addJavaDocLine(" */");
  }

  public void addJavaFileComment(CompilationUnit compilationUnit)
  {
    compilationUnit.addFileCommentLine("/**");
    compilationUnit.addFileCommentLine(" * Description: 类名 " + compilationUnit.getType().getFullyQualifiedName());
    compilationUnit.addFileCommentLine(" * All rights Reserved, Designed By BeLLE");
    compilationUnit.addFileCommentLine(" * Copyright:   Copyright(C) 2014-2015");
    compilationUnit.addFileCommentLine(" * Company:     Wonhigh.");
    compilationUnit.addFileCommentLine(" * @author:     " + System.getProperty("user.name"));
    compilationUnit.addFileCommentLine(" * @date:  " + sdf.format(new Date()));
    compilationUnit.addFileCommentLine(" * @version " + Context.getCodeVersion());
    compilationUnit.addFileCommentLine(" */");
  }

  public static String addJSFileComment()
  {
    StringBuffer sbf = new StringBuffer();
    sbf.append("/**\n");
    sbf.append(" * Description: ${d}\n");
    sbf.append(" * All rights Reserved, Designed By BeLLE\n");
    sbf.append(" * Copyright:   Copyright(C) 2014-2015\n");
    sbf.append(" * Company:     Wonhigh.\n");
    sbf.append(" * @author:     " + System.getProperty("user.name") + "\n");
    sbf.append(" * @date:  " + sdf.format(new Date()) + "\n");
    sbf.append(" * @version " + Context.getCodeVersion() + "\n");
    sbf.append(" */");
    return sbf.toString();
  }

  public void addFieldComment(Field field, IntrospectedTable introspectedTable, IntrospectedColumn introspectedColumn)
  {
    StringBuilder sb = new StringBuilder();

    field.addJavaDocLine("/**");
    sb.append(" * " + (introspectedColumn.getRemarks() == null ? "" : introspectedColumn.getRemarks()));
    field.addJavaDocLine(sb.toString());

    field.addJavaDocLine(" */");
  }

  public void addGeneralMethodComment(Method method, IntrospectedTable introspectedTable)
  {
  }

  public void addGetterComment(Method method, IntrospectedTable introspectedTable, IntrospectedColumn introspectedColumn)
  {
    addGetterComment(method, introspectedTable, introspectedColumn, "");
  }

  public void addGetterComment(Method method, IntrospectedTable introspectedTable, IntrospectedColumn introspectedColumn, String comment)
  {
    StringBuilder sb = new StringBuilder();

    method.addJavaDocLine("/**");
    if ((comment != null) && ("".equals(comment))) {
      sb.append(" * ");
      sb.append(comment);
      method.addJavaDocLine(sb.toString());
      sb.setLength(0);
    }
    sb.append(" * {@linkplain #");
    sb.append(introspectedColumn.getJavaProperty());
    sb.append("}");
    method.addJavaDocLine(sb.toString());

    method.addJavaDocLine(" *");

    sb.setLength(0);
    sb.append(" * @return the value of ");
    sb.append(introspectedTable.getFullyQualifiedTable());
    sb.append('.');
    sb.append(introspectedColumn.getActualColumnName());
    method.addJavaDocLine(sb.toString());

    method.addJavaDocLine(" */");
  }

  public void addSetterComment(Method method, IntrospectedTable introspectedTable, IntrospectedColumn introspectedColumn)
  {
    addSetterComment(method, introspectedTable, introspectedColumn, "");
  }

  public void addComment(XmlElement xmlElement, boolean suppressComments)
  {
    if (suppressComments) {
      return;
    }
    xmlElement.addElement(new TextElement("<!--"));
    StringBuilder sb = new StringBuilder();
    sb.append("  WARNING -");
    sb.append("  SQL是带逻辑性的表达式，数据权限目前只支持一级SQL，无法支持SQL嵌套或union、union all语句,推荐语句见如下,详细介绍见wiki ");
    xmlElement.addElement(new TextElement(sb.toString()));
    xmlElement.addElement(new TextElement("  wiki:http://10.0.30.57:8090/pages/viewpage.action?pageId=5111962"));
    xmlElement.addElement(new TextElement("  e.g.:"));
    xmlElement.addElement(new TextElement("  select u.userId,u.userName from user u "));
    xmlElement.addElement(new TextElement("  inner join company c on (u.companyId=c.companyId) "));
    xmlElement.addElement(new TextElement("  where append_condition=? "));
    xmlElement.addElement(new TextElement("-->"));
  }

  public void addSetterComment(Method method, IntrospectedTable introspectedTable, IntrospectedColumn introspectedColumn, String comment)
  {
    StringBuilder sb = new StringBuilder();

    method.addJavaDocLine("/**");
    if ((comment != null) && ("".equals(comment))) {
      sb.append(" * ");
      sb.append(comment);
      method.addJavaDocLine(sb.toString());
      sb.setLength(0);
    }
    sb.append(" * {@linkplain #");
    sb.append(introspectedColumn.getJavaProperty());
    sb.append("}");
    method.addJavaDocLine(sb.toString());

    Parameter parm = (Parameter)method.getParameters().get(0);
    sb.setLength(0);
    sb.append(" * @param ");
    sb.append(parm.getName());
    sb.append(" the value for ");
    sb.append(introspectedTable.getFullyQualifiedTable());
    sb.append('.');
    sb.append(introspectedColumn.getActualColumnName());
    method.addJavaDocLine(sb.toString());

    method.addJavaDocLine(" */");
  }
}