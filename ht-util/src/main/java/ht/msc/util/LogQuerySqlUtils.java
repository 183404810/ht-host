package ht.msc.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.ResultMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class LogQuerySqlUtils {

	private LogQuerySqlUtils(){
		
	}
	
	protected  static Logger logger = LoggerFactory.getLogger(LogQuerySqlUtils.class);
			
 /**
  * 改成查询日志的结果集
  * @param builder
  * @param ms
  * @param id
  */
  public static  void changeResult2Log( MappedStatement.Builder builder,MappedStatement ms,String id){
	if(StringUtils.isBlank(ThreadLocals.getLogMasterModule())){ builder.resultMaps(ms.getResultMaps()); return;}
	List<ResultMap> resultMaps = new ArrayList<ResultMap>();
	Class<?> clazz;
	try {
			clazz = Class.forName(getNewClassName());
			List<ResultMapping> resultMaping=new ArrayList<ResultMapping>();
			resultMaping.addAll(ms.getResultMaps().get(0).getResultMappings());
			ResultMapping resultMapping1=new  ResultMapping.Builder(ms.getConfiguration(),
					"logAttribute", "log_attribute", String.class).build();
			ResultMapping resultMapping2=new  ResultMapping.Builder(ms.getConfiguration(),
					"logId", "log_id", Integer.class).build();
			ResultMapping resultMapping3=new  ResultMapping.Builder(ms.getConfiguration(),
					"logTime", "log_time", Date.class).build();
			ResultMapping resultMapping4=new  ResultMapping.Builder(ms.getConfiguration(),
					"logUser", "log_user", String.class).build();
			resultMaping.add(resultMapping1);
			resultMaping.add(resultMapping2);
			resultMaping.add(resultMapping3);
			resultMaping.add(resultMapping4);
			ResultMap resultMap = new ResultMap.Builder(ms.getConfiguration(), id, clazz, resultMaping).build();
			resultMaps.add(resultMap);
		} catch (ClassNotFoundException e) {
			//e.printStackTrace();
			logger.error("",e);
		}
		
	    builder.resultMaps(resultMaps);
//	    clearTreadLocal();
  }
  
	public static String getlogSql(String orginSql){
		if(StringUtils.isBlank(ThreadLocals.getLogMasterModule())) return orginSql;
		orginSql=orginSql.replace("select", "SELECT");
		String annotationStr = "";
		if(orginSql.contains("*/")){
			 int index = orginSql.lastIndexOf("*/");
			 annotationStr = orginSql.substring(0, index + 2);
			 orginSql = orginSql.substring(index + 2);
		}
		orginSql=orginSql.replaceFirst("SELECT", "SELECT log_attribute,log_time,log_id,log_user,");
		orginSql=annotationStr+orginSql;
		orginSql=orginSql.replace(getOldTableName(), getNewTableName());
		return orginSql;
	}
	
	public static String getOldTableName(){
		return CommonUtil.convertJaveBeanStrToUnderLine(
				CommonUtil.changeFirstCharUporLow(ThreadLocals.getLogMasterModule(), 1));
	}
	
	public static String getNewClassName(){
		String classPath="com.belle.scm."+CommonUtil.app_sys+".dao.entity.";
		String newTableName= getNewTableName();
		String newEntityName=CommonUtil.convertUnderLineStrToJaveBean(newTableName);
		newEntityName=CommonUtil.changeFirstCharUporLow(newEntityName, 0);
		return classPath+newEntityName;
	}
	
	public static String getNewTableName(){
		String oldtable=getOldTableName();
		return "log"+oldtable.substring(oldtable.indexOf("_"));
		
	}
	public static void clearTreadLocal(){
		ThreadLocals.setLogMasterModule(null);
		ThreadLocals.clearSumSqlThreadLocal();
	}
}
