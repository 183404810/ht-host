package ht.plugin.configration;

import java.util.List;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class ParserErrorHanlder implements ErrorHandler{
	private List<String> warnings;
	private List<String> errors;
	
	public ParserErrorHanlder(List<String> warnings,List<String> errors){
		this.warnings=warnings;
		this.errors=errors;
	}

	@Override
	public void warning(SAXParseException exception) throws SAXException {
		this.warnings.add(exception.getMessage());
	}

	@Override
	public void error(SAXParseException exception) throws SAXException {
		exception.printStackTrace();
		this.errors.add(exception.getMessage());
	}

	@Override
	public void fatalError(SAXParseException exception) throws SAXException {
		this.errors.add(exception.getMessage());
	}
}
