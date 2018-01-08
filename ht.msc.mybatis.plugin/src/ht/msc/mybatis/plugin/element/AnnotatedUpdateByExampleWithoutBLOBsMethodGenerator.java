package ht.msc.mybatis.plugin.element;

import ht.msc.mybatis.plugin.FullyQualifiedJavaType;
import ht.msc.mybatis.plugin.Interface;
import ht.msc.mybatis.plugin.Method;
import ht.msc.mybatis.plugin.UpdateByExampleWithoutBLOBsMethodGenerator;

public class AnnotatedUpdateByExampleWithoutBLOBsMethodGenerator extends UpdateByExampleWithoutBLOBsMethodGenerator
{
  public void addMapperAnnotations(Interface interfaze, Method method)
  {
    FullyQualifiedJavaType fqjt = new FullyQualifiedJavaType(this.introspectedTable.getMyBatis3SqlProviderType());
    interfaze.addImportedType(new FullyQualifiedJavaType("org.apache.ibatis.annotations.UpdateProvider"));
    StringBuilder sb = new StringBuilder();
    sb.append("@UpdateProvider(type=");
    sb.append(fqjt.getShortName());
    sb.append(".class, method=\"");
    sb.append(this.introspectedTable.getUpdateByExampleStatementId());
    sb.append("\")");

    method.addAnnotation(sb.toString());
  }
}