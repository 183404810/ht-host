package ht.msc.mybatis.plugin;

import java.util.ArrayList;
import java.util.List;


public class YouGouServiceGenerator extends AbstractJavaGenerator
{
  public List<CompilationUnit> getCompilationUnits()
  {
    CommentGenerator commentGenerator = this.context.getCommentGenerator();

    FullyQualifiedJavaType type = new FullyQualifiedJavaType(
      this.introspectedTable.getMyBatis3JavaServiceType());
    Interface interfaze = new Interface(type);
    interfaze.setVisibility(JavaVisibility.PUBLIC);

    JavaClientGeneratorConfiguration clientConfig = this.context.getJavaClientGeneratorConfiguration();
    YouGouServiceGeneratorConfiguration serviceConfig = this.context.getYouGouServiceGeneratorConfiguration();

    String modelName = this.introspectedTable.getFullyQualifiedTable().getDomainObjectName();
    if (serviceConfig.getInterFaceExtendSupInterfaceDoMain().length() > 0)
    {
      interfaze.addImportedType(new FullyQualifiedJavaType(serviceConfig.getInterfaceExtendSupInterface()));
      String base = serviceConfig.getInterFaceExtendSupInterfaceDoMain();
      if (new Boolean(serviceConfig.getEnableInterfaceSupInterfaceGenericity()).booleanValue()) {
        interfaze.addImportedType(new FullyQualifiedJavaType(this.introspectedTable.getBaseRecordType()));
        interfaze.addSuperInterface(new FullyQualifiedJavaType(base + "<" + modelName + ">"));
      } else {
        interfaze.addSuperInterface(new FullyQualifiedJavaType(base));
      }
      commentGenerator.addClassComment(interfaze);
    }

    String dalpackagestr = type.toString();

    String Mybatis3JavaBaseCrudDaoType = "com.belle.scm.common.base.dal.BaseCrudDao";
    String Mybatis3JavaDaoType = dalpackagestr.substring(0, dalpackagestr.indexOf("service")) + "dao.dal." + modelName + "Dao";
    TopLevelClass c = new TopLevelClass(new FullyQualifiedJavaType(this.introspectedTable.getMyBatis3JavaServiceImplType()));
    c.addImportedType(new FullyQualifiedJavaType("javax.annotation.Resource"));
    c.addImportedType(new FullyQualifiedJavaType("com.belle.scm.common.base.service.BaseCrudServiceImpl"));
    c.addImportedType(new FullyQualifiedJavaType("org.springframework.stereotype.Service"));
    c.addImportedType(new FullyQualifiedJavaType(Mybatis3JavaBaseCrudDaoType));
    c.addImportedType(new FullyQualifiedJavaType(Mybatis3JavaDaoType));
    c.addImportedType(new FullyQualifiedJavaType(this.introspectedTable.getMyBatis3JavaServiceType()));

    if (clientConfig.getInterFaceExtendSupInterfaceDoMain().length() > 0) {
      String ServiceName = firstCharToLower(this.introspectedTable.getMyBatis3JavaServiceType().substring(this.introspectedTable.getMyBatis3JavaServiceType().lastIndexOf(".") + 1));
      c.addAnnotation("@Service(\"" + ServiceName + "\")");
      if (new Boolean(serviceConfig.getEnableSupClassGenericity()).booleanValue()) {
        c.addImportedType(new FullyQualifiedJavaType(this.introspectedTable.getBaseRecordType()));
        c.setSuperClass(new FullyQualifiedJavaType(serviceConfig.getExtendSupClassDoMain() + "<" + modelName + ">"));
      } else {
        c.setSuperClass(new FullyQualifiedJavaType(serviceConfig.getExtendSupClassDoMain()));
      }
      c.addSuperInterface(new FullyQualifiedJavaType(modelName + "Service"));
    }

    Field field = new Field();
    field.addAnnotation("@Resource");
    field.setVisibility(JavaVisibility.PRIVATE);
    field.setType(new FullyQualifiedJavaType(Mybatis3JavaDaoType));
    String fieldName = firstCharToLower(Mybatis3JavaDaoType.substring(Mybatis3JavaDaoType.lastIndexOf(".") + 1));

    field.setName(fieldName);
    c.addField(field);

    Method method = new Method();
    method.setVisibility(JavaVisibility.PUBLIC);
    method.addAnnotation("@Override");

    method.setReturnType(new FullyQualifiedJavaType(Mybatis3JavaBaseCrudDaoType));
    method.setName("init");
    method.addBodyLine("return " + fieldName + ";");
    c.addMethod(method);

    commentGenerator.addClassComment(c);

    List answer = new ArrayList();
    answer.add(interfaze);
    answer.add(c);
    return answer;
  }

  private String firstCharToLower(String modelName)
  {
    if (modelName.length() > 0)
      modelName = new StringBuilder(String.valueOf(modelName.charAt(0))).toString().toLowerCase() + modelName.substring(1);
    else {
    	Character.isLowerCase(modelName.charAt(0));
    }
    return modelName;
  }
}