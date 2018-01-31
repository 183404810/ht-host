package ht.plugin.introspect;

public abstract class IElement {
	protected String tabContext;
	protected String name;
	protected IJavaType type;
	protected String modifier;
	protected String annotation;
	protected Boolean isStatic=false;
	protected Boolean isFinal=false;
	protected String endEnter="\r\n";
	protected String remarkStart="/**";
	protected String remarkEnd="*/";
	protected String remarkTag="*";
	protected String splitStr=" ";
	protected String pressRemarks=";";
	protected String startM="{";
	protected String endM="}";
	
	public IElement(String tabContext,String name,IJavaType type,
			String modifier,String annotation){
		this.tabContext=tabContext;
		this.name=name;
		this.type=type;
		this.modifier=modifier;
		this.annotation=annotation;
	}
	public void setIsStatic(Boolean isStatic) {
		this.isStatic = isStatic;
	}
	public void setIsFinal(Boolean isFinal) {
		this.isFinal = isFinal;
	}
	
	protected StringBuilder getMethodHeader(){
		StringBuilder sb=new StringBuilder();
		sb.append(tabContext).append(remarkStart).append(endEnter).append(tabContext).append(remarkTag);
		sb.append(annotation).append(endEnter).append(tabContext).append(remarkEnd);
		return sb;
	}
	
	protected StringBuilder newLine(StringBuilder sb){
		return sb.append(endEnter);
	}
}
