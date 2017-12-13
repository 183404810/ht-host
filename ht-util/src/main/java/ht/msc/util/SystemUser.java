package ht.msc.util;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.alibaba.fastjson.annotation.JSONField;

public class SystemUser implements Serializable {

		 /**
		 * 
		 */
		private static final long serialVersionUID = -8094604492554763459L;

		/**
	     * 用户id
	     */
	    private Integer userId;

	    /**
	     * 用户编码
	     */
	    private String userCode;

	    /**
	     * 用户名称
	     */
	    private String userName;

	    /**
	     * 启用状态(0=禁用 1=启用)
	     */
	    private Integer enableFlag;

	    /**
	     * 用户密码
	     */
	    private String password;

	    /**
	     * 密码等级编号
	     */
	    private Integer pwdLevelNo;

	    /**
	     * 密码修改时间
	     */
	    @JSONField (format="yyyy-MM-dd HH:mm:ss")
	    private Date pwdModifyTime;

	    /**
	     * 所属部门编号
	     */
	    private Integer deptNo;

	    /**
	     * 是否管理员组(0=否 1=是)
	     */
	    private Integer isAdminGroup;

	    /**
	     * mac地址检测(0=不检测 1=检测)
	     */
	    private Integer checkMac;

	    /**
	     * 设定的mac地址
	     */
	    private String addressMac;

	    /**
	     * ip地址检测(0=不检测 1=检测)
	     */
	    private Integer checkIp;

	    /**
	     * 设定的ip地址
	     */
	    private String addressIp;

	    /**
	     * 手机号码(如不为_，则必须唯一，以支持可以用手机登录)
	     */
	    private String mobileNo;

	    /**
	     * 邮箱(如不为_，则必须唯一，以支持可以用邮箱登录)
	     */
	    private String email;

	    /**
	     * 建档人
	     */
	    private String creator;

	    /**
	     * 建档时间
	     */
	    @JSONField (format="yyyy-MM-dd HH:mm:ss")
	    private Date createTime;

	    /**
	     * 修改人
	     */
	    private String modifier;

	    /**
	     * 修改时间
	     */
	    @JSONField (format="yyyy-MM-dd HH:mm:ss")
	    private Date modifyTime;

	    /**
	     * 备注
	     */
	    private String remarks;
	    
	    /**
	     * 部门名称
	     */
	    private String deptName;
	    
	    /**
	     * 用户权限菜单
	     * key=Url val=RightValue
	     * 格式：key:/blf1-mdm-web/basbrandrelation val:127
	     */
	    private HashMap<String, String> userMenuMap;
	    
	    /**
	     * 是否为超级管理员
	     * 1是，0否
	     */
	    private Integer suAdminRoleId;
	    
	    /**
	     * 应用list(除了/blf1-uc-web/)
	     * 格式：/blf1-pd-web/
	     */
	    private List<String> appUrlList;
	    
	    /**
	     * 供应商号
	     */
	    private String supplierNo;
	    
	    private String supplierName;
	    /**
	     * 0:企业用户 1:供应商
	     */
	    private int userType;
	    
	    /**
	     * 是否为超级管理员
	     * 1是，0否
	     */
	    private Integer isSuperAdmin;	//为超级管理员角色

		public Integer getIsSuperAdmin() {
			return isSuperAdmin;
		}

		public void setIsSuperAdmin(Integer isSuperAdmin) {
			this.isSuperAdmin = isSuperAdmin;
		}

		public Integer getUserId() {
			return userId;
		}

		public void setUserId(Integer userId) {
			this.userId = userId;
		}

		public String getUserCode() {
			return userCode;
		}

		public void setUserCode(String userCode) {
			this.userCode = userCode;
		}

		public String getUserName() {
			return userName;
		}

		public void setUserName(String userName) {
			this.userName = userName;
		}

		public Integer getEnableFlag() {
			return enableFlag;
		}

		public void setEnableFlag(Integer enableFlag) {
			this.enableFlag = enableFlag;
		}

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}

		public Integer getPwdLevelNo() {
			return pwdLevelNo;
		}

		public void setPwdLevelNo(Integer pwdLevelNo) {
			this.pwdLevelNo = pwdLevelNo;
		}

		public Date getPwdModifyTime() {
			return pwdModifyTime;
		}

		public void setPwdModifyTime(Date pwdModifyTime) {
			this.pwdModifyTime = pwdModifyTime;
		}

		public Integer getDeptNo() {
			return deptNo;
		}

		public void setDeptNo(Integer deptNo) {
			this.deptNo = deptNo;
		}

		public Integer getIsAdminGroup() {
			return isAdminGroup;
		}

		public void setIsAdminGroup(Integer isAdminGroup) {
			this.isAdminGroup = isAdminGroup;
		}

		public Integer getCheckMac() {
			return checkMac;
		}

		public void setCheckMac(Integer checkMac) {
			this.checkMac = checkMac;
		}

		public String getAddressMac() {
			return addressMac;
		}

		public void setAddressMac(String addressMac) {
			this.addressMac = addressMac;
		}

		public Integer getCheckIp() {
			return checkIp;
		}

		public void setCheckIp(Integer checkIp) {
			this.checkIp = checkIp;
		}

		public String getAddressIp() {
			return addressIp;
		}

		public void setAddressIp(String addressIp) {
			this.addressIp = addressIp;
		}

		public String getMobileNo() {
			return mobileNo;
		}

		public void setMobileNo(String mobileNo) {
			this.mobileNo = mobileNo;
		}

		public String getEmail() {
			return email;
		}

		public void setEmail(String email) {
			this.email = email;
		}

		public String getCreator() {
			return creator;
		}

		public void setCreator(String creator) {
			this.creator = creator;
		}

		public Date getCreateTime() {
			return createTime;
		}

		public void setCreateTime(Date createTime) {
			this.createTime = createTime;
		}

		public String getModifier() {
			return modifier;
		}

		public void setModifier(String modifier) {
			this.modifier = modifier;
		}

		public Date getModifyTime() {
			return modifyTime;
		}

		public void setModifyTime(Date modifyTime) {
			this.modifyTime = modifyTime;
		}

		public String getRemarks() {
			return remarks;
		}

		public void setRemarks(String remarks) {
			this.remarks = remarks;
		}

		public HashMap<String, String> getUserMenuMap() {
			return userMenuMap;
		}

		public void setUserMenuMap(HashMap<String, String> userMenuMap) {
			this.userMenuMap = userMenuMap;
		}

		public String getDeptName() {
			return deptName;
		}

		public void setDeptName(String deptName) {
			this.deptName = deptName;
		}

		/**
		 * 是否为超级管理员
	     * 1是，0否
		 * @return
		 */
		public Integer getSuAdminRoleId() {
			return suAdminRoleId;
		}

		public void setSuAdminRoleId(Integer suAdminRoleId) {
			this.suAdminRoleId = suAdminRoleId;
		}

		public List<String> getAppUrlList() {
			return appUrlList;
		}

		public void setAppUrlList(List<String> appUrlList) {
			this.appUrlList = appUrlList;
		}

		public String getSupplierNo() {
			return supplierNo;
		}

		public void setSupplierNo(String supplierNo) {
			this.supplierNo = supplierNo;
		}

		public String getSupplierName() {
			return supplierName;
		}

		public void setSupplierName(String supplierName) {
			this.supplierName = supplierName;
		}

		public int getUserType() {
			return userType;
		}

		public void setUserType(int userType) {
			this.userType = userType;
		}

}
