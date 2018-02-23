package ht.plugin.configration.config;

import ht.plugin.adapter.PropertiesAdapter;
import ht.plugin.configration.Configration;
import ht.plugin.properties.JDBCType;
import ht.plugin.util.HtClassLoader;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.Properties;

public class JDBCConfig extends PropertiesAdapter{
	private String url;
	private String driverClass;
	private String userId;
	private String password;
	private JDBCType dbType;
	private Driver driver;
	
	public JDBCConfig(String url,String driverClass,String userId,String password){
		this.url=url;
		this.driverClass=driverClass;
		this.userId=userId;
		this.password=password;
	}
	
	public Connection getConn(Configration config) throws SQLException, InstantiationException, IllegalAccessException{
		if(this.driver==null)
			driver=(Driver)HtClassLoader.loadClass(config).newInstance();
		Properties info=new Properties();
		info.setProperty("user", userId);
		info.setProperty("password", password);
		return driver.connect(url, info);
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
