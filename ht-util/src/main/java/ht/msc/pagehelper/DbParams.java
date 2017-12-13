package ht.msc.pagehelper;

import org.springframework.stereotype.Component;

@Component
public class DbParams {
	
	public static String pageDbType;
	
	private String dbType;

	public String getDbType() {
		return dbType;
	}

	public void setDbType(String dbType) {
		this.dbType = dbType;
		DbParams.pageDbType=dbType;
	}
}