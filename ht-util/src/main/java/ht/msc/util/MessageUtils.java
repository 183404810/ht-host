package ht.msc.util;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;


public class MessageUtils {
	private static Logger logger = LoggerFactory.getLogger(MessageUtils.class);

	/**
	 * 发送消息到UC
	 * @param content
	 */
	public static void sendMessage(String userId, String content){
		Map<String,String> params = new HashMap<String,String>();
		params.put("userId", userId);
		params.put("content",content);
		
		if("2.0".equals(LoginUtils.runVersion)){
			String url = "http://"+CommonUtil.getHostUrl()+"/bl-uc-web/notification_data/notify.json";
			boolean flag1 = postSendMessage(url, params);
			boolean flag2 = false;
			if(!flag1){
				url = "http://"+CommonUtil.getHostUrl()+"/notification_data/notify.json";
				flag2 = postSendMessage(url, params);
			}
			if(!flag1 && !flag2){
				throw new SCException("发送消息失败");
			}
		}else{
			try{
				String url = "http://"+CommonUtil.getHostUrl()+"/blf1-uc-web/itg_message/sendMsg.json";
				
				String str = HttpUtils.post(url, params);
				String result=JsonUtils.getValueByKey("result", str);
				String reusltCode=JsonUtils.getValueByKey("resultCode", result);
				if(!reusltCode.equals("0")){
					throw new SCException("发送消息失败");
				}
			}catch(Exception e){
				throw new SCException("发送消息失败");
			}
		}
		return;
	}
	
	private static boolean postSendMessage(String url, Map<String, String> params) {
		try {
			String res = HttpUtils.postWithAuthority(url, params);
			if (StringUtils.isNotEmpty(res)) {
				JSONObject json = (JSONObject) JSON.parse(res);
				if ("0".equals(json.getJSONObject("flag").getString("retCode"))) {
					return true;
				} else {
					logger.error("postSendMessage error: " + url + json);
				}
			}
		} catch (IOException e) {
			logger.error("postSendMessage error: " + url, e);
		}
		return false;
	}
	
	/**
	 * 发送消息到我的srm消息表
	 * @param entity 实现了BaseSrmMsgEntity接口单据头的实体
	 * @return 成功返回null,失败返回对应的出错信息
	 */
	@Deprecated
	public static String sendSrmMsg(BaseSrmMsgEntity msgEntity) {
		if (StringUtils.isEmpty(msgEntity.getModuleNo()) || 
			StringUtils.isEmpty(msgEntity.getSrmModuleNo()) ||
			StringUtils.isEmpty(msgEntity.getBillStatus())) {
			return "原和目标系统模块编号和单据状态不能为空！";
		}
		String url = "http://" + CommonUtil.getHostUrl() + "/blf1-uc-web/itg_message/sendSrmMsg.json";
		HashMap<String, String> params = new HashMap<String, String>();
		try {
			for(Method method:BaseSrmMsgEntity.class.getDeclaredMethods()){
				try {
					String fieldName=ConvertGetMethodToFiledName(method);
					if (!StringUtils.isEmpty(fieldName)){
						Object val = method.invoke(msgEntity);
						if (val !=null){
							if (val instanceof Date){
								params.put(fieldName, "\""+DateUtil.format((Date)val, "yyyy-MM-dd HH:mm:ss")+"\"");//避免多转日期格式的转换
							}else{
								params.put(fieldName, val.toString());
							}
						}
					}
				} catch (Exception e) {
					logger.error("生成参数组失败:", e.getMessage());
					return e.getMessage();
				}
			}
			logger.info(" request uc sendMsg");
			logger.info("params:" + params);
			logger.info("url:" + url);
			String strResult = HttpUtils.post(url, params);
			logger.info("sendMsg  return :" + strResult);
			String result = JsonUtils.getValueByKey("result", strResult);
			String reusltCode = JsonUtils.getValueByKey("resultCode", result);
			if (!reusltCode.equals("0")) {
				logger.error("发送消息失败:", strResult);
				return result;
			}
		} catch (IOException e) {
			logger.error("发送消息失败:", e);
			return e.getMessage();
		}
		return null;
	}
	
	/**
	 * get方法属性名转成字段名
	 * @param method get方法属性名
	 * @return
	 */
	public static String ConvertGetMethodToFiledName(Method method){
		String methodName = method.getName();
		if (methodName.length()>3 && methodName.substring(0, 3).equals("get")){
			StringBuffer sb = new StringBuffer();
			sb.append(methodName.substring(3, 4).toLowerCase());
			if (methodName.length()>4){
				sb.append(methodName.substring(4,methodName.length()));
			}
			return sb.toString();
		}
		return null;
	}
}
