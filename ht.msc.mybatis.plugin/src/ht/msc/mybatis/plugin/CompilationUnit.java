package ht.msc.mybatis.plugin;

import java.util.List;
import java.util.Set;

public abstract interface CompilationUnit
{
  public abstract String getFormattedContent();

  public abstract Set<FullyQualifiedJavaType> getImportedTypes();

  public abstract Set<String> getStaticImports();

  public abstract FullyQualifiedJavaType getSuperClass();

  public abstract boolean isJavaInterface();

  public abstract boolean isJavaEnumeration();

  public abstract Set<FullyQualifiedJavaType> getSuperInterfaceTypes();

  public abstract FullyQualifiedJavaType getType();

  public abstract void addImportedType(FullyQualifiedJavaType paramFullyQualifiedJavaType);

  public abstract void addImportedTypes(Set<FullyQualifiedJavaType> paramSet);

  public abstract void addStaticImport(String paramString);

  public abstract void addStaticImports(Set<String> paramSet);

  public abstract void addFileCommentLine(String paramString);

  public abstract List<String> getFileCommentLines();

  public abstract void addJSFileContentLine(String paramString);

  public abstract List<String> getJSFileContentLines();
}