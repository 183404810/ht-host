package ht.msc.mybatis.plugin;

public class TextElement extends Element
{
  private String content;

  public TextElement(String content)
  {
    this.content = content;
  }

  public String getFormattedContent(int indentLevel)
  {
    StringBuilder sb = new StringBuilder();
    OutputUtilities.xmlIndent(sb, indentLevel);
    sb.append(this.content);
    return sb.toString();
  }

  public String getContent() {
    return this.content;
  }
}