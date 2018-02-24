package ht.plugin.util;

public class StringUtils {
	
	public static boolean isEmpty(String str){
		if(str==null || "".equals(str)) return true;
		return false;
	}
	/**
	 * @param name 字段名
	 * @param s 首字符是否大写
	 * */
	public static String tfNameTransfer(String name,boolean s){
		StringBuilder sb=new StringBuilder();
		String[] ss=name.split("_");
		for(String st: ss){
			if(sb.length()<=0 && !s) {
				sb.append(st);
				continue;
			}
			sb.append(st.substring(0,1).toUpperCase()+st.substring(1));
		}
		return sb.toString();
	}
}
