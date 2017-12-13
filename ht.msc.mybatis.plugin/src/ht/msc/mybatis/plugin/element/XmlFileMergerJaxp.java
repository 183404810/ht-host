package ht.msc.mybatis.plugin.element;


import ht.msc.mybatis.plugin.GeneratedXmlFile;
import ht.msc.mybatis.plugin.Messages;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


public class XmlFileMergerJaxp
{
  public static String getMergedSource(GeneratedXmlFile generatedXmlFile, File existingFile)
    throws ShellException
  {
    try
    {
      return getMergedSource(new InputSource(new StringReader(generatedXmlFile.getFormattedContent())), 
        new InputSource(new InputStreamReader(new FileInputStream(existingFile), "UTF-8")), 
        existingFile.getName());
    } catch (IOException e) {
      throw new ShellException(Messages.getString("Warning.13", 
        existingFile.getName()), e);
    } catch (SAXException e) {
      throw new ShellException(Messages.getString("Warning.13", 
        existingFile.getName()), e);
    } catch (ParserConfigurationException e) {
      throw new ShellException(Messages.getString("Warning.13", 
        existingFile.getName()), e);
    }
  }

  public static String getMergedSource(InputSource newFile, InputSource existingFile, String existingFileName)
    throws IOException, SAXException, ParserConfigurationException, ShellException
  {
    DocumentBuilderFactory factory = 
      DocumentBuilderFactory.newInstance();
    factory.setExpandEntityReferences(false);
    DocumentBuilder builder = factory.newDocumentBuilder();
    builder.setEntityResolver(new NullEntityResolver());

    Document existingDocument = builder.parse(existingFile);
    Document newDocument = builder.parse(newFile);

    DocumentType newDocType = newDocument.getDoctype();
    DocumentType existingDocType = existingDocument.getDoctype();

    if (!newDocType.getName().equals(existingDocType.getName())) {
      throw new ShellException(Messages.getString("Warning.12", 
        existingFileName));
    }

    Element existingRootElement = existingDocument.getDocumentElement();
    Element newRootElement = newDocument.getDocumentElement();

    NamedNodeMap attributes = existingRootElement.getAttributes();
    int attributeCount = attributes.getLength();
    for (int i = attributeCount - 1; i >= 0; i--) {
      Node node = attributes.item(i);
      existingRootElement.removeAttribute(node.getNodeName());
    }

    attributes = newRootElement.getAttributes();
    attributeCount = attributes.getLength();
    for (int i = 0; i < attributeCount; i++) {
      Node node = attributes.item(i);
      existingRootElement.setAttribute(node.getNodeName(), node
        .getNodeValue());
    }

    List<Node> nodesToDelete = new ArrayList();
    NodeList children = existingRootElement.getChildNodes();
    int length = children.getLength();
    Node node;
    for (int i = 0; i < length; i++) {
      node = children.item(i);
      if (isGeneratedNode(node))
        nodesToDelete.add(node);
      else if ((isWhiteSpace(node)) && 
        (isGeneratedNode(children.item(i + 1)))) {
        nodesToDelete.add(node);
      }
    }

    for (Node node1 : nodesToDelete) {
      existingRootElement.removeChild(node1);
    }

    children = newRootElement.getChildNodes();
    length = children.getLength();
    Node firstChild = existingRootElement.getFirstChild();
    for (int i = 0; i < length; i++) {
      Node node2 = children.item(i);

      if ((i == length - 1) && 
        (isWhiteSpace(node2)))
      {
        break;
      }

      Node newNode = existingDocument.importNode(node2, true);
      if (firstChild == null)
        existingRootElement.appendChild(newNode);
      else {
        existingRootElement.insertBefore(newNode, firstChild);
      }

    }

    return prettyPrint(existingDocument);
  }

  private static String prettyPrint(Document document) throws ShellException {
    DomWriter dw = new DomWriter();
    String s = dw.toString(document);
    return s;
  }

  private static boolean isGeneratedNode(Node node) {
    boolean rc = false;

    if ((node != null) && (node.getNodeType() == 1)) {
      Element element = (Element)node;
      String id = element.getAttribute("id");
      if (id != null) {
        for (String prefix : MergeConstants.OLD_XML_ELEMENT_PREFIXES) {
          if (id.startsWith(prefix)) {
            rc = true;
            break;
          }
        }
      }

      if (!rc)
      {
        NodeList children = node.getChildNodes();
        int length = children.getLength();
        for (int i = 0; i < length; i++) {
          Node childNode = children.item(i);
          if (!isWhiteSpace(childNode))
          {
            if (childNode.getNodeType() != 8) break;
            Comment comment = (Comment)childNode;
            String commentData = comment.getData();
            for (String tag : MergeConstants.OLD_ELEMENT_TAGS) {
              if (commentData.contains(tag)) {
                rc = true;
                break;
              }
            }
          }
        }

      }

    }

    return rc;
  }

  private static boolean isWhiteSpace(Node node) {
    boolean rc = false;

    if ((node != null) && (node.getNodeType() == 3)) {
      Text tn = (Text)node;
      if (tn.getData().trim().length() == 0) {
        rc = true;
      }
    }

    return rc;
  }

  private static class NullEntityResolver
    implements EntityResolver
  {
    public InputSource resolveEntity(String publicId, String systemId)
      throws SAXException, IOException
    {
      StringReader sr = new StringReader("");

      return new InputSource(sr);
    }
  }
}