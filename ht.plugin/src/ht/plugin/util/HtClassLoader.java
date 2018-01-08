package ht.plugin.util;

import ht.plugin.exception.ClassLoaderException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class HtClassLoader extends ClassLoader{
	private String classPath;
	
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
