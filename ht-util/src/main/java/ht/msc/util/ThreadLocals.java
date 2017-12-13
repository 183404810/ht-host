package ht.msc.util;

import java.util.HashMap;

public class ThreadLocals {

	public static final ThreadLocal<HashMap<String,String>> headersThreadLocal=new ThreadLocal<HashMap<String,String>>();
	
	public static final ThreadLocal<String> errorMsgs = new ThreadLocal<String>();
	
	public static final ThreadLocal<String> moduleIdThreadLocal = new ThreadLocal<String>();
	
	/**
	 * 日志查询时，主表module
	 */
	public static final ThreadLocal<String> logMasterModuleThreadLocal=new ThreadLocal<String>();
	
	/**
	 * 不需要过滤的表， 多个表用逗号隔开
	 */
	public static final ThreadLocal<String> noFilterDataThreadLocal=new ThreadLocal<String>();
	
	/**
	 * 加上非空不过滤
	 */
	public static final ThreadLocal<String> addNoNullFilterThreadLocal=new ThreadLocal<String>();
	
	/**
	 * 需要统计的属性名，多个用逗号隔开
	 */
	public static final ThreadLocal<String> sumSqlThreadLocal=new ThreadLocal<String>();
	
	 public static void setErrorMsgs(String msg)
	    {
		 errorMsgs.set(errorMsgs.get()+msg);
	    }

	    public static String getErrorMsgs()
	    {
	        return errorMsgs.get();
	    }
	    
	    
	public static void setHeaderInfo(HashMap<String,String> headerMap){
		headersThreadLocal.set(headerMap);
	}
	
	public static HashMap<String,String> getHeaderInfo(){
		return headersThreadLocal.get();
	}
	
	public static void setModuleId(String moduleId){
		moduleIdThreadLocal.set(moduleId);
	}

	public static String getModuleId(){
		return moduleIdThreadLocal.get();
	}
	
	public static String getLogMasterModule(){
		return logMasterModuleThreadLocal.get();
	}
	
	public static void setLogMasterModule(String moduleName){
		logMasterModuleThreadLocal.set(moduleName);
	}
	
	public static void setNoFilterData(String noFilterData){
		noFilterDataThreadLocal.set(noFilterData);
	}
	
	public static String getNoFilterData(){
		return noFilterDataThreadLocal.get();
	}
	
	public static void clearNoFilterData(){
		noFilterDataThreadLocal.remove();
	}
	
	public static void setAddNoNullFilter(String addNoNullFilter){
		addNoNullFilterThreadLocal.set(addNoNullFilter);
	}
	
	public static String getAddNoNullFilter(){
		return (String)addNoNullFilterThreadLocal.get();
	}
	
	public static String getSumSqlThreadLocal(){
		return (String)sumSqlThreadLocal.get();
	}
	
	public static void setSumSqlThreadLocal(String sumPropertys ){
		sumSqlThreadLocal.set(sumPropertys);
	}
	
	public static void clearSumSqlThreadLocal(){
		sumSqlThreadLocal.remove();
	}
}
