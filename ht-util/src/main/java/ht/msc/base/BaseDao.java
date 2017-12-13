package ht.msc.base;

import ht.msc.util.CommonOperatorEnum;
import ht.msc.util.SCException;
import ht.msc.util.SimplePage;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public abstract class BaseDao {

	private BaseMapper mapper;
	
	@SuppressWarnings("unused")
	@PostConstruct
	private void initConfig(){
		this.mapper=this.init();
	}
	
	public abstract BaseMapper init();

	
	public <ModelType> int deleteById(ModelType modelType) throws SCException {
		try {
			return mapper.deleteByPrimarayKeyForModel(modelType);
		} catch (Exception e) {
			throw new SCException("",e);
		}
	}

	public <ModelType> int add(ModelType modelType) throws SCException {
		try {
			return mapper.insertSelective(modelType);
		} catch (Exception e) {
			throw new SCException("",e);
		}
	}

	public <ModelType> ModelType findById(ModelType modelType) throws SCException {
		try {
			return mapper.selectByPrimaryKey(modelType);
		} catch (Exception e) {
			throw new SCException("",e);
		}
	}
	
	public <ModelType> List<ModelType> findByBiz(ModelType modelType,
			Map<String, Object> params) throws SCException {
		try {
			return mapper.selectByParams(modelType, params);
		} catch (Exception e) {
			throw new SCException("",e);
		}
	}

	public <ModelType> int modifyById(ModelType modelType) throws SCException {
		try {
			return mapper.updateByPrimaryKeySelective(modelType);
		} catch (Exception e) {
			throw new SCException("",e);
		}
	}

	public int findCount(Map<String,Object> params) throws SCException {
		try {
			return mapper.selectCount(params);
		} catch (Exception e) {
			throw new SCException("",e);
		}
	}

	public <ModelType> List<ModelType> findByPage(SimplePage page, String orderByField,
			String orderBy,Map<String,Object> params) throws SCException {
		try {
			return mapper.selectByPage(page, orderByField, orderBy, params);
		} catch (SCException e) {
			throw e;
		}
	}

	@Transactional(propagation = Propagation.REQUIRED,rollbackFor=SCException.class)
	public <ModelType> int save(Map<CommonOperatorEnum, List<ModelType>> params) throws SCException {
		try {
			int count=0;
			for (Entry<CommonOperatorEnum, List<ModelType>> param : params.entrySet()) {
				if(param.getKey().equals(CommonOperatorEnum.DELETED)){
					List<ModelType> list=params.get(CommonOperatorEnum.DELETED);
					if(null!=list&&list.size()>0){
						for (ModelType modelType : list) {
							count+=this.mapper.deleteByPrimarayKeyForModel(modelType);
						}
					}
				}
				if(param.getKey().equals(CommonOperatorEnum.UPDATED)){
					List<ModelType> list=params.get(CommonOperatorEnum.UPDATED);
					if(null!=list&&list.size()>0){
						for (ModelType modelType : list) {
							count+=this.mapper.updateByPrimaryKeySelective(modelType);
						}
					}
				}
				if(param.getKey().equals(CommonOperatorEnum.INSERTED)){
					List<ModelType> list=params.get(CommonOperatorEnum.INSERTED);
					if(null!=list&&list.size()>0){
						for (ModelType modelType : list) {
							this.mapper.insertSelective(modelType);
						}
						count+=list.size();
					}
				}
			}
			return count;
		} catch (Exception e) {
			throw new SCException("",e);
		}
	}
	
	public <ModelType> int deleteByPrimarayKeyForModel(ModelType record)throws SCException{
		return mapper.deleteByPrimarayKeyForModel(record);
	}
}
