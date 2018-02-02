package ht.plugin.context;

import java.util.ArrayList;
import java.util.List;

import ht.plugin.configration.Configration;
import ht.plugin.configration.config.JavaFileConfig;
import ht.plugin.generate.GeneratedFile;
import ht.plugin.generate.GeneratorJavaFile;
import ht.plugin.introspect.DBIntrospect;
import ht.plugin.introspect.IColumn;
import ht.plugin.introspect.IField;
import ht.plugin.introspect.IJavaType;
import ht.plugin.introspect.IMethod;
import ht.plugin.introspect.ITable;
import ht.plugin.properties.LayoutEnum;

public class MybatisGenerator implements Generator{
	private PluginContext context;
	
	public MybatisGenerator(PluginContext context){
		this.context=context;
	}
	
	public void generate(List<GeneratedFile> generatedFiles,List<String> tableList){
		if(generatedFiles==null) generatedFiles=new ArrayList<>();
		generatedFiles.clear();
		Configration config=context.getConfig();
 
		DBIntrospect introspect=new DBIntrospect(context);
		introspect.introspect(tableList);
		for(ITable table:context.getTables()){
			for(String key:config.getConfig().keySet()){
				JavaFileConfig cfg=config.getConfig().get(key);
				List<IField> fields=getIField(table.getColumns());
			 
				switch(key){
					case "javaModelGenerator":
						String targetProject=cfg.getTargetProject();
						GeneratorJavaFile javaFile=new GeneratorJavaFile(fields,targetProject);
						generatedFiles.add(javaFile);
					case "javaServiceGenerator":
						
						
						
					case "sqlMapGenerator":
					case "javaDaoGenerator":
					case "javaControllerGenerator":
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
