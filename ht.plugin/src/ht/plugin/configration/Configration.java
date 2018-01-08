package ht.plugin.configration;

import ht.plugin.configration.config.JDBCConfig;
import ht.plugin.configration.config.JSFileConfig;
import ht.plugin.configration.config.JavaFileConfig;
import ht.plugin.configration.config.XmlFileConfig;

public class Configration {
	private JavaFileConfig javaConfig;
	private JDBCConfig dbConfig;
	private JSFileConfig jsConfig;
	private XmlFileConfig xfconfig;
	 
	public JavaFileConfig getJavaConfig() {
		return javaConfig;
	}
	public void setJavaConfig(JavaFileConfig javaConfig) {
		this.javaConfig = javaConfig;
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
}
