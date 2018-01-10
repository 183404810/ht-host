package ht.plugin.properties;

public enum JDBCType {
	DB2("DB2",1),MYSQL("MYSQL",2),ORCAL("ORCL",3);
	
	private String type;
	private Integer value;
	
	JDBCType(String type,Integer value){
		this.type=type;
		this.value=value;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Integer getValue() {
		return value;
	}
	public void setValue(Integer value) {
		this.value = value;
	}
}
