package ht.msc.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;

public class LoginUtils {
private LoginUtils(){
		
	}
	
	private static Logger logger = LoggerFactory.getLogger(LoginUtils.class);
	
	/**
	 * 模块url参数名
	 */
	private static String sys_menu_moduleUrl="sys_menu_moduleUrl";
	
	/*
	 * 按钮操作权限
	 */
	static Map<String, Integer> rmap=new HashMap<String, Integer>();
	/*
	 * 操作权限不拦截的url
	 */
	private static List<String> excludeUrlsList=new ArrayList<String>();
	
	//当前运行的版本 1.0还是2.0
	public static String runVersion;

	static{
		excludeUrlsList.add("/blf1-uc-web/logout.json");
		excludeUrlsList.add("/blf1-uc-web/clearUserCache.json");
		excludeUrlsList.add("/blf1-uc-web/getAppUrlList.json");
		excludeUrlsList.add("/blf1-uc-web/itg_menu_list/getusermenulist.json");
		excludeUrlsList.add("/blf1-uc-web/getCurrentLoginUser.json");
		excludeUrlsList.add("/load_service.json");
		excludeUrlsList.add("/index.html");
		excludeUrlsList.add("/srm.html");
		excludeUrlsList.add("/blf1-uc-web/itg_menu_list/reloadAllUserRedisCache.json");
		/**
		 * 模块的right_value大于等于下列值则拥有相应操作权限
		 * 1 浏览
		 * 2 编辑
		 * 4 增加
		 * 8 删除
		 * 16 打印设置
		 * 32 打印
		 * 64 导出
		 * 128 赋权
		 * 256 权限传递
		 */
		rmap.put("/add.json", 4);
		rmap.put("/deleteById.json", 8);
		rmap.put("/modifyById.json", 2);
		rmap.put("/get.json", 1);
		rmap.put("/list.json", 1);
		rmap.put("/getvo.json", 1);
		rmap.put("/listvo.json", 1);
		rmap.put("/listvoAll.json", 1);
		rmap.put("/listAll.json", 1);
		rmap.put("/batchOperate.json", 8);
		rmap.put("/listsave.json", 8);
		rmap.put("/addMasterCustomer.json", 8);
		rmap.put("/saveMasterCustomerList.json", 8);
		rmap.put("/audit.json", 2);
		rmap.put("/saveSizeHorizontalData.json", 2);
		rmap.put("/do_export", 64);
		//rmap.put("/do_master_customer_export", 64);
		//rmap.put("/importExcel.json", 64);
	}
	
	/**
	 * 加载操作权限拦截例外url(加载配置文件)
	 */
	public static void loadExcludeUrlsList(){
		String liststr=PropertiesUtils.getString("loginUtils.unauthorizedExcludeUrls");
		loadExcludeUrlsList(liststr);
	}
	
	/**
	 * 加载操作权限拦截例外url
	 * @param liststr
	 */
	public static void loadExcludeUrlsList(String liststr){
		logger.info("loadExcludeUrlsList statr. liststr:"+liststr);
		if(StringUtils.isNoneEmpty(liststr)){
			String arr[]=liststr.split(",");
			for(String tmp:arr){
				if(excludeUrlsList.contains(tmp)==false){
					excludeUrlsList.add(tmp);
				}
			}
		}
		logger.info("loadExcludeUrlsList end. excludeUrlsList:"+excludeUrlsList);
	}
	
	/**
	 * 过滤ajax请求。未登录返回timeout
	 * @param request
	 * @param response
	 */
	public static boolean filterXMLHttpRequest(final HttpServletRequest request, final HttpServletResponse response){
		String requestType =(String) request.getHeader("X-Requested-With"); 
		logger.debug("RequestURI:"+request.getRequestURI());
		boolean ret=false;
		try {
			if (requestType != null && "XMLHttpRequest".equals(requestType)) {
				responseOutJson(response, getLoginOutResult());
				ret=true;
			}
		} catch (Exception e) {
			logger.error("error:",e);
		}
		return ret;
	}
	
	/**
	 * 过滤操作权限
	 * @param request
	 * @param response
	 * @return 无权限true;有权限false
	 */
	public static boolean filterUnauthorizedRequest(
			final HttpServletRequest request, final HttpServletResponse response,SystemUser user) {
		try {
			//非超级管理员需要过滤操作权限
			if(user.getSuAdminRoleId()!=null&&user.getSuAdminRoleId()==0){
				//@lt 1.0则做资源权限的校验,2.0则不做直接返回
				if(runVersion.equals("1.0")){
					return filterUnauthorizedRequest(request, response, user.getUserMenuMap());
				}
				return false;
			}
			else{
				return false;
			}
		} catch (Exception e) {
			logger.error("error:",e);
		}
		return false;
	}
	
	/**
	 * 过滤操作权限
	 * @param request
	 * @param response
	 * @return 无权限true;有权限false
	 */
	public static boolean filterUnauthorizedRequest(
			final HttpServletRequest request, final HttpServletResponse response,HashMap<String, String> userMenuMap) {
		String url=request.getRequestURI(); //"/blf1-uc-web/itg_roleright/list.json";
		boolean ret=false;
		try {
			String moduleUrl=request.getParameter(sys_menu_moduleUrl);
			logger.debug("moduleUrl:"+moduleUrl);
			if(unauthorizedExcludeUrls(url)||userMenuMap==null){
	    		return false;
	    	}
			else if(StringUtils.isNotEmpty(moduleUrl)&&"/blf1-uc-web/main".contains(moduleUrl)){
				return false;
			}
			
			boolean operatFlag=constructValidOperateUrl(url,userMenuMap,moduleUrl);
			if (operatFlag==false) {	
				logger.debug("RequestURI Unauthorized url:"+url);
				responseOutJson(response, getUnauthorized(url));
				ret=true;
			}
		} catch (Exception e) {
			logger.error("error:",e);
		}
		return ret;
	}
	
	/**
	 * 操作权限不拦截的url检测
	 * @param url
	 * @return 不拦截的url返回true,否则返回false
	 */
	public static boolean unauthorizedExcludeUrls(String url) {
		if (CommonUtil.isExtension(url,"json")) {
            for (String excludeUrl: excludeUrlsList) {
                if (url.contains(excludeUrl)) {
                    return true;
                }
            }
        }
        else{
        	return true;
        }
        
		return false;
    }
	
	/**
	 * 检测请求URI是否拥有操作权限
	 * @param url 请求url
	 * @param userMenuMap
	 * @param moduleUrl 来源模块url
	 * @return 有权限true,无false
	 */
	public static boolean constructValidOperateUrl(String url,HashMap<String, String> userMenuMap,String moduleUrl) {
		boolean rightFlag=false;
		String arr[]=url.substring(1).split("/");
		String right_value="";
		String operateUrl="/"+arr[2];
		if(StringUtils.isNotEmpty(moduleUrl)){
			///blf1-mdm-web/basbrandrelation
			right_value=userMenuMap.get(moduleUrl);
			rightFlag=checkOperateRight(operateUrl, right_value);
		}
		else if(arr.length>2){
			//原：/blf1-uc-web/itg_roleright/list.json
			//新：/blf1-uc-web/itgroleright
			String url_new="/"+arr[0]+"/"+arr[1].replace("_", "");
			right_value=userMenuMap.get(url_new);
			
			//主从表请求url处理
			if(url_new.lastIndexOf("dtl")>=0){
				right_value=userMenuMap.get(url_new.substring(0,url_new.lastIndexOf("dtl")));
			}
			rightFlag=checkOperateRight(operateUrl, right_value);
		}
		else{
			//没有模块路径的默认都有权限。
			//如/blf1-uc-web/reLoadExcludeUrlsList.json,/blf1-uc-web/load_service.json
			rightFlag=true;
		}
		return rightFlag;
	}
	
	/**
	 * 检测菜单拥有的操作权限
	 * 模块的right_value大于等于下列值则拥有相应操作权限
	 * 1 浏览
	 * 2 编辑
	 * 4 增加
	 * 8 删除
	 * 16 打印设置
	 * 32 打印
	 * 64 导出
	 * 128 赋权
	 * 256 权限传递
	 * @param operateUrl 如：list.json,listAll.json
	 * @param right_value
	 * @param moduleUrl 如：/blf1-mdm-web/basbrandrelation
	 * @return 有权限true,无false
	 */
	public static boolean checkOperateRight(String operateUrl, String right_value) {
		boolean rightFlag=false;
		logger.debug("operateUrl:"+operateUrl+" right_value:"+right_value);
		
		if(StringUtils.isEmpty(right_value)){
			rightFlag=false;
		}
		else if(rmap.get(operateUrl)!=null){
			int val=rmap.get(operateUrl);
			if(Integer.parseInt(right_value)>=val){
				rightFlag=true;
			}
			else{
				rightFlag=false;
			}
		}
		else if(Integer.parseInt(right_value)>0){
			//没有定义按钮级别权限的默认有权限
			rightFlag=true;
		}
		return rightFlag;
	}

	
	/** 
     * 以JSON格式输出 
     * @param response 
     */  
	public static void responseOutJson(HttpServletResponse response, Object responseObject) {  
        response.setCharacterEncoding("UTF-8");  
        response.setContentType("application/json; charset=utf-8");  
        PrintWriter out = null;  
        try {
        	String outstr=JSON.toJSONString(responseObject);
            out = response.getWriter();  
            out.append(outstr);  
            logger.debug("response:"+outstr);  
            response.flushBuffer();
        } catch (IOException e) {
        	logger.error("error:",e);  
        } finally {  
            if (out != null) {  
                out.close();  
            }  
        }  
    }  
	
	/**
     * 返回TimeOut
     * @return
     */
	public static  Map<String,Object> getLoginOutResult(){
    	Map<String, Object> resultMap =new HashMap<String, Object>();
    	ResultModel resultModel = new ResultModel();
    	resultModel.setResultCode("timeout");
    	resultModel.setMsg("未登录或登录超时，请重新登录。");
    	resultMap.put("result", resultModel);
		return resultMap;
    }
	
	/**
     * 返回无权限访问该资源
     * @return
     */
	public static  Map<String,Object> getUnauthorized(String reqURI){
    	Map<String, Object> resultMap =new HashMap<String, Object>();
    	ResultModel resultModel = new ResultModel();
    	resultModel.setResultCode("unauthorized");
    	resultModel.setMsg("当前登录用户无权限访问资源:"+reqURI);
    	resultMap.put("result", resultModel);
		return resultMap;
    }

	   
    /**
	 * 从session或Redis缓存取登录用户对象
	 * @param loginName
	 * @return
	 */
	public static SystemUser getUserBySessionOrRedis(String loginName){
		SystemUser ret=null;
		
		if(StringUtils.isNotEmpty(loginName)){
			 Subject currentUser = SecurityUtils.getSubject();  
		     if(null != currentUser){
		        	Session session=currentUser.getSession();
		        	if(session!=null){
		        		Object userObj=session.getAttribute(SysConstans.SESSION_USER);
		        		if(userObj!=null){
		        			SystemUser user=(SystemUser) userObj;
		        			if(loginName.equals(user.getUserCode())&&StringUtils.isNotEmpty(user.getPassword())){
		        				ret=user;
		        			}
		        		}
		        	}
		     }
		     
		     if(ret==null){
		    	 ShiroSession shiroSession=(ShiroSession) SpringComponent.getBean("ShiroSession");
		    	 ret=shiroSession.getUserByLoginNamePassword(loginName, null);
		     }
		}
		String ret_log=(ret==null)? " ret:null":" ret userCode:"+ret.getUserCode()+" UserName:"+ret.getUserName() + " userId:"+ret.getUserId();
		logger.debug("getUserBySessionOrRedis() loginName:"+loginName+ret_log);
			
		return ret;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		/*HashMap<String, String> userMenuMap=new HashMap<>();
		userMenuMap.put("/blf1-sd-web/sdordermettinginfo", "127");
		userMenuMap.put("/blf1-uc-web/itgroleright", "11");
		userMenuMap.put("/blf1-mdm-web/basdeliverypoint", "0");
		userMenuMap.put("/blf1-uc-web/itgroleright", "127");*/
		//System.out.println("ret:"+checkOperateRight("/blf1-uc-web/itg_roleright/list.json", userMenuMap));
		//System.out.println("ret:"+checkOperateRight("/blf1-uc-web/itg_roleright/listvo.json", userMenuMap));
		//System.out.println("ret:"+checkOperateRight(url, userMenuMap));
		//System.out.println("ret:"+checkOperateRight(url, userMenuMap));
		/*String url="/blf1-uc-web/itg_rolerightdtl/list.json";
		System.out.println("ret:"+checkOperateRight(url, userMenuMap));*/
		/*System.out.println(unauthorizedExcludeUrls("/blf1-pd-web/load_service.json"));
		System.out.println(unauthorizedExcludeUrls("/blf1-uc-web/logout.json"));
		System.out.println(unauthorizedExcludeUrls("/blf1-uc-web/clearUserCache.json"));
		System.out.println(unauthorizedExcludeUrls("/blf1-uc-web/getAppUrlList.json"));
		System.out.println(unauthorizedExcludeUrls("/blf1-uc-web/itg_menu_list/getusermenulist.json"));
		System.out.println(unauthorizedExcludeUrls("/blf1-uc-web/getCurrentLoginUser.json"));
		System.out.println(unauthorizedExcludeUrls("/blf1-pd-web/load_service.json"));
		System.out.println(unauthorizedExcludeUrls("/blf1-pd-web/index.html"));
		System.out.println(unauthorizedExcludeUrls("/blf1-mdm-web/load_service.json"));
		System.out.println(unauthorizedExcludeUrls("/blf1-mdm-web/index.html"));*/
		/*System.out.println(excludeUrlsList);
		loadExcludeUrlsList("/blf1-uc-web/itg_department/listAll.json,/blf1-mdm-web/bas_country/listAll.json,/blf1-mdm-web/bas_province/listAll.json,/blf1-uc-web/itg_department/listAll.json");
		System.out.println(excludeUrlsList);*/
	}
}
