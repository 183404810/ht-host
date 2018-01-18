package ht.plugin.introspect;

public class IColumn {
	private ITable table;
	private String columnName;
	private int length;
	private String remarks;
	
	public IColumn(ITable table){
		this.table=table;
	}
	
	public ITable getTable() {
		return table;
	}
	public String getColumnName() {
		return columnName;
	}
	public int getLength() {
		return length;
	}
	public String getRemarks() {
		return remarks;
	}

	public void setTable(ITable table) {
		this.table = table;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
}
