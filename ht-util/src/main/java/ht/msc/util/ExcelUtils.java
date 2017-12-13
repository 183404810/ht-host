package ht.msc.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;


public class ExcelUtils {
private ExcelUtils(){
		
	}

	private static Logger logger = LoggerFactory.getLogger(ExcelUtils.class);
	
	public final static Map<String, Class<?>[]> SET_PARAMS_MAP;
	
	public static final DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
	static{
		SET_PARAMS_MAP = new HashMap<String, Class<?>[]>();
		SET_PARAMS_MAP.put("java.lang.String", new Class<?>[]{String.class});
		SET_PARAMS_MAP.put("int", new Class<?>[]{Integer.class});
		SET_PARAMS_MAP.put("java.lang.Integer", new Class<?>[]{Integer.class});
		SET_PARAMS_MAP.put("double", new Class<?>[]{Double.class});
		SET_PARAMS_MAP.put("java.lang.Double", new Class<?>[]{Double.class});
		SET_PARAMS_MAP.put("long", new Class<?>[]{Long.class});
		SET_PARAMS_MAP.put("java.lang.Long", new Class<?>[]{Long.class});
		SET_PARAMS_MAP.put("java.math.BigDecimal", new Class<?>[]{BigDecimal.class});
		SET_PARAMS_MAP.put("char", new Class<?>[]{Character.class});
		SET_PARAMS_MAP.put("java.lang.Character", new Class<?>[]{Character.class});
		SET_PARAMS_MAP.put("boolean", new Class<?>[]{Boolean.class});
		SET_PARAMS_MAP.put("java.lang.Boolean", new Class<?>[]{Boolean.class});
		SET_PARAMS_MAP.put("short", new Class<?>[]{Short.class});
		SET_PARAMS_MAP.put("java.lang.Short", new Class<?>[]{Short.class});
		SET_PARAMS_MAP.put("java.lang.String", new Class<?>[]{String.class});
		SET_PARAMS_MAP.put("java.util.Date", new Class<?>[]{Date.class});
	}
	/**
	 * 返回类属性的类型Map,key为对应的set方法名
	 * 
	 * @param c
	 * @return
	 */
	public static <T> Map<String, String> getFieldType(Class<T> c) {
		Map<String, String> map = new HashMap<String, String>();
		Method[] ms = c.getMethods();
		String setter;
		for (Method m : ms) {
			setter = m.getName();
			if (setter.indexOf("set") == 0) {
				map.put(setter, m.getParameterTypes()[0].getName());
			}
		}
		return map;
	}
	/**
	 * 从导入的Excel获取数据
	 * @param request
	 * @param colNames 列名数组
	 * @param mustArray 对应列是否为必须
	 * @param mainKey 确定是否重复的键
	 * @param c Bean.class
	 * @return
	 * @throws Exception
	 */
	public static <T> ImportResolve<T> getData(HttpServletRequest request,
			String[] colNames, String [] mustArray,String[] mainKey, Class<T> c,String specialField,String objJson) throws Exception{
		ImportResolve<T> ret = new ImportResolve<T>();
		try{
			ret= getData(request, 0, 1, colNames, mustArray, mainKey, c,objJson);
		}catch (Exception e) {
			throw new Exception(e);
		}
		return ret;
	}
	
	/**
	 * 从导入的Excel获取数据
	 * @param request
	 * @param sheetIdx Excel工作簿序号,从0开始
	 * @param firstLineIdx 首行数据序列号 一般从1开始
	 * @param colNames 列名数组
	 * @param mustArray 对应列是否为必须
	 * @param mainKey 确定是否重复的键
	 * @param c Bean.class
	 * @return
	 * @throws Exception
	 */
	public static <T> ImportResolve<T> getData(HttpServletRequest request, int sheetIdx,
			int firstLineIdx, String[] colNames, String [] mustArray,String[] mainKey, Class<T> c,String objJson) throws Exception{
		ImportResolve<T> importResolve=new ImportResolve<T>();
		List<Integer> rowlist=new ArrayList<Integer>();
		List<T> list = new ArrayList<T>();
		InputStream in = null;
		MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
		MultipartFile multipartFile = multipartRequest.getFile("importFileValue");
		Workbook wb = null;
		in = multipartFile.getInputStream();
		if(in.available()>5242880){
			throw new Exception("导入文件不能超过5M");
		}
//		if(in instanceof ByteArrayInputStream){
//			throw new Exception("导入文件内容不能为空");
//		}
		try {
			Map<String, String> mainKeyMap = new HashMap<String, String>();
			Map<String, String> mainKeyValueMap = new HashMap<String, String>();
			if(mainKey != null){
				for(String mk:mainKey){
					mainKeyMap.put(mk, mk);
				}
			} 
			int colNum = colNames.length;//
			Map<String, String> columsLine=new HashMap<String, String>();
			for(int i=0;i<colNum;i++){
				columsLine.put(colNames[i],String.valueOf((char)('A'+i))+"列");
			}
			importResolve.setColumsLine(columsLine);
			
			try{
				wb = new HSSFWorkbook(in);
			}catch (IllegalArgumentException e) {
				in=(FileInputStream)multipartFile.getInputStream();
				wb=new XSSFWorkbook(in);
			}
			Sheet sheet = wb.getSheetAt(sheetIdx);

			Map<String, String> fieldTypeMap = getFieldType(c);

			T t = null;
			String setter;
			String fieldTypeStr;
			String fieldName;
			String data;
			Class<?>[] filedType;
			Object[] params = null;
			Map<String,String> dateformat=new HashMap<String,String>();
			Method m;
			
			//计算后面的空白行数量
			int blankEndCount=0;
			for (int idx = sheet.getLastRowNum(); idx >=firstLineIdx; idx--) {
				Row row = sheet.getRow(idx);
				if(row==null){
					blankEndCount++;
				}else{
					boolean isRowAllBlank=true;
					for (int colIdx = 0; colIdx < colNum; colIdx++) {
						data = GetValueTypeForXLSX(row.getCell(colIdx));
						if(!StringUtils.isBlank(data)){
							isRowAllBlank=false;
						}						
					}
					if(isRowAllBlank){
						blankEndCount++;
					}else{
						break;
					}
				}
			}			
			
			int len=sheet.getLastRowNum()-blankEndCount + 1;
			if(len>1){
				logger.info("excel 需要解析的行数为:"+(len-1));
			}
			StringBuilder sb = new StringBuilder("");
			for (int idx = firstLineIdx; idx < len; idx++) {
				Row row = sheet.getRow(idx);
				String mainKeyValue = "";
				if(row==null){
					sb.append("【 ").append(idx+1).append("行").append(" 】不能为空<br/>");
					continue;
				}
				if(StringUtils.isBlank(objJson)){
					t = c.newInstance();
				}else{
					t=JSON.parseObject(objJson, c);
				}
				boolean flag=true;
				for (int colIdx = 0; colIdx < colNum; colIdx++) {
					data = GetValueTypeForXLSX(row.getCell(colIdx));
					fieldName = colNames[colIdx];
					if(mainKeyMap.get(fieldName) != null){
						mainKeyValue += data;
					}
					if(StringUtils.isBlank(fieldName)){
						continue;
					}
					
					setter = "set"
							+ String.valueOf(fieldName.charAt(0)).toUpperCase()
							+ fieldName.substring(1);
					fieldTypeStr = fieldTypeMap.get(setter);
					filedType = SET_PARAMS_MAP.get(fieldTypeStr);
					m = c.getMethod(setter, filedType);
					params = new Object[]{data};
					if("true".equals(mustArray[colIdx]) && StringUtils.isBlank(data)){
						sb.append("【 ").append(idx+1).append("行").append((char)('A'+colIdx)).append("列").append(" 】不能为空<br/>");
						flag=false;
						continue;
					}
					if(fieldTypeStr.indexOf("int")>=0 || fieldTypeStr.indexOf("Integer")>=0){
						try {
							params = new Object[]{Integer.valueOf(StringUtils.isBlank(data)?"0":data)};
							if(data.contains("."))throw new Exception("");
						} catch (NumberFormatException e) {
							sb.append("【 ").append(idx+1).append("行").append((char)('A'+colIdx)).append("列").append(" 】必须为数值<br/>");
							flag=false;
							continue;
						}
					}else if(fieldTypeStr.indexOf("BigDecimal")>=0){
						try{
							if(data.contains(".")){
								BigDecimal b1=new BigDecimal(data);
								BigDecimal b2=new BigDecimal(data.substring(0, data.indexOf(".")));
								if(b1.compareTo(b2)==0)throw new Exception("");
							}
							params = new Object[]{new BigDecimal(StringUtils.isBlank(data)?"0":data)};
						} catch (NumberFormatException e) {
							sb.append("【 ").append(idx+1).append("行").append((char)('A'+colIdx)).append("列").append(" 】必须为数值<br/>");
							flag=false;
							continue;
						}
					}else if(fieldTypeStr.indexOf("Date")>=0){
						try{
                            if(StringUtils.isEmpty(data))continue;
                            String formatStr=null;
							if(dateformat.containsKey(fieldName)){
								formatStr=dateformat.get(fieldName);
							}else{
								formatStr=(t.getClass().getDeclaredField(fieldName).isAnnotationPresent(JSONField.class))?
									 ((JSONField) t.getClass().getDeclaredField(fieldName).getAnnotation(JSONField.class)).format():"yyyy-MM-dd HH:mm:ss";
							}
							if(data.indexOf("-")>-1){
								params = new Object[]{DateUtil.getdate(data, formatStr)};
							}else{
								params = new Object[]{ DateUtil.getDateByDays(data, formatStr)};
							}
							dateformat.put(fieldName, formatStr);
						} catch (Exception e) {
							sb.append("【 ").append(idx+1).append("行").append((char)('A'+colIdx)).append("列").append(" 】必须为日期<br/>");
							flag=false;
							continue;
						}
					}else if(fieldTypeStr.indexOf("double")>=0 || fieldTypeStr.indexOf("Double")>=0){
						try{
							if(data.contains(".")){
								Double d=Double.valueOf(data);
								long l=(long)d.longValue();
								if(d-l==0)throw new Exception("");
							}
							params = new Object[]{Double.valueOf(StringUtils.isBlank(data)?"0":data)};
						} catch (NumberFormatException e) {
							sb.append("【 ").append(idx+1).append("行").append((char)('A'+colIdx)).append("列").append(" 】必须为数值<br/>");
							flag=false;
							continue;
						}
					}else if(fieldTypeStr.indexOf("long")>=0 || fieldTypeStr.indexOf("Long")>=0){
						try{
							params = new Object[]{Long.valueOf(StringUtils.isBlank(data)?"0":data)};
							if(data.contains("."))throw new Exception("");
						} catch (NumberFormatException e) {
							sb.append("【 ").append(idx+1).append("行").append((char)('A'+colIdx)).append("列").append(" 】必须为数值<br/>");
							flag=false;
							continue;
						}
					}else if(fieldTypeStr.indexOf("char")>=0 || fieldTypeStr.indexOf("Character")>=0){
						params = new Object[]{data.charAt(0)};
					}else if(fieldTypeStr.indexOf("boolean")>=0 || fieldTypeStr.indexOf("Boolean")>=0){
						params = new Object[]{Boolean.valueOf(data)};
					}else if(fieldTypeStr.indexOf("short")>=0 || fieldTypeStr.indexOf("Short")>=0){
						try{
							params = new Object[]{Short.valueOf(StringUtils.isBlank(data)?"0":data)};
						} catch (NumberFormatException e) {
							sb.append("【 ").append(idx+1).append("行").append((char)('A'+colIdx)).append("列").append(" 】必须为数值<br/>");
							flag=false;
							continue;
						}
					}
					
					
					m.invoke(t, params);
					
				}
				if(!StringUtils.isBlank(mainKeyValue)){
					if(mainKeyValueMap.get(mainKeyValue) != null){
						sb.append("【 ").append(idx+1).append("行").append(" 】为重复数据<br/>");
						continue;
					}else{
						mainKeyValueMap.put(mainKeyValue, mainKeyValue);
					}
				}
				if(flag){
					rowlist.add(idx+1);
					list.add(t);
				}
			}
			if(sb.length() > 0){
				ThreadLocals.setErrorMsgs("数据异常:"+sb.toString());
				//throw new Exception("数据异常:"+sb.toString());
				importResolve.setErrorMsg("数据异常:"+sb.toString());
			}else{
				importResolve.setErrorMsg("");
			}
		} catch (IOException e) {
			throw new Exception(e.getMessage(),e);
		} catch (NumberFormatException e) {
			throw new Exception(e);
		} catch (NullPointerException e) {
			throw new Exception(e.getMessage(),e);
		} catch (InstantiationException e) {
			throw new Exception(e.getMessage(),e);
		} catch (IllegalAccessException e) {
			throw new Exception(e.getMessage(),e);
		} catch (IllegalArgumentException e) {
			throw new Exception(e.getMessage(),e);
		} catch (InvocationTargetException e) {
			throw new Exception(e.getMessage(),e);
		} catch (Exception e) {
			throw new SCException("myselfMsg:文件解析不成功，请把文件另存为Excel 97-2003工作簿再导入!");
//			throw new Exception(e.getMessage(),e);
		} 
		importResolve.setRowlist(rowlist);
		importResolve.setDataList(list);
		logger.info("excel 解析成功的行数为:"+list.size());
		return importResolve;
	}

	/**
	 * 将Double转成String,可能不含有小数和小数点
	 * @param value
	 * @return
	 */
	public static String doubleToString(Double value){
		String rs = "";
		if(value != null){
			Long a = value.longValue();
			double b = value.doubleValue() - a;
			if(b == 0){
				rs = a.toString();
			}else{
				rs = value.toString();
			}
		}
		return rs;
	}
	public static String GetValueTypeForXLSX(Cell cell) {
		if (cell == null)
			return null;
		Object obj;
		cell.setCellType(Cell.CELL_TYPE_STRING);
		obj = cell.getStringCellValue();
		return obj == null ? "" : (obj+"").trim();
	}
}
