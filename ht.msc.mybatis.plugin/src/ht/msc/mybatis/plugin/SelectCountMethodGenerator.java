package ht.msc.mybatis.plugin;


public class SelectCountMethodGenerator extends AbstractJavaMapperMethodGenerator
{
  public void addInterfaceElements(Interface interfaze)
  {
    Method method = new Method();
    method.setVisibility(JavaVisibility.PUBLIC);

    method.setReturnType(FullyQualifiedJavaType.getIntInstance());

    method.setName(this.introspectedTable.getSelectCount());

    addMapperAnnotations(interfaze, method);

    this.context.getCommentGenerator().addGeneralMethodComment(method, 
      this.introspectedTable);

    if (this.context.getPlugins().clientSelectCountMethodGenerated(
      method, interfaze, this.introspectedTable))
      interfaze.addMethod(method);
  }

  public void addMapperAnnotations(Interface interfaze, Method method)
  {
  }
}