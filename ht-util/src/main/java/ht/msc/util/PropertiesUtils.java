package ht.msc.util;

import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 读取配置文件的值
 * Description: 
 * All rights Reserved, Designed ByBeLLE

 * ------------------------------------------
 */
public class PropertiesUtils {
	
	private PropertiesUtils(){
		
	}
	
	protected  static Logger logger = LoggerFactory.getLogger(PropertiesUtils.class);
			
	/**
	 * 获得配置文件的值
	 * @param key
	 * @return
	 */
	public static String getPropertieValue(String key){
		Properties pfb=(Properties)SpringComponent.getBean("configRealm");
		try {
			return pfb.getProperty(key);
		} catch (Exception e) {
			logger.error("",e);
			return "";
		}
	}
	
	/**
	 * 获得配置文件的值
	 * @param key
	 * @return
	 */
	public static String getString(String key){
		try {
			return getPropertieValue(key);
		} catch (Exception e) {
			logger.error("",e);
			return "";
		}
	}
	
	/**
	 * 获得配置文件的值
	 * @param key
	 * @return
	 */
	public static boolean getBoolean(String key){
		try {
			return Boolean.parseBoolean(getPropertieValue(key));
		} catch (Exception e) {
			logger.error("",e);
			return false;
		}
	}
	
	/**
	 * 获得配置文件的值
	 * @param key
	 * @return
	 */
	public static int getInteger(String key){
		try {
			return Integer.parseInt(getPropertieValue(key));
		} catch (Exception e) {
			logger.error("",e);
			return -1;
		}
	}
	
	/**
	 * 获得配置文件的值，配置为空时，返回defaultVal
	 * @param key
	 * @param defaultVal
	 * @return
	 */
	public static String getString(String key,String defaultVal){
		try {
			return StringUtils.isNotEmpty(getPropertieValue(key))? getPropertieValue(key):defaultVal;
		} catch (Exception e) {
			logger.error("",e);
			return "";
		}
	}
	
	/**
	 * 获得配置文件的值，配置为空时，返回defaultVal
	 * @param key
	 * @param defaultVal
	 * @return
	 */
	public static boolean getBoolean(String key,boolean defaultVal){
		try {
			return StringUtils.isNotEmpty(getPropertieValue(key))? Boolean.parseBoolean(getPropertieValue(key)):defaultVal;
		} catch (Exception e) {
			logger.error("",e);
			return false;
		}
	}
	
	/**
	 * 获得配置文件的值，配置为空时，返回defaultVal
	 * @param key
	 * @param defaultVal
	 * @return
	 */
	public static int getInteger(String key,int defaultVal){
		try {
			return StringUtils.isNotEmpty(getPropertieValue(key))? Integer.parseInt(getPropertieValue(key)):defaultVal;
		} catch (Exception e) {
			logger.error("",e);
			return -1;
		}
	}
	
}
