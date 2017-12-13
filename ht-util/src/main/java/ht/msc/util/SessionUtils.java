package ht.msc.util;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SessionUtils {
	private static Logger logger = LoggerFactory.getLogger(SessionUtils.class);
	/** * 保存变量的ThreadLocal，保持在同一线程中同步数据. */
    @SuppressWarnings("rawtypes")
	private static final ThreadLocal SESSION_MAP = new ThreadLocal();

    /** * 工具类的protected构造方法. */
    protected SessionUtils() {
    }

    /**
     * 获得线程中保存的属性.
     *
     * @param attribute
     *            属性名称
     * @return 属性值
     */
    @SuppressWarnings("rawtypes")
	public static Object get(String attribute) {
        Map map = (Map) SESSION_MAP.get();
        if(map!=null){
        	return map.get(attribute);
        }
        return null;
    }

    /**
     * 获得线程中保存的属性，使用指定类型进行转型.
     *
     * @param attribute
     *            属性名称
     * @param clazz
     *            类型
     * @param <T>
     *            自动转型
     * @return 属性值
     */
    @SuppressWarnings("unchecked")
	public static <T> T get(String attribute, Class<T> clazz) {
        return (T) get(attribute);
    }

    /**
     * 设置制定属性名的值.
     *
     * @param attribute
     *            属性名称
     * @param value
     *            属性值
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
	public static void set(String attribute, Object value) {
        Map map = (Map) SESSION_MAP.get();
        if (map == null) {
            map = new HashMap();
            SESSION_MAP.set(map);
        }
        map.put(attribute, value);
    }
    
    /**
     * 请求信息保存本地线程
     * @param httpRequest
     * @param user
     */
    public static void setReqInfo2ThreadLocal(HttpServletRequest httpRequest,SystemUser user ){
    	//保存模块号
    	//setReqModuleId(httpRequest);
    	//保存用户信息
    	SessionUtils.set(SysConstans.SESSION_USER, user);
    	if (logger.isDebugEnabled()){
			logger.debug(" userId:"+user.getUserId());
    	}
    	ThreadLocals.setLogMasterModule(httpRequest.getParameter("moduleEntity"));//日志查询时用到
    }
    /**
     * 获得当前登录用户对象
     * @return
     */
    public static SystemUser getCurrentLoginUser(){
    	SystemUser user=null;
    	if(SessionUtils.get(SysConstans.SESSION_USER)!=null){
    		user=(SystemUser) SessionUtils.get(SysConstans.SESSION_USER);
    	}
    	return user;
    }

    /**
     * 保存模块号
     * @param httpRequest
     */
    public static void setReqModuleId(HttpServletRequest httpRequest){
    	if (logger.isDebugEnabled()){
			logger.debug(" sys_menu_moduleId:"+httpRequest.getParameter("sys_menu_moduleId"));
    	}
//		if(StringUtils.isNotBlank(httpRequest.getParameter("sys_menu_moduleId"))){
			ThreadLocals.setModuleId(httpRequest.getParameter("sys_menu_moduleId"));
//		}
	}
}