package ht.msc.mybatis.plugin;

import java.util.ArrayList;
import java.util.List;

public class Parameter
{
  private String name;
  private FullyQualifiedJavaType type;
  private boolean isVarargs;
  private List<String> annotations;

  public Parameter(FullyQualifiedJavaType type, String name, boolean isVarargs)
  {
    this.name = name;
    this.type = type;
    this.isVarargs = isVarargs;
    this.annotations = new ArrayList();
  }

  public Parameter(FullyQualifiedJavaType type, String name) {
    this(type, name, false);
  }

  public Parameter(FullyQualifiedJavaType type, String name, String annotation) {
    this(type, name, false);
    addAnnotation(annotation);
  }

  public Parameter(FullyQualifiedJavaType type, String name, String annotation, boolean isVarargs) {
    this(type, name, isVarargs);
    addAnnotation(annotation);
  }

  public String getName()
  {
    return this.name;
  }

  public FullyQualifiedJavaType getType()
  {
    return this.type;
  }

  public List<String> getAnnotations() {
    return this.annotations;
  }

  public void addAnnotation(String annotation) {
    this.annotations.add(annotation);
  }

  public String getFormattedContent() {
    StringBuilder sb = new StringBuilder();

    for (String annotation : this.annotations) {
      sb.append(annotation);
      sb.append(' ');
    }

    sb.append(this.type.getShortName());
    sb.append(' ');
    if (this.isVarargs) {
      sb.append("... ");
    }
    sb.append(this.name);

    return sb.toString();
  }

  public String toString()
  {
    return getFormattedContent();
  }

  public boolean isVarargs() {
    return this.isVarargs;
  }
}