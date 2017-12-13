package ht.msc.util;
public class SysConstans {
	
	private SysConstans(){
		
	}
	public static final String LOGIN_SYSTEM_USER_COOKIE_ID = "login_system_user_cookie_id";
	public static final int LOGIN_COOKIE_TIME = 7*24*60*60;
	public static final String RETAIL_SESSION_USER="retail_session_user";
	public static final String DOMAIN=".belle.cn";
	public static final String SESSION_SYSTEMID="systemid";
	public static final String SESSION_AREASYSTEMID="areasystemid";
	public static final String UC_INDEX_URL="ucIndexUrl";
	public static final String SUCCESS_KEY="success";
	public static final String FAILED_KEY="failed";
	public static final String ERROR_KEY="error";
	public static final String UTF8_ENCODE="UTF-8";
	
	public static final String SYSTEM_NAME="blf1-common";
	public static final String SESSION_USER="blf1_session_user";
	
	public static final String INSERTED_TYPE="inserted";
	public static final String UPDATED_TYPE="updated";
	public static final String DELETED_TYPE="deleted";
	
	//redis缓存key常量
	public static final String REDIS_CACHE_USER="blf1_rediscache_user_";
	public static final String REDIS_CACHE_USERMENULIST="blf1_rediscache_usermenulist_";//用户菜单缓存key-loginName
	public static final String REDIS_CACHE_BAS_DICT_COMBO = "blf1_rediscache_basdictcombo_";//数据字典缓存key-dictCode
	
	/**
	 * 用户异步消息key
	 */
	public static final String REDIS_CACHE_UserMessageKey="User_Redis_PushMessage_Key_";
	
	//uc常量
	public static final String DEF_PROJECT_CODE="BLF1";//默认项目编码 数据库存储值为小写
	public static final int SUPER_ADMIN_ROLE_ID=1;//超级管理员角色ID
	
	public static final String BIll_NO="billNo";
	
	public static final String CREATOR_FIELDNAME= "creator";
	
	public static final String UNIQUE="unique";
	public static final String IS_EXIST="isexist";
	
	public static final String MDM_MYCAT="mycat";
	
	public static final String MDM_SYS="blf1:mdm=";
	
	public static final String SD_SYS="blf1:sd=";

	public static final int MAX_ROWCOLWRAP_SIZE= 20;	//尺码横排支持的最大码数
	
	public static final String REQ_DATETIME="req_dateTime";
	
	public static final String SIGN_VAR="sign";
	
	public static final String SIGN_KEY="adfkafqpfioqewriytibvnkadfhakaefpewqr";
	
	public static final String DATA_PRIVILEGE_KEY="data_privilege_list";
	
	public static final String DATA_PRIVILEGE_TABLE_KEY="data_privilege_table_";
	
	public static final String BILL_AUDIT_STATUS="20";
	
	public static final String  SUPPLIER_NO_SESSION="supplier_No";
	public static final String DATA_PRIVILEGE_TABLE_KEY_NEW="data_privilege_table_new";
	public static final String DATA_PRIVILEGE_KEY_NEW="data_privilege_list_new";
}
