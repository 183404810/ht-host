package ht.msc.mybatis.plugin.element;

import ht.msc.mybatis.plugin.FullyQualifiedJavaType;
import ht.msc.mybatis.plugin.GeneratedKey;
import ht.msc.mybatis.plugin.Interface;
import ht.msc.mybatis.plugin.Method;
import ht.msc.mybatis.plugin.InsertSelectiveMethodGenerator;

public class AnnotatedInsertSelectiveMethodGenerator extends InsertSelectiveMethodGenerator
{
  public void addMapperAnnotations(Interface interfaze, Method method)
  {
    FullyQualifiedJavaType fqjt = new FullyQualifiedJavaType(this.introspectedTable.getMyBatis3SqlProviderType());
    interfaze.addImportedType(new FullyQualifiedJavaType("org.apache.ibatis.annotations.InsertProvider"));
    StringBuilder sb = new StringBuilder();
    sb.append("@InsertProvider(type=");
    sb.append(fqjt.getShortName());
    sb.append(".class, method=\"");
    sb.append(this.introspectedTable.getInsertSelectiveStatementId());
    sb.append("\")");

    method.addAnnotation(sb.toString());

    GeneratedKey gk = this.introspectedTable.getGeneratedKey();
    if (gk != null)
      addGeneratedKeyAnnotation(interfaze, method, gk);
  }
}