package ht.plugin.util;

import ht.plugin.configration.Configration;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

public class HtClassLoader extends ClassLoader{
	private String classPath;
	private static URLClassLoader urlLoader;
	
	public static Class<?> loadClass(Configration config) {
		List<String> classpath=config.getClassEntry();
		String driverName=config.getDbConfig().getDriverClass();
		Class<?> clazz=null;
		try{
			for(String str:classpath){
				File f=new File(str);
				URL url=f.toURI().toURL();
				urlLoader=new URLClassLoader(new URL[]{url});
				clazz=urlLoader.loadClass(driverName);
			}
		}catch(ClassNotFoundException e){
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		if(clazz==null){
			try {
				clazz=Thread.currentThread().getClass().getClassLoader().loadClass(driverName);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		return clazz;
	}
	
	
	public HtClassLoader(String classPath,ClassLoader parent){
		super(parent);
		this.classPath=classPath;
	}
	
	@Override
	public Class<?> findClass(String name){
		byte[] data=loadClassData(name);
		return defineClass(name,data,0,data.length);
	}
	
	private byte[] loadClassData(String name){
		name=name.replace("\\.", "/");
		try {
			FileInputStream ins=new FileInputStream(new File(classPath+name+".class"));
			ByteArrayOutputStream baos=new ByteArrayOutputStream();
			int b;
			while((b=ins.read())!=-1)
				baos.wait(b);
			return baos.toByteArray();
		} catch (FileNotFoundException e) {
			e.printStackTrace(); 
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} 
		return null;
	}
}
