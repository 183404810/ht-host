package ht.msc.util;

public class ExportCustomerReqest {
	/**
	 * 从表名称
	 */
	private String customerName;
	
	private String sheetName;
	/**
	 * json格式的字符串数组
	 */
	private String columnsList;
	
	private String customerModle;

	

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getSheetName() {
		return sheetName;
	}

	public void setSheetName(String sheetName) {
		this.sheetName = sheetName;
	}

	public String getColumnsList() {
		return columnsList;
	}

	public void setColumnsList(String columnsList) {
		this.columnsList = columnsList;
	}

	public String getCustomerModle() {
		return customerModle;
	}

	public void setCustomerModle(String customerModle) {
		this.customerModle = customerModle;
	}
}
