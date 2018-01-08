package ht.msc.mybatis.plugin;

import java.util.ArrayList;
import java.util.List;


public class YouGouControllerGenerator extends AbstractJavaGenerator
{
  public List<CompilationUnit> getCompilationUnits()
  {
    CommentGenerator commentGenerator = this.context.getCommentGenerator();

    YouGouServiceGeneratorConfiguration serviceConfig = this.context.getYouGouServiceGeneratorConfiguration();
    YouGouControllerGeneratorConfiguration contrllerConfig = this.context.getYouGouControllerGeneratorConfiguration();
    String modelName = this.introspectedTable.getFullyQualifiedTable().getDomainObjectName();

    TopLevelClass c = new TopLevelClass(new FullyQualifiedJavaType(this.introspectedTable.getMyBatis3JavaControllerType()));
    c.addImportedType(new FullyQualifiedJavaType("javax.annotation.Resource"));
    c.addImportedType(new FullyQualifiedJavaType("org.springframework.stereotype.Controller"));
    c.addImportedType(new FullyQualifiedJavaType("org.springframework.web.bind.annotation.RequestMapping"));
    c.addImportedType(new FullyQualifiedJavaType("com.belle.scm.common.base.service.BaseCrudService"));
    c.addImportedType(new FullyQualifiedJavaType(this.introspectedTable.getMyBatis3JavaServiceType()));
    c.setVisibility(JavaVisibility.PUBLIC);

    if (contrllerConfig.getExtendSupClassDoMain().length() > 0) {
      c.addImportedType(new FullyQualifiedJavaType(contrllerConfig.getExtendSupClass()));
      if (new Boolean(contrllerConfig.getEnableSupClassGenericity()).booleanValue()) {
        c.addImportedType(new FullyQualifiedJavaType(this.introspectedTable.getBaseRecordType()));
        c.setSuperClass(new FullyQualifiedJavaType(contrllerConfig.getExtendSupClassDoMain() + "<" + modelName + ">"));
      } else {
        c.setSuperClass(new FullyQualifiedJavaType(contrllerConfig.getExtendSupClassDoMain()));
      }
    }

    Field field = new Field();
    field.addAnnotation("@Resource");
    field.setVisibility(JavaVisibility.PRIVATE);
    field.setType(new FullyQualifiedJavaType(this.introspectedTable.getMyBatis3JavaServiceType()));
    String fieldName = firstCharToLower(this.introspectedTable.getMyBatis3JavaServiceType().substring(this.introspectedTable.getMyBatis3JavaServiceType().lastIndexOf(".") + 1));
    field.setName(fieldName);
    c.addField(field);

    c.addAnnotation("@Controller");

    String controllerName = this.introspectedTable.getMyBatis3JavaControllerInstanceName();
    c.addAnnotation("@RequestMapping(\"/" + controllerName + "\")");

    Method method = new Method();
    method.setVisibility(JavaVisibility.PUBLIC);
    method.addAnnotation("@Override");

    method.setName("init");
    method.setReturnType(new FullyQualifiedJavaType(serviceConfig.getInterFaceExtendSupInterfaceDoMain()));

    method.addBodyLine("return " + fieldName + ";");
    c.addMethod(method);

    commentGenerator.addClassComment(c);

    List answer = new ArrayList();
    answer.add(c);
    return answer;
  }

  private String firstCharToLower(String modelName)
  {
    if (modelName.length() > 0)
      modelName = new StringBuilder(String.valueOf(modelName.charAt(0))).toString().toLowerCase() + modelName.substring(1);
    else {
      Character.toLowerCase(modelName.charAt(0));
    }
    return modelName;
  }
}