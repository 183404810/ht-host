package ht.msc.util;

import java.util.List;
import java.util.Map;


/**
 * 封装表名和表数据对象
 * @author zhu.l
 *
 */
public class TableEntity {
	
	/**
	 * 表名
	 */
	private String tableName;
	
	/**
	 * 数据
	 */
	private List<Map<String,Object>> tableValue;

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public List<Map<String, Object>> getTableValue() {
		return tableValue;
	}

	public void setTableValue(List<Map<String, Object>> tableValue) {
		this.tableValue = tableValue;
	}

}
