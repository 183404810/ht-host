package ht.plugin.generate;

import ht.plugin.properties.LayoutEnum;

public abstract class GeneratedFile {
	protected String targetProject;
	protected String targetPackage;
	protected String annotation;
	protected String fileName;
	protected String modifier;
	protected String newLine="\r\n";
    protected String FilePostfix="java";
    protected String fileLocal="src/main/java";
    
    protected LayoutEnum layout;
	
	public GeneratedFile(String targetProject,String targetPakage1,LayoutEnum layout){
		this.targetProject=targetProject;
		this.targetPackage=targetPakage1;
		this.layout=layout;
	}
	
	public abstract String generator();
	public abstract StringBuilder getFileHeader();
	public void setLayout(LayoutEnum layout){
		this.layout=layout;
	}
	
	public String getFileLocal(){
		return this.fileLocal;
	}
	public void setFileLocal(String fileLocal){
		this.fileLocal=fileLocal;
	}
	
	public LayoutEnum getLayout(){
		return this.layout;
	}
	
	public void setFilePostfix(String postfix){
		this.FilePostfix=postfix;
	}
	public String getTargetProject() {
		return targetProject;
	}
	public String getTargetPackage() {
		return targetPackage;
	}
	public String getFileName(){
		return fileName+"."+FilePostfix;
	}
}
