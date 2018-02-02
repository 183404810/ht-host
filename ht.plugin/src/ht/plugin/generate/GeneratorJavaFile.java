package ht.plugin.generate;

import java.util.ArrayList;
import java.util.List;

import ht.plugin.introspect.IField;
import ht.plugin.introspect.IJavaType;
import ht.plugin.introspect.IMethod;


public class GeneratorJavaFile extends GeneratedFile{
	private List<IField> fields;
	private List<IMethod> methods;
	private String annotation;
	private String fileName;
	private String modifier;

	public GeneratorJavaFile(String targetProject) {
		super(targetProject);
	}
	
	public GeneratorJavaFile(List<IField> fields,String targetProject){
		super(targetProject);
		this.fields=fields;
		generateGetSetMethod();
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
	
	private void generateGetSetMethod(){
		if(methods==null) this.methods=new ArrayList<>();
		if(fields==null || fields.size()<=0) return;
		String tabContext="  ";
		String modifier="public";
		for(IField field: fields){
			String fs=field.getName().substring(0,1).toUpperCase()+field.getName().substring(0);
			String setName="set"+fs;
			List<IJavaType> setParams=new ArrayList<>();
			setParams.add(field.getType());
			IMethod setMethod=new IMethod(tabContext,setName,IJavaType.getVoidType(),modifier,setName,setParams);
			this.methods.add(setMethod);
			String getName="get"+fs;
			setParams.clear();
			IMethod getMethod=new IMethod(tabContext,getName,field.getType(),modifier,getName,setParams);
			this.methods.add(getMethod);
		}
	}
	
	public StringBuilder getFileHeader(){
		StringBuilder sb=new StringBuilder();
		sb.append("package ").append(targetProject).append(newLine);
		sb.append(annotation).append(newLine).append(modifier).append(" class ");
		sb.append(fileName);
		return sb;
	}
}
