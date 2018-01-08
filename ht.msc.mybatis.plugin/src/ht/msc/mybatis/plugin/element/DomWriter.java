package ht.msc.mybatis.plugin.element;

import ht.msc.mybatis.plugin.Messages;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;

import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.EntityReference;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;


public class DomWriter
{
  protected PrintWriter printWriter;
  protected boolean isXML11;

  public synchronized String toString(Document document)
    throws ShellException
  {
    StringWriter sw = new StringWriter();
    this.printWriter = new PrintWriter(sw);
    write(document);
    String s = sw.toString();
    return s;
  }

  protected Attr[] sortAttributes(NamedNodeMap attrs)
  {
    int len = attrs != null ? attrs.getLength() : 0;
    Attr[] array = new Attr[len];
    for (int i = 0; i < len; i++) {
      array[i] = ((Attr)attrs.item(i));
    }
    for (int i = 0; i < len - 1; i++) {
      String name = array[i].getNodeName();
      int index = i;
      for (int j = i + 1; j < len; j++) {
        String curName = array[j].getNodeName();
        if (curName.compareTo(name) < 0) {
          name = curName;
          index = j;
        }
      }
      if (index != i) {
        Attr temp = array[i];
        array[i] = array[index];
        array[index] = temp;
      }
    }

    return array;
  }

  protected void normalizeAndPrint(String s, boolean isAttValue)
  {
    int len = s != null ? s.length() : 0;
    for (int i = 0; i < len; i++) {
      char c = s.charAt(i);
      normalizeAndPrint(c, isAttValue);
    }
  }

  protected void normalizeAndPrint(char c, boolean isAttValue)
  {
    switch (c) {
    case '<':
      this.printWriter.print("&lt;");
      break;
    case '>':
      this.printWriter.print("&gt;");
      break;
    case '&':
      this.printWriter.print("&amp;");
      break;
    case '"':
      if (isAttValue)
        this.printWriter.print("&quot;");
      else {
        this.printWriter.print('"');
      }
      break;
    case '\r':
      this.printWriter.print("&#xD;");
      break;
    default:
      if (((this.isXML11) && (
        ((c >= '\001') && (c <= '\037') && (c != '\t') && (c != '\n')) || 
        ((c >= '') && (c <= '')) || (c == ' '))) || (
        (isAttValue) && ((c == '\t') || (c == '\n')))) {
        this.printWriter.print("&#x");
        this.printWriter.print(Integer.toHexString(c).toUpperCase());
        this.printWriter.print(';');
      } else {
        this.printWriter.print(c);
      }
      break;
    }
  }

  protected String getVersion(Document document)
  {
    if (document == null) {
      return null;
    }
    String version = null;
    Method getXMLVersion = null;
    try {
      getXMLVersion = document.getClass().getMethod("getXmlVersion", 
        new Class[0]);

      if (getXMLVersion != null) {
        version = (String)getXMLVersion.invoke(document, 
          null);
      }
    }
    catch (Exception localException)
    {
    }
    return version;
  }

  protected void writeAnyNode(Node node) throws ShellException
  {
    if (node == null) {
      return;
    }

    short type = node.getNodeType();
    switch (type) {
    case 9:
      write((Document)node);
      break;
    case 10:
      write((DocumentType)node);
      break;
    case 1:
      write((Element)node);
      break;
    case 5:
      write((EntityReference)node);
      break;
    case 4:
      write((CDATASection)node);
      break;
    case 3:
      write((Text)node);
      break;
    case 7:
      write((ProcessingInstruction)node);
      break;
    case 8:
      write((Comment)node);
      break;
    case 2:
    case 6:
    default:
      throw new ShellException(Messages.getString(
        "RuntimeError.18", Short.toString(type)));
    }
  }

  protected void write(Document node) throws ShellException {
    this.isXML11 = "1.1".equals(getVersion(node));
    if (this.isXML11)
      this.printWriter.println("<?xml version=\"1.1\" encoding=\"UTF-8\"?>");
    else {
      this.printWriter.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
    }
    this.printWriter.flush();
    write(node.getDoctype());
    write(node.getDocumentElement());
  }

  protected void write(DocumentType node) throws ShellException {
    this.printWriter.print("<!DOCTYPE ");
    this.printWriter.print(node.getName());
    String publicId = node.getPublicId();
    String systemId = node.getSystemId();
    if (publicId != null) {
      this.printWriter.print(" PUBLIC \"");
      this.printWriter.print(publicId);
      this.printWriter.print("\" \"");
      this.printWriter.print(systemId);
      this.printWriter.print('"');
    } else if (systemId != null) {
      this.printWriter.print(" SYSTEM \"");
      this.printWriter.print(systemId);
      this.printWriter.print('"');
    }

    String internalSubset = node.getInternalSubset();
    if (internalSubset != null) {
      this.printWriter.println(" [");
      this.printWriter.print(internalSubset);
      this.printWriter.print(']');
    }
    this.printWriter.println('>');
  }

  protected void write(Element node) throws ShellException {
    this.printWriter.print('<');
    this.printWriter.print(node.getNodeName());
    Attr[] attrs = sortAttributes(node.getAttributes());
    for (Attr attr : attrs) {
      this.printWriter.print(' ');
      this.printWriter.print(attr.getNodeName());
      this.printWriter.print("=\"");
      normalizeAndPrint(attr.getNodeValue(), true);
      this.printWriter.print('"');
    }

    if (node.getChildNodes().getLength() == 0) {
      this.printWriter.print(" />");
      this.printWriter.flush();
    } else {
      this.printWriter.print('>');
      this.printWriter.flush();

      Node child = node.getFirstChild();
      while (child != null) {
        writeAnyNode(child);
        child = child.getNextSibling();
      }

      this.printWriter.print("</");
      this.printWriter.print(node.getNodeName());
      this.printWriter.print('>');
      this.printWriter.flush();
    }
  }

  protected void write(EntityReference node) {
    this.printWriter.print('&');
    this.printWriter.print(node.getNodeName());
    this.printWriter.print(';');
    this.printWriter.flush();
  }

  protected void write(CDATASection node) {
    this.printWriter.print("<![CDATA[");
    this.printWriter.print(node.getNodeValue());
    this.printWriter.print("]]>");
    this.printWriter.flush();
  }

  protected void write(Text node) {
    normalizeAndPrint(node.getNodeValue(), false);
    this.printWriter.flush();
  }

  protected void write(ProcessingInstruction node) {
    this.printWriter.print("<?");
    this.printWriter.print(node.getNodeName());
    String data = node.getNodeValue();
    if ((data != null) && (data.length() > 0)) {
      this.printWriter.print(' ');
      this.printWriter.print(data);
    }
    this.printWriter.print("?>");
    this.printWriter.flush();
  }

  protected void write(Comment node) {
    this.printWriter.print("<!--");
    String comment = node.getNodeValue();
    if ((comment != null) && (comment.length() > 0)) {
      this.printWriter.print(comment);
    }
    this.printWriter.print("-->");
    this.printWriter.flush();
  }
}