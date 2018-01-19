package ht.plugin.configration;

import ht.plugin.exception.XMLParserException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ConfigrationParser{
	private List<String> warnings;
	private List<String> parseErrors;
	
	
	public ConfigrationParser(List<String> warnings,List<String> parseErrors){
		if(warnings!=null)
			this.warnings=warnings;
		else
			this.warnings=new ArrayList<>();
			
		if(parseErrors!=null)
			this.parseErrors=parseErrors;
		else
			this.parseErrors=new ArrayList<>();
	}
	public void parser(AbstractParser parser,String path,Configration config) throws XMLParserException {
		try {
			parser(parser,new File(path), config);
		} catch (Exception e) {
			if(this.parseErrors.size()>0)
				throw new XMLParserException(this.parseErrors);
			e.printStackTrace();
		}
 	}
	
	public void parser(AbstractParser parser,File file,Configration config) throws XMLParserException {
		try {
			parser.setParseErrors(this.parseErrors);
			parser.setWarnings(this.warnings);
			parser.parser(file, config);
			if (this.parseErrors.size() > 0) {
				throw new XMLParserException(this.parseErrors);
			}
		} catch (Exception e) {
			if(this.parseErrors.size()>0)
				throw new XMLParserException(this.parseErrors);
			e.printStackTrace();
		}
 	}
}
