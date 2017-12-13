package ht.msc.mybatis.plugin;

import java.util.List;

public class ColumnOverride extends PropertyHolder
{
  private String columnName;
  private String javaProperty;
  private String jdbcType;
  private String javaType;
  private String typeHandler;
  private boolean isColumnNameDelimited;
  private String configuredDelimitedColumnName;

  public ColumnOverride(String columnName)
  {
    this.columnName = columnName;
    this.isColumnNameDelimited = StringUtility.stringContainsSpace(columnName);
  }

  public String getColumnName() {
    return this.columnName;
  }

  public String getJavaProperty() {
    return this.javaProperty;
  }

  public void setJavaProperty(String javaProperty) {
    this.javaProperty = javaProperty;
  }

  public String getJavaType() {
    return this.javaType;
  }

  public void setJavaType(String javaType) {
    this.javaType = javaType;
  }

  public String getJdbcType() {
    return this.jdbcType;
  }

  public void setJdbcType(String jdbcType) {
    this.jdbcType = jdbcType;
  }

  public String getTypeHandler() {
    return this.typeHandler;
  }

  public void setTypeHandler(String typeHandler) {
    this.typeHandler = typeHandler;
  }

  public XmlElement toXmlElement() {
    XmlElement xmlElement = new XmlElement("columnOverride");
    xmlElement.addAttribute(new Attribute("column", this.columnName));

    if (StringUtility.stringHasValue(this.javaProperty)) {
      xmlElement.addAttribute(new Attribute("property", this.javaProperty));
    }

    if (StringUtility.stringHasValue(this.javaType)) {
      xmlElement.addAttribute(new Attribute("javaType", this.javaType));
    }

    if (StringUtility.stringHasValue(this.jdbcType)) {
      xmlElement.addAttribute(new Attribute("jdbcType", this.jdbcType));
    }

    if (StringUtility.stringHasValue(this.typeHandler)) {
      xmlElement.addAttribute(new Attribute("typeHandler", this.typeHandler));
    }

    if (StringUtility.stringHasValue(this.configuredDelimitedColumnName)) {
      xmlElement.addAttribute(new Attribute(
        "delimitedColumnName", this.configuredDelimitedColumnName));
    }

    addPropertyXmlElements(xmlElement);

    return xmlElement;
  }

  public boolean isColumnNameDelimited() {
    return this.isColumnNameDelimited;
  }

  public void setColumnNameDelimited(boolean isColumnNameDelimited) {
    this.isColumnNameDelimited = isColumnNameDelimited;

    this.configuredDelimitedColumnName = (isColumnNameDelimited ? "true" : "false");
  }

  public void validate(List<String> errors, String tableName) {
    if (!StringUtility.stringHasValue(this.columnName))
      errors.add(Messages.getString("ValidationError.22", 
        tableName));
  }
}