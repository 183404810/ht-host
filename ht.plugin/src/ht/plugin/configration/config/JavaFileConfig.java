package ht.plugin.configration.config;

import ht.plugin.adapter.PropertiesAdapter;

public class JavaFileConfig extends PropertiesAdapter{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String targetPackage;
	private String targetProject;
	private String implementationPackage;
	private String interfaceExtendSupInterface;
	private boolean enableInterfaceSupInterfaceGenericity;
	private String extendSupClass;
	private boolean enableSupClassGenericity;
	
	public String getTargetPackage() {
		return targetPackage;
	}
	public void setTargetPackage(String targetPackage) {
		this.targetPackage = targetPackage;
	}
	public String getTargetProject() {
		return targetProject;
	}
	public void setTargetProject(String targetProject) {
		this.targetProject = targetProject;
	}
	public String getImplementationPackage() {
		return implementationPackage;
	}
	public void setImplementationPackage(String implementationPackage) {
		this.implementationPackage = implementationPackage;
	}
	public String getInterfaceExtendSupInterface() {
		return interfaceExtendSupInterface;
	}
	public void setInterfaceExtendSupInterface(String interfaceExtendSupInterface) {
		this.interfaceExtendSupInterface = interfaceExtendSupInterface;
	}
	public boolean isEnableInterfaceSupInterfaceGenericity() {
		return enableInterfaceSupInterfaceGenericity;
	}
	public void setEnableInterfaceSupInterfaceGenericity(
			boolean enableInterfaceSupInterfaceGenericity) {
		this.enableInterfaceSupInterfaceGenericity = enableInterfaceSupInterfaceGenericity;
	}
	public String getExtendSupClass() {
		return extendSupClass;
	}
	public void setExtendSupClass(String extendSupClass) {
		this.extendSupClass = extendSupClass;
	}
	public boolean isEnableSupClassGenericity() {
		return enableSupClassGenericity;
	}
	public void setEnableSupClassGenericity(boolean enableSupClassGenericity) {
		this.enableSupClassGenericity = enableSupClassGenericity;
	}
}
