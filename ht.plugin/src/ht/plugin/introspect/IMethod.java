package ht.plugin.introspect;

import java.util.List;

public class IMethod extends IElement{
	private String methodContext;
	private List<IJavaType> params;
	
	public IMethod(String tabContext,String name,IJavaType type,
			String modifier,String annotation){
		super(tabContext,name,type,modifier,annotation);
	}
	
	public String getMethodContext(){
		StringBuilder sb=getMethodHeader();
		sb.append(tabContext).append(modifier).append(splitStr);
		if(isStatic)
			sb.append("static").append(splitStr);
		if(isFinal)
			sb.append("final").append(splitStr);
		sb.append(name).append("(").append(")").append(startM).append(endEnter);
		return sb.toString();
	}
	public void setParams(List<IJavaType> params) {
		this.params = params;
	}
}
