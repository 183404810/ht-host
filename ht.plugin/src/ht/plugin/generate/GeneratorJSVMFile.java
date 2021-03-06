package ht.plugin.generate;

import java.util.List;

import ht.plugin.introspect.IField;
import ht.plugin.properties.LayoutEnum;

public class GeneratorJSVMFile extends GeneratedFile{
	private List<IField> fields;
	private String annotation;
	private String fileName;
	private String modifier;
	
	public GeneratorJSVMFile(String targetProject,String targetPakage,LayoutEnum layout) {
		super(targetProject,targetPakage,layout);
	}
	
	public GeneratorJSVMFile(List<IField> fields,String targetProject,String targetPakage,LayoutEnum layout){
		super(targetProject,targetPakage,layout);
		this.fields=fields;
	}
	
	public String generator(){
		StringBuilder sb=getFileHeader();
		sb.append("{").append(newLine);
		for(IField filed:fields){
			sb.append(filed.getJSModelContext());
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
