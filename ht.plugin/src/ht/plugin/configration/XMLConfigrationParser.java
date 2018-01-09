package ht.plugin.configration;

import ht.plugin.exception.XMLParserException;
import ht.plugin.properties.Constant;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
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
		Properties properties;
		for(int i=0;i<nodes.getLength();i++){
			Node node=nodes.item(i);
			if(node.getNodeType()!=1) continue;
			properties=parserAttribute(node);			
			if("properties".equals(node.getNodeName())){
				String url=properties.getProperty("url");
				try {
					URL openUrl=new URL(url);
					InputStream inStream=openUrl.openStream();
					this.getProperties().load(inStream);
				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}else if("classPathEntry".equals(node.getNodeName())){
				String classUrl=properties.getProperty("location");
				config.getClassEntry().add(classUrl);
			}else if("context".equals(node.getNodeName())){
				parserContext(node,config);
			}
		}
	}
	
	private void parserContext(Node node,Configration config){
		NodeList nlist=node.getChildNodes();
		for(int i=0;i<nlist.getLength();i++){
			Node cnode=nlist.item(i);
			if(cnode.getNodeType()!=Node.ELEMENT_NODE) continue;
			switch(cnode.getNodeName()){
				case "property":
					
			
			
			
			}
			
			
		}
		
		
		
		
	}
	
	private Properties parserAttribute(Node node){
		Properties properties=new Properties();
		NamedNodeMap list=node.getAttributes();
		for(int i=0;i<list.getLength();i++){
			String value=parserTokens(list.item(i).getNodeValue());
			properties.put(list.item(i).getNodeName(),value);
		}
		return properties;
	}
	
	private String parserTokens(String tokens){
		if(tokens==null || "".equals(tokens.replace(" ", ""))) 
			return tokens;
		String rstr=tokens;
		int start=rstr.indexOf(Constant.xmlTokenStart);
		int end=rstr.indexOf(Constant.xmlTokenEnd);
		while(start>-1 && end>start){
			String pro=rstr.substring(0,start);
			String last=rstr.substring(end+Constant.xmlTokenEnd.length());
			String key=rstr.substring(start+Constant.xmlTokenStart.length(),end);
			if(key!=null && !"".equals(key)){
				rstr=pro+this.properties.getProperty(key)+last;
			}
			start=rstr.indexOf(Constant.xmlTokenStart);
			end=rstr.indexOf(Constant.xmlTokenEnd);
		}
		return rstr;
	}
}
