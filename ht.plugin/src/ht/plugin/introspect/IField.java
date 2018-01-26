package ht.plugin.introspect;

public class IField extends IElement{
	public IField(String tabContext,String name,IJavaType type,
			String modifier,String annotation){
		super(tabContext,name,type,modifier,annotation);
	}
	public String getFieldContext(){
		return modifier+" "+type+" "+name;
	}
}
