package ht.msc.mybatis.plugin;

import java.util.Iterator;

public class SelectByDataAuthorityElementGenerator extends AbstractXmlElementGenerator
{
  public void addElements(XmlElement parentElement)
  {
    XmlElement answer = new XmlElement("select");

    this.context.getCommentGenerator().addComment(answer, false);

    answer.addAttribute(new Attribute(
      "id", "selectByPageForDataAuthority"));
    answer.addAttribute(new Attribute("resultMap", 
      this.introspectedTable.getBaseResultMapId()));

    answer.addAttribute(new Attribute("parameterType", 
      "map"));

    this.context.getCommentGenerator().addComment(answer);

    StringBuilder sb = new StringBuilder();
    sb.append("SELECT ");

    answer.addElement(new TextElement(sb.toString()));

    if (this.introspectedTable.getRules().generateBaseColumnList()) {
      answer.addElement(getBaseColumnListElement());
    } else {
      sb.setLength(0);
      Iterator iter = this.introspectedTable
        .getNonBLOBColumns().iterator();
      while (iter.hasNext()) {
        sb.append(MyBatis3FormattingUtilities.getSelectListPhrase(
          (IntrospectedColumn)iter
          .next()));

        if (iter.hasNext()) {
          sb.append(", ");
        }

        if (sb.length() > 80) {
          answer.addElement(new TextElement(sb.toString()));
          sb.setLength(0);
        }
      }
      answer.addElement(new TextElement(sb.toString()));
    }

    if (this.introspectedTable.hasBLOBColumns()) {
      answer.addElement(new TextElement(","));
      answer.addElement(getBlobColumnListElement());
    }

    sb.setLength(0);
    sb.append(" FROM ");
    sb.append(this.introspectedTable
      .getAliasedFullyQualifiedTableNameAtRuntime());
    answer.addElement(new TextElement(sb.toString()));

    parentElement.addElement(answer);
  }
}