package ht.msc.base;

import ht.msc.util.SimplePage;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;


public interface BaseMapper {
	public int deleteByPrimaryKey(int id);

    public <ModelType> int insert(ModelType record);

    public <ModelType> int insertSelective(ModelType record);

    public <ModelType> ModelType selectByPrimaryKey(ModelType modelType);
    
    public <ModelType> List<ModelType> selectByParams(@Param("model")ModelType modelType,@Param("params")Map<String,Object> params);

    public <ModelType> int updateByPrimaryKeySelective(ModelType record);

    public <ModelType> int updateByPrimaryKey(ModelType record);
    
    public int selectCount(@Param("params")Map<String,Object> params);
    
    public <ModelType> List<ModelType> selectByPage(@Param("page") SimplePage page,@Param("orderByField") String orderByField,@Param("orderBy") String orderBy,@Param("params")Map<String,Object> params);
    
    public <ModelType> int deleteByPrimarayKeyForModel(ModelType record);
    
    /**
     * 更新日志同步表的同步状态为需要同步
     * @param params
     */
    public void updateLogSynStatus(@Param("params")Map<String,Object> params);
}
