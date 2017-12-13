package ht.msc.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.xml.JRXmlLoader;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.ui.Model;

 

public class ReportFillterDataSource {
private static Logger logger = LoggerFactory.getLogger(ReportFillterDataSource.class);
	
	/**
	 * 填充报表数据
	 * @param list 数据源(必填)
	 * @param jasperName jasper文件名 (必填)
	 * @param model (必填)
	 * @param format(选填，默认为pdf)
	 */
	public static void ReportFillter(List<?> list,String jasperName,Model model,String format){
		  // 报表数据源  
        JRDataSource jrDataSource = new JRBeanCollectionDataSource(list);  
        format=StringUtils.isNotBlank(format)?format:"pdf";
        // 动态指定报表模板url  
        model.addAttribute("url", "/WEB-INF/jasper/"+jasperName+".jrxml");  
        model.addAttribute("format", format); // 报表格式  
        model.addAttribute("jrMainDataSource", jrDataSource);  
	}
	
	/**
	 * 填充报表数据
	 * @param list 数据源(必填)
	 * @param jasperName jasper文件名 (必填)
	 * @param model (必填)
	 */
	public static void ReportFillter(List<?> list,String jasperName,Model model){
		ReportFillter(list, jasperName, model, "pdf");
	}
	
	/**
	 * sql方式导出ireport
	 * @param jasperName jasper名称
	 * @param model
	 */
	public static void ReportFillterForSql(String jasperName,Model model,Map<String, Object> params,String format){
		  // 报表数据源  
      DataSource jdbcDataSource=(DataSource)SpringComponent.getBean("dataSource");
      format=StringUtils.isNotBlank(format)?format:"pdf";
      // 动态指定报表模板url  
      model.addAttribute("url", "/WEB-INF/jasper/"+jasperName+".jrxml");  
      model.addAttribute("format", format); // 报表格式  
      if(params!=null && params.size()>0){
          model.addAllAttributes(params);
      }
      model.addAttribute("jrMainDataSource", jdbcDataSource);  
	}
	
	/**
	 * sql方式导出ireport
	 * @param jasperName jasper名称
	 * @param model
	 */
	public static void ReportFillterForSql(String jasperName,Model model,Map<String, Object> params){
		ReportFillterForSql(jasperName, model,params,"pdf");
	}
	
	@SuppressWarnings({ "deprecation"})
	public static JasperPrint  fillReport(HttpServletRequest req,String jrxmlName,Map<String, Object> params,DataSource dataSource ){
		File f=new File(req.getRealPath("/WEB-INF/jasper/"+jrxmlName+".jrxml"));
		InputStream inputStream=null;
		JasperPrint jasperPrint=null;
		Connection con=null;
		con= DataSourceUtils.getConnection(dataSource);
		try{
			inputStream=new FileInputStream(f);
			JasperDesign design = JRXmlLoader.load(inputStream);
			JasperReport jsreprot= JasperCompileManager.compileReport(design);
			jasperPrint = JasperFillManager.fillReport(jsreprot, params, con);
		} catch (Exception e) {
			logger.error(e.toString());
			
		}finally{
			try {
				inputStream.close();
				con.close();
			} catch (Exception e) {
			}
			
		}
		 return jasperPrint;
	}
	
	
	@SuppressWarnings("deprecation")
	public static JasperPrint  fillReport(HttpServletRequest req,String jrxmlName,Map<String, Object> params, List<?> list){
		File f=new File(req.getRealPath("/WEB-INF/jasper/"+jrxmlName+".jrxml"));
		InputStream inputStream=null;
		JasperPrint jasperPrint=null;
		try {
			JRDataSource jrDataSource = new JRBeanCollectionDataSource(list);  
			inputStream=new FileInputStream(f);
			JasperDesign design = JRXmlLoader.load(inputStream);
			JasperReport jsreprot= JasperCompileManager.compileReport(design);
			jasperPrint = JasperFillManager.fillReport(jsreprot, params,jrDataSource);
		} catch (Exception e) {
			logger.error(e.toString());
		}finally{
			try {
				inputStream.close();
			} catch (Exception e) {
			}
		}
		  return jasperPrint;
	}
	
	@SuppressWarnings("deprecation")
	public static void exportReport(List<JasperPrint> jasperPrintList,HttpServletResponse response)throws Exception{
		  ByteArrayOutputStream baos = new ByteArrayOutputStream();
		   JRPdfExporter exporter = new JRPdfExporter();
		   exporter.setParameter(JRExporterParameter.JASPER_PRINT_LIST, jasperPrintList);
		   exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, baos);
		   exporter.exportReport();
		   byte[] bytes= baos.toByteArray();
		   response.getOutputStream().write(bytes);
	}
}
