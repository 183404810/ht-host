package ht.msc.util;

import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFFooter;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Footer;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import com.alibaba.fastjson.JSON;

public class ExcelExport {
	/**
	 * 工具类防止用户new出实例
	 */
	private ExcelExport(){}

	/**
	 * 导出数据到Excel,自动获取easyui的表头信息 与 查询条件  暂时不支持合并的表头  纵横转换的表头是单独写的
	 * @param fileName
	 * @param ColumnsMapList
	 * @param dataMapList
	 * @param response
	 * @param rowAccessWindowSize 导出excel过程中，如果需要访问导的第几行数据，则，需要给定这个参数为访问的excel行数；如传递的为空，则默认值为1行，
	 * 推荐使用默认值。 例如：如果想在程序中取得最后100行的数据，那么该参数=100； 否则就按照默认值导出。
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	public static void commonExportData(String fileName, List<Map> ColumnsMapList, List<Map> dataMapList,
			HttpServletResponse response, Integer rowAccessWindowSize,String fileType,Class<?> clazz) throws Exception {
		response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=UTF-8;");
		fileName=StringUtils.isNotBlank(fileName)?fileName:String.valueOf(new Date().getTime());
		fileType=StringUtils.isBlank(fileType)?"xls":fileType;
		fileName = new String(fileName.getBytes(), "ISO8859-1");
		//文件名
		response.setHeader("Content-Disposition", "attachment;filename=" +fileName + "."+fileType);
		response.setHeader("Pragma", "no-cache");

		if (rowAccessWindowSize == null) {

			rowAccessWindowSize = 1;
		}

		SXSSFWorkbook wb = new SXSSFWorkbook(rowAccessWindowSize.intValue());
		Sheet sheet1 = wb.createSheet();
		wb.setSheetName(0, "Sheet1");
		sheet1.setDefaultRowHeightInPoints(20);
		sheet1.setDefaultColumnWidth((short) 16);
		//设置页脚
		Footer footer = sheet1.getFooter();
		footer.setRight("Page " + HSSFFooter.page() + " of " + HSSFFooter.numPages());

		//设置样式 表头
		CellStyle style1 = wb.createCellStyle();
		style1.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		Font font1 = wb.createFont();
		font1.setFontHeightInPoints((short) 13);
		font1.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		style1.setFont(font1);
		//设置样式 表头
		CellStyle styleRIGHT = wb.createCellStyle();
		styleRIGHT.setAlignment(HSSFCellStyle.ALIGN_RIGHT);
		styleRIGHT.setWrapText(false);
		CellStyle styleLEFT = wb.createCellStyle();
		styleLEFT.setAlignment(HSSFCellStyle.ALIGN_LEFT);
		styleLEFT.setWrapText(false);
		
		
//		//合并
//		CellRangeAddress rg1 = new CellRangeAddress(0, (short) 0, 0, (short) (ColumnsMapList.size() - 1));
//		sheet1.addMergedRegion(rg1);
		//设置样式 表头
		CellStyle style3 = wb.createCellStyle();
		style3.setAlignment(HSSFCellStyle.ALIGN_LEFT);
		Font font3 = wb.createFont();
		font3.setFontHeightInPoints((short) 18);
		font3.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		style3.setFont(font3);
		style3.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
		style3.setFillPattern(CellStyle.SOLID_FOREGROUND);
//		Row row0 = sheet1.createRow(0);
//		row0.setHeightInPoints(35);
//		//第一行 提示长
//		Cell cell0 = row0.createCell((short) 0);
//		cell0.setCellValue(fileName.toString());
//		cell0.setCellStyle(style3);

		//设置表头
		Row row1 = sheet1.createRow(0);
		row1.setHeightInPoints(20);
		for (int i = 0; i < ColumnsMapList.size(); i++) {
			Cell cell1 = row1.createCell(i);
			cell1.setCellType(HSSFCell.ENCODING_UTF_16);
			cell1.setCellValue(ColumnsMapList.get(i).get("title").toString());
			cell1.setCellStyle(style1);
		}
		Map<String,Boolean> isfieldMap=new HashMap<String,Boolean>();
		//填充数据
		for (int j = 0; j < dataMapList.size(); j++) {
			Row row2 = sheet1.createRow((j + 1)); // 第三行开始填充数据 
			Map cellDataMap = dataMapList.get(j);
			for (int i = 0; i < ColumnsMapList.size(); i++) {
				Cell cell = row2.createCell(i);
				String cellValue = StringUtils.EMPTY;
				String fieldString=null;
				if (ColumnsMapList.get(i).get("field") != null) {
					fieldString = String.valueOf(ColumnsMapList.get(i).get("field"));
					cellValue = String.valueOf(cellDataMap.get(fieldString));
				}
				setCellvalue(cell, cellValue, fieldString, clazz, isfieldMap,styleRIGHT,styleLEFT);
//				cell.setCellStyle(style2);
			}

		}

		wb.write(response.getOutputStream());
		response.getOutputStream().flush();
		response.getOutputStream().close();
		wb.dispose();
	}
	
	/**
	 * 导出excel
	 * @param fileName 文件名
	 * @param exportColumns 导出列 json字符串 格式如：[{"field":"appCode","title":"编码"},{"field":"name","title":"名称"}]
	 * @param datalist 数据list
	 * @param response
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	public static void commonExportData(String fileName, String exportColumns, List datalist,	HttpServletResponse response,String fileType) throws Exception {
           List<Map> listArrayList= new ArrayList< Map>();
           Map map;
           Class<?> clazz=null;
            if (datalist!= null&&datalist.size()>0){
               clazz=datalist.get(0).getClass();
               for (Object vo:datalist){
                   map= new HashMap();
                  CommonUtil.object2MapWithoutNull(vo,map);
                  listArrayList.add(map);
               }
            }
            List<Map> ColumnsMapList=JSON.parseArray(exportColumns, Map.class);
            commonExportData(fileName, ColumnsMapList, listArrayList,
            		response, 1, fileType,clazz);
	}
	
	/**
	 * 导出excel(导入一个sheet)
	 * @param fileName 文件名
	 * @param exportColumns 导出列 json字符串 格式如：[{"field":"appCode","title":"编码"},{"field":"name","title":"名称"}]
	 * @param datalist 数据list
	 * @param response
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	public static void commonExportData(List datalist,
		 HttpServletResponse response,HttpServletRequest req) throws Exception {
		 req.setCharacterEncoding("utf-8");
		 String exportColumns=(String)URLDecoder.decode(req.getParameter( "exportColumns"),"utf-8");
         String fileName=  StringEscapeUtils.unescapeJava(req.getParameter( "fileName"));
         String fileType=req.getParameter("fileType");
        
         commonExportData(fileName, exportColumns, datalist, response, fileType);
	}
	
	
	
	/**
	 * 加载excel基本信息
	 * @param response
	 * @param fileName 导入excel名称
	 * @throws Exception
	 */
	public static SXSSFWorkbook initExcel( HttpServletResponse response,String fileName,String fileType) throws Exception{
		SXSSFWorkbook wb =new SXSSFWorkbook();
		response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
		fileName=StringUtils.isNotBlank(fileName)?fileName:String.valueOf(new Date().getTime());
		String fileName2 = new String(fileName.getBytes("gb2312"), "iso-8859-1");
		fileType=StringUtils.isBlank(fileType)?"xls":fileType;
		//文件名
		response.setHeader("Content-Disposition", "attachment;filename=" + fileName2 + "."+fileType);
		response.setHeader("Pragma", "no-cache");
		return wb;
	}
	
	/**
	 * 设置表头信息
	 * @param wb
	 * @param ColumnsMapList
	 * @param sheetName
	 * @param sheetIndex
	 * @return
	 */
	@SuppressWarnings("rawtypes") 
	public static Sheet initSheet(SXSSFWorkbook wb, List<Map> columnsMapList,String sheetName,int sheetIndex){
		Sheet sheet = wb.createSheet();
		sheetName=StringUtils.isBlank(sheetName)?"sheet"+sheetIndex:sheetName;
		wb.setSheetName(sheetIndex, sheetName);
		sheet.setDefaultRowHeightInPoints(20);
		sheet.setDefaultColumnWidth((short) 16);
		//设置页脚
		Footer footer = sheet.getFooter();
		footer.setRight("Page " + HSSFFooter.page() + " of " + HSSFFooter.numPages());
		//设置样式 表头
		CellStyle style1 = wb.createCellStyle();
		style1.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		Font font1 = wb.createFont();
		font1.setFontHeightInPoints((short) 13);
		font1.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		style1.setFont(font1);
		Row row1 = sheet.createRow(0);
		row1.setHeightInPoints(20);
		for (int i = 0; i <columnsMapList.size(); i++) {
			Cell cell1 = row1.createCell(i);
			cell1.setCellType(HSSFCell.ENCODING_UTF_16);
			cell1.setCellValue(columnsMapList.get(i).get("title").toString());
			cell1.setCellStyle(style1);
		}

		return sheet;
	}
	
	/**
	 * 填充数据
	 * @param wb
	 * @param sheet
	 * @param dataMapList 数据项
	 * @param ColumnsMapList 表头
	 */
	@SuppressWarnings("rawtypes") 
	public static void fillSheetData(SXSSFWorkbook wb,Sheet sheet,List dataList, List<Map> columnsMapList)throws Exception{
		 List<Map> dataMapList= new ArrayList< Map>();
         Map map;
          if (dataList!= null&&dataList.size()>0){
             for (Object vo:dataList){
                 map= new HashMap();
                CommonUtil.object2MapWithoutNull(vo,map);
                dataMapList.add(map);
                     
             }
          }
		//设置样式 表头
				CellStyle style = wb.createCellStyle();
				style.setAlignment(HSSFCellStyle.ALIGN_RIGHT);
				//style.setWrapText(true);
				//填充数据
				Map cellDataMap;
				for (int j = 0; j < dataMapList.size(); j++) {
					Row row2 = sheet.createRow((j + 1)); // 第三行开始填充数据 
					 cellDataMap = dataMapList.get(j);
					for (int i = 0; i < columnsMapList.size(); i++) {
						Cell cell = row2.createCell(i);
						String cellValue = StringUtils.EMPTY;
						if (columnsMapList.get(i).get("field") != null) {
							String fieldString = String.valueOf(columnsMapList.get(i).get("field"));
							cellValue = String.valueOf(cellDataMap.get(fieldString));
						}
						cell.setCellValue(cellValue);
						cell.setCellStyle(style);
					}

				}
	}
	
	/**
	 * 输出excel
	 * @param wb
	 * @param response
	 * @throws Exception
	 */
	public static void responseExcel(SXSSFWorkbook wb,HttpServletResponse response) throws Exception{
		wb.write(response.getOutputStream());
		response.getOutputStream().flush();
		response.getOutputStream().close();
		wb.dispose();
	}
	
	/**
	 * 设置单元格式数字类型
	 * @param cell  单元格实体
	 * @param cellValue  单元格式的值
	 * @param fieldString 属性名 如:appCode
	 * @param clazz  实体类型
	 * @param isfieldMap 用于存储列的数据类型 （在行数据循环实例化）
	 */
	public static void setCellvalue(Cell cell,String cellValue,String fieldString,Class<?> clazz ,Map<String,Boolean> isfieldMap){
		if(StringUtils.isNotBlank(cellValue) && StringUtils.isNotBlank(fieldString)){
			if(!isfieldMap.containsKey(fieldString)){
				if(CommonUtil.fieldIsNumber(clazz, fieldString)){
					cell.setCellValue(new Double(cellValue));
					isfieldMap.put(fieldString, true);
					return;
				}
			}else{
				if(isfieldMap.get(fieldString)){
					cell.setCellValue(new Double(cellValue));
					return;
				}else{
					cell.setCellValue(cellValue);
					return;
				}
			}
		}
		isfieldMap.put(fieldString, false);
	    cell.setCellValue(cellValue);
		
	}
	
	
	/**
	 * 设置单元格式数字类型
	 * @param cell  单元格实体
	 * @param cellValue  单元格式的值
	 * @param fieldString 属性名 如:appCode
	 * @param clazz  实体类型
	 * @param isfieldMap 用于存储列的数据类型 （在行数据循环实例化）
	 * @param CellStyle 单元格样式(右对齐)
	 * @param CellStyle 单元格样式（左对齐)
	 */
	public static void setCellvalue(Cell cell,String cellValue,String fieldString,Class<?> clazz ,Map<String,Boolean> isfieldMap,CellStyle styleRIGHT ,CellStyle styleLEFT){
		if(StringUtils.isNotBlank(cellValue) && StringUtils.isNotBlank(fieldString)){
			if(!isfieldMap.containsKey(fieldString)){
				if(CommonUtil.fieldIsNumber(clazz, fieldString)){
					cell.setCellValue(new Double(cellValue));
					isfieldMap.put(fieldString, true);
					cell.setCellStyle(styleRIGHT);
					return;
				}
			}else{
				if(isfieldMap.get(fieldString)){
					cell.setCellValue(new Double(cellValue));
					cell.setCellStyle(styleRIGHT);
					return;
				}else{
					cell.setCellValue(cellValue);
					cell.setCellStyle(styleLEFT);
					return;
				}
			}
		}
		cell.setCellStyle(styleLEFT);
		//isfieldMap.put(fieldString, false);
	    cell.setCellValue(cellValue);
		
	}
	
	/**
	 * 导出excel(导出一个sheet)，仅用于导出尺码信息的多表头
	 * @param datalist 表体信息
	 * @param headList 尺码表头信息
	 * @param columnMap 多表头的每个尺码包含的列信息，
	 * fieldStart:列字段的开始字符，多个按顺序以","分隔，跟尺码表头SizeRowColWrapModel匹配由f开始，也可自定义； 
	 * title:列显示名，多个按顺序以","分隔，如果不需要显示列名，将值设为null。
	 * 例：{"fieldStart": "f,g", "title": "尺码数量,收货数量"}
	 * @param response
	 * @param req
	 * @throws Exception
	 */
	public static void commonExportMore(List datalist, List headList,  Map<String, String> columnMap,
			HttpServletResponse response, HttpServletRequest req) throws Exception {
		if (headList == null || headList.size() == 0){
			ExcelExport.commonExportData(datalist, response, req);
			return;
		}
		req.setCharacterEncoding("utf-8");
		String exportColumns=(String)URLDecoder.decode(req.getParameter( "exportColumns"),"utf-8");
        String fileName= StringEscapeUtils.unescapeJava(req.getParameter( "fileName"));
        String fileType=req.getParameter("fileType");
        commonExportMore(fileName, exportColumns, datalist, headList, columnMap, response, fileType);
	}
	
	public static void commonExportMore(String fileName, String exportColumns, List datalist, List headList,   
			Map<String, String> columnMap,HttpServletResponse response,String fileType) throws Exception {
		List<Map> listArrayList= new ArrayList< Map>();
        Map map;
        Class<?> clazz=null;
        if (datalist!= null&&datalist.size()>0){
        	clazz = datalist.get(0).getClass();
            for (Object vo:datalist){
                map= new HashMap();
                CommonUtil.object2MapWithoutNull(vo,map);
                listArrayList.add(map);
            }
        }
        List<String> fieldList = new ArrayList<String>();
        initFieldList(columnMap, fieldList);
        List<Map> ColumnsMapList=JSON.parseArray(exportColumns, Map.class);
        preRemoveColumn(ColumnsMapList, fieldList);
        List<List<Map>> columnsSizeList = columnsListAddSizeList(headList, ColumnsMapList, columnMap);
        removeNullSize(columnsSizeList, columnMap);
        commonExportMore(fileName, columnsSizeList, listArrayList, columnMap, response, 1, fileType, clazz);
	}
	
	public static void commonExportMore(String fileName, List<List<Map>> columnsSizeList, List<Map> dataMapList, 
			Map<String, String> columnMap,
			HttpServletResponse response, Integer rowAccessWindowSize,String fileType,Class<?> clazz) throws Exception {
		response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
		fileName=StringUtils.isNotBlank(fileName)?fileName:String.valueOf(new Date().getTime());
		fileType=StringUtils.isBlank(fileType)?"xls":fileType;
		fileName = new String(fileName.getBytes(), "ISO8859-1");
		//文件名
		response.setHeader("Content-Disposition", "attachment;filename=" + fileName + "."+fileType);
		response.setHeader("Pragma", "no-cache");

		if (rowAccessWindowSize == null) {
			rowAccessWindowSize = 1;
		}

		SXSSFWorkbook wb = new SXSSFWorkbook(rowAccessWindowSize.intValue());
		Sheet sheet1 = wb.createSheet();
		wb.setSheetName(0, "Sheet1");
		sheet1.setDefaultRowHeightInPoints(20);
		sheet1.setDefaultColumnWidth((short) 16);
		//设置页脚
		Footer footer = sheet1.getFooter();
		footer.setRight("Page " + HSSFFooter.page() + " of " + HSSFFooter.numPages());

		//设置样式 表头
		CellStyle style1 = wb.createCellStyle();
		style1.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		Font font1 = wb.createFont();
		font1.setFontHeightInPoints((short) 13);
		font1.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		style1.setFont(font1);
		//设置样式 表头
		CellStyle styleRIGHT = wb.createCellStyle();
		styleRIGHT.setAlignment(HSSFCellStyle.ALIGN_RIGHT);
		styleRIGHT.setWrapText(false);
		CellStyle styleLEFT = wb.createCellStyle();
		styleLEFT.setAlignment(HSSFCellStyle.ALIGN_LEFT);
		styleLEFT.setWrapText(false);
		
		//设置样式 表头
		CellStyle style3 = wb.createCellStyle();
		style3.setAlignment(HSSFCellStyle.ALIGN_LEFT);
		Font font3 = wb.createFont();
		font3.setFontHeightInPoints((short) 18);
		font3.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		style3.setFont(font3);
		style3.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
		style3.setFillPattern(CellStyle.SOLID_FOREGROUND);
		Map<String,Boolean> isfieldMap=new HashMap<String,Boolean>();

		int headSize= columnsSizeList.size();
		//设置表头
		for (int j = 0; j < headSize; j++){
			Row row1 = sheet1.createRow(j);
			row1.setHeightInPoints(20);
			List<Map> ColumnsMapList = columnsSizeList.get(j);
			for (int i = 0; i < ColumnsMapList.size(); i++) {
				Cell cell1 = row1.createCell(i);
				cell1.setCellType(HSSFCell.ENCODING_UTF_16);
				String title = ColumnsMapList.get(i).get("title").toString();
				cell1.setCellValue("0".equals(title) ? "" : title);
				cell1.setCellStyle(style1);
			}
		}

		//合并表头
		for (int j = 0; j < headSize; j++){
			String[] starts = columnMap.get("fieldStart").split(",");
			List<Map> ColumnsMapList = columnsSizeList.get(j);
			for (int i = ColumnsMapList.size() -1 ; i >= 0 ; i--) {
				String title = ColumnsMapList.get(i).get("title").toString();
				String field = ColumnsMapList.get(i).get("field").toString();
				if (StringUtils.isEmpty(title) 
						&& field.indexOf(starts[starts.length -1]) == 0
						&& Pattern.compile("[0-9]*").matcher(field.substring(1)).matches()){
					sheet1.addMergedRegion(new CellRangeAddress(j, j, i - starts.length + 1, i));
				}
				if (j == headSize-1 && StringUtils.isEmpty(title)
						&& field.indexOf(starts[starts.length -1]) != 0){
					sheet1.addMergedRegion(new CellRangeAddress(0, j, i, i));
				}
			}
			
		}

		List<Map> ColumnsMapList = columnsSizeList.get(0);
		Map<String,Boolean> fieldTypeMap = new HashMap<String,Boolean>();
		//填充数据
		for (int j = 0; j < dataMapList.size(); j++) {
			Row row2 = sheet1.createRow((j + headSize)); // 第headSize+1行开始填充数据 
			Map cellDataMap = dataMapList.get(j);
			for (int i = 0; i < ColumnsMapList.size(); i++) {
				Cell cell = row2.createCell(i);
				String cellValue = StringUtils.EMPTY;
				String fieldString = null;
				if (ColumnsMapList.get(i).get("field") != null) {
					fieldString = String.valueOf(ColumnsMapList.get(i).get("field"));
					cellValue = String.valueOf(cellDataMap.get(fieldString));
				}
				setCellvalue(cell, cellValue, fieldString, clazz, isfieldMap, styleRIGHT, styleLEFT);
//				setCellvalue(cell, cellValue, fieldString, clazz, fieldTypeMap);
//				cell.setCellStyle(style2);
			}

		}

		wb.write(response.getOutputStream());
		response.getOutputStream().flush();
		response.getOutputStream().close();
		wb.dispose();
	}
	
	/**
	 * 初始化预删除列字段
	 * @param columnMap
	 * @param fieldList
	 */
	private static void initFieldList(Map<String, String> columnMap, List<String> fieldList){
		String[] starts = columnMap.get("fieldStart").split(",");
		for (int i = 1; i <= 20; i++){
			for (String start : starts){
				fieldList.add(start + i);
			}
		}
		fieldList.add("sizeTypeNo");
	}
	
	/**
	 * 预删除列字段
	 * @param ColumnsMapList
	 * @param fields
	 */
	private static void preRemoveColumn(List<Map> columnsMapList, List<String> fieldList){
		if (columnsMapList == null || columnsMapList.size() == 0){
			return;
		}
		for (int i = columnsMapList.size() - 1; i >= 0; i--){
			Map columnsMap = columnsMapList.get(i);
			if (fieldList.contains(columnsMap.get("field"))){
				columnsMapList.remove(i);
			}
		}
	}
	
	/**
	 * 表头数据（多条）增加尺码数据,此时会有多出的一行用来分解成多列的列名
	 * @param headList
	 * @param ColumnsMapList
	 * @return
	 */
	private static List<List<Map>> columnsListAddSizeList(List headList, 
			List<Map> ColumnsMapList, Map<String, String> columnMap) throws Exception {
		List<List<Map>> columnsSizeList = new ArrayList<List<Map>>();
        if (headList != null && headList.size() > 0){
			 for (int i = 0, len = headList.size(); i < len; i++){
				 Object sizeModel = headList.get(i);
				 List<Map> columnsSize = new ArrayList<Map>();
				 if (i == 0){
					 columnsSize.addAll(ColumnsMapList);
				 } else {
					 for (Map columns : ColumnsMapList){
						 Map<String, String> sizeMap = new HashMap<String, String>();
		        		 sizeMap.put("field", columns.get("field").toString());
		        		 sizeMap.put("title", "");
		        		 columnsSize.add(sizeMap);
			         }
				 }
				 setSizeToColumns(sizeModel, columnsSize, columnMap);
				 columnsSizeList.add(columnsSize);
			 }
			 if (columnMap.get("title") != null 
					 && StringUtils.isNotEmpty(columnMap.get("title").toString())){
				 List<Map> columnsSize = new ArrayList<Map>();
				 for (Map columns : ColumnsMapList){
					 Map<String, String> sizeMap = new HashMap<String, String>();
	        		 sizeMap.put("field", columns.get("field").toString());
	        		 sizeMap.put("title", "");
	        		 columnsSize.add(sizeMap);
		         }
				 setSizeToColumns(null, columnsSize, columnMap);
				 columnsSizeList.add(columnsSize);
			 }
        }
        return columnsSizeList;
	}
	
	/**
	 * 表头数据（多条）增加尺码数据
	 * @param sizeModel
	 * @param columnsSize
	 * @return
	 */
	private static List<Map> setSizeToColumns(Object sizeModel, 
			List<Map> columnsSize, Map<String, String> columnMap) throws Exception {
		Map<String, String> sizeMap = new HashMap<String, String>();
		sizeMap.put("field", "sizeTypeNo");
		if (sizeModel == null){
			sizeMap.put("title", "配码组");
		} else {
			Method method = sizeModel.getClass().getMethod("getSizeTypeNo", new Class[0]);
			Object obj = method.invoke(sizeModel, new Object[0]);
			sizeMap.put("title", obj.toString());
		}
		columnsSize.add(sizeMap);
		String[] starts = columnMap.get("fieldStart").split(",");
		for (int i = 1; i <= 20; i++){
			for (int j = 0; j < starts.length; j++){
				Map<String, String> fMap = new HashMap<String, String>();
				fMap.put("field", starts[j] + i);
				if (sizeModel == null) {
					fMap.put("title", columnMap.get("title").split(",")[j]);
				} else if (j == 0){
					Method method = sizeModel.getClass().getMethod("get"+ starts[0].toUpperCase() + i, new Class[0]);
					Object obj = method.invoke(sizeModel, new Object[0]);
					fMap.put("title", obj.toString());
				} else {
					fMap.put("title", "");
				}
				columnsSize.add(fMap);
			}
		}
		return columnsSize;
	}
	
	/**
	 * 删除空白的尺码数据
	 * @param columnsSizeList
	 * @return
	 */
	private static List<List<Map>> removeNullSize(List<List<Map>> columnsSizeList, Map<String, String> columnMap){
		List<Integer> nullSizeList = new ArrayList<Integer>();
		List<Map> columnsSize = columnsSizeList.get(0);
		for (int j = 0, len = columnsSize.size(); j < len; j++){
			boolean nullSizeFlag = true;
			int size = columnMap.get("title") == null ? columnsSizeList.size() : columnsSizeList.size() - 1;
			for (int i = 0; i < size; i++){
				Map map = columnsSizeList.get(i).get(j);
				if (!map.get("title").toString().equals("0")){
					nullSizeFlag = false;
					break;
				}
			}
			if (nullSizeFlag){
				size = columnMap.get("fieldStart").split(",").length;
				for (int i = 0; i < size; i++){
					nullSizeList.add(0, j + i);
				}
			}
		}
		if (nullSizeList.size() == 0){
			return columnsSizeList;
		}
		for (int k = 0; k < nullSizeList.size(); k++){
			for (int i = 0; i < columnsSizeList.size(); i++){
				columnsSizeList.get(i).remove(nullSizeList.get(k).intValue());
			}
		}
		return columnsSizeList;
	}
}
