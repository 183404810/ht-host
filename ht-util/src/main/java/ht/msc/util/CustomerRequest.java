package ht.msc.util;

public class CustomerRequest {
	private String customerName;
	
	private String insertlist;
	
	private String deletelist;
	
	private String updatelist;
	/**是否是尺码横排  1:为是要转换 ,0或空则不需要转换尺码横排**/
	private String isSizeHorizontal;
	/**0：尺码横排，1：物料新旧替换**/
	private String type;
	/**尺码数量需要设置的字段，默认为sizeQty**/
	private String qtyProperty;

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getInsertlist() {
		return insertlist;
	}

	public void setInsertlist(String insertlist) {
		this.insertlist = insertlist;
	}

	public String getDeletelist() {
		return deletelist;
	}

	public void setDeletelist(String deletelist) {
		this.deletelist = deletelist;
	}

	public String getUpdatelist() {
		return updatelist;
	}

	public void setUpdatelist(String updatelist) {
		this.updatelist = updatelist;
	}

	public String getIsSizeHorizontal() {
		return isSizeHorizontal;
	}

	public void setIsSizeHorizontal(String isSizeHorizontal) {
		this.isSizeHorizontal = isSizeHorizontal;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getQtyProperty() {
		return qtyProperty;
	}

	public void setQtyProperty(String qtyProperty) {
		this.qtyProperty = qtyProperty;
	}
}
