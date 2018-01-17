package ht.plugin.context;

import ht.plugin.generate.GeneratedFile;

import java.util.List;

public class MybatisGenerator implements Generator{
	private PluginContext context;
	private List<GeneratedFile> generateFiles;
	
	public MybatisGenerator(PluginContext context){
		this.context=context;
	}
}
