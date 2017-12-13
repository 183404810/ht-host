package ht.msc.util;

import java.util.List;
import java.util.Map;

public class ImportResolve<T> {
	/**
	 * 解析后的list
	 */
	private List<T> dataList;
	/**
	 * 解析后list对应的行号
	 */
	private List<Integer> rowlist;
	/**
	 * 导入错误信息
	 */
	private String errorMsg;
	
	private Map<String, String> columsLine;
	/**
	 * 导入成功
	 */
	private String succesMsg;

	public List<T> getDataList() {
		return dataList;
	}

	public void setDataList(List<T> dataList) {
		this.dataList = dataList;
	}

	public List<Integer> getRowlist() {
		return rowlist;
	}

	public void setRowlist(List<Integer> rowlist) {
		this.rowlist = rowlist;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	public Map<String, String> getColumsLine() {
		return columsLine;
	}

	public void setColumsLine(Map<String, String> columsLine) {
		this.columsLine = columsLine;
	}

	public String getSuccesMsg() {
		return succesMsg;
	}

	public void setSuccesMsg(String succesMsg) {
		this.succesMsg = succesMsg;
	}
}
