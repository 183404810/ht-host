package ht.msc.util;

import java.util.Date;
import java.util.List;
import java.util.Map;


public interface BaseSqlDao<T> {
	/**
	 * 查询VO对象
	 * @param params
	 * @return
	 * @throws DaoException
	 */
	public T getVo(Map<String,Object> params) throws SCException;
	
	/**
	 * 查询ListVO对象
	 * @param page
	 * @param orderByField
	 * @param orderBy
	 * @param params
	 * @return
	 * @throws DaoException
	 */
    public List<T> getListVoByPage(SimplePage page,String orderByField,String orderBy,Map<String,Object> params) throws SCException;
    
	/**
	 * 查询单据的最大序号
	 * @param params
	 * @return
	 * @throws DaoException
	 */
	public int selectMaxBillOrderNo(Map<String,Object> params)throws SCException;
	
	/**
	 * 更新单据
	 * @param billNo
	 * @param tableName
	 * @return
	 */
	public int updateBillNo(String billNo,String tableName,SystemUser systemUser
			,String billStatus,Date date,String divisionNo,String orgDivisionNo);
	
	public int queryBaseCount(Map<String,Object> params)throws SCException;
}