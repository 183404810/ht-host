package ht.plugin.configration;

import java.io.File;
import java.io.InputStream;

public interface Parser {
	public Configration parser(String path);
	public Configration parser(File file);
	public Configration parser(InputStream is);
}
