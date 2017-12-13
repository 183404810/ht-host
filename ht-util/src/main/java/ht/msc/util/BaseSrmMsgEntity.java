package ht.msc.util;

import java.util.Date;

public interface BaseSrmMsgEntity {

	/**
	 * 事业部编号
	 */
	public String getDivisionNo();
	
	/**
	 * 品牌
	 */
	public String getBrandNo();
	
	/**
	 * 单据编号
	 */
	public String getBillNo();
	
	/**
	 * 单据状态
	 */
	public String getBillStatus();
	
	/**
	 * 单据类型
	 */
	public String getBillTypeNo();
	
	/**
	 * 模块编号(跟blf1系统itg_module_list模块管理必须要一致,取消息设置用)
	 */
	public String getModuleNo();
	
	/**
	 * SRM系统模块编号
	 */
	public String getSrmModuleNo();
	
	/**
	 * 供应商编号
	 */
	public String getSupplierNo();
	
	/**
	 * 创建人(发送人)
	 */
	public String getCreator();
	
	/**
	 * 创建时间(发送时间)
	 */
	public Date getCreateTime();
}
