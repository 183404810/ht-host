package ht.msc.mybatis.plugin;

import java.util.List;

public class SqlMapGeneratorConfiguration extends PropertyHolder
{
  private String targetPackage;
  private String targetProject;

  public String getTargetProject()
  {
    return this.targetProject;
  }

  public void setTargetProject(String targetProject) {
    this.targetProject = targetProject;
  }

  public String getTargetPackage() {
    return this.targetPackage;
  }

  public void setTargetPackage(String targetPackage) {
    this.targetPackage = targetPackage;
  }

  public XmlElement toXmlElement() {
    XmlElement answer = new XmlElement("sqlMapGenerator");

    if (this.targetPackage != null) {
      answer.addAttribute(new Attribute("targetPackage", this.targetPackage));
    }

    if (this.targetProject != null) {
      answer.addAttribute(new Attribute("targetProject", this.targetProject));
    }

    addPropertyXmlElements(answer);

    return answer;
  }

  public void validate(List<String> errors, String contextId) {
    if (!StringUtility.stringHasValue(this.targetProject))
      errors.add(Messages.getString("ValidationError.1", contextId));
  }
}