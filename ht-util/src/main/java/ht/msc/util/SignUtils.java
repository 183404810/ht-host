package ht.msc.util;

import java.util.Date;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

 
public class SignUtils {
	private static Logger logger = LoggerFactory.getLogger(SignUtils.class);
	/**
	 * 签名的KEY
	 * @return
	 */
	public static String getSign_key(){
		String sign_key=PropertiesUtils.getPropertieValue("sign_key");
		sign_key=StringUtils.isNotBlank(sign_key)?sign_key:SysConstans.SIGN_KEY;
		return sign_key;
	}
	
	
	public static String getSign(String req_time,String sign_key){
		return CommonUtil.md5(req_time+sign_key);
	}
	
	public static boolean isSignEquals(String sign,String req_time){
		    long currentTime=new Date().getTime();
		    long reqTime=Long.parseLong(req_time);
	   		if(Math.abs(currentTime-reqTime)<=3600000){//同一个时间请求两小时内失效(正负一小时)
	   			 String sign_key=CommonUtil.getSign_key();
	       		 if(sign.equals(SignUtils.getSign(req_time,sign_key)))
	       		 return true;
	   		 }
	   		if(logger.isDebugEnabled()){
				logger.debug("SignUtils local currentTime:"+currentTime+
						"   sign:"+SignUtils.getSign(req_time,CommonUtil.getSign_key()));
			}
		    return false;
	}
	
	/**
	 * 是否过滤带sign的请求
	 * 是返回true,否则false
	 * @param request
	 * @return
	 */
	public static boolean filterSignRequest(HttpServletRequest request){
		if(StringUtils.isNotBlank(request.getParameter(SysConstans.SIGN_VAR)) 
        		&&  StringUtils.isNotBlank(request.getParameter(SysConstans.REQ_DATETIME)) ){
        	String sign= request.getParameter(SysConstans.SIGN_VAR);
   		    String req_dateTime_str= request.getParameter(SysConstans.REQ_DATETIME);
   		    if(SignUtils.isSignEquals(sign, req_dateTime_str)){
	       		 return true;
   		    }
        }
		if(logger.isDebugEnabled()){
			logger.debug("from sign:"+request.getParameter(SysConstans.SIGN_VAR) 
					+"  req_dateTime_str:"+request.getParameter(SysConstans.REQ_DATETIME));
		}
		return false;
	}
	
	
	/**
	 * 保存当前线程 http请求的头信息
	 */
	public void setThreadInfo(HttpServletRequest httpRequest){
		HashMap<String,String> hashmap=new HashMap<String,String>();
		if(StringUtils.isNotBlank(httpRequest.getHeader("Referer"))){
			hashmap.put("Referer", httpRequest.getHeader("Referer"));
		}
		if(StringUtils.isNotBlank(httpRequest.getHeader("Cookie"))){
			hashmap.put("Cookie", httpRequest.getHeader("Cookie"));
		}
		ThreadLocals.setHeaderInfo(hashmap);
		
		if(httpRequest.getSession()!=null){
			SystemUser user=(SystemUser)httpRequest.getSession().
					getAttribute(SysConstans.SESSION_USER);
			if(user!=null){
				SessionUtils.set(SysConstans.SESSION_USER, user);
			}
		}
	}
}
