package ht.msc.mybatis.plugin;

import java.util.ArrayList;
import java.util.List;

public abstract class JavaElement
{
  private List<String> javaDocLines;
  private JavaVisibility visibility = JavaVisibility.DEFAULT;
  private boolean isStatic;
  private boolean isFinal;
  private List<String> annotations;

  public JavaElement()
  {
    this.javaDocLines = new ArrayList();
    this.annotations = new ArrayList();
  }

  public JavaElement(JavaElement original)
  {
    this();
    this.annotations.addAll(original.annotations);
    this.isFinal = original.isFinal;
    this.isStatic = original.isFinal;
    this.javaDocLines.addAll(original.javaDocLines);
    this.visibility = original.visibility;
  }

  public List<String> getJavaDocLines()
  {
    return this.javaDocLines;
  }

  public void addJavaDocLine(String javaDocLine) {
    this.javaDocLines.add(javaDocLine);
  }

  public List<String> getAnnotations() {
    return this.annotations;
  }

  public void addAnnotation(String annotation) {
    this.annotations.add(annotation);
  }

  public JavaVisibility getVisibility()
  {
    return this.visibility;
  }

  public void setVisibility(JavaVisibility visibility)
  {
    this.visibility = visibility;
  }

  public void addSuppressTypeWarningsAnnotation() {
    addAnnotation("@SuppressWarnings(\"unchecked\")");
  }

  public void addFormattedJavadoc(StringBuilder sb, int indentLevel) {
    for (String javaDocLine : this.javaDocLines) {
      OutputUtilities.javaIndent(sb, indentLevel);
      sb.append(javaDocLine);
      OutputUtilities.newLine(sb);
    }
  }

  public void addFormattedAnnotations(StringBuilder sb, int indentLevel) {
    for (String annotation : this.annotations) {
      OutputUtilities.javaIndent(sb, indentLevel);
      sb.append(annotation);
      OutputUtilities.newLine(sb);
    }
  }

  public boolean isFinal() {
    return this.isFinal;
  }

  public void setFinal(boolean isFinal) {
    this.isFinal = isFinal;
  }

  public boolean isStatic() {
    return this.isStatic;
  }

  public void setStatic(boolean isStatic) {
    this.isStatic = isStatic;
  }
}