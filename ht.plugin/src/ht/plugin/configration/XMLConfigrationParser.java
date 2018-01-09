package ht.plugin.configration;

import ht.plugin.exception.XMLParserException;
import ht.plugin.properties.Constant;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XMLConfigrationParser extends AbstractParser{
	private List<String> warnings;
	private List<String> parseErrors;
	
	@Override
	public void parser(InputStream is, Configration config) throws Exception {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setValidating(true);
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			builder.setEntityResolver(new ParserResolver());
			builder.setErrorHandler(new ParserErrorHanlder(warnings,parseErrors));
			Document document = null;
		     
			try {
				document = builder.parse(is);
			} catch (SAXException e) {
				this.parseErrors.add(e.getMessage());
			} catch (IOException e) {
		        this.parseErrors.add(e.getMessage());
			}
			
			if (this.parseErrors.size() > 0) {
				throw new XMLParserException(this.parseErrors);
			}
			Element rootNode = document.getDocumentElement();
			DocumentType docType = document.getDoctype();
			if(!Constant.docTypePublicId.equals(docType.getPublicId())){
				throw new XMLParserException("暂时只能支持myBatis文档！");
			}
			
			parserXml(rootNode,config);
		} catch (ParserConfigurationException e) {
			this.parseErrors.add(e.getMessage());
		}
	}
	
	private void parserXml(Element rootNode,Configration config){
		NodeList nodes=rootNode.getChildNodes();
		for(int i=0;i<nodes.getLength();i++){
			Node node=nodes.item(i);
			if("classPathEntry".equals(node.getNodeName())){
				 
			}
		}
	}
	
	private Properties parserAttribute(Node node){
		Properties properties=new Properties();
		NamedNodeMap list=node.getAttributes();
		for(int i=0;i<list.getLength();i++){
			properties.put(list.item(i).getNodeName(), list.item(i).getNodeValue());
		}
		return properties;
	}
	
	private String parserTokens(String tokens){
		
		
	}

}
