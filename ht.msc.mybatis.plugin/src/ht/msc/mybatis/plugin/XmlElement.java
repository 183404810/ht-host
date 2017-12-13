package ht.msc.mybatis.plugin;

import java.util.ArrayList;
import java.util.List;



public class XmlElement extends Element
{
  private List<Attribute> attributes;
  private List<Element> elements;
  private String name;

  public XmlElement(String name)
  {
    this.attributes = new ArrayList();
    this.elements = new ArrayList();
    this.name = name;
  }

  public XmlElement(XmlElement original)
  {
    this.attributes = new ArrayList();
    this.attributes.addAll(original.attributes);
    this.elements = new ArrayList();
    this.elements.addAll(original.elements);
    this.name = original.name;
  }

  public List<Attribute> getAttributes()
  {
    return this.attributes;
  }

  public void addAttribute(Attribute attribute) {
    this.attributes.add(attribute);
  }

  public List<Element> getElements()
  {
    return this.elements;
  }

  public void addElement(Element element) {
    this.elements.add(element);
  }

  public void addElement(int index, Element element) {
    this.elements.add(index, element);
  }

  public String getName()
  {
    return this.name;
  }

  public String getFormattedContent(int indentLevel)
  {
    StringBuilder sb = new StringBuilder();

    OutputUtilities.xmlIndent(sb, indentLevel);
    sb.append('<');
    sb.append(this.name);

    for (Attribute att : this.attributes) {
      sb.append(' ');
      sb.append(att.getFormattedContent());
    }

    if (this.elements.size() > 0) {
      sb.append(" >");
      for (Element element : this.elements) {
        OutputUtilities.newLine(sb);
        sb.append(element.getFormattedContent(indentLevel + 1));
      }
      OutputUtilities.newLine(sb);
      OutputUtilities.xmlIndent(sb, indentLevel);
      sb.append("</");
      sb.append(this.name);
      sb.append('>');
    }
    else {
      sb.append(" />");
    }

    return sb.toString();
  }

  public void setName(String name) {
    this.name = name;
  }
}