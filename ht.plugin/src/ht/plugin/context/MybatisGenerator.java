package ht.plugin.context;

import ht.plugin.generate.GeneratedFile;
import ht.plugin.introspect.DBIntrospect;

import java.util.ArrayList;
import java.util.List;

public class MybatisGenerator implements Generator{
	private PluginContext context;
	
	public MybatisGenerator(PluginContext context){
		this.context=context;
	}
	
	public void generate(List<GeneratedFile> generatedFiles,List<String> tableList){
		if(generatedFiles==null) generatedFiles=new ArrayList<>();
		generatedFiles.clear();
		DBIntrospect introspect=new DBIntrospect(context);
		introspect.introspect(tableList);
	}
}
