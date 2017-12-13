package ht.msc.mybatis.plugin;

import java.util.List;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;


public class ParserErrorHandler
  implements ErrorHandler
{
  private List<String> warnings;
  private List<String> errors;

  public ParserErrorHandler(List<String> warnings, List<String> errors)
  {
    this.warnings = warnings;
    this.errors = errors;
  }

  public void warning(SAXParseException exception)
    throws SAXException
  {
    this.warnings.add(Messages.getString("Warning.7", 
      Integer.toString(exception.getLineNumber()), exception
      .getMessage()));
  }

  public void error(SAXParseException exception)
    throws SAXException
  {
    this.errors.add(Messages.getString("RuntimeError.4", 
      Integer.toString(exception.getLineNumber()), exception
      .getMessage()));
  }

  public void fatalError(SAXParseException exception)
    throws SAXException
  {
    this.errors.add(Messages.getString("RuntimeError.4", 
      Integer.toString(exception.getLineNumber()), exception
      .getMessage()));
  }
}