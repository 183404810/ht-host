package ht.msc.mybatis.plugin;
import ht.msc.mybatis.plugin.element.CountByExampleMethodGenerator;

public class AnnotatedCountByExampleMethodGenerator extends CountByExampleMethodGenerator
{

	public void addMapperAnnotations(Interface interfaze, Method method)
  {
    FullyQualifiedJavaType fqjt = new FullyQualifiedJavaType(this.introspectedTable.getMyBatis3SqlProviderType());
    interfaze.addImportedType(new FullyQualifiedJavaType("org.apache.ibatis.annotations.SelectProvider"));
    StringBuilder sb = new StringBuilder();
    sb.append("@SelectProvider(type=");
    sb.append(fqjt.getShortName());
    sb.append(".class, method=\"");
    sb.append(this.introspectedTable.getCountByExampleStatementId());
    sb.append("\")");

    method.addAnnotation(sb.toString());
  }
}