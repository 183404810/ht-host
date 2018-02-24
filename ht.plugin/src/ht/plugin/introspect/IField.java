package ht.plugin.introspect;

import ht.plugin.properties.JSTemplate;
import ht.plugin.util.StringUtils;

public class IField extends IElement{
	private String proertySplit=",";
	
	public IField(String tabContext,String name,IJavaType type,
			String modifier,String annotation){
		super(tabContext,name,type,modifier,annotation);
	}
	public String getJavaFieldContext(){
		StringBuilder sb=getMethodHeader();
		sb=newLine(sb);
		sb.append(tabContext).append(modifier).append(splitStr).append(type.getTypeName()).append(splitStr).append(StringUtils.tfNameTransfer(name,false)).append(pressRemarks);
		sb.append(endEnter);
		return sb.toString();
	}
	
	public String getJSModelContext(){
		StringBuilder sb=new StringBuilder();
		sb.append(tabContext).append(startM).append(JSTemplate.NAME).append(name).append(proertySplit);
		sb.append(JSTemplate.TEXT).append(annotation).append(proertySplit);
		sb.append(JSTemplate.TYPE).append(type).append(endM);
		return newLine(sb).toString();
	}
	
	public String getJSViewContext(){
		StringBuilder sb=new StringBuilder();
		return sb.toString();
	}
}
