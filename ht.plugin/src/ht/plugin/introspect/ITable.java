package ht.plugin.introspect;

import java.util.List;

public class ITable {
	private String catalog;
	private String schema;
	private String tableName;
	private String alias;
	private List<IColumn> columns;
	private List<IColumn> keyColumns;

	public ITable(List<IColumn> columns){
		this.columns=columns;
	}
	
	public String getCatalog() {
		return catalog;
	}
	public String getSchema() {
		return schema;
	}
	public String getTableName() {
		return tableName;
	}
	public String getAlias() {
		return alias;
	}
	public List<IColumn> getColumns() {
		return columns;
	}
	public void setCatalog(String catalog) {
		this.catalog = catalog;
	}
	public void setSchema(String schema) {
		this.schema = schema;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public void setAlias(String alias) {
		this.alias = alias;
	}
	public void setColumns(List<IColumn> columns) {
		this.columns = columns;
	}
	public List<IColumn> getKeyColumns() {
		return keyColumns;
	}
	public void setKeyColumns(List<IColumn> keyColumns) {
		this.keyColumns = keyColumns;
	}
}
