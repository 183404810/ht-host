package ht.plugin.configration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

public abstract class AbstractParser implements Parser{
	protected Properties properties;
	private List<String> warnings;
	private List<String> parseErrors;
	
	@Override
	public void parser(String path,Configration config) throws Exception {
		 File file=new File(path);
		 if(file.exists())
			 parser(file,config);
	}

	@Override
	public void parser(File file,Configration config) throws Exception {
		InputStream is=null;
		try {
			is = new FileInputStream(file);
			if(is!=null)
				parser(is,config);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally{
				try {
					if(is!=null)
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}

	@Override
	public abstract void parser(InputStream is,Configration config) throws Exception;

	public Properties getProperties() {
		return properties;
	}

	public void setProperties(Properties properties) {
		this.properties = properties;
	}

	public List<String> getWarnings() {
		return warnings;
	}

	public void setWarnings(List<String> warnings) {
		this.warnings = warnings;
	}

	public List<String> getParseErrors() {
		return parseErrors;
	}

	public void setParseErrors(List<String> parseErrors) {
		this.parseErrors = parseErrors;
	}
}
