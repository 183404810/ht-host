package ht.plugin.configration;

import ht.plugin.configration.config.JDBCConfig;
import ht.plugin.configration.config.JavaFileConfig;
import ht.plugin.configration.config.TableSettingConfig;
import ht.plugin.context.PluginContext;
import ht.plugin.exception.XMLParserException;
import ht.plugin.properties.Constant;
import ht.plugin.properties.LayoutEnum;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
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
	
	public XMLConfigrationParser(){
		if(this.properties==null)
			this.properties=new Properties();
	}
	
 	@Override
	public void parser(InputStream is, Configration config) throws Exception {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setValidating(true);
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			builder.setEntityResolver(new ParserResolver());
			builder.setErrorHandler(new ParserErrorHanlder(this.warnings,this.parseErrors));
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
			config.getContext().putAll(this.properties);
			config.getContext().putAll(System.getProperties());
		} catch (ParserConfigurationException e) {
			this.parseErrors.add(e.getMessage());
		}
	}
	
	private void parserXml(Element rootNode,Configration config){
		NodeList nodes=rootNode.getChildNodes();
		Properties prop;
		for(int i=0;i<nodes.getLength();i++){
			Node node=nodes.item(i);
			if(node.getNodeType()!=1) continue;
			prop=parserAttribute(node);			
			if("properties".equals(node.getNodeName())){
				String url=prop.getProperty("url");
				try {
					URL openUrl=new URL(url);
					InputStream inStream=openUrl.openStream();
					this.properties.load(inStream);
				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}else if("classPathEntry".equals(node.getNodeName())){
				String classUrl=prop.getProperty("location");
				config.getClassEntry().add(classUrl);
			}else if("context".equals(node.getNodeName())){
				parserContext(node,config);
			}
		}
	}
	
	private void parserContext(Node node,Configration config){
		NodeList nlist=node.getChildNodes();
		PluginContext context=config.getContext();
		Map<String,JavaFileConfig> jConfig=new HashMap<>();
		config.setConfig(jConfig);
		Properties xmlPropertes=null;
		for(int i=0;i<nlist.getLength();i++){
			Node cnode=nlist.item(i);
			if(cnode.getNodeType()!=Node.ELEMENT_NODE) continue;
			xmlPropertes=parserAttribute(cnode);		
			switch(cnode.getNodeName()){
				case "property":
					String key=xmlPropertes.getProperty("name");
					String value=xmlPropertes.getProperty("value");
					context.setProperty(key,value);
					break;
				case "jdbcConnection":
					String url=xmlPropertes.getProperty("connectionURL");
					String driverClass=xmlPropertes.getProperty("driverClass");
					String userId=xmlPropertes.getProperty("userId");
					String password=xmlPropertes.getProperty("password");
					JDBCConfig dbConfig=new JDBCConfig(url,driverClass,userId,password);
					config.setDbConfig(dbConfig);
					break;
				case "tableSetting":
					TableSettingConfig tsconfig=new TableSettingConfig();
					config.setTbConfig(tsconfig);
					tsconfig.setSchema(Boolean.valueOf(xmlPropertes.get("isSchema").toString()));
					tsconfig.setEnableCountByExample(Boolean.valueOf(xmlPropertes.get("enableCountByExample").toString()));
					tsconfig.setEnableDeleteByExample(Boolean.valueOf(xmlPropertes.get("enableDeleteByExample").toString()));
					tsconfig.setEnableUpdateByExample(Boolean.valueOf(xmlPropertes.get("enableUpdateByExample").toString()));
					tsconfig.setEnableSelectByExample(Boolean.valueOf(xmlPropertes.get("enableSelectByExample").toString()));
					tsconfig.setSelectByExampleQueryId(Boolean.valueOf(xmlPropertes.get("selectByExampleQueryId").toString()));
					break;
				case "javaModelGenerator":
				case "sqlMapGenerator":
				case "javaServiceGenerator":
				case "javaDaoGenerator":
				case "javaControllerGenerator":
					
					JavaFileConfig fconfig=new JavaFileConfig();
					fconfig.setTargetPackage(xmlPropertes.getProperty("targetPackage"));
					fconfig.setEnableInterfaceSupInterfaceGenericity(Boolean.getBoolean(xmlPropertes.getProperty("enableInterfaceSupInterfaceGenericity")));
					fconfig.setEnableSupClassGenericity(Boolean.parseBoolean(xmlPropertes.getProperty("enableSupClassGenericity")));
					fconfig.setExtendSupClass(xmlPropertes.getProperty("extendSupClass"));
					fconfig.setImplementationPackage(xmlPropertes.getProperty("implementationPackage"));
					fconfig.setInterfaceExtendSupInterface(xmlPropertes.getProperty("interfaceExtendSupInterface"));
					fconfig.setTargetProject(xmlPropertes.getProperty("targetProject"));
					jConfig.put(cnode.getNodeName(), fconfig);
					NodeList childs=cnode.getChildNodes();
					for(int j=0;j<childs.getLength();j++){
						Node n=childs.item(i);
						if(n.getNodeType()==1){
							Properties ps=parserAttribute(n);
							fconfig.setProperty(ps.getProperty("name"), ps.getProperty("value"));
						}
					}
					break;
			}
		}
	}
	
	private Properties parserAttribute(Node node){
		Properties properties=new Properties();
		node.getNodeName();
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
