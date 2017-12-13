package ht.msc.mybatis.plugin;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class InnerClass extends JavaElement
{
  private List<Field> fields;
  private List<InnerClass> innerClasses;
  private List<InnerEnum> innerEnums;
  private FullyQualifiedJavaType superClass;
  private FullyQualifiedJavaType type;
  private Set<FullyQualifiedJavaType> superInterfaceTypes;
  private List<Method> methods;
  private boolean isAbstract;
  private List<InitializationBlock> initializationBlocks;

  public InnerClass(FullyQualifiedJavaType type)
  {
    this.type = type;
    this.fields = new ArrayList();
    this.innerClasses = new ArrayList();
    this.innerEnums = new ArrayList();
    this.superInterfaceTypes = new HashSet();
    this.methods = new ArrayList();
    this.initializationBlocks = new ArrayList();
  }

  public InnerClass(String typeName) {
    this(new FullyQualifiedJavaType(typeName));
  }

  public List<Field> getFields()
  {
    return this.fields;
  }

  public void addField(Field field) {
    this.fields.add(field);
  }

  public FullyQualifiedJavaType getSuperClass()
  {
    return this.superClass;
  }

  public void setSuperClass(FullyQualifiedJavaType superClass)
  {
    this.superClass = superClass;
  }

  public void setSuperClass(String superClassType) {
    this.superClass = new FullyQualifiedJavaType(superClassType);
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

  public List<InitializationBlock> getInitializationBlocks() {
    return this.initializationBlocks;
  }

  public void addInitializationBlock(InitializationBlock initializationBlock) {
    this.initializationBlocks.add(initializationBlock);
  }

  public String getFormattedContent(int indentLevel) {
    StringBuilder sb = new StringBuilder();

    addFormattedJavadoc(sb, indentLevel);
    addFormattedAnnotations(sb, indentLevel);

    OutputUtilities.javaIndent(sb, indentLevel);
    sb.append(getVisibility().getValue());

    if (isAbstract()) {
      sb.append("abstract ");
    }

    if (isStatic()) {
      sb.append("static ");
    }

    if (isFinal()) {
      sb.append("final ");
    }

    sb.append("class ");
    sb.append(getType().getShortName());

    if (this.superClass != null) {
      sb.append(" extends ");
      sb.append(this.superClass.getShortName());
    }

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

    Iterator fldIter = this.fields.iterator();
    while (fldIter.hasNext()) {
      OutputUtilities.newLine(sb);
      Field field = (Field)fldIter.next();
      sb.append(field.getFormattedContent(indentLevel));
      if (fldIter.hasNext()) {
        OutputUtilities.newLine(sb);
      }
    }

    if (this.initializationBlocks.size() > 0) {
      OutputUtilities.newLine(sb);
    }

    Iterator blkIter = this.initializationBlocks.iterator();
    while (blkIter.hasNext()) {
      OutputUtilities.newLine(sb);
      InitializationBlock initializationBlock = (InitializationBlock)blkIter.next();
      sb.append(initializationBlock.getFormattedContent(indentLevel));
      if (blkIter.hasNext()) {
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

  public boolean isAbstract() {
    return this.isAbstract;
  }

  public void setAbstract(boolean isAbtract) {
    this.isAbstract = isAbtract;
  }
}