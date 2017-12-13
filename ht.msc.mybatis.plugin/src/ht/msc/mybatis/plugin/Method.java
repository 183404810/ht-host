package ht.msc.mybatis.plugin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ListIterator;

public class Method extends JavaElement
{
  private List<String> bodyLines;
  private boolean constructor;
  private FullyQualifiedJavaType returnType;
  private String name;
  private List<Parameter> parameters;
  private List<FullyQualifiedJavaType> exceptions;
  private boolean isSynchronized;
  private boolean isNative;

  public Method()
  {
    this("bar");
  }

  public Method(String name)
  {
    this.bodyLines = new ArrayList();
    this.parameters = new ArrayList();
    this.exceptions = new ArrayList();
    this.name = name;
  }

  public Method(Method original)
  {
    super(original);
    this.bodyLines = new ArrayList();
    this.parameters = new ArrayList();
    this.exceptions = new ArrayList();
    this.bodyLines.addAll(original.bodyLines);
    this.constructor = original.constructor;
    this.exceptions.addAll(original.exceptions);
    this.name = original.name;
    this.parameters.addAll(original.parameters);
    this.returnType = original.returnType;
    this.isNative = original.isNative;
    this.isSynchronized = original.isSynchronized;
  }

  public List<String> getBodyLines()
  {
    return this.bodyLines;
  }

  public void addBodyLine(String line) {
    this.bodyLines.add(line);
  }

  public void addBodyLine(int index, String line) {
    this.bodyLines.add(index, line);
  }

  public void addBodyLines(Collection<String> lines) {
    this.bodyLines.addAll(lines);
  }

  public void addBodyLines(int index, Collection<String> lines) {
    this.bodyLines.addAll(index, lines);
  }

  public String getFormattedContent(int indentLevel, boolean interfaceMethod) {
    StringBuilder sb = new StringBuilder();

    addFormattedJavadoc(sb, indentLevel);
    addFormattedAnnotations(sb, indentLevel);

    OutputUtilities.javaIndent(sb, indentLevel);

    if (!interfaceMethod) {
      sb.append(getVisibility().getValue());

      if (isStatic()) {
        sb.append("static ");
      }

      if (isFinal()) {
        sb.append("final ");
      }

      if (isSynchronized()) {
        sb.append("synchronized ");
      }

      if (isNative())
        sb.append("native ");
      else if (this.bodyLines.size() == 0) {
        sb.append("abstract ");
      }
    }

    if (!this.constructor) {
      if (getReturnType() == null)
        sb.append("void");
      else {
        sb.append(getReturnType().getShortName());
      }
      sb.append(' ');
    }

    sb.append(getName());
    sb.append('(');

    boolean comma = false;
    for (Parameter parameter : getParameters()) {
      if (comma)
        sb.append(", ");
      else {
        comma = true;
      }

      sb.append(parameter.getFormattedContent());
    }

    sb.append(')');

    if (getExceptions().size() > 0) {
      sb.append(" throws ");
      comma = false;
      for (FullyQualifiedJavaType fqjt : getExceptions()) {
        if (comma)
          sb.append(", ");
        else {
          comma = true;
        }

        sb.append(fqjt.getShortName());
      }

    }

    if ((this.bodyLines.size() == 0) || (isNative())) {
      sb.append(';');
    } else {
      sb.append(" {");
      indentLevel++;

      ListIterator listIter = this.bodyLines.listIterator();
      while (listIter.hasNext()) {
        String line = (String)listIter.next();
        if (line.startsWith("}")) {
          indentLevel--;
        }

        OutputUtilities.newLine(sb);
        OutputUtilities.javaIndent(sb, indentLevel);
        sb.append(line);

        if (((line.endsWith("{")) && (!line.startsWith("switch"))) || 
          (line.endsWith(":"))) {
          indentLevel++;
        }

        if (line.startsWith("break"))
        {
          if (listIter.hasNext()) {
            String nextLine = (String)listIter.next();
            if (nextLine.startsWith("}")) {
              indentLevel++;
            }

            listIter.previous();
          }
          indentLevel--;
        }
      }

      indentLevel--;
      OutputUtilities.newLine(sb);
      OutputUtilities.javaIndent(sb, indentLevel);
      sb.append('}');
    }

    return sb.toString();
  }

  public boolean isConstructor()
  {
    return this.constructor;
  }

  public void setConstructor(boolean constructor)
  {
    this.constructor = constructor;
  }

  public String getName()
  {
    return this.name;
  }

  public void setName(String name)
  {
    this.name = name;
  }

  public List<Parameter> getParameters() {
    return this.parameters;
  }

  public void addParameter(Parameter parameter) {
    this.parameters.add(parameter);
  }

  public void addParameter(int index, Parameter parameter) {
    this.parameters.add(index, parameter);
  }

  public FullyQualifiedJavaType getReturnType()
  {
    return this.returnType;
  }

  public void setReturnType(FullyQualifiedJavaType returnType)
  {
    this.returnType = returnType;
  }

  public List<FullyQualifiedJavaType> getExceptions()
  {
    return this.exceptions;
  }

  public void addException(FullyQualifiedJavaType exception) {
    this.exceptions.add(exception);
  }

  public boolean isSynchronized() {
    return this.isSynchronized;
  }

  public void setSynchronized(boolean isSynchronized) {
    this.isSynchronized = isSynchronized;
  }

  public boolean isNative() {
    return this.isNative;
  }

  public void setNative(boolean isNative) {
    this.isNative = isNative;
  }
}