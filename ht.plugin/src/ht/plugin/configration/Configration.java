package ht.plugin.configration;

import ht.plugin.configration.config.JDBCConfig;
import ht.plugin.configration.config.JSFileConfig;
import ht.plugin.configration.config.JavaFileConfig;
import ht.plugin.configration.config.XmlFileConfig;
import ht.plugin.context.PluginContext;
import ht.plugin.properties.LayoutEnum;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class Configration {
	private Map<String,JavaFileConfig> config;
	private JDBCConfig dbConfig;
	private JSFileConfig jsConfig;
	private XmlFileConfig xfconfig;
	private List<String> classEntry;
	private Properties properties;
	private PluginContext context;
	private List<LayoutEnum> layout;
	
	public Configration(){
		if(config==null)
			config=new HashMap<>();
		if(classEntry==null)
			classEntry=new ArrayList<>();
		if(layout==null)
			layout=new ArrayList<>();
	}
	
	public JDBCConfig getDbConfig() {
		return dbConfig;
	}
	public void setDbConfig(JDBCConfig dbConfig) {
		this.dbConfig = dbConfig;
	}
	public JSFileConfig getJsConfig() {
		return jsConfig;
	}
	public void setJsConfig(JSFileConfig jsConfig) {
		this.jsConfig = jsConfig;
	}
	public XmlFileConfig getXfconfig() {
		return xfconfig;
	}
	public void setXfconfig(XmlFileConfig xfconfig) {
		this.xfconfig = xfconfig;
	}
	public List<String> getClassEntry() {
		return classEntry;
	}
	public void setClassEntry(List<String> classEntry) {
		this.classEntry = classEntry;
	}
	public Properties getProperties() {
		return properties;
	}
	public void setProperties(Properties properties) {
		this.properties = properties;
	}
	public PluginContext getContext() {
		return context;
	}
	public void setContext(PluginContext context) {
		this.context = context;
	}
	public Map<String, JavaFileConfig> getConfig() {
		return config;
	}
	public void setConfig(Map<String, JavaFileConfig> config) {
		this.config = config;
	}
	public List<LayoutEnum> getLayout() {
		return layout;
	}
	public void setLayout(List<LayoutEnum> layout) {
		this.layout = layout;
	}
}