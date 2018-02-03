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
			String tableName=getTableEntriyName(table.getTableName());
			for(String key:config.getConfig().keySet()){
				JavaFileConfig cfg=config.getConfig().get(key);
				List<IField> fields=getIField(table.getColumns());
				String targetProject=cfg.getTargetProject();
				String targetPakage=cfg.getTargetPackage();
				switch(key){
					case "javaModelGenerator":
						generatedFiles.add(new GeneratorJavaFile(fields,targetProject,targetPakage,tableName,"public",tableName,LayoutEnum.DAO_LAYOUT));
						break;
					case "javaServiceGenerator":
						if(cfg.isEnableInterfaceSupInterfaceGenericity())
							generatedFiles.add(new GeneratorServiceJavaFile(tableName+"Service",tableName,config,key,LayoutEnum.SERVICE_LAYOUT));
						generatedFiles.add(new GeneratorServiceJavaFile(tableName+"ServiceImpl",tableName,config,key,LayoutEnum.SERVICE_LAYOUT));
						break;
					case "sqlMapGenerator":
						
						
					case "javaDaoGenerator":
						if(cfg.isEnableInterfaceSupInterfaceGenericity())
							generatedFiles.add(new GeneratorServiceJavaFile(tableName+"Dao",tableName,config,key,LayoutEnum.DAO_LAYOUT));
						generatedFiles.add(new GeneratorServiceJavaFile(tableName+"DaoImpl",tableName,config,key,LayoutEnum.DAO_LAYOUT));
						break;
					case "javaControllerGenerator":
						generatedFiles.add(new GeneratorServiceJavaFile(tableName+"Controller",tableName,config,key,LayoutEnum.CONTROLLER_LAYOUT));
						break;
				}
			}
		}
	}
	
	private String getTableEntriyName(String tableName){
		String[] t=tableName.split("_");
		StringBuilder sb=new StringBuilder();
		for(String s:t){
			if(sb.length()<=0)
				sb.append(s);
			sb.append(s.substring(0, 1).toUpperCase()+s.substring(1));
		}
		return sb.toString();
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
		switch(String){
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
		}
		return null;
	}
}
