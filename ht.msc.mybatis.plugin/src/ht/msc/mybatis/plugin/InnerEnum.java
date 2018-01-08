package ht.msc.mybatis.plugin;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class InnerEnum extends JavaElement
{
  private List<Field> fields;
  private List<InnerClass> innerClasses;
  private List<InnerEnum> innerEnums;
  private FullyQualifiedJavaType type;
  private Set<FullyQualifiedJavaType> superInterfaceTypes;
  private List<Method> methods;
  private List<String> enumConstants;

  public InnerEnum(FullyQualifiedJavaType type)
  {
    this.type = type;
    this.fields = new ArrayList();
    this.innerClasses = new ArrayList();
    this.innerEnums = new ArrayList();
    this.superInterfaceTypes = new HashSet();
    this.methods = new ArrayList();
    this.enumConstants = new ArrayList();
  }

  public List<Field> getFields()
  {
    return this.fields;
  }

  public void addField(Field field) {
    this.fields.add(field);
  }

  public List<InnerClass> getInnerClasses()
  {
    return this.innerClasses;
  }

  public void addInnerClass(InnerClass innerClass) {
    this.innerClasses.add(innerClass);
  }

  public List<InnerEnum> getInnerEnums() {
    return this.innerEnums;
  }

  public void addInnerEnum(InnerEnum innerEnum) {
    this.innerEnums.add(innerEnum);
  }

  public List<String> getEnumConstants() {
    return this.enumConstants;
  }

  public void addEnumConstant(String enumConstant) {
    this.enumConstants.add(enumConstant);
  }

  public String getFormattedContent(int indentLevel) {
    StringBuilder sb = new StringBuilder();

    addFormattedJavadoc(sb, indentLevel);
    addFormattedAnnotations(sb, indentLevel);

    OutputUtilities.javaIndent(sb, indentLevel);
    if (getVisibility() == JavaVisibility.PUBLIC) {
      sb.append(getVisibility().getValue());
    }

    sb.append("enum ");
    sb.append(getType().getShortName());

    if (this.superInterfaceTypes.size() > 0) {
      sb.append(" implements ");

      boolean comma = false;
      for (FullyQualifiedJavaType fqjt : this.superInterfaceTypes) {
        if (comma)
          sb.append(", ");
        else {
          comma = true;
        }

        sb.append(fqjt.getShortName());
      }
    }

    sb.append(" {");
    indentLevel++;

    Iterator strIter = this.enumConstants.iterator();
    while (strIter.hasNext()) {
      OutputUtilities.newLine(sb);
      OutputUtilities.javaIndent(sb, indentLevel);
      String enumConstant = (String)strIter.next();
      sb.append(enumConstant);

      if (strIter.hasNext())
        sb.append(',');
      else {
        sb.append(';');
      }
    }

    if (this.fields.size() > 0) {
      OutputUtilities.newLine(sb);
    }

    Iterator fldIter = this.fields.iterator();
    while (fldIter.hasNext()) {
      OutputUtilities.newLine(sb);
      Field field = (Field)fldIter.next();
      sb.append(field.getFormattedContent(indentLevel));
      if (fldIter.hasNext()) {
        OutputUtilities.newLine(sb);
      }
    }

    if (this.methods.size() > 0) {
      OutputUtilities.newLine(sb);
    }

    Iterator mtdIter = this.methods.iterator();
    while (mtdIter.hasNext()) {
      OutputUtilities.newLine(sb);
      Method method = (Method)mtdIter.next();
      sb.append(method.getFormattedContent(indentLevel, false));
      if (mtdIter.hasNext()) {
        OutputUtilities.newLine(sb);
      }
    }

    if (this.innerClasses.size() > 0) {
      OutputUtilities.newLine(sb);
    }

    Iterator icIter = this.innerClasses.iterator();
    while (icIter.hasNext()) {
      OutputUtilities.newLine(sb);
      InnerClass innerClass = (InnerClass)icIter.next();
      sb.append(innerClass.getFormattedContent(indentLevel));
      if (icIter.hasNext()) {
        OutputUtilities.newLine(sb);
      }
    }

    if (this.innerEnums.size() > 0) {
      OutputUtilities.newLine(sb);
    }

    Iterator ieIter = this.innerEnums.iterator();
    while (ieIter.hasNext()) {
      OutputUtilities.newLine(sb);
      InnerEnum innerEnum = (InnerEnum)ieIter.next();
      sb.append(innerEnum.getFormattedContent(indentLevel));
      if (ieIter.hasNext()) {
        OutputUtilities.newLine(sb);
      }
    }

    indentLevel--;
    OutputUtilities.newLine(sb);
    OutputUtilities.javaIndent(sb, indentLevel);
    sb.append('}');

    return sb.toString();
  }

  public Set<FullyQualifiedJavaType> getSuperInterfaceTypes()
  {
    return this.superInterfaceTypes;
  }

  public void addSuperInterface(FullyQualifiedJavaType superInterface) {
    this.superInterfaceTypes.add(superInterface);
  }

  public List<Method> getMethods()
  {
    return this.methods;
  }

  public void addMethod(Method method) {
    this.methods.add(method);
  }

  public FullyQualifiedJavaType getType()
  {
    return this.type;
  }
}