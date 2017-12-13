package ht.msc.util;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

public class SqlHandlerUtils {
	
	private SqlHandlerUtils(){
		
	}
	
	private static final String sql_where="WHERE";
	
	private static final String sql_left_join="LEFT JOIN ";

	/**
	 * 处理sql，加上过滤条件
	 * @param originSql 
	 * @param addSql
	 * @param tableName
	 * @param tableField
	 * @return
	 */
	public static String handlerSql(String originSql,String addSql,String tableName,String tableField){
		originSql=originSql.replace("\t", " ").replace("\n", " ");
		String userid=String.valueOf(SessionUtils.getCurrentLoginUser().getUserId());
		addSql=addSql.replace("[userid]", userid);
		originSql=originSql.replaceAll(" where ", " "+sql_where+" ");
		originSql=originSql.replace("left", "LEFT");
		originSql=originSql.replace("join", "JOIN");
		originSql=originSql.replaceAll("LEFT[ ]+JOIN[ ]+", sql_left_join);
		List<String> matcherList= matcherTableSql(originSql, tableName);
		for(String matcherSql:matcherList){
		  if(originSql.contains(sql_left_join+matcherSql) )continue;
		  String privliesql=getPrivilegeSql(matcherSql, tableField, tableName, addSql);
		  String newMatcherSql=mergeSql(matcherSql, privliesql);
		  originSql=originSql.replace(matcherSql, newMatcherSql);
		}
		return originSql;
	}
	

	
	
	/**
	 * 获得指定单词后第一个单词
	 * @param str
	 * @param word
	 * @return
	 */
	public static String getAfterFirstWord(String str,String word){
		  String regex = "\\b"+word+"\\b\\s+(\\w+)\\b";
	        Pattern pattern = Pattern.compile(regex);
	        Matcher matcher = pattern.matcher(str);
	        while (matcher.find()) {
	        	return matcher.group(1).trim();
	        }
	        return "";
	}
	
	/**
	 * 获得指定单词后第二个单词
	 * @param str
	 * @param word
	 * @return
	 */
	public static String getAfterSecondWord(String str,String word){
		 String reg=word+"\\s+[a-zA-Z0-9]+\\s+(\\w+)\\b";
	     Pattern pattern = Pattern.compile(reg);
	     Matcher matcher = pattern.matcher(str);
	     while (matcher.find()) {
	        return matcher.group(1).trim();
	     }
	     return "";
	}
	
	/**
	 * 要查询的属性加上别名
	 * @param originSql
	 * @param matcherA
	 * @param tableField
	 * @param tableName
	 * @return
	 */
	public static String getPrivilegeField(String originSql,String tableField,String tableName){
		String  otherName=getAfterFirstWord(originSql,tableName);//解析别名  指定单词后第一个单词
		if(StringUtils.isNotBlank(otherName) && otherName.toLowerCase().equals("as")){//如果第一个单词为 as ，取后面第二个
			otherName=getAfterSecondWord(originSql, tableName);
		}
		return  (StringUtils.isBlank(otherName) ||otherName.equals(sql_where))?tableField:otherName+"."+tableField;
	}
	
	/**
	 * 查询条件过滤
	 * @param originSql
	 * @param tableField
	 * @param tableName
	 * @param privilegeSql
	 * @return
	 */
	public static String getPrivilegeSql(String originSql,String tableField,String tableName ,String privilegeSql){
		 if(StringUtils.isBlank(ThreadLocals.getAddNoNullFilter())){
			 privilegeSql=getPrivilegeField( originSql, tableField, tableName)
						+" in ("
						+privilegeSql
						+") AND " ;
		 }else{
			 String filedValue=getPrivilegeField( originSql, tableField, tableName);
			 privilegeSql ="(coalesce(" +filedValue+",'')='' or "+filedValue+" in ("+privilegeSql	+")) AND " ;
		 }
		 
		 return privilegeSql;
	}
	
	/**
	 * 合并sql
	 * @param originSql
	 * @param privilegeSql
	 * @return
	 */
	public static String mergeSql(String originSql,String privilegeSql){
		return originSql.replace(sql_where, sql_where +" "+privilegeSql);
	}
	
	/**
	 * 配置符合
	 * @param originSql
	 * @param tableName
	 * @return
	 */
	public static List<String> matcherTableSql(String originSql,String tableName){
		 ArrayList<String> matcharList = new ArrayList<String>();
		 String regex = tableName+"[ ,](.*?)WHERE";
	     Pattern pattern = Pattern.compile(regex);
	     Matcher matcher = pattern.matcher(originSql);
	     while (matcher.find()) {
	        matcharList.add(matcher.group());
	     }
	     return matcharList;
	}
	
	/**
	 * 查统计sql
	 * @param sql
	 * @return
	 */
	public static String getSumSql(String sql,String sumPropertys){
		if(StringUtils.isBlank(sumPropertys))return sql;
		String annotationSql="";
		if(sql.contains("*/")){
			int index=sql.lastIndexOf("*/");
			annotationSql=sql.substring(0,index+2);
			sql=sql.substring(index+2);
		}
		String sumStr=" SELECT ";
		String[] strArr=sumPropertys.split(",");
		int len=strArr.length-1;
		String propertyName="";
		for(int i=0;i<=len;i++){
			propertyName=CommonUtil.convertJaveBeanStrToUnderLine(strArr[i]);
			sumStr+="sum(sum_table."+propertyName+") "+propertyName;
		    if(i<len){sumStr+=",";}
		 };
		 sql=sumStr+" FROM ("+sql+")sum_table";
		 sql=annotationSql+ sql;
		return sql;
	};
	
//	public static String getAnnotationSql(){
//		
//	}
	
	 public static void main(String[] args) {
		 String sql="SELECT\r\n"
		 
+"bco.line_id, bco.bill_no, bco.bill_type_no, bco.ref_bill_no, bco.ref_bill_type_no, bco.contract_no, bco.brand_no,"
		+"bco.division_no, bco.order_type, bco.bill_status, bco.bill_status_max, bco.customer_no,bco.customer_type,"
		+"bco.order_date, bco.currency_no, bco.payment_clause_no, bco.order_source, bco.deliver_date, bco.creator,"
		+"bco.create_time,"
		+"bco.modifier,"
		+"bco.modify_time, bco.auditor, bco.audit_time, bco.remarks,"
	 
		 
		+"bs.division_name as divisionName,"
		+"bb.brand_cname as brandCname,"
		+"bm.customer_name as customerName\r\n\t\n\t,\t\n,\t"
	 
		+"FROM bl_co bco"
		+" inner  JOIN bas_brand  as  bb1 ON bco.brand_no=bb.brand_no"
		+" left JOIN bas_division bs ON bco.division_no=bs.division_no"
		+" LEFT JOIN  bas_customer bm ON bco.customer_no=bm.customer_no"
		+" WHERE 1=1";
	        String str = " SELECT *   FROM bl_co x,bl_co_dtl    WHERE 1=1 ";
//	        String regex = "bl_co(.*?)WHERE";
	        String addsql="select brand_no from user_brand    as a1_A where userid=[userid]";
	        
//	        sql=sql.replaceAll("LEFT[ ]+JOIN[ ]+", "LEFT JOIN ");
	        String reg="user_brand\\s+[a-zA-Z0-9]+\\s+(\\w+)\\b";
	        String regex = "\\b"+"user_brand"+"\\b\\s+(\\w+)\\b";
	        SystemUser s=new SystemUser();
	        s.setUserId(1);
	        SessionUtils.set(SysConstans.SESSION_USER, s);
	        ThreadLocals.setAddNoNullFilter("1");
//	        System.out.println( handlerSql(sql, addsql, "bas_brand", "bill_no"));
	        
	        URL url = JsqlparserUtils.class.getResource("sql.json");
	        String sqlsum="";
			try {
				sqlsum = FileUtils.readFileToString(FileUtils.toFile(url),
				        "utf-8");
			} catch (IOException e) {
				e.printStackTrace();
			}
	        
//		    String ss="aa";
//		    String resultstr="select ";
//		    String[] strArr=ss.split(",");
//		    int len=strArr.length-1;
//		    for(int i=0;i<=len;i++){
//		    	resultstr+="sum("+strArr[i]+") "+strArr[i];
//		    	if(i<len){resultstr+=",";}
//		    };
//		    System.out.println(resultstr);
//		    resultstr+=" from ("+sqlsum+")";
			
		    System.out.println(getSumSql(sqlsum,"sizeQty"));
		    
//	        Pattern pattern = Pattern.compile(reg);
//	        Matcher matcher = pattern.matcher(addsql);
//	        while (matcher.find()) {
//	        	System.out.println( matcher.group(1).trim());
//	        }

	      
	 }
	 
}
