package ht.msc.util;

import java.io.Serializable;


public class DataPrivilegeVO implements  Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 78474384318553030L;

	private String privilegeNo;
	
	private String privilegeName;
	
	private String privilegeSql;
	
	private Integer useAll;
	
	private String useModuleNoList;
	
	private String nonuseUseModuleNoList;

	public String getPrivilegeNo() {
		return privilegeNo;
	}

	public void setPrivilegeNo(String privilegeNo) {
		this.privilegeNo = privilegeNo;
	}

	public String getPrivilegeName() {
		return privilegeName;
	}

	public void setPrivilegeName(String privilegeName) {
		this.privilegeName = privilegeName;
	}

	public String getPrivilegeSql() {
		return privilegeSql;
	}

	public void setPrivilegeSql(String privilegeSql) {
		this.privilegeSql = privilegeSql;
	}

	public Integer getUseAll() {
		return useAll;
	}

	public void setUseAll(Integer useAll) {
		this.useAll = useAll;
	}

	public String getUseModuleNoList() {
		return useModuleNoList;
	}

	public void setUseModuleNoList(String useModuleNoList) {
		this.useModuleNoList = useModuleNoList;
	}

	public String getNonuseUseModuleNoList() {
		return nonuseUseModuleNoList;
	}

	public void setNonuseUseModuleNoList(String nonuseUseModuleNoList) {
		this.nonuseUseModuleNoList = nonuseUseModuleNoList;
	}
}