package ht.plugin.configration;
import java.io.IOException;
import java.io.InputStream;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class ParserResolver implements EntityResolver{

	@Override
	public InputSource resolveEntity(String publicId, String systemId)
			throws SAXException, IOException {
		InputStream is = getClass().getClassLoader().getResourceAsStream(
				"ht/plugin/properties/mybatis-generator-config_1_0.dtd");
		InputSource ins = new InputSource(is);
		return ins;
	}
}
