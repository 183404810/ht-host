package ht.plugin.generate;

public abstract class GeneratedFile {
	private String targetProject;
	public GeneratedFile(String targetProject){
		this.targetProject=targetProject;
	}
	public void setTargetProject(String targetProject) {
		this.targetProject = targetProject;
	}
}
