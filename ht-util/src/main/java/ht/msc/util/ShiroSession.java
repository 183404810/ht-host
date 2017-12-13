package ht.msc.util;

import org.apache.shiro.authz.SimpleAuthorizationInfo;

public interface ShiroSession {
	/**
     * 保存登录用户的session
     * @param userName
     */
    public void saveLoginUserSession(SystemUser user);
    
    /**
	 * 获取登录用户对象
	 * @param loginName
	 * @param loginPassword
	 * @return
	 */
    public SystemUser getUserByLoginNamePassword(String loginName, String loginPassword);
    
    
    /**
     * 获取用户的系统访问权限
     * @param userName
     * @return
     */
	public SimpleAuthorizationInfo getUserRolepermission(String loginName);
    
    /**
	 * 退出登录时清空缓存MointorRealm
	 * @param loginName
	 */
	public void removeMointorRealmCache(String loginName);
}
