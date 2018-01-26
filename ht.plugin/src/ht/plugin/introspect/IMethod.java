package ht.plugin.introspect;

public class IMethod extends IElement{
	
	private String startM="{";
	private String endM="}";
	private String splitStr=" ";
	private String methodContext;
	private IJavaType params;
	
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
		sb.append(name).append(startM).append(endEnter);
		return sb.toString();
	}
	
	private StringBuilder getMethodHeader(){
		StringBuilder sb=new StringBuilder();
		sb.append(remarkStart);
		
		return sb;
	}

	public void setParams(IJavaType params) {
		this.params = params;
	}
}
