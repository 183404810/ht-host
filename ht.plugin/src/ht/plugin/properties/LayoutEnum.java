package ht.plugin.properties;

public enum LayoutEnum {
	DAY_LAYOUT("dao"),SERVICE_LAYOUT("service"),CONTROLLER_LAYOUT("controller"),
	JSMVC_LAYOUT("jsmvc");
	
	public String name;
	
	LayoutEnum(String type){
		this.name=type;
	}
}
