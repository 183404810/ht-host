package ht.msc.mybatis.plugin;

import java.util.Set;
import java.util.TreeSet;


public class SelectByPageMethodGenerator extends AbstractJavaMapperMethodGenerator
{
  private Context context;

  public SelectByPageMethodGenerator(Context context)
  {
    this.context = context;
  }

  public void addInterfaceElements(Interface interfaze)
  {
    Set importedTypes = new TreeSet();
    importedTypes.add(FullyQualifiedJavaType.getNewListInstance());
    importedTypes.add(FullyQualifiedJavaType.getNewMapInstance());
    importedTypes.add(FullyQualifiedJavaType.getAnnotateParam());
    importedTypes.add(new FullyQualifiedJavaType("com.yougou.logistics.base.common.utils.SimplePage"));
    String interfaceName = this.context.getJavaClientGeneratorConfiguration().getInterfaceExtendSupInterface();
    if ((interfaceName != null) && (!"".equals(interfaceName))) {
      importedTypes.add(new FullyQualifiedJavaType(interfaceName));
    }

    Method method = new Method();
    method.setVisibility(JavaVisibility.PUBLIC);

    FullyQualifiedJavaType returnType = 
      FullyQualifiedJavaType.getNewListInstance();
    FullyQualifiedJavaType listType;
    if (this.introspectedTable.getRules().generateRecordWithBLOBsClass()) {
      listType = new FullyQualifiedJavaType(this.introspectedTable
        .getRecordWithBLOBsType());
    }
    else {
      listType = new FullyQualifiedJavaType(this.introspectedTable
        .getBaseRecordType());
    }

    importedTypes.add(listType);
    returnType.addTypeArgument(listType);
    method.setReturnType(returnType);
    method.setName(this.introspectedTable
      .getSelectByPage());

    FullyQualifiedJavaType parameterType = new FullyQualifiedJavaType("SimplePage");
    method.addParameter(new Parameter(parameterType, 
      "page", "@Param(\"page\")"));
    parameterType = new FullyQualifiedJavaType("String");
    method.addParameter(new Parameter(parameterType, 
      "orderByField", "@Param(\"orderByField\")"));
    method.addParameter(new Parameter(parameterType, 
      "orderBy", "@Param(\"orderBy\")"));

    this.context.getCommentGenerator().addGeneralMethodComment(method, 
      this.introspectedTable);

    addMapperAnnotations(interfaze, method);

    interfaze.addImportedTypes(importedTypes);
    interfaze.addMethod(method);
  }

  public void addMapperAnnotations(Interface interfaze, Method method)
  {
  }
}