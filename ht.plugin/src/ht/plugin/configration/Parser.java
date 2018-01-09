package ht.plugin.configration;

import java.io.File;
import java.io.InputStream;

public interface Parser {
	public void parser(String path,Configration config) throws Exception;
	public void parser(File file,Configration config) throws Exception;
	public void parser(InputStream is,Configration config) throws Exception;
}
