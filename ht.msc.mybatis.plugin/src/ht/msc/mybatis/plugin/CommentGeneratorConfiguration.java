package ht.msc.mybatis.plugin;

public class CommentGeneratorConfiguration extends TypedPropertyHolder
{
  public XmlElement toXmlElement()
  {
    XmlElement answer = new XmlElement("commentGenerator");
    if (getConfigurationType() != null) {
      answer.addAttribute(new Attribute("type", getConfigurationType()));
    }

    addPropertyXmlElements(answer);

    return answer;
  }
}