package ht.msc.mybatis.plugin;

import java.util.List;
import java.util.Set;

public class JavaClientGeneratorConfiguration extends TypedPropertyHolder
{
  private String targetPackage;
  private String implementationPackage;
  private String targetProject;
  private String interfaceExtendSupInterface;
  private String interfaceExtendSupInterfaceDoMain;
  private String enableInterfaceSupInterfaceGenericity;
  public Set<String> exclusionsMethods;

  public String getInterfaceExtendSupInterface()
  {
    return this.interfaceExtendSupInterface;
  }

  public void setInterfaceExtendSupInterface(String interfaceExtendSupInterface) {
    this.interfaceExtendSupInterface = interfaceExtendSupInterface;
  }

  public void setInterfaceExtendSupInterfaceDoMain(String interfaceExtendSupInterfaceDoMain)
  {
    this.interfaceExtendSupInterfaceDoMain = interfaceExtendSupInterfaceDoMain;
  }

  public String getEnableInterfaceSupInterfaceGenericity() {
    return this.enableInterfaceSupInterfaceGenericity;
  }

  public void setEnableInterfaceSupInterfaceGenericity(String enableInterfaceSupInterfaceGenericity)
  {
    this.enableInterfaceSupInterfaceGenericity = enableInterfaceSupInterfaceGenericity;
  }

  public String getInterFaceExtendSupInterfaceDoMain() {
    int point = -1;
    if ((this.interfaceExtendSupInterface != null) && (!"".equals(this.interfaceExtendSupInterface)))
      point = this.interfaceExtendSupInterface.lastIndexOf(".");
    if ((this.interfaceExtendSupInterface != null) && (this.interfaceExtendSupInterface.length() > point))
      this.interfaceExtendSupInterfaceDoMain = this.interfaceExtendSupInterface.substring(point + 1);
    else {
      this.interfaceExtendSupInterfaceDoMain = "";
    }
    return this.interfaceExtendSupInterfaceDoMain;
  }

  public Set<String> getExclusionsMethods()
  {
    return this.exclusionsMethods;
  }

  public void setExclusionsMethods(Set<String> exclusionsMethods) {
    this.exclusionsMethods = exclusionsMethods;
  }

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
    XmlElement answer = new XmlElement("javaClientGenerator");
    if (getConfigurationType() != null) {
      answer.addAttribute(new Attribute("type", getConfigurationType()));
    }

    if (this.targetPackage != null) {
      answer.addAttribute(new Attribute("targetPackage", this.targetPackage));
    }

    if (this.targetProject != null) {
      answer.addAttribute(new Attribute("targetProject", this.targetProject));
    }

    if (this.implementationPackage != null) {
      answer.addAttribute(new Attribute(
        "implementationPackage", this.targetProject));
    }

    addPropertyXmlElements(answer);

    return answer;
  }

  public String getImplementationPackage() {
    return this.implementationPackage;
  }

  public void setImplementationPackage(String implementationPackage) {
    this.implementationPackage = implementationPackage;
  }

  public void validate(List<String> errors, String contextId) {
    if (!StringUtility.stringHasValue(this.targetProject)) {
      errors.add(Messages.getString("ValidationError.2", contextId));
    }

    if (!StringUtility.stringHasValue(this.targetPackage)) {
      errors.add(Messages.getString("ValidationError.12", 
        "javaClientGenerator", contextId));
    }

    if (!StringUtility.stringHasValue(getConfigurationType()))
      errors.add(Messages.getString("ValidationError.20", 
        contextId));
  }
}