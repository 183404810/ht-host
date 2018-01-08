package ht.plugin.configration;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class ConfigrationParser extends AbstractParser{

	@Override
	public Configration parser(InputStream is) {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setValidating(true);
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			builder.setEntityResolver(new ParserResolver());
			builder.setErrorHandler(new ParserErrorHanlder());
			Document document = null;
		     
			try {
				document = builder.parse(is);
			} catch (SAXException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		
		
		return null;
	}

}
