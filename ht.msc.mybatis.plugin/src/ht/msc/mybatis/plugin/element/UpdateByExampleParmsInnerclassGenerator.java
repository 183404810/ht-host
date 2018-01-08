package ht.msc.mybatis.plugin.element;

import ht.msc.mybatis.plugin.AbstractDAOElementGenerator;
import ht.msc.mybatis.plugin.Field;
import ht.msc.mybatis.plugin.FullyQualifiedJavaType;
import ht.msc.mybatis.plugin.InnerClass;
import ht.msc.mybatis.plugin.Interface;
import ht.msc.mybatis.plugin.JavaVisibility;
import ht.msc.mybatis.plugin.Method;
import ht.msc.mybatis.plugin.Parameter;
import ht.msc.mybatis.plugin.TopLevelClass;


public class UpdateByExampleParmsInnerclassGenerator extends AbstractDAOElementGenerator
{
  public void addImplementationElements(TopLevelClass topLevelClass)
  {
    topLevelClass.addImportedType(new FullyQualifiedJavaType(
      this.introspectedTable.getExampleType()));

    InnerClass innerClass = new InnerClass(new FullyQualifiedJavaType(
      "UpdateByExampleParms"));
    innerClass.setVisibility(JavaVisibility.PROTECTED);
    innerClass.setStatic(true);
    innerClass.setSuperClass(this.introspectedTable.getExampleType());
    this.context.getCommentGenerator().addClassComment(innerClass, 
      this.introspectedTable);

    Method method = new Method();
    method.setConstructor(true);
    method.setVisibility(JavaVisibility.PUBLIC);
    method.setName(innerClass.getType().getShortName());
    method.addParameter(new Parameter(
      FullyQualifiedJavaType.getObjectInstance(), "record"));
    method.addParameter(new Parameter(new FullyQualifiedJavaType(
      this.introspectedTable.getExampleType()), "example"));
    method.addBodyLine("super(example);");
    method.addBodyLine("this.record = record;");
    innerClass.addMethod(method);

    Field field = new Field();
    field.setVisibility(JavaVisibility.PRIVATE);
    field.setType(FullyQualifiedJavaType.getObjectInstance());
    field.setName("record");
    innerClass.addField(field);

    method = new Method();
    method.setVisibility(JavaVisibility.PUBLIC);
    method.setReturnType(FullyQualifiedJavaType.getObjectInstance());
    method.setName("getRecord");
    method.addBodyLine("return record;");
    innerClass.addMethod(method);

    topLevelClass.addInnerClass(innerClass);
  }

  public void addInterfaceElements(Interface interfaze)
  {
  }
}