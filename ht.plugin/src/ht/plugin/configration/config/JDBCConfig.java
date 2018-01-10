package ht.plugin.configration.config;

import ht.plugin.adapter.PropertiesAdapter;
import ht.plugin.properties.JDBCType;

public class JDBCConfig extends PropertiesAdapter{
	private String url;
	private String driverClass;
	private String userId;
	private String password;
	private JDBCType dbType;
	
	public JDBCConfig(String url,String driverClass,String userId,String password){
		this.url=url;
		this.driverClass=driverClass;
		this.userId=userId;
		this.password=password;
	}
	
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getDriverClass() {
		return driverClass;
	}
	public void setDriverClass(String driverClass) {
		this.driverClass = driverClass;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public JDBCType getDbType() {
		return dbType;
	}
	public void setDbType(JDBCType dbType) {
		this.dbType = dbType;
	}
}
