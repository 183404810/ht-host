package ht.msc.util;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;

public class BillUtils {

	private BillUtils(){
		
	}
	private static Logger logger = LoggerFactory.getLogger(BillUtils.class);
	
	/**
	 * 获得单据编号
	 * @billType 单据类型
	 * @detail 对象的json字符串
	 * @return
	 */
	public static String getBillNo(String billType,String objJson){
		 //String url = "http://172.20.50.73:8080/blf1-mdm-web/bas_system_code/getSheetIdCode.json";
		  String url = "http://"+CommonUtil.getHostUrl()+"/blf1-mdm-web/bas_system_code/getSheetIdCode.json";
	      HashMap<String,String> params = new HashMap<String,String>();
	      String ret;
	      params.put("billtypeNo",billType);
	      if(StringUtils.isNotBlank(objJson)){
	    	  params.put("detail", objJson);
	      }
			try {
				logger.info(" request mdm getSheetIdCode");
				logger.info("params:"+params);
				logger.info("url:"+url);
				String str = HttpUtils.post(url, params);
				logger.info("getSheetIdCode  return :"+str);
			    String result=JsonUtils.getValueByKey("result", str);
			    String reusltCode=JsonUtils.getValueByKey("resultCode", result);
			    if(reusltCode.equals("0")){
			    	ret= JsonUtils.getValueByKey("sheetIdCode", str);
			    	if(StringUtils.isBlank(ret)){
			    		logger.error("获取单据失败:", str);
			    		return null;
			    	}
			    	return ret;
			    }else{
			    	logger.error("获取单据失败:", str);
			    }
			} catch (IOException e) {
				logger.error("获取单据失败:", e);
			}
	       return null;
	}
	
	
	/**
	 * 获取单据号
	 * @param billType  单据类型
	 * @param obj 具体对象
	 * @return
	 */
	public static String getBillNo(String billType,Object obj){
		String objJson="";
		if(obj!=null){
			objJson=JSON.toJSONString(obj);
		}
		return getBillNo( billType, objJson);
	}
	
	/**
	 * 获取单据号，指定IP加端口
	 * @param billType
	 * @param obj
	 * @param hostAndPort
	 * @return
	 */
	public static String getBillNo(String billType,Object obj,String hostAndPort ){
			String objJson="";
			if(obj!=null){
				objJson=JSON.toJSONString(obj);
			}
		  String url = "http://"+hostAndPort+"/blf1-mdm-web/bas_system_code/getSheetIdCode.json";
	      HashMap<String,String> params = new HashMap<String,String>();
	      String ret;
	      params.put("billtypeNo",billType);
	      if(StringUtils.isNotBlank(objJson)){
	    	  params.put("detail", objJson);
	      }
			try {
				logger.info(" request mdm getSheetIdCode");
				logger.info("params:"+params);
				logger.info("url:"+url);
				String str = HttpUtils.post(url, params);
				logger.info("getSheetIdCode  return :"+str);
			    String result=JsonUtils.getValueByKey("result", str);
			    String reusltCode=JsonUtils.getValueByKey("resultCode", result);
			    if(reusltCode.equals("0")){
			    	ret= JsonUtils.getValueByKey("sheetIdCode", str);
			    	if(StringUtils.isBlank(ret)){
			    		logger.error("获取单据失败:", str);
			    		return null;
			    	}
			    	return ret;
			    }else{
			    	logger.error("获取单据失败:", str);
			    }
			} catch (IOException e) {
				logger.error("获取单据失败:", e);
			}
	       return null;
	}
	
	/**
	 * 设置单据号及编辑人和时间等
	 * @param obj
	 * @param fieldname
	 * @param clazz
	 * @param systemUser
	 * @param date
	 */
	public static void setBillNoAndCreator(Object obj,
			Class<?> clazz,SystemUser systemUser,Date date,String billNo){
		List<String> fieldList=CommonUtil.getFieldNames(obj);
		if(CommonUtil.isContainsField(fieldList, SysConstans.BIll_NO)){
			CommonUtil.setFieldValue(obj, SysConstans.BIll_NO, clazz, billNo);
		}
		if(CommonUtil.isContainsField(fieldList,SysConstans.CREATOR_FIELDNAME)){
		   CommonUtil.setEntityDefaultField(obj, 0, systemUser,clazz,date);//设置时间与编辑人
		}
	}

	/**
	 * 审核单据，此方法处理了同一个单被多次审核问题
	 * （只更新修改时间、修改人、审核时间、审核人、及状态，如果需要更新其他的字段则自己实现）
	 * @param obj 需要更新的实体对象
	 * @param systemUser
	 */
	public static void billAudit(Object obj,SystemUser systemUser){
		billAudit(obj, systemUser, "20");
	}
	
	/**
	 * 审核单据，此方法处理了同一个单被多次审核问题
	 * （只更新修改时间、修改人、审核时间、审核人、及状态，如果需要更新其他的字段则自己实现）
	 * @param obj 需要更新的实体对象
	 * @param systemUser 
	 * @billStatus 审核状态  
	 */
	public static void billAudit(Object obj,SystemUser systemUser,String billStatus){
		@SuppressWarnings("unchecked")
		BaseSqlDao<Object> baseSqlDao=(BaseSqlDao<Object>) SpringComponent.getBean("baseSqlDao");
	    String billNo=CommonUtil.getFieldValue(obj, "billNo", obj.getClass()).toString();
	    String divisionNo=(String)CommonUtil.getFieldValue(obj, "divisionNo", obj.getClass());
	    String orgDivisionNo=(String)CommonUtil.getFieldValue(obj, "orgDivisionNo", obj.getClass());
	    String className=obj.getClass().getName();
	    int lastIndex=className.lastIndexOf(".");
	   	String entityName=className.substring(lastIndex+1);
	   	entityName=CommonUtil.changeFirstCharUporLow(entityName, 1);
	   	String tableName=CommonUtil.convertJaveBeanStrToUnderLine(entityName);
	   	Date date=new Date();
	   	CommonUtil.setEntityDefaultField(obj, 1, systemUser, obj.getClass(), date);
	   	CommonUtil.setFieldValue(obj, "auditor",  obj.getClass(), systemUser.getUserName());
		CommonUtil.setFieldValue(obj, "auditTime",  obj.getClass(),date);
		CommonUtil.setFieldValue(obj, "billStatus", obj.getClass(), billStatus);
		int affectedRows= baseSqlDao.updateBillNo(billNo, tableName, systemUser,
				billStatus,date,divisionNo,orgDivisionNo);
		if(affectedRows==0)throw new SCException("审核失败！单据"+billNo+"不存在或已被审核");
	}
	
	/**
	 * 
	 * 将任务写入dts队列表中
	 * @param business_No 主题编号 
	 * @param business_Name  主题名称
	 * @param reqParams 条件参数
	 */
	public static String writeDtsTopic(String business_No,String business_Name,Map<String,Object> reqParams){
		
		String url = "http://"+CommonUtil.getHostUrl()+"/blf1-dtsc-web/dtscAPI/PostDts.json";
		String taskId;
		 Map<String,String> params = new HashMap<String,String>();
		 params.put("business_Name", business_Name);
		 params.put("business_No", business_No);
		 params.put("reqParams", JSON.toJSONString(reqParams));
		try {
			logger.info(url);
			String str = HttpUtils.post(url, params);
			String reusltStr=JsonUtils.getValueByKey("result", str);
			String reusltCode=JsonUtils.getValueByKey("resultCode", reusltStr);
			if("0".equals(reusltCode)){
				 taskId=JsonUtils.getValueByKey("taskId", str);
				 if(StringUtils.isBlank(taskId)){
					 logger.error("获取队列任务号失败:");
					 return null;
				 }
			}else{
					 logger.error("获取队列任务号失败:");
					 return null;
			}
			return taskId;
		} catch (IOException e) {
			logger.error("写入DTS队列表失败:", e);
		}
		return "";
	}
	
	
	/**
	 * 
	 * 将任务写入dts队列表中
	 * @param dtsClient 指定是当前是哪个客户端
	 * @param business_No 主题编号 
	 * @param business_Name  主题名称
	 * @param reqParams 条件参数
	 */
	public static String writeDtsTopic(String dtsClient,String business_No,String business_Name,Map<String,Object> reqParams){
		
		String url = "http://"+CommonUtil.getHostUrl()+"/blf1-dtsc-web/dtscAPI/PostDts.json";
		String taskId;
		 Map<String,String> params = new HashMap<String,String>();
		 params.put("business_Name", business_Name);
		 params.put("business_No", business_No);
		 params.put("reqParams", JSON.toJSONString(reqParams));
		 params.put("dtsClient", dtsClient);
		try {
			logger.info(url);
			String str = HttpUtils.post(url, params);
			String reusltStr=JsonUtils.getValueByKey("result", str);
			String reusltCode=JsonUtils.getValueByKey("resultCode", reusltStr);
			if("0".equals(reusltCode)){
				 taskId=JsonUtils.getValueByKey("taskId", str);
				 if(StringUtils.isBlank(taskId)){
					 logger.error("获取队列任务号失败:");
					 return null;
				 }
			}else{
					 logger.error("获取队列任务号失败:");
					 return null;
			}
			return taskId;
		} catch (IOException e) {
			logger.error("写入DTS队列表失败:", e);
		}
		return "";
	}
}
