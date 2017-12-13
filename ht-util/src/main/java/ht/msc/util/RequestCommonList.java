package ht.msc.util;

import java.util.List;
public class RequestCommonList<T> {
	private   List<T> insertlist;
	private List<T> deletelist;
	private List<T> updatelist;
	
	/**
	 * 单据类型
	 */
	private String billType;

	public List<T> getInsertlist() {
		return insertlist;
	}

	public void setInsertlist(List<T> insertlist) {
		this.insertlist = insertlist;
	}

	public List<T> getDeletelist() {
		return deletelist;
	}

	public void setDeletelist(List<T> deletelist) {
		this.deletelist = deletelist;
	}

	public List<T> getUpdatelist() {
		return updatelist;
	}

	public void setUpdatelist(List<T> updatelist) {
		this.updatelist = updatelist;
	}

	public String getBillType() {
		return billType;
	}

	public void setBillType(String billType) {
		this.billType = billType;
	}
}

