package ht.msc.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;


public class DataPrivilegeUtils{
	
	private DataPrivilegeUtils(){
		
	}
	
	private static Logger logger = LoggerFactory.getLogger(DataPrivilegeUtils.class);
	/**
	 * 处理权限数据
	 * @param originSql
	 */
	public static String handlerDataPrivilege(String originSql){
		originSql=doDataPrivilege(originSql);
		if(StringUtils.isNotBlank(ThreadLocals.getSumSqlThreadLocal())){
			logger.info(" handler sum sql ");
			String sumPropertys=ThreadLocals.getSumSqlThreadLocal();
			originSql=SqlHandlerUtils.getSumSql(originSql, sumPropertys);
		}
		return originSql;
	}
	
	public static String doDataPrivilege(String originSql){
		String oldsql=originSql;
		try{
			if(StringUtils.isBlank(CommonUtil.app_sys)|| "uc".equals(CommonUtil.app_sys))return originSql;
			SystemUser systemuser=SessionUtils.getCurrentLoginUser();
			logger.info("moduleId="+ThreadLocals.getModuleId()+  
					(systemuser==null ? "systemuser=null":"  userid="+systemuser.getUserId() + "  userCode="+systemuser.getUserCode()
					+"  IsSuperAdmin="+systemuser.getIsSuperAdmin()));
			
			//超级管理员不用过滤
			if (systemuser == null
					|| (systemuser.getIsSuperAdmin() != null && systemuser.getIsSuperAdmin().intValue() == 1) // 2.0 superAdmin
					|| (systemuser.getIsAdminGroup() != null && systemuser.getIsAdminGroup().intValue() == 1) // 1.0 superAdmin
			){
				return originSql;
			}
			
			if(systemuser.getUserId()==null)return originSql;
			if(logger.isDebugEnabled())logger.debug("userid:"+systemuser.getUserId());
			if(StringUtils.isBlank(ThreadLocals.getModuleId()))return originSql;
			if("0".equals(ThreadLocals.getModuleId()) || "-110".equals(ThreadLocals.getModuleId()))return originSql;
			if(StringUtils.isNotBlank(ThreadLocals.getNoFilterData()))return originSql;//本次查询不做过滤
			
			//
			List<DataPrivilegeVO> listDataPrivilege=getlistDataPrivilege();
			
			if(listDataPrivilege==null || listDataPrivilege.size()==0)return originSql;
			for(DataPrivilegeVO dataPrivilegeVO:listDataPrivilege){//遍历每项数据权限
				if(dataPrivilegeVO.getUseAll()!=null && dataPrivilegeVO.getUseAll().intValue()==0){//不是应用所有模块
					if(dataPrivilegeVO.getUseModuleNoList()==null|| 
							!dataPrivilegeVO.getUseModuleNoList().contains(ThreadLocals.getModuleId()))
						continue;
				}else {
					if(dataPrivilegeVO.getNonuseUseModuleNoList()==null ||
							dataPrivilegeVO.getNonuseUseModuleNoList().contains(ThreadLocals.getModuleId()))
						continue;
				}
				originSql= handlerTablePrivilege(dataPrivilegeVO.getPrivilegeNo(), dataPrivilegeVO.getPrivilegeSql(), originSql);
			}
		}catch (Exception e) {
			logger.error("error:originSql:"+originSql, e);
			return oldsql;
		}
		return originSql;
	}
	
	/**
	 * 遍历该权限所有需要过滤的表
	 * @param privilegeNo
	 * @param addSql
	 * @param originSql
	 * @return
	 */
	private static String handlerTablePrivilege(String privilegeNo,String addSql,String originSql){
		List<DataPrivilegeTableVO> listPrivilegeTable = getDataPrivilegeTable(privilegeNo);
		
		if(listPrivilegeTable==null ||listPrivilegeTable.size()==0)return originSql;
		for(DataPrivilegeTableVO privilegeTable:listPrivilegeTable){
			if(originSql.contains(privilegeTable.getTableName())){
				originSql=SqlHandlerUtils.handlerSql(originSql, addSql, privilegeTable.getTableName(), privilegeTable.getColumnName());
			}
		}
		return originSql;
	}
	
	/**
	 * 获取权限数据
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private static  List<DataPrivilegeVO> getlistDataPrivilege(){
		List<DataPrivilegeVO> list = null;
		//bl2.0
		if(LoginUtils.runVersion.equals("2.0")){
			//正式数据过滤时开启下面代码   防止有人把缓存人为全部清除
			RedisClient redis = (RedisClient) SpringComponent.getBean("redisClient");
			list = (List<DataPrivilegeVO>) redis.get(SysConstans.DATA_PRIVILEGE_KEY_NEW);
			if (list == null || list.size() == 0) {
				String url = CommonUtil.getHostUrlWithHttp()+"/bl-uc-web/itg_data_privilege/blf1_findAll.json";
				Map<String, String> params = new HashMap<String, String>();
				list = getDataPrivilege(url, params);
				redis.set(SysConstans.DATA_PRIVILEGE_KEY_NEW, list, 1800);		//有效期30min钟，设置数据权限后，需过30min钟才能生效
			}
		}else{
			//bl1.0
			RedisClient redis = (RedisClient) SpringComponent.getBean("redisClient");
			list = (List<DataPrivilegeVO>) redis.get(SysConstans.DATA_PRIVILEGE_KEY);
			//正式数据过滤时开启下面代码 防止有人把缓存人为全部清除
			if (list == null || list.size() == 0) {
				String url = CommonUtil.getHostUrlWithHttp()+ "/blf1-uc-web/itg_data_privilege/init_cache.json";
				try {
					Map<String, String> params = new HashMap<String, String>();
					String str = HttpUtils.post(url, params);
					String result = JsonUtils.getValueByKey("result", str);
					String reusltCode = JsonUtils.getValueByKey("resultCode",result);
					if (reusltCode.equals("0")) {
						list = (List<DataPrivilegeVO>) redis.get(SysConstans.DATA_PRIVILEGE_KEY);
					}
				} catch (IOException e) {
					logger.error("error:getlistDataPrivilege:", e);
				}
			}
		}
		return list;
	}
	
	
	private static List<DataPrivilegeVO>  getDataPrivilege(String url, Map<String, String> params){
		List<DataPrivilegeVO> list = null;

		try {
			String str = HttpUtils.post(url, params);
			String flag = JsonUtils.getValueByKey("flag", str);
			String reusltCode = JsonUtils.getValueByKey("retCode", flag);
			if(reusltCode.equals("0")){
				String dataPrivilegeVos = JsonUtils.getValueByKey("rows", str);
				list = JSON.parseArray(dataPrivilegeVos, DataPrivilegeVO.class);
			} else {
				logger.error(">>>>>>>>getlistDataPrivilege error: "+ url + str);
			}
		} catch (IOException e) {
			logger.error(">>>>>>>>getlistDataPrivilege error: "+ url, e);
		}
		
		return list;
	}
	
	/**
	 * 数据权限从表
	 * @param privilegeNo 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private static  List<DataPrivilegeTableVO> getDataPrivilegeTable(String privilegeNo){
		List<DataPrivilegeTableVO> list = null;
		if(LoginUtils.runVersion.equals("2.0")){
			RedisClient redis = (RedisClient) SpringComponent.getBean("redisClient");
			list = (List<DataPrivilegeTableVO>) redis.get(SysConstans.DATA_PRIVILEGE_TABLE_KEY_NEW + privilegeNo);
			if (list == null || list.size() == 0) {
				String url = CommonUtil.getHostUrlWithHttp()+"/bl-uc-web/itg_data_privilege/blf1_ItgDataPrivilegeUseTm_find.json";
				Map<String, String> params = new HashMap<String, String>();
				params.put("privilegeNo", privilegeNo);
				params.put("moduleCode", ThreadLocals.getModuleId());
				list = getDataPrivilegeTable(url, params);
				redis.set(SysConstans.DATA_PRIVILEGE_TABLE_KEY_NEW + privilegeNo, list, 1800);		//有效期30min钟，设置数据权限后，需过30min钟才能生效
			}
		}else{
			RedisClient redis=(RedisClient)SpringComponent.getBean("redisClient");
			list = (List<DataPrivilegeTableVO>)redis.get(SysConstans.DATA_PRIVILEGE_TABLE_KEY+privilegeNo);
		}
		return list;
	}
	
	private static List<DataPrivilegeTableVO>  getDataPrivilegeTable(String url, Map<String, String> params){
		List<DataPrivilegeTableVO> list = null;
		
		try {
			String str = HttpUtils.post(url, params);
			String flag = JsonUtils.getValueByKey("flag", str);
			String reusltCode = JsonUtils.getValueByKey("retCode", flag);
			if(reusltCode.equals("0")){
				String dataPrivilegeVos = JsonUtils.getValueByKey("rows", str);
				list = JSON.parseArray(dataPrivilegeVos, DataPrivilegeTableVO.class);
			} else {
				logger.error(">>>>>>>>blf1_ItgDataPrivilegeUseTm_find error: "+ url + str);
			}
		} catch (IOException e) {
			logger.error(">>>>>>>>blf1_ItgDataPrivilegeUseTm_find error: "+ url, e);
		}
		
		return list;
	}
	
}