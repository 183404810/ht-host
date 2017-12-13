package ht.msc.util;

import java.io.Serializable;

public class DataPrivilegeTableVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8380505432107327719L;

	private Long privilegeNo;
	
	private String tableName;
	
	private String columnName;


	public Long getPrivilegeNo() {
		return privilegeNo;
	}

	public void setPrivilegeNo(Long privilegeNo) {
		this.privilegeNo = privilegeNo;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

}
