package ht.msc.mybatis.plugin;

public class Attribute
{
  private String name;
  private String value;

  public Attribute(String name, String value)
  {
    this.name = name;
    this.value = value;
  }

  public String getName()
  {
    return this.name;
  }

  public String getValue()
  {
    return this.value;
  }

  public String getFormattedContent() {
    StringBuilder sb = new StringBuilder();
    sb.append(this.name);
    sb.append("=\"");
    sb.append(this.value);
    sb.append('"');

    return sb.toString();
  }
}