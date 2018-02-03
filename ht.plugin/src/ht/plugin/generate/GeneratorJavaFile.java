package ht.plugin.generate;

import java.util.ArrayList;
import java.util.List;

import ht.plugin.introspect.IField;
import ht.plugin.introspect.IJavaType;
import ht.plugin.introspect.IMethod;
import ht.plugin.properties.LayoutEnum;


public class GeneratorJavaFile extends GeneratedFile{
	private List<IField> fields;
	private List<IMethod> methods;

	public GeneratorJavaFile(String targetProject,String targetPakage,LayoutEnum layout) {
		super(targetProject,targetPakage,layout);
	}
	
	public GeneratorJavaFile(List<IField> fields,String targetProject,String targetPakage,
			String fileName,String modifer,String annotaion,LayoutEnum layout){
		super(targetProject,targetPakage,layout);
		this.fields=fields;
		this.fileName=fileName;
		this.modifier=modifier;
		this.annotation=annotation;
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
			setMethod.setMethodContext("this."+field.getName()+"="+field.getName());
			this.methods.add(setMethod);
			String getName="get"+fs;
			setParams.clear();
			IMethod getMethod=new IMethod(tabContext,getName,field.getType(),modifier,getName,setParams);
			getMethod.setMethodContext("return "+"this."+field);
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
