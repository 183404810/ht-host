package ht.plugin.introspect;

import java.util.List;

public class IMethod extends IElement{
	private String methodContext;
	private List<IJavaType> params;
	
	public IMethod(String tabContext,String name,IJavaType type,
			String modifier,String annotation,List<IJavaType> params){
		super(tabContext,name,type,modifier,annotation);
		this.params=params;
	}
	
	public String getMethodContext(){
		StringBuilder sb=getMethodHeader();
		sb.append(endEnter).append(tabContext).append(modifier).append(splitStr);
		if(isStatic)
			sb.append("static").append(splitStr);
		if(isFinal)
			sb.append("final").append(splitStr);
		sb.append(type.getTypeName()).append(splitStr).append(name).append("(");
		int i=0;
		for(IJavaType type:params){
			if(i>0)
				sb.append(",");
			sb.append(type.getTypeName()).append(splitStr).append(type.getParamName());
			i++;
		}
		sb.append(")").append(splitStr).append(startM).append(endEnter).append(tabContext).append(methodContext).append(endEnter).append(tabContext).append(endM);
		return sb.append(endEnter).toString();
	}
	
	public String getImport(){
		StringBuilder sb=new StringBuilder();
		if(type!=null && type.getPackageTarget()!=null){
			newLine(sb.append(type.getPackageTarget()).append(type.getTypeName()).append(";"));
		}
		for(IJavaType type:params){
			newLine(sb.append(type.getPackageTarget()).append(type.getTypeName()).append(";"));
		}
		return sb.toString();
	}
	
	public void setParams(List<IJavaType> params) {
		this.params = params;
	}
	
	public void setMethodContext(String methodContext){
		this.methodContext=methodContext;
	}
}
