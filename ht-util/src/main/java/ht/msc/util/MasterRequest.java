package ht.msc.util;

public class MasterRequest {
	
	/**
	 * 主表对象
	 */
	private String masterJson;
	/**
	 * 操作类型
	 */
	private String operateType;
	
	private String customerListData;
	
	private String idFieldName;
	
	/**
	 * 单据类型
	 */
	private String billType;
	


	public String getMasterJson() {
		return masterJson;
	}

	public void setMasterJson(String masterJson) {
		this.masterJson = masterJson;
	}

	public String getOperateType() {
		return operateType;
	}

	public void setOperateType(String operateType) {
		this.operateType = operateType;
	}

	public String getCustomerListData() {
		return customerListData;
	}

	public void setCustomerListData(String customerListData) {
		this.customerListData = customerListData;
	}

	public String getIdFieldName() {
		return idFieldName;
	}

	public void setIdFieldName(String idFieldName) {
		this.idFieldName = idFieldName;
	}

	public String getBillType() {
		return billType;
	}

	public void setBillType(String billType) {
		this.billType = billType;
	}
}
