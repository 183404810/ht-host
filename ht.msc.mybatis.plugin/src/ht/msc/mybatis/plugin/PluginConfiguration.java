package ht.msc.mybatis.plugin;

import java.util.List;

public class PluginConfiguration extends TypedPropertyHolder
{
  public XmlElement toXmlElement()
  {
    XmlElement answer = new XmlElement("plugin");
    if (getConfigurationType() != null) {
      answer.addAttribute(new Attribute("type", getConfigurationType()));
    }

    addPropertyXmlElements(answer);

    return answer;
  }

  public void validate(List<String> errors, String contextId) {
    if (!StringUtility.stringHasValue(getConfigurationType()))
      errors.add(Messages.getString("ValidationError.17", 
        contextId));
  }
}