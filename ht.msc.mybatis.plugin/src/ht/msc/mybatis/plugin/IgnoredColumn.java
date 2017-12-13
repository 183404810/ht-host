package ht.msc.mybatis.plugin;

import java.util.List;

public class IgnoredColumn
{
  private String columnName;
  private boolean isColumnNameDelimited;
  private String configuredDelimitedColumnName;

  public IgnoredColumn(String columnName)
  {
    this.columnName = columnName;
    this.isColumnNameDelimited = StringUtility.stringContainsSpace(columnName);
  }

  public String getColumnName() {
    return this.columnName;
  }

  public boolean isColumnNameDelimited() {
    return this.isColumnNameDelimited;
  }

  public void setColumnNameDelimited(boolean isColumnNameDelimited) {
    this.isColumnNameDelimited = isColumnNameDelimited;
    this.configuredDelimitedColumnName = (isColumnNameDelimited ? "true" : "false");
  }

  public boolean equals(Object obj) {
    if ((obj == null) || (!(obj instanceof IgnoredColumn))) {
      return false;
    }

    return this.columnName.equals(((IgnoredColumn)obj).getColumnName());
  }

  public int hashCode() {
    return this.columnName.hashCode();
  }

  public XmlElement toXmlElement() {
    XmlElement xmlElement = new XmlElement("ignoreColumn");
    xmlElement.addAttribute(new Attribute("column", this.columnName));

    if (StringUtility.stringHasValue(this.configuredDelimitedColumnName)) {
      xmlElement.addAttribute(new Attribute(
        "delimitedColumnName", this.configuredDelimitedColumnName));
    }

    return xmlElement;
  }

  public void validate(List<String> errors, String tableName) {
    if (!StringUtility.stringHasValue(this.columnName))
      errors.add(Messages.getString("ValidationError.21", 
        tableName));
  }
}