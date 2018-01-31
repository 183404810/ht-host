package ht.plugin.generate;

import ht.plugin.introspect.IField;
import ht.plugin.introspect.IMethod;

import java.util.List;


public class GeneratorJavaFile extends GeneratedFile{
	private List<IField> fields;
	private List<IMethod> methods;
	private String annotation;
	private String fileName;
	private String modifier;

	public GeneratorJavaFile(String targetProject) {
		super(targetProject);
	}
	
	public GeneratorJavaFile(List<IField> fields,List<IMethod> methods,String targetProject){
		super(targetProject);
		this.fields=fields;
		this.methods=methods;
	}
	
	public String generator(){
		StringBuilder sb=getFileHeader();
		sb.append("{").append(newLine);
		for(IField filed:fields){
			sb.append(filed.getJavaFieldContext());
		}
		for(IMethod method:methods){
			sb.append(method.getMethodContext());
		}
		sb.append("}");
		return sb.toString();
	}
	
	public StringBuilder getFileHeader(){
		StringBuilder sb=new StringBuilder();
		sb.append("package ").append(targetProject).append(newLine);
		sb.append(annotation).append(newLine).append(modifier).append(" class ");
		sb.append(fileName);
		return sb;
	}
}
