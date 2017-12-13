package ht.msc.mybatis.plugin;

import java.util.List;

public class JDBCConnectionConfiguration extends PropertyHolder
{
  private String driverClass;
  private String connectionURL;
  private String userId;
  private String password;
  private String dbmsType;

  public String getDbmsType()
  {
    return this.dbmsType;
  }

  public void setDbmsType(String dbmsType) {
    this.dbmsType = dbmsType;
  }

  public String getConnectionURL()
  {
    return this.connectionURL;
  }

  public void setConnectionURL(String connectionURL) {
    this.connectionURL = connectionURL;
  }

  public String getPassword() {
    return this.password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getUserId() {
    return this.userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public String getDriverClass() {
    return this.driverClass;
  }

  public void setDriverClass(String driverClass) {
    this.driverClass = driverClass;
  }

  public XmlElement toXmlElement() {
    XmlElement xmlElement = new XmlElement("jdbcConnection");
    xmlElement.addAttribute(new Attribute("driverClass", this.driverClass));
    xmlElement.addAttribute(new Attribute("connectionURL", this.connectionURL));

    if (StringUtility.stringHasValue(this.userId)) {
      xmlElement.addAttribute(new Attribute("userId", this.userId));
    }

    if (StringUtility.stringHasValue(this.password)) {
      xmlElement.addAttribute(new Attribute("password", this.password));
    }

    addPropertyXmlElements(xmlElement);

    return xmlElement;
  }

  public void validate(List<String> errors) {
    if (!StringUtility.stringHasValue(this.driverClass)) {
      errors.add(Messages.getString("ValidationError.4"));
    }

    if (!StringUtility.stringHasValue(this.connectionURL))
      errors.add(Messages.getString("ValidationError.5"));
  }

  public String toString() {
    return "[driverClass:" + this.driverClass + ",connectionURL:" + this.connectionURL + ",userId:" + this.userId + ",password:" + this.password + "]";
  }
}