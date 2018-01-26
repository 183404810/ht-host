package ht.plugin.generate;

public abstract class GeneratedFile {
	protected String targetProject;
	protected String newLine="\r\n";
	public GeneratedFile(String targetProject){
		this.targetProject=targetProject;
	}
}
