package ht.msc.mybatis.plugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class Interface extends JavaElement implements CompilationUnit
{
	private Set<FullyQualifiedJavaType> importedTypes;
	private Set<String> staticImports;
	private FullyQualifiedJavaType type;
	private Set<FullyQualifiedJavaType> superInterfaceTypes;
	private List<Method> methods;
	private List<String> fileCommentLines;
	private List<String> jsfileContentLines;
	
	public Interface(FullyQualifiedJavaType type)
	{
	  this.type = type;
	  this.superInterfaceTypes = new LinkedHashSet();
	  this.methods = new ArrayList();
	  this.importedTypes = new TreeSet();
	  this.fileCommentLines = new ArrayList();
	  this.staticImports = new TreeSet();
	  this.jsfileContentLines = new ArrayList();
	}
	
	public Interface(String type) {
	  this(new FullyQualifiedJavaType(type));
	}
	
	public Set<FullyQualifiedJavaType> getImportedTypes() {
	  return Collections.unmodifiableSet(this.importedTypes);
	}
	
	public void addImportedType(FullyQualifiedJavaType importedType) {
	  if ((importedType.isExplicitlyImported()) && 
	    (!importedType.getPackageName().equals(this.type.getPackageName())))
	    this.importedTypes.add(importedType);
	}
	
	public String getFormattedContent()
	{
	  StringBuilder sb = new StringBuilder();
	
	  for (String commentLine : this.fileCommentLines) {
	    sb.append(commentLine);
	    OutputUtilities.newLine(sb);
	  }
	
	  if (StringUtility.stringHasValue(getType().getPackageName())) {
	    sb.append("package ");
	    sb.append(getType().getPackageName());
	    sb.append(';');
	    OutputUtilities.newLine(sb);
	    OutputUtilities.newLine(sb);
	  }
	
	  for (String staticImport : this.staticImports) {
	    sb.append("import static ");
	    sb.append(staticImport);
	    sb.append(';');
	    OutputUtilities.newLine(sb);
	  }
	
	  if (this.staticImports.size() > 0) {
	    OutputUtilities.newLine(sb);
	  }
	
	  Set<String> importStrings = OutputUtilities.calculateImports(this.importedTypes);
	  for (String importString : importStrings) {
	    sb.append(importString);
	    OutputUtilities.newLine(sb);
	  }
	
	  if (importStrings.size() > 0) {
	    OutputUtilities.newLine(sb);
	  }
	
	  int indentLevel = 0;
	
	  addFormattedJavadoc(sb, indentLevel);
	  addFormattedAnnotations(sb, indentLevel);
	
	  sb.append(getVisibility().getValue());
	
	  if (isStatic()) {
	    sb.append("static ");
	  }
	
	  if (isFinal()) {
	    sb.append("final ");
	  }
	
	  sb.append("interface ");
	  sb.append(getType().getShortName());
	
	  if (getSuperInterfaceTypes().size() > 0) {
	    sb.append(" extends ");
	
	    boolean comma = false;
	    for (FullyQualifiedJavaType fqjt : getSuperInterfaceTypes()) {
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
	
	  Iterator mtdIter = getMethods().iterator();
	  while (mtdIter.hasNext()) {
	    OutputUtilities.newLine(sb);
	    Method method = (Method)mtdIter.next();
	    sb.append(method.getFormattedContent(indentLevel, true));
	    if (mtdIter.hasNext()) {
	      OutputUtilities.newLine(sb);
	    }
	  }
	
	  indentLevel--;
	  OutputUtilities.newLine(sb);
	  OutputUtilities.javaIndent(sb, indentLevel);
	  sb.append('}');
	
	  return sb.toString();
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
	
	public FullyQualifiedJavaType getSuperClass()
	{
	  return null;
	}
	
	public Set<FullyQualifiedJavaType> getSuperInterfaceTypes() {
	  return this.superInterfaceTypes;
	}
	
	public boolean isJavaInterface() {
	  return true;
	}
	
	public boolean isJavaEnumeration() {
	  return false;
	}
	
	public void addFileCommentLine(String commentLine) {
	  this.fileCommentLines.add(commentLine);
	}
	
	public List<String> getFileCommentLines() {
	  return this.fileCommentLines;
	}
	
	public void addImportedTypes(Set<FullyQualifiedJavaType> importedTypes) {
	  this.importedTypes.addAll(importedTypes);
	}
	
	public Set<String> getStaticImports() {
	  return this.staticImports;
	}
	
	public void addStaticImport(String staticImport) {
	  this.staticImports.add(staticImport);
	}
	
	public void addStaticImports(Set<String> staticImports) {
	  this.staticImports.addAll(staticImports);
	}
	
	public void addJSFileContentLine(String jsContentLine)
	{
	  this.jsfileContentLines.add(jsContentLine);
	}
	
	public List<String> getJSFileContentLines()
	{
	  return this.jsfileContentLines;
	}
}