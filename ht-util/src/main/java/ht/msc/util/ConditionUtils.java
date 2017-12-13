package ht.msc.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class ConditionUtils {
private ConditionUtils(){
		
	}
	
	private final static Map<String, String> operatorType=new HashMap<String, String>();
	
	private final static List<String> fiterList= new ArrayList<String>();
	
	static{
		operatorType.put("10", "=");
		operatorType.put("11", ">");
		operatorType.put("12", "<");
		operatorType.put("13", ">=");
		operatorType.put("14", "<=");
		operatorType.put("15", "LIKE");
		operatorType.put("16", "!=");
		operatorType.put("17", "is null");
		operatorType.put("18", "is not null");
		operatorType.put("19", "LIKE");//左匹配
		operatorType.put("20", "LIKE");//右匹配

		fiterList.add("exec");
		fiterList.add("delete");
		fiterList.add("master");
		fiterList.add("truncate");
		fiterList.add("declare");
		fiterList.add("create");
		fiterList.add("xp_");
		fiterList.add("'");
	}
	
	/**
	 * 拼接条件
	 * @param list
	 * @return
	 */
	public static String getQueryCondition(List<QueryConditionReq> list,Map<String, Object> retParams )throws SCException{
		StringBuffer sb=new StringBuffer();
		String fieldName="";
		for(QueryConditionReq qcr:list){
			if(fiterList.contains(qcr.getValue())) throw new SCException("myselfMsg:查询条件中存在非法字符！");
			fieldName=CommonUtil.convertJaveBeanStrToUnderLine(qcr.getProperty());
			sb.append(" AND ").append(fieldName)
			.append(" ").append(operatorType.get(qcr.getOperator())).append(getConditionValue(qcr.getOperator(),qcr.getValue()));
//			retParams.put(qcr.getProperty(), qcr.getValue());
		}
		return sb.toString();
	}

	private static String getConditionValue(String operator,String value){
		if("15".equals(operator)){
			return   " '%"+value+"%'";
		}
		if("19".equals(operator)){
			return   " '"+value+"%'";
		}
		if("20".equals(operator)){
			return   " '%"+value+"'";
		}
		if(value==null)return "";
		if(value=="")return " ''";
		return  " '"+value+"'";
	}

}
