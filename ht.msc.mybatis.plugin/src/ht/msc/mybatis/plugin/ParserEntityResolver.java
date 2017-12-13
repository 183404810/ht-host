package ht.msc.mybatis.plugin;

import java.io.IOException;
import java.io.InputStream;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


public class ParserEntityResolver
  implements EntityResolver
{
  public InputSource resolveEntity(String publicId, String systemId)
    throws SAXException, IOException
  {
    if ("-//Apache Software Foundation//DTD Apache iBATIS Ibator Configuration 1.0//EN".equalsIgnoreCase(publicId)) {
      InputStream is = getClass().getClassLoader().getResourceAsStream(
        "org/mybatis/generator/config/xml/ibator-config_1_0.dtd");
      InputSource ins = new InputSource(is);

      return ins;
    }
    if ("-//mybatis.org//DTD MyBatis Generator Configuration 1.0//EN"
      .equalsIgnoreCase(publicId)) {
      InputStream is = getClass()
        .getClassLoader()
        .getResourceAsStream(
        "ht/msc/mybatis/plugin/mybatis-generator-config_1_0.dtd");
      InputSource ins = new InputSource(is);

      return ins;
    }
    return null;
  }
}