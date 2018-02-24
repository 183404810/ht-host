package ht.plugin.context;

import java.util.ArrayList;
import java.util.List;

import ht.plugin.configration.Configration;
import ht.plugin.configration.config.JavaFileConfig;
import ht.plugin.generate.GeneratedFile;
import ht.plugin.generate.GeneratorJavaFile;
import ht.plugin.generate.GeneratorServiceJavaFile;
import ht.plugin.introspect.DBIntrospect;
import ht.plugin.introspect.IColumn;
import ht.plugin.introspect.IField;
import ht.plugin.introspect.IJavaType;
import ht.plugin.introspect.ITable;
import ht.plugin.properties.LayoutEnum;
import ht.plugin.util.StringUtils;

public class MybatisGenerator implements Generator{
	private PluginContext context;
	
	public MybatisGenerator(PluginContext context){
		this.context=context;
	}
	
	public void generate(List<GeneratedFile> generatedFiles,List<String> tableList){
		generatedFiles.clear();
		Configration config=context.getConfig();
 
		DBIntrospect introspect=new DBIntrospect(context);
		introspect.introspect(tableList);
		for(ITable table:context.getTables()){
			String tableName=StringUtils.tfNameTransfer(table.getTableName(),true);
			List<IField> fields=getIField(table.getColumns());
			generatedFiles.add(new GeneratorServiceJavaFile(tableName+"Mapper",tableName,config.getXfconfig().getProperty("targetProject"),config.getXfconfig().getProperty("targetPackage"),LayoutEnum.DAO_LAYOUT,table.getTableName(),config));
			for(String key:config.getConfig().keySet()){
				JavaFileConfig cfg=config.getConfig().get(key);
				String targetProject=cfg.getTargetProject();
				String targetPakage=cfg.getTargetPackage();
				switch(key){
					case "javaModelGenerator":
						generatedFiles.add(new GeneratorJavaFile(fields,targetProject,targetPakage,tableName,"public",tableName,LayoutEnum.DAO_LAYOUT));
						break;
					case "javaServiceGenerator":
						if(cfg.isEnableInterfaceSupInterfaceGenericity())
							generatedFiles.add(new GeneratorServiceJavaFile(tableName+"Service",tableName,config,key,LayoutEnum.SERVICE_LAYOUT,table.getTableName()));
						generatedFiles.add(new GeneratorServiceJavaFile(tableName+"ServiceImpl",tableName,config,key,LayoutEnum.SERVICE_LAYOUT,table.getTableName()));
						break;
					case "javaDaoGenerator":
						if(cfg.isEnableInterfaceSupInterfaceGenericity())
							generatedFiles.add(new GeneratorServiceJavaFile(tableName+"Dao",tableName,config,key,LayoutEnum.DAO_LAYOUT,table.getTableName()));
						generatedFiles.add(new GeneratorServiceJavaFile(tableName+"DaoImpl",tableName,config,key,LayoutEnum.DAO_LAYOUT,table.getTableName()));
						break;
					case "javaControllerGenerator":
						generatedFiles.add(new GeneratorServiceJavaFile(tableName+"Controller",tableName,config,key,LayoutEnum.CONTROLLER_LAYOUT,table.getTableName()));
						break;
				}
			}
		}
	}
	
	private List<IField> getIField(List<IColumn> columns){
		List<IField> fields=new ArrayList<>();
		for(IColumn column: columns){
			String tabContext="  ";
			IField field=new IField(tabContext,column.getColumnName(),convert(column.getTypeName()),
					"private",column.getRemarks());
			fields.add(field);
		}
		return fields;
	}
	
	/**
	 * 转换数据库类型为java类型
	 * 
	 * */
	private IJavaType convert(String String){
		switch(String.toLowerCase()){
			case "char":
			case "varchar":
			case "text":
			case "nchar":
			case "nvarchar":
			case "ntext":
				return IJavaType.getStringInstance();
			case "datetime":
			case "smalldatetime":
			case "date":
				return IJavaType.getDateInstance();
			case "bitint":
			case "int":
			case "smallint":
			case "tinyint":	
			case "bit":	
				return IJavaType.getIntegerInstance();
			case "decimal":
			case "numeric":	
			case "float":	
				return IJavaType.getBigDecimalInstance();
			case "bigint":
				return IJavaType.getLongInstance();
		}
		return null;
	}
}
