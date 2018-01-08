package ht.msc.mybatis.plugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class TopLevelClass extends InnerClass implements CompilationUnit
{
	private Set<FullyQualifiedJavaType> importedTypes;
	private Set<String> staticImports;
	private List<String> fileCommentLines;
	private List<String> jsfileContentLines;
	
	public TopLevelClass(FullyQualifiedJavaType type)
	{
	  super(type);
	  this.importedTypes = new TreeSet();
	  this.fileCommentLines = new ArrayList();
	  this.staticImports = new TreeSet();
	  this.jsfileContentLines = new ArrayList();
	}
	
	public TopLevelClass(String typeName) {
	  this(new FullyQualifiedJavaType(typeName));
	}
	
	public Set<FullyQualifiedJavaType> getImportedTypes()
	{
	  return Collections.unmodifiableSet(this.importedTypes);
	}
	
	public void addImportedType(String importedType) {
	  addImportedType(new FullyQualifiedJavaType(importedType));
	}
	
	public void addImportedType(FullyQualifiedJavaType importedType) {
	  if ((importedType != null) && 
	    (importedType.isExplicitlyImported()) && 
	    (!importedType.getPackageName().equals(
	    getType().getPackageName())))
	    this.importedTypes.add(importedType);
	}
	
	public String getFormattedContent()
	{
	  StringBuilder sb = new StringBuilder();
	
	  for (String fileCommentLine : this.fileCommentLines) {
	    sb.append(fileCommentLine);
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
	
	  sb.append(super.getFormattedContent(0));
	
	  return sb.toString();
	}
	
	public boolean isJavaInterface() {
	  return false;
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