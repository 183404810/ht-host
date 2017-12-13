package ht.msc.util;

public class DelMasterCustomerReq {
	/**
	 * 主表数据list
	 */
	private String masterJsonList;
	/**
	 * 从表 多个用逗号隔开
	 */
	private String customerObjs;
	/**
	 * 主表主键
	 */
	private String masterId;
	
	
	public String getMasterJsonList() {
		return masterJsonList;
	}
	public void setMasterJsonList(String masterJsonList) {
		this.masterJsonList = masterJsonList;
	}
	public String getCustomerObjs() {
		return customerObjs;
	}
	public void setCustomerObjs(String customerObjs) {
		this.customerObjs = customerObjs;
	}
	public String getMasterId() {
		return masterId;
	}
	public void setMasterId(String masterId) {
		this.masterId = masterId;
	}
	
}
