package ht.msc.mybatis.plugin;

import java.util.ArrayList;
import java.util.List;


public class YouGouDaoGenerator extends AbstractJavaGenerator
{
  public List<CompilationUnit> getCompilationUnits()
  {
    CommentGenerator commentGenerator = this.context.getCommentGenerator();

    FullyQualifiedJavaType type = new FullyQualifiedJavaType(this.introspectedTable.getMyBatis3JavaDaoType());
    Interface interfaze = new Interface(type);
    interfaze.setVisibility(JavaVisibility.PUBLIC);

    YouGouDaoGeneratorConfiguration daoConfig = this.context.getYouGouDaoGeneratorConfiguration();

    String modelName = this.introspectedTable.getFullyQualifiedTable().getDomainObjectName();
    if (daoConfig.getInterFaceExtendSupInterfaceDoMain().length() > 0)
    {
      interfaze.addImportedType(new FullyQualifiedJavaType(daoConfig.getInterfaceExtendSupInterface()));
      String base = daoConfig.getInterFaceExtendSupInterfaceDoMain();
      interfaze.addImportedType(new FullyQualifiedJavaType(daoConfig.getInterfaceExtendSupInterface()));
      if (new Boolean(daoConfig.getEnableInterfaceSupInterfaceGenericity()).booleanValue()) {
        interfaze.addImportedType(new FullyQualifiedJavaType(this.introspectedTable.getBaseRecordType()));
        interfaze.addSuperInterface(new FullyQualifiedJavaType(base + "<" + modelName + ">"));
      } else {
        interfaze.addSuperInterface(new FullyQualifiedJavaType(base));
      }
      commentGenerator.addClassComment(interfaze);
    }

    String dalpackagestr = type.toString();

    String Mybatis3JavaBaseCrudMapperType = "com.belle.scm.common.base.mapper.BaseCrudMapper";
    String Mybatis3JavaMapperType = dalpackagestr.substring(0, dalpackagestr.indexOf("dal")) + "mapper." + modelName + "Mapper";
    TopLevelClass c = new TopLevelClass(new FullyQualifiedJavaType(this.introspectedTable.getMyBatis3JavaDaoImplType()));
    c.addImportedType(new FullyQualifiedJavaType("javax.annotation.Resource"));
    c.addImportedType(new FullyQualifiedJavaType("org.springframework.stereotype.Repository"));
    c.addImportedType(new FullyQualifiedJavaType(daoConfig.getExtendSupClass()));
    c.addImportedType(new FullyQualifiedJavaType(this.introspectedTable.getMyBatis3JavaDaoType()));
    c.addImportedType(new FullyQualifiedJavaType(Mybatis3JavaBaseCrudMapperType));
    c.addImportedType(new FullyQualifiedJavaType(Mybatis3JavaMapperType));
    if ((Mybatis3JavaBaseCrudMapperType != null) && (Mybatis3JavaBaseCrudMapperType.length() > 0)) {
      c.addImportedType(new FullyQualifiedJavaType(Mybatis3JavaBaseCrudMapperType));
    }

    if (daoConfig.getInterFaceExtendSupInterfaceDoMain().length() > 0) {
      String ServiceName = firstCharToLower(this.introspectedTable.getMyBatis3JavaDaoType().substring(this.introspectedTable.getMyBatis3JavaDaoType().lastIndexOf(".") + 1));
      c.addAnnotation("@Repository(\"" + ServiceName + "\")");
      if (new Boolean(daoConfig.getEnableSupClassGenericity()).booleanValue()) {
        c.addImportedType(new FullyQualifiedJavaType(this.introspectedTable.getBaseRecordType()));
        c.setSuperClass(new FullyQualifiedJavaType(daoConfig.getExtendSupClassDoMain() + "<" + modelName + ">"));
      } else {
        c.setSuperClass(new FullyQualifiedJavaType(daoConfig.getExtendSupClassDoMain()));
      }
      c.addSuperInterface(new FullyQualifiedJavaType(this.introspectedTable.getMyBatis3JavaDaoType()));
    }

    Field field = new Field();
    field.addAnnotation("@Resource");
    field.setVisibility(JavaVisibility.PRIVATE);
    field.setType(new FullyQualifiedJavaType(Mybatis3JavaMapperType));
    String fieldName = firstCharToLower(Mybatis3JavaMapperType.substring(Mybatis3JavaMapperType.lastIndexOf(".") + 1));
    field.setName(fieldName);
    c.addField(field);

    Method method = new Method();
    method.setVisibility(JavaVisibility.PUBLIC);
    method.addAnnotation("@Override");

    method.setReturnType(new FullyQualifiedJavaType(Mybatis3JavaBaseCrudMapperType));
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
      Character.toLowerCase(modelName.charAt(0));
    }
    return modelName;
  }
}