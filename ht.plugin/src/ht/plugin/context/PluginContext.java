package ht.plugin.context;

import ht.plugin.adapter.PropertiesAdapter;
import ht.plugin.configration.Configration;
import ht.plugin.configration.ConfigrationParser;
import ht.plugin.configration.Parser;
import ht.plugin.introspect.ITable;
import ht.plugin.util.HtClassLoader;

import java.util.List;

public class PluginContext extends PropertiesAdapter{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<ITable> tables;
	private Configration config;
	private ConfigrationParser parser;
	private ClassLoader loader;
	
	public void generat(){
		
	}
	
	public PluginContext(){
		if(config==null)
			config=new Configration();
		if(loader==null)
			loader=new HtClassLoader("",System.class.getClassLoader());
		if(parser==null)
			parser=new ConfigrationParser(null,null);
	}

	public List<ITable> getTables() {
		return tables;
	}

	public void setTables(List<ITable> tables) {
		this.tables = tables;
	}

	public Configration getConfig() {
		return config;
	}

	public void setConfig(Configration config) {
		this.config = config;
	}

	public ConfigrationParser getParser() {
		return parser;
	}

	public void setParser(ConfigrationParser parser) {
		this.parser = parser;
	}
	public ClassLoader getLoader() {
		return loader;
	}
	public void setLoader(ClassLoader loader) {
		this.loader = loader;
	}
	
}
