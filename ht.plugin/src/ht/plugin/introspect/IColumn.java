package ht.plugin.introspect;

public class IColumn {
	private ITable table;
	private String typeName;
	private String columnName;
	private int length;
	private int nullAble;
	private String remarks;
	private String defaultValue;
	
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
	public String getTypeName() {
		return typeName;
	}
	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	public int getNullAble() {
		return nullAble;
	}

	public void setNullAble(int nullAble) {
		this.nullAble = nullAble;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}
}
