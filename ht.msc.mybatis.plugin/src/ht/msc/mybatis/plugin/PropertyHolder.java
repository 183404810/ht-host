package ht.msc.mybatis.plugin;

import java.util.Enumeration;
import java.util.Properties;

public abstract class PropertyHolder
{
  private Properties properties;

  public PropertyHolder()
  {
    this.properties = new Properties();
  }

  public void addProperty(String name, String value) {
    this.properties.setProperty(name, value);
  }

  public String getProperty(String name) {
    return this.properties.getProperty(name);
  }

  public Properties getProperties() {
    return this.properties;
  }

  protected void addPropertyXmlElements(XmlElement xmlElement) {
    Enumeration enumeration = this.properties.propertyNames();
    while (enumeration.hasMoreElements()) {
      String propertyName = (String)enumeration.nextElement();

      XmlElement propertyElement = new XmlElement("property");
      propertyElement.addAttribute(new Attribute("name", propertyName));
      propertyElement.addAttribute(new Attribute(
        "value", this.properties.getProperty(propertyName)));
      xmlElement.addElement(propertyElement);
    }
  }
}