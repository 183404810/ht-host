package ht.plugin.properties;

public enum JSTemplate {
	NAME("name"),TEXT("text"),TYPE("type");
	
	private String name;
	
	JSTemplate(String name){
		this.name=name;
	}
	
	public String getValue(){
		return name;
	}
}
