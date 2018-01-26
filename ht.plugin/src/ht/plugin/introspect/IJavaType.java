package ht.plugin.introspect;

public class IJavaType {
	private String packageTarget;
	private String typeName;
	
	public IJavaType(String packageTarget,String typeName){
		this.packageTarget=packageTarget;
		this.typeName=typeName;
	}

	public String getPackageTarget() {
		return packageTarget;
	}

	public String getTypeName() {
		return typeName;
	}	
}
