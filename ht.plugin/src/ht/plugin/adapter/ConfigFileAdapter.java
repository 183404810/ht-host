package ht.plugin.adapter;

import org.eclipse.core.resources.IFile;

public class ConfigFileAdapter {
	private IFile configFile;
	
	public ConfigFileAdapter(IFile file){
		this.configFile=file;
	}
	
	public IFile getConfigFile(){
		return this.configFile;
	}
}
