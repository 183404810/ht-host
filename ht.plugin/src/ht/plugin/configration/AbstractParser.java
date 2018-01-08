package ht.plugin.configration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public abstract class AbstractParser implements Parser{

	@Override
	public Configration parser(String path) {
		 File file=new File(path);
		 if(file.exists())
			 return parser(file);
		 return null;
	}

	@Override
	public Configration parser(File file) {
		InputStream is=null;
		try {
			is = new FileInputStream(file);
			if(is!=null)
				return parser(is);
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
		return null;
	}

	@Override
	public abstract Configration parser(InputStream is);	
}
