package ht.plugin.generate;

import java.util.ArrayList;
import java.util.List;

import ht.plugin.introspect.IField;
import ht.plugin.introspect.IJavaType;
import ht.plugin.introspect.IMethod;
import ht.plugin.properties.LayoutEnum;
import ht.plugin.util.StringUtils;


public class GeneratorJavaFile extends GeneratedFile{
	private List<IField> fields;
	private List<IMethod> methods;
	private String remarkStart="/**";
	private String remarkEnd="*/";
	private String remarkTag="*";
	//private String annationContext="{@linkplain #-}";
	//private String returnContext="@return the value of";
	//private String paramContext="@param - the value of |";
	
	public GeneratorJavaFile(String targetProject,String targetPakage,LayoutEnum layout) {
		super(targetProject,targetPakage,layout);
	}
	
	public GeneratorJavaFile(List<IField> fields,String targetProject,String targetPakage,
			String fileName,String modifer1,String annotaion1,LayoutEnum layout){
		super(targetProject,targetPakage,layout);
		this.fields=fields;
		this.fileName=fileName;
		this.modifier=modifer1;
		this.annotation=annotaion1;
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
			String fs=StringUtils.tfNameTransfer(field.getName(), true);
			String entry=StringUtils.tfNameTransfer(field.getName(), false);
			String setName="set"+fs;
			List<IJavaType> setParams=new ArrayList<>();
			field.getType().setParamName(entry);
			setParams.add(field.getType());
			IMethod setMethod=new IMethod(tabContext,setName,IJavaType.getVoidType(),modifier,setName,setParams);
			setMethod.setMethodContext("  this."+entry+"="+entry+";");
			this.methods.add(setMethod);
			String getName="get"+fs;
			List<IJavaType> getParams=new ArrayList<>();
			IMethod getMethod=new IMethod(tabContext,getName,field.getType(),modifier,getName,getParams);
			getMethod.setMethodContext("  return "+"this."+entry+";");
			this.methods.add(getMethod);
		}
	}
	
	public StringBuilder getFileHeader(){
		StringBuilder sb=new StringBuilder();
		sb.append("package ").append(targetPackage).append(newLine);
		sb.append(remarkStart).append(newLine).append(remarkTag).append(annotation).append(newLine).append(remarkEnd);
		sb.append(newLine).append(modifier).append(" class ");
		sb.append(fileName);
		return sb;
	}
}
